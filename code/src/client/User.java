package client;

import java.awt.Color;

public class User {

	private String name;
	private Color color;
	private boolean connected;

	public User(String name){
		this.name = name;
		this.connected = true;
		int r = (int) (Math.random()*190);
		int g = (int) (Math.random()*190);
		int b = (int) (Math.random()*190);
		
		this.color = new Color(r,g,b);
	}

	public synchronized void chooseColor(){
		
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized Color getColor() {
		return color;
	}

	public synchronized void setColor(Color color) {
		this.color = color;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
