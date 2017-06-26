package communication;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import org.jfree.io.IOUtils;

import evt4dataprocessor.EVT4DataProcessor;
import view.ConnPanel;

public class UDPConnection
{
	
	int bBegin, bEnd, buffSize, bN, rMarker;
	boolean runReceive, runProcess, onlineProcessing;
	
	private Thread receiveThread, processThread;
	private Semaphore receiveSemaphore, processSemaphore;
	UDPItem[] buf;
	DatagramPacket UDPPacket;
	
	DatagramSocket PCSocket;
	
	EVT4DataProcessor dataProc;
	
	ConnPanel connPane;
	
	public UDPConnection (int sizeX16kb, ConnPanel cPane) throws IOException
	{
		connPane = cPane;
			
		PCSocket = new DatagramSocket();
		PCSocket.setReceiveBufferSize(10485760);
		PCSocket.setSoTimeout(5000); // 5 seconds
			
		buffSize = sizeX16kb;		
		InitBuffer();
		UDPPacket = new DatagramPacket(buf[bEnd].data, Config.UDP_LENGTH);
		CreateThreads();
		if(connPane.getIfOnlineProcessing())
		{
			receiveSemaphore = new Semaphore(buffSize,true);
			processSemaphore = new Semaphore(0,true);
		}
		byte[] startBuffer = new byte[512];
			
		DatagramPacket UDPStartPacket = new DatagramPacket(startBuffer,0, startBuffer.length, Config.IP_ADDRESS, Config.UDP_PORT);
		PCSocket.send(UDPStartPacket);
	}
	
	public void InitBuffer()
	{
		bEnd = 0;
		rMarker = 0;
		buf = new UDPItem[buffSize];
		for (int i = 0; i < buffSize; i++)
		{
		    buf[i] = new UDPItem();
		    buf[i].data = new byte[Config.UDP_LENGTH];
		}
	}
	
	public void CreateThreads()
	{
		runReceive = true;
		runProcess = true;
		receiveThread = new Thread(new Runnable(){
			public void run()
			{
				ReceiveData();
			}
		});
		
		processThread = new Thread(new Runnable(){
			public void run()
			{
				ProcessData();
			}
		});
	}
	
	public void StartWithOnlineProcessing()
	{
		if(runReceive == false && runProcess == false)
		{
			CreateThreads();
			InitBuffer();
		}	
		receiveThread.start();
		processThread.start();
		onlineProcessing = true;
	}
	
	public void StartWithoutOnlineProcessing()
	{
		if(runReceive == false && runProcess == false)
		{
			CreateThreads();
			InitBuffer();
		}	
		receiveThread.start();
		onlineProcessing = false;
	}
	
	public void ReceiveData()
	{
		
		while(runReceive)
		{
			try 
			{
				if(connPane.getIfOnlineProcessing())
				{
					receiveSemaphore.acquire();
					PCSocket.receive(UDPPacket);
					bEnd = (bEnd + 1) % buffSize;
					UDPPacket.setData(buf[bEnd].data);
					processSemaphore.release();
				}
				else
				{
					PCSocket.receive(UDPPacket);
					boolean k = false;
					if(k = false)
					{
						System.out.println("cos odebralo");
						k = true;
					}
					bEnd = (bEnd + 1) % buffSize;
					UDPPacket.setData(buf[bEnd].data);
				}
			} 
			catch (SocketTimeoutException e) 
			{
				System.out.println(bEnd +" " + rMarker);
				stopReceiveThread();
				if(connPane.getIfOnlineProcessing())processSemaphore.release();
				connPane.receivingFinished();
				System.out.println("Receive thread timed out");
				if(processThread != null)System.out.println(runProcess);
				
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	public void ProcessData()
	{      
		dataProc = new EVT4DataProcessor();
		while(runProcess)
		{
			if(connPane.getIfOnlineProcessing())
			{
				try 
				{
					processSemaphore.acquire();
					if(rMarker == bEnd && runReceive == false)
					{
						System.out.println("Processing Thread Finished");
						connPane.processingFinished();
						stopProcessThread();
					}				
					dataProc.Process(buf[rMarker].data, buf[rMarker].data.length);
					rMarker = (rMarker + 1) % buffSize;
					receiveSemaphore.release();
					
					
				} 
				catch (InterruptedException e) {

					e.printStackTrace();
				}			
			}
			else
			{				
				dataProc.Process(buf[rMarker].data, buf[rMarker].data.length);
				rMarker = (rMarker + 1) % buffSize;
				
				if(rMarker == bEnd && runReceive == false)
				{
					connPane.processingFinished();
					stopProcessThread();
				}
			}
		}
	}
	
	public void closeConnection()
	{
		stopThreads();
		if(PCSocket != null) PCSocket.close();		
	}
	
	public void stopThreads()
	{
		stopReceiveThread();
		stopProcessThread();
	}
	
	public void saveRawDataToFiles(String fileName)
	{
		stopReceiveThread();
		FileOutputStream fos;
		try 
		{
			fos = new FileOutputStream(fileName);
			for(int i = 0; i < buffSize; i++)
			{
				fos.write(buf[i].data);				
			}
			fos.close();
		} 
		catch (IOException e) {
			System.out.println(e);
		}
		
	}
	
	public void stopReceiveThread()
	{
	    runReceive = false;
	}
	
	public void stopProcessThread()
	{
	    runProcess = false;
	}
	
	public Thread getReceiveThread()
	{
		return receiveThread;
	}
	
	public Thread getProcessThread()
	{
		return processThread;
	}
	
	public EVT4DataProcessor getDataProc()
	{
		return dataProc;
	}

}
