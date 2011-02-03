import mpi.*;           

/*--------------------------------------------------------------------
                                                                      
	Swendsen-Wang Monte Carlo update of the Ising model            
	(Swendsen and Wang, Phys. Rev. Lett. 58 (1987) 86).            
                                                                      
	Self-labeling (sequential local algorithm plus local           
	label propagation of edges between processors) is used         
	to do the parallel component labeling. See Clive Baillie       
	and Paul Coddington, Concurrency: Practice and Experience      
        3, 129 (1991).                                                 
                                                                      
	Paul Coddington, August 1992, June 1993                        

---------------------------------------------------------------------
 
       MPI-JAVA version by Sung-Hoon Ko
                          April 1998.

---------------------------------------------------------------------*/

class SWcluster {
 
  static public void main(String[] args) {

    int    i, start;
    long   seed;
    double starttime, endtime, time;

    CommVars swvars = new CommVars();


    //  Initialize.

    // Initialize some MPI stuff.
    MPI.Init(args);


    // Read in the parameters.
    Init.input(swvars);

 
    // Do domain decomposition of the lattice over the nodes.
    Domain.decompose(swvars);
 
 
    / Initialize the random number generator.
    Prand.randinit(swvars);


    // Initialize the spins.
    Init.init_spins(swvars);
	

    // Initialize the data.
    Measure.init(metvars);

    // Probability for connecting sites.
    swvars.p = 1. - exp(-2.*beta);
    if (swvars.procnum == 0 )
      System.out.println("\nSwendsen-Wang probability is ",swvars.p);


    // Thermalize the system.
    SWupdate(swvars,swvars.therm_sweeps); 



    // Swendsen-Wang update, taking measurements every 'sweeps' iterations.
    time = 0;
    for (i=0; i<swvars.measurements; i++) {
      starttime = MPI_Wtime();

      SWupdate(swvars,swvars.sweeps);
    
      endtime = MPI_Wtime();
      time += (endtime - starttime);
	
      Measure.compute(swvars);
    } 
  


    // Print out results.
    if(swvars.procnum == 0) {
      Measure.output(metvars);
      System.out.println("\n\n Time is "+time+" seconds\n\n");
    }



    MPI.Finalize();



  }



  /*-------------------------------------------------------------------
                                                                      
       Swendsen-Wang Monte Carlo update of the Ising model            
       (Swendsen and Wang, Phys. Rev. Lett. 58 (1987) 86).            
                                                                      
       Self-labeling (sequential local algorithm plus local           
       label propagation of edges between processors) is used         
       to do the parallel component labeling. See Clive Baillie       
       and Paul Coddington, Concurrency: Practice and Experience      
       3, 129 (1991).                                                 
                                                                      
       Paul Coddington, August 1992, June 1993.                       
                                                                      
----------------------------------------------------------------------*/

  static public void SWupdate(CommVars cv, int iterations)
  {
    int   iter,i,j,n,num_clusters;
    double prand(),rr;


    /* Number of iterations before checking for termination
       (N.B. the value is purely empirical). */
    cv.minits = 1.8 * Math.sqrt((double)nprocs);


    /* Offset to make local cluster labels global. */
    cv.offset = cv.sites * cv.procnum;


    for (iter=0; iter<iterations; iter++) {


      /* Set up the bonds between sites. */
      for (i=1; i<=cv.isiz; i++) {
	for (j=1; j<=cv.jsiz; j++) {
	  if (( cv.spin[i][j] == cv.spin[i+1][j] ) && ( cv.p > Prand.rand() ))
	    cv.ibond[i][j] = true;
	  else
	    cv.ibond[i][j] = false;
     
	  if (( cv.spin[i][j] == cv.spin[i][j+1] ) && ( cv.p > Prand.rand() ))
	    cv.jbond[i][j] = true;
	  else
	    cv.jbond[i][j] = false;
	}
      }




      /* Pass bond edges. */ 
      Self_edges.bond_edges(cv); 



      /* Label the connected sub-clusters of sites on each processor. */
      cluster_label(cv); 



      /* Choose a random spin value for each cluster. */
      num_clusters = 0;
      for (n=1; n<=cv.sites; n++) {
	if (cv.Npoint[n] > 0) {
	  num_clusters++;
	  if ( Prand.rand() < 0.5 ) 
	    cv.new_spin[n] = 1;
	  else
	    cv.new_spin[n] = -1;
	}
      }
 

      /* Initialize the global cluster labels to be the site number. */
      for (n=0; n<=cv.sites+1; n++) 
	cv.Npoint[n] = n + cv.offset;
       


      /* Get the global cluster labels by matching up the edges. */
      Self_Label.global_label(cv); 



      /* Update the spins at every site according to their cluster label. */
      for (i=1; i<=cv.isiz; i++) {
	for (j=1; j<=cv.jsiz; j++) {
	  cv.spin[i][j] = cv.new_spin[cv.label[i][j]];
	  lcv.abel[i][j] = cv.Npoint[cv.label[i][j]];
	}
      }


      /* Copy the edges. */
      Edges.spin_edges(cv);


      /* Debug. */
      /* check_labeling(); */
        

    }



  }



