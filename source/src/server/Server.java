package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Vector;

public class Server
{
	private Vector<ActiveClient> clients = new Vector<ActiveClient>();
	private LinkedList<MessageQueue> messageQueueA = new LinkedList<MessageQueue>();
	private LinkedList<MessageQueue> ServerMessageQueueA = new LinkedList<MessageQueue>();

	private Console console = new Console("Wobble - Server console v1.0.0.0");
	private MessageHandler messageHandler = new MessageHandler(this);
	private StatusMonitor monitor = new StatusMonitor(this);
	private ServerMessageHandler serverMessageHandler = new ServerMessageHandler(this);
	private File serverLogFile = new File("Logs/serverLog.txt");
	private FileOutputStream serverLog;
	private boolean sent = false;
	private ServerSocketChannel serverSocketChannel;
	private int maxClients = 25;

	public Server() throws IOException
	{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(27027));
	}

	public synchronized void run() {
		try {
			initLog();
			init();
			updateConsole(getCurrentTime()+" Server is running...");
			writeLog(getCurrentTime()+" Server is running..."); } catch (IOException localIOException) {
			}
	}

	public synchronized void init() throws IOException {
		for (int i=0; i<maxClients;i++) {
			Thread clientHandler = new Thread(new ClientHandler(this, serverSocketChannel));
			clientHandler.start();
		}
		Thread messageHandlerThread = new Thread(messageHandler);
		messageHandlerThread.start();
		Thread serverMessageHandlerThread = new Thread(serverMessageHandler);
		serverMessageHandlerThread.start();
		Thread monitorThread = new Thread(monitor);
		monitorThread.start();
	}

	public synchronized void initLog() {
		try {
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("(HH.mm.ss) - yyyy.MM.dd");
			String date = formatter.format(currentDate.getTime());
			serverLogFile = new File("Logs/serverLog - "+date+".txt");
			serverLogFile.getParentFile().mkdirs();
			serverLogFile.createNewFile();
			serverLog = new FileOutputStream("Logs/serverLog - "+date+".txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void writeLog(String message) {
		try {
			byte[] receivedBytes = (getCurrentTime()+" "+message+"\r\n").getBytes();
			this.serverLog.write(receivedBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void updateConsole(String message) {
		this.console.updateDisplay(message);
	}

	public synchronized void startNewThread() throws IOException {
		Thread clientHandler = new Thread(new ClientHandler(this, this.serverSocketChannel));
		clientHandler.start();
	}

	public synchronized void addMessageToQueueA(String message, SocketChannel socketChannel) {
		this.messageQueueA.addFirst(new MessageQueue(message, socketChannel));
	}

	public synchronized void addServerMessageToQueueA(String message, SocketChannel socketChannel) {
		this.ServerMessageQueueA.addFirst(new MessageQueue(message, socketChannel));
	}

	public synchronized void addNewClient(String name, SocketChannel socketChannel) {
		boolean exists = false;
		boolean nameTaken = false;
		for (int i = 0; i<clients.size(); i++) {
			if (clients.get(i).getSocketChannel().equals(socketChannel)) {
				exists = true;
				break;
			}
		}
		for (int i=0;i<clients.size();i++) {
			if (clients.get(i).getName().equals(name)) {
				nameTaken = true;
				break;
			}
		}
		if ((!exists) && (!nameTaken)) {
			clients.add(new ActiveClient(name, socketChannel));
			updateConsole(getCurrentTime()+" Login: "+name+"\" has logged in.");
			addServerMessageToQueueA("user server username "+name, socketChannel);
			sendUserList();
		}
		else if ((!exists) && (nameTaken)) {
			updateConsole(getCurrentTime()+" Sent: "+name+"\" is already in use.");
			addServerMessageToQueueA("user server error The specified name is already in use.", socketChannel);
		}
		else {
			updateConsole(getCurrentTime()+" Sent: User is already logged in.");
			addServerMessageToQueueA("user server error You're already logged in.", socketChannel);
		}
	}

	public synchronized void cleanClientList(SocketChannel socketChannel) {
		boolean removed = false;
		for (int i=0;i<clients.size(); i++) {
			if (clients.get(i).getSocketChannel().equals(socketChannel)) {
				updateConsole(getCurrentTime()+" Connection: "+clients.get(i).getName()+" has disconnected.");
				writeLog("Sent: Connection: "+clients.get(i).getName()+" has disconnected.");
				clients.remove(i);
				removed = true;
			}
		}
		if (!removed) {
			updateConsole(getCurrentTime()+" Connection: Unknown user disconnected.");
			writeLog("Sent: Connection: Unknown user disconnected.");
		}
	}

	public synchronized String getCurrentTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String timeNow = formatter.format(currentDate.getTime());
		return "("+timeNow+")";
	}

	public synchronized void sendUserList() {
		String userList = "";
		if (clients.size()>0) {
			for (int i=0;i<clients.size();i++) {
				userList = userList.concat(clients.get(i).getName()+" ");
			}
			for (int i=0;i<clients.size();i++) {
				addServerMessageToQueueA("user server update "+userList, clients.get(i).getSocketChannel());
				if (!this.sent) {
					writeLog("Sent: user server update "+userList);
				}
				this.sent = true;
			}
			this.sent = false;
			updateConsole(getCurrentTime()+" Sent: user server update "+userList);
		}
	}

	public static void main(String[] argv) throws IOException {
		Server server = new Server();
		server.run();
	}

	public synchronized LinkedList<MessageQueue> getMessageQueueA() {
		return this.messageQueueA;
	}

	public synchronized Vector<ActiveClient> getClients() {
		return clients;
	}

	public synchronized MessageHandler getMessageHandler() {
		return this.messageHandler;
	}

	public synchronized void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public synchronized LinkedList<MessageQueue> getServerMessageQueueA() {
		return this.ServerMessageQueueA;
	}

	public synchronized void setServerMessageQueueA(LinkedList<MessageQueue> serverMessageQueueA) {
		this.ServerMessageQueueA = serverMessageQueueA;
	}
}