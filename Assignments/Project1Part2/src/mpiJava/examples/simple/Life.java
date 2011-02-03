//import MPI.*;
import mpi.*;

public class Life {

  static final int N = 11 ;
  static final int NITER = 3 ;

  static int blockSizeX, blockSizeY, blockBaseX, blockBaseY ;
  static int sX, sY ;  // Segment sizes, including ghost regions.
  static byte block [] ;

  static public void main(String [] args) throws MPIException {
    args = MPI.Init(args) ;

    for(int k= 0 ; k < args.length ; k++)
       System.out.println("arg " + k + " = " + args [k]) ;

    int dims [] = new int [2] ;
    boolean periods [] = new boolean [2] ;
    dims [0] = 2 ;
    dims [1] = 2 ;
    periods [0] = true ;
    periods [1] = true ;

    Cartcomm p = MPI.COMM_WORLD.Create_cart(dims, periods, false) ;

    /* Compute local `blockSizeX', `blockBaseX', `blockSizeY', `blockBaseY'. */

    {
      int coords [] = new int [2] ;
      int b, stepCoord ;

      CartParms cp;
      cp=p.Get();
      for(int k=0;k<2;k++)
      {  
         dims[k]=cp.dims[k];
         periods[k]=cp.periods[k];
         coords[k]=cp.coords[k];
        }

      b         = N / dims [0] ;
      stepCoord = N % dims [0] ;
      if(coords [0] < stepCoord) {
        blockSizeX = b + 1 ;
        blockBaseX = blockSizeX * coords [0] ;
      }
      else {
        blockSizeX = b ;
        blockBaseX = b * coords [0] + stepCoord ;
      }

      b         = N / dims [1] ;
      stepCoord = N % dims [1] ;
      if(coords [1] < stepCoord) {
        blockSizeY = b + 1 ;
        blockBaseY = blockSizeY * coords [1] ;
      }
      else {
        blockSizeY = b ;
        blockBaseY = b * coords [1] + stepCoord ;
      }
    }

    /* Create `block', allowing for ghost cells. */

    sX = blockSizeX + 2 ;
    sY = blockSizeY + 2 ;
    block = new byte [sX * sY] ;


    /* Define initial state of Life board */

    for(int i = 0, ib = 1 ; i < blockSizeX ; i++, ib++)
      for(int j = 0, jb = 1 ; j < blockSizeY ; j++, jb++) {
        int x = blockBaseX + i, y = blockBaseY + j ;
        if(x == N / 2 || y == N / 2)
          block[ib * sY + jb] = 1 ;
        else
          block[ib * sY + jb] = 0 ;
      }

    dumpBoard() ;

    // System.out.println("dumpBoard finished.");

    /* Precompute parameters of shift communications */

    Datatype edgeXType = Datatype.Contiguous(sY,MPI.BYTE);
    edgeXType.Commit() ;

    Datatype edgeYType = Datatype.Vector(sX, 1, sY,MPI.BYTE);
    edgeYType.Commit() ;

    int[] srcX = new int[2];
    int[] dstX = new int[2];
    int[] srcY = new int[2];
    int[] dstY = new int[2];
    
    ShiftParms sp;
    sp=p.Shift(0,  1);
    srcX[0]=sp.rank_source; dstX[0]=sp.rank_dest;
    sp=p.Shift(0, -1);
    srcX[1]=sp.rank_source; dstX[1]=sp.rank_dest;
    sp=p.Shift(1,  1);
    srcY[0]=sp.rank_source; dstY[0]=sp.rank_dest;
    sp=p.Shift(1, -1);
    srcY[1]=sp.rank_source; dstY[1]=sp.rank_dest;

    /* Main update loop. */

    int sums [] = new int [blockSizeX * blockSizeY] ;

    for(int iter = 0 ; iter < NITER ; iter++) {

      /* Shift this block's upper x edge into next neighbour's lower ghost edge */

      p.Sendrecv(block, blockSizeX * sY, 1, edgeXType, dstX[0], 0,
                 block, 0,               1, edgeXType, srcX[0], 0) ;

      /* Shift this block's lower x edge into prev neighbour's upper ghost edge */

      p.Sendrecv(block, sY,                    1, edgeXType, dstX[1], 0,
                 block, (blockSizeX + 1) * sY, 1, edgeXType, srcX[1], 0);

      /* Shift this block's upper y edge into next neighbour's lower ghost edge */

      p.Sendrecv(block, blockSizeY, 1, edgeYType, dstY[0], 0,
                 block, 0,          1, edgeYType, srcY[0], 0) ;

      /* Shift this block's lower y edge into prev neighbour's upper ghost edge */

      p.Sendrecv(block, 1,              1, edgeYType, dstY[1], 0,
                 block, blockSizeY + 1, 1, edgeYType, srcY[1], 0) ;


      /*  Calculate block of neighbour sums. */

      for(int i = 0, ib = 1 ; i < blockSizeX ; i++, ib++)
        for(int j = 0, jb = 1 ; j < blockSizeY ; j++, jb++) {
          int ibns = (ib - 1) * sY, ibs = ib * sY, ibps = (ib + 1) * sY ;
          int jbn = jb - 1, jbp = jb + 1 ;
          sums[i * blockSizeY + j] =
              block[ibns + jbn] + block[ibns + jb] + block[ibns + jbp] +
              block[ibs  + jbn] +                    block[ibs  + jbp] +
              block[ibps + jbn] + block[ibps + jb] + block[ibps + jbp] ;
        }

      for(int i = 0, ib = 1 ; i < blockSizeX ; i++, ib++)
        for(int j = 0, jb = 1 ; j < blockSizeY ; j++, jb++) {
          int ibs = ib * sY ;
          switch (sums[i * blockSizeY + j]) {
            case 2 : break;
            case 3 : block[ibs + jb] = 1; break;
            default: block[ibs + jb] = 0; break;
          }
        }

      dumpBoard() ;
    }

    MPI.Finalize();
  }


