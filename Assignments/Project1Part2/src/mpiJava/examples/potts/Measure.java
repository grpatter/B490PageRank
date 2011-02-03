import mpi.*;

public class Measure { 


/*--------------------------------------------------------------------
                                                                      
  Initialize values for accumulation of measurements.                 
                                                                      
----------------------------------------------------------------------*/

  static void init()
  {
    // Initialize sums and sum of squares.
    /*
    CommVars.ibin = 0;
    CommVars.nbin = 0;
    CommVars.sum_energy = 0.0;
    CommVars.sum_enersq = 0.0;
    CommVars.sum_spins  = 0.0;
    CommVars.sum_spinsq = 0.0;
    CommVars.bin_energy = 0.0;
    CommVars.bin_enersq = 0.0;
    CommVars.bin_spins  = 0.0;
    CommVars.bin_spinsq = 0.0;
    CommVars.avbin_spheat   = 0.0;
    CommVars.avbin_spheatsq = 0.0;
    CommVars.avbin_susp     = 0.0;
    CommVars.avbin_suspsq   = 0.0;
    CommVars.avbin_enersq = 0.0;
    CommVars.avbin_spinsq = 0.0;
    */
    CommVars.ibin = 1;
    CommVars.cntaccept=0; 
    CommVars.cntenergy=0; 
    CommVars.cntspin=0;
    for (int m=0; m<CommVars.M; m++) { 
      CommVars.avenergy[m]=0.;   CommVars.avenersq[m]=0.; 
      CommVars.avspheat[m]=0.;   CommVars.avspheatsq[m]=0.; 
      CommVars.avspin[m]=0.;     CommVars.avspinsq[m]=0.; 
      CommVars.avsuss[m]=0.;     CommVars.avsusssq[m]=0.;
      CommVars.avbinenersq[m]=0; CommVars.avbinspinsq[m]=0;
      CommVars.avmagsq[m]=0.;    CommVars.avbinmagsqsq[m]=0.;
      CommVars.avimsuss[m]=0.;   CommVars.avimsusssq[m]=0.;
      CommVars.minenergy[m] = (float)10.0; 
      CommVars.maxenergy[m] = (float)-10.0;
      CommVars.minspin[m] = (float)1.0; 
      CommVars.maxspin[m] = (float)0.0;

      CommVars.tmpavenergy[m]=0.;   CommVars.tmpavenersq[m]=0.; 
      CommVars.tmpavspheat[m]=0.;   CommVars.tmpavspheatsq[m]=0.; 
      CommVars.tmpavspin[m]=0.;     CommVars.tmpavspinsq[m]=0.; 
      CommVars.tmpavsuss[m]=0.;     CommVars.tmpavsusssq[m]=0.;
      CommVars.tmpavbinenersq[m]=0; CommVars.tmpavbinspinsq[m]=0;
    }
  }
 


/*--------------------------------------------------------------------
                                                                      
  Take measurements of energy and magnetization.                      
                                                                      
----------------------------------------------------------------------*/

