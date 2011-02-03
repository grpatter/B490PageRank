/*
 * File         : mpjdev_ReadBuffer.c
 * Author       : Sang Lim
 * Created      : Mon Jan 28 15:25:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */

#include "mpjdev_ReadBuffer.h"
#include "buff_typeDef.h"
#include <inttypes.h>

extern jfieldID mpjdev_jBufferId;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_reset (JNIEnv * env, jobject jthis)
{

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, jthis, 
                                                             mpjdev_jBufferId);
  pBuffer-> readPtr = 0;
}

/******************* READ METHODS ***********************************/

#define READBODY(T)                                                       \
int destSize = (*env)-> GetArrayLength(env, dest);                        \
                                                                          \
if (((jint*)src)[1] != numEls) {                                          \
  mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);                    \
  return;                                                                 \
}                                                                         \
                                                                          \
if (destSize < (dstOff + numEls)){                                        \
  /* 'dest' array is too short to hold all read elements.*/               \
  char str[100];                                                          \
  sprintf(str, "Size of destination array is too short.\n"                \
    "Size of destination array: %d.\nSize needed: %d", destSize,          \
    (dstOff + numEls));                                                   \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
  int isThereError = 0;                                                   \
  T* dst = (*env)->GetPrimitiveArrayCritical(env, dest, 0);               \
                                                                          \
  if (dst == 0) {                                                         \
    isThereError = 1;                                                     \
  } else {                                                                \
                                                                          \
    int i, byte_numEls = sizeof(T) * numEls;                              \
    T* psrc = (T*)(src + MPJDEV_HEADER_SIZE);                                    \
                                                                          \
    for (i = 0; i < numEls; i++)                                          \
      dst[i + dstOff] = psrc[i];                                          \
                                                                          \
    /* To ensure that the sections all start on 8-byte boundaries.*/      \
    /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                      \
    pBuffer-> readPtr += MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);        \
  }                                                                       \
                                                                          \
  (*env)->ReleasePrimitiveArrayCritical(env, dest, dst, JNI_ABORT);       \
                                                                          \
  if (isThereError)                                                       \
    mpjdev_outOfMemoryError(env);                                                \
}

#define ENCODEREADBODY(T, byteRev)                                        \
int destSize  = (*env)-> GetArrayLength(env, dest);                       \
char eSize[4] = {src[7], src[6], src[5], src[4]};                         \
                                                                          \
if (((jint*)eSize)[0] != numEls) {                                        \
  mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);                    \
  return;                                                                 \
}                                                                         \
                                                                          \
