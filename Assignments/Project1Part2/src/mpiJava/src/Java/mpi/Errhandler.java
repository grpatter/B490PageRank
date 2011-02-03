/*
 * File         : Errhandler.java
 * Author       : Xinying Li
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2001/08/07 16:36:25 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

package mpi;
//import mpi.*;

public class Errhandler{
  public final static int FATAL = 1;
  public final static int RETURN = 0;

  private static native void init();

  //public Errhandler() {}
  public Errhandler(int Type) { GetErrhandler(Type);}
  public Errhandler(long _handle) { handle = _handle;}  

  protected native void GetErrhandler(int Type);
  
  protected long handle;

  static {
    init();
  }

}