  /*--------------------------------------------------------------------
                                                                      

   Hoshen-Kopelmann algorithm for labeling connected clusters
   (J. Hoshen and R. Kopelman, Phys. Rev. B 14 (1976) 3438).

   Every site has a pointer N. If this pointer is negative, then 
   it points to the label of the root site of the cluster. If it
   is positive, then the site is the root, and N refers to the 
   number of sites in the cluster, and the label is the given by
   the original label of that site. 

   This differs from the sequential algorithm in that periodic
   boundary conditions are not imposed.

----------------------------------------------------------------------*/

  static public void cluster_label(CommVars cv)
  {
    int     cluster_number;
    int     i,j,l,n,m[5],current_label,neighbors,min,sum;
    int     different;
    int     classify();

    cluster_number = 0;
  
    for (i=1; i<=cv.isiz; i++) {
      for (j=1; j<=cv.jsiz; j++) {

	neighbors = 0;

	/* Check if the site is bonded to sites which have already been
	   done (i.e. those in the negative directions). If so, use the
	   subroutine classify() to determine the proper cluster labels 
	   of the neighbors. */


	if ( cv.ibond[i-1][j] && (i != 1) ) {
	  current_label = cv.label[i-1][j];
	  neighbors++;
	  m[neighbors] = classify(cv,current_label);

	}
	if ( cv.jbond[i][j-1] && (j != 1) ) {
	  current_label = cv.label[i][j-1];
	  neighbors++;
	  m[neighbors] = classify(cv,current_label);

	}


	if (neighbors > 0) {
	  /*  Work out the proper cluster label. */
	  min = m[1];
	  sum = cv.Npoint[m[1]];
	  /* Find the smallest (i.e. proper) cluster label. */
	  for (n=2; n<=neighbors; n++) {
	    if (m[n] < min){
	      min = m[n];
	    }
	    /* If clusters merge, add the cluster sizes. */
	    different = TRUE;
	    for (l=1; l<=n-1; l++) 
	      if (m[n] == m[l]) {
		different = FALSE;
	      }
	    if (different) {
	      sum += cv.Npoint[m[n]];
	    
	    }
	  }
	  cv.label[i][j] = min;
	  cv.Npoint[min] = sum + 1;
	  /* Set pointers to the proper cluster label. */
	  for (n=1; n<=neighbors; n++)
	    if (m[n] != min) cv.Npoint[m[n]] = -min;
	}
	else {
	  /* Give the site a new cluster label. */
	  cluster_number++;
	  cv.label[i][j] = cluster_number;
	  cv.Npoint[cluster_number] = 1;
	}



      }
    }



    /*  Loop over sites again to find the size of the clusters 
	and set the labels to be the proper cluster label. */
	
    for (i=1; i<=cv.isiz; i++) 
      for (j=1; j<=cv.jsiz; j++) 
	/* Work out the proper cluster label. */
	cv.label[i][j] = classify(cv,cv.label[i][j]);





  }



  /*--------------------------------------------------------------------
 
  Go down the tree of pointers until you hit the root, which 
  is returned as the proper label.

---------------------------------------------------------------------*/

  static public int classify(CommVars cv, int current_label)
  {
    int  r, t;


    r = current_label;
    t = -cv.Npoint[r];


    if (t < 0) return(r);
    else { 
      r = t;
      t = -cv.Npoint[t];
      if (t < 0) return(r);
      else {
	while(1) {
	  r = t;
	  t = -cv.Npoint[t];
	  if (t < 0)  break;
	}
      }    
      cv.Npoint[current_label] = -r;
      return(r);
    }


  }
  

}
