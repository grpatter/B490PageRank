
/*--------------------------------------------------------------------
                                                                      
   Extra global variables for the Swendsen-Wang program.              
                                                                      
----------------------------------------------------------------------*/

public class SWVars {
 

	int   new_spin[MAX_SITES+1];
	int   label[MAX_SIDE+2][MAX_SIDE+2];
	int   Npoint[MAX_SITES+1];
	int   ibond[MAX_SIDE+2][MAX_SIDE+2];
	int   jbond[MAX_SIDE+2][MAX_SIDE+2];
        int   sendi[2*MAX_SIDE], sendj[2*MAX_SIDE];
        int   recvi[2*MAX_SIDE], recvj[2*MAX_SIDE];
	
	int   offset, minits;


}
