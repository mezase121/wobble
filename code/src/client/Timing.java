package client;


public class Timing extends Thread {
	
	private GUI gui;

	public Timing(GUI gui){
		this.gui = gui;
	}

	public synchronized void run() {
		while(true){
			try {
				gui.setCurrentTime(gui.getCurrentTime());
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	

}
