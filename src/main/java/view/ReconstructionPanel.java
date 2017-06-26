package view;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

import evt4dataprocessor.EVT4DataProcessor;
import headerfile.LineChart_AWT;
import headerfile.NormalizeData;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JCheckBox;



public class ReconstructionPanel extends JPanel implements ChangeListener, ActionListener
{
	JSpinner eConfigIdxSpinner, imageIdxSpinner, channelIdxSpinner, epsMaxSpinner,
	epsMinSpinner, relaxParameterSpinner, iterationsSpinner;
	SpinnerNumberModel iterSpinnerModel,epsMinSpinnerModel, epsMaxSpinnerModel, imageIdxSpinnerModel, 
						eConfigIdxSpinnerModel, channelIdxSpinnerModel, relaxSpinnerModel;
	JPanel chartPane;
	JLabel lblEconfig,lblImageindex;
	JButton loadDataButton, drawButton;
	JCheckBox rawDataCheckBox, GPUCheckBox;
	JComboBox displayDataTypeComboBox, reconMethodComboBox;
	String[] displayBoxContent = {"Single U","All U", "Channels"};
	String[] reconMethodBoxContent = {"LBP", "Landweber"};
	JPanel reconConfigPane;
	JTabbedPane displayTabbedPane;
	
	ImgBmpPanel imgPane;
	
	ControlPanel mainPane;
	EVT4DataProcessor dataProc;
	int eConfigIndex, imageIndex, channelIndex;
	
	ReconstructionPanel(ControlPanel mPane)
	{
		mainPane = mPane;
		setLayout(null);
		
		reconConfigPane = new JPanel();
		reconConfigPane.setBounds(10, 0, 195, 572);
		add(reconConfigPane);
		reconConfigPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Reconstruction method");
		lblNewLabel.setBounds(10, 11, 164, 14);
		reconConfigPane.add(lblNewLabel);
		
		JLabel lblIterations = new JLabel("Iterations");
		lblIterations.setBounds(10, 55, 123, 14);
		reconConfigPane.add(lblIterations);
		
		JLabel lblRelaxationParameters = new JLabel("Relaxation parameter");
		lblRelaxationParameters.setBounds(10, 111, 123, 14);
		reconConfigPane.add(lblRelaxationParameters);
		
		JLabel lblMin = new JLabel("Min");
		lblMin.setBounds(10, 163, 46, 14);
		reconConfigPane.add(lblMin);
		
		JLabel lblMax = new JLabel("Max");
		lblMax.setBounds(10, 214, 46, 14);
		reconConfigPane.add(lblMax);
		
		iterSpinnerModel = new SpinnerNumberModel(100, 1,  null, 1);	
		iterationsSpinner = new JSpinner(iterSpinnerModel);
		iterationsSpinner.setBounds(10, 70, 123, 20);
		reconConfigPane.add(iterationsSpinner);
		
		relaxSpinnerModel = new SpinnerNumberModel(0.01,0.001,1,0.001);
		relaxParameterSpinner = new JSpinner(relaxSpinnerModel);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(relaxParameterSpinner) ;
		relaxParameterSpinner.setEditor(editor);
		relaxParameterSpinner.setBounds(10, 128, 123, 20);
		reconConfigPane.add(relaxParameterSpinner);
		
		epsMinSpinnerModel = new SpinnerNumberModel(2, 1,  null, 1);
		epsMinSpinner = new JSpinner(epsMinSpinnerModel);
		epsMinSpinner.setBounds(10, 178, 123, 20);
		reconConfigPane.add(epsMinSpinner);
		
		epsMaxSpinnerModel = new SpinnerNumberModel(3, 1,  null, 1);
		epsMaxSpinner = new JSpinner(epsMaxSpinnerModel);
		epsMaxSpinner.setBounds(10, 229, 123, 20);
		reconConfigPane.add(epsMaxSpinner);
		
		eConfigIdxSpinnerModel = new SpinnerNumberModel(0, 0,  null, 1);
		eConfigIdxSpinner = new JSpinner(eConfigIdxSpinnerModel);
		eConfigIdxSpinner.setBounds(10, 457, 64, 20);
		eConfigIdxSpinner.addChangeListener(this);
		reconConfigPane.add(eConfigIdxSpinner);
		
		lblEconfig = new JLabel("EConfig No.");
		lblEconfig.setBounds(10, 441, 89, 14);
		reconConfigPane.add(lblEconfig);
		
		imageIdxSpinnerModel = new SpinnerNumberModel(0, 0,  null, 1);
		imageIdxSpinner = new JSpinner(imageIdxSpinnerModel);
		imageIdxSpinner.setBounds(10, 421, 64, 20);
		imageIdxSpinner.addChangeListener(this);
		reconConfigPane.add(imageIdxSpinner);
		
		lblImageindex = new JLabel("Image No.");
		lblImageindex.setBounds(10, 404, 89, 14);
		reconConfigPane.add(lblImageindex);
		
		channelIdxSpinnerModel = new SpinnerNumberModel(0, 0,  null, 1);
		channelIdxSpinner = new JSpinner(channelIdxSpinnerModel);
		channelIdxSpinner.setBounds(10, 491, 64, 20);
		channelIdxSpinner.addChangeListener(this);
		reconConfigPane.add(channelIdxSpinner);
		
		JLabel lblChannelNo = new JLabel("Channel No.");
		lblChannelNo.setBounds(10, 477, 64, 14);
		reconConfigPane.add(lblChannelNo);
		
		displayDataTypeComboBox = new JComboBox(displayBoxContent);
		displayDataTypeComboBox.setBounds(10, 373, 123, 20);
		displayDataTypeComboBox.addActionListener(this);
		reconConfigPane.add(displayDataTypeComboBox);
		
		JLabel lblSelectDisplay = new JLabel("Select Display");
		lblSelectDisplay.setBounds(10, 358, 89, 14);
		reconConfigPane.add(lblSelectDisplay);
		
		loadDataButton = new JButton("Load Data");
		loadDataButton.setBounds(10, 324, 103, 23);
		loadDataButton.addActionListener(this);
		reconConfigPane.add(loadDataButton);
		
		rawDataCheckBox = new JCheckBox("Raw Data");
		rawDataCheckBox.setBounds(119, 324, 97, 23);
		reconConfigPane.add(rawDataCheckBox);
		
		drawButton = new JButton("Draw");
		drawButton.addActionListener(this);
		drawButton.setBounds(20, 260, 89, 23);
		reconConfigPane.add(drawButton);
		
		reconMethodComboBox = new JComboBox(reconMethodBoxContent);
		reconMethodComboBox.setBounds(10, 24, 123, 20);
		reconMethodComboBox.addActionListener(this);
		reconConfigPane.add(reconMethodComboBox);
		
		GPUCheckBox = new JCheckBox("GPU");
		GPUCheckBox.setBounds(119, 260, 97, 23);
		reconConfigPane.add(GPUCheckBox);
		
		displayTabbedPane = new JTabbedPane();
		displayTabbedPane.setBounds(215, 11, 559, 561);
		add(displayTabbedPane);
		
		chartPane = new JPanel();
		chartPane.setLayout(new BorderLayout());
		displayTabbedPane.add("Raw Data", chartPane);
		
		imgPane = new ImgBmpPanel(this);
		displayTabbedPane.add("Image", imgPane);
		displayTabbedPane.setEnabledAt(1, false);
		
		
		mainPane = mPane;
		
		
		
		eConfigIndex = 0;
		imageIndex = 0;
		channelIndex = 0;
	}
	
