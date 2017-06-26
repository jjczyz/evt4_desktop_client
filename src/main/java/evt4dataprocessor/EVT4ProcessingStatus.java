package evt4dataprocessor;

public class EVT4ProcessingStatus {
	
	int readout_wordlen;
	short num_of_readouts;
	int samples_counter;
	int readout_data_counter;
	int excitation_counter;
	int[] electrodes_config_prev_vect = new int[8];
	int electrodes_config;
	int crc;
	int readout_number;
	int[] prev_word = new int[8];
	State state;
	State[] state_to_be_continued = new State[8];
	int exidx;
	int[] image_idxs = new int[8];
	boolean exception;
	boolean[] searchImageStart = new boolean[8];

}
