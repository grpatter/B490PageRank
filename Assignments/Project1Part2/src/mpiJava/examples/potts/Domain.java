import mpi.*;

/*--------------------------------------------------------------------
                                                                      
   Initialize the domain decomposition of the lattice                 
   across the processors.                                             
                                                                      
----------------------------------------------------------------------*/

public class Domain { 
  
  static void decompose() throws MPIException {

    int proc_array_num;
    boolean reorder;
    boolean periods[] = new boolean[2];


    /*  Processes at the ends are connected. */
    periods[0] = true;
    periods[1] = true;
    reorder    = true;

    CommVars.comm2d = 
      MPI.COMM_WORLD.Create_cart(CommVars.procdim, periods, reorder);

    CommVars.root2d =
        Group.Translate_ranks(MPI.COMM_WORLD.Group(), new int [] {0},
                              CommVars.comm2d.Group()) [0] ;
    //CommVars.procnum = CommVars.comm2d.Rank();

 
    /* Get the coords of this processor in the proc grid. */
    CommVars.proccoord = CommVars.comm2d.Coords(CommVars.procnum);


    /* Get the coords of neighboring processors. */
    CommVars.left   = CommVars.comm2d.Shift(0, 1).rank_source;
    CommVars.right  = CommVars.comm2d.Shift(0, 1).rank_dest;
    CommVars.bottom = CommVars.comm2d.Shift(1, 1).rank_source;
    CommVars.top    = CommVars.comm2d.Shift(1, 1).rank_dest;


  /* Actual position of the proc in the proc array */
    proc_array_num = 
      (CommVars.proccoord[1] * CommVars.procdim[0]) + CommVars.proccoord[0];
    System.out.println
    (" procnum ="+CommVars.procnum+", proc_array_num = "+proc_array_num+
     ", proccoord[0]="+CommVars.proccoord[0]+
     ", proccoord[1]="+CommVars.proccoord[1]);



  /* Debug! */
    System.out.println
    (" procnum = "+CommVars.procnum+", left = "+CommVars.left+", right = "+CommVars.right);
    System.out.println
    (" procnum = "+CommVars.procnum+", bottom = "+CommVars.bottom+", top = "+CommVars.top);
  


  /* Decompose the global arrays onto processors */
    CommVars.globsize[0] = CommVars.globisiz;
    CommVars.globsize[1] = CommVars.globjsiz;

    CommVars.isiz = CommVars.globisiz/CommVars.procdim[0];
    CommVars.jsiz = CommVars.globjsiz/CommVars.procdim[1];

    CommVars.isiz_int = 4 * CommVars.isiz;
    CommVars.jsiz_int = 4 * CommVars.jsiz;
    CommVars.isiz2_int = 4 * (CommVars.isiz+2);
    CommVars.jsiz2_int = 4 * (CommVars.jsiz+2);


    CommVars.spin = new int[CommVars.isiz+2][CommVars.jsiz+2];
	
    CommVars.sites = CommVars.isiz * CommVars.jsiz;

    if(CommVars.procnum == 0){
      System.out.println
	("\n\n Using a "+CommVars.procdim[0]+" x "+CommVars.procdim[1]+
	 " = "+CommVars.nprocs+" processor array");

      System.out.println(" with a "+CommVars.isiz+" x "+CommVars.jsiz+
			 " lattice segment on each processor");
    }



  }


}


