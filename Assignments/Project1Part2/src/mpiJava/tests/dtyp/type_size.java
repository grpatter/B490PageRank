/*
 * File         : type_size.java
 * Author       : Sung-Hoon Ko
 * Created      : Fri Jul 10 15:07:58 1998
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 1999/09/14 20:51:41 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */


import mpi.*;

class type_size {
  static public void main(String[] args) throws MPIException {   
    int my_rank;
    
  
    MPI.Init(args);   
    
    System.out.println();
    System.out.println("MPI.BYTE.Size()    = "+ MPI.BYTE.Size());
    System.out.println("MPI.CHAR.Size()    = "+ MPI.CHAR.Size());
    System.out.println("MPI.SHORT.Size()   = "+ MPI.SHORT.Size());
    System.out.println("MPI.BOOLEAN.Size() = "+ MPI.BOOLEAN.Size());
    System.out.println("MPI.INT.Size()     = "+ MPI.INT.Size());
    System.out.println("MPI.LONG.Size()    = "+ MPI.LONG.Size());
    System.out.println("MPI.FLOAT.Size()   = "+ MPI.FLOAT.Size());
    System.out.println("MPI.DOUBLE.Size()  = "+ MPI.DOUBLE.Size());
    System.out.println();

    MPI.Finalize();


  }
}
