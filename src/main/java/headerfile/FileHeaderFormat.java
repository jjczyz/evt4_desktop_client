package headerfile;

public class FileHeaderFormat {

	byte[] signature = new byte[8];  // znacznik pliku
	byte[] size = new byte[2];         // rozmiar naglowka
									  //### parametry administracyjne
	byte[]	date = new byte[10];		// data w formacie dd.mm.yyyy
	byte[]	time = new byte[8];		// czas w formacie hh.mm.ss
	byte[]	name = new byte[64];		//name
	byte[]	comment = new byte [512];	//komentarz

	//### sonda tomograficzna
	byte[]	sensor_name = new byte[32];	//nazwa czujnika
	byte[]	sensor_type = new byte[2];		//2D, 3D
	byte[]	sensor_shape = new byte[4];	//cyli, cubo
	byte[]  nElectrodes = new byte[2];       //short calkowita liczba elektrod
	byte[] 	nRings = new byte[2];			//short liczba pierscieni 2D
	byte[]	nElectrodesInRing = new byte[2]; //short liczba elektrod w pierscieniu
	byte[]  d1 = new byte[4];	//float dlugosc,
	byte[]  d2 = new byte[4];	//float szerokosc,
	byte[]  h = new byte[4];	//float wysokosc(w przypadku cylindra d1 = d2) [mm]
	byte[]	sensor_description = new byte[512];	// opis sondy
	byte[]	sensor_file_name = new byte[256];		//plik z definicja sondy, macierza wrazliwosci
	//offset bloku danych z macierz¹ wra¿liwoœci (od pocz¹tku pliku)
	byte[] sensitivity_matrix_block_offset = new byte[4]; //int  0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku

	//### pomiar pojemnosci
	byte[] repetition = new byte[2];			// short 1 - pomiary z powtorzeniami, 2 - pomiary bez powtorzen
	byte[] nPairOfElectrodes = new byte[2];           // short liczba pomiarów (par elektrod)
											// jesli 2D: (((n-1)*n)/repetition)*nRings , gdzie n to liczba elektrod w pierscieniu
											// jesli 3D: (((n-1)*n)/repetition), gdzie n to liczba wszystkich elektrod

	byte[] method = new byte[32];		//nazwa metody pomiarowej
	byte[] voltage = new byte[4];		//float napiecie pobudzenia[V]

	//offset bloku danych z parami elektrod (od pocz¹tku pliku). Tablica struktur EVT4PairOfElectrodes o rozmiarze nPairOfElectrodes
	byte[] pair_of_electrodes_block_offset = new byte[4]; //int  0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku

	//### kalibracja
	byte[] calibration_min = new byte[2]; //short  0 - nie ma kalibracji, 1 - kalibracja
	byte[] calibration_max = new byte[2]; //short  0 - nie ma kalibracji, 1 - kalibracja
	byte[]	permitivity_min = new byte[4]; //float minimalna wzgledna przenikalnosc elektryczna
	byte[]  permitivity_max = new byte[4]; //float maksymalna wzgledna przenikalnosc elektryczna

	//### typ pliku
	byte[] file_type = new byte[2]; //short typ pliku


	//### obrazy
	byte[] nFrames = new byte[4]; //int liczba probek czasowych   nTimeFrames ?
	byte[] interval = new byte[4]; //int probkowanie ramek[us]
	
	//offset bloku danych pomiarowych (od pocz¹tku pliku).
	byte[] data_block_offset = new byte[4]; //int  0 - jeœli bloku nie ma, w przeciwnym wypadku offset do bloku
}
