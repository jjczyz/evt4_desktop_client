package communication;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.lang3.ArrayUtils;

public class TCPConnection {
	
	private Socket PCSocket;
	private OutputStream outputStream;
	private DataOutputStream dataOutputStream;
	
	public TCPConnection() throws IOException
	{
			PCSocket = new Socket(Config.IP_ADDRESS,Config.TCP_PORT);
	}
	
	public void SendCommand(String cmd)
	{
		try {
			outputStream = PCSocket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
			byte[] size = my_int_to_bb_le(cmd.length()+4);

			byte[] cmdToSend = ArrayUtils.addAll(size,cmd.getBytes());
			dataOutputStream.write(cmdToSend);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public void closeConnection()
	{
		try {
			if(outputStream != null) outputStream.close();
			if(PCSocket != null) PCSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static  byte[] my_int_to_bb_le(int myInteger){
	    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myInteger).array();
	}


}
