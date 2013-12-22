package server;

import java.nio.channels.SocketChannel;

public class ActiveClient {
	
	private String name = "";
	private SocketChannel socketChannel;
	private boolean alive = false;
	private boolean admin = false;
	
	public ActiveClient(String name, SocketChannel socketChannel) {
		this.name = name;
		this.socketChannel = socketChannel;
		this.alive = true;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public synchronized void setConnectionSocket(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isAdmin() {
		return admin;
	}	

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isAlive() {
		return alive;
	}
}
