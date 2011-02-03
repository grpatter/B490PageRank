/****************************************************************************

 MESSAGE PASSING INTERFACE TEST CASE SUITE

 Copyright IBM Corp. 1995

 IBM Corp. hereby grants a non-exclusive license to use, copy, modify, and
 distribute this software for any purpose and without fee provided that the
 above copyright notice and the following paragraphs appear in all copies.

 IBM Corp. makes no representation that the test cases comprising this
 suite are correct or are an accurate representation of any standard.

 In no event shall IBM be liable to any party for direct, indirect, special
 incidental, or consequential damage arising out of the use of this software
 even if IBM Corp. has been advised of the possibility of such damage.

 IBM CORP. SPECIFICALLY DISCLAIMS ANY WARRANTIES INCLUDING, BUT NOT LIMITED
 TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS AND IBM
 CORP. HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

****************************************************************************

 These test cases reflect an interpretation of the MPI Standard.  They are
 are, in most cases, unit tests of specific MPI behaviors.  If a user of any
 test case from this set believes that the MPI Standard requires behavior
 different than that implied by the test case we would appreciate feedback.

 Comments may be sent to:
    Richard Treumann
    treumann@kgn.ibm.com

****************************************************************************

 MPI-Java version :
    Sung-Hoon Ko(shko@npac.syr.edu)
    Northeast Parallel Architectures Center at Syracuse University
    03/22/98

****************************************************************************/

import mpi.*;

class attr {
  static public void main(String[] args) throws MPIException {
    
    int rc,me,tasks,i,val,key,temp, valdata;
    boolean flag;
    
    final int TAG_UB = (1<<30)-1;
    
    
    MPI.Init(args);
    me = MPI.COMM_WORLD.Rank();
    tasks = MPI.COMM_WORLD.Size();



    val = MPI.COMM_WORLD.Attr_get(MPI.TAG_UB);
    if(val != TAG_UB) {
      System.out.println
	("WARNING in MPI.Attr_get: tag_ub = "+val+", expected be "+TAG_UB);
      System.out.println("This is an error only if tag_ub < 32767");
    }


    
    val = MPI.COMM_WORLD.Attr_get(MPI.HOST);

    //if(val != MPI.PROC_NULL)
    //  System.out.println
    //	("ERROR in MPI_Attr_get: host = "+val+", should be "+MPI.PROC_NULL);
         // Don't believe the MPI spec requires this.  dbc.

    val = MPI.COMM_WORLD.Attr_get(MPI.IO);
    if(val != MPI.ANY_SOURCE)
      System.out.println
	("ERROR in MPI.Attr_get: io = "+val+", should be "+MPI.ANY_SOURCE);


    if(me == 0)  System.out.println("Attr TEST COMPLETE\n");
    MPI.Finalize();
  }
}

// Things to do
//
//   This doesn't make too much sense to me.  Attribute values should be
//   permitted to be implementation-dependent, otherwise what is the point?
//     dbc

