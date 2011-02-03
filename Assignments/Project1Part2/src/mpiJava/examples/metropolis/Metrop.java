import mpi.*;

/*---------------------------------------------------------------------
                                                                      
       Metropolis Monte Carlo update of the Ising model.              
      (Metropolis et al., J. Chem. Phys. 21 (1953) 1087).             
                                                                      
    Based on the code in the book "Statistical Field Theory",         
    G. Parisi, (Addison-Wesley, 1988).                                
                                                                      
       Paul Coddington, August 1992, June 1993.                       

---------------------------------------------------------------------

       MPI-JAVA version : Sung-Hoon Ko
                          April 1998.
 Northeast Parallel Architectures Center at Syracuse University 1998
----------------------------------------------------------------------*/


class Metrop {

  static public void main(String[] args) throws MPIException {
    int    i, start;
    long   seed;
    double starttime, endtime, time;
    
    CommVars metvars = new CommVars();

    // Initialize some MPI stuff.
    MPI.Init(args);


    // Read in the parameters.
    Init.input(metvars); 


    // Do domain decomposition of the lattice over the nodes.
    Domain.decompose(metvars);


    // Initialize the random number generator.
    Prand.randinit(metvars); 


    // Initialize the spins.
    Init.init_spins(metvars);


    // Initialize the data.
    Measure.init(metvars);



    // Thermalize the system.
    metupdate(metvars,metvars.therm_sweeps);


    // Metropolis update, taking measurements every 'sweeps' iterations.

    time = 0;
    
    for (i=0; i<metvars.measurements; i++) {
      starttime = MPI.Wtime();

      metupdate(metvars,metvars.sweeps);
      endtime = MPI.Wtime();
	   
      time += (endtime - starttime);

      Measure.compute(metvars);
    }
 
 


    // Print out results 
  
    if(metvars.procnum == 0) {
      Measure.output(metvars);
      System.out.println("\n\n Time is "+time+" seconds\n\n");
    }



    MPI.Finalize();


  }



/*--------------------------------------------------------------------
                                                                      
                                                                      
    Metropolis Monte Carlo update of the Ising model.                 
                                                                      
    For the Ising model new_spin = - old_spin, so                     
    the old energy   oldE = - old_spin * (sum of neighboring spins)   
    the new energy   newE = - new_spin * (sum of neighboring spins)   
                          = - oldE                                    
                                                                      
    Do black/white checkerboard update, so can pass edge values       
    (blocked data) which will be more efficient than passing          
    single spins at a time (unblocked data).                          
                                                                      
----------------------------------------------------------------------*/

  static public void metupdate(CommVars cv, int iterations) throws MPIException {
    int black_white;
    int n, i,j, jstart;
    int new_spin, newE,oldE,deltaE;
    double randnum;
    
	
    for (n=0; n<iterations; n++) {

      cv.ACCEPT = 0;
      
      // Checkerboard update. */
      // Stride of inner loop is 2, starting point of inner loop
      // depends on whether we are updating black or white sites. 
      
      for (black_white=1; black_white<=2; black_white++) {
	
	// Pass edge values to get neighboring spins. 
	Edges.all_edges(cv);
	
	jstart = black_white;
	jstart = 3 - jstart;
	
	for (i=1; i<=cv.isiz; i++) {
	  // Alternate between jstart=1 and jstart=2 
	  jstart = 3 - jstart;
	  for (j=jstart; j<=cv.jsiz; j+=2) {
	    
	    // New trial spin (= - old spin)
	    
	    new_spin = - cv.spin[i][j];
	    	    
	    
	    // Calculate old and new energy (new energy = - old energy).
	    //   (May need to get some neighboring spin values values from 
	    //   other processors). 
	    
	    oldE = - cv.spin[i][j] * 
	      ( cv.spin[i-1][j] + cv.spin[i+1][j] +
		cv.spin[i][j-1] + cv.spin[i][j+1] );
	    
	    newE = - oldE;
	    deltaE = newE - oldE;
	    
	    
	    //  Metropolis accept/reject
	    randnum = Prand.rand();
	    if ( ( deltaE <= 0 )  ||
		 ( Math.exp(-cv.beta*deltaE) > randnum ) ) {
	      cv.spin[i][j] = new_spin;
	      cv.ACCEPT++;
	    }
	    
	    
	  }
	}
	
		
      } //  End of loop over black/white
    }


    // Pass edges to neighboring processors.  
    Edges.spin_edges(cv);    
  }  
    

}


