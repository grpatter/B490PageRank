/*
 * File         : mpjdev_Comm.c
 * Author       : Sang Lim
 * Created      : Sun Jan 20 16:50:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:20 $
 */

#include <mpi.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <inttypes.h>

#include "mpjdev_Comm.h"
#include "buff_typeDef.h"

enum encode mpjdev_encoding;

jfieldID mpjdev_jBufferId;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

jfieldID mpjdev_handleID;
jfieldID mpjdev_worldID;

jmethodID mpjdev_constructorID;

/* Method ID in 'Buffer' */
jmethodID mpjdev_getObjectByteArrayID;
jfieldID mpjdev_fid;

/* Field and Method IDs in `Status' */
jfieldID mpjdev_sourceID, mpjdev_tagID, mpjdev_indexID;
jmethodID mpjdev_status_constructorID;
jobject mpjdev_staticStatusObject;

/* Fields and Constructor ID in 'Request'*/
jfieldID mpjdev_reqhandleID, mpjdev_typeTagID, mpjdev_opTagID, mpjdev_hdrReqID; 
jfieldID mpjdev_bufferID, mpjdev_commID, mpjdev_r_tagID;
jmethodID mpjdev_request_constructorID, mpjdev_iwaitID;

/*
 * Class:     mpjdev_Comm
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Comm_init (JNIEnv * env, jclass clazz)
{
  jobject worldObj;
  jclass bufferClass, statusClass, requestClass;
  int testValue = 0x01020304;
  char *testptr = (char*) &testValue;

  if (testptr[0] == 01)
    mpjdev_encoding = MPJDEV_BIG_ENDIAN;
  else 
    mpjdev_encoding = MPJDEV_LITTLE_ENDIAN;

  mpjdev_worldID  = (*env)-> GetStaticFieldID(env, clazz, "WORLD", 
                                       "Lmpjdev/Comm;");
  worldObj = (*env)-> GetStaticObjectField(env, clazz, mpjdev_worldID);

  mpjdev_handleID = (*env)-> GetFieldID(env, clazz, "handle", "J") ;
  (*env)-> SetLongField(env, worldObj, mpjdev_handleID, MPI_COMM_WORLD);

  mpjdev_constructorID = (*env)-> GetMethodID(env, clazz, "<init>", "()V");

  /*Get Buffer information*/
  bufferClass     = (*env)-> FindClass(env, "mpjdev/Buffer");
  mpjdev_getObjectByteArrayID = (*env)-> GetMethodID(env, bufferClass, 
                                              "getObjectByteArray", "()[B");
  mpjdev_fid = (*env)-> GetFieldID(env, bufferClass, "byte_buf", "[B");

  mpjdev_jBufferId = (*env)-> GetFieldID(env, bufferClass, "buffId", "J");


  /*Get status information.*/
  statusClass  = (*env)-> FindClass(env, "mpjdev/Status");  
  mpjdev_status_constructorID = (*env)-> GetMethodID(env, statusClass, 
                                              "<init>", "()V");

  mpjdev_sourceID = (*env)->GetFieldID(env, statusClass, "source", "I");
  mpjdev_tagID    = (*env)->GetFieldID(env, statusClass, "tag", "I");
  mpjdev_indexID  = (*env)->GetFieldID(env, statusClass, "index", "I");

  mpjdev_staticStatusObject = (*env)->NewGlobalRef(env, 
                (*env)-> NewObject(env, statusClass, mpjdev_status_constructorID));

  /*Get request information*/
  requestClass  = (*env)-> FindClass(env, "mpjdev/Request"); 
  mpjdev_request_constructorID =  (*env)-> GetMethodID(env, requestClass, 
                                                "<init>", "()V");

  mpjdev_iwaitID = (*env)-> GetMethodID(env, requestClass, "iwait", 
                                 "()Lmpjdev/Status;");
  
  mpjdev_reqhandleID = (*env)->GetFieldID(env, requestClass, "requestId", "J");
  mpjdev_typeTagID   = (*env)->GetFieldID(env, requestClass, "typeTag", "I");
  mpjdev_opTagID     = (*env)->GetFieldID(env, requestClass, "opTag", "I");
  mpjdev_hdrReqID    = (*env)->GetFieldID(env, requestClass, "hdrReq", 
                                   "Lmpjdev/Request;");

  mpjdev_bufferID  = (*env)->GetFieldID(env, requestClass, "buf","Lmpjdev/Buffer;");
  mpjdev_commID    = (*env)->GetFieldID(env, requestClass, "comm","J");
  mpjdev_r_tagID   = (*env)->GetFieldID(env, requestClass, "tag","I");
}

