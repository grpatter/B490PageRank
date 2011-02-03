/*
 * File         : ObjectWriteBuffer.java
 * Author       : Sang Lim
 * Created      : Fri Feb 22 14:21:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */
package mpjdev;

import java.io.*;

public class ObjectWriteBuffer extends WriteBuffer {

    public ObjectWriteBuffer(int capacity) {
        super(capacity);
    }

    private ObjectOutputStream out;
    private ByteArrayOutputStream byteOut;

    /**
     * Empty the buffer.
     */

    public void clear(){

        if (out != null) {
            try { 
                out.flush();
                out.close();
                out = null;
            } catch (IOException e){
                // IO error should not occure when we close 
                // byte array inputstream.
            }
        }
        
        super.clear();
    }

    /**
     * Write a section to the buffer containing `numEls' Object
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD
     * </pre>
     * units of buffer capacity (note the serialized objects themselves
     * are always stored in a separate, dynamically allocated area,
     * and are not consider to occupy buffer capacity).
     *
     * @exception IOException 
     *           InvalidClassException, NotSerializableException are possible.
     */
    public void write(Object [] source, int srcOff, int numEls)
            throws IOException {

        writeObjectHeader(numEls);

        if (out == null){
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
        }
        
        for (int i = srcOff; i < numEls; i++)
            out.writeObject(source[i]);     
    }

    /**
     * Write a section to the buffer containing Object elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD
     * </pre>
     * units of buffer capacity.
     *
     * @exception IOException 
     *           InvalidClassException, NotSerializableException are possible.
     *
     * @see #gather(float [],int,int,int[]) float version.
     */

    public void gather(Object [] source, 
                       int numEls, int offs, int [] indexes)
            throws IOException {

        writeObjectHeader(numEls);

        if (out == null){
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
        }
        
        for (int i = 0; i < numEls; i++)
            out.writeObject(source[indexes[offs + i]]);     
    }

    /**
     * Write a section to the buffer containing Object elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD
     * </pre>
     * units of buffer capacity.
     *
     * @exception IOException 
     *           InvalidClassException, NotSerializableException are possible.
     *
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public void strGather(Object [] source, int srcOff,
                          int rank, int exts, int strs, int [] indexes)
            throws IOException{

        int volume = 1;
        for (int i = 0; i < rank; i++){
            /* calculate 'volum' which is described above.*/
            volume *= indexes[exts + i];
        }

        writeObjectHeader(volume);

        if (out == null){
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
        }
        
        objectStrGather(source, srcOff, rank, exts, strs, indexes);
    }

    private void objectStrGather(Object [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes)
            throws IOException{

        if (rank == 0)
            out.writeObject(source[srcOff]);    
        else{
            int str = indexes[strs];
            for (int i = 0; i < indexes[exts]; i++)
                objectStrGather(source, srcOff + str * i, 
                                rank - 1, exts + 1, strs + 1, indexes); 
        }
    }

    private native void writeObjectHeader(int numEls);

    public void free(){

        if (out != null) {
            try { 
                out.flush();
                out.close();
            } catch (IOException e){
                // IO error should not occure when we close 
                // byte array inputstream.
            }
        }
        
        super.free();
    }

    /**
     * Create byte array for serialized Java Object type.
     * This mehod should be called before send Java Object type.
     */
    public void flush(){
        if (out == null)
            byte_buf = null;
        else 
            byte_buf = byteOut.toByteArray();
    }
}
