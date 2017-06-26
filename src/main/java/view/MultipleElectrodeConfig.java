package view;
	
import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MultipleElectrodeConfig implements ActionListener{
		
	private ConnPanel connectionPane;
	private JFrame frame;
	private JPanel checkboxPane, mainPane, buttonPane;
	private JButton confirmButton, addConfigButton, deleteButton;
	private JTextField imageNumField;
	private JLabel imageNumLabel;
	
	private String command;
	
	private JScrollPane configsListScrollPane;
	private JList<String> configsList;
	private DefaultListModel<String> configsListModel;
	
	private ArrayList<Checkbox> checkboxes;
	private ArrayList<String> configs;
	private int imageNum;
	
	MultipleElectrodeConfig(int e, ConnPanel pane)
	{
		connectionPane = pane;
		frame = new JFrame("Electrode Configuration");
		frame.setSize(400, 600);
		configs = new ArrayList<String>();
		mainPane = new JPanel();
		mainPane.setBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		
		checkboxPane = new JPanel();
		checkboxPane.setLayout(new GridLayout(0,4));	        
	    checkboxes = new ArrayList<Checkbox>();
	    for (int i = 0; i < e/4; i++) {
	    	for(int j = 0; j < 4; j++)
	    	{
	    		Checkbox checkbox = new Checkbox(""+(j+4*i + 1));
	    		checkboxes.add(checkbox);
	    		checkboxPane.add(checkbox);
	    	}           
	    }	        
	    c.fill = GridBagConstraints.BOTH;
	    c.weightx = 1.0;
	    c.weighty = 1.0;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 4;
	    c.gridheight = e/4;
	    mainPane.add(checkboxPane,c);

	    configsListModel = new DefaultListModel<String>();
	    configsList = new JList<String>(configsListModel);
	    configsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	    configsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	    configsList.setFixedCellWidth(90);
	    configsList.setVisibleRowCount(-1);
	    configsListScrollPane =  new JScrollPane(configsList);
	    c.fill = GridBagConstraints.BOTH;
	    c.gridx = 0;
	    c.gridy = e/4 + 1;
	    c.gridwidth = 3;
	    c.gridheight = 4;
	    mainPane.add(configsListScrollPane, c);
	    
	    buttonPane = new JPanel();	
	    buttonPane.setLayout(new GridLayout(5,1,0,5));
	    
	    addConfigButton = new JButton("Add");
	    addConfigButton.addActionListener(this);
	    buttonPane.add(addConfigButton);
	    
	    deleteButton = new JButton("Delete");
	    deleteButton.addActionListener(this);
	    buttonPane.add(deleteButton);
	    
	    imageNumLabel = new JLabel("No. Images");
	    buttonPane.add(imageNumLabel);
	    
	    imageNumField = new JTextField();
	    buttonPane.add(imageNumField);
	    
	    confirmButton = new JButton("Confirm");
	    confirmButton.addActionListener(this);
	    buttonPane.add(confirmButton);
	    
	    c.fill = GridBagConstraints.HORIZONTAL;
	    c.weightx = 0;
	    c.weighty = 0;
	    c.gridwidth = 1;
	    c.gridheight = 4;
	    c.gridx = 3;
	    c.gridy = e/4 + 1;
	    c.anchor = GridBagConstraints.NORTH;
	    mainPane.add(buttonPane,c);
	    
	    frame.setContentPane(mainPane);   
	    frame.setLocationByPlatform(true);
	    frame.setVisible(true);
	}
		@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirmButton)
		{
				for(int i=0; i < configsListModel.getSize(); i++)
				{
				     String o =  configsListModel.getElementAt(i); 
				     configs.add(o);
				}
				try
				{
					imageNum = Integer.parseInt(imageNumField.getText());
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(mainPane, ex.getMessage(), "Wrong Image Number", JOptionPane.ERROR_MESSAGE);
				}
				
				JSONObject JSONCommand =  new JSONObject();
				try 
				{
					JSONCommand.put("command", "multipleMeasurement");
					JSONCommand.put("numberOfImages", imageNum);
					JSONCommand.put("electrodesConfigurations", configs);
				} 
				catch (JSONException e1) 
				{
					e1.printStackTrace();
				}
				command = JSONCommand.toString();
				if(connectionPane.getIfOnlineProcessing()) 
				{
					connectionPane.getUDPConn().StartWithOnlineProcessing();;				
				}
				else
				{
					connectionPane.getUDPConn().StartWithoutOnlineProcessing();;	
				}
				connectionPane.receivingStarted();
				connectionPane.getTCPConn().SendCommand(command);
				frame.dispose();			
		}
		if(e.getSource() == addConfigButton)
		{
			int signedConfig = 0;
			for (int i=0; i < checkboxes.size(); i++)
			{
				if (checkboxes.get(i).getState())
				{
					signedConfig += 1 << i;
				}
			}
			// value has to be unsigned
			long unsignedConfig = signedConfig & 0xffffffffl;
			String config = ("0x" + String.format("%08x", unsignedConfig));
			configsListModel.addElement(config);
		}
		if(e.getSource() == deleteButton)
		{
			configsListModel.remove(configsList.getSelectedIndex());
		}
	}
}
