package evt4dataprocessor;

public class EVT4Header {
	
	int marker;
	short header_bytelen;
	int frame_bytelen;
	int jiffies;
	short crc;
	short rxstatus;
	short frame_counter;
	short[] reserved;
}
