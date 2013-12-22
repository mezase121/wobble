package server;

import java.nio.channels.SocketChannel;

public class MessageQueue {
	
	private SocketChannel socketChannel;
	private String message = "";	
	
	public MessageQueue(String message, SocketChannel socketChannel) {
		this.message = message;
		this.socketChannel = socketChannel;
	}

	public synchronized SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	public synchronized String getMessage() {
		return message;
	}
}
