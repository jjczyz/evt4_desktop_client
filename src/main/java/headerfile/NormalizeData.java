package headerfile;

import java.util.ArrayList;

import com.aparapi.Kernel;

public class NormalizeData {
	
	public float[]   S;                  // macierz wra¿liwoœci
    public float[]   Sn;            // macierz wra¿liwoœci znormalizowana
    public int[]   Sn2;            // macierz wra¿liwoœci znormalizowana, wartoœci 0-255
    public int[]   Sn3;			// macierz nieznormalizowana, wartosci 0-255
    
    public float  smin, smax, savg, ssdev;    // statystyka w mapie wra¿liwoœci
    
    public float  C[];                  // pomiary nieunormowane - dane z przetwornika
    public float  Cn[];          // pomiary unormowane do zakresu <0; 1>
    public float  Cmin[];        // pomiar kalibracyjny sonda pusta - pomiary nieunormowane - dane z przetwornika
    public float  Cmax[];        // pomiar kalibracyjny sonda pe³na - pomiary nieunormowane - dane z przetwornika
    
    public int    NPairs;              // liczba pomiarów pojemnoœci
    public int    ImageSize;    // rozmiar matrycy rekonstruowanego obrazu (ImageSize x ImageSize = 32 x 32)
    public int    NPixels;      // liczba pikseli = 1024
    
    public float  EpsMin;              // wzglêdna przenikalnoœæ materia³u - minimalna przenikalnoœæ (eps=1)
    public float  EpsMax;              // wzglêdna przenikalnoœæ materia³u - maksymalna przenikalnoœæ (eps=3)

    public float  fImage[];      // macierz obrazu - wartoœci z rekonstrukcji <EpsMin; EpsMax>
    public int 	  imageNorm[];
    public boolean[]   fov;              // maska pikseli nale¿¹cych do ko³a wpisanego w kwadrat - 1

    public short  Image[];            // macierz obrazu - indeksy palety kolorów <0;255>
    public float  min, max, avg, sdev;  // statystyka obrazu

    public int  Smap[];             // mapa wra¿liwosci - indeksy palety kolorów <0;255>
    
    public int  num_byte_read;       // ?
    
    public float[]  Se = new float[4096];     // wspó³czynniki dla algorytmu Landwebera
    public float[]  dC = new float[4096];
    
    AparapiReconstruction ap;

	 
    
	public NormalizeData(int imasize, int npairs)
	{
	       ImageSize = imasize;               // rozmiar matrycy rekonstruowanego obrazu
	       NPixels = ImageSize*ImageSize;     // liczba pikseli = 1024
	       NPairs = npairs;                          // liczba pomiarów pojemnoœci
	 
	       S = new float[NPairs*NPixels];            // macierz wra¿liwoœci
	       Sn = new float[NPairs*NPixels];              // macierz wra¿liwoœci znormalizowana	 
	       Sn2 = new int[NPairs*NPixels];              // macierz wra¿liwoœci znormalizowana	2
	       Sn3 = new int[NPairs*NPixels]; 
	       
	       C = new float[NPairs];			// pomiary nieunormowane - dane z przetwornika
	       Cn = new float[NPairs];			// pomiary unormowane do zakresu <0; 1>
	       Cmin = new float[NPairs];		// pomiar kalibracyjny sonda pusta - pomiary nieunormowane - dane z przetwornika
	       Cmax = new float[NPairs];		// pomiar kalibracyjny sonda pe³na - pomiary nieunormowane - dane z przetwornika 
	       
	       fImage = new float[NPixels];
	       imageNorm = new int[NPixels];
	}
	

	void NormalizeFrameData(FileHeader fh, int[] C)
	{
		double denom;
		for (int i=0; i<fh.nPairOfElectrodes; i++)
		{       denom = fh.poe_list.get(i).max - fh.poe_list.get(i).min;
		        Cn[i] = (int) (denom>8 ? (C[i] - fh.poe_list.get(i).min)/denom : 0);
		}
		return;
	}
	
	float[] NormalizeFrameData(FileHeader fh, float[] C)
	{
		double denom;
		float[] Cn = new float[fh.nPairOfElectrodes];
		for (int i=0; i<fh.nPairOfElectrodes; i++)
		{       denom = fh.poe_list.get(i).max - fh.poe_list.get(i).min;
		        Cn[i] = (float) (denom>8 ? (C[i] - fh.poe_list.get(i).min)/denom : 0);
		}
		return Cn;
	}
	
	public void CopySensMatrix(FileHeader fh){
		for(int i = 0; i< fh.sm.rows; i++)
		{
			for(int j=0; j< fh.sm.columns; j++) 
			{
				S[i*fh.sm.columns + j] = fh.sm.S[i][j];
			}			
		}
	}
	
	public void CopyMinMax(FileHeader fh)
	{
		for(int i=0; i<fh.nPairOfElectrodes; i++)
		{
			Cmin[i] = (float)(fh.poe_list.get(i).min);
			Cmax[i] = (float)(fh.poe_list.get(i).max);
		}
		
		EpsMin = fh.permitivity_min;
		EpsMax = fh.permitivity_max;
	}
	
