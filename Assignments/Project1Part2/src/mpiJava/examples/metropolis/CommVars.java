import mpi.*;

/*--------------------------------------------------------------------
                                                                      
   Global variables common to Metropolis and Swendsen-Wang programs.  
                                                                      
----------------------------------------------------------------------*/

public class CommVars {


  /*  MPI stuff */
  static public int procnum, nprocs;
  static public int [] procdim = new int[2];
  static public int [] proccoord = new int[2];
  static public int [] globsize = new int[2];
  static public int [] localsiz = new int[2];
  static public int [] orig     = new int[2];
  Cartcomm comm2d; 


  /* Parallel stuff */
  static public int globside, globisiz, globjsiz, globsites;
  static public int left, right, top, bottom;


  static public int [][] spin;
  static public int   isiz, jsiz, sites, ACCEPT;
  static public int   isiz_int,jsiz_int,isiz2_int,jsiz2_int,imax_int;
  static public float volume, beta, p;
  static public long  seed;
  static public int   start;


      
  /* Measurement stuff */
  static public double  sum_energy, sum_enersq, sum_spins, 
                        sum_spinsq, sum_accept;
  static public double  bin_energy, bin_enersq, bin_spins, bin_spinsq;
  static public double  avbin_spheat, avbin_spheatsq, avbin_susp, avbin_suspsq;
  static public double  avbin_enersq, avbin_spinsq;
  static public int     therm_sweeps, sweeps, bin_size, 
                        nbin, num_bins, measurements, ibin;



}





