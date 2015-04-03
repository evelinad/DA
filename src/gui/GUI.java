package gui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class GUI extends JFrame implements Runnable{
	private static final long serialVersionUID = -8118352433755008641L;
	private final int HEIGHT = 200;
	private final int WIDTH = 400;
	public JTextArea textArea;
	
	public GUI(int id, String protocol) {
		super(protocol + " node " + id);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLayout(new GridLayout(1, 1));
	    
        textArea = new JTextArea();
        textArea.setText("DUMMY");
        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        this.add(textArea);
        this.setVisible(true);  
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