if (destSize < (dstOff + numEls)){                                        \
  /* 'dest' array is too short to hold all read elements.*/               \
  char str[100];                                                          \
  sprintf(str, "Size of destination array is too short.\n"                \
    "Size of destination array: %d.\nSize needed: %d", destSize,          \
    (dstOff + numEls));                                                   \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
  int isThereError = 0;                                                   \
  T* dst = (*env)->GetPrimitiveArrayCritical(env, dest, 0);               \
                                                                          \
  if (dst == 0) {                                                         \
    isThereError = 1;                                                     \
  } else {                                                                \
                                                                          \
    int i, byte_numEls = sizeof(T) * numEls;                              \
    char* pdst = (char*)(dst + dstOff);                                   \
                                                                          \
    for(i = 0; i < numEls; i++){                                          \
      char* psrc = src + i * sizeof(T) + MPJDEV_HEADER_SIZE;                     \
      byteRev                                                             \
    }                                                                     \
                                                                          \
    /* To ensure that the sections all start on 8-byte boundaries.*/      \
    /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                      \
    pBuffer-> readPtr += MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);        \
  }                                                                       \
                                                                          \
  (*env)->ReleasePrimitiveArrayCritical(env, dest, dst, JNI_ABORT);       \
                                                                          \
  if (isThereError)                                                       \
    mpjdev_outOfMemoryError(env);                                                \
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([FII)V
 *
 * Read a section from the buffer containing exactly `numEls' float
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3FII (JNIEnv * env,     jobject jthis,
                                         jfloatArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_FLOAT)
    mpjdev_typeMismatchException(env, mpjdev_getType(src[0]), "float");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jfloat)
  } else {
    ENCODEREADBODY(jfloat, *(pdst++) = psrc[3];
                           *(pdst++) = psrc[2];
                           *(pdst++) = psrc[1];
                           *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([DII)V
 *
 * Read a section from the buffer containing exactly `numEls' double
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3DII (JNIEnv * env,      jobject jthis,
                                         jdoubleArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_DOUBLE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "double");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jdouble)
  } else {
    ENCODEREADBODY(jdouble, *(pdst++) = psrc[7];
                            *(pdst++) = psrc[6];
                            *(pdst++) = psrc[5];
                            *(pdst++) = psrc[4];
                            *(pdst++) = psrc[3];
                            *(pdst++) = psrc[2];
                            *(pdst++) = psrc[1];
                            *(pdst++) = psrc[0];)
  } 
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([III)V
 *
 * Read a section from the buffer containing exactly `numEls' int
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3III (JNIEnv * env,   jobject jthis,
                                         jintArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_INT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "int");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jint)
  } else {
    ENCODEREADBODY(jint, *(pdst++) = psrc[3];
                         *(pdst++) = psrc[2];
                         *(pdst++) = psrc[1];
                         *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([ZII)V
 *
 * Read a section from the buffer containing exactly `numEls' boolean
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3ZII (JNIEnv * env,       jobject jthis,
                                         jbooleanArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BOOLEAN)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "boolean");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jboolean)
  } else {
    ENCODEREADBODY(jboolean, *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([BII)V
 *
 * Read a section from the buffer containing exactly `numEls' byte
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3BII (JNIEnv * env,    jobject jthis,
                                         jbyteArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BYTE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "byte");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jbyte)
  } else {
    ENCODEREADBODY(jbyte, *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([CII)V
 *
 * Read a section from the buffer containing exactly `numEls' char
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3CII (JNIEnv * env,    jobject jthis,
                                         jcharArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_CHAR)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "char");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jchar)
  } else {
    ENCODEREADBODY(jchar, *(pdst++) = psrc[1];
                          *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([JII)V
 *
 * Read a section from the buffer containing exactly `numEls' long
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3JII (JNIEnv * env,    jobject jthis,
                                         jlongArray dest, jint dstOff,  
                                         jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_LONG)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "long");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jlong)
  } else {
    ENCODEREADBODY(jlong, *(pdst++) = psrc[7];
                          *(pdst++) = psrc[6];
                          *(pdst++) = psrc[5];
                          *(pdst++) = psrc[4];
                          *(pdst++) = psrc[3];
                          *(pdst++) = psrc[2];
                          *(pdst++) = psrc[1];
                          *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    read
 * Signature: ([SII)V
 *
 * Read a section from the buffer containing exactly `numEls' short
 * elements into the array `dest', starting at `dstOff'.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_read___3SII (JNIEnv * env,    jobject jthis,
                                    jshortArray dest, jint dstOff,  
                                    jint numEls)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_SHORT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "short");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    READBODY(jshort)
  } else {
    ENCODEREADBODY(jshort, *(pdst++) = psrc[1];
                           *(pdst++) = psrc[0];)
  }
}

/**********************SCATTER METHODS **********************************/

#define SCATTERBODY(T)                                                    \
int destSize  = (*env)-> GetArrayLength(env, dest);                       \
int indexSize = (*env)-> GetArrayLength(env, indexes);                    \
                                                                          \
if (((jint*)src)[1] != numEls) {                                          \
  mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);                    \
  return;                                                                 \
}                                                                         \
                                                                          \
