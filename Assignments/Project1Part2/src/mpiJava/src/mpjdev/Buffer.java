/*
 * File         : Buffer.java
 * Author       : Sang Lim
 * Created      : Thu Jan 17 17:31:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;
import java.io.*;

/**
 * Buffer object, containing a message.
 *
 * A message is considered to consist of zero or more "sections".
 * Each section is consists of a sequence of elements which
 * either all have the same primitive type, or all have Object type.
 * Each section is written or read in a single `write', `gather',
 * `read' or `scatter' operation.  The sections can only be written
 * or read in fixed order starting from the beginning of the buffer.
 * At any time a buffer is considered to have a fixed capacity.
 * Each section is preceded by a header occupying SECTION_OVERHEAD
 * units of buffer capacity.  The total space occupied by the message
 * must be less than or equal to the current buffer capacity.
 */
public class Buffer {
  
    protected Buffer(int capacity) {
        nativeBuffer(capacity);
    }

    private native void nativeBuffer(int capacity);

    /**
     * Temporarily increase the buffer capacity (if necessary), so it is
     * at least `newCapacity'. Clears data from the buffer.
     */

    public native void ensureCapacity(int newCapacity);

    /**
     * Subsequent to one or more calls to `ensureCapacity': restore the buffer
     * capacity to its original value, and free any extra storage
     * that was temporarily allocated. Clears data from the buffer.
     */

    public native void restoreCapacity();

    public native void free();

    public static final int SECTION_OVERHEAD = 8 ;
    public static final int MAX_SECTION_PADDING = 7;

    static {
        //if (!Comm.isLoaded) {
        //
        //    System.loadLibrary("savesignals");
        //    Comm.saveSignalHandlers();
        //
        //    System.loadLibrary("mpjdev");
        //
        //    Comm.restoreSignalHandlers();
        //
        //    Comm.isLoaded = true;
        //}
        
        System.loadLibrary("mpjbuf") ;

        setEndian();
    }

    private static native void setEndian();

    long buffId;

    byte[] byte_buf;

    byte[] getObjectByteArray(){
        
        return byte_buf;
    }

}