  static void compute() throws MPIException 
  {

    int    n,i,j, sumener,magn;
    double energy,magnetization;
    double bin_mean, bin_meansq, bin_spheat, bin_susp;
    int SndTemp[] = new int[3];
    int RcvTemp[] = new int[3];
    int inbin[] =  new int[CommVars.Q+1];
    int outbin[] = new int[CommVars.Q+1];

    sumener = 0;
    //magn = 0;

    for (n=0;n<CommVars.Q;n++) inbin[n] = 0;  /* initialize Q-state bin */
    
    for (i=1; i<=CommVars.isiz; i++) {
      for (j=1; j<=CommVars.jsiz; j++) {
	inbin[ CommVars.spin[i][j] ]++;
	if(CommVars.spin[i][j] == CommVars.spin[i+1][j]) sumener++;
	if(CommVars.spin[i][j] == CommVars.spin[i][j+1]) sumener++;
	//sumener += (CommVars.spin[i][j] == CommVars.spin[i+1][j]) +
	//         (CommVars.spin[i][j] == CommVars.spin[i][j+1]);
	/* for ising model which has spin value -1 and 1 */
	//ener += CommVars.spin[i][j] * ( CommVars.spin[i+1][j] + CommVars.spin[i][j+1] ); 
	//magn += CommVars.spin[i][j];
      }
    }

    //System.out.println(" procnum ="+CommVars.procnum+",sumener="+sumener);    

    // Sum the energy and magnetization over processors. */
    /*
    SndTemp[0] = ener;
    SndTemp[1] = magn;
    SndTemp[2] = CommVars.ACCEPT;
    CommVars.comm2d.Allreduce(SndTemp, 0, RcvTemp, 0, 3, MPI.INT, MPI.SUM);
    */
    inbin[CommVars.Q] = sumener;
    CommVars.comm2d.Allreduce(inbin, 0, outbin, 0, CommVars.Q+1, MPI.INT, MPI.SUM);
    sumener = outbin[CommVars.Q];

    /*
    ener   = RcvTemp[0];
    magn   = RcvTemp[1];
    CommVars.ACCEPT = RcvTemp[2];
    */


    /* Energy */
    CommVars.energy[0] = (double)sumener / CommVars.volume;
    //CommVars.energy[0] = sumener / CommVars.volume;

    /* Magnetization (spin in the preferred direction) */
    for (n=0; n<CommVars.Q; n++) 
      CommVars.Qspin[n] = (float)outbin[n] / CommVars.volume;
    CommVars.spinmax = CommVars.Qspin[0];
    CommVars.maxdir = 0;
    for (i=1;i<CommVars.Q;i++) {
      if (CommVars.Qspin[i] > CommVars.spinmax) {
	CommVars.spinmax = CommVars.Qspin[i];
	CommVars.maxdir = i;
      }
    }  
    CommVars.mag[0] = ((CommVars.Q*CommVars.spinmax) - 1) / (CommVars.Q-1);
    
    /* Spin in the 0 direction */
    CommVars.mag[1] = CommVars.Qspin[0];
    
    /* Square of the magnetization */
    CommVars.magsq[0] = CommVars.mag[0] * CommVars.mag[0];

    
    /* Energy */ 
    CommVars.cntenergy++;
    analyze_energy();
 
    /* Magnetization (spin in the preferred direction) */
    CommVars.cntspin++; 
    analyze_spin();
    
    /* Magnetization squared */
    analyze_magsq();
    



  }

  /*---------------------------------------------------------------------
                                                                      
   Calculate averages and errors and print out results.               
                                                                      
----------------------------------------------------------------------*/
  /* FUNCTIONS */
  