/*
 * Class:     mpjdev_Comm
 * Method:    initNative
 * Signature: ([Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL 
Java_mpjdev_Comm_initNative (JNIEnv *env, jclass obj, jobjectArray argv)
{
  jsize   i;
  jclass  string;
  jstring jc;
  jobject value;
  int len      = (*env)->GetArrayLength(env,argv);
  char** sargs = (char**)calloc(len+1, sizeof(char*));

  for (i=0; i<len; i++) {

    jc       = (jstring)(*env)-> GetObjectArrayElement(env, argv, i);
    sargs[i] = (char*)calloc(strlen((*env)-> GetStringUTFChars(env,jc,0)) + 1,
                             sizeof(char));

    strcpy(sargs[i], (*env)-> GetStringUTFChars(env, jc, 0));
  }

  MPI_Init(&len, &sargs);

  string = (*env)-> FindClass(env, "java/lang/String");
  value  = (*env)-> NewObjectArray(env, len, string, NULL);

  for (i = 0; i < len; i++) {

    jc = (*env)-> NewStringUTF(env, sargs[i]);
    (*env)-> SetObjectArrayElement(env, value, i, jc);
  }

  return value;
}

/*
 * Class:     mpjdev_Comm
 * Method:    size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_mpjdev_Comm_size (JNIEnv * env, jobject jthis)
{
  int size;

  MPI_Comm_size((MPI_Comm)(intptr_t)((*env)->GetLongField(env,jthis,mpjdev_handleID)),
                &size);

  return size;
}

/*
 * Class:     mpjdev_Comm
 * Method:    id
 * Signature: ()I
 */
JNIEXPORT jint JNICALL 
Java_mpjdev_Comm_id (JNIEnv * env, jobject jthis)
{

  int rank;

  MPI_Comm_rank((MPI_Comm)(intptr_t)((*env)->GetLongField(env,jthis,mpjdev_handleID)),
                &rank);

  return rank;
}

