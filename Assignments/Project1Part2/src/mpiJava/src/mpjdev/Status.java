/*
 * File         : Status.java
 * Author       : Sang Lim
 * Created      : Thu Jan 17 17:20:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:29 $
 */

/**
 * Status object describing a completed communication.
 */

package mpjdev;

public class Status {

    /**
     * For a receive operation, the source of the message.
     */

    public int source ;

    /**
     * For a receive operation, the tag in the message.
     */

    public int tag ;

    /**
     * For a `waitany()' operation in class `Request'.
     * This field defines which communication in the `reqs' array was selected.
     * @see Request#iwaitany(Request [] reqs)
     */

    public int index ;
}
