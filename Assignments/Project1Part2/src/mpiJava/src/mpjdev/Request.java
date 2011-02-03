/*
 * File         : Request.java
 * Author       : Sang Lim
 * Created      : Thu Jan 17 17:17:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

/**
 * Request object for non-blocking communications.
 */

package mpjdev;

public class Request {

    long requestId;

    int typeTag = 0;  // Hold information about primitive or object type.
                      // 0 means primitivetype. 1 means object type.

    int opTag ;  // Hold status information for send or recv.
                 // 0 means send. 1 means recv.

    Request hdrReq;  // Hold object type header information.
                     // Used by 'Isend'.

    int tag;     // Hold 'irecv' tag information. 
                 // Used when recv object byte array.

    long comm;   // Hold 'irecv' comm information.
                 // Used when recv object byte array.

    Buffer buf;  // ReadBuffer which will hold object byte array.

    /**
     * Wait for a single non-blocking communication to complete.
     * If this was a receive, initializes the `source' and `tag' fields
     * of the returned status object.
     * Equivalent to MPI_WAIT.
     */

    public native Status iwait();

    /**
     * Wait for one non-blocking communication from a set to complete.
     * The `index' field of the returned status object defines which
     * communication in the `reqs' array was selected.
     * If this was a receive, the `source' and `tag' fields
     * of the returned status object are also initialized.
     * Equivalent to MPI_WAITANY.
     */

    public static native Status iwaitany(Request [] reqs);

    public static final Request MPI_REQUEST_NULL = new Request();
}
