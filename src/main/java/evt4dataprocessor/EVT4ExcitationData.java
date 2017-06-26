package evt4dataprocessor;

import java.util.ArrayList;

public class EVT4ExcitationData {
	
	boolean[] channels = new boolean[32];
	public ArrayList<EVT4ChannelData> channelsData = new ArrayList<EVT4ChannelData>();
	
	EVT4ExcitationData()
	{
		for (int i = 0; i < 32; ++i)
		{
			channels[i] = false;
		}
	}
	
	boolean checkIfChannelsDataExists(int readout_num)
	{
		return channels[readout_num*4];
	}
	
	void setChannelsDataFlags(int readout_number) {
		channels[readout_number * 4 + 0] = true;
		channels[readout_number * 4 + 1] = true;
		channels[readout_number * 4 + 2] = true;
		channels[readout_number * 4 + 3] = true;
	}

}
