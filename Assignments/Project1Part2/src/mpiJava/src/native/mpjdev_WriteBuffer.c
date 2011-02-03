/*
 * File         : mpjdev_WriteBuffer.c
 * Author       : Sang Lim
 * Created      : Mon Jan 28 15:25:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */

#include "mpjdev_WriteBuffer.h"
#include "buff_typeDef.h"
#include <inttypes.h>

extern jfieldID mpjdev_jBufferId;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    clear
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_clear (JNIEnv * env, jobject jthis)
{

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, jthis, 
                                                             mpjdev_jBufferId);

  pBuffer -> size    = 0;
  pBuffer -> readPtr = 0;
}

/******************* WRITE METHODS ***********************************/

#define WRITEBODY(T, messageType)                                         \
Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,   \
                                                            mpjdev_jBufferId);   \
int sourceSize = (*env)-> GetArrayLength(env, source);                    \
                                                                          \
int byte_numEls =  sizeof(T) * numEls;                                    \
/* To ensure that the sections all start on 8-byte boundaries.*/          \
/* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                          \
int newSize = pBuffer-> size + MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);  \
                                                                          \
if (pBuffer-> capacity < newSize - 8) {                                       \
  char str[100];                                                          \
  sprintf(str, "In method 'Write', buffer capacity is too short.\n"       \
    "Buffer capacity: %d.\n"                                              \
    "Trying to write: %d", pBuffer-> capacity, newSize);                  \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
}                                                                         \
                                                                          \
if (sourceSize < numEls + srcOff){                                        \
  char str[100];                                                          \
  sprintf(str, "In method 'Write', trying to write more then source "     \
    "size.\nSize of source: %d.\n"                                        \
    "Trying to write: %d", sourceSize, numEls + srcOff);                  \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
  int isThereError = 0;                                                   \
  T *src = (*env)->GetPrimitiveArrayCritical(env, source, 0);             \
                                                                          \
  if (src == 0) {                                                         \
    isThereError = 1;                                                     \
  } else {                                                                \
                                                                          \
    int i;                                                                \
    T *ptarget;                                                           \
                                                                          \
    /* MPJDEV_HEADER_SIZE is primary header size.*/                              \
    char *target       = pBuffer-> message + pBuffer-> size + MPJDEV_HEADER_SIZE;\
    target[0]          = (char)messageType; /*Set element type.*/         \
    ((jint*)target)[1] = numEls;            /*Set Length of section.*/    \
                                                                          \
    /* MPJDEV_HEADER_SIZE is Section header size.*/                              \
    ptarget = (T*)(target + MPJDEV_HEADER_SIZE);                                 \
    for (i = 0; i < numEls; i++){                                         \
                                                                          \
      ptarget[i] = src[i + srcOff];                                       \
    }                                                                     \
                                                                          \
    pBuffer-> size = newSize;                                             \
  }                                                                       \
  (*env)->ReleasePrimitiveArrayCritical(env, source, src, 0);             \
                                                                          \
  if (isThereError) {                                                     \
    mpjdev_outOfMemoryError(env);                                                \
    return;                                                               \
  }                                                                       \
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([FII)V
 *
 * Write a section to the buffer containing `numEls' float
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 4 * numEls' units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3FII (JNIEnv * env,       jobject jthis,
                                           jfloatArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jfloat, MPJDEV_FLOAT);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([DII)V
 *
 * Write a section to the buffer containing `numEls' double
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 8 * numEls' units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3DII (JNIEnv * env,        jobject jthis,
                                           jdoubleArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jdouble, MPJDEV_DOUBLE);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([III)V
 *
 * Write a section to the buffer containing `numEls' int
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 4 * numEls' units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3III (JNIEnv * env,     jobject jthis,
                                           jintArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jint, MPJDEV_INT);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([ZII)V
 *
 * Write a section to the buffer containing `numEls' boolean
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 1 * numEls' units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3ZII (JNIEnv * env,         jobject jthis,
                                           jbooleanArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jboolean, MPJDEV_BOOLEAN);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([BII)V
 *
 * Write a section to the buffer containing `numEls' float
 * elements in `source', starting at `srcOff'.  This requires
 * "SECTION_OVERHEAD + 1 * numEls" units of buffer capacity. 
*/
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3BII (JNIEnv * env,      jobject jthis,
                                           jbyteArray source, jint srcOff,   
                                           jint numEls)
{
  WRITEBODY(jbyte, MPJDEV_BYTE);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([CII)V
 *
 * Write a section to the buffer containing `numEls' char
 * elements in `source', starting at `srcOff'.  This requires
 * "SECTION_OVERHEAD + 2 * numEls" units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3CII (JNIEnv * env,      jobject jthis,
                                      jcharArray source, jint srcOff,   
                                      jint numEls)
{
  WRITEBODY(jchar, MPJDEV_CHAR);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([JII)V
 *
 * Write a section to the buffer containing `numEls' short
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 8 * numEls' units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3JII (JNIEnv * env,      jobject jthis,
                                           jlongArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jlong, MPJDEV_LONG);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    write
 * Signature: ([SII)V
 *
 * Write a section to the buffer containing `numEls' short
 * elements in `source', starting at `srcOff'.  This requires
 * 'SECTION_OVERHEAD + 2 * numEls'units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_write___3SII (JNIEnv * env,       jobject jthis,
                                           jshortArray source, jint srcOff, 
                                           jint numEls)
{
  WRITEBODY(jshort, MPJDEV_SHORT);
}

/********************* GATHER METHODS ***********************************/
#define GATHERBODY(T, messageType)                                        \
Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,   \
                                                            mpjdev_jBufferId);   \
                                                                          \
int indexSize = (*env) -> GetArrayLength(env, indexes);                   \
int srcSize   = (*env) -> GetArrayLength(env, source);                    \
                                                                          \
int byte_numEls =  sizeof(T) * numEls;                                    \
/* To ensure that the sections all start on 8-byte boundaries.*/          \
/* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                          \
int newSize = pBuffer-> size + MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);  \
                                                                          \
if (pBuffer-> capacity < newSize - 8) {                                   \
  char str[100];                                                          \
  sprintf(str, "In method 'Gather', buffer capacity is too short.\n"      \
    "Buffer capacity: %d.\n"                                              \
    "Trying to write: %d", pBuffer-> capacity, newSize);                  \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
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
  T *src = (*env)->GetPrimitiveArrayCritical(env, source, 0);             \
  jint *index  = (*env)->GetPrimitiveArrayCritical(env, indexes, 0);      \
                                                                          \
  if (src == 0 || index == 0) {                                           \
    /* out of memory error*/                                              \
    isThereMemoryError = 1;                                               \
  } else {                                                                \
                                                                          \
    int i;                                                                \
    T *ptarget;                                                           \
                                                                          \
    /* MPJDEV_HEADER_SIZE is primary header size.*/                              \
    char *target       = pBuffer-> message + pBuffer-> size + MPJDEV_HEADER_SIZE;\
    target[0]          = (char)messageType; /*Set element type.*/         \
    ((jint*)target)[1] = numEls;            /*Set Length of section.*/    \
                                                                          \
    /* MPJDEV_HEADER_SIZE is Section header size.*/                              \
    ptarget = (T*)(target + MPJDEV_HEADER_SIZE);                                 \
    for (i = 0; i < numEls; i++){                                         \
      jint indx = index[offs + i];                                        \
      if (srcSize - 1 < indx){                                            \
        isThereOutOfBoundsException = 1;                                  \
        sprintf(str, "In method 'Gather', one of the index "              \
          "point to out side of the destination array.\n"                 \
          "Size of destination: %d.\n"                                    \
          "Index: %d", srcSize, indx);                                    \
        break;                                                            \
      }else                                                               \
        ptarget[i] = src[indx];                                           \
    }                                                                     \
                                                                          \
    pBuffer-> size = newSize;                                             \
  }                                                                       \
  (*env)->ReleasePrimitiveArrayCritical(env, indexes, index, 0);          \
  (*env)->ReleasePrimitiveArrayCritical(env, source,  src,  0);           \
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
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([FII[I)V
 *
 * Write a section to the buffer containing float elements from
 * an indexed section of the array `source'.  The indexes are
 *   (indexes [offs], indexes [offs + 1], ..., indexes [offs + numEls - 1])
 * This requires
 *     SECTION_OVERHEAD + 4 * numEls
 * units of buffer capacity.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3FII_3I (JNIEnv * env,    jobject jthis,
                                               jfloatArray source,jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jfloat, MPJDEV_FLOAT)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([DII[I)V
 *
 * Write a section to the buffer containing double elements from
 * an indexed section of the array `source'. 
 * Similar to the float version above.
 * This requires
 *     SECTION_OVERHEAD + 8 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3DII_3I (JNIEnv * env,    jobject jthis,
                                               jdoubleArray source, 
                                               jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jdouble, MPJDEV_DOUBLE)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([III[I)V
 *
 * Write a section to the buffer containing int elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 4 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3III_3I (JNIEnv * env,     jobject jthis,
                                               jintArray source, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jint, MPJDEV_INT)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([ZII[I)V
 *
 * Write a section to the buffer containing boolean elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 1 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3ZII_3I (JNIEnv * env,     jobject jthis,
                                               jbooleanArray source, 
                                               jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jboolean, MPJDEV_BOOLEAN)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([BII[I)V
 *
 * Write a section to the buffer containing byte elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 1 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3BII_3I (JNIEnv * env,    jobject jthis, 
                                               jbyteArray source, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jbyte, MPJDEV_BYTE)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([CII[I)V
 *
 * Write a section to the buffer containing char elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 2 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3CII_3I (JNIEnv * env,    jobject jthis,
                                               jcharArray source, jint numEls,
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jchar, MPJDEV_CHAR)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([JII[I)V
 *
 * Write a section to the buffer containing long elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 8 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3JII_3I (JNIEnv * env,    jobject jthis,
                                               jlongArray source, jint numEls, 
                                               jint offs,    jintArray indexes)
{
  GATHERBODY(jlong, MPJDEV_LONG)
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    gather
 * Signature: ([SII[I)V
 *
 * Write a section to the buffer containing short elements from
 * an indexed section of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 2 * numEls
 * units of buffer capacity.
 * @see #gather(float [],int,int,int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_gather___3SII_3I (JNIEnv * env,    jobject jthis,
                                               jshortArray source,jint numEls, 
                                               jint offs,   jintArray indexes)
{
  GATHERBODY(jshort, MPJDEV_SHORT)
}

/********************* STRGATHER METHODS ********************************/
#define STRGATHERBODY(T, messageType)                                     \
Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, jthis,   \
                                                            mpjdev_jBufferId);   \
int indexSize = (*env)-> GetArrayLength(env, indexes);                    \
                                                                          \
if (indexSize < rank + exts){                                             \
  char str[100];                                                          \
  sprintf(str, "In method 'strGather', one of the 'Extent'"               \
    " point to %d which is outside of index array"                        \
    " which size is %d.\n", indexSize, (rank + exts));                    \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else if (indexSize < rank + strs){                                      \
  char str[100];                                                          \
  sprintf(str, "In method 'strGather', one of the 'Stride'"               \
    " point to %d which is outside of index array"                        \
    " which size is %d.\n", indexSize, (rank + strs));                    \
  mpjdev_arrayIndexOutOfBoundsException(env, str);                               \
  return;                                                                 \
} else {                                                                  \
  char str[100]; /* Exception message. */                                 \
  int i, newSize, volume = 1, byte_numEls;                                \
  int isThereMemoryError = 0, isThereOutOfBoundsException = 0;            \
                                                                          \
  int srcSize = (*env)-> GetArrayLength(env, source);                     \
  T *src      = (*env)->GetPrimitiveArrayCritical(env, source, 0);        \
  jint *index = (*env)->GetPrimitiveArrayCritical(env, indexes, 0);       \
                                                                          \
  if (index == 0 || src == 0)                                             \
    isThereMemoryError = 1;                                               \
                                                                          \
  for (i = 0; i < rank; i++)                                              \
    volume *= index[exts + i];                                            \
                                                                          \
  byte_numEls =  sizeof(T) * volume;                                      \
  /* To ensure that the sections all start on 8-byte boundaries.*/        \
  /* 8 * ceiling(byte_numEls / 8) - byte_numEls.*/                        \
  newSize = pBuffer-> size + MPJDEV_HEADER_SIZE + 8 * ((byte_numEls+ 7) / 8);    \
                                                                          \
  if (pBuffer-> capacity < newSize - 8) {                                 \
    isThereOutOfBoundsException = 1;                                      \
    sprintf(str, "In method 'strGather', buffer capacity is too short.\n" \
      "Buffer capacity: %d\n"                                             \
      "Trying to write: %d\n", pBuffer-> capacity, newSize);              \
  } else {                                                                \
    int minIndex = srcOff, maxIndex = srcOff;                             \
                                                                          \
    /* MPJDEV_HEADER_SIZE is primary header size.*/                              \
    char *target       = pBuffer-> message + pBuffer-> size + MPJDEV_HEADER_SIZE;\
    target[0]          = (char)messageType; /*Set element type.*/         \
    ((jint*)target)[1] = volume;            /*Set Length of section.*/    \
                                                                          \
    for (i = 0; i < rank; i++) {                                          \
      if (index[strs + i] < 0)                                            \
        minIndex += index[strs + i] * (index[exts + i] - 1);              \
      else                                                                \
        maxIndex += index[strs + i] * (index[exts + i] - 1);              \
    }                                                                     \
                                                                          \
    if(minIndex < 0) {                                                    \
      isThereOutOfBoundsException = 1;                                    \
      sprintf(str, "In method 'strGather', one of the index point "       \
              "out side of the dstination array.\n"                       \
              "Index point: %d \n", minIndex);                            \
    } else if (maxIndex > srcSize - 1) {                                  \
      isThereOutOfBoundsException = 1;                                    \
      sprintf(str, "In method 'strGather', one of the index point "       \
              "out side of the dstination array.\n"                       \
              "Index point: %d \n", maxIndex);                            \
    } else {                                                              \
                                                                          \
      T##StrGather((T*)(target + MPJDEV_HEADER_SIZE),                            \
                   src + srcOff, rank, index + exts, index + strs);       \
    }                                                                     \
                                                                          \
    pBuffer-> size = newSize;                                             \
                                                                          \
  }                                                                       \
  (*env)->ReleasePrimitiveArrayCritical(env, source,  src,  0);           \
  (*env)->ReleasePrimitiveArrayCritical(env, indexes, index, 0);          \
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

#define STRGATHERDEF(T)                                             \
T* T##StrGather(T* dest, T* src, int rank, jint* exts, jint* strs)  \
{                                                                   \
  int i, j, k;                                                      \
  switch (rank)                                                     \
  {                                                                 \
    case 0:                                                         \
                                                                    \
      dest[0] = src[0];                                             \
      break;                                                        \
    case 1:{                                                        \
      T* src0 = src;                                                \
      for (i = 0; i < exts[0]; i++){                                \
        *(dest++) = *(src0);                                        \
        src0 += strs[0];                                            \
      }                                                             \
      break;                                                        \
    }                                                               \
    case 2:{                                                        \
      T* src0 = src;                                                \
      for (i = 0; i < exts[0]; i++){                                \
        T* src1 = src0;                                             \
        for (j = 0; j < exts[1]; j++) {                             \
          *(dest++) = *(src1);                                      \
          src1 += strs[1];                                          \
        }                                                           \
        src0 += strs[0];                                            \
      }                                                             \
      break;                                                        \
    }                                                               \
    case 3:{                                                        \
      T* src0 = src;                                                \
      for (i = 0; i < exts[0]; i++){                                \
        T* src1 = src0;                                             \
        for (j = 0; j < exts[1]; j++) {                             \
          T* src2 = src1;                                           \
          for (k = 0; k < exts[2]; k++){                            \
            *(dest++) = *(src2);                                    \
            src2 += strs[2];                                        \
          }                                                         \
          src1 += strs[1];                                          \
        }                                                           \
        src0 += strs[0];                                            \
      }                                                             \
      break;                                                        \
    }                                                               \
    default: {                                                              \
      int count = exts[0];                                                  \
      int str   = strs[0];                                                  \
      rank--;                                                               \
      exts++;                                                               \
      strs++;                                                               \
      for (i = 0; i < count; i++) {                                         \
        dest = T##StrGather(src,                                              \
                         dest + str * i, rank, exts, strs);                 \
      }                                                                     \
    }                                                                       \
  }                                                                 \
                                                                    \
  return dest;                                                      \
}


STRGATHERDEF(jfloat)
STRGATHERDEF(jdouble)
STRGATHERDEF(jint)
STRGATHERDEF(jbyte)
STRGATHERDEF(jchar)
STRGATHERDEF(jshort)
STRGATHERDEF(jboolean)
STRGATHERDEF(jlong)

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([FIIII[I)V
 *
 * Write a section to the buffer containing float elements from
 * a multi-dimensional, strided patch of the array `source'.
 * The rank of the patch is `rank', its shape is:
 *   (indexes [exts], indexes [exts + 1], ..., indexes [exts + rank - 1])
 * and the strides within the `source' array associated with each
 * dimension of the patch are:
 *   (indexes [strs], indexes [strs + 1], ..., indexes [strs + rank - 1])
 * This requires
 *     SECTION_OVERHEAD + 4 * volume
 * units of buffer capacity, where `volume' is the product of
 * the extents (the elements of the shape vector).
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3FIIII_3I (JNIEnv * env,jobject jthis,
                                                    jfloatArray source, 
                                                    jint srcOff,
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jfloat, MPJDEV_FLOAT);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([DIIII[I)V
 *
 * Write a section to the buffer containing double elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 8 * volume
 * units of buffer capacity.
 * @see #gather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3DIIII_3I (JNIEnv * env,jobject jthis,
                                                    jdoubleArray source,
                                                    jint srcOff,
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jdouble, MPJDEV_DOUBLE);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([IIIII[I)V
 *
 * Write a section to the buffer containing int elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 4 * volume
 * units of buffer capacity.
 * @see #gather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3IIIII_3I (JNIEnv * env,jobject jthis,
                                                    jintArray source, 
                                                    jint srcOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jint, MPJDEV_INT);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([ZIIII[I)V
 *
 * Write a section to the buffer containing boolean elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 1 * volume
 * units of buffer capacity.
 * @see #gather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3ZIIII_3I(JNIEnv * env, jobject jthis,
                                                   jbooleanArray source,
                                                   jint srcOff,
                                                   jint rank,
                                                   jint exts, 
                                                   jint strs,
                                                   jintArray indexes)
{
  STRGATHERBODY(jboolean, MPJDEV_BOOLEAN);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([BIIII[I)V
 *
 * Write a section to the buffer containing byte elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 1 * volume
 * units of buffer capacity.
 * @see #strGather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3BIIII_3I (JNIEnv * env,jobject jthis,
                                                    jbyteArray source, 
                                                    jint srcOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jbyte, MPJDEV_BYTE);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([CIIII[I)V
 *
 * Write a section to the buffer containing char elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 2 * volume
 * units of buffer capacity.
 * @see #strGather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3CIIII_3I (JNIEnv * env,jobject jthis,
                                                    jcharArray source, 
                                                    jint srcOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jchar, MPJDEV_CHAR);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([JIIII[I)V
 *
 * Write a section to the buffer containing long elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 8 * volume
 * units of buffer capacity.
 * @see #strGather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3JIIII_3I (JNIEnv * env,jobject jthis,
                                                    jlongArray source,
                                                    jint srcOff, 
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jlong, MPJDEV_LONG);
}

/*
 * Class:     mpjdev_WriteBuffer
 * Method:    strGather
 * Signature: ([SIIII[I)V
 *
 * Write a section to the buffer containing short elements from
 * a multi-dimensional, strided patch of the array `source'.
 * Similar to the float version above.  This requires
 *     SECTION_OVERHEAD + 2 * volume
 * units of buffer capacity.
 * @see #strGather(float [], int, int, int, int, int[]) float version.
 */
JNIEXPORT void JNICALL 
Java_mpjdev_WriteBuffer_strGather___3SIIII_3I (JNIEnv * env,jobject jthis,
                                                    jshortArray source,
                                                    jint srcOff,
                                                    jint rank,
                                                    jint exts, 
                                                    jint strs,
                                                    jintArray indexes)
{
  STRGATHERBODY(jshort, MPJDEV_SHORT);
}










