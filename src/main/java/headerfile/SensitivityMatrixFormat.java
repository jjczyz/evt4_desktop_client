package headerfile;

public class SensitivityMatrixFormat {
	
	byte[] size = new byte[2]; //short rozmiar naglowka bloku
	byte[] img_size_x= new byte[2]; //short
	byte[] img_size_y= new byte[2]; //short
	byte[] rows= new byte[2]; //short
	byte[] columns= new byte[2]; //short
	byte[] precision= new byte[2];	// short, 4 bytes if float

}
