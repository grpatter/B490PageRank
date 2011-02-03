/*
 * File         : WriteBuffer.java
 * Author       : Sang Lim
 * Created      : Mon Jan 28 14:54:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */
package mpjdev;

import java.io.*;

public class WriteBuffer extends Buffer {

    public WriteBuffer(int capacity) {
        
        super(capacity);
    }

    public native void clear();

    /**
     * Write a section to the buffer containing `numEls' float
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * numEls
     * </pre>
     * units of buffer capacity.
     */
    
    public native void write(float [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' double
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(double [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' int
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(int [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' boolean
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(boolean [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' byte
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(byte [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' char
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(char [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' long
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(long [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing `numEls' short
     * elements in `source', starting at `srcOff'.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void write(short [] source, int srcOff, int numEls);

    /**
     * Write a section to the buffer containing float elements from
     * an indexed section of the array `source'.  The indexes are
     * <pre>
     *   (indexes [offs], indexes [offs + 1], ..., indexes [offs + numEls - 1])
     * </pre>
     * This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * numEls
     * </pre>
     * units of buffer capacity.
     */

    public native void gather(float [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing double elements from
     * an indexed section of the array `source'. 
     * Similar to the float version above.
     * This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(double [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing int elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(int [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing boolean elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(boolean [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing byte elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(byte [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing char elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(char [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing long elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(long [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing short elements from
     * an indexed section of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * numEls
     * </pre>
     * units of buffer capacity.
     * @see #gather(float [],int,int,int[]) float version.
     */

    public native void gather(short [] source, 
                              int numEls, int offs, int [] indexes);

    /**
     * Write a section to the buffer containing float elements from
     * a multi-dimensional, strided patch of the array `source'.
     * The rank of the patch is `rank', its shape is:
     * <pre>
     *   (indexes [exts], indexes [exts + 1], ..., indexes [exts + rank - 1])
     * </pre>
     * and the strides within the `source' array associated with each
     * dimension of the patch are:
     * <pre>
     *   (indexes [strs], indexes [strs + 1], ..., indexes [strs + rank - 1])
     * </pre>
     * This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * volume
     * </pre>
     * units of buffer capacity, where `volume' is the product of
     * the extents (the elements of the shape vector).
     */

    public native void strGather(float [] source, int srcOff, 
                              int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing double elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(double [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing int elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 4 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(int [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing boolean elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(boolean [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing byte elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 1 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(byte [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);
    
    /**
     * Write a section to the buffer containing char elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(char [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing long elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 8 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(long [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);

    /**
     * Write a section to the buffer containing chort elements from
     * a multi-dimensional, strided patch of the array `source'.
     * Similar to the float version above.  This requires
     * <pre>
     *     SECTION_OVERHEAD + 2 * volume
     * </pre>
     * units of buffer capacity.
     * @see #strGather(float [], int, int, int, int, int[]) float version.
     */

    public native void strGather(short [] source, int srcOff,
                                 int rank, int exts, int strs, int [] indexes);
}
