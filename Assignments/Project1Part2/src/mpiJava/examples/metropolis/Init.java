import mpi.*;
import java.io.*; 
/*--------------------------------------------------------------------
                                                                      
  Read in the parameters for the simulation.                         
                                                                      
---------------------------------------------------------------------*/
public class Init { 
  
  static void input(CommVars cv) throws MPIException {
    double inputdata[] = new double[8];;


    cv.procnum = MPI.COMM_WORLD.Rank();
    cv.nprocs  = MPI.COMM_WORLD.Size();

    cv.procdim[0] = 0;  cv.procdim[1] = 0;
    Cartcomm.Dims_create(cv.nprocs, cv.procdim);


    if( cv.procnum == 0) {

      String input[] = new String[8];
      DataInputStream in = new DataInputStream(System.in);
      
      try { 
	System.out.println(" Length of the lattice?");
	//input[0] = in.readLine();
	inputdata[0] = Double.valueOf(in.readLine()).doubleValue();
      
	System.out.println(" Number of thermalization sweeps?");
	//input[1] = in.readLine();	
      	inputdata[1] = Double.valueOf(in.readLine()).doubleValue();

	System.out.println(" Number of sweeps per measurement?");
	//input[2] = in.readLine();
	inputdata[2] = Double.valueOf(in.readLine()).doubleValue();
      
	System.out.println(" Bin size?");
	//input[3] = in.readLine();
	inputdata[3] = Double.valueOf(in.readLine()).doubleValue();
      
	System.out.println(" Number of bins?");
	//input[4] = in.readLine();
	inputdata[4] = Double.valueOf(in.readLine()).doubleValue();

	System.out.println(" Beta (inverse temperature)?");
	//input[5] = in.readLine();
	inputdata[5] = Double.valueOf(in.readLine()).doubleValue();
      
	System.out.println(" Ordered (0) or random (1) start?");
	//input[6] = in.readLine();
	inputdata[6] = Double.valueOf(in.readLine()).doubleValue();
      
	System.out.println(" Seed for the random number generator?");
	//input[7] = in.readLine();      
	inputdata[7] = Double.valueOf(in.readLine()).doubleValue();
      } 
      catch (IOException e) {
	System.out.println(e);
	System.out.println("An input error was caught");
      }

    }

	
    MPI.COMM_WORLD.Bcast(inputdata, 0, 8, MPI.DOUBLE, 0);
    
    
    cv.globside  = (int)inputdata[0];
    cv.globisiz  = cv.globside;
    cv.globjsiz  = cv.globside;
    cv.globsites = cv.globside * cv.globside;
    cv.volume    = (float)cv.globsites;
    
    cv.therm_sweeps = (int)inputdata[1];
    cv.sweeps       = (int)inputdata[2];
    cv.bin_size     = (int)inputdata[3];
    cv.num_bins     = (int)inputdata[4];
    cv.measurements = cv.bin_size * cv.num_bins;
    
    cv.beta  = (float)inputdata[5];
    cv.start = (int)inputdata[6];
    cv.seed  = (long)inputdata[7];
    
    
    
    if(cv.procnum == 0) {
      System.out.println("\n\n\n ------ Input Data ------"); 

      System.out.println(" Number of processors : "+cv.nprocs); 
      
      System.out.println(" Length of the lattice : "+cv.globside); 
      
      System.out.println(" Volume = "+cv.globsites); 
      
      System.out.println
	(" Number of thermalization sweeps : "+cv.therm_sweeps); 
      
      System.out.println(" Number of sweeps per measurement : "+cv.sweeps); 
      
      System.out.println(" Bin size : "+cv.bin_size); 
      
      System.out.println(" Number of bins? : "+cv.num_bins); 
      
      System.out.println
	(" Number of measurements = bin size x number of bins :"+cv.measurements); 
      
      System.out.println(" Beta (inverse temperature) : "+cv.beta); 
      
      System.out.println(" Ordered (0) or random (1) start? : "+cv.start); 
      
      System.out.println(" Seed for the random number generator? : "+cv.seed); 
    }
   

  }
 
  

 
  /*---------------------------------------------------------------------
    
    Initialize the spins with an ordered or random start.               
    
    ---------------------------------------------------------------------*/

  static void init_spins(CommVars cv) throws MPIException {
    
    int   i,j;
    
    
    if (cv.start == 0) { 
      // Ordered (cold) start 
      if(cv.procnum == 0) 
	System.out.println(" Cold start\n");
      
      for (i=1; i<=cv.isiz; i++)
	for (j=1; j<=cv.jsiz; j++){
	  cv.spin[i][j] = 1; 
	}
    }
    else {
      // Random (hot) start 
      if(cv.procnum == 0) 
	System.out.println(" Hot start-- \n"+cv.start);
      
      for (i=1; i<=cv.isiz; i++) {
	for (j=1; j<=cv.jsiz; j++) {
	  if (Prand.rand() < 0.5) 
	    cv.spin[i][j] = 1;
	  else
	    cv.spin[i][j] = -1;
	  System.out.print(cv.spin[i][j]+" "); 
	}
	  System.out.println(); 
      }
    }

    // Pass edges to neighboring processors.     
    Edges.spin_edges(cv);
    
    
  }


}

