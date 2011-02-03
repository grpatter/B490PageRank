#include <stdio.h>

#define M          1000000.

void table_top(int npes, int n_init, int n_mess, int samples, double t_call,
                char *Title)
{
       printf("\n\n %s\n",Title);
 
       printf(  "\n Number of PEs:  %8d",npes);

       printf("\n\n Starts:         %8d",n_init);
       printf(  "\n Messages:       %8d",n_mess);
       printf(  "\n Samples:        %8d",samples);
       printf(  "\n Timer overhead: %10.5f  microsecond\n",t_call*M);
       printf(  "\n             Length       Time         Rate   ");
       printf(  "\n Slave      [Bytes]  [Microsec.]    [Mbyte/s] ");
       printf(  "\n -----   ----------  -----------   ---------- \n");
}

void table_body(int islave, int n_byte, int n_init, int n_mess, int samples,
         double t_call, double *tf, FILE *ifp)
{
    double t_last;
    int    ns;

    t_last  = (tf[samples]-tf[samples-1])-t_call;
    /*printf(" %5d %12d %14.6f %14.8f\n",islave,n_byte,
                     t_last*M, n_mess*n_byte/(t_last*M)); */

    printf(" %5d  %12d   %f   %f\n",islave,n_byte,
	   (t_last*M)/n_mess , n_mess*n_byte/(t_last*M));

    fprintf(ifp,"\n%10d %10d %10d %10d %10d\n",
                    islave,n_byte,samples,n_init,n_mess);
    for (ns = 1; ns <= samples; ++ns) {
    fprintf(ifp,"%10d %g\n",ns,tf[ns]-tf[ns-1]-t_call);
    }
}
