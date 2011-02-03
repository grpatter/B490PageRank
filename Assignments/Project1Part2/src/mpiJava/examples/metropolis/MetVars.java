/*--------------------------------------------------------------------
                                                                      
   Global variables common to Metropolis and Swendsen-Wang programs.  
                                                                      
----------------------------------------------------------------------*/

public class CommVars {
  //public int [] dims;


  //#define FALSE 0
  //#define TRUE  1



  /*  MPI stuff */
  public int procnum, nprocs;
  //procdim[2], proccoord[2];
  public int [] procdim, proccoord;
  //globsize[2], localsiz[2], orig[2];
  public int [] globsize, localsiz, orig;
  //MPI_Comm comm2d; 
  Comm comm2d; 


  //#define MAX_SIDE   256 
  //#define MAX_BUFF   MAX_SIDE + 2 
  //#define MAX_SITES  MAX_SIDE * MAX_SIDE 
  //#define MAX_BYTES  MAX_BUFF * 4 
  //#define MAX_PROCS  64 
  /*#define MASK       32768 */
	

  /* Parallel stuff */
  public int globside, globisiz, globjsiz, globsites;
  public int left, right, top, bottom;


  //#define EDGE  10 


  //int   spin[MAX_SIDE+2][MAX_SIDE+2];
  public int [][] spin;
  public int   isiz, jsiz, sites, ACCEPT;
  public int   isiz_int,jsiz_int,two_isiz_int,two_jsiz_int,imax_int;
  public float volume, beta, p;


      
  /* Measurement stuff */
  public double  sum_energy, sum_enersq, sum_spins, sum_spinsq, sum_accept;
  public double  bin_energy, bin_enersq, bin_spins, bin_spinsq;
  public double  avbin_spheat, avbin_spheatsq, avbin_susp, avbin_suspsq;
  public double  avbin_enersq, avbin_spinsq;
  public int     therm_sweeps, sweeps, bin_size, 
                 num_bins, measurements, ibin;

}





