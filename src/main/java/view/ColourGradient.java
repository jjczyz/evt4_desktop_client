package view;


public class ColourGradient 
{
	
	class RGBColour 
	{
		int red;
		int green;
		int blue;
	}

	RGBColour[] Gradient = new RGBColour[256];
	
	ColourGradient()
	{
		/*
		 * blue = 0,0,255
		 * green = 0,255,0
		 * yellow = 255,255,0
		 * red = 255, 0, 0
		 */
		
		for(int i = 0; i < 5; i++)
		{
			Gradient[i] = new RGBColour();
			Gradient[i].red = 0;
			Gradient[i].green = 0;
			Gradient[i].blue =  40;
		}
		
		for(int i = 5; i < Gradient.length/3; i++)
		{
			float v = ((float)i - 5)/((Gradient.length/3)-5);
			Gradient[i] = new RGBColour();
			Gradient[i].red = (int)0;
			Gradient[i].green = (int) (v*255);
			Gradient[i].blue = (int) (40+(1-v)*(255-40));
		}
		
		for(int i = Gradient.length/3; i < 2*Gradient.length/3; i++)
		{
			float v = ((float)i - Gradient.length/3)/(2*Gradient.length/3 - Gradient.length/3);
			Gradient[i] = new RGBColour();
			Gradient[i].red = (int) (v*255);
			Gradient[i].green = 255;
			Gradient[i].blue = (int) (128-v*(255-128));
		}
		for(int i = 2*Gradient.length/3; i < Gradient.length-15; i++)
		{
			float v = ((float)i - 2*Gradient.length/3)/(Gradient.length-15 - 2*Gradient.length/3);
			Gradient[i] = new RGBColour();
			Gradient[i].red = 255;
			Gradient[i].green = (int) ((1-v)*255);
			Gradient[i].blue = 0;
		}
		
		
		for(int i = Gradient.length-15; i < Gradient.length; i++)
		{
			float v = ((float)i - Gradient.length-15)/(Gradient.length - (Gradient.length-15));
			Gradient[i] = new RGBColour();
			Gradient[i].red = 255;
			Gradient[i].green = (int) (v*255);
			Gradient[i].blue = (int) (v*255);
		}
	}
}
