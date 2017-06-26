package view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.json.JSONException;
import org.json.JSONObject;

import communication.Config;
import communication.TCPConnection;
import communication.UDPConnection;
import javax.swing.JCheckBox;

public class ConnPanel extends JPanel implements ActionListener {
	
	private ControlPanel cntrlPane;
	private ImgBmpPanel imgBmpPane;
	private JLabel ipLabel, commandsLabel, connectionStatusLabel,electrodeNumLabel;
	private JButton customConfigButton, resetButton, saveRawDataButton, connectButton, disconnectButton, 
					stopButton, processDataButton, multipleMeasurementButton;
	private JTextField ipField,imageNumField;
	private String command;
	private JCheckBox onlineProcessingCheckBox;
	
	private JComboBox electrodeSelectionBox;
	private String[] electrodeSelectionBoxContent = {"4","8","12","16","20","24","28","32"};
	
	private SingleElectrodeConfig singleEConfig;
	private MultipleElectrodeConfig multipleEConfig;
	
	private UDPConnection UDPConn;
	private TCPConnection TCPConn;
	
	private int electrodeNum, imageNum;
	
	
	
	ConnPanel(ControlPanel cPane)
	{
		cntrlPane = cPane;
		setLayout(null);				
		
		ipLabel = new JLabel("IP:");
		Dimension size = ipLabel.getPreferredSize();
		ipLabel.setBounds(25, 21,
		             size.width, size.height);		
		add(ipLabel);
		

		ipField = new JTextField("192.168.###.###");
		ipField.addActionListener(this);
		size = ipField.getPreferredSize();
		ipField.setBounds(49, 18,
			             size.width, size.height);			
		add(ipField);
		
		connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		connectButton.setBounds(25, 46,
		             102, 23);		
		add(connectButton);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.addActionListener(this);
		disconnectButton.setBounds(25, 80,
					102, 23);		
		add(disconnectButton);
		
		commandsLabel = new JLabel("No Images");
		commandsLabel.setBounds(81, 171,
		             83, 14);		
		add(commandsLabel);
		
		
		electrodeSelectionBox = new JComboBox(electrodeSelectionBoxContent);
		electrodeSelectionBox.addActionListener(this);
		electrodeSelectionBox.setBounds(25, 137,
		             46, 20);		
		add(electrodeSelectionBox);
		electrodeSelectionBox.setEnabled(false);
		try
		{
			electrodeNum = Integer.valueOf(electrodeSelectionBoxContent[0]);
		}
		catch(IllegalArgumentException err)
		{
			System.out.println("Wrong arguments in electrodeSelectionBoxContent");
		}
		
				
		
		customConfigButton = new JButton("Custom Configuration");
		customConfigButton.addActionListener(this);
		customConfigButton.setBounds(25, 233,
		             157, 23);		
		add(customConfigButton );
		customConfigButton.setEnabled(false);
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		resetButton.setBounds(25, 267,
		             73, 23);		
		add(resetButton);
		resetButton.setEnabled(false);
		
		multipleMeasurementButton = new JButton("Measure N Images");
		multipleMeasurementButton.addActionListener(this);
        size = multipleMeasurementButton.getPreferredSize();
        multipleMeasurementButton.setBounds(25, 199,
                     157, 23);      
        add(multipleMeasurementButton);
        multipleMeasurementButton.setEnabled(false);
		
		connectionStatusLabel = new JLabel("Not connected");
		connectionStatusLabel.setBounds(341, 21,
	             245, 14);
		add(connectionStatusLabel);
		
		saveRawDataButton = new JButton("Save raw data");
		saveRawDataButton.addActionListener(this);
		saveRawDataButton.setBounds(341, 114,
	             135, 23);
		add(saveRawDataButton );
		saveRawDataButton.setEnabled(false);
		
		stopButton = new JButton("Stop");
		stopButton.setBounds(341, 46, 103, 23);
		add(stopButton);
		stopButton.setEnabled(false);
		
		processDataButton = new JButton("Process Data");
		processDataButton.setBounds(341, 80, 135, 23);
		processDataButton.addActionListener(this);
		add(processDataButton);
		processDataButton.setEnabled(false);
		
		onlineProcessingCheckBox = new JCheckBox("Online processing");
		onlineProcessingCheckBox.setBounds(135, 45, 135, 23);
		add(onlineProcessingCheckBox);
		
		imageNumField = new JTextField();
		imageNumField.setBounds(25, 168, 46, 20);
		add(imageNumField);
		imageNumField.setColumns(10);
		imageNumField.setEnabled(false);
		
		electrodeNumLabel = new JLabel("No Electrodes");
		electrodeNumLabel.setBounds(81, 140, 83, 14);
		add(electrodeNumLabel);
		
		
			
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == connectButton)
		{
			try 
			{
				Config.IP_ADDRESS = InetAddress.getByName(ipField.getText());
				openConnection();			
			} 
			catch (UnknownHostException err) {
				err.printStackTrace();
			}			
		}
		