  static void dumpBoard() throws MPIException {

    /* Output current state of board */

    int np = MPI.COMM_WORLD.Size() ;
    int id = MPI.COMM_WORLD.Rank() ;

    if(id == 0) {
      byte board [] = new byte [N * N] ;

      for(int i = 0, ib = 1 ; i < blockSizeX ; i++, ib++)
        for(int j = 0, jb = 1 ; j < blockSizeY ; j++, jb++) {
          int x = blockBaseX + i, y = blockBaseY + j ;
          board[N * x + y] = block[ib * sY + jb] ;
        }
      
      for(int src = 1 ; src < np ; src++) {
        int params [] = new int [4] ;

        MPI.COMM_WORLD.Recv(params, 0, 4, MPI.INT, src, 0) ;
        int sizeX = params [0] ;
        int sizeY = params [1] ;
        int baseX = params [2] ;
        int baseY = params [3] ;

        /* Get local block from slave. */

        Datatype blockType = Datatype.Vector(sizeX, sizeY, N,MPI.BYTE);
        blockType.Commit() ;

        MPI.COMM_WORLD.Recv(board, N * baseX + baseY, 1, blockType, src, 0) ;
      }
      for(int x = 0 ; x < N ; x++) {
        for(int y = 0 ; y < N ; y++)
          System.out.print(" " + board[N * x + y]) ;
        System.out.println() ;
      }
      System.out.println() ;
    }
    else {
      int params [] = new int [4] ;
      params [0] = blockSizeX ;
      params [1] = blockSizeY ;
      params [2] = blockBaseX ;
      params [3] = blockBaseY ;

      MPI.COMM_WORLD.Send(params, 0, 4, MPI.INT, 0, 0) ;

      /* Send local block (excluding ghost areas) to master */

      Datatype blockType = Datatype.Vector(blockSizeX, blockSizeY, sY,MPI.BYTE);
      blockType.Commit() ;

      MPI.COMM_WORLD.Send(block, sY + 1, 1, blockType, 0, 0) ;
    }
  }
}