	public void drawAll()
	{
		drawCharts();
		drawImage();		
	}
	public void drawCharts()
	{
		LineChart_AWT chart = new LineChart_AWT();		
		if(displayDataTypeComboBox.getSelectedItem() == "Single U")
		{
			chartPane.add(chart.createSingleUChartPanel("Single U" , "U: Img"+ imageIndex + "ElecConfig" + eConfigIndex, dataProc,
													imageIndex, eConfigIndex), BorderLayout.CENTER);
		}
		if(displayDataTypeComboBox.getSelectedItem() == "All U")
		{
			chartPane.add(chart.createAllUChartPanel("Single U" , "U: Img"+ imageIndex, dataProc,
													imageIndex), BorderLayout.CENTER);
		}
		if(displayDataTypeComboBox.getSelectedItem() == "Channels")
		{
			chartPane.add(chart.createChannelsChartPanel("Wykres danych" , "Channel " + channelIndex, 
						  						dataProc,imageIndex, eConfigIndex, channelIndex), BorderLayout.CENTER);
		}
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getRenderer().setSeriesPaint(0, Color.RED);
		plot.getRenderer().setSeriesPaint(1, Color.BLUE);
		chartPane.revalidate();
	}
	
	public void drawImage()
	{
		try 
		{
			if(mainPane.getNorm() == null) throw new IOException();			
			mainPane.getNorm().ImportImageData(dataProc.GetImageVector());
			mainPane.getNorm().NormalizeImageData();
			mainPane.getNorm().LBP();
			if(reconMethodComboBox.getSelectedItem() == "Landweber")
			{
				if(GPUCheckBox.isSelected())
				{
					mainPane.getNorm().GPULandweber((Integer)iterationsSpinner.getValue(),(Double)relaxParameterSpinner.getValue());
				}
				else
				{
					mainPane.getNorm().Landweber((Integer)iterationsSpinner.getValue(),(Double)relaxParameterSpinner.getValue());
				}

			}
			imgPane.createImage();
			imgPane.setBounds(318, 11, 452, 426);
			displayTabbedPane.setEnabledAt(1, true);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setProcessedData(EVT4DataProcessor data)
	{
		dataProc = data;
	}
	public EVT4DataProcessor getProcessedData()
	{
		return dataProc;
	}
	
	public NormalizeData getNorm()
	{
		return mainPane.getNorm();
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		if(e.getSource() == eConfigIdxSpinner)
		{
			eConfigIndex = (Integer) eConfigIdxSpinner.getValue();
			drawCharts();
		}
		
		if(e.getSource() == imageIdxSpinner)
		{
			imageIndex = (Integer) imageIdxSpinner.getValue();	
			dataProc.SetImageVector(imageIndex);
			drawAll();
		}
		
		if(e.getSource() == channelIdxSpinner)
		{
			channelIndex = (Integer) channelIdxSpinner.getValue();			
			drawCharts();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == loadDataButton)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Choose file");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".dat", "dat");
			chooser.setFileFilter(filter);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
			{				
				dataProc = new EVT4DataProcessor();
				dataProc.setNumberOfExcitations(16);
				dataProc.LoadDataFromFile(chooser.getSelectedFile().getAbsolutePath(), rawDataCheckBox.isSelected());
				dataProc.SetImageVector(imageIndex);
				drawAll();
			}
			else 
			{
			  System.out.println("No Selection ");
			}			
		}
		if(e.getSource() == displayDataTypeComboBox)
		{
			drawCharts();
		}
		if(e.getSource() == reconMethodComboBox)
		{
			drawImage();
		}
		if(e.getSource() == drawButton)
		{
			long startTime = System.currentTimeMillis();
			drawImage();
			long timeElapsed = (System.currentTimeMillis() - startTime);
			JOptionPane.showMessageDialog(this, "Time elapsed for " + reconMethodComboBox.getSelectedItem() +": "+ timeElapsed+" miliseconds");
		}
	}
}