if (indexSize < offs + numEls){                                           \
  char str[100];                                                          \
  sprintf(str, "Size of the indexes array is too short.\n"                \
   "Size of indexes array: %d\nSize needed: %d\n",                        \
   indexSize, (offs + numEls));                                           \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
                                                                          \
  int isThereMemoryError = 0;                                             \
  int isThereOutOfBoundsException = 0;                                    \
  char str[100];                                                          \
  T* dst       = (*env)->GetPrimitiveArrayCritical(env, dest, 0);         \
  jint *index  = (*env)->GetPrimitiveArrayCritical(env, indexes, 0);      \
                                                                          \
  if (dst == 0 || index == 0)                                             \
    isThereMemoryError = 1;                                               \
  else {                                                                  \
                                                                          \
    int i, byte_numEls = numEls * sizeof(T);                              \
    T* psrc = (T*)(src + MPJDEV_HEADER_SIZE);                                    \
                                                                          \
    for (i = 0; i < numEls; i++){                                         \
      jint indx = index[offs + i];                                        \
      if (destSize - 1 < indx) {                                          \
        isThereOutOfBoundsException = 1;                                  \
        sprintf(str, "In method 'Scatter', one of the index "             \
          "point to out side of the destination array.\n"                 \
          "Size of destination: %d.\n"                                    \
          "Index: %d", destSize, indx);                                   \
        break;                                                            \
      } else                                                              \
        dst[indx] = psrc[i];                                              \
    }                                                                     \
                                                                          \
    /* To ensure that the sections all start on 8-byte boundaries.*/      \
    /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                      \
    pBuffer-> readPtr += MPJDEV_HEADER_SIZE +                                    \
              8 * ((byte_numEls / 8) + (((byte_numEls % 8) == 0) ? 0:1)); \
  }                                                                       \
                                                                          \
  (*env)->ReleasePrimitiveArrayCritical(env, dest, dst, JNI_ABORT);       \
  (*env)->ReleasePrimitiveArrayCritical(env, indexes, index, JNI_ABORT);  \
                                                                          \
  if (isThereMemoryError) {                                               \
    mpjdev_outOfMemoryError(env);                                                \
    return;                                                               \
  }                                                                       \
                                                                          \
  if (isThereOutOfBoundsException) {                                      \
    mpjdev_arrayIndexOutOfBoundsException(env, str);                             \
    return;                                                               \
  }                                                                       \
}


#define ENCODESCATTERBODY(T, byteRev)                                     \
int destSize  = (*env)-> GetArrayLength(env, dest);                       \
int indexSize = (*env)-> GetArrayLength(env, indexes);                    \
                                                                          \
char eSize[4] = {src[7], src[6], src[5], src[4]};                         \
                                                                          \
if (((jint*)eSize)[0] != numEls) {                                        \
  mpjdev_sizeMismatchException(env, ((jint*)src)[1], numEls);                    \
  return;                                                                 \
}                                                                         \
                                                                          \
