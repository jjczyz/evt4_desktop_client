package evt4dataprocessor;

import java.util.ArrayList;

public class EVT4ImageData {
	
	ArrayList<Integer> excitations = new ArrayList<Integer>();
	short size;	
	public ArrayList<EVT4ExcitationData> excitationData = new ArrayList<EVT4ExcitationData>();
	
	short getIndexOfExcitation(int excitation)
	{
		for (int i = 0; i < excitations.size(); ++i) {
			if (excitations.get(i) == excitation)
				return (short)i;
		}
		return -1;
	}
}
