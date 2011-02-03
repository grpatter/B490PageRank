// a test program from MPICH test suite.
/****************************************************************************

 MPI-Java version :
    Sung-Hoon Ko(shko@npac.syr.edu)
    Northeast Parallel Architectures Center at Syracuse University
    03/22/98

****************************************************************************/

import mpi.*;

class packO {
  static public void main(String[] args) throws MPIException {
    
/*
      Check pack/unpack of mixed datatypes.
*/

    final int BUF_SIZE = 500;

    int myrank;
    byte buffer[] = new byte[BUF_SIZE];
    int n[]    = new int[1];
    int size[] = new int[1];
    int src, dest, errcnt, errs;
    test a[] = new test[1];
    test c[] = new test[1];
    double b[] = new double[1];
    int pos;
    int apos[] = new int[1];


    Status status;


    MPI.Init(args);
    myrank = MPI.COMM_WORLD.Rank();

    src	   = 0;
    dest   = 1;
    
    errcnt = 0;
    if (myrank == src) {
      pos  = 0;
      n[0] = 10;
      a[0] = new test();
      a[0].a = 1;
      c[0] = new test();
      c[0].a = 999;
      b[0] = 2.2;
      pos = MPI.COMM_WORLD.Pack(n,0,1,MPI.INT,buffer,pos);

      pos = MPI.COMM_WORLD.Pack(a,0,1,MPI.OBJECT,buffer,pos);

      pos = MPI.COMM_WORLD.Pack(b,0,1,MPI.DOUBLE,buffer,pos);

      apos[0] = pos;
      MPI.COMM_WORLD.Send(apos, 0, 1, MPI.INT, dest, 999);

      MPI.COMM_WORLD.Send(buffer, 0, pos, MPI.PACKED, dest, 99);
    }
    else {
      status = MPI.COMM_WORLD.Recv(size, 0, 1, MPI.INT, src, 999);

      status = MPI.COMM_WORLD.Recv(buffer,0,size[0],MPI.PACKED,src,99);

      pos = 0;
      
      pos = MPI.COMM_WORLD.Unpack(buffer,pos,n,0,1,MPI.INT);
 
      pos = MPI.COMM_WORLD.Unpack(buffer,pos,c,0,1,MPI.OBJECT);

      pos = MPI.COMM_WORLD.Unpack(buffer,pos,b,0,1,MPI.DOUBLE);
      
      /* Check results */
      if (n[0] != 10) { 
	errcnt++;
	System.out.println
	  ("Wrong value for n; got "+n[0]+" expected 10");
      }
      if (c[0].a != 1) { 
	errcnt++;
	System.out.println
	  ("Wrong value for a; got "+a[0]+" expected 1.1");
      }
      if (b[0] != 2.2) { 
	errcnt++;
	System.out.println
	  ("Wrong value for b; got "+b[0]+" expected 2.2");
      }
    }

    if(myrank == 0)  System.out.println("PackO TEST COMPLETE\n");    
    MPI.Finalize();

  }
}
