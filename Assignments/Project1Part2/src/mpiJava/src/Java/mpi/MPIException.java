/*
 * File         : MPIException.java
 * Author       : Bryan Carpenter
 * Created      : Tue Sep 14 13:03:57 EDT 1999
 * Revision     : $Revision: 1.1 $
 * Updated      : $Date: 1999/09/14 22:01:52 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1999
 */

package mpi;

public class MPIException extends Exception {
  public MPIException() {super() ;}
  public MPIException(String message) {super(message) ;}
}

