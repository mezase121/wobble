package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javazoom.jl.player.Player;

public class GUI
{
	private ArrayList<User> userlist = new ArrayList<User>();
	private ArrayList<History> history = new ArrayList<History>();
	private int historySize;
	private int historyIndex;
	private boolean viewingHistory;
	private boolean viewedUp;
	private boolean viewedDown;
	private int displayTimer = 0;
	private JFrame jframe;
	private Client client;
	private JMenuBar jMenuBar1;
	private JMenu jMenu1;
	private JMenuItem jMenuItem1;
	private JMenuItem jMenuItem2;
	private JMenuItem jMenuItem3;
	private JMenuItem jMenuItem4;
	private JList jList1;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JScrollPane jScrollPane3;
	private JTextPane jTextArea1;
	private JTextArea jTextArea2;
	private SimpleAttributeSet nameSet = new SimpleAttributeSet();
	private SimpleAttributeSet serverSet = new SimpleAttributeSet();
	private SimpleAttributeSet infoSet = new SimpleAttributeSet();
	private ImageIcon logoIcon = new ImageIcon("resources/logo Wobble 03_small.png");
	private ImageIcon connectIcon = new ImageIcon("resources/connect_icon 01_small.png");
	private ImageIcon loginIcon = new ImageIcon("resources/login_icon 01_small.png");
	private ImageIcon exitIcon = new ImageIcon("resources/exit_icon 01_small.png");
	private Document doc;
	private int currentTime = 0;
	private int lastTimeSent = -10;
	private int new_messageIndex = 1;

	/*@SuppressWarnings("serial")
	private Action enter = new AbstractAction("Enter") {
		public synchronized void actionPerformed(ActionEvent e) {
			try {
				if (Math.abs(currentTime-lastTimeSent)>=0){
					System.out.println(shiftKeyPressed);
					decodeInput(jTextArea2);
				}
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	};*/

	public GUI(Client client, String title)
	{
		init(title);
		initComponents();
		this.client = client;
	}

