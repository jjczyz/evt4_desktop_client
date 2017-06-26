package headerfile;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import javax.swing.JOptionPane;



public class LoadFile {
	
	public FileHeader LoadFileHeader (String filePath) throws IOException 
	{
		  InputStream is = null;
	      DataInputStream dis = null; 
	      FileHeader fh = new FileHeader();
	      try{
	    	 FileHeaderFormat fh_form = new FileHeaderFormat();
	         is = new FileInputStream(filePath);         
	         dis = new DataInputStream(is);         	           
	         dis.readFully(fh_form.signature); 	
	         fh.signature = bytesToString(fh_form.signature);
	         dis.readFully(fh_form.size);	
	         fh.size = bytesToString(fh_form.size);
	         dis.readFully(fh_form.date);	
	         fh.date = bytesToString(fh_form.date);
	         dis.readFully(fh_form.time);			
	         fh.time = bytesToString(fh_form.time);
	         dis.readFully(fh_form.name);
	         fh.name = bytesToString(fh_form.name);
	         dis.readFully(fh_form.comment);	
	         fh.comment = bytesToString(fh_form.comment);
	         dis.readFully(fh_form.sensor_name);		
	         fh.sensor_name = bytesToString(fh_form.sensor_name);
	         dis.readFully(fh_form.sensor_type);		
	         fh.sensor_type = bytesToString(fh_form.sensor_type);
	         dis.readFully(fh_form.sensor_shape);		
	         fh.sensor_shape = bytesToString(fh_form.sensor_shape);
	         dis.readFully(fh_form.nElectrodes);		
	         fh.nElectrodes = bytesToShort(fh_form.nElectrodes);
	         dis.readFully(fh_form.nRings);			
	         fh.nRings = bytesToShort(fh_form.nRings);
	         dis.readFully(fh_form.nElectrodesInRing);
	         fh.nElectrodesInRing = bytesToShort(fh_form.nElectrodesInRing);
	         dis.readFully(fh_form.d1);			
	         fh.d1 = bytesToFloat(fh_form.d1);
	         dis.readFully(fh_form.d2);			
	         fh.d2 = bytesToFloat(fh_form.d2);
	         dis.readFully(fh_form.h);		
	         fh.h = bytesToFloat(fh_form.h);
	         dis.readFully(fh_form.sensor_description);	
	         fh.sensor_description = bytesToString(fh_form.sensor_description);
	         dis.readFully(fh_form.sensor_file_name);		
	         fh.sensor_file_name = bytesToString(fh_form.sensor_file_name);
	         dis.readFully(fh_form.sensitivity_matrix_block_offset);	
	         fh.sensitivity_matrix_block_offset = bytesToInt(fh_form.sensitivity_matrix_block_offset);
	         dis.readFully(fh_form.repetition);		
	         fh.repetition = bytesToShort(fh_form.repetition);
	         dis.readFully(fh_form.nPairOfElectrodes);
	         fh.nPairOfElectrodes = bytesToShort(fh_form.nPairOfElectrodes);
	         dis.readFully(fh_form.method);
	         fh.method = bytesToString(fh_form.method);
	         dis.readFully(fh_form.voltage);
	         fh.voltage = bytesToFloat(fh_form.voltage);
	         dis.readFully(fh_form.pair_of_electrodes_block_offset);
	         fh.pair_of_electrodes_block_offset = bytesToInt(fh_form.pair_of_electrodes_block_offset);
	         dis.readFully(fh_form.calibration_min);
	         fh.calibration_min = bytesToShort(fh_form.calibration_min);
	         dis.readFully(fh_form.calibration_max);
	         fh.calibration_max = bytesToShort(fh_form.calibration_max);
	         dis.readFully(fh_form.permitivity_min);
	         fh.permitivity_min = bytesToFloat(fh_form.permitivity_min);
	         dis.readFully(fh_form.permitivity_max);
	         fh.permitivity_max = bytesToFloat(fh_form.permitivity_max);
	         dis.readFully(fh_form.file_type);
	         fh.file_type = bytesToShort(fh_form.file_type);
	         dis.readFully(fh_form.nFrames);
	         fh.nFrames = bytesToInt(fh_form.nFrames);
	         dis.readFully(fh_form.interval);
	         fh.interval = bytesToInt(fh_form.interval);
	         dis.readFully(fh_form.data_block_offset);
	         fh.data_block_offset = bytesToInt(fh_form.data_block_offset);
	      }catch(Exception e){
	         e.printStackTrace();
	         JOptionPane.showMessageDialog(null, "Wrong file directory");
	         return null;
	      }finally{
	         
	         if(is!=null)
	            is.close();
	         if(dis!=null)
	            dis.close();
	      }
	      
	      	fh.sm = LoadSensitivityMatrix(filePath, fh);
	      	fh.poe_list = LoadPairOfElectrodes(filePath, fh);
	      	fh.frameList = LoadFrame(filePath, fh);
	      return fh;
	 }
	
	
	public SensitivityMatrix LoadSensitivityMatrix(String filePath, FileHeader fh) throws IOException
	{
		  InputStream is = null;
	      DataInputStream dis = null;
	      SensitivityMatrix sm = new SensitivityMatrix();
	      try{
	    	  if(fh.sensitivity_matrix_block_offset != 0)
	    	  {
	    		  is = new FileInputStream(filePath);         
			      dis = new DataInputStream(is);
	    		  dis.skipBytes(fh.sensitivity_matrix_block_offset);
	    		  SensitivityMatrixFormat sm_form = new SensitivityMatrixFormat(); 
				  dis.readFully(sm_form.size);
		          sm.size = bytesToShort(sm_form.size);
			      dis.readFully(sm_form.img_size_x);
    		      sm.img_size_x = bytesToShort(sm_form.img_size_x);
			      dis.readFully(sm_form.img_size_y);
			      sm.img_size_y = bytesToShort(sm_form.img_size_y);
			      dis.readFully(sm_form.rows);
			      sm.rows = bytesToShort(sm_form.rows);
			      dis.readFully(sm_form.columns);
			      sm.columns = bytesToShort(sm_form.columns);
	     	      dis.readFully(sm_form.precision);
			      sm.precision = bytesToShort(sm_form.precision);
	
			      byte[] sm_value = new byte[4];
			      sm.S = new float[sm.rows][sm.columns];
			      for(int i=0; i<sm.rows; i++)
			      {
			    	  for(int j=0; j<sm.columns; j++)
			    	  {
			    		  dis.readFully(sm_value);
			    		  sm.S[i][j] = bytesToFloat(sm_value);
			    	  }
			      }  
	    	  }
	    	  
	      }catch(Exception e){
		         e.printStackTrace();
		      }finally{
		         
		         if(is!=null)
		            is.close();
		         if(dis!=null)
		            dis.close();
		      }
		return sm;
	 }
	    
	
	
