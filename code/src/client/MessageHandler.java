package client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class MessageHandler extends Thread
{
	private LinkedList<MessageQueue> messageQueueB = new LinkedList<MessageQueue>();
	private Client client;
	private GUI gui;
	private String messageQ = "";

	public MessageHandler(Client client, GUI gui) {
		this.client = client;
		this.gui = gui;
	}

	public synchronized void run() {
		while (true) {
			updateQueue();
			processQueue();
			try {
				Thread.sleep(5); } 
			catch (InterruptedException localInterruptedException) {}
		}
	}

	public synchronized void updateQueue() {
		messageQueueB = client.getMessageQueueA();
	}

	public synchronized void processQueue() {
		for (int i=0;i<messageQueueB.size();i++){
			try {
				messageQ = ((MessageQueue)messageQueueB.getLast()).getMessage();
				if (messageQ.length()>0) {
					decodeMessage(messageQ);
				}
				messageQueueB.removeLast();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public synchronized void decodeMessage(String receivedMsg) {
		char[] msgToDecode = receivedMsg.toCharArray();
		String receiver = "";
		String sender = "";
		String time = "";
		String command = "";
		String user = "";
		String message = "";
		int spaces = 0;
		int finishers = 0;
		char current = msgToDecode[0];
		if (receivedMsg.length() > 0) {
			for (int i=0;i<receivedMsg.length();i++) {//@1219!4@
				current = msgToDecode[i];								
				if (current=='@' && finishers==0){
					finishers++;
					if (msgToDecode.length-i>=7) {
						if (msgToDecode[i+1]=='1' && finishers==1){
							finishers++;							
							if (msgToDecode[i+2]=='2' && finishers==2){
								finishers++;									
								if (msgToDecode[i+3]=='1' && finishers==3){
									finishers++;											
									if (msgToDecode[i+4]=='9' && finishers==4){
										finishers++;													
										if (msgToDecode[i+5]=='!' && finishers==5){
											finishers++;															
											if (msgToDecode[i+6]=='4' && finishers==6){
												finishers++;																	
												if (msgToDecode[i+7]=='@' && finishers==7){
													if (message.length() > 0) {
														message = message.substring(1);
													}
													System.out.println("111: <r:>"+receiver+"<s:>"+sender+"<c:>"+command+"<u:>"+user+"<m:>"+message);
													gui.updateChat(receiver, sender, getCurrentTime(), command, user, message);
													receiver = "";
													sender = "";
													time = "";
													command = "";
													user = "";
													message = "";
													spaces = 0;
													finishers = 0;
													i=i+8;
												}
												else {
													finishers=0;
												}																	
											}
											else {
												finishers=0;
											}																														
										}
										else {
											finishers=0;
										}																										
									}
									else {
										finishers=0;
									}											
								}
								else {
									finishers=0;
								}																											
							}
							else {
								finishers=0;
							}													
						}
						else {
							finishers=0;
						}
					}
				}			
				if (msgToDecode.length-i>0){
					current = msgToDecode[i];	
					if (current==' ') {
						spaces++;
					}
					if (spaces==0 && current!=' ') {
						receiver = receiver.concat(Character.toString(current));
					}
					else if (spaces==1 && current!=' ') {
						sender = sender.concat(Character.toString(current));
					}
					/*else if (spaces==2 && current!=' ') {
						time = time.concat(Character.toString(current));
					}*/
					else if (spaces==2 && current!=' ') {
						command = command.concat(Character.toString(current));
					}
					else if (spaces==3 && current!=' ') {
						user = user.concat(Character.toString(current));
					}
					else if (spaces>=4) {
						message = message.concat(Character.toString(current));
					}
				}
			}
		}
		System.out.println("222: <r:>"+receiver+"<s:>"+sender+"<t:>"+time+"<c:>"+command+"<u:>"+user+"<m:>"+message);
		/*if (message.length() > 0) {
			message = message.substring(1);
		}
		System.out.println("222: <r:>"+receiver+"<s:>"+sender+"<t:>"+time+"<c:>"+command+"<u:>"+user+"<m:>"+message);
		gui.updateChat(receiver, sender, time, command, user, message);*/
	}

	public synchronized String getCurrentTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String timeNow = formatter.format(currentDate.getTime());
		return "("+timeNow+"):";
	}
}