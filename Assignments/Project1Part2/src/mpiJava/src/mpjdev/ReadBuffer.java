/*
 * File         : ReadBuffer.java
 * Author       : Sang Lim
 * Created      : Mon Jan 28 14:56:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

public class ReadBuffer extends Buffer {

    public ReadBuffer(int capacity){
        super(capacity);
    }

    public native void reset();

    /**
     * Read a section from the buffer containing exactly `numEls' float
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(float [] dest, int dstOff, int numEls) 
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' double
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(double [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' int
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(int [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' boolean
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(boolean [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' byte
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(byte [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' char
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(char [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' long
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(long [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' short
     * elements into the array `dest', starting at `dstOff'.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of destination array is too short.
     */

    public native void read(short [] dest, int dstOff, int numEls)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' float
     * elements into an indexed section of the array `dest'.  The indexes are
     * <pre>
     *   (indexes [offs], indexes [offs + 1], ..., indexes [offs + numEls - 1])
     * </pre>
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     */

    public native void scatter(float [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' double
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(double [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' int
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(int [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' boolean
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(boolean [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' byte
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(byte [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;
    /**
     * Read a section from the buffer containing exactly `numEls' char
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(char [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' long
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(long [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `numEls' short
     * elements into an indexed section of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #scatter(float [], int, int, int[]) float version.
     */

    public native void scatter(short [] dest,
                               int numEls, int offs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' float
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * The rank of the patch is `rank', its shape is:
     * <pre>
     *   (indexes [exts], indexes [exts + 1], ..., indexes [exts + rank - 1])
     * </pre>
     * and the strides within the `source' array associated with each
     * dimension of the patch are:
     * <pre>
     *   (indexes [strs], indexes [strs + 1], ..., indexes [strs + rank - 1])
     * </pre>
     * and the `volume' referred to above is the product of
     * the extents (the elements of the shape vector).
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     */

    public native void strScatter(float [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' double
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(double [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' int
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(int [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' boolean
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(boolean [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' byte
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(byte [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' char
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(char [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' long
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(long [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;

    /**
     * Read a section from the buffer containing exactly `volume' short
     * elements into a multi-dimensional, strided patch of the array `dest'.
     * Similar to the float version above.
     *
     * @exception BufferReadException
     *              If type or section size mismatches.
     *
     * @exception ArrayIndexOutofBounds
     *              If size of indexes array is too short or 
     *              if one of the index point out side of the dstination array.
     *
     * @see #strScatter(float [], int, int, int, int, int[]) float version.
     */

    public native void strScatter(short [] dest, int dstOff,
                               int rank, int exts, int strs, int [] indexes)
            throws BufferReadException;
}



