/*
 * File         : TypeMismatchException.java
 * Author       : Sang Lim
 * Created      : Mon Feb 04 22:07:42 2001
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

public class TypeMismatchException extends BufferReadException{

    public TypeMismatchException(){
        super();
    }

    public TypeMismatchException(String s) {
             
        super(s);
    }
}
