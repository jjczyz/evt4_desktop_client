package view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import headerfile.NormalizeData;


public class ControlPanel extends JPanel 
{
	
	private JTabbedPane tabbedPane;
	
	private SensPanel sensPane;
	private ConnPanel connPane;
	private ReconstructionPanel reconPane;
	private AmpControlPanel ampPane;
	
	private NormalizeData norm;
	
	public ControlPanel()
	{
		this.setLayout(new BorderLayout());
		
		sensPane = new SensPanel(this);
		connPane = new ConnPanel(this);
		reconPane = new ReconstructionPanel(this);
		ampPane = new AmpControlPanel();
		
		
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Sensor", sensPane);
		tabbedPane.add("EVT4", connPane);
		tabbedPane.add("Reconstruction", reconPane);
		tabbedPane.add("Amplification control", ampPane);
						
		add(tabbedPane,BorderLayout.CENTER);
	}

	public NormalizeData getNorm() {
		return norm;
	}

	public void setNorm(NormalizeData norm) {
		this.norm = norm;
	}
	
	public SensPanel getSensPanel()
	{
		return sensPane;
	}
	
	public ConnPanel getConnPanel()
	{
		return connPane;
	}
	
	public ReconstructionPanel getReconPanel()
	{
		return reconPane;
	}

}