  static void analyze_energy()
  {
    int i,j,m,n;

    for (m=0;m<CommVars.M;m++) {
      if (CommVars.energy[m] < CommVars.minenergy[m]) 
	CommVars.minenergy[m] = CommVars.energy[m];
      if (CommVars.energy[m] > CommVars.maxenergy[m])
	CommVars.maxenergy[m] = CommVars.energy[m];

      CommVars.partenergy[m] += CommVars.energy[m];
      CommVars.partenersq[m] += CommVars.energy[m] * CommVars.energy[m];

      /*
      System.out.println("analyze_energy2="+CommVars.partenergy[0]);    
      System.out.println("analyze_energy3="+CommVars.cntenergy);    
      System.out.println("analyze_energy4="+CommVars.binsz);    
      */
      if ((CommVars.cntenergy % CommVars.binsz) == 0) {
	CommVars.binmeansq = (CommVars.partenergy[m]*CommVars.partenergy[m])/(CommVars.binsz*CommVars.binsz);
	CommVars.spheat[m] = CommVars.partenersq[m]/CommVars.binsz - CommVars.binmeansq;
	/*
	System.out.println("CommVars.spheat["+m+"]="+CommVars.spheat[m]);
	System.out.println("CommVars.partenersq11["+m+"]="+CommVars.partenersq[m]+", CommVars.binsz="+CommVars.binsz);
	System.out.println("CommVars.partenersq22["+m+"]="+CommVars.partenersq[m]/CommVars.binsz);
	System.out.println("CommVars.binmeansq="+CommVars.binmeansq);
	*/
	CommVars.avspheat[m] += CommVars.spheat[m];
	CommVars.avspheatsq[m] += CommVars.spheat[m] * CommVars.spheat[m];
	CommVars.avenergy[m] += CommVars.partenergy[m];
	CommVars.avenersq[m] += CommVars.partenersq[m];
	CommVars.avbinenersq[m] += CommVars.binmeansq;
	CommVars.partenergy[m] = 0.;
	CommVars.partenersq[m] = 0.;
	//System.out.println("CommVars.avspheat["+m+"]="+CommVars.avspheat[m]);
      }
      //System.out.println("analyze_energy1="+CommVars.avenergy[0]);    
      /* Energy auto-correlations */
      /* autocorrs(lastNEA,EA,EA2,energy,m); */
      //autocorrs(lastNEA,EA,energy,m);
    }
  }
  
  
  static void analyze_spin()
  {
    int i,j,m,n;

    for (m=0;m<CommVars.M;m++) {
      if (CommVars.mag[m] < CommVars.minspin[m]) 
	CommVars.minspin[m] = CommVars.mag[m];
      if (CommVars.mag[m] > CommVars.maxspin[m]) 
	CommVars.maxspin[m] = CommVars.mag[m];
      CommVars.partspin[m] += CommVars.mag[m];
      CommVars.partspinsq[m] += CommVars.mag[m] * CommVars.mag[m];
      if (CommVars.cntspin % CommVars.binsz == 0) {
	CommVars.binmeansq = (CommVars.partspin[m]*CommVars.partspin[m])/(CommVars.binsz*CommVars.binsz);
	CommVars.suss[m] = CommVars.partspinsq[m]/CommVars.binsz - CommVars.binmeansq;
	CommVars.avsuss[m] += CommVars.suss[m];
	CommVars.avsusssq[m] += CommVars.suss[m] * CommVars.suss[m];
	CommVars.avspin[m] += CommVars.partspin[m];
	CommVars.avspinsq[m] += CommVars.partspinsq[m];
	CommVars.avbinspinsq[m] += CommVars.binmeansq;
	CommVars.partspin[m] = 0.;
	CommVars.partspinsq[m] = 0.;
	CommVars.spinmeansq[m] = CommVars.binmeansq;
      }
      /* Spin auto-correlations */
      /* autocorrs(lastNMA,MA,MA2,mag,m); */
      //autocorrs(lastNMA,MA,mag,m);
    }
  }

  static void analyze_magsq()
  {
    int i,j,m,n;
    
    n = 0;
    CommVars.partmagsq[n] += CommVars.magsq[n];
    if (CommVars.cntspin % CommVars.binsz == 0) {
      CommVars.avmagsq[n] += CommVars.partmagsq[n];
      CommVars.avbinmagsqsq[n] += (CommVars.partmagsq[n] * CommVars.partmagsq[n]) / (CommVars.binsz*CommVars.binsz);
      for (m=0;m<CommVars.M;m++) {
	CommVars.imsuss[m] = (CommVars.partmagsq[n]/CommVars.binsz) - CommVars.spinmeansq[m];
	CommVars.avimsuss[m] += CommVars.imsuss[m];
	CommVars.avimsusssq[m] += CommVars.imsuss[m] * CommVars.imsuss[m];
      }
      CommVars.partmagsq[n] = 0.;
    }
    /* Spin squared auto-correlations */
    /* autocorrs(lastNM2A,M2A,M2A2,magsq,n); */
    //autocorrs(lastNM2A,M2A,magsq,n);
  }


