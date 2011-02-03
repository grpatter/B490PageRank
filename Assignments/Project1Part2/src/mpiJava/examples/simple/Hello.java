  /*
   * Author of revised version: Franklyn Pinedo
   *
   * Adapted from Source Code in C of Tutorial/User's Guide for MPI by
   * Peter Pacheco.
   */

  import mpi.* ;
 
  class Hello {
    static public void main(String[] args) throws MPIException {
      

      MPI.Init(args) ;

      int my_rank; // Rank of process
      int source;  // Rank of sender
      int dest;    // Rank of receiver 
      int tag=50;  // Tag for messages	
      int myrank = MPI.COMM_WORLD.Rank() ;
      int      p = MPI.COMM_WORLD.Size() ;

      if(myrank != 0) {
	dest=0;
        char [] message = ("Greetings from process " + myrank).toCharArray() ;
        MPI.COMM_WORLD.Send(message, 0, message.length, MPI.CHAR,dest, tag) ;
     }
      else {  // my_rank == 0
	for (source =1;source < p;source++) {
        char [] message = new char [40] ;
        MPI.COMM_WORLD.Recv(message, 0, 40, MPI.CHAR, source, tag) ;
        System.out.println("received: " + new String(message) + " : ") ;
	}
      }
 
      MPI.Finalize();
    }
  }
