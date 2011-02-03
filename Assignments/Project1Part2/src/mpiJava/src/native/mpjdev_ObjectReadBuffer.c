/*
 * File         : mpjdev_ObjectReadBuffer.c
 * Author       : Sang Lim
 * Created      : Fri Feb 22 14:12:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */

#include "mpjdev_ObjectReadBuffer.h"
#include "buff_typeDef.h"
#include <inttypes.h>

extern jfieldID mpjdev_jBufferId;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

/*
 * Class:     mpjdev_ObjectReadBuffer
 * Method:    readObjectHeader
 * Signature: (I)V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ObjectReadBuffer_readObjectHeader (JNIEnv * env, 
                                                    jobject jthis, 
                                                    jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_OBJECT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "Object");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    if (((jint*)src)[1] != numEls) {
      mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);
      return;
    }
  } else {
    char eSize[4] = {src[7], src[6], src[5], src[4]};

    if (((jint*)eSize)[0] != numEls) {
      mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);
      return;
    }
  }

  pBuffer-> readPtr += 8;
}
