package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientThread extends Thread
{
	private Client client;
	private SocketChannel socketChannel;
	private GUI gui;
	private String message;
	private ByteBuffer buf = ByteBuffer.allocate(65536);
	private String errorMessage = "";

	private boolean serverOnline = true;

	public ClientThread(Client client, SocketChannel socketChannel, GUI gui) {
		this.client = client;
		this.socketChannel = socketChannel;
		this.gui = gui;
	}

	public synchronized void run() {
		try {
			message = "";
			while (serverOnline)
				read();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void read(){		
		try
		{
			buf.clear();
			int numBytesRead = socketChannel.read(buf);
			if (numBytesRead == -1) {
				errorMessage = "Connection closed. Try reconnecting!";
				disconnect();
			}
			buf.flip();
			for (int i=0;i<numBytesRead;i++) {
				message = message.concat(Character.toString((char)buf.get()));
			}
			client.addMessageToQueueA(message);
			System.out.println("Message received: "+message);
			message = "";
		}
		catch (IOException e) {		
			e.printStackTrace();
			disconnect();
		}
	}

	public synchronized void disconnect(){
		try {
			serverOnline = false;
			socketChannel.close();
			client.setConnected(false);
			client.setUsername("");
			gui.getJframe().setTitle(client.getAppName());
			gui.toggleChatInput();
			gui.toggleConnectMenu();
			gui.toggleLoginMenu();
			client.setHost("");
			gui.clearUserlist();
			if (errorMessage.equals("")){
				errorMessage = "Server has gone offline.";	
			}			
			gui.updateChat("notification", "", "", "", "", errorMessage);
			errorMessage = "";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean isServerOnline() {
		return serverOnline;
	}

	public synchronized void setServerOnline(boolean serverOnline) {
		this.serverOnline = serverOnline;
	}

	public SocketChannel getClientSocket() {
		return socketChannel;
	}

	public void setClientSocket(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}