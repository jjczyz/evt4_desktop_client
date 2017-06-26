package view;

import headerfile.FileHeader;
import headerfile.Frame;
import headerfile.LineChart_AWT;
import headerfile.LoadFile;
import headerfile.NormalizeData;
import headerfile.PairOfElectrodes;
import headerfile.SensitivityMatrix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

import javax.swing.JTextField;

public class SensPanel extends JPanel implements ActionListener {

	
	private JButton loadFileButton;
	private ControlPanel cntrlPane;
	private JTextArea headerArea;
	private JScrollPane scroll;
	private JPanel sensorInfoPane, frameDataPane, sensMatrixPane, rawSensMatrixPane;
	private JTabbedPane sensTabbedPane;
	private FileHeader fh;
	
	private NormalizeData norm;

	public SensPanel(ControlPanel cPane)
	{
		cntrlPane = cPane;
		
		this.setLayout(null);
		Insets insets = this.getInsets();
				
		
		loadFileButton = new JButton("Load sensitivity matrix");
		loadFileButton.addActionListener(this);
		Dimension size = loadFileButton.getPreferredSize();
		loadFileButton.setBounds(10, 22,
		             163, 16);		
		this.add(loadFileButton);
		
		
		
		sensTabbedPane = new JTabbedPane();
		sensTabbedPane.setBounds(215, 11,
						559, 572);
		this.add(sensTabbedPane);
		
		sensorInfoPane = new JPanel();
		sensorInfoPane.setLayout(new BorderLayout());	
		
		headerArea = new JTextArea();
		headerArea.setEditable(false);
		scroll = new JScrollPane(headerArea);
		sensorInfoPane.add(scroll, BorderLayout.CENTER);
		
		frameDataPane = new JPanel();
		frameDataPane.setLayout(new BorderLayout());
		
		sensMatrixPane = new JPanel();
		sensMatrixPane.setLayout(new BorderLayout());
		
		rawSensMatrixPane = new JPanel();
		rawSensMatrixPane.setLayout(new BorderLayout());
		
		sensTabbedPane.add("Sensor information", sensorInfoPane);
		sensTabbedPane.add("Frame data", frameDataPane);
		sensTabbedPane.add("Sensitivity matrix", sensMatrixPane);
		sensTabbedPane.add("Sensitivity matrix - not normalised", rawSensMatrixPane);
			
		setEnableRec(this, false);
		loadFileButton.setEnabled(true);
		
		
	}
	
	private void setEnableRec(Component container, boolean enable){
	    container.setEnabled(enable);

	    try {
	        Component[] components= ((Container) container).getComponents();
	        for (int i = 0; i < components.length; i++) {
	            setEnableRec(components[i], enable);
	        }
	    } catch (ClassCastException e) {

	    }
	}
	
