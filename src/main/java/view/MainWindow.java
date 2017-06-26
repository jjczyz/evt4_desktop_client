package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;


public class MainWindow extends JFrame{
	
	private ControlPanel ctrlPane;
	
	
	public MainWindow()
	{
		ctrlPane = new ControlPanel();
		this.setSize(800,700);
		this.setTitle("EVT4 Control Panel");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());	
		this.add(ctrlPane, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	
	
	public static void main(String[] args)
	{
	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            	new MainWindow();
	        }
	    });
	}

}
