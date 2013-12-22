package server;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console
{
  private JFrame jframe = new JFrame();
  private JTextArea textDisplay;
  private int displayTimer = 0;

  public Console(String title) {
    Font font = new Font("Arial", 0, 14);
    this.textDisplay = new JTextArea();
    this.textDisplay.setEditable(false);
    this.textDisplay.setDisabledTextColor(new Color(0.1F, 0.1F, 0.1F));
    this.textDisplay.setFont(font);
    this.textDisplay.setLineWrap(true);
    this.jframe.setTitle(title);
    this.jframe.setContentPane(new JScrollPane(this.textDisplay));
    this.jframe.setSize(400, 500);
    this.jframe.setLocation(10, 40);
    this.jframe.setResizable(true);
    this.jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.jframe.setIgnoreRepaint(false);
    this.jframe.setVisible(true);
    this.jframe.setAlwaysOnTop(false);
  }

  public synchronized void updateDisplay(String message)
  {
    this.displayTimer -= 1;
    if (this.displayTimer <= 0) {
      this.textDisplay.append(message + "\n");
      this.textDisplay.setCaretPosition(this.textDisplay.getText().length());
      this.displayTimer = 0;
    }
  }

  public synchronized JFrame getJframe() {
    return this.jframe;
  }

  public synchronized void setJframe(JFrame jframe) {
    this.jframe = jframe;
  }
}