	public ArrayList<PairOfElectrodes> LoadPairOfElectrodes(String filePath, FileHeader fh) throws IOException
	{
		  InputStream is = null;
	      DataInputStream dis = null;
	      ArrayList<PairOfElectrodes> pairOfElectrodesList = new ArrayList<PairOfElectrodes>();
	      
	      try{
	    	  if(fh.pair_of_electrodes_block_offset != 0)
	    	  {
	    		  is = new FileInputStream(filePath);         
			      dis = new DataInputStream(is);
			      dis.skipBytes(fh.pair_of_electrodes_block_offset);
			      PairOfElectrodesFormat poe_form = new PairOfElectrodesFormat(); 
	    		  for(int i=0; i<fh.nPairOfElectrodes; i++)
	    		  {
		    		  PairOfElectrodes poe = new PairOfElectrodes();
		    		  dis.readFully(poe_form.excitation_electrodes_mask);
		    		  poe.excitation_electrodes_mask = bytesToInt(poe_form.excitation_electrodes_mask);
		    		  dis.readFully(poe_form.measurement_electrode_number);
		    		  poe.measurement_electrode_number = bytesToShort(poe_form.measurement_electrode_number);
		    		  dis.readFully(poe_form.el_width);
		    		  poe.el_width = bytesToFloat(poe_form.el_width);
		    		  dis.readFully(poe_form.el_height);
		    		  poe.el_height = bytesToFloat(poe_form.el_height);
		    		  dis.readFully(poe_form.readout_number);
		    		  poe.readout_number = bytesToShort(poe_form.readout_number);
		    		  dis.readFully(poe_form.channel_number);
		    		  poe.channel_number = bytesToShort(poe_form.channel_number);
		    		  dis.readFully(poe_form.amplification);
		    		  poe.amplification = bytesToFloat(poe_form.amplification);
		    		  dis.readFully(poe_form.offset);
		    		  poe.offset = bytesToFloat(poe_form.offset);
		    		  dis.readFully(poe_form.analogue_switches_settings);
		    		  poe.analogue_switches_settings = bytesToShort(poe_form.analogue_switches_settings);
		    		  dis.readFully(poe_form.op_amp_settings);
		    		  poe.op_amp_settings = bytesToShort(poe_form.op_amp_settings);
		    		  dis.readFully(poe_form.min);
		    		  poe.min = bytesToDouble(poe_form.min);
		    		  dis.readFully(poe_form.max);
		    		  poe.max = bytesToDouble(poe_form.max);
		    		  dis.readFully(poe_form._dummy);
		    		  pairOfElectrodesList.add(poe);
	    		  }		  
	    		  CopyMinMax(pairOfElectrodesList, fh);
	    		  return pairOfElectrodesList;
	    	  }
	      }catch(Exception e){
		         e.printStackTrace();
		      }finally{
		         
		         if(is!=null)
		            is.close();
		         if(dis!=null)
		            dis.close();
		      }
		return null;
	 }
	