/*
 * Class:     mpjdev_Comm
 * Method:    dup
 * Signature: ()Lmpjdev/Comm;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Comm_dup (JNIEnv * env, jobject jthis)
{

  MPI_Comm new_comm;
  jclass   commClass  = (*env)-> GetObjectClass(env, jthis);
  jobject  commObject = (*env)-> NewObject(env, commClass, mpjdev_constructorID);
  
  MPI_Comm_dup((MPI_Comm)(intptr_t)((*env)->GetLongField(env,jthis,mpjdev_handleID)), 
               &new_comm);

  (*env)-> SetLongField(env, commObject, mpjdev_handleID, (long)new_comm);

  return commObject;
}

/*
 * Class:     mpjdev_Comm
 * Method:    create
 * Signature: ([I)Lmpjdev/Comm;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Comm_create (JNIEnv * env, jobject jthis, jintArray ids)
{

  MPI_Comm  new_comm;
  MPI_Group group, new_group;

  jclass  commClass;
  jobject commObject;

  int   size = (*env)-> GetArrayLength(env, ids);
  jint* pids = (*env)->GetIntArrayElements(env, ids, 0);

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)((*env)-> GetLongField(env, jthis,
                                                                 mpjdev_handleID));

  MPI_Comm_group(mpi_comm, &group);

  MPI_Group_incl(group, size, pids, &new_group);

  MPI_Comm_create(mpi_comm, new_group, &new_comm);

  (*env)->ReleaseIntArrayElements(env, ids, pids, JNI_ABORT);

  if (new_comm != MPI_COMM_NULL) {

    commClass  = (*env)-> GetObjectClass(env, jthis);
    commObject = (*env)-> NewObject(env, commClass, mpjdev_constructorID);
    (*env)->SetLongField(env, commObject, mpjdev_handleID, (long)new_comm);

    return commObject;
  } else 
    return NULL;
}

/*
 * Class:     mpjdev_Comm
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_Comm_free (JNIEnv * env, jobject jthis)
{
  MPI_Comm comm = (MPI_Comm)(intptr_t)((*env)->GetLongField(env, jthis,
                                                            mpjdev_handleID));
  MPI_Comm_free(&comm);
  (*env)->SetLongField(env, jthis, mpjdev_handleID, MPI_COMM_NULL);
}

/*
 * Class:     mpjdev_Comm
 * Method:    send
 * Signature: (Lmpjdev/Buffer;II)V
 */
JNIEXPORT void JNICALL 
Java_mpjdev_Comm_send (JNIEnv * env, jobject jthis, 
                            jobject buffer,  jint dest,    jint tag)
{

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)((*env)->GetLongField(env, jthis,
                                                                mpjdev_handleID));

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)-> GetLongField(env, buffer, 
                                                              mpjdev_jBufferId);

  char* pmessage   = pBuffer-> message;
  char* pObjHeader = pmessage + pBuffer-> size + MPJDEV_HEADER_SIZE;

  jbyteArray byte_array = (*env)-> CallObjectMethod(env, buffer, 
                                                    mpjdev_getObjectByteArrayID);

  ((jint*)pmessage)[1] = pBuffer-> size; /*set primary buffer size p.*/

  if (byte_array == NULL) {
    ((jint*)pObjHeader)[1] = 0;

    MPI_Send(pmessage, pBuffer-> size + 16, MPI_BYTE, dest, tag, mpi_comm);
  } else {

    jboolean isCopy;
    jint     size         = (*env)->GetArrayLength(env, byte_array);
    jbyte*   pbyte_array  = (*env)->GetByteArrayElements(env, byte_array, 
                                                         &isCopy);
    ((jint*)pObjHeader)[1] = size;

    if ((pBuffer-> size + size) < pBuffer-> capacity) {
      /* The secondary patload is not empty, and the sum of its size and
       * the size of the primary payload are less than the capacity of the 
       * buffer object at the sending end, the two concatenated togather
       * in a single message.
       */
      int i;
      /* Store object byte array into the secondary payload of message.*/
      for (i = 0; i < size; i ++)
        pObjHeader[i + MPJDEV_HEADER_SIZE] = pbyte_array[i];

      MPI_Send(pmessage, pBuffer-> size + size + 16, MPI_BYTE, dest, tag, 
               mpi_comm);
    } else {

      MPI_Send(pmessage, pBuffer-> size + 16, MPI_BYTE, dest, tag, mpi_comm);
      MPI_Send(pbyte_array, size, MPI_BYTE, dest, tag, mpi_comm);
    }
  }
}

