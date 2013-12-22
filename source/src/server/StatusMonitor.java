package server;

import java.nio.channels.SocketChannel;

public class StatusMonitor extends Thread {

	private Server server;

	public StatusMonitor(Server server) {
		this.server = server;
	}

	public synchronized void run(){
		while(true){
			try {
				ping();
				Thread.sleep(12000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void ping(){
		for (int i=0;i<server.getClients().size();i++){
			SocketChannel newSocketChannel = server.getClients().get(i).getSocketChannel();
			server.addServerMessageToQueueA("user server alive", newSocketChannel);
		}
		/*if (server.getClients().size()>0){
			System.out.println("Ping has been sent..............");
		}*/
	}	
}
