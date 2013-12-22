package server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class ServerMessageHandler extends Thread
{
	private LinkedList<MessageQueue> ServerMessageQueueB = new LinkedList<MessageQueue>();
	private Server server;
	private SocketChannel socketChannel;
	private ByteBuffer buf = ByteBuffer.allocate(65536);
	private String messageQ = "";

	public ServerMessageHandler(Server server)
	{
		this.server = server;
	}

	public synchronized void run() {
		while (true) {
			updateQueue();
			processQueue();
			try {
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void updateQueue() {
		ServerMessageQueueB = server.getServerMessageQueueA();
	}

	public synchronized void processQueue() {
		for (int i=0;i<ServerMessageQueueB.size();i++) {
			messageQ = ServerMessageQueueB.getLast().getMessage();
			socketChannel = ServerMessageQueueB.getLast().getSocketChannel();
			if ((messageQ.length()>0) && (socketChannel != null)) {
				send(messageQ, socketChannel);
				//System.out.println("Mesage Sent: "+messageQ+"<<<<<<Server-SMH<<<<<<");
			}
			this.ServerMessageQueueB.removeLast();
		}
	}

	public synchronized void send(String message, SocketChannel socketChannel) {
		try {
			buf.clear();
			buf.put((message+"@1219!4@").getBytes());
			buf.flip();
			while (buf.hasRemaining())
				socketChannel.write(buf);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized String findUserBySocket(SocketChannel socketChannel) {
		String username = "";
		for (int i=0;i<server.getClients().size();i++) {
			if (server.getClients().get(i).getSocketChannel()==socketChannel) {
				username = server.getClients().get(i).getName();
			}
		}
		return username;
	}
}