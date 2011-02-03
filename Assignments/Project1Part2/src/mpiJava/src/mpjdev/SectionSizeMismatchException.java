/*
 * File         : SectionSizeMismatchException.java
 * Author       : Sang Lim
 * Created      : Mon Feb 04 22:07:42 2001
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

package mpjdev;

public class SectionSizeMismatchException extends BufferReadException{

    public SectionSizeMismatchException(){
        super();
    }

    public SectionSizeMismatchException(String s) {
             
        super(s);
    }
}