	public void NormalizeMap()
	{
		int pix, pair;
		for(pix=0; pix<NPixels; pix++)
	       {      float sum=0;
	              // sum over all measurements (pair of electrodes) for given pixel
	              for (pair=0; pair<NPairs; pair++)  sum+=S[pair*NPixels+pix];
	              // sum==0   !=  all_coefficients==0
	              if (sum!=0)  
	            	  {
		            	  for (pair=0; pair<NPairs; pair++)
		            	  {
		            		  Sn[pair*NPixels+pix] = (S[pair*NPixels+pix]/sum);
		            	  }
	            	  }
	              else
	              {
	            	  for (pair=0; pair<NPairs; pair++)
	            	  {
	            		  Sn[pair*NPixels+pix] = 0;
	            	  }
	              }
	       }
		ap = new AparapiReconstruction();
	       return;
	}
	
	
	
	public void  NormalizeMap2()
	{
		int pix, pair;
	    for (pair=0; pair<NPairs; pair++)
	    {
	    	float max = 0;
	    	float min = 0;
	    	for (pix=0; pix<NPixels; pix++)   
	    	{
		    	if (Sn[pair*NPixels+pix] > max)
		        {
		    	     max = Sn[pair*NPixels+pix];
		    	}
		    	if (Sn[pair*NPixels+pix] < min)
		        {
		    	     min = Sn[pair*NPixels+pix];
		        }
	    	}
	    	float denom = max - min;
	    	for (pix=0; pix<NPixels; pix++)   
    		{
	    		Sn2[pair*NPixels+pix] = (int) (((Sn[pair*NPixels+pix] - min)/denom)*255) ;
    		}	    	
	    }      
	    return;
	}
	
	public void  NormalizeMap3()
	{
		int pix, pair;   
	    for (pair=0; pair<NPairs; pair++)
	    {
	    	int sum = 0;
		    float average;
		    float max = 0;
	    	for (pix=0; pix<NPixels; pix++)   
	    	{
	    		sum = sum + Sn2[pair*NPixels+pix];
	    		if (Sn2[pair*NPixels+pix] > max)
		        {
		    	     max = Sn2[pair*NPixels+pix];
		    	}
	    	}
	    	average = 1.0f * sum/NPixels;
	    	float denom = max - average;
	    	
	    	for (pix=0; pix<NPixels; pix++)   
    		{
	    		Sn2[pair*NPixels+pix] = (int) ((((float)Sn2[pair*NPixels+pix] - average)/denom)*255) ;
	    		if(Sn2[pair*NPixels+pix]<60) Sn2[pair*NPixels+pix] =0;
	    		if(Sn2[pair*NPixels+pix]>245) Sn2[pair*NPixels+pix] =255;
	    		
    		}	

	    }      	    
	}
	
	public void  NormalizeMap4()
	{
		int pix, pair;
	    for (pair=0; pair<NPairs; pair++)
	    {
	    	float sum = 0;
	    	float max = 0;
	    	float min = 0;
	    	for (pix=0; pix<NPixels; pix++)   
	    	{
	    		sum = sum + S[pair*NPixels+pix];
		    	if (S[pair*NPixels+pix] > max)
		        {
		    	     max = S[pair*NPixels+pix];
		    	}
		    	if (S[pair*NPixels+pix] < min)
		        {
		    	     min = S[pair*NPixels+pix];
		        }
	    	}
	    	float denom = max - min;
	    	for (pix=0; pix<NPixels; pix++)   
    		{
	    		Sn3[pair*NPixels+pix] = (int) (((S[pair*NPixels+pix] - min)/denom)*255) ;
	    		if(Sn3[pair*NPixels+pix] < 0)
	    		{
	    			Sn3[pair*NPixels+pix] = 0;
	    		}
    		}	    	
	    }      
	    return;
	}
	
	public void ImportImageData(ArrayList<Float> src)	// kopiuj nieznormalizowane dane (jedna ramka) z tomografu EVT4 do C
	{
		for (int i = 0; i<NPairs; i++)
		{
			C[i] = src.get(i);
		}
	}
	
	public void NormalizeImageData()
	{
		 float denom;
		 for (int p=0; p<NPairs; p++)		 
		 {       denom = Cmax[p]-Cmin[p];
		         Cn[p] =  (C[p] - Cmin[p])/denom;
		 }
	}
	
	
	public void LBP ()
	{
		 float v; 
		 int pix, pair;
		 for (pix=0; pix<NPixels; pix++)
		 {		
			 v = 0;
				for ( pair=0; pair<NPairs; pair++)
		        {
		            v += Cn[pair]*Sn[pair*NPixels+pix];
		        }
		        fImage[pix] = (EpsMin + v * (EpsMax - EpsMin));
		        
		 }
	}
	
	
	public void NormalizeImage(int min, int max)
	{
		int pix, pair;
		for (pix=0; pix<NPixels; pix++)
		 {		
				imageNorm[pix] = (int)(((fImage[pix] - EpsMin)/(EpsMax-EpsMin))*255);
		        imageNorm[pix] = (int)((((float)imageNorm[pix] - min)/(max - min))*255);
		        if(imageNorm[pix] < 0) imageNorm[pix] =0;
		        if(imageNorm[pix] > 255) imageNorm[pix] =255;
		 }
	}
	
