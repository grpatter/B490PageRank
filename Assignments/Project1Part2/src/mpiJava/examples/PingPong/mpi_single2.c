#include <stdio.h>
#include <stdlib.h>
/*#include <windows.h>*/

#include "mpi.h"

#define PINGPONGS         16
#define SAMPLES           16
#define LOG2N_MAX         19

#define N_MAX             (1<<LOG2N_MAX-3)

/*
LARGE_INTEGER StartTime;
LARGE_INTEGER GetStartTime();
void InitTimer();
*/
double timer();
void table_top(), table_body();

int main(int argc, char * argv[])
{
    
 
    double     tf[SAMPLES + 1], t_call;
    int        i, j_pe, log2nbyte, nbyte, ns, n_pp, my_pe, npes;
    int        n_init, n_mess;
    double     *A;
    char       *Title ="Single Messages(Double type) --- MPICH_C on Solaris";
    FILE       *ifp;

    MPI_Status status;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_pe);
    MPI_Comm_size(MPI_COMM_WORLD, &npes);

    /*StartTime = GetStartTime();*/ /* Get StartTime */

    tf[0] = MPI_Wtime();
    for (ns = 1; ns <= SAMPLES; ++ns) {
       tf[ns] = MPI_Wtime();
    }
    t_call = (tf[SAMPLES] - tf[0]) / SAMPLES;

    printf( " %16.12f \n ", t_call);

    n_init = 2 * PINGPONGS;
    n_mess = 2 * PINGPONGS;

    if (my_pe == 0) {
       ifp = fopen("mpi_single_C.dat","w");
       table_top(npes, n_init, n_mess, SAMPLES, t_call, Title);
    }

    for (log2nbyte = 0; log2nbyte <= LOG2N_MAX; ++log2nbyte) {
       nbyte = (1<< log2nbyte);

       if ( (A  = (double *) malloc(nbyte * sizeof(double))) == NULL ) {
         printf( "Insufficient memory available\n" );
         exit(1);
	   }

       for (i = 0; i < nbyte; i++) {
         A[i] = (double) 1. / (i + 1);
	   }

	   
	   for (j_pe = 1; j_pe <= npes-1; ++j_pe) {
          MPI_Barrier(MPI_COMM_WORLD);
          tf[0] = MPI_Wtime();
          for (ns = 0; ns < SAMPLES; ns++) {
             for (n_pp = 0; n_pp < PINGPONGS; n_pp++) {
                if (my_pe == j_pe) {
		  /*printf("my PE = %d \n", my_pe);*/
                   MPI_Send(A,nbyte,MPI_DOUBLE,0,10,MPI_COMM_WORLD);
                   MPI_Recv(A,nbyte,MPI_DOUBLE,0,20,MPI_COMM_WORLD, &status);
                }
                if (my_pe == 0) {
		  /*printf("my PE = %d \n", my_pe);*/
                   MPI_Recv(A,nbyte,MPI_DOUBLE,j_pe,10,MPI_COMM_WORLD,&status);
                   MPI_Send(A,nbyte,MPI_DOUBLE,j_pe,20,MPI_COMM_WORLD);
                }
             }
          tf[ns+1] = MPI_Wtime();
          }
          if (my_pe == 0)
             table_body(j_pe, nbyte, n_init, n_mess, SAMPLES, t_call, tf, ifp);
       }

	   free(A);

    }

    printf("\n Finished \n");
    MPI_Finalize();

    return 0;
}