	public void writeFile(FileHeader fh) throws IOException
	{
		headerArea.append("EVT4HeaderFile:\nSygnatura: "+fh.signature + "\n");
		headerArea.append("Data: " + fh.date+ "\n");
		headerArea.append("Czas: " + fh.time+ "\n");
		headerArea.append("Nazwa: " + fh.name+ "\n");
		headerArea.append("Komentarz: " + fh.comment+ "\n");
		headerArea.append("Nazwa czujnika: " + fh.sensor_name+ "\n");
		headerArea.append("Typ sensora: " + fh.sensor_type+ "\n");
		headerArea.append("Ksztalt sensora: " + fh.sensor_shape+ "\n");
		headerArea.append("Calkowita liczba elektrod: " + fh.nElectrodes+ "\n");
		headerArea.append("Liczba pierscieni: " + fh.nRings+ "\n");
		headerArea.append("Liczba elektrod w pierscieniu: " + fh.nElectrodesInRing+ "\n");
		headerArea.append("Dlugosc: " + fh.d1+ "\n");
		headerArea.append("Szerokosc: " + fh.d2+ "\n");
		headerArea.append("Wysokosc: " + fh.h+ "\n");
		headerArea.append("Opis sondy: " + fh.sensor_description+ "\n");
		headerArea.append("Plik z definicja sondy i macierza wrazliwosci: " + fh.sensor_file_name+ "\n");
		headerArea.append("Offset do bloku macierzy wrazliwosci: " + fh.sensitivity_matrix_block_offset+ "\n");
		headerArea.append("Pomiary z powtorzeniami: " + fh.repetition+ "\n");
		headerArea.append("Liczba par elektrod: " + fh.nPairOfElectrodes+ "\n");
		headerArea.append("Nazwa metody pomiarowej: " + fh.method+ "\n");
		headerArea.append("Napiecie pobudzenia: " + fh.voltage+ "\n");
		headerArea.append("Offset do bloku par elektrod: " + fh.pair_of_electrodes_block_offset+ "\n");
		headerArea.append("Kalibracja min: " + fh.calibration_min+ "\n");
		headerArea.append("Kalibracja max: " + fh.calibration_max+ "\n");
		headerArea.append("Minimalna wzgledna przenikalnosc elektryczna: " + fh.permitivity_min+ "\n");
		headerArea.append("Maksymalna wzgledna przenikalnosc elektryczna: " + fh.permitivity_max+ "\n");
		headerArea.append("Typ pliku: " + fh.file_type+ "\n");
		headerArea.append("Liczba probek czasowych: " + fh.nFrames+ "\n");
		headerArea.append("Probkowanie ramek [us]: " + fh.interval+ "\n");
		headerArea.append("Offset danych pomiarowych(od poczatku pliku): " + fh.data_block_offset+ "\n");
		
		SensitivityMatrix sm = fh.sm;
		headerArea.append("\nEVT4SensitivityMatrix:\nRozmiar naglowka bloku: " + sm.size+ "\n");
		headerArea.append("Rozmiar x obrazu: " + sm.img_size_x+ "\n");
		headerArea.append("Rozmiar y obrazu: " + sm.img_size_y+ "\n");
		headerArea.append("Rzedy: " + sm.rows+ "\n");
		headerArea.append("Kolumny: " + sm.columns+ "\n");
		headerArea.append("Precyzja: " + sm.precision+ "\n");
		
		ArrayList<PairOfElectrodes> poe_list = fh.poe_list;
		PairOfElectrodes poe = poe_list.get(0);
		headerArea.append("\nEVT4PairOfElectrodes[0]:\nMaska bitow wymuszajacych: "+ poe.excitation_electrodes_mask+ "\n");
		headerArea.append("Numer mierzonej elektrody: "+ poe.measurement_electrode_number+ "\n");
		headerArea.append("Szerokosc elektrody: " + poe.el_width+ "\n");
		headerArea.append("Wysokosc elektrody: " +poe.el_height+ "\n");
		headerArea.append("Numer karty ReadOut: " + poe.readout_number+ "\n");
		headerArea.append("Numer kanalu: " + poe.channel_number+ "\n");
		headerArea.append("Wzmocnienie: "+poe.amplification+ "\n");
		headerArea.append("Offset: "+poe.offset+ "\n");
		headerArea.append("Ustawienie analogowych przelacznikow: "+poe.analogue_switches_settings+ "\n");
		headerArea.append("Ustawienia wzmacniacza operacyjnego: " +poe.op_amp_settings+ "\n");
		headerArea.append("Wartosci kalibracyjne z kanalu \nmin: " +poe.min +"\nmax: " +poe.max+ "\n");
				
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == loadFileButton)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Choose file");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".cap", "cap");
			chooser.setFileFilter(filter);

			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
			{
				LoadFile lf = new LoadFile();
				try {
					fh = lf.LoadFileHeader(chooser.getSelectedFile().getAbsolutePath());			
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if(fh != null) 
				{
					setEnableRec(this,true);
				}
				
				try 
				{
					writeFile(fh);
					norm = new NormalizeData(fh.sm.img_size_x,fh.nPairOfElectrodes);
					norm.CopyMinMax(fh);
					cntrlPane.setNorm(norm);
					
					LineChart_AWT chart = new LineChart_AWT();
					frameDataPane.add(chart.createSensitivityChartPanel("Wykres danych" , "Dane w ramkach", fh), BorderLayout.CENTER);
					XYPlot plot = (XYPlot) chart.getPlot();
					plot.getRenderer().setSeriesPaint(0, Color.RED);
					plot.getRenderer().setSeriesPaint(1, Color.BLUE);
					frameDataPane.revalidate();
					
					SensBmpPanel bmp = new SensBmpPanel(fh, norm, true);
					sensMatrixPane.add(bmp, BorderLayout.CENTER);
					sensMatrixPane.revalidate();
					
					bmp = new SensBmpPanel(fh, norm, false);
					rawSensMatrixPane.add(bmp, BorderLayout.CENTER);
					rawSensMatrixPane.revalidate();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
			else 
			{
			  System.out.println("No Selection ");
			}
			
			
		}
	}
	
	public NormalizeData getNormData()
	{
		return norm;
	}
}
