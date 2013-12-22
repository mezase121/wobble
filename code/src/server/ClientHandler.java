package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ClientHandler
implements Runnable
{
	private Server server;
	private ServerSocketChannel serverSocketChannel;
	private SocketChannel socketChannel;
	private ByteBuffer buf = ByteBuffer.allocate(65536);

	private boolean running = true;

	public ClientHandler(Server server, ServerSocketChannel serverSocketChannel) throws IOException {
		this.serverSocketChannel = serverSocketChannel;
		this.server = server;
	}
	public synchronized void run() {
		try {
			socketChannel = serverSocketChannel.accept(); } 
		catch (IOException localIOException) {}
		server.updateConsole(server.getCurrentTime()+" Connection:"+socketChannel);
		while (running){
			read();
		}
	}

	public synchronized void read()
	{
		String message = "";
		try {
			buf.clear();
			int numBytesRead = socketChannel.read(buf);
			if (numBytesRead == -1) {
				disconnect();
			}
			else {
				buf.flip();
				for (int i = 0; i < numBytesRead; i++) {
					message = message.concat(Character.toString((char)buf.get()));
				}
				//server.updateConsole(server.getCurrentTime()+"Received: "+message);
				server.addMessageToQueueA(message, socketChannel);
				server.writeLog("Received: "+message);
				message = "";
			}
		} catch (Exception e) {
			disconnect();
		}
	}

	public synchronized void disconnect() {
		server.cleanClientList(socketChannel);
		server.sendUserList();
		try {
			socketChannel.close();
			server.startNewThread();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		running = false;
	}
}