if (indexSize < offs + numEls){                                           \
  char str[100];                                                          \
  sprintf(str, "Size of the indexes array is too short.\n"                \
   "Size of indexes array: %d\nSize needed: %d\n",                        \
   indexSize, (offs + numEls));                                           \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
                                                                          \
  int isThereMemoryError = 0;                                             \
  int isThereOutOfBoundsException = 0;                                    \
  char str[100];                                                          \
  T* dst       = (*env)->GetPrimitiveArrayCritical(env, dest, 0);         \
  jint *index  = (*env)->GetPrimitiveArrayCritical(env, indexes, 0);      \
                                                                          \
  if (dst == 0 || index == 0) {                                           \
    isThereMemoryError = 1;                                               \
  } else {                                                                \
    int i, byte_numEls = numEls * sizeof(T);                              \
    for(i = 0; i < numEls; i++){                                          \
      jint indx = index[offs + i];                                        \
      if (destSize - 1 < indx) {                                          \
        isThereOutOfBoundsException = 1;                                  \
        sprintf(str, "In method 'Scatter', one of the index "             \
          "point to out side of the destination array.\n"                 \
          "Size of destination array: %d.\n"                              \
          "Index: %d", destSize, indx);                                   \
        break;                                                            \
      } else {                                                            \
        char* pdst = (char*)(dst + indx);                                 \
        char* psrc = src + i * sizeof(T) + MPJDEV_HEADER_SIZE;                   \
        byteRev                                                           \
      }                                                                   \
    }                                                                     \
                                                                          \
    /* To ensure that the sections all start on 8-byte boundaries.*/      \
    /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                      \
    pBuffer-> readPtr += MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);        \
  }                                                                       \
                                                                          \
  (*env)->ReleasePrimitiveArrayCritical(env, dest, dst, JNI_ABORT);       \
  (*env)->ReleasePrimitiveArrayCritical(env, indexes, index, JNI_ABORT);  \
                                                                          \
  if (isThereMemoryError) {                                               \
    mpjdev_outOfMemoryError(env);                                                \
    return;                                                               \
  }                                                                       \
                                                                          \
  if (isThereOutOfBoundsException) {                                      \
    mpjdev_arrayIndexOutOfBoundsException(env, str);                             \
    return;                                                               \
  }                                                                       \
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([FII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' float
 * elements into an indexed section of the array `dest'.  The indexes are
 *   (indexes [offs], indexes [offs + 1], ..., indexes [offs + numEls - 1])
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3FII_3I (JNIEnv * env,     jobject jthis,
                                               jfloatArray dest, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId);
  
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE; 
  
  if (src[0] != (char)MPJDEV_FLOAT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "float");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jfloat)
  } else {
    ENCODESCATTERBODY(jfloat, *(pdst++) = psrc[3];
                              *(pdst++) = psrc[2];
                              *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
  }
}

/*
 * Class:    mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([DII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' double
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3DII_3I (JNIEnv * env,    jobject jthis,
                                               jdoubleArray dest, jint numEls, 
                                               jint offs,   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_DOUBLE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "double");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jdouble);
  } else {
    ENCODESCATTERBODY(jdouble, *(pdst++) = psrc[7];
                               *(pdst++) = psrc[6];
                               *(pdst++) = psrc[5];
                               *(pdst++) = psrc[4];
                               *(pdst++) = psrc[3];
                               *(pdst++) = psrc[2];
                               *(pdst++) = psrc[1];
                               *(pdst++) = psrc[0];)
  } 
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([III[I)V
 *
 * Read a section from the buffer containing extactly `numEls' int
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3III_3I (JNIEnv * env,   jobject jthis,
                                               jintArray dest, jint numEls, 
                                               jint offs,   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_INT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "int");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jint);
  } else {
    ENCODESCATTERBODY(jint, *(pdst++) = psrc[3];
                            *(pdst++) = psrc[2];
                            *(pdst++) = psrc[1];
                            *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([ZII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' boolean
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3ZII_3I (JNIEnv * env,    jobject jthis,
                                               jbooleanArray dest,jint numEls, 
                                               jint offs,    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BOOLEAN)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "boolean");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jboolean);
  } else {
    ENCODESCATTERBODY(jboolean, *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([BII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' byte
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3BII_3I (JNIEnv * env,    jobject jthis,
                                               jbyteArray dest, jint numEls, 
                                               jint offs,   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BYTE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "byte");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jbyte);
  } else {
    ENCODESCATTERBODY(jbyte, *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([CII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' char
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3CII_3I (JNIEnv * env,    jobject jthis,
                                               jcharArray dest, jint numEls, 
                                               jint offs,   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_CHAR)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "char");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jchar);
  } else {
    ENCODESCATTERBODY(jchar, *(pdst++) = psrc[1];
                             *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([JII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' long
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3JII_3I (JNIEnv * env,    jobject jthis,
                                               jlongArray dest, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_LONG)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "long");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jlong);
  } else {
    ENCODESCATTERBODY(jlong, *(pdst++) = psrc[7];
                             *(pdst++) = psrc[6];
                             *(pdst++) = psrc[5];
                             *(pdst++) = psrc[4];
                             *(pdst++) = psrc[3];
                             *(pdst++) = psrc[2];
                             *(pdst++) = psrc[1];
                             *(pdst++) = psrc[0];)
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    scatter
 * Signature: ([SII[I)V
 *
 * Read a section from the buffer containing extactly `numEls' short
 * elements into an indexed section of the array `dest'.
 * Similar to the float version above.
 * @see #scatter(float [], int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_scatter___3SII_3I (JNIEnv * env,    jobject jthis,
                                               jshortArray dest, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis, 
                                                              mpjdev_jBufferId); 
  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_SHORT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "short");

  if (pBuffer-> message[0] == (char)mpjdev_encoding){
    SCATTERBODY(jshort);
  } else {
    ENCODESCATTERBODY(jshort, *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
  }
}

/**********************STRSCATTER METHODS **********************************/

#define STRSCATTERBODY(T)                                                 \
int i;                                                                    \
int destSize  = (*env)-> GetArrayLength(env, dest);                       \
int indexSize = (*env)-> GetArrayLength(env, indexes);                    \
jint src_volume, dst_volume = 1;                                          \
                                                                          \
if (pBuffer-> message[0] == (char)mpjdev_encoding)                               \
  src_volume = ((jint*)src)[1];                                           \
else {                                                                    \
  char eSize[4] = {src[7], src[6], src[5], src[4]};                       \
  src_volume = ((jint*)eSize)[0];                                         \
}                                                                         \
                                                                          \
if (indexSize < rank + exts){                                             \
  char str[100];                                                          \
  sprintf(str, "In method 'StrScatter', one of the 'Extent'"              \
    " point to outside of index array.\n"                                 \
    " Size of index array:%d \n"                                          \
    " 'Extent' point:%d\n",                                               \
    (2 * rank + exts + strs), indexSize);                                 \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else if (indexSize < rank + strs){                                      \
  char str[100];                                                          \
  sprintf(str, "In method 'StrScatter', one of the 'Strs'"                \
    " point to outside of index array.\n"                                 \
    " Size of index array:%d \n"                                          \
    " 'Strs' point:%d\n",                                                 \
    (2 * rank + exts + strs), indexSize);                                 \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
  int isThereMemoryError = 0, isThereSizeMismatchException = 0;           \
  int isThereOutOfBoundsException = 0;                                    \
  char str[100];                                                          \
  T*    dst   = (*env)->GetPrimitiveArrayCritical(env, dest, 0);          \
  jint* index = (*env)->GetPrimitiveArrayCritical(env, indexes, 0);       \
                                                                          \
  if (dst == 0 || index == 0) {                                           \
    /* out of memory error*/                                              \
    isThereMemoryError = 1;                                               \
  } else {                                                                \
                                                                          \
    for (i = 0; i < rank; i++)                                            \
      dst_volume *= index[exts + i];                                      \
                                                                          \
    if (src_volume != dst_volume){                                        \
      isThereSizeMismatchException = 1;                                   \
    } else {                                                              \
      int minIndex = dstOff, maxIndex = dstOff;                           \
      for (i = 0; i < rank; i++) {                                        \
        if (index[strs + i] < 0)                                          \
          minIndex += index[strs + i] * (index[exts + i] - 1);            \
        else                                                              \
          maxIndex += index[strs + i] * (index[exts + i] - 1);            \
      }                                                                   \
                                                                          \
      if(minIndex < 0) {                                                  \
        isThereOutOfBoundsException = 1;                                  \
        sprintf(str, "In method 'strScatter', one of the index point "    \
                "out side of the dstination array.\n"                     \
                "Index point: %d \n", minIndex);                          \
      } else if (maxIndex > destSize - 1) {                               \
        isThereOutOfBoundsException = 1;                                  \
        sprintf(str, "In method 'strScatter', one of the index point "    \
                "out side of the dstination array.\n"                     \
                "Index point: %d \n", maxIndex);                          \
      } else {                                                            \
        int byte_numEls = src_volume * sizeof(T);                         \
        if (pBuffer-> message[0] == (char)mpjdev_encoding)                       \
          T##Scatter((T*)(src + 8),                                       \
                     dst + dstOff, rank, index + exts, index + strs);     \
        else                                                              \
          T##EncodeScatter ((T*)(src + 8),                                \
                            dst + dstOff, rank, index + exts,             \
                            index + strs);                                \
                                                                          \
        /* To ensure that the sections all start on 8-byte boundaries.*/  \
        /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                  \
        pBuffer-> readPtr += MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);    \
      }                                                                   \
    }                                                                     \
  }                                                                       \
                                                                          \
  (*env)->ReleasePrimitiveArrayCritical(env, dest,  dst,  JNI_ABORT);     \
  (*env)->ReleasePrimitiveArrayCritical(env, indexes, index, JNI_ABORT);  \
                                                                          \
  if (isThereSizeMismatchException){                                      \
    mpjdev_sizeMismatchException(env, src_volume, dst_volume);                   \
    return;                                                               \
  }                                                                       \
                                                                          \
  if (isThereOutOfBoundsException) {                                      \
    mpjdev_arrayIndexOutOfBoundsException(env, str);                             \
    return;                                                               \
  }                                                                       \
                                                                          \
  if (isThereMemoryError) {                                               \
    mpjdev_outOfMemoryError(env);                                                \
    return;                                                               \
  }                                                                       \
}

#define STRSCATTERDEF(T)                                                    \
T* T##Scatter(T* src, T* dest, int rank, jint* exts, jint* strs)            \
{                                                                           \
  int i, j, k;                                                              \
  switch (rank)                                                             \
  {                                                                         \
    case 0:                                                                 \
                                                                            \
      dest[0] = src[0];                                                     \
      break;                                                                \
    case 1:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        *(dest0) = *(src++);                                                \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    case 2:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        T* dest1 = dest0;                                                   \
        for (j = 0; j < exts[1]; j++) {                                     \
          *(dest1) = *(src++);                                              \
          dest1 += strs[1];                                                 \
        }                                                                   \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    case 3:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        T* dest1 = dest0;                                                   \
        for (j = 0; j < exts[1]; j++) {                                     \
          T* dest2 = dest1;                                                 \
          for (k = 0; k < exts[2]; k++){                                    \
            *(dest2) = *(src++);                                            \
            dest2 += strs[2];                                               \
          }                                                                 \
          dest1 += strs[1];                                                 \
        }                                                                   \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    default: {                                                              \
      int count = exts[0];                                                  \
      int str   = strs[0];                                                  \
      rank--;                                                               \
      exts++;                                                               \
      strs++;                                                               \
      for (i = 0; i < count; i++) {                                         \
        dest = T##Scatter(src,                                              \
                         dest + str * i, rank, exts, strs);                 \
      }                                                                     \
    }                                                                       \
  }                                                                         \
                                                                            \
  return dest;                                                              \
}

#define ENCODESTRSCATTERDEF(T, byteRev)                                     \
T* T##EncodeScatter(T* src, T* dest, int rank, jint* exts, jint* strs)      \
{                                                                           \
  int i, j, k;                                                              \
  switch (rank)                                                             \
  {                                                                         \
    case 0:{                                                                \
      char* pdst = (char*)dest;                                             \
      char* psrc = (char*)src;                                              \
      byteRev                                                               \
      break;                                                                \
    }                                                                       \
    case 1:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        char* pdst = (char*)dest0;                                          \
        char* psrc = (char*)(src++);                                        \
        byteRev                                                             \
                                                                            \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    case 2:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        T* dest1 = dest0;                                                   \
        for (j = 0; j < exts[1]; j++) {                                     \
          char* pdst = (char*)dest1;                                        \
          char* psrc = (char*)(src++);                                      \
          byteRev                                                           \
                                                                            \
          dest1 += strs[1];                                                 \
        }                                                                   \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    case 3:{                                                                \
      T* dest0 = dest;                                                      \
      for (i = 0; i < exts[0]; i++){                                        \
        T* dest1 = dest0;                                                   \
        for (j = 0; j < exts[1]; j++) {                                     \
          T* dest2 = dest1;                                                 \
          for (k = 0; k < exts[2]; k++){                                    \
            char* pdst = (char*)dest2;                                      \
            char* psrc = (char*)(src++);                                    \
            byteRev                                                         \
                                                                            \
            dest2 += strs[2];                                               \
          }                                                                 \
          dest1 += strs[1];                                                 \
        }                                                                   \
        dest0 += strs[0];                                                   \
      }                                                                     \
      break;                                                                \
    }                                                                       \
    default: {                                                              \
      int count = exts[0];                                                  \
      int str   = strs[0];                                                  \
      rank--;                                                               \
      exts++;                                                               \
      strs++;                                                               \
      for (i = 0; i < count; i++) {                                         \
        dest = T##EncodeScatter(src,                                        \
                         dest + str * i, rank, exts, strs);                 \
      }                                                                     \
    }                                                                       \
  }                                                                         \
                                                                            \
  return dest;                                                              \
}

STRSCATTERDEF(jfloat)
STRSCATTERDEF(jdouble)
STRSCATTERDEF(jint)
STRSCATTERDEF(jbyte)
STRSCATTERDEF(jchar)
STRSCATTERDEF(jshort)
STRSCATTERDEF(jboolean)
STRSCATTERDEF(jlong)

ENCODESTRSCATTERDEF(jfloat,   *(pdst++) = psrc[3];
                              *(pdst++) = psrc[2];
                              *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jdouble,  *(pdst++) = psrc[7];
                              *(pdst++) = psrc[6];
                              *(pdst++) = psrc[5];
                              *(pdst++) = psrc[4];
                              *(pdst++) = psrc[3];
                              *(pdst++) = psrc[2];
                              *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jint,     *(pdst++) = psrc[3];
                              *(pdst++) = psrc[2];
                              *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jbyte,    *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jchar,    *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jshort,   *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jboolean, *(pdst++) = psrc[0];)
ENCODESTRSCATTERDEF(jlong,    *(pdst++) = psrc[7];
                              *(pdst++) = psrc[6];
                              *(pdst++) = psrc[5];
                              *(pdst++) = psrc[4];
                              *(pdst++) = psrc[3];
                              *(pdst++) = psrc[2];
                              *(pdst++) = psrc[1];
                              *(pdst++) = psrc[0];)

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([FIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' float
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * The rank of the patch is `rank', its shape is:
 *   (indexes [exts], indexes [exts + 1], ..., indexes [exts + rank - 1])
 * and the strides within the `source' array associated with each
 * dimension of the patch are:
 *   (indexes [strs], indexes [strs + 1], ..., indexes [strs + rank - 1])
 * and the `volume' referred to above is the product of
 * the extents (the elements of the shape vector).
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3FIIII_3I(JNIEnv * env, jobject jthis,
                                                   jfloatArray dest,
                                                   jint dstOff, 
                                                   jint rank,      
                                                   jint exts, 
                                                   jint strs,
                                                   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_FLOAT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "float");
  else { 
    STRSCATTERBODY(jfloat);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([DIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' double
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL
Java_mpjdev_ReadBuffer_strScatter___3DIIII_3I (JNIEnv * env,jobject jthis,
                                                    jdoubleArray dest, 
                                                    jint dstOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs, 
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_DOUBLE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "double");
  else {
    STRSCATTERBODY(jdouble);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([IIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' int
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3IIIII_3I (JNIEnv * env,jobject jthis,
                                                    jintArray dest, 
                                                    jint dstOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_INT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "int");
  else {
    STRSCATTERBODY(jint);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([ZIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' boolean
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3ZIIII_3I (JNIEnv * env,jobject jthis,
                                                    jbooleanArray dest, 
                                                    jint dstOff,
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BOOLEAN)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "boolean");
  else {
    STRSCATTERBODY(jboolean); 
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([BIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' byte
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */

JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3BIIII_3I (JNIEnv * env,jobject jthis,
                                                    jbyteArray dest, 
                                                    jint dstOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_BYTE)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "byte");
  else {
    STRSCATTERBODY(jbyte);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([CIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' char
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3CIIII_3I (JNIEnv * env,jobject jthis,
                                                    jcharArray dest, 
                                                    jint dstOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_CHAR)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "char");
  else {
    STRSCATTERBODY(jchar);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([JIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' long
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */ 
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3JIIII_3I (JNIEnv * env,jobject jthis,
                                                    jlongArray dest, 
                                                    jint dstOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_LONG)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "long");
  else {
    STRSCATTERBODY(jlong);
  }
}

/*
 * Class:     mpjdev_ReadBuffer
 * Method:    strScatter
 * Signature: ([SIIII[I)V
 *
 * Read a section from the buffer containing exactly `volume' short
 * elements into a multi-dimensional, strided patch of the array `dest'.
 * Similar to the float version above.
 * @see #strScatter(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_ReadBuffer_strScatter___3SIIII_3I(JNIEnv * env, jobject jthis,
                                                   jshortArray dest,
                                                   jint dstOff, 
                                                   jint rank,
                                                   jint exts, 
                                                   jint strs,
                                                   jintArray indexes)
{
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,
                                                              mpjdev_jBufferId);

  char *src = pBuffer-> message + pBuffer-> readPtr + MPJDEV_HEADER_SIZE;

  if (src[0] != (char)MPJDEV_SHORT)
    mpjdev_typeMismatchException(env, mpjdev_getType((int)src[0]), "short");
  else {
    STRSCATTERBODY(jshort)
  }
}






