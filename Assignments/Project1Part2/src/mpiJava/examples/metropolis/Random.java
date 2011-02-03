import mpi.*;

/*---------------------------------------------------------------------
                                                                      
  Parallel version of the lagged Fibonnacci random number generator   
  RANMAR.                                                             
                                                                      
  See F. James, Comp. Phys. Comm. 60, 329 (1990), or                  
  G. Marsaglia et al., Stat. Prob. Lett. 9, 35 (1990).                
                                                                      
  Need to initialize the seeds for every processor differently, so    
  take a single seed and generate independent numbers on each         
  processor using a DIFFERENT random number generator, in this case,  
  the parallel linear congruential generator prand (see Fox et al.,   
  Solving Problems on Concurrent Processors).                         
                                                                      
  MUST be initialized with randinit() before any calls to rand().     
                                                                      
----------------------------------------------------------------------*/

public class Random { 
  

#define MULT  1103515245
#define ADD   12345

/*#define MASK ( 0x7fffffff )*/
#define MASK ( 123457 )

int  AAA, BBB;
int    randx;
double  u[98], c, cd, cm;
int     i97, j97, ivec;

void rmarin();
int irand();


/*--------------------------------------------------------------------*/
  

  static void randinit(CommVars cv) {

void randinit(seed)
     int seed;
{
  int irand();
  int  proc;
  int seed1, seed2;


  /* Initialize seeds for the random number generator irand, 
     so that every processor gets a different seed. */
  AAA = 1;
  BBB = 0;

  seed = seed % MASK;

  for (proc=0;proc<nprocs;++proc) {
    AAA = (MULT * AAA) % MASK;
    BBB = (MULT * BBB + ADD) % MASK;
    if (proc == procnum) {
      randx = (AAA*seed + BBB) % MASK;
    }
   
  }


  /* Initialize the random number generator ranmar using irand to
     generate the seeds, so that every processor gets different seeds. */
  seed1 = irand();
  seed2 = irand();


  rmarin(seed1, seed2); 


    

};


/*--------------------------------------------------------------------
                                                                      
  Linear congruential generator to return a random int in [0, 2^15).  
                                                                      
----------------------------------------------------------------------*/

int irand()
{

  randx = (AAA*randx+BBB)%MASK;
  
  return(randx);

};




/*--------------------------------------------------------------------
                                                                      
 This is the initialization routine RMARIN for the random number      
     generator RANMAR                                                 
                                                                      
 NOTE: The seed variables can have values between:  0 <= IJ <= 31328  
                                                    0 <= KL <= 30081  
----------------------------------------------------------------------*/


void rmarin(ij, kl)
     int ij, kl;
{
  int i, ii, j, jj, k, l, m;
  double t, s;


  if( ij < 0  ||  ij > 31328 )
        ij = ij % (31328+1);

  if( kl < 0  ||  kl > 30081 ) 
        kl = kl % (30081+1);

  i = ((ij/177)% 177) + 2;
  j = (ij%177) + 2;
  k = ((kl/169)% 178) + 1;
  l =  kl%169;


  for ( ii = 1 ; ii <= 97; ii++ ){
    s = 0.0;
    t = 0.5;
    for ( jj = 1 ; jj <= 24; jj++ ){
      m = (((i*j)% 179)*k)% 179;
      i = j;
      j = k;
      k = m;
      l = (53*l+1) % 169;
      if ( ((l*m)%64) >= 32) 
	s = s + t;
            
      t = 0.5 * t;
    }
    u[ii] = s;
  }

  c = 362436.0 / 16777216.0;
  cd = 7654321.0 / 16777216.0;
  cm = 16777213.0 /16777216.0;
  i97 = 97;
  j97 = 33;



};





/*-----------------------------------------------------------------
                                                                    
  Lagged Fibonacci random number generator RANMAR().                  
                                                                      
-------------------------------------------------------------------*/

double prand()
{
  double uni;
      
  uni = u[i97] - u[j97];
  if( uni < 0.0 ) uni = uni + 1.0;
  u[i97] = uni;
  i97 = i97 - 1;
  if(i97 == 0) i97 = 97;
  j97 = j97 - 1;
  if(j97 == 0) j97 = 97;
  c = c - cd;
  if( c < 0.0 ) c = c + cm;
  uni = uni - c;
  if( uni < 0.0 ) uni = uni + 1.0;
  
  /* printf("\n proc=%d,  rand=%lf",procnum,uni); */

  return(uni);
  

};