	public ArrayList<Frame> LoadFrame(String filePath, FileHeader fh) throws IOException
	{
		  InputStream is = null;
	      DataInputStream dis = null;
	      ArrayList<Frame> frameList = new ArrayList<Frame>();
	      
	      try{
	    	  if(fh.data_block_offset != 0)
	    	  {
		    	  is = new FileInputStream(filePath);         
			      dis = new DataInputStream(is);
			      dis.skipBytes(fh.data_block_offset);
			      byte[] FrameLength = new byte[fh.nPairOfElectrodes*2];
			      for (int i = 0; i < fh.nFrames; i++)
			      {
			    	  Frame f = new Frame();
			    	  FrameFormat f_form = new FrameFormat();
		    	  
			    	  int[] intArray = new int[fh.nPairOfElectrodes*2/2];
			    	  dis.readFully(f_form.time);
			    	  f.time = bytesToInt(f_form.time);
			    	  dis.readFully(FrameLength);
			    	  for(int j = 0; j< (fh.nPairOfElectrodes*2)/2; j++)
			    	  {			
			    		  byte[] frameData = new byte[2];
			    		  frameData[0] = FrameLength[2*j];
			    		  frameData[1] = FrameLength[2*j+1];
			    		  intArray[j] = bytesToShort(frameData) & 0xffff;	
			    	  }
			    	  f.frameData = intArray;    	  
			    	  frameList.add(f);
			      }
			      
	    	  }
	      }catch(Exception e){
		         e.printStackTrace();
		      }finally{
		         
		         if(is!=null)
		            is.close();
		         if(dis!=null)
		            dis.close();
		      }
		return frameList;
	 }
	public void CopyMinMax(ArrayList<PairOfElectrodes> poe, FileHeader fh)     // exportuje dane do buforów min i max
	{	 
		 float[] Cmin = new float[fh.nPairOfElectrodes];
		 float[] Cmax = new float[fh.nPairOfElectrodes];
	     for(int i=0; i<poe.size(); i++)
	     {
	         Cmin[i] = (float)(poe.get(i).min);
	         Cmax[i] = (float)(poe.get(i).max);
         } 
	     fh.Cmin = Cmin;
	     fh.Cmax = Cmax;
	      return;
	}
	
	public float bytesToFloat(byte[] by)
	{
	return ByteBuffer.wrap(by).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	public int bytesToInt(byte[] by)
	{
	return ByteBuffer.wrap(by).order(ByteOrder.LITTLE_ENDIAN).getInt();	
	}
	public short bytesToShort(byte[] by)
	{	
	 return ByteBuffer.wrap(by).order(ByteOrder.LITTLE_ENDIAN).getShort();	
	}
	public double bytesToDouble(byte[] by)
	{	
	 return ByteBuffer.wrap(by).order(ByteOrder.LITTLE_ENDIAN).getDouble();	
	}
	
	public String bytesToString(byte[] by)
	{
		 String s ="";
         for(byte b: by)
         {
        	 if(b != 0)
        	 {
        	 char c = (char)(b & 0xFF) ;
        	 s = s + c;
        	 }
        	 
         }
         return s;
	}

	

	
	
}