  static void mid_output()      
  {
    int i,j,m,n;
    int binnum;
    double tmp1, tmp2;

    CommVars.numbins = CommVars.cntenergy/CommVars.binsz;

    //      if ((CommVars.cntenergy % CommVars.binsz) == 0) {
    //if (CommVars.cntenergy != 0) {
    if (CommVars.numbins == CommVars.ibin) {
      //System.out.println("CommVars.numbins="+CommVars.numbins+", CommVars.ibin:"+CommVars.ibin);
      for (m=0;m<CommVars.M;m++) {
        //CommVars.avenergy[m] /= (double)CommVars.cntenergy;
	CommVars.tmpavenergy[m] =
	  CommVars.avenergy[m] / (double)CommVars.cntenergy;

        //CommVars.avenersq[m] /= (double)CommVars.cntenergy;
	CommVars.tmpavenersq[m] = 
	  CommVars.avenersq[m] / (double)CommVars.cntenergy;

        //CommVars.avbinenersq[m] /= (double)CommVars.numbins;
	CommVars.tmpavbinenersq[m] =
	  CommVars.avbinenersq[m] / (double)CommVars.numbins;


        //CommVars.spheat[m] = (float)CommVars.avbinenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];
	tmp1 = (float)CommVars.avbinenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];

        //CommVars.errener[m] = Math.sqrt( CommVars.spheat[m] / (double)CommVars.numbins );
	 CommVars.tmperrener[m] = Math.sqrt( tmp1 / (double)CommVars.numbins );
        //CommVars.avspheat[m] /= (double)CommVars.numbins;
	CommVars.tmpavspheat[m] = 
	  CommVars.avspheat[m] / (double)CommVars.numbins;

        //CommVars.avspheatsq[m] /= (double)CommVars.numbins;
	CommVars.tmpavspheatsq[m] =
	  CommVars.avspheatsq[m] / (double)CommVars.numbins;

        //CommVars.spheat[m]  = CommVars.avspheatsq[m] - CommVars.avspheat[m]*CommVars.avspheat[m];
	tmp1  = CommVars.tmpavspheatsq[m] - CommVars.tmpavspheat[m]*CommVars.tmpavspheat[m];
        //CommVars.errspheat[m] = Math.sqrt( CommVars.spheat[m] / (double)CommVars.numbins );
	CommVars.tmperrspheat[m] = 
	  Math.sqrt( tmp1 / (double)CommVars.numbins );
        //CommVars.spheat[m] = CommVars.avenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];
	tmp1 = CommVars.tmpavenersq[m] - CommVars.tmpavenergy[m]*CommVars.tmpavenergy[m];
	
	CommVars.tmpavspheat[m] = CommVars.tmpavspheat[m] * CommVars.globsites;
	CommVars.tmperrspheat[m] = CommVars.tmperrspheat[m] * CommVars.globsites;
        //if (cntenergy >= NA)  	 /* finish energy autocorr */
	/* finish_autocorr(lastNEA,EA,EA2,errEA,avenergy,cntenergy,m); */
	//finish_autocorr(lastNEA,EA,avenergy,cntenergy,m);
      }
      
    }

