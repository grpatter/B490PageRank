import mpi.*;

/*--------------------------------------------------------------------
                                                                      
   Initialize the domain decomposition of the lattice                 
   across the processors.                                             
                                                                      
----------------------------------------------------------------------*/

public class Domain { 
  
  static void decompose(CommVars cv) throws MPIException {

    int proc_array_num;
    boolean reorder;
    boolean periods[] = new boolean[2];


    /*  Processes at the ends are connected. */
    periods[0] = true;
    periods[1] = true;
    reorder    = true;

    cv.comm2d = MPI.COMM_WORLD.Create_cart(cv.procdim, periods, reorder);

    cv.procnum = cv.comm2d.Rank();

 
    /* Get the coords of this processor in the proc grid. */
    cv.proccoord = cv.comm2d.Coords(cv.procnum);


    /* Get the coords of neighboring processors. */
    cv.left   = cv.comm2d.Shift(0, 1).rank_source;
    cv.right  = cv.comm2d.Shift(0, 1).rank_dest;
    cv.bottom = cv.comm2d.Shift(1, 1).rank_source;
    cv.top    = cv.comm2d.Shift(1, 1).rank_dest;


  /* Actual position of the proc in the proc array */
    proc_array_num = (cv.proccoord[1] * cv.procdim[0]) + cv.proccoord[0];
    //System.out.println
    //(" procnum ="+cv.procnum+", proc_array_num = "+proc_array_num);



  /* Debug! */
    //System.out.println
    //(" procnum = "+cv.procnum+", left = "+cv.left+", right = "+cv.right);
    //System.out.println
    //(" procnum = "+cv.procnum+", bottom = "+cv.bottom+", top = "+cv.top);
  


  /* Decompose the global arrays onto processors */
    cv.globsize[0] = cv.globisiz;
    cv.globsize[1] = cv.globjsiz;

    cv.isiz = cv.globisiz/cv.procdim[0];
    cv.jsiz = cv.globjsiz/cv.procdim[1];

    cv.isiz_int = 4 * cv.isiz;
    cv.jsiz_int = 4 * cv.jsiz;
    cv.isiz2_int = 4 * (cv.isiz+2);
    cv.jsiz2_int = 4 * (cv.jsiz+2);


    cv.spin = new int[cv.isiz+2][cv.jsiz+2];
	
    cv.sites = cv.isiz * cv.jsiz;

    if(cv.procnum == 0){
      System.out.println
	("\n\n Using a "+cv.procdim[0]+" x "+cv.procdim[1]+
	 " = "+cv.nprocs+" processor array");

      System.out.println(" with a "+cv.isiz+" x "+cv.jsiz+
			 " lattice segment on each processor");
    }



  }


}


