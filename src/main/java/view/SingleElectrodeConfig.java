package view;

import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleElectrodeConfig implements ActionListener {
	
	private ConnPanel connectionPane;
	private String passedValue;
	private ArrayList<Checkbox> checkboxes;
	private JButton confirmButton;
	private JFrame frame;
	private long config = 0;
	private String command;
	
	SingleElectrodeConfig(int e, ConnPanel pane)
	{
		connectionPane = pane;
		frame = new JFrame("Electrode Configuration");

        JPanel contentPane = new JPanel();
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new GridLayout(0,4));
        
        checkboxes = new ArrayList<Checkbox>();
        
        for (int i = 0; i < e/4; i++) {
        	for(int j = 0; j < 4; j++)
        	{
        		Checkbox checkbox = new Checkbox(""+(j+4*i + 1));
                checkboxes.add(checkbox);
                contentPane.add(checkbox);
        	}           
        }
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(this);
        contentPane.add(confirmButton);
        frame.setContentPane(contentPane);
        frame.pack();   
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confirmButton)
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
			config = signedConfig & 0xffffffffl;
			JSONObject JSONCommand =  new JSONObject();
			try 
			{
				JSONCommand.put("command", "singleMeasurement");
				JSONCommand.put("electrodesConfigurations", config);
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
	}
	
	public long getConfig()
	{
		return config;
	}
}