      CommVars.numbins = CommVars.cntspin/CommVars.binsz;    
      //if (CommVars.cntspin != 0) {
      if (CommVars.numbins == CommVars.ibin) {
	CommVars.ibin++;      
      for (m=0;m<CommVars.M;m++) {
        //CommVars.avspin[m] /= (double)CommVars.cntspin;
	CommVars.tmpavspin[m] = 
	  CommVars.avspin[m] / (double)CommVars.cntspin;
        //CommVars.avspinsq[m] /= (double)CommVars.cntspin;
	CommVars.tmpavspinsq[m] = 
	  CommVars.avspinsq[m] / (double)CommVars.cntspin;
        //CommVars.avbinspinsq[m] /= (double)CommVars.numbins;
	CommVars.tmpavbinspinsq[m] =
	  CommVars.avbinspinsq[m] / (double)CommVars.numbins;
        //CommVars.suss[m] = CommVars.avbinspinsq[m] - CommVars.avspin[m]*CommVars.avspin[m];
	tmp1 = CommVars.tmpavbinspinsq[m] - CommVars.tmpavspin[m]*CommVars.tmpavspin[m];
        //CommVars.errspin[m] = Math.sqrt( CommVars.suss[m] / (double)CommVars.numbins );
	CommVars.tmperrspin[m] = 
	  Math.sqrt( tmp1 / (double)CommVars.numbins );
        //CommVars.avsuss[m] /= (double)CommVars.numbins;
	CommVars.tmpavsuss[m] = 
	  CommVars.avsuss[m] / (double)CommVars.numbins;
        //CommVars.avsusssq[m] /= (double)CommVars.numbins;
	CommVars.tmpavsusssq[m] = 
	  CommVars.avsusssq[m] / (double)CommVars.numbins;
        //CommVars.suss[m] = CommVars.avsusssq[m] - CommVars.avsuss[m]*CommVars.avsuss[m];
	tmp1 = CommVars.tmpavsusssq[m] - CommVars.tmpavsuss[m]*CommVars.tmpavsuss[m];
        //CommVars.errsuss[m] = Math.sqrt( CommVars.suss[m] / (double)CommVars.numbins );
	CommVars.tmperrsuss[m] = Math.sqrt( tmp1 / (double)CommVars.numbins );
        //CommVars.suss[m] = CommVars.avspinsq[m] - CommVars.avspin[m]*CommVars.avspin[m];
	tmp1 = CommVars.tmpavspinsq[m] - CommVars.tmpavspin[m]*CommVars.tmpavspin[m];
	
	CommVars.tmpavsuss[m] =  CommVars.tmpavsuss[m] * CommVars.globsites;
	CommVars.tmperrsuss[m] =  CommVars.tmperrsuss[m] * CommVars.globsites;
        //if (cntspin >= NA)    /* finish spin autocorr */
          /* finish_autocorr(lastNMA,MA,MA2,errMA,avspin,cntspin,m); */
	//finish_autocorr(lastNMA,MA,avspin,cntspin,m);
      }
      
      n = 0;
      CommVars.avmagsq[n] /= (double)CommVars.cntspin;
      CommVars.avbinmagsqsq[n] /= (double)CommVars.numbins;
      CommVars.errmagsq[n] = Math.sqrt( (CommVars.avbinmagsqsq[n] - CommVars.avmagsq[n]*CommVars.avmagsq[n]) / (double)CommVars.numbins );
      for (m=0;m<CommVars.M;m++) {
        CommVars.avimsuss[m] /= (double)CommVars.numbins;
        CommVars.avimsusssq[m] /= (double)CommVars.numbins;
        CommVars.imsuss[m] = CommVars.avimsusssq[m] - CommVars.avimsuss[m]*CommVars.avimsuss[m];
        CommVars.errimsuss[m] = Math.sqrt( CommVars.imsuss[m] / (double)CommVars.numbins );
        CommVars.imsuss[m] = CommVars.avmagsq[n] - CommVars.avspin[m]*CommVars.avspin[m];
      }
      
    }

