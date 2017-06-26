package view;

import headerfile.FileHeader;
import headerfile.NormalizeData;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SensBmpPanel extends JPanel implements ChangeListener {
	
	 JSpinner spinner;
	 JPanel mapPane;
	
	 public SensBmpPanel(FileHeader fh, NormalizeData norm, boolean isNorm) throws IOException   {
	        norm.CopySensMatrix(fh);
	        norm.NormalizeMap();
	        norm.NormalizeMap2();
	    //    norm.NormalizeMap3();
	        norm.NormalizeMap4();
	        
	        this.setLayout(new BorderLayout());
	        
	        SpinnerModel spinnerModel =
	                new SpinnerNumberModel(1, //initial value
	                   1, //min
	                   fh.nPairOfElectrodes, //max
	                   1);//step
	        spinner = new JSpinner(spinnerModel);
	        spinner.setEditor(new JSpinner.DefaultEditor(spinner));
	        spinner.addChangeListener(this);
	        this.add(spinner,BorderLayout.NORTH);
	        
	        mapPane = new JPanel() {
	            @Override
	            public void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                if(isNorm)
	                {
	                		g.drawImage(getImageFromArray(norm.fov, norm.Sn2, norm.ImageSize, norm.ImageSize, (Integer)spinner.getValue()),
								0,
								0,
								this.getWidth(),
								this.getHeight(),
								this);
	                }
	                else
	                {
	                	g.drawImage(getImageFromArray(norm.fov, norm.Sn3, norm.ImageSize, norm.ImageSize, (Integer)spinner.getValue()),
								0,
								0,
								this.getWidth(),
								this.getHeight(),
								this);
	                }
	            }
	        };
	        this.add(mapPane, BorderLayout.CENTER);
	    }
  
	  public static BufferedImage getImageFromArray(boolean[] fovCircle, int[] pixels, int width, int height, int num) {
          BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          int alpha = 255;
          int red = 0,green = 0,blue = 0;
          ColourGradient g = new ColourGradient();
          
          for (int i = 0; i < height; i++) {
              for (int j = 0; j < width; j++) {
            	  

	            	  int value = (pixels[i*height+j + width*height*(num-1)]);
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
		
		if(e.getSource() == spinner)
		{
			mapPane.repaint();
		}
	}
}
