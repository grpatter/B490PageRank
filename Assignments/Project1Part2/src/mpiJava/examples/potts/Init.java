import mpi.*;
import java.io.*; 
/*--------------------------------------------------------------------
                                                                      
  Read in the parameters for the simulation.                         
                                                                      
---------------------------------------------------------------------*/
public class Init { 
  
  static void met_mpi() throws MPIException {

    CommVars.procnum = MPI.COMM_WORLD.Rank();
    CommVars.nprocs  = MPI.COMM_WORLD.Size();

    CommVars.procdim[0] = 0;  CommVars.procdim[1] = 0;
    Cartcomm.Dims_create(CommVars.nprocs, CommVars.procdim);
  }


  static void input() throws MPIException {
    double inputdata[] = new double[12];;

    if( CommVars.procnum == 0) {
      /*
  static Label nprocsLabel;    static Choice nprocsChoice;
  static Label statesLabel;    static Choice statesChoice;
  static Label lsizeLabel;     static Choice lsizeChoice;
  static Label tsweepsLabel;   static TextField tsweepsTxt;
  static public Label msweepsLabel;   static public TextField msweepsTxt;    
  static Label binsizeLabel;   static TextField binsizeTxt;  
  static Label numbinLabel;    static TextField numbinTxt;  
  static Label betaLabel;      static Choice betaChoice;
  static Label orderLabel;     static Choice orderChoice;
  static Label rseedLabel;     static TextField rseedTxt;
  */
      inputdata[0] = Double.valueOf
	(MetroInputs.lsizeChoice.getSelectedItem()).doubleValue();
      inputdata[1] = 0;
      inputdata[2] = Double.valueOf
	(MetroInputs.msweepsTxt.getText()).doubleValue();
      inputdata[3] = 1;
      inputdata[4] = Double.valueOf
	(MetroInputs.measureTxt.getText()).doubleValue() / inputdata[3];
      /*  bin size is fixed to 2 for just demo
      inputdata[3] = Double.valueOf
	(MetroInputs.binsizeTxt.getText()).doubleValue();
      inputdata[4] = Double.valueOf
	(MetroInputs.numbinTxt.getText()).doubleValue() / inputdata[3];
      */
      inputdata[5] = Double.valueOf
	(MetroInputs.betaTxt.getText()).doubleValue();
      inputdata[6] = (double) MetroInputs.orderChoice.getSelectedIndex();
      inputdata[7] = Double.valueOf
	(MetroInputs.rseedTxt.getText()).doubleValue();
      inputdata[8] = Double.valueOf
	(MetroInputs.statesChoice.getSelectedItem()).doubleValue();
      inputdata[9] = (double) MetroInputs.updateChoice.getSelectedIndex();
      inputdata[10] = Double.valueOf
	(MetroInputs.xnprocsChoice.getSelectedItem()).doubleValue();
      inputdata[11] = Double.valueOf
	(MetroInputs.ynprocsTxt.getText()).doubleValue();
    }

	
    MPI.COMM_WORLD.Bcast(inputdata, 0, 12, MPI.DOUBLE, 0);
    
    
    CommVars.globside  = (int)inputdata[0];
    CommVars.globisiz  = CommVars.globside;
    CommVars.globjsiz  = CommVars.globside;
    CommVars.globsites = CommVars.globside * CommVars.globside;
    CommVars.volume    = (float)CommVars.globsites;
    
    CommVars.therm_sweeps = (int)inputdata[1];
    CommVars.sweeps       = (int)inputdata[2];
    //CommVars.bin_size     = (int)inputdata[3];
    //CommVars.num_bins     = (int)inputdata[4];
    CommVars.numbins     = (int)inputdata[4];
    CommVars.binsz     = (int)inputdata[3];

    //CommVars.measurements = CommVars.bin_size * CommVars.num_bins;
    CommVars.measurements = CommVars.binsz * CommVars.numbins;
    
    CommVars.beta  = (float)inputdata[5];
    CommVars.start = (int)inputdata[6];
    CommVars.seed  = (long)inputdata[7];
    
    CommVars.Q = (int)inputdata[8];
    CommVars.updateflag = (int)inputdata[9];

    CommVars.procdim[0] = (int)inputdata[10];
    CommVars.procdim[1] = (int)inputdata[11];
    
    
    if(CommVars.procnum == 0) {
      System.out.println("\n\n\n ------ Input Data ------"); 

      System.out.println(" Number of processors : "+CommVars.nprocs); 
      
      System.out.println(" Length of the lattice : "+CommVars.globside); 
      
      System.out.println(" Volume = "+CommVars.globsites); 
      
      System.out.println
	(" Number of thermalization sweeps : "+CommVars.therm_sweeps); 
      
      System.out.println(" Number of sweeps per measurement : "+CommVars.sweeps); 
      
      System.out.println(" Bin size : "+CommVars.binsz); 
      
      System.out.println(" Number of bins? : "+CommVars.numbins); 
      
      System.out.println
	(" Number of measurements = bin size x number of bins :"+CommVars.measurements); 
      
      System.out.println(" Beta (inverse temperature) : "+CommVars.beta); 
      
      System.out.println(" Ordered (0) or random (1) start? : "+CommVars.start); 
      
      System.out.println(" Seed for the random number generator? : "+CommVars.seed); 
    }


  }
 
  

 
  /*---------------------------------------------------------------------
    
    Initialize the spins with an ordered or random start.               
    
    ---------------------------------------------------------------------*/

  static void init_spins() throws MPIException {
    
    int   i,j;
    
    
    if (CommVars.start == 0) { 
      // Ordered (cold) start 
      //if(CommVars.procnum == 0) 
      //System.out.println(" Cold start\n");
      
      for (i=1; i<=CommVars.isiz; i++)
	for (j=1; j<=CommVars.jsiz; j++){
	  CommVars.spin[i][j] = 0; 
	}
    }
    else {
      // Random (hot) start 
      //if(CommVars.procnum == 0) 
      //System.out.println(" Hot start-- \n"+CommVars.start);
      
      for (i=1; i<=CommVars.isiz; i++) {
	for (j=1; j<=CommVars.jsiz; j++) {
	  CommVars.spin[i][j] = (int) Prand.rand() * CommVars.Q;
	  if (CommVars.spin[i][j] == CommVars.Q) 
	    CommVars.spin[i][j] = 0;
	}
      }
    }
    
    // Pass edges to neighboring processors.     
    Edges.spin_edges();
    
    
  }


}

