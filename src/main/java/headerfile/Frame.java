package headerfile;

public class Frame {
	
	public int[] frameData;
	public int time;              // czas pomiaru ramki [us] (DWORD)
	public short frame;          // liczba pomiarow pojemnosci
										// jesli 2D: (((n-1)*n)/repetition)*nRings , gdzie n to liczba elektrod w pierscieniu
										// jesli 3D: (((n-1)*n)/repetition), gdzie n to liczba wszystkich elektrod

}
