package view;

import headerfile.FileHeader;
import headerfile.NormalizeData;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import evt4dataprocessor.EVT4DataProcessor;
import javax.swing.BoxLayout;
import javax.swing.JScrollBar;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class ImgBmpPanel extends JPanel implements ChangeListener {
	
	 JSpinner minSpinner, maxSpinner;
	 JPanel imagePane;
	 ReconstructionPanel reconPane;
	
	 public ImgBmpPanel(ReconstructionPanel rPane) 
	 {
		reconPane = rPane;
	 	setLayout(new BorderLayout());
        
        JPanel thresholdPanel = new JPanel();
        add(thresholdPanel, BorderLayout.SOUTH);
        thresholdPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JLabel lblNewLabel = new JLabel("MinThreshold");
        thresholdPanel.add(lblNewLabel);
        
        minSpinner = new JSpinner();
        minSpinner.setValue(70);
        minSpinner.addChangeListener(this);
        thresholdPanel.add(minSpinner);
        
        JLabel lblNewLabel_1 = new JLabel("MaxThreshold");
        thresholdPanel.add(lblNewLabel_1);
        
        maxSpinner = new JSpinner();
        maxSpinner.setValue(245);
        maxSpinner.addChangeListener(this);
        thresholdPanel.add(maxSpinner);
	 }
	 
	 public void createImage()throws IOException
	 {              	
		try
     	{  		
			reconPane.getNorm().NormalizeImage((Integer)minSpinner.getValue(),(Integer)maxSpinner.getValue());
     	}
     	catch(Exception ex)
     	{
     		ex.printStackTrace();
     	}
		 	
	        imagePane = new JPanel() {
	            @Override
	            public void paintComponent(Graphics g) {
	
	                super.paintComponent(g);
	                try
	                {
	                	if(reconPane.getNorm() == null) throw new IOException();
	                	g.drawImage(getImageFromArray(reconPane.getNorm().fov, reconPane.getNorm().imageNorm, 
														reconPane.getNorm().ImageSize, reconPane.getNorm().ImageSize),
	                				0,
	                				0,
	                				this.getWidth(),
	                				this.getHeight(),
	                				this);
	                }
	                catch(IOException ex)
	                {
	                	ex.printStackTrace();
	                }
					
	            }
	        };
	        this.add(imagePane, BorderLayout.CENTER);
	 }
	 
	 
	  public static BufferedImage getImageFromArray(boolean[] fovCircle, int[] pixels, int width, int height) 
	  {
          BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          int alpha = 255;
          int red = 0,green = 0,blue = 0;
          ColourGradient g = new ColourGradient();
          
          for (int i = 0; i < height; i++) {
              for (int j = 0; j < width; j++) {
            	  
	            	  int value = (pixels[i*height+j]);
	            	  red   = g.Gradient[value].red;  
	            	  green = g.Gradient[value].green; ;     
	            	  blue  = g.Gradient[value].blue;

	               if(fovCircle != null && !fovCircle[i*height+j])
	               {
            		  red   = 0;
                	  green = 0;     
                	  blue  = 80;
	               }

                  int p = (alpha<<24) | (red<<16) | (green<<8) | blue;
                  image.setRGB(i, j, p);
              }
          }
          return image;
      }
			
	@Override
	public void stateChanged(ChangeEvent e) {
		
		if(e.getSource() == minSpinner || e.getSource() == maxSpinner)
		{
			reconPane.getNorm().NormalizeImage((Integer)minSpinner.getValue(),(Integer)maxSpinner.getValue());
			imagePane.repaint();
		}
	}
}