	void FovCircle()
	{
		int	i,j;
		float	x,y, rr = ImageSize/2*ImageSize/3;
		float  SIZ2_05 =(float)(ImageSize/2-0.5);
		fov = new boolean[ImageSize*ImageSize];
	
		for( j=0; j<ImageSize; j++)
		{
			for( i=0; i<ImageSize; i++)
		   	{	x = i-SIZ2_05; y = j-SIZ2_05;
		        	if( x*x+y*y <= rr)	fov[j*ImageSize+i] = true;
		            else				fov[j*ImageSize+i] = false;
		    }
		}
			return;
	}
	
	public void Landweber (int iter, double alpha)
	{
		 float v;
		 float e[] = fImage;
		 int i, pair, pix;
		 
		 // start solution = 0
		 for (pix=0, v = 0; pix<NPixels; pix++) e[pix] = 0;

		 long startTime = 0;
		 long firstTime = 0;
		 long secondTime = 0;
		 // Landweber: e = e + lambda*St*(C -S*e) 
		 for (i=0; i<iter; i++)
		 {
			 	startTime = System.currentTimeMillis();
				 // S*e
				 for ( pair=0; pair<NPairs; pair++)
				 {		for (pix=0, Se[pair] = 0; pix<NPixels; pix++)
						{		
							Se[pair] += e[pix]*Sn[pair*NPixels+pix];
						}
						dC[pair] = Cn[pair] - Se[pair];
				 }
				 firstTime += System.currentTimeMillis() - startTime;
				 
				 startTime = System.currentTimeMillis();
				 // e = e + St * dC
				 for (pix=0; pix<NPixels; pix++)
				 {
					    v = 0;
					    for ( pair=0; pair<NPairs; pair++)
						{
							v += dC[pair]*Sn[pair*NPixels+pix];
							//v += Sn[pair*NPixels+pix];
						}
						e[pix] += alpha*v;
						if (e[pix]<0) e[pix] = 0;
				 }
				 secondTime += System.currentTimeMillis() - startTime;
		 }
		 System.out.println(firstTime + " " + secondTime);

		 for (pix = 0; pix<NPixels; pix++)
			 fImage[pix] = EpsMin + e[pix] * (EpsMax - EpsMin);


		 return;
	}
	
	public void GPULandweber(int iter, double alpha)
	{		
		ap.Landweber(iter, alpha);
		return;
	}
	
	public class AparapiReconstruction extends Kernel
	{
		double relaxationParam;
		float e[];
		public float[] Se_gpu = new float[4096];  
		public float[] dC_gpu = new float[4096];
		public float[] Sn_gpu;
		int pixels;
		int pairs;
		@Override
		public void run() 
		{
			int pix = getGlobalId();
			int pair;
			float v = 0;
			for ( pair=0; pair<pairs; pair++)
			{
				v += dC_gpu[pair]*Sn_gpu[pair*pixels+pix];				
				//v += Sn[pair*NPixels+pix];
			}
			
			e[pix] += relaxationParam*v;
			if (e[pix]<0) e[pix] = 0;
		}
		
		AparapiReconstruction()
		{			
			Sn_gpu = Sn;
		}	
		public void Landweber(int iter, double alpha)
		{			 
			 e = fImage;
			 int i, pair, pix;
			 relaxationParam = alpha;
			 pixels = NPixels;
			 pairs = NPairs;
			 long startTime = 0;
			 long firstTime = 0;
			 long secondTime = 0;
			 
			 // start solution = 0
			 for (pix=0; pix<pixels; pix++) e[pix] = 0;


			 // Landweber: e = e + lambda*St*(C -S*e) 
			 for (i=0; i<iter; i++)
			 {
					 // S*e
				 	 startTime = System.currentTimeMillis();
					 for ( pair=0; pair<pairs; pair++)
					 {		for (pix=0, Se_gpu[pair] = 0; pix<pixels; pix++)
							{		
						 		Se_gpu[pair] += e[pix]*Sn_gpu[pair*pixels+pix];
							}
							dC_gpu[pair] = Cn[pair] - Se_gpu[pair];
					 }
					 // e = e + St * dC
					 firstTime += System.currentTimeMillis() - startTime;
					 startTime = System.currentTimeMillis();
					 this.execute(pixels);
					 secondTime += System.currentTimeMillis() - startTime;
					
			 }
			 System.out.println(firstTime + " " + secondTime);

			 for (pix = 0; pix<pixels; pix++)
				 fImage[pix] = EpsMin + e[pix] * (EpsMax - EpsMin);


			 return;
		}
	}
	
}
