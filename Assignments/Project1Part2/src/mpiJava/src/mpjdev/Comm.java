/*
 * File         : Comm.java
 * Author       : Sang Lim
 * Created      : Thu Jan 17 17:15:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

/**
  * The communicator class.  Directly analogous to an MPI communicator.
  */
public class Comm {

    public static final int MODEL_MULTIPROCESS = 0;

    public static final int MODEL_MULTITHREADED = 1;

    /*
     * Note this can be called before `Comm.init()', thus before
     * libraries have been loaded.  This is convenient because it
     * doesn't require `java.library.path' or equivalent to be set up
     * if we are only calling this method (handy in scripts that use
     * `SpmdEnv.main' to test model).
     */
    public static int getModel() {

        return MODEL_MULTIPROCESS;
    }

    /**
     * Number of processes spanned by this communicator.
     */

    public native int size();

    /**
     * Id of current process relative to this communicator.
     * Equivalent to MPI_COMM_RANK.
     */

    public native int id();

    /**
     * Create a new communicator the spanning the same
     * set of processes, but with a distinct communication context.
     */

    public native Comm dup();

    /**
     * Create a new communicator the spanning the set of processes
     * selected by the `ids' array (containing ids are relative to this
     * communicator).
     * The new communicator also has a distinct communication context.
     * Processes which are out side of the group will return null.
     */

    public native Comm create(int [] ids);

    /**
     * Destroy this communicator.
     * Java binding of the MPI operation MPI_COMM_FREE.
     */
    
    public native void free();
        
    /**
     * Blocking send of message containing the contents of `buf'.
     * Equivalent to MPI_SEND
     */

    public native void send(Buffer buf, int dest, int tag);

    /**
     * Blocking receive of message, whose contents are copied to `buf'.
     * The capacity of `buf' must be large enough to accept these
     * contents.  Initializes the `source' and `tag' fields of the
     * returned `Status'.  Equivalent to MPI_RECV.
     */

    public native Status recv(Buffer buf, int src, int tag);

    /**
     * Non-blocking version of `send'.
     * Equivalent to MPI_ISEND
     */

    public native Request isend(Buffer buf, int dest, int tag);

    /**
     * Non-blocking version of `recv'.
     * Equivalent to MPI_IRECV
     */

    public native Request irecv(Buffer buf, int src, int tag);

    /**
     * Initialize MPI.
     * <p>
     * <table>
     * <tr><td><tt> args </tt></td><td> arguments to <tt>main</tt> method.</tr>
     * </table>
     */
    
    //public static String [] init(String[] args) {
    //    String [] newArgs = initNative(args);
    //    
    //    return newArgs ;
    //}
    //
    //private static native String [] initNative(String[] args);

    public static String [] init(String[] args) {

        String [] result = null ;

        try {
            result = mpi.MPI.Init(args) ;
        }
        catch(mpi.MPIException e) {
            System.err.println(e.getMessage()) ;
            e.printStackTrace() ;
            System.exit(1) ;
        }

        init();  // Implemented in `libmpijava'.
                 // Must appear after MPI has been loaded.

        return result ;
    }

    /**
     * Finalize MPI.
     */

    //public static native void finish();

    public static void finish() {
        
        try {
            mpi.MPI.Finalize() ;
        }
        catch(mpi.MPIException e) {
            System.err.println(e.getMessage()) ;
            e.printStackTrace() ;
            System.exit(1) ;
        }
    }

    /**
     * Equivalent of MPI_ANY_SOURCE.
     * May be passed as `src' argument of `recv' or `irecv'.
     */

    public static final int ANY_SOURCE = -2;

    /**
     * Equivalent of MPI_ANY_TAG.
     * May be passed as `tag' argument of `recv' or `irecv'.
     */

    public static final int ANY_TAG = -1;

    /**
     * The initial communicator.
     * Equivalent of MPI_COMM_WORLD.
     */

    public static final Comm WORLD = new Comm();

    static int MAX_PROCESSOR_NAME = 256;

    long handle;

    //static {
    //    
    //    // Native call to `init()', below, is implemented in
    //    // `libmpijava'.
    //
    //    // Arbitrary call into `mpi.MPI' here forces that class,
    //    // and thus `libmpijava', to be loaded.
    //
    //    try {
    //        mpi.MPI.Initialized() ;
    //    }
    //    catch(mpi.MPIException e) {
    //        System.err.println(e.getMessage()) ;
    //        e.printStackTrace() ;
    //        System.exit(1) ;
    //    }
    //
    //    init();
    //}
    
    
    private static native void init();
}



