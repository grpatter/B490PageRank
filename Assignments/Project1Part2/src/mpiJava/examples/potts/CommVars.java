import mpi.*;

/*--------------------------------------------------------------------
                                                                      
   Global variables common to Metropolis and Swendsen-Wang programs.  
                                                                      
----------------------------------------------------------------------*/

public class CommVars {


  /*  MPI stuff */
  static public int procnum, nprocs;
  static public int root2d ;
  static public int [] procdim = new int[2];
  static public int [] proccoord = new int[2];
  static public int [] globsize = new int[2];
  static public int [] localsiz = new int[2];
  static public int [] orig     = new int[2];
  static public Cartcomm comm2d; 


  /* Parallel stuff */
  static public int globside, globisiz, globjsiz, globsites;
  static public int left, right, top, bottom;


  static public int [][] spin;
  static public int [][] global_spin;
  static public int   Q, isiz, jsiz, sites, ACCEPT;
  static public int   isiz_int,jsiz_int,isiz2_int,jsiz2_int,imax_int;
  static public float volume, beta, p;
  static public long  seed;
  static public int   start;
  static public int   updateflag, iter;


  /* Analysis stuff */

  static final int M = 2;  /* number of different energy or mag measurements */
  static final int MAXNA = 200; /* max distance of autocorr measurements */
  static final int MAXQ = 20;

  static public double [] energy = new double[M];
  static public double [] spheat = new double[M];
  static public double [] mag = new double[M];
  static public double [] suss = new double[M];
  static public double [] errener = new double[M];
  static public double [] errspheat = new double[M];
  static public double [] errspin = new double[M];
  static public double [] errsuss = new double[M];
  static public double [] maxenergy = new double[M];
  static public double [] minenergy = new double[M];
  static public double [] maxspin = new double[M];
  static public double [] minspin = new double[M];
  static public double [] avenergy = new double[M];
  static public double [] avenersq = new double[M];
  static public double [] avbinenersq = new double[M];
  static public double [] avspheat = new double[M];
  static public double [] avspheatsq = new double[M];
  static public double [] avspin = new double[M];
  static public double [] avspinsq = new double[M];
  static public double [] avbinspinsq = new double[M];
  static public double [] avsuss = new double[M];
  static public double [] avsusssq = new double[M];
  static public double [] magsq = new double[M];
  static public double [] imsuss = new double[M];
  static public double [] spinmeansq = new double[M];
  static public double [] errmagsq = new double[M];
  static public double [] errimsuss = new double[M];
  static public double [] partmagsq = new double[M];
  static public double [] avmagsq = new double[M];
  static public double [] avbinmagsqsq = new double[M];
  static public double [] avimsuss = new double[M];
  static public double [] avimsusssq = new double[M];
  static public double [] partenergy = new double[M];
  static public double [] partenersq = new double[M];
  static public double [] partspin = new double[M];
  static public double [] partspinsq = new double[M];

  static public double [] tmpavenergy = new double[M];
  static public double [] tmpavenersq = new double[M];
  static public double [] tmpavbinenersq = new double[M];
  static public double [] tmpavspheat = new double[M];
  static public double [] tmpavspheatsq = new double[M];
  static public double [] tmpavspin = new double[M];
  static public double [] tmpavspinsq = new double[M];
  static public double [] tmpavbinspinsq = new double[M];
  static public double [] tmpavsuss = new double[M];
  static public double [] tmpavsusssq = new double[M];
  static public double [] tmperrener = new double[M];
  static public double [] tmperrspheat = new double[M];
  static public double [] tmperrspin = new double[M];
  static public double [] tmperrsuss = new double[M];
  
  static public double mass, tmp, fluct, autocorr, spinmax, binmeansq;
  static public int NA, binsz, numbins, maxdir, num_accept;
  static public int cntaccept, cntenergy, cntspin, cntcorr, oldcount;

  static public float [] Qspin = new float[MAXQ];

      
  /* Measurement stuff for ising model */
  static public double  sum_energy, sum_enersq, sum_spins, 
                        sum_spinsq, sum_accept;
  static public double  bin_energy, bin_enersq, bin_spins, bin_spinsq;
  static public double  avbin_spheat, avbin_spheatsq, avbin_susp, avbin_suspsq;
  static public double  avbin_enersq, avbin_spinsq;
  static public int     therm_sweeps, sweeps, bin_size, 
                        nbin, num_bins, measurements, ibin;



}





