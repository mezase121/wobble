package client;

public class MessageQueue {

	private String message = "";	
	
	public MessageQueue(String message) {
		this.message = message;
	}
	
	public synchronized String getMessage() {
		return message;
	}
}