	public synchronized void init(String title) {
		jframe = new JFrame(title);
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				if ("Windows".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex); } catch (InstantiationException ex) {
				Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex); } catch (IllegalAccessException ex) {
					Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex); } catch (UnsupportedLookAndFeelException ex) {
						Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
					}
	}

	private synchronized void initComponents() {
		jMenuBar1 = new JMenuBar();
		jMenu1 = new JMenu();
		jMenuItem1 = new JMenuItem(connectIcon);
		jMenuItem2 = new JMenuItem(loginIcon);
		jMenuItem3 = new JMenuItem();
		jMenuItem4 = new JMenuItem(exitIcon);
		jTextArea1 = new JTextPane();
		jTextArea2 = new JTextArea();
		jScrollPane1 = new JScrollPane();
		jScrollPane2 = new JScrollPane();
		jScrollPane3 = new JScrollPane();
		jList1 = new JList();

		jframe.setIconImage(logoIcon.getImage());

		Font font = new Font("Arial", 0, 16);
		doc = jTextArea1.getStyledDocument();

		jTextArea1.setFont(font);
		jTextArea2.setFont(font);
		jTextArea1.setForeground(new Color(0.15F, 0.15F, 0.15F));
		jTextArea2.setForeground(new Color(0.15F, 0.15F, 0.15F));
		jTextArea1.setEditable(false);
		jTextArea2.setLineWrap(true);
		jList1.setFont(font);
		jList1.setCellRenderer(new CellRenderer());
		jframe.setLocation(658, 40);
		jframe.setResizable(true);
		jTextArea2.addKeyListener(new KeyInput());
		jTextArea2.setEnabled(false);
		jTextArea2.setBackground(new Color(225, 225, 225));
		jTextArea2.setText("Please log in first.");
		jTextArea2.setDocument(new JTextFieldLimit(1200));
		jframe.setDefaultCloseOperation(3);
		jScrollPane1.setViewportView(jList1);
		jScrollPane2.setViewportView(jTextArea1);

		jTextArea2.setColumns(20);
		jTextArea2.setRows(4);
		jScrollPane3.setViewportView(jTextArea2);

		/*jTextArea2.getInputMap().put(KeyStroke.getKeyStroke(10, 0), "Enter");
		jTextArea2.getActionMap().put("Enter", enter);*/

		jMenuItem1.setText("Connect...");
		jMenuItem2.setText("Login...");
		jMenuItem3.setText("");
		jMenuItem4.setText("Exit");
		jMenuItem3.setEnabled(false);
		jMenu1.add(jMenuItem1);
		jMenu1.add(jMenuItem2);
		jMenu1.add(jMenuItem3);
		jMenu1.add(jMenuItem4);
		jMenu1.setText("Start");
		jMenuBar1.add(jMenu1);
		jframe.setJMenuBar(jMenuBar1);
		jTextArea2.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (jTextArea2.getText().equals("Type a message...")){
					jTextArea2.setForeground(new Color(0.1f,0.1f,0.1f));
					jTextArea2.setText("");
				}
				else {
					jTextArea2.setForeground(new Color(0.1f,0.1f,0.1f));
				}
			}
			public void focusLost(FocusEvent e) {
				if (jTextArea2.getText().length()==0){
					jTextArea2.setForeground(new Color(0.5f,0.5f,0.5f));
					jTextArea2.setText("Type a message...");
				}
			}
		});

		jList1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				jList1 = (JList)evt.getSource();
				if (evt.getClickCount() >= 2) {
					int index = jList1.locationToIndex(evt.getPoint());
					if (jList1.getModel().getSize()>0){
						String selectedUser = jList1.getModel().getElementAt(index).toString();
						jTextArea2.setText("/w "+selectedUser+" ");
					}
					jTextArea2.requestFocusInWindow();
				}
			}
		});

		jMenuItem1.addActionListener(new java.awt.event.ActionListener() {//TODO
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem1ActionPerformed(evt);
			}
		});

		jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem2ActionPerformed(evt);
			}
		});

		jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jMenuItem4ActionPerformed(evt);
			}
		});


		GroupLayout layout = new GroupLayout(jframe.getContentPane());
		jframe.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
								.addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
								.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
								.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
										.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)))
										.addContainerGap())
				);
		jframe.setVisible(true);
		jframe.pack();		
		jTextArea2.requestFocusInWindow();
	}

	private void jMenuItem1ActionPerformed(ActionEvent evt) {
		new MenuWindow("Connect...", "Enter IP Address:", "Connect", "Connect", jTextArea2);
	}

	private void jMenuItem2ActionPerformed(ActionEvent evt) {
		new MenuWindow("Login...", "Enter username:", "Log in", "Login", jTextArea2);
	}

	private void jMenuItem4ActionPerformed(ActionEvent evt) {
		System.exit(0);
	}

	public synchronized void updateChat(String receiver, String sender, String time, String command, String user, String message)
	{		
		try {
			if (sender.equals("server")) {
				if (command.equals("error")) {
					StyleConstants.setForeground(serverSet, new Color(0, 180, 0));
					StyleConstants.setBold(serverSet, true);
					doc.insertString(doc.getLength(), "\n"+sender+" "+time+" "+user+" "+message, serverSet);
					playSound("serverNotification1");
				}
				else if (command.equals("username")) {
					if (client.getUsername().equals("")){
						toggleChatInput();
						toggleLoginMenu();
					}
					client.setUsername(user);
					jframe.setTitle(client.getAppName()+" - "+user);
					updateChat("", "", "", "", "", "You're now logged in as "+user+".");
					playSound("login2");
				}//TODO
				else if (command.equals("update")) {
					updateUserList(user+" "+message);
				}
				else if (command.equals("alive")) {
					client.send("alive "+client.getUsername());
					System.out.println("......................Pong has been sent!.");
				}
				else if (command.equals("kicked")) {
					client.disconnect( "You have been kicked!");
				}
			}
			else if (command.equals("/w")) {
				if (sender.equals(client.getUsername())) {
					StyleConstants.setForeground(nameSet, new Color(50, 50, 180));
					StyleConstants.setBold(nameSet, true);
					doc.insertString(doc.getLength(), "\n", null);
					doc.insertString(doc.getLength(), "[To "+receiver+"] ", nameSet);
					StyleConstants.setBold(nameSet, false);
					doc.insertString(doc.getLength(), time+" "+message, nameSet);
					StyleConstants.setItalic(nameSet, false);
				}
				else {
					StyleConstants.setForeground(nameSet, findUserByName(sender).getColor());
					StyleConstants.setBold(nameSet, true);
					doc.insertString(doc.getLength(), "\n", null);
					doc.insertString(doc.getLength(), "[From "+sender+"] ", nameSet);
					StyleConstants.setBold(nameSet, false);
					doc.insertString(doc.getLength(), time+" "+message, nameSet);
					StyleConstants.setItalic(nameSet, false);
				}
				playSound("new_privateMessage1");
			}
			else if (command.equals("all")) {
				if (sender.equals(client.getUsername())) {
					StyleConstants.setForeground(nameSet, new Color(50, 50, 180));
				}
				else {
					StyleConstants.setForeground(nameSet, findUserByName(sender).getColor());
				}
				jTextArea1.setCharacterAttributes(nameSet, true);
				doc.insertString(doc.getLength(), "\n", null);
				doc.insertString(doc.getLength(), sender+" "+time+" ", nameSet);
				if (message.length()<=1){
					doc.insertString(doc.getLength(), user, nameSet);
				}
				else {
					doc.insertString(doc.getLength(), user+" "+message.trim(), nameSet);
				}				
				if (new_messageIndex>3) {
					new_messageIndex = 1;
				}
				playSound("new_message"+new_messageIndex);
				new_messageIndex+=1;
			}
			else if ((sender.equals("")) && (time.equals("")) && (command.equals("")) && (user.equals(""))) {
				if (receiver.equals("notification")) {
					playSound("serverNotification1");
				}
				StyleConstants.setForeground(infoSet, new Color(140, 140, 255));
				StyleConstants.setBold(infoSet, true);
				if (jTextArea1.getText().length()<=0){
					doc.insertString(doc.getLength(), message, infoSet);
				}
				else {
					doc.insertString(doc.getLength(), "\n", null);
					doc.insertString(doc.getLength(), message, infoSet);
				}				
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					jTextArea1.scrollRectToVisible(new Rectangle(0, jTextArea1.getHeight()-2, 1, 1));
				}
			});
		}
		catch (Exception e1) {
			//e1.printStackTrace();
		}
	}	

	public synchronized void decodeInput(JTextArea textDisplay) throws IOException {
		lastTimeSent = getCurrentTime();
		char[] msgToDecode = textDisplay.getText().toCharArray();
		String command = "";
		String message = "";
		String user = "";
		int spaces = 0;
		for (int i=0;i<textDisplay.getText().length();i++) {
			char current = msgToDecode[i];
			if (current==' ') {
				spaces++;
			}
			if ((spaces==0) && (current != ' ')) {
				command = command.concat(Character.toString(current));
			}
			if ((spaces==1) && (current != ' ')) {
				user = user.concat(Character.toString(current));
			}
			if (spaces >= 2) {
				message = message.concat(Character.toString(current));
			}
		}
		user = user.trim();
		message = message.trim();
		System.out.println("<c:>"+command+"<u:>"+user+"<m:>"+message);
		if (command.equals("/w") && !user.equals("") && !client.getUsername().equals("")) {
			clearDisplay(jTextArea2);
			client.send(command+" "+user+" "+message);
			updateHistory(command, user, message);
		}
		else if (command.equals("/admin") && !user.equals("") && !client.getUsername().equals("")){
			clearDisplay(jTextArea2);
			client.send(command+" "+user+" "+message);
		}
		else if (command.equals("/kick") && !user.equals("") && !client.getUsername().equals("")){
			clearDisplay(jTextArea2);
			client.send(command+" "+user+" "+message);
			updateHistory(command, user, message);
		}
		else if (jTextArea2.getText().length()>0 && !client.getUsername().equals("")) {
			clearDisplay(jTextArea2);
			client.send("all "+command+" "+user+" "+message);
			updateHistory(command, user, message);
		}
	}

	public synchronized User findUserByName(String name) {
		User user = null;
		for (int i = 0; i<userlist.size(); i++) {
			if (userlist.get(i).getName().equals(name)) {
				user = (User)userlist.get(i);
			}
		}
		return user;
	}

	public synchronized void updateHistory(String command, String user, String message)
	{
		String result = "";
		command = command.trim();
		user = user.trim();
		message = message.trim();
		if ((message.length()<=0) && (user.length()<=0)) {
			result = command+user+message;
		}
		else if ((message.length()<=0) && (user.length()>0)) {
			result = command+" "+user+message;
		}
		else {
			result = command+" "+user+" "+message;
		}
		history.add(new History(result));
		historySize+=1;
		historyIndex = historySize;
		viewingHistory = false;
		viewedUp = false;
		viewedDown = false;
	}

	public synchronized boolean userAlreadyExists(String name) {
		boolean exists = false;
		for (int i = 0; i<userlist.size(); i++) {
			if (userlist.get(i).getName().equals(name)) {
				exists = true;
				break;
			}
		}

		return exists;
	}

	public synchronized void updateUserList(String newList)
	{
		String[] userList = new String[0];
		String name = "";
		char[] msgToDecode = newList.toCharArray();

		int users = 0;
		if (newList.length()>0) {
			users = 0;
			for (int i = 0; i<newList.length(); i++) {
				char current = msgToDecode[i];
				if (current==' ') {
					users++;
				}
			}
			if (users>1) {
				userList = new String[users-1];
				users = 0;
				for (int i = 0; i<newList.length(); i++) {
					char current = msgToDecode[i];
					if ((current==' ') && 
							(name.length()>0)) {
						if (!name.equals(client.getUsername())) {
							userList[users] = name;
							if (!userAlreadyExists(name)) {
								userlist.add(new User(name));
							}
							name = "";
							users++;
						}
						else {
							name = "";
						}
					}

					if (current != ' ') {
						name = name.concat(Character.toString(current));
					}
				}
				jList1.setListData(userList);
			}
			else {
				jList1.setListData(userList);
			}
		}
	}

	public synchronized void clearUserlist(){
		String[] userList = new String[0];
		jList1.setListData(userList);
		userlist.clear();
	}

	public synchronized void cleanUserlist(String[] jUserlist)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//System.out.println("ArraySize:"+userlist.size());
				System.out.println("Model:"+jList1.getModel().getSize());

				for (int i=0; i<userlist.size();i++) {
					boolean exists = false;
					for (int j= 0;j<jList1.getModel().getSize();j++) {
						System.out.println("Array Index:"+i);
						System.out.println("Model Index:"+j);
						if (userlist.get(i).getName().equals(jList1.getModel().getElementAt(j))) {
							userlist.get(j).setConnected(true);
							exists = true;
							break;
						}
						if (!exists) {
							userlist.get(j).setConnected(false);
						}
					}
				}
				for (int i = 0; i<userlist.size(); i++) {
					System.out.println("Array content:"+i+"-->"+userlist.get(i).getName());
					if (!userlist.get(i).isConnected()) {
						System.out.println("User "+userlist.get(i).getName()+" has been removed.");
						userlist.remove(i);
					}
				}
			}
		});
	}

	public synchronized void updateDisplay(String msg, JTextArea textDisplay) {
		displayTimer-=1;
		if (displayTimer<=0) {
			textDisplay.append(msg+"\n");
			textDisplay.setCaretPosition(textDisplay.getText().length());
			displayTimer = 0;
		}
	}

	public synchronized void clearDisplay(JTextArea textDisplay) {
		textDisplay.setText("");
	}

	public synchronized void resetCaretPosition(JTextArea textDisplay) {
		if (textDisplay.getCaretPosition()<textDisplay.getText().length())
			textDisplay.setCaretPosition(textDisplay.getText().length());
	}

	public synchronized void playSound(String sound){			
		FileInputStream music;
		try {
			music = new FileInputStream("resources/"+sound+".mp3");
			final Player mp3 = new Player(music);
			new Thread() {
				public void run() {
					try {mp3.play(); }
					catch (Exception e) { System.out.println(e); }
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void toggleConnectMenu() {
		if (jMenuItem1.isEnabled()) {
			jMenuItem1.setEnabled(false);
		}
		else
			jMenuItem1.setEnabled(true);
	}

	public synchronized void toggleLoginMenu()
	{
		if (jMenuItem2.isEnabled()) {
			jMenuItem2.setEnabled(false);
		}
		else
			jMenuItem2.setEnabled(true);
	}

	public synchronized void toggleChatInput()
	{
		if ((jTextArea2.isEnabled()) && (jTextArea2.isEditable())) {
			jTextArea2.setBackground(new Color(225, 225, 225));
			jTextArea2.setEnabled(false);
			jTextArea2.setEditable(false);
			jTextArea2.setText("Please log in first.");
		}
		else {
			jTextArea2.setBackground(new Color(255, 255, 255));
			jTextArea2.setEnabled(true);
			jTextArea2.setEditable(true);
			jTextArea2.setText("");
			jTextArea2.requestFocusInWindow();
		}
	}

	public synchronized int getCurrentTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("ss");
		String timeNow = formatter.format(currentDate.getTime());
		return Integer.parseInt(timeNow);
	}

	public synchronized void setCurrentTime(int currentTime) {
		this.currentTime = currentTime;
	}

	public synchronized JFrame getJframe() {
		return jframe;
	}

	private class CellRenderer implements ListCellRenderer {
		private JLabel jLabel1 = new JLabel();
		private Font font = new Font("Arial", 0, 16);

		public CellRenderer() {
			jLabel1.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			jLabel1.setText(jList1.getModel().getElementAt(index).toString());
			jLabel1.setFont(font);
			if (isSelected) {
				jLabel1.setBackground(Color.white);
				jLabel1.setForeground(userlist.get(index).getColor());
			} else {
				jLabel1.setBackground(Color.white);
				jLabel1.setForeground(userlist.get(index).getColor());
			}
			return jLabel1;
		}
	}

	@SuppressWarnings("serial")
	private class JTextFieldLimit extends PlainDocument {
		private int limit;

		JTextFieldLimit(int limit)
		{
			this.limit = limit;
		}

		public synchronized void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str==null) return;
			if (getLength()+str.length()<=limit)
				super.insertString(offset, str, attr);
		}
	}

	private class KeyInput extends KeyAdapter {
		private boolean shiftKey = false;
		public synchronized void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_UP) {
				if (historySize>0){
					if (!viewingHistory) {
						if (historyIndex<historySize) {
							jTextArea2.setText(history.get(historyIndex).getMessage());
						}
						else {
							jTextArea2.setText(history.get(historyIndex-1).getMessage());
						}

					}
					else if (viewedDown) {
						if (historySize>0) {
							historyIndex-=1;
							jTextArea2.setText(history.get(historyIndex-1).getMessage());
						}
					}
					else {
						if (historyIndex>0) {
							historyIndex-=1;
						}
						if (historyIndex<=0) {
							historyIndex = 1;
						}
						jTextArea2.setText(history.get(historyIndex-1).getMessage());
					}
				}
				viewedUp = true;
				viewedDown = false;
				viewingHistory = true;
			}
			if (e.getKeyCode()==KeyEvent.VK_DOWN) {
				if (historySize>0){
					if (!viewingHistory) {
						if (historyIndex<historySize) {
							jTextArea2.setText(history.get(historyIndex).getMessage());
						}
						else {
							jTextArea2.setText(history.get(historyIndex-1).getMessage());
						}
					}
					else if (viewedUp) {
						if (historyIndex<historySize) {
							historyIndex+=1;
						}
						jTextArea2.setText(history.get(historyIndex-1).getMessage());
					}
					else {
						if (historyIndex<historySize) {
							historyIndex+=1;
						}
						jTextArea2.setText(history.get(historyIndex-1).getMessage());
					}
				}
				viewedUp = false;
				viewedDown = true;
				viewingHistory = true;
			}
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				try {
					e.setKeyCode(0);
					e.consume();
					if (shiftKey==true){
						jTextArea2.insert("\n", jTextArea2.getCaretPosition());
					}
					else {
						if (Math.abs(currentTime-lastTimeSent)>=1){
							decodeInput(jTextArea2);							
						}
					}					
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
			if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
				shiftKey = true;
			}
		}
		public synchronized void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
				shiftKey = false;
			}			
		}
	}

	private class MenuWindow {
		private JFrame CWjframe;
		private JButton jButton1;
		private JLabel jLabel1;
		private JTextField jTextField1;
		private String title;
		private String labelText;
		private String buttonText;
		private String action;
		private JTextArea jTextArea2;

		public MenuWindow(String title, String labelText, String buttonText, String action, JTextArea jTextArea2){
			this.title = title;
			this.labelText = labelText;
			this.buttonText = buttonText;
			this.action = action;
			this.jTextArea2 = jTextArea2;
			initComponents();
		}

		public void initComponents() {
			CWjframe = new JFrame();
			Font font = new Font("Arial", Font.PLAIN, 12);
			jLabel1 = new JLabel();
			jTextField1 = new JTextField();
			jButton1 = new JButton();

			CWjframe.setIgnoreRepaint(false);
			CWjframe.setAlwaysOnTop(true);
			CWjframe.setTitle(title);
			Point jframeLocation = getJframe().getLocation();
			int locX = (int)jframeLocation.getX();
			int locY = (int)jframeLocation.getY();
			CWjframe.setLocation(locX+20, locY+30);

			jLabel1.setText(labelText);
			jLabel1.setFont(font);
			jTextField1.setText("");
			jTextField1.setFont(font);			
			jTextField1.requestFocusInWindow();
			jButton1.setText(buttonText);
			jButton1.setFont(font);

			if (action.equals("Login")) {
				jTextField1.setDocument(new JTextFieldLimit(12));
				jButton1.setEnabled(false);
				jTextField1.getDocument().addDocumentListener(new DocumentListener() {					
					public void changedUpdate(DocumentEvent e) {

					}
					public void removeUpdate(DocumentEvent e) {	
						int spaces = 0;
						boolean invalid = false;
						String username = jTextField1.getText();
						char[] username2 = username.toCharArray();
						for (int i=0;i<username.length();i++){
							char current = username2[i];
							if(current==' '){
								spaces++;
							}							
							else if (current=='\t') {
								invalid = true;
							}
							if (username2[0]=='@'){
								invalid = true;
							}
						}
						if (username.length()<=3 || spaces>1 || invalid==true){
							jTextField1.setForeground(new Color(255,0,0));
							jButton1.setEnabled(false);
						}
						else {
							jTextField1.setForeground(new Color(0,0,0));
							jButton1.setEnabled(true);
						}						
					}
					public void insertUpdate(DocumentEvent e) {	
						int spaces = 0;
						boolean invalid = false;
						String username = jTextField1.getText();
						char[] username2 = username.toCharArray();
						for (int i=0;i<username.length();i++){
							char current = username2[i];
							if(current==' '){
								spaces++;
							}							
							else if (current=='\t') {
								invalid = true;
							}
							if (username2[0]=='@'){
								invalid = true;
							}
						}
						if (username.length()<=3 || spaces>1 || invalid==true){
							jTextField1.setForeground(new Color(255,0,0));
							jButton1.setEnabled(false);
						}
						else {
							jTextField1.setForeground(new Color(0,0,0));
							jButton1.setEnabled(true);
						}
					}
				});
			}


			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					try {jButton1ActionPerformed(evt);
					} catch (IOException e) {}
				}
			});

			CWjframe.getRootPane().setDefaultButton(jButton1);

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(CWjframe.getContentPane());
			CWjframe.getContentPane().setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addComponent(jLabel1)
									.addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
									.addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
									.addContainerGap())
					);
			layout.setVerticalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(jLabel1)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(7, 7, 7)
							.addComponent(jButton1)
							.addContainerGap())
					);
			CWjframe.setVisible(true);
			CWjframe.pack();
		}// end initComponents()

		private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
			if (action.equals("Connect")){
				client.connect(jTextField1.getText());
				CWjframe.dispose();
				if (!client.getHost().equals("")){
					new MenuWindow("Login...","Enter username:","Log in","Login", jTextArea2);
				}
				else {
					new MenuWindow("Connect...", "Enter IP Address:", "Connect", "Connect", jTextArea2);
				}				
			}
			else if (action.equals("Login")){
				String username = jTextField1.getText();
				username = username.replace(' ', '_');
				username = username.substring(0,1).toUpperCase()+username.substring(1, username.length()); //Capitalize first letter
				if (username.length()>=3){
					client.send("login"+" "+username);
					CWjframe.dispose();
					jTextArea2.requestFocusInWindow();
				}				
			}
		}
	}

	/*private class ChatArea extends JTextPane {
		private Image backgroundLogo = new ImageIcon("resources/logo Wobble 03_medium.png").getImage();

		public ChatArea(){
			setOpaque(false);
			setBackground(new Color(0,0,0,0));
		}
		@Override
		protected void paintComponent(Graphics g) {
			g.drawImage(backgroundLogo, 310, 70, null);	

			super.paintComponent(g);
		}
	}*/
}