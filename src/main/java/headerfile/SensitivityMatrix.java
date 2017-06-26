package headerfile;

public class SensitivityMatrix {
	
	//Sensitivity Matrix
	
	public short size; //rozmiar naglowka bloku
	public short img_size_x;
	public short img_size_y;
	public short rows;
	public short columns;
	public short precision;		// 4 bytes if float
	public float[][] S;

}
