package server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class MessageHandler extends Thread
{
	private LinkedList<MessageQueue> messageQueueB = new LinkedList<MessageQueue>();
	private Server server;
	private SocketChannel socketChannel;
	private ByteBuffer buf = ByteBuffer.allocate(65536);
	private String messageQ = "";
	private boolean sent = false;

	public MessageHandler(Server server) {
		this.server = server;
	}

	public synchronized void run() {
		while (true) {
			updateQueue();
			processQueue();
			try {
				Thread.sleep(4);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void updateQueue() {
		messageQueueB = server.getMessageQueueA();
	}

	public synchronized void processQueue() {
		for (int i=0;i<messageQueueB.size();i++)
			try {
				messageQ = messageQueueB.getLast().getMessage();
				socketChannel = messageQueueB.getLast().getSocketChannel();
				//server.updateConsole(server.getCurrentTime()+" Received: "+messageQ);
				server.writeLog(server.getCurrentTime()+" Received: "+messageQ);
				if ((messageQ.length() > 0) && (socketChannel!=null)) {
					decodeMessage(messageQ, socketChannel);
				}
				messageQueueB.removeLast();
			}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Trace: m:"+messageQ+"---- cs:"+socketChannel+"---- size:"+messageQueueB.size());
		}
	}

	public synchronized void send(String message, String receiver, SocketChannel newSocketChannel, int appendType) {
		try {
			String time = server.getCurrentTime();
			String sender = findUserBySocket(socketChannel);
			buf.clear();
			buf.put((receiver+" "+sender+" "+message+"@1219!4@").getBytes());
			buf.flip();
			while (buf.hasRemaining()) {
				newSocketChannel.write(buf);
			}
			if (!sent)
				server.writeLog("Sent: "+receiver+" "+sender+" "+time+": "+message);
		}
		catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
	}

	/*public synchronized void send(String message, SocketChannel socketChannel) {
		try {
			buf.clear();
			buf.put(message.getBytes());
			buf.flip();
			while (buf.hasRemaining()) {
				socketChannel.write(buf);
			}
			server.writeLog(message);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
	}*/

	public synchronized void decodeMessage(String receivedMsg, SocketChannel socketChannel) {
		char[] msgToDecode = receivedMsg.toCharArray();
		String command = "";
		String user = "";
		String message = "";
		int spaces = 0;
		char current = msgToDecode[0];
		if (receivedMsg.length() > 0) {
			for (int i=0; i<receivedMsg.length();i++) {
				current = msgToDecode[i];
				if (current == ' ') {
					spaces++;
				}

				if ((spaces == 0) && (current != ' ')) {
					command = command.concat(Character.toString(current));
				}
				if ((spaces == 1) && (current != ' ')) {
					user = user.concat(Character.toString(current));
				}
				if (spaces >= 2) {
					message = message.concat(Character.toString(current));
				}
			}
		}
		if (message.length() > 0) {
			message = message.substring(1);
		}
		System.out.println("<c:>"+command+"<u:>"+user+"<m:>"+message);
		if (command.equals("login")) {
			server.addNewClient(user+message, socketChannel);
		}
		else if (command.equals("username")) {
			server.addNewClient(user+message, socketChannel);
		}
		else if (command.equals("/w") && authenticate(socketChannel)) {
			boolean userFound = false;
			for (int i = 0; i < server.getClients().size(); i++) {
				if (server.getClients().get(i).getName().equals(user)) {
					SocketChannel newSocketChannel = server.getClients().get(i).getSocketChannel();
					userFound = true;
					if (socketChannel.equals(newSocketChannel)) break;
					send(command+" "+user+" "+message, user, newSocketChannel, 2);
					sent = true;
					send(command+" "+user+" "+message, user, socketChannel, 2);
					server.updateConsole(server.getCurrentTime()+" Sent whisper: "+command+" "+user+" "+message);
					break;
				}
				sent = false;				
			}
			if (userFound==false) {
				server.addServerMessageToQueueA("user server error User \""+user+"\" is not online.", socketChannel);
				server.updateConsole(server.getCurrentTime()+" Sent: "+"User"+" "+"Server"+" "+server.getCurrentTime()+": server User \""+user+"\" is not online.");
			}
		}
		else if ((command.equals("all")) && (authenticate(socketChannel))) {
			for (int i=0;i<server.getClients().size();i++) {
				SocketChannel newSocketChannel = server.getClients().get(i).getSocketChannel();
				send(command+" "+user+" "+message, "all", newSocketChannel, 1);
				sent = true;
			}
			sent = false;
		}
		else if ((command.equals("/admin")) && (authenticate(socketChannel))) {
			if(user.equals("lmao")){
				for (int i=0;i<server.getClients().size();i++) {
					if (server.getClients().get(i).getSocketChannel().equals(socketChannel)){
						server.getClients().get(i).setAdmin(true);
						server.addServerMessageToQueueA("user server error You're now an admin.", socketChannel);
						server.updateConsole("Admin has logged in: \""+user+"\"");
					}
				}
			}
		}
		else if ((command.equals("/kick")) && (authenticate(socketChannel))) {
			boolean isAdmin = false;
			for (int i=0;i<server.getClients().size();i++) {
				if (server.getClients().get(i).getSocketChannel().equals(socketChannel)){
					if (server.getClients().get(i).isAdmin()==true){
						isAdmin=true;
					}
				}
			}
			if (isAdmin==true){
				for (int i=0;i<server.getClients().size();i++) {
					if (server.getClients().get(i).getName().equals(user)){
						SocketChannel newSocketChannel = server.getClients().get(i).getSocketChannel();
						server.addServerMessageToQueueA("user server kicked", newSocketChannel);
					}
				}
			}			
		}
	}

	public synchronized SocketChannel findUserByName(String username) {
		SocketChannel newSocketChannel = null;
		for (int i=0;i<server.getClients().size();i++) {
			if (server.getClients().get(i).getName().equals(username)) {
				newSocketChannel = server.getClients().get(i).getSocketChannel();
			}
		}
		return newSocketChannel;
	}

	public synchronized String findUserBySocket(SocketChannel socketChannel) {
		String username = "";
		for (int i = 0; i < server.getClients().size(); i++) {
			if (((ActiveClient)server.getClients().get(i)).getSocketChannel() == socketChannel) {
				username = ((ActiveClient)server.getClients().get(i)).getName();
			}
		}
		return username;
	}

	public synchronized boolean authenticate(SocketChannel socketChannel) {
		boolean exists = false;
		if (server.getClients().size() > 0) {
			for (int i = 0; i < server.getClients().size(); i++) {
				if (((ActiveClient)server.getClients().get(i)).getSocketChannel().equals(socketChannel)) {
					exists = true;
					break;
				}
				exists = false;
			}
			if (exists==true) {
				return true;
			}
			server.addServerMessageToQueueA("user server server Please log in first.  How: 1.Start  2.Login...", socketChannel);
			server.updateConsole(server.getCurrentTime()+" Sent: "+"loginerror"+" "+"Server"+" "+server.getCurrentTime()+": server Please log in first. 1.Start  2.Login...");
			return false;
		}
		server.addServerMessageToQueueA("user server server Please log in first.  How: 1.Start  2.Login...", socketChannel);
		server.updateConsole(server.getCurrentTime()+" Sent: "+"loginerror"+" "+"Server"+" "+server.getCurrentTime()+": server Please log in first. 1.Start  2.Login...");
		return false;
	}

	public synchronized LinkedList<MessageQueue> getMessageQueueB() {
		return messageQueueB;
	}

	public synchronized void setMessageQueueB(LinkedList<MessageQueue> messageQueueB) {
		this.messageQueueB = messageQueueB;
	}
}