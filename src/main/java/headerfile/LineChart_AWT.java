package headerfile;


import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import evt4dataprocessor.EVT4DataProcessor;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart_AWT
{
	private JFreeChart lineChart;
	
   public ChartPanel createSensitivityChartPanel(String applicationTitle , String chartTitle, FileHeader fh )
   {
      lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "","Value",
         createSensitivityMatrixDataset(fh),
         PlotOrientation.VERTICAL,
         true,true,false);
      
      XYPlot plot = (XYPlot)lineChart.getPlot();
      NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
      yAxis.setAutoRangeIncludesZero(false);

      NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
      xAxis.setAutoRangeIncludesZero(false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      return chartPanel;
   }
   
   public ChartPanel createSingleUChartPanel(String applicationTitle , String chartTitle, EVT4DataProcessor dataProc, int imgIdx, int eCfgIdx )
   {
	   
       lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "","Value",
         createSingleUDataset(dataProc, imgIdx, eCfgIdx),
         PlotOrientation.VERTICAL,
         true,true,false);
      
      XYPlot plot = (XYPlot)lineChart.getPlot();
      NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
      yAxis.setAutoRangeIncludesZero(false);

      NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
      xAxis.setAutoRangeIncludesZero(false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      return chartPanel;
   }
   
   public ChartPanel createAllUChartPanel(String applicationTitle , String chartTitle, EVT4DataProcessor dataProc, int imgIdx)
   {
       lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "","Value",
         createAllUDataset(dataProc, imgIdx),
         PlotOrientation.VERTICAL,
         true,true,false);
      
      XYPlot plot = (XYPlot)lineChart.getPlot();
      NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
      yAxis.setAutoRangeIncludesZero(false);

      NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
      xAxis.setAutoRangeIncludesZero(false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      return chartPanel;
   }
   
   public ChartPanel createChannelsChartPanel(String applicationTitle , String chartTitle, 
		   					EVT4DataProcessor dataProc, int imgIdx, int excIdx, int chIdx )
   {
      lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "","Value",
         createChannelsDataset(dataProc,imgIdx, excIdx, chIdx),
         PlotOrientation.VERTICAL,
         true,true,false);
      
   // Create an NumberAxis
      XYPlot plot = (XYPlot)lineChart.getPlot();
      NumberAxis yAxis = (NumberAxis) plot.getDomainAxis();
      yAxis.setAutoRangeIncludesZero(false);

      NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
      xAxis.setAutoRangeIncludesZero(false);
      
             
      ChartPanel chartPanel = new ChartPanel(lineChart);
      return chartPanel;
   }

   private XYDataset createSensitivityMatrixDataset(FileHeader fh )
   {
	  XYSeriesCollection  dataset = new XYSeriesCollection ( );
	  final XYSeries series1 = new XYSeries("Max");
	  final XYSeries series2 = new XYSeries("Min");
	  
      for(int i = 0; i < fh.poe_list.size(); i++)
      {
    	  series1.add(i,(double) fh.poe_list.get(i).max);
    	  series2.add(i,(double) fh.poe_list.get(i).min);
      }
      
      dataset.addSeries(series1);
      dataset.addSeries(series2);

      return dataset;
   }
   
   private XYDataset createSingleUDataset(EVT4DataProcessor dataProc, int imgIdx, int eExcIdx )
   {
	   XYSeriesCollection dataset = new XYSeriesCollection( );
	   final XYSeries channelData = new XYSeries("Image "+imgIdx+"  EConfig "+eExcIdx );
	   dataProc.SetUVector(imgIdx, eExcIdx);
       for(int i = 0; i < dataProc.GetUVector().size(); i++)
       {
     	  channelData.add(i,(double) dataProc.GetUVector().get(i));
       }
       dataset.addSeries(channelData);

       return dataset;
   }
   
   private XYDataset createAllUDataset(EVT4DataProcessor dataProc, int imgIdx)
   {
	   XYSeriesCollection dataset = new XYSeriesCollection( );
	   final XYSeries channelData = new XYSeries("Image "+imgIdx);
	   for(int j = 0; j< dataProc.numberOfExcitations -1; j++)
	   {
	   dataProc.SetUVector(imgIdx, j);
	       for(int i = 0; i < dataProc.GetUVector().size(); i++)
	       {
	     	  channelData.add(i + j*dataProc.GetUVector().size(),(double) dataProc.GetUVector().get(i));
	       }
	   }
       dataset.addSeries(channelData);

       return dataset;
   }
   
   private XYDataset createChannelsDataset(EVT4DataProcessor dataProc,int imgIdx, int eExcIdx, int channelIndex )
   {
	  XYSeriesCollection dataset = new XYSeriesCollection( );
	  final XYSeries channelData = new XYSeries("Image "+imgIdx+"  EConfig "+eExcIdx+"  Channel "+channelIndex);
      for(int i = 0; i < dataProc.images.get(imgIdx).excitationData.get(eExcIdx).channelsData.get(channelIndex).sampleData.size(); i++)
      {
    	  channelData.add(i,(double) -dataProc.images.get(imgIdx).excitationData.get(eExcIdx).channelsData.get(channelIndex).sampleData.get(i));
      }
      dataset.addSeries(channelData);

      return dataset;
   }

	public XYPlot getPlot() 
	{
		
		return lineChart.getXYPlot();
	}
}
