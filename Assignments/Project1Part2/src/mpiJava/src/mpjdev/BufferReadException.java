/*
 * File         : BufferReadException.java
 * Author       : Sang Lim
 * Created      : Mon Feb 04 22:07:42 2001
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

public class BufferReadException extends java.io.IOException{

    public BufferReadException(){
        super();
    }

    public BufferReadException(String s) {
             
        super(s);
    }
}