/*
 * Class:     mpjdev_Comm
 * Method:    recv
 * Signature: (Lmpjdev/Buffer;II)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Comm_recv (JNIEnv * env, jobject jthis, 
                            jobject buffer,  jint source,     jint tag)
{

  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)((*env)-> GetLongField(env, jthis,
                                                                 mpjdev_handleID));
  char* pObjHeader;
  jint objMsgSize, primarySize;

  MPI_Status status;
  jclass     statusClass;
  jobject    statusObject;

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, buffer, 
                                                             mpjdev_jBufferId);

  char* pmessage = pBuffer-> message;
  char encode    = pmessage[0];

  MPI_Recv(pBuffer-> message, pBuffer-> capacity + 16, MPI_BYTE, source, tag, 
           mpi_comm, &status);

  statusClass  = (*env)-> FindClass(env, "mpjdev/Status");  
  statusObject = (*env)-> NewObject(env, statusClass, mpjdev_status_constructorID);

  (*env)->SetIntField(env, statusObject, mpjdev_sourceID, status.MPI_SOURCE);
  (*env)->SetIntField(env, statusObject, mpjdev_tagID, status.MPI_TAG);

  if (encode == pmessage[0]) {

    primarySize = ((jint*)pmessage)[1];
    pObjHeader  = pmessage + primarySize + MPJDEV_HEADER_SIZE;
    objMsgSize  = ((jint*)pObjHeader)[1];
  } else {
    ((char*)primarySize)[0] = pmessage[7];
    ((char*)primarySize)[1] = pmessage[6];
    ((char*)primarySize)[2] = pmessage[5];
    ((char*)primarySize)[3] = pmessage[4];

    pObjHeader = pmessage + primarySize + MPJDEV_HEADER_SIZE;

    ((char*)objMsgSize)[0] = pObjHeader[7];
    ((char*)objMsgSize)[1] = pObjHeader[6];
    ((char*)objMsgSize)[2] = pObjHeader[5];
    ((char*)objMsgSize)[3] = pObjHeader[4];
  }

  if (objMsgSize != 0) {

    jbyteArray byte_array = (*env)-> NewByteArray(env, objMsgSize);
    jbyte* pbyte_array = (*env)-> GetByteArrayElements(env, byte_array, NULL);
    int recv_size;

    MPI_Get_count(&status, MPI_BYTE, &recv_size);

    if ((primarySize + 16) < recv_size) {

      int i;
      for (i = 0; i < objMsgSize; i++)
        pbyte_array[i] = pObjHeader[MPJDEV_HEADER_SIZE + i];

    } else {
 
      MPI_Recv(pbyte_array, objMsgSize, MPI_BYTE, source, tag, mpi_comm, 
               &status);
    }      
    
    (*env)-> SetObjectField(env, buffer, mpjdev_fid, byte_array);
    (*env)-> ReleaseByteArrayElements(env, byte_array, pbyte_array, 0);
  }
  
  return statusObject;
}

/*
 * Class:     mpjdev_Comm
 * Method:    isend
 * Signature: (Lmpjdev/Buffer;II)Lmpjdev/Request;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Comm_isend (JNIEnv * env,    jobject jthis, 
                             jobject buffer,  jint dest,    jint tag)
{

  MPI_Request request;
  
  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)((*env)-> GetLongField(env, jthis,
                                                                 mpjdev_handleID));

  jclass requestClass = (*env)-> FindClass(env, "mpjdev/Request");
  jobject hdr_request = (*env)-> NewObject(env, requestClass, 
                                           mpjdev_request_constructorID);

  jobject obj_request = (*env)-> NewObject(env, requestClass, 
                                           mpjdev_request_constructorID);
 
  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, buffer, 
                                                             mpjdev_jBufferId);

  char* pmessage   = pBuffer-> message;
  char* pObjHeader = pmessage + pBuffer-> size + MPJDEV_HEADER_SIZE;

  jbyteArray byte_array = (*env)-> CallObjectMethod(env, buffer, 
                                                    mpjdev_getObjectByteArrayID);

  ((jint*)pmessage)[1] = pBuffer-> size; /*set primary buffer size p.*/

  if (byte_array == NULL) { /* Primitive case.*/
    ((jint*)pObjHeader)[1] = 0;

    MPI_Isend(pmessage, pBuffer-> size + 16, MPI_BYTE, dest, tag, 
              mpi_comm, &request);
    
    (*env)->SetLongField(env, hdr_request, mpjdev_reqhandleID, (long)request);
    
    return hdr_request;

  } else {  /*OBJECT case.*/
 
    jboolean isCopy;
    jint size           = (*env)->GetArrayLength(env, byte_array);
    jbyte* pbyte_array  = (*env)->GetByteArrayElements(env, byte_array, 
                                                       &isCopy);
    ((jint*)pObjHeader)[1] = size;

    if ((pBuffer-> size + size) < pBuffer-> capacity) {
      /* The secondary patload is not empty, and the sum of its size and
       * the size of the primary payload are less than the capacity of the 
       * buffer object at the sending end, the two concatenated togather
       * in a single message.
       */
      int i;
      /* Store object byte array into the secondary payload of message.*/
      for (i = 0; i < size; i ++)
        pObjHeader[i + MPJDEV_HEADER_SIZE] = pbyte_array[i];

      MPI_Isend(pmessage, pBuffer-> size + size + 16, MPI_BYTE, dest, tag, 
                mpi_comm, &request);
      
      (*env)->SetLongField(env, hdr_request, mpjdev_reqhandleID, (long)request);
      
      return hdr_request;
    } else {

      MPI_Isend(pmessage, pBuffer-> size + 16, MPI_BYTE, dest, tag, 
                mpi_comm, &request);
      
      (*env)-> SetLongField(env, hdr_request, mpjdev_reqhandleID, (long)request);
      
      MPI_Isend(pbyte_array, size, MPI_BYTE, dest, tag, mpi_comm, &request);
      
      (*env)-> SetIntField   (env, obj_request, mpjdev_opTagID,     0);
      (*env)-> SetIntField   (env, obj_request, mpjdev_typeTagID,   1);
      (*env)-> SetLongField  (env, obj_request, mpjdev_reqhandleID, (long)request);
      (*env)-> SetObjectField(env, obj_request, mpjdev_hdrReqID,    hdr_request);

      return obj_request;
    }
  }
}

