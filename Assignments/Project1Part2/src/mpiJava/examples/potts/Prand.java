import mpi.*;

/************************************************************************

PRAND 

Parallel MLCG using leapfrog.
J.W. Flower and S.W. Otto, 
Solving Problems on Concurrent Processors, Vol. I, Chapter 12.

*************************************************************************/

/*
Some routines to do parallel random number generation.  The idea is
to take a standard linear congruential algorithm, and have every
processor compute the Pth iterate of that algorithm (where P is the
number of processors.)  If the seeds are set up properly (staggered)
the processors leapfrog over one another, and it is just as good as
having used the basic algorithm on a sequential machine. See Chap 12
of the Book.
*/


public class Prand { 


  final static int MULT = 1103515245;
  final static int ADD  = 12345;
  final static int MASK = 0x7fffffff;

  final static double _twoto31 = 2147483648.0;
  static long AAA,BBB;

  static long randx;


/*
	Initialize random number generator.
*/
  static void randinit() {
    int proc;
    
    AAA = 1;
    BBB = 0;
    for (proc=0;proc<CommVars.nprocs;++proc) {
      AAA = (MULT * AAA) & MASK;
      BBB = (MULT * BBB + ADD) & MASK;
      if (proc == CommVars.procnum) randx = (AAA*CommVars.seed + BBB) & MASK;
    }

  }
  
  
/*
	Return a random double in [0, 1.0).
*/
  static double rand()
  {
    double retvalue;
    
    retvalue = randx / _twoto31;
    randx = (AAA*randx+BBB)&MASK;
    return(retvalue);
  }



}

