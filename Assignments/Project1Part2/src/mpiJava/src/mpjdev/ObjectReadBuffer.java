/*
 * File         : ObjectReadBuffer.java
 * Author       : Sang Lim
 * Created      : Fri Feb 22 14:02:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

import java.io.*;

public class ObjectReadBuffer extends ReadBuffer {

    public ObjectReadBuffer(int capacity){
        super(capacity);
    }

    ObjectInputStream in;

    /**
     * Reset the read pointer to the start of the buffer.
     */
    
    public void reset() {

        if (in != null) {
            try {
                in.close();
                in = null;
            } catch(IOException e){
                // IO error should not occure when we close 
                // byte array inputstream.
            }
        } 

        super.reset();
    }

    /**
     * Read a section from the buffer containing exactly `numEls' Object
     * elements into the array `dest', starting at `dstOff'.
     */

    public void read(Object [] dest, int dstOff, int numEls) 
            throws IOException, ClassNotFoundException{

        readObjectHeader(numEls);
        if (in == null){
            ByteArrayInputStream o = new ByteArrayInputStream(byte_buf);
            in = new ObjectInputStream(o);
        }

        for (int i = 0; i < numEls; i++)
            dest[dstOff + i] = in.readObject();     
    }

    /**
     * Read a section from the buffer containing exactly `numEls' Object
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public void scatter(Object [] dest,
                        int numEls, int offs, int [] indexes)
            throws IOException, ClassNotFoundException{

        readObjectHeader(numEls);

        if (in == null){
            ByteArrayInputStream o = new ByteArrayInputStream(byte_buf);
            in = new ObjectInputStream(o);
        }
        
        for (int i = 0; i < numEls; i++)
            dest[indexes[offs + i]] = in.readObject();
    }

    /**
     * Read a section from the buffer containing exactly `volume' object
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception IOException
     *              
     * @exception ClassNotFoundException
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public void strScatter(Object [] dest, int dstOff,
                           int rank, int exts, int strs, int [] indexes)
            throws IOException, ClassNotFoundException{

        int volume = 1;
        for (int i = 0; i < rank; i++){
            /* calculate 'volum' which is described above.*/
            volume *= indexes[exts + i];
        }
        readObjectHeader(volume);

        if (in == null){
            ByteArrayInputStream o = new ByteArrayInputStream(byte_buf);
            in = new ObjectInputStream(o);
        }
        
        objectStrScatter(dest, dstOff, rank, exts, strs, indexes);
    }

    private void objectStrScatter(Object [] dest, int dstOff,
                                 int rank, int exts, int strs, int [] indexes)
            throws IOException, ClassNotFoundException {

        if (rank == 0)
            dest[dstOff] = in.readObject();
        else {
            for (int i = 0; i < indexes[exts]; i++)
                objectStrScatter(dest, dstOff + indexes[strs] * i, 
                                 rank - 1, exts + 1, strs + 1, indexes); 
        }
    }

    private native void readObjectHeader(int numEls);

    public void free(){

        if (in != null) {
            try { 
                in.close();
            } catch (IOException e){
                // IO error should not occure when we close 
                // byte array inputstream.
            }
        }

        super.free();
    }
}
