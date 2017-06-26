package headerfile;

import java.util.ArrayList;

public class PairOfElectrodes {
	
	public int excitation_electrodes_mask; //bitowo zaznaczone elektrody wymuszaj¹ce. Czy zrobic z tego tablice? chodzi nam tylko o elektrody wymuszajace
	public short measurement_electrode_number;
	public float el_width, el_height; //szerokosc i wysokosc elektrody pomiarowej
	public short readout_number;
	public short channel_number;
	public float amplification; //wzmocnienie jako K
	public float offset; //przesuniêcie zera
	public short analogue_switches_settings;
	public short op_amp_settings;
	public double min, max;		// wartoœci kalibracyjne z kana³u (pary elektrod)
	public short[] _dummy = new short[16];
	
	public static ArrayList<PairOfElectrodes> poe_list;

}
