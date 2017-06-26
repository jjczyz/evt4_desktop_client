package headerfile;

public class FrameFormat {
	
	byte[] time = new byte[4];              //int czas pomiaru ramki [us] (DWORD)
	byte[] frame = new byte[2];          //short  liczba pomiarow pojemnosci
										// jesli 2D: (((n-1)*n)/repetition)*nRings , gdzie n to liczba elektrod w pierscieniu
										// jesli 3D: (((n-1)*n)/repetition), gdzie n to liczba wszystkich elektrod

}
