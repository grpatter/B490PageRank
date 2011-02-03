/*
 * File         : mpjdev_Buffer.c
 * Author       : Sang Lim
 * Created      : Sun Jan 20 17:31:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */

#include "mpjdev_Buffer.h"
#include "buff_typeDef.h"
#include <inttypes.h>

jfieldID mpjdev_jBufferId;

enum encode mpjdev_encoding;

/*
const int MPJDEV_HEADER_SIZE = 8;
*/

/*
 * Class:     mpjdev_Buffer
 * Method:    setEndian
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Buffer_setEndian (JNIEnv * env, jclass jObject)
{
  int testValue = 0x01020304;
  char *testptr = (char*) &testValue;
  if (testptr[0] == 01)
    mpjdev_encoding = MPJDEV_BIG_ENDIAN;
  else 
    mpjdev_encoding = MPJDEV_LITTLE_ENDIAN;

  mpjdev_jBufferId = (*env)->GetFieldID(env, jObject, "buffId", "J");
}

/*
 * Class:     mpjdev_Buffer
 * Method:    nativeBuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Buffer_nativeBuffer (JNIEnv * env, jobject jthis, 
                                      jint capacity)
{
  Buffer *pBuffer = (Buffer*) malloc(sizeof(Buffer));

  pBuffer -> originalCapacity = capacity;
  pBuffer -> capacity         = capacity;
  pBuffer -> size             = 0;
  pBuffer -> readPtr          = 0;

  /*put extra space for primary and secondary header*/
  pBuffer -> message    = (char*)malloc(capacity + 2 * MPJDEV_HEADER_SIZE);
  pBuffer -> message[0] = (char)mpjdev_encoding;

  (*env)->SetLongField(env, jthis, mpjdev_jBufferId, (long)pBuffer);
}

/*
 * Class:     mpjdev_Buffer
 * Method:    ensureCapacity
 * Signature: (I)V
 * Temporarily increase the buffer capacity (if necessary), so it is
 * at least `newCapacity'.  Clears data from the buffer.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Buffer_ensureCapacity (JNIEnv * env, jobject jthis, 
                                   jint newCapacity)
{

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, jthis, 
                                                             mpjdev_jBufferId);

  if (newCapacity > pBuffer -> capacity){
    pBuffer -> capacity = newCapacity;

    free(pBuffer -> message);
    /*put extra space for primary and secondary header*/
    pBuffer -> message    = (char*)malloc(newCapacity + 2 * MPJDEV_HEADER_SIZE);
  }

  pBuffer -> size     = 0;
  pBuffer -> readPtr  = 0;

  pBuffer -> message[0] = (char)mpjdev_encoding;
}

/*
 * Class:     mpjdev_Buffer
 * Method:    restoreCapacity
 * Signature: ()V
 * Subsequent to one or more calls to `ensureCapacity': restore the buffer
 * capacity to its original value, and free any extra storage
 * that was temporarily allocated. Clears data from the buffer.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Buffer_restoreCapacity (JNIEnv * env, jobject jthis)
{

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, jthis, 
                                                             mpjdev_jBufferId);

  if ( pBuffer-> originalCapacity < pBuffer-> capacity) {

    pBuffer-> capacity = pBuffer-> originalCapacity;

    free(pBuffer-> message);
    /*put extra space for primary and secondary header*/
    pBuffer-> message     = (char*)malloc(pBuffer-> originalCapacity + 
                                          2 * MPJDEV_HEADER_SIZE);
  }

  pBuffer-> size     = 0;
  pBuffer-> readPtr  = 0;
  
  pBuffer -> message[0] = (char)mpjdev_encoding;
}

/*
 * Class:     mpjdev_Buffer
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Buffer_free (JNIEnv * env, jobject jthis)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  free(pBuffer-> message);
  free(pBuffer);
}

void mpjdev_arrayIndexOutOfBoundsException(JNIEnv * env, char* str)
{
  jclass arrayOutOfBound = 
    (*env)-> FindClass(env, "java/lang/ArrayIndexOutOfBoundsException");
  
  (*env)-> ThrowNew(env, arrayOutOfBound, str);

  return;
}

void mpjdev_outOfMemoryError(JNIEnv * env)
{
  jclass outOfMemory =
    (*env)-> FindClass(env, "java/lang/OutOfMemoryError");
  
  (*env)-> ThrowNew(env, outOfMemory, "Out of memory error.");

  return;
}

void mpjdev_typeMismatchException(JNIEnv * env, char* src, char* dst)
{

  jclass typeMismatch = 
    (*env)-> FindClass(env, "mpjdev/TypeMismatchException");
  
  char str[100];
  sprintf(str,"Trying to read %s array into %s array.\n",
          src, dst); 

  (*env)-> ThrowNew(env, typeMismatch, str);
  
  return;
}

void mpjdev_sizeMismatchException(JNIEnv * env, jint srcSize, jint dstSize)
{
  jclass sizeMismatch = 
    (*env)-> FindClass(env, "mpjdev/SectionSizeMismatchException");
  
  char str[100];
  sprintf(str, "Trying to read a section with different size of array.\n"
          "A section Size: %d\n Destination size: %d.\n", 
          srcSize,dstSize);

  (*env)-> ThrowNew(env, sizeMismatch, str);

  return;
}

char* mpjdev_getType(char typeNum)
{
  switch(typeNum) {
    case 0 : return "btye";
    case 1 : return "char";
    case 2 : return "short";
    case 3 : return "boolean";
    case 4 : return "int";
    case 5 : return "long";
    case 6 : return "float";
    case 7 : return "double";
    case 8 : return "Object";
    default: return " ";
  }
}

