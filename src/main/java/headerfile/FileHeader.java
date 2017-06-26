package headerfile;

import java.util.ArrayList;

public class FileHeader {
	
	//FileHeader
	
		public String signature;  // znacznik pliku
		public String size;         // rozmiar naglowka
										  //### parametry administracyjne
		public String	date;		// data w formacie dd.mm.yyyy
		public String	time;		// czas w formacie hh.mm.ss
		public String	name;		//name
		public String	comment;	//komentarz

		//### sonda tomograficzna
		public String	sensor_name;	//nazwa czujnika
		public String	sensor_type;		//2D, 3D
		public String	sensor_shape;	//cyli, cubo
		public short  nElectrodes;       // calkowita liczba elektrod
		public short 	nRings;			// liczba pierscieni 2D
		public short	nElectrodesInRing; // liczba elektrod w pierscieniu
		public float  d1;	// dlugosc,
		public float  d2;	// szerokosc,
		public float  h;	// wysokosc(w przypadku cylindra d1 = d2) [mm]
		public String	sensor_description;	// opis sondy
		public String	sensor_file_name;		//plik z definicja sondy, macierza wrazliwosci
		//offset bloku danych z macierz¹ wra¿liwoœci (od pocz¹tku pliku)
		public int sensitivity_matrix_block_offset; //int  0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku

		//### pomiar pojemnosci
		public short repetition;			//  1 - pomiary z powtorzeniami, 2 - pomiary bez powtorzen
		public short nPairOfElectrodes;           //  liczba pomiarów (par elektrod)
												// jesli 2D: (((n-1)*n)/repetition)*nRings , gdzie n to liczba elektrod w pierscieniu
												// jesli 3D: (((n-1)*n)/repetition), gdzie n to liczba wszystkich elektrod
		public String method;		//nazwa metody pomiarowej
		public float voltage;		// napiecie pobudzenia[V]

		//offset bloku danych z parami elektrod (od pocz¹tku pliku). Tablica struktur EVT4PairOfElectrodes o rozmiarze nPairOfElectrodes
		public int pair_of_electrodes_block_offset; //  0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku

		//### kalibracja
		public short calibration_min; //  0 - nie ma kalibracji, 1 - kalibracja
		public short calibration_max; //  0 - nie ma kalibracji, 1 - kalibracja
		public float	permitivity_min; // minimalna wzgledna przenikalnosc elektryczna
		public float   permitivity_max; // maksymalna wzgledna przenikalnosc elektryczna

		//### typ pliku
		public short file_type; //short typ pliku

		//### obrazy
		public int nFrames; //int liczba probek czasowych   nTimeFrames ?
		public int interval; //int probkowanie ramek[us]
		
		//offset bloku danych pomiarowych (od pocz¹tku pliku).
		public int data_block_offset; //0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku
		
		public SensitivityMatrix sm;
		public ArrayList<PairOfElectrodes> poe_list;
		public ArrayList<Frame> frameList;
		public float[] Cmin;
		public float[] Cmax;
		
		
		

}