/*
 * Class:     mpjdev_Comm
 * Method:    irecv
 * Signature: (Lmpjdev/Buffer;II)Lmpjdev/Request;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Comm_irecv (JNIEnv * env, jobject jthis, 
                             jobject buffer,  jint source, jint tag)
{

  MPI_Request request;
  MPI_Comm mpi_comm = (MPI_Comm)(intptr_t)((*env)-> GetLongField(env, jthis,
                                                                 mpjdev_handleID));
  
  jclass requestClass = (*env)-> FindClass(env, "mpjdev/Request");
  jobject hdr_request = (*env)-> NewObject(env, requestClass, 
                                           mpjdev_request_constructorID);

  Buffer *pBuffer = (Buffer *)(intptr_t)(*env)->GetLongField(env, buffer, 
                                                             mpjdev_jBufferId);

  MPI_Irecv(pBuffer-> message, pBuffer->capacity + 16, MPI_BYTE, source, 
            tag, mpi_comm, &request);
  
  (*env)-> SetIntField   (env, hdr_request, mpjdev_opTagID    , 1);
  (*env)-> SetIntField   (env, hdr_request, mpjdev_typeTagID  , 1);
  (*env)-> SetIntField   (env, hdr_request, mpjdev_r_tagID    , tag);
  (*env)-> SetLongField  (env, hdr_request, mpjdev_reqhandleID, (long)request);
  (*env)-> SetLongField  (env, hdr_request, mpjdev_commID     , (long)mpi_comm);
  (*env)-> SetObjectField(env, hdr_request, mpjdev_bufferID   , buffer);

  return hdr_request;
}

/*
 * Class:     mpjdev_Comm
 * Method:    finish
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpjdev_Comm_finish (JNIEnv *env, jclass obj)
{
/*
  int flag = 0;
  MPI_Finalized(&flag);

  if(!flag)
*/
    MPI_Finalize();
}

