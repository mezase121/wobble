package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Client
{
	private LinkedList<MessageQueue> messageQueueA = new LinkedList<MessageQueue>();

	private String username = "";
	private SocketChannel socketChannel;
	private ClientThread clientThread;
	private Timing timingThread;
	private MessageHandler messageHandler;
	private String host = "";

	private String appName = "Wobble 2.0.3";
	private GUI gui = new GUI(this, appName);

	private boolean connected = false;
	private boolean firstTimeConnect = true;

	public Client() {
		timingThread = new Timing(gui);
		timingThread.start();
	}

	public synchronized void connect(String hostname) {
		try {
			if (!connected) {
				if (firstTimeConnect) {
					if (hostname.length() == 0) {
						host = "localhost";
					}
					else {
						host = hostname;
					}
					socketChannel = SocketChannel.open();
					socketChannel.connect(new InetSocketAddress(host, 27027));
					clientThread = new ClientThread(this, socketChannel, gui);
					clientThread.setServerOnline(true);
					clientThread.start();
					messageHandler = new MessageHandler(this, gui);
					messageHandler.start();
					connected = true;
					firstTimeConnect = false;
					gui.updateChat("", "", "", "", "", "Successfully connected to \""+host+"\".");
					gui.toggleConnectMenu();
				}
				else {
					if (hostname.length() == 0) {
						host = "localhost";
					}
					else {
						host = hostname;
					}
					socketChannel = SocketChannel.open();
					socketChannel.connect(new InetSocketAddress(this.host, 27027));
					clientThread = new ClientThread(this, socketChannel, gui);
					clientThread.setServerOnline(true);
					clientThread.start();
					connected = true;
					gui.updateChat("", "", "", "", "", "Successfully connected to \""+host+"\".");
					gui.toggleConnectMenu();
				}
			}
			else
				this.gui.updateChat("notification", "", "", "", "", "You're already connected to \""+host+"\".");
		}
		catch (Exception e) {
			try {
				gui.updateChat("notification", "", "", "", "", "Failed to connect to \""+host+"\".");
				host = "";
				socketChannel.close();
				connected = false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public synchronized void send(String message) {
		try {
			ByteBuffer buf = ByteBuffer.allocate(65536);
			buf.clear();
			buf.put(message.getBytes());
			buf.flip();
			while (buf.hasRemaining())
				this.socketChannel.write(buf);
		}
		catch (Exception e) {
			e.printStackTrace();
			this.gui.updateChat("notification", "", "", "", "", "Connect to server first!");
		}
	}
	
	public synchronized void disconnect(String errorMessage) throws IOException {
		clientThread.setErrorMessage(errorMessage);
		socketChannel.close();
	}

	public synchronized void addMessageToQueueA(String message)
	{
		messageQueueA.addFirst(new MessageQueue(message));
	}

	public static void main(String[] argv) {
		new Client();
	}

	public synchronized LinkedList<MessageQueue> getMessageQueueA() {
		return messageQueueA;
	}

	public synchronized String getUsername() {
		return username;
	}

	public synchronized void setUsername(String username) {
		this.username = username;
	}

	public synchronized boolean isConnected() {
		return connected;
	}

	public synchronized void setConnected(boolean connected) {
		this.connected = connected;
	}

	public synchronized String getHost() {
		return this.host;
	}

	public synchronized void setHost(String host) {
		this.host = host;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}