		if(e.getSource() == disconnectButton)
		{
			closeConnection();	
		}
		
		if(e.getSource() == customConfigButton)
		{
			multipleEConfig = new MultipleElectrodeConfig(32,this);
		}
		if(e.getSource() == resetButton)
		{
			JSONObject JSONCommand =  new JSONObject();
			try {
				JSONCommand.put("command", "reset");
			} catch (JSONException err) {
				err.printStackTrace();
			}
			command = JSONCommand.toString();
			TCPConn.SendCommand(command);
		}
		
		if(e.getSource() == saveRawDataButton)
		{
			UDPConn.saveRawDataToFiles("test");
		}
		
		if(e.getSource() == stopButton)
		{
			UDPConn.stopThreads();
		}
		if(e.getSource() == processDataButton)
		{
			connectionStatusLabel.setText("Connected - Processing Data");
			saveRawDataButton.setEnabled(false);
			UDPConn.getProcessThread().start();
		}
		
		if(e.getSource() == electrodeSelectionBox)
		{
			electrodeNum = Integer.valueOf((String)electrodeSelectionBox.getSelectedItem());
		}
		
		if(e.getSource() == multipleMeasurementButton) {
		    
			try
			{
				imageNum = Integer.parseInt(imageNumField.getText());
				JSONObject JSONCommand =  new JSONObject();
	            try {
	                JSONCommand.put("command", "multipleMeasurement");
	                JSONCommand.put("numberOfImages", imageNum);
	                List<String> l = new ArrayList<String>();
	                
	                for(int i = 0; i < electrodeNum; ++i) {
	                    l.add(String.format("0x%x", new Long(new Double(Math.pow(2,i)).longValue())));
	                }
	                
	                JSONCommand.put("electrodesConfigurations", l);
	            } catch (JSONException err) {               
	            	err.printStackTrace();
	            }
	            command = JSONCommand.toString();
	            if(getIfOnlineProcessing())
	            {
	            	UDPConn.StartWithOnlineProcessing();
	            }
	            else
	            {
	            	UDPConn.StartWithoutOnlineProcessing();
	            }
	            receivingStarted();          
	            TCPConn.SendCommand(command);
			}
			catch(IllegalArgumentException err)
			{
				err.printStackTrace();
				JOptionPane.showMessageDialog(this, err.getMessage(), "Wrong Image Number", JOptionPane.ERROR_MESSAGE);
			}			
		}
	}
	public void openConnection()
	{	
		try
		{
		    UDPConn = new UDPConnection(50000, this);
			TCPConn = new TCPConnection();
			connectionStatusLabel.setText("Connected to: " + Config.IP_ADDRESS);
			
			electrodeSelectionBox.setEnabled(true);
			imageNumField.setEnabled(true);
			multipleMeasurementButton.setEnabled(true);
			resetButton.setEnabled(true);
			customConfigButton.setEnabled(true);	
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void closeConnection()
	{	
		if(TCPConn != null) TCPConn.closeConnection();
		if(UDPConn != null) UDPConn.closeConnection();
		connectionStatusLabel.setText("Not connected");
		
		electrodeSelectionBox.setEnabled(false);
		imageNumField.setEnabled(false);
		multipleMeasurementButton.setEnabled(false);
		resetButton.setEnabled(false);
		customConfigButton.setEnabled(false);
	}
	public void receivingStarted()
	{
		stopButton.setEnabled(true);
		connectionStatusLabel.setText("Connected - Receiving Data");
		stopButton.setEnabled(true);
	}
	public void receivingFinished()
	{
		connectionStatusLabel.setText("Connected - Receiving Finished");
		saveRawDataButton.setEnabled(true);
		processDataButton.setEnabled(true);
	}
	public void processingFinished()
	{
		connectionStatusLabel.setText("Connected - Processing Finished");
		saveRawDataButton.setEnabled(true);
		
		
		cntrlPane.getReconPanel().setProcessedData(UDPConn.getDataProc());
		cntrlPane.getReconPanel().drawAll();
		
	}
	public boolean getIfOnlineProcessing()
	{
		return onlineProcessingCheckBox.isSelected();
	}
	
	public UDPConnection getUDPConn()
	{
		return UDPConn;
	}
	
	public TCPConnection getTCPConn()
	{
		return TCPConn;
	}
}
