import mpi.*;

public class Measure { 


/*--------------------------------------------------------------------
                                                                      
  Initialize values for accumulation of measurements.                 
                                                                      
----------------------------------------------------------------------*/

  static void init(CommVars cv)
  {
    // Initialize sums and sum of squares.
    cv.ibin = 0;
    cv.nbin = 0;
    cv.sum_energy = 0.0;
    cv.sum_enersq = 0.0;
    cv.sum_spins  = 0.0;
    cv.sum_spinsq = 0.0;
    cv.bin_energy = 0.0;
    cv.bin_enersq = 0.0;
    cv.bin_spins  = 0.0;
    cv.bin_spinsq = 0.0;
    cv.avbin_spheat   = 0.0;
    cv.avbin_spheatsq = 0.0;
    cv.avbin_susp     = 0.0;
    cv.avbin_suspsq   = 0.0;
    cv.avbin_enersq = 0.0;
    cv.avbin_spinsq = 0.0;

  }
 


/*--------------------------------------------------------------------
                                                                      
  Take measurements of energy and magnetization.                      
                                                                      
----------------------------------------------------------------------*/

  static void compute(CommVars cv) throws MPIException
  {

    int    i,j, ener,magn;
    double energy,magnetization;
    double bin_mean, bin_meansq, bin_spheat, bin_susp;
    int SndTemp[] = new int[3];
    int RcvTemp[] = new int[3];

    ener = 0;
    magn = 0;
    for (i=1; i<=cv.isiz; i++) {
      for (j=1; j<=cv.jsiz; j++) {
	ener += cv.spin[i][j] * ( cv.spin[i+1][j] + cv.spin[i][j+1] );
	magn += cv.spin[i][j];
      }
    }



    // Sum the energy and magnetization over processors. */

    SndTemp[0] = ener;
    SndTemp[1] = magn;
    SndTemp[2] = cv.ACCEPT;
    
    cv.comm2d.Allreduce(SndTemp, 0, RcvTemp, 0, 3, MPI.INT, MPI.SUM);
    
    ener   = RcvTemp[0];
    magn   = RcvTemp[1];
    cv.ACCEPT = RcvTemp[2];
    

    // Normalize.
    if (magn < 0)    magn = - magn;
    energy = - (double)ener / cv.volume;
    magnetization = (double)magn / cv.volume;
    cv.sum_accept += (double)cv.ACCEPT/cv.volume;
    

    // Bin the data in order to calculate errors. */
    cv.ibin++;

    cv.bin_energy += energy;
    cv.bin_enersq += (energy * energy);
    
    cv.bin_spins  += magnetization;
    cv.bin_spinsq += (magnetization * magnetization);

    if (cv.ibin == cv.bin_size) {
      
      /* Add the bin values to the total value */
      
      cv.sum_energy += cv.bin_energy;
      cv.sum_enersq += cv.bin_enersq;
      
      cv.sum_spins  += cv.bin_spins;
      cv.sum_spinsq += cv.bin_spinsq;
      
      // Calculate bin means and mean squares
      
      bin_mean = cv.bin_energy / cv.bin_size;
      bin_meansq = bin_mean * bin_mean;
      bin_spheat = (cv.bin_enersq/cv.bin_size) - bin_meansq;
      cv.avbin_spheat = cv.avbin_spheat + bin_spheat;
      cv.avbin_spheatsq = cv.avbin_spheatsq + (bin_spheat * bin_spheat);
      cv.avbin_enersq = cv.avbin_enersq + bin_meansq;
       
      bin_mean = cv.bin_spins / cv.bin_size;
      bin_meansq = bin_mean * bin_mean;
      bin_susp = (cv.bin_spinsq/cv.bin_size) - bin_meansq;
      cv.avbin_susp += bin_susp;
      cv.avbin_suspsq += (bin_susp * bin_susp);
      cv.avbin_spinsq += bin_meansq;


      // Reset bin sums to zero for new bin */
      cv.ibin = 0;
      cv.nbin++;
      cv.bin_energy = 0.0;
      cv.bin_enersq = 0.0;
      cv.bin_spins  = 0.0;
      cv.bin_spinsq = 0.0;



      // Print out the number of bins currently done as a status check
      if(cv.procnum == 0) {
	System.out.println( "Completed "+cv.nbin+" of "+cv.num_bins+" bins");
      }

    }


  }

  

  /*---------------------------------------------------------------------
                                                                      
   Calculate averages and errors and print out results.               
                                                                      
----------------------------------------------------------------------*/

  static void output(CommVars cv)
  {
    double av_energy, av_enersq, av_spins, av_spinsq, av_accept;
    double err_ener, err_spin, err_spheat, err_susp;
    double specific_heat, susceptibility, variance, scale;

    // Take averages.

    av_energy = cv.sum_energy / cv.measurements;
    av_enersq = cv.sum_enersq / cv.measurements;
    av_spins  = cv.sum_spins  / cv.measurements;
    av_spinsq = cv.sum_spinsq / cv.measurements;
    av_accept = cv.sum_accept / cv.measurements;

    cv.avbin_enersq   = cv.avbin_enersq   / cv.num_bins;
    cv.avbin_spheat   = cv.avbin_spheat   / cv.num_bins;
    cv.avbin_spheatsq = cv.avbin_spheatsq / cv.num_bins;
    cv.avbin_spinsq   = cv.avbin_spinsq   / cv.num_bins;
    cv.avbin_susp     = cv.avbin_susp     / cv.num_bins;
    cv.avbin_suspsq   = cv.avbin_suspsq   / cv.num_bins;

    // Work out variances and errors.

    // Energy 

    variance = cv.avbin_enersq - (av_energy * av_energy);
    err_ener = Math.sqrt( variance / cv.num_bins );

    // Magnetization 

    variance = cv.avbin_spinsq - (av_spins * av_spins);
    err_spin = Math.sqrt( variance / cv.num_bins );

    // Specific heat

    specific_heat  = av_enersq - (av_energy * av_energy);
    scale = cv.volume * cv.beta * cv.beta;
    specific_heat  = specific_heat * scale;
    variance = cv.avbin_spheatsq - (cv.avbin_spheat * cv.avbin_spheat);
    err_spheat = Math.sqrt( variance / cv.num_bins );
    cv.avbin_spheat = cv.avbin_spheat * scale;
    err_spheat = err_spheat * scale;


    // Susceptibility

    susceptibility = av_spinsq - (av_spins * av_spins);
    susceptibility = susceptibility * scale;
    variance = cv.avbin_suspsq - (cv.avbin_susp * cv.avbin_susp);
    err_susp = Math.sqrt( variance / cv.num_bins );
    cv.avbin_susp = cv.avbin_susp * scale;
    err_susp = err_susp * scale;


    /* Print out results. */

    System.out.println("\n\n");
    System.out.println
      (" Energy         is "+av_energy+" +/- "+err_ener );
    System.out.println
      (" Magnetization  is "+av_spins+" +/- "+err_spin );
    System.out.println
      (" Specific heat  is "+specific_heat+" +/- "+err_spheat);
    System.out.println
      (" Susceptibility is "+susceptibility+" +/- "+err_susp);
    System.out.println
      (" Acceptance     is "+av_accept);

  }


}  

