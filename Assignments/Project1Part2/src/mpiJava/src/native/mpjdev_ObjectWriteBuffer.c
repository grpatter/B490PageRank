/*
 * File         : mpjdev_ObjectWriteBuffer.c
 * Author       : Sang Lim
 * Created      : Fri Feb 22 14:29:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */

#include "mpjdev_ObjectWriteBuffer.h"
#include "buff_typeDef.h"
#include <inttypes.h>

extern jfieldID mpjdev_jBufferId;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

/*
 * Class:     mpjdev_ObjectWriteBuffer
 * Method:    writeObjectHeader
 * Signature: (I)V
 *
 * Write a section to the buffer containing `numEls' Object
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD' units of buffer capacity 
 * (note the serialized objects themselves
 * are always stored in a separate, dynamically allocated area,
 * and are not consider to occupy buffer capacity).
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ObjectWriteBuffer_writeObjectHeader(JNIEnv * env, 
                                                     jobject jthis, 
                                                     jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId);
  
  int newSize = pBuffer-> size + MPJDEV_HEADER_SIZE;
  if (pBuffer-> capacity < newSize){
    char str[100];
    sprintf(str, "In method 'Write', buffer capacity is too short.\n"
      "Buffer capacity: %d.\n"
      "Trying to write: %d", pBuffer-> capacity, newSize);
    mpjdev_arrayIndexOutOfBoundsException(env, str);
    return;
  } else {
    
    char *target       = pBuffer-> message + pBuffer-> size + MPJDEV_HEADER_SIZE;
    target[0]          = (char)MPJDEV_OBJECT;
    ((jint*)target)[1] = numEls;
    
    pBuffer-> size = newSize;
  }
}


