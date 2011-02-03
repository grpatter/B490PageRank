//
// mpiJava version : Sung-Hoon Ko
//                   June 1998
// Northeast Parallel Architectures Center at Syracuse University
//

import java.io.*;

import mpi.*;

class PingPong {

  static final int  PINGPONGS = 16;
  static final int  SAMPLES   = 16;
  static final int  LOG2N_MAX = 20;
  static final int  N_MAX     = ( 1 << (LOG2N_MAX-3) );

  public static void main(String[] args) throws MPIException {
     
    double      tf[] = new double [SAMPLES + 1];
    double      t_call;
    int         i, j_pe, log2nbyte, nbyte, ns, n_pp, my_pe, npes;
    int         n_init, n_mess;
    //    String      Title ="Single Messages --- MPI_JAVA on Solaris(SM) ";
    String      Title ="Single Messages --- MPI_JAVA";
    PrintStream pfout = null;
    Status      status;

    MPI.Init(args);

    my_pe = MPI.COMM_WORLD.Rank();
    npes  = MPI.COMM_WORLD.Size();

    System.out.println("\n"+MPI.Get_processor_name()+": Started");
    MPI.COMM_WORLD.Barrier();

    tf[0] = MPI.Wtime();
    for (ns = 1; ns <= SAMPLES; ++ns) {
      tf[ns] = MPI.Wtime();
    }
    
    t_call = (tf[SAMPLES] - tf[0]) / (double) SAMPLES ;

    if(my_pe==0) System.out.println("\n t_call= "+ t_call + " secs");
    
    n_init = 2 * PINGPONGS;
    n_mess = 2 * PINGPONGS;    

    if (my_pe == 0) {
      try {
	    FileOutputStream fout = new FileOutputStream("PingPong_JAVA_SM2.dat");
	    pfout = new PrintStream(fout);
      } catch(IOException ioe) {
	    System.err.println("Error in FileOutput...."+ioe);
      }
      Table.table_top(npes, n_init, n_mess, SAMPLES, t_call, Title);
    }

    for (log2nbyte = 0; log2nbyte <= LOG2N_MAX; ++log2nbyte) {
      nbyte = ( 1 << log2nbyte );
      byte A[] = new byte [nbyte];
      
      for (i = 0; i < nbyte; i++) {
	    A[i] = (byte)'0'; //(double) 1. / (i + 1);
      }
          
      for (j_pe = 1; j_pe <= npes-1; ++j_pe) {
	    MPI.COMM_WORLD.Barrier();
	    tf[0] = MPI.Wtime();
	    for (ns = 0; ns < SAMPLES; ns++) {
	      for (n_pp = 0; n_pp < PINGPONGS; n_pp++) {
	        if (my_pe == j_pe) {
	          MPI.COMM_WORLD.Send(A, 0, nbyte, MPI.BYTE, 0, 10);
	          status = MPI.COMM_WORLD.Recv(A, 0, nbyte, MPI.BYTE, 0, 20);
	        }
	        if (my_pe == 0) {
	          status = MPI.COMM_WORLD.Recv(A, 0 ,nbyte, MPI.BYTE, j_pe, 10);
	          MPI.COMM_WORLD.Send(A, 0, nbyte, MPI.BYTE, j_pe, 20);
	        }
	      }
	    tf[ns+1] = MPI.Wtime();
	  }
	  if (my_pe == 0)
	    Table.table_body(j_pe, nbyte, n_init, n_mess, SAMPLES, t_call, tf, pfout);
      }
      
    }
    
    MPI.Finalize();    
  }

}