    /* Print out results */
      /*
    System.out.println
      (" Energy         is "+CommVars.tmpavenergy[0]+" +/- "+CommVars.tmperrener[0] );
    System.out.println
      (" Magnetization  is "+CommVars.tmpavspin[0]+" +/- "+CommVars.tmperrspin[0] );
    System.out.println
      (" Specific heat  is "+CommVars.tmpavspheat[0]+" +/- "+CommVars.tmperrspheat[0]);
    System.out.println
      (" Susceptibility is "+CommVars.tmpavsuss[0]+" +/- "+CommVars.tmperrsuss[0]);
      */
    //System.out.println(" Acceptance     is "+av_accept);
    
  }


  static void final_output()      
  {
    int i,j,m,n;
    int binnum;
    
    //      if ((CommVars.cntenergy % CommVars.binsz) == 0) {
    CommVars.numbins = CommVars.cntenergy/CommVars.binsz;
    if (CommVars.cntenergy != 0) {
      for (m=0;m<CommVars.M;m++) {
        CommVars.avenergy[m] /= (double)CommVars.cntenergy;
        CommVars.avenersq[m] /= (double)CommVars.cntenergy;
        CommVars.avbinenersq[m] /= (double)CommVars.numbins;
        CommVars.spheat[m] = (float)CommVars.avbinenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];
	CommVars.spheat[m] = (float)CommVars.avbinenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];

        CommVars.errener[m] = Math.sqrt( CommVars.spheat[m] / (double)CommVars.numbins );
        CommVars.avspheat[m] /= (double)CommVars.numbins;
        CommVars.avspheatsq[m] /= (double)CommVars.numbins;
        CommVars.spheat[m] = CommVars.avspheatsq[m] - CommVars.avspheat[m]*CommVars.avspheat[m];
        CommVars.errspheat[m] = Math.sqrt( CommVars.spheat[m] / (double)CommVars.numbins );
        CommVars.spheat[m] = CommVars.avenersq[m] - CommVars.avenergy[m]*CommVars.avenergy[m];
	
        //if (cntenergy >= NA)  	 /* finish energy autocorr */
	/* finish_autocorr(lastNEA,EA,EA2,errEA,avenergy,cntenergy,m); */
	//finish_autocorr(lastNEA,EA,avenergy,cntenergy,m);
      }
      
    }

    CommVars.numbins = CommVars.cntspin/CommVars.binsz;    
    if (CommVars.cntspin != 0) {
      for (m=0;m<CommVars.M;m++) {
        CommVars.avspin[m] /= (double)CommVars.cntspin;
        CommVars.avspinsq[m] /= (double)CommVars.cntspin;
        CommVars.avbinspinsq[m] /= (double)CommVars.numbins;
        CommVars.suss[m] = CommVars.avbinspinsq[m] - CommVars.avspin[m]*CommVars.avspin[m];
        CommVars.errspin[m] = Math.sqrt( CommVars.suss[m] / (double)CommVars.numbins );
        CommVars.avsuss[m] /= (double)CommVars.numbins;
        CommVars.avsusssq[m] /= (double)CommVars.numbins;
        CommVars.suss[m] = CommVars.avsusssq[m] - CommVars.avsuss[m]*CommVars.avsuss[m];
        CommVars.errsuss[m] = Math.sqrt( CommVars.suss[m] / (double)CommVars.numbins );
        CommVars.suss[m] = CommVars.avspinsq[m] - CommVars.avspin[m]*CommVars.avspin[m];
	
        //if (cntspin >= NA)    /* finish spin autocorr */
          /* finish_autocorr(lastNMA,MA,MA2,errMA,avspin,cntspin,m); */
	//finish_autocorr(lastNMA,MA,avspin,cntspin,m);
      }
      
      n = 0;
      CommVars.avmagsq[n] /= (double)CommVars.cntspin;
      CommVars.avbinmagsqsq[n] /= (double)CommVars.numbins;
      CommVars.errmagsq[n] = Math.sqrt( (CommVars.avbinmagsqsq[n] - CommVars.avmagsq[n]*CommVars.avmagsq[n]) / (double)CommVars.numbins );
      for (m=0;m<CommVars.M;m++) {
        CommVars.avimsuss[m] /= (double)CommVars.numbins;
        CommVars.avimsusssq[m] /= (double)CommVars.numbins;
        CommVars.imsuss[m] = CommVars.avimsusssq[m] - CommVars.avimsuss[m]*CommVars.avimsuss[m];
        CommVars.errimsuss[m] = Math.sqrt( CommVars.imsuss[m] / (double)CommVars.numbins );
        CommVars.imsuss[m] = CommVars.avmagsq[n] - CommVars.avspin[m]*CommVars.avspin[m];
      }
      
    }

    /* Print out results */
    System.out.println("Final output>>>>>>>>>>>>>>>>>>");
    System.out.println
      (" Energy         is "+CommVars.avenergy[0]+" +/- "+CommVars.errener[0] );
    System.out.println
      (" Magnetization  is "+CommVars.avspin[0]+" +/- "+CommVars.errspin[0] );
    CommVars.avspheat[0] *= CommVars.globsites;
    CommVars.errspheat[0] *= CommVars.globsites;
    System.out.println
      (" Specific heat  is "+CommVars.avspheat[0]+" +/- "+CommVars.errspheat[0]);
    CommVars.avsuss[0] *= CommVars.globsites;
    CommVars.errsuss[0] *= CommVars.globsites;
    System.out.println
      (" Susceptibility is "+CommVars.avsuss[0]+" +/- "+CommVars.errsuss[0]);
    //System.out.println(" Acceptance     is "+av_accept);
    
    /*
      // Energy and specific heat 
      
      fprintf(fpax,"E (%d) = %f +/- %f\n", cntenergy,avenergy[m],errener[m]);
      fprintf(fpax,"Sp heat (%d) = %f  :  %f +/- %f\n", 
      cntenergy/binsz,globsites*spheat[m],globsites*avspheat[m],globsites*errspheat[m]);
      
      // Magnetization and susceptibility 
      fprintf(fpax,"Mag (%d) = %f +/- %f\n", cntspin,avspin[m],errspin[m]);
      fprintf(fpax,"Susp (%d) = %f  :  %f +/- %f\n", 
      cntspin/binsz,globsites*suss[m],globsites*avsuss[m],globsites*errsuss[m]);
      */
    


  }



  static void output_org()
  {
    double av_energy, av_enersq, av_spins, av_spinsq, av_accept;
    double err_ener, err_spin, err_spheat, err_susp;
    double specific_heat, susceptibility, variance, scale;

    // Take averages.

    av_energy = CommVars.sum_energy / CommVars.measurements;
    av_enersq = CommVars.sum_enersq / CommVars.measurements;
    av_spins  = CommVars.sum_spins  / CommVars.measurements;
    av_spinsq = CommVars.sum_spinsq / CommVars.measurements;
    av_accept = CommVars.sum_accept / CommVars.measurements;

    CommVars.avbin_enersq   = CommVars.avbin_enersq   / CommVars.num_bins;
    CommVars.avbin_spheat   = CommVars.avbin_spheat   / CommVars.num_bins;
    CommVars.avbin_spheatsq = CommVars.avbin_spheatsq / CommVars.num_bins;
    CommVars.avbin_spinsq   = CommVars.avbin_spinsq   / CommVars.num_bins;
    CommVars.avbin_susp     = CommVars.avbin_susp     / CommVars.num_bins;
    CommVars.avbin_suspsq   = CommVars.avbin_suspsq   / CommVars.num_bins;

    // Work out variances and errors.

    // Energy 

    variance = CommVars.avbin_enersq - (av_energy * av_energy);
    err_ener = Math.sqrt( variance / CommVars.num_bins );

    // Magnetization 

    variance = CommVars.avbin_spinsq - (av_spins * av_spins);
    err_spin = Math.sqrt( variance / CommVars.num_bins );

    // Specific heat

    specific_heat  = av_enersq - (av_energy * av_energy);
    scale = CommVars.volume * CommVars.beta * CommVars.beta;
    specific_heat  = specific_heat * scale;
    variance = CommVars.avbin_spheatsq - (CommVars.avbin_spheat * CommVars.avbin_spheat);
    err_spheat = Math.sqrt( variance / CommVars.num_bins );
    CommVars.avbin_spheat = CommVars.avbin_spheat * scale;
    err_spheat = err_spheat * scale;


    // Susceptibility

    susceptibility = av_spinsq - (av_spins * av_spins);
    susceptibility = susceptibility * scale;
    variance = CommVars.avbin_suspsq - (CommVars.avbin_susp * CommVars.avbin_susp);
    err_susp = Math.sqrt( variance / CommVars.num_bins );
    CommVars.avbin_susp = CommVars.avbin_susp * scale;
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

