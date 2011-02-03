import mpi.*;

/*----------------------------------------------------------------------

  Copy edge spin values between neighboring processors. 

----------------------------------------------------------------------*/

public class Edges { 
  
/*----------------------------------------------------------------------

   Get the values of the spins along edges in positive directions.
   This is all that is required to do measurements.

----------------------------------------------------------------------*/
  static void spin_edges(CommVars cv) throws MPIException
  {
    int len,vele,voffset,items;
    int sndoffset,rcvoffset;
    int i,j,k;
    int tmp[] = new int[(cv.isiz+2)*(cv.jsiz+2)];
    Status status;
    Datatype strideType;
    
    // copy 2D -> 1D; mpiJava can only send 1D array.
    for(i=0;i<cv.isiz+2;i++)
      for(j=0;j<cv.jsiz+2;j++)
	tmp[i*(cv.jsiz+2)+j] = cv.spin[i][j];

  
    // left edge -> right edge  (right node passes to left node) 
    len = cv.jsiz;
    sndoffset = (cv.jsiz+3);                //&spin[1][1]
    rcvoffset = (cv.jsiz+2)*(cv.isiz+1)+1;  //&spin[isiz+1][1]

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, cv.left,  10,
       tmp, rcvoffset, len, MPI.INT, cv.right, 10);




    // bottom edge -> top edge  (top node passes to bottom node).
    items   = cv.isiz;
    sndoffset = (cv.jsiz+3);       //&spin[1][1]
    rcvoffset = (cv.jsiz+2)*2-1;   //&spin[1][jsiz+1]

    vele    = 1;
    voffset = cv.jsiz+2;

    strideType = Datatype.Vector(items, vele, voffset, MPI.INT);

    // New data type must be committed before it can be used
    strideType.Commit();

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, cv.bottom,  20,
       tmp, rcvoffset, vele, strideType, cv.top, 20);      


    // copy back 1D -> 2D
    for(i=0;i<cv.isiz+2;i++)
      for(j=0;j<cv.jsiz+2;j++)
	cv.spin[i][j] = tmp[i*(cv.jsiz+2)+j];


  }




  /*----------------------------------------------------------------------

   Get the values of the spins along edges in all directions.
   Need this for Metropolis update with blocked data communications.

-----------------------------------------------------------------------*/

  static void all_edges(CommVars cv) throws MPIException
  {
    int len,vele,vlen,voffset,items;
    int sndoffset,rcvoffset;
    int i,j,k;
    int tmp[] = new int[(cv.isiz+2)*(cv.jsiz+2)];
    Status status;
    Datatype strideType;

    // copy 2D -> 1D; mpiJava can only send 1D array.
    for(i=0;i<cv.isiz+2;i++)
      for(j=0;j<cv.jsiz+2;j++)
	tmp[i*(cv.jsiz+2)+j] = cv.spin[i][j];

 


    // bottom edge -> top edge  (top node passes to bottom node).
    items   = cv.isiz;
    sndoffset = (cv.jsiz+3);       //&spin[1][1]
    rcvoffset = (cv.jsiz+2)*2-1;   //&spin[1][jsiz+1]

    vele    = 1;
    voffset = cv.jsiz+2;

    strideType = Datatype.Vector(items, vele, voffset, MPI.INT);
    // New data type must be committed before it can be used
    strideType.Commit();

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, cv.bottom,  30,
       tmp, rcvoffset, vele, strideType, cv.top,     30);      
	  

    // top edge -> bottom edge  (bottom node passes to top node).
    sndoffset = (cv.jsiz+2)*2-2;   //&spin[1][jsiz]
    rcvoffset = (cv.jsiz+2);       //&spin[1][0]

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, vele, strideType, cv.top,    40,
       tmp, rcvoffset, vele, strideType, cv.bottom, 40);      





    len = cv.jsiz;

    // left edge -> right edge  (right node passes to left node).
    sndoffset = (cv.jsiz+3);  //&spin[1][1]
    rcvoffset = (cv.jsiz+2)*(cv.isiz+1)+1;  //&spin[isiz+1][1]

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, cv.left,  50,
       tmp, rcvoffset, len, MPI.INT, cv.right, 50);



    // right edge -> left edge  (left node passes to right node).
    sndoffset = (cv.jsiz+2)*(cv.isiz)+1;  //&spin[isiz][1]
    rcvoffset = 1;  //&spin[0][1]

    status = cv.comm2d.Sendrecv
      (tmp, sndoffset, len, MPI.INT, cv.right, 50,
       tmp, rcvoffset, len, MPI.INT, cv.left,  50);



    for(i=0;i<cv.isiz+2;i++)
      for(j=0;j<cv.jsiz+2;j++)
	cv.spin[i][j] = tmp[i*(cv.jsiz+2)+j];


  }


}


