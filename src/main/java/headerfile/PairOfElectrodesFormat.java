package headerfile;

public class PairOfElectrodesFormat {
	
	byte[] excitation_electrodes_mask = new byte[4]; //int bitowo zaznaczone elektrody wymuszaj¹ce. Czy zrobic z tego tablice? chodzi nam tylko o elektrody wymuszajace
	byte[] measurement_electrode_number = new byte[2]; //short
	byte[] el_width = new byte[4];  //float szerokosc elektrody pomiarowej
	byte[] el_height = new byte[4]; //float wysokosc elektrody pomiarowej
	byte[] readout_number = new byte[2]; //short
	byte[] channel_number = new byte[2]; //short
	byte[] amplification = new byte[4]; //float wzmocnienie jako K
	byte[] offset = new byte[4]; //float przesuniêcie zera
	byte[] analogue_switches_settings = new byte[2]; //short
	byte[] op_amp_settings = new byte[2]; //short
	byte[] min = new byte[8];
	byte[] max = new byte[8];		//double wartoœci kalibracyjne z kana³u (pary elektrod)
	byte[] _dummy = new byte[32];  //16 shortów

}
