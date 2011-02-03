import mpi.*;

/*----------------------------------------------------------------------

  Copy edge spin values between neighboring processors. 

----------------------------------------------------------------------*/

public class Edges { 
  
/*----------------------------------------------------------------------

   Get the values of the spins along edges in positive directions.
   This is all that is required to do measurements.

----------------------------------------------------------------------*/
  static void spin_edges() throws MPIException  
  {
    int len,vele,voffset,items;
    int sndoffset,rcvoffset;
    int i,j,k;
    int tmp[] = new int[(CommVars.isiz+2)*(CommVars.jsiz+2)];
    Status status;
    Datatype strideType;
    
    // copy 2D -> 1D; mpiJava can only send 1D array.
    for(i=0;i<CommVars.isiz+2;i++)
      for(j=0;j<CommVars.jsiz+2;j++)
	tmp[i*(CommVars.jsiz+2)+j] = CommVars.spin[i][j];

  
    // left edge -> right edge  (right node passes to left node) 
    len = CommVars.jsiz;
    sndoffset = (CommVars.jsiz+3);                //&spin[1][1]
    rcvoffset = (CommVars.jsiz+2)*(CommVars.isiz+1)+1;  //&spin[isiz+1][1]

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, CommVars.left,  10,
       tmp, rcvoffset, len, MPI.INT, CommVars.right, 10);




    // bottom edge -> top edge  (top node passes to bottom node).
    items   = CommVars.isiz;
    sndoffset = (CommVars.jsiz+3);       //&spin[1][1]
    rcvoffset = (CommVars.jsiz+2)*2-1;   //&spin[1][jsiz+1]

    vele    = 1;
    voffset = CommVars.jsiz+2;

    strideType = Datatype.Vector(items, vele, voffset, MPI.INT);

    // New data type must be committed before it can be used
    strideType.Commit();

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, CommVars.bottom,  20,
       tmp, rcvoffset, vele, strideType, CommVars.top, 20);      


    // copy back 1D -> 2D
    for(i=0;i<CommVars.isiz+2;i++)
      for(j=0;j<CommVars.jsiz+2;j++)
	CommVars.spin[i][j] = tmp[i*(CommVars.jsiz+2)+j];


  }




  /*----------------------------------------------------------------------

   Get the values of the spins along edges in all directions.
   Need this for Metropolis update with blocked data communications.

-----------------------------------------------------------------------*/

  static void all_edges() throws MPIException 
  {
    int len,vele,vlen,voffset,items;
    int sndoffset,rcvoffset;
    int i,j,k;
    int tmp[] = new int[(CommVars.isiz+2)*(CommVars.jsiz+2)];
    Status status;
    Datatype strideType;

    // copy 2D -> 1D; mpiJava can only send 1D array.
    for(i=0;i<CommVars.isiz+2;i++)
      for(j=0;j<CommVars.jsiz+2;j++)
	tmp[i*(CommVars.jsiz+2)+j] = CommVars.spin[i][j];

 


    // bottom edge -> top edge  (top node passes to bottom node).
    items   = CommVars.isiz;
    sndoffset = (CommVars.jsiz+3);       //&spin[1][1]
    rcvoffset = (CommVars.jsiz+2)*2-1;   //&spin[1][jsiz+1]

    vele    = 1;
    voffset = CommVars.jsiz+2;

    strideType = Datatype.Vector(items, vele, voffset, MPI.INT);
    // New data type must be committed before it can be used
    strideType.Commit();

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, CommVars.bottom,  30,
       tmp, rcvoffset, vele, strideType, CommVars.top,     30);      
	  

    // top edge -> bottom edge  (bottom node passes to top node).
    sndoffset = (CommVars.jsiz+2)*2-2;   //&spin[1][jsiz]
    rcvoffset = (CommVars.jsiz+2);       //&spin[1][0]

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, CommVars.top,    40,
       tmp, rcvoffset, vele, strideType, CommVars.bottom, 40);      





    len = CommVars.jsiz;

    // left edge -> right edge  (right node passes to left node).
    sndoffset = (CommVars.jsiz+3);  //&spin[1][1]
    rcvoffset = (CommVars.jsiz+2)*(CommVars.isiz+1)+1;  //&spin[isiz+1][1]

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, CommVars.left,  50,
       tmp, rcvoffset, len, MPI.INT, CommVars.right, 50);



    // right edge -> left edge  (left node passes to right node).
    sndoffset = (CommVars.jsiz+2)*(CommVars.isiz)+1;  //&spin[isiz][1]
    rcvoffset = 1;  //&spin[0][1]

    status = CommVars.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, CommVars.right, 50,
       tmp, rcvoffset, len, MPI.INT, CommVars.left,  50);



    for(i=0;i<CommVars.isiz+2;i++)
      for(j=0;j<CommVars.jsiz+2;j++)
	CommVars.spin[i][j] = tmp[i*(CommVars.jsiz+2)+j];


  }


  //////////////////////////////////////////////////////////
  static int get_spin(int i, int j) throws MPIException {
   
    int tmpin[] = new int[1];
    int tmpout[] = new int[1];
    Status status;

    /* Get the value of the spin at site (i,j).
       This may require some communication if (i,j) is on
       another processor */

    if( i > CommVars.isiz ) {
      tmpin[0] = CommVars.spin[1][j];
      status = CommVars.comm2d.Sendrecv      
	(tmpin, 0, 1, MPI.INT, CommVars.left,  13,
	 tmpout, 0, 1, MPI.INT, CommVars.right, 13);
      CommVars.spin[CommVars.isiz+1][j] = tmpout[0];
    }
    else if( i < 1) {
      tmpin[0] = CommVars.spin[CommVars.isiz][j];
      status = CommVars.comm2d.Sendrecv      
	(tmpin, 0, 1, MPI.INT, CommVars.right,  14,
	 tmpout, 0, 1, MPI.INT, CommVars.left,   14);
      CommVars.spin[0][j] = tmpout[0];
    }
      

    if( j > CommVars.jsiz ) {
      tmpin[0] = CommVars.spin[i][1];
      status = CommVars.comm2d.Sendrecv      
	(tmpin, 0, 1, MPI.INT, CommVars.bottom,  15,
	 tmpout, 0, 1, MPI.INT, CommVars.top,     15);
      CommVars.spin[i][CommVars.jsiz+1] = tmpout[0];
    }
    else if( j < 1) {
      tmpin[0] = CommVars.spin[i][CommVars.jsiz];
      status = CommVars.comm2d.Sendrecv      
	(tmpin, 0, 1, MPI.INT, CommVars.top,     16,
	 tmpout, 0, 1, MPI.INT, CommVars.bottom,  16);
      CommVars.spin[i][0] = tmpout[0];
    }

   /*      
    System.out.println("procnum22 ="+CommVars.procnum+
		       "CommVars.spin["+i+"]["+j+"] = "+
		       CommVars.spin[i][j]);
    */
    return CommVars.spin[i][j];

  }


}


