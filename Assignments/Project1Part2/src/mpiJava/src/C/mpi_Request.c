/*
 * File         : mpi_Request.c
 * Headerfile   : mpi_Request.h 
 * Author       : Sung-Hoon Ko, Xinying Li, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.11 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>

#include "mpi_Request.h"
#include "mpiJava.h"

/* Field IDs in `Status' */
extern jfieldID stathandleID ;
extern jfieldID sourceID, tagID, indexID, elementsID ;

/* Field IDs in `Request' */
jfieldID reqhandleID;
jfieldID opTagID ;
jfieldID bufSaveID, countSaveID, offsetSaveID ;
jfieldID baseTypeSaveID, bufbaseSaveID, bufptrSaveID ;
jfieldID commSaveID, typeSaveID ;

extern void clearFreeList(JNIEnv*) ;

/* Following functions defined in `mpi_Comm.c': */

void releaseBufPtr(JNIEnv *env, jobject buf, void* bufbase, int baseType) ;

#ifndef GC_DOES_PINNING

extern MPI_Datatype Dts[] ;

void releaseMPIBuf(JNIEnv *env, jobject buf, int offset,
                   int count, MPI_Datatype type, MPI_Comm comm,
                   void* bufptr, int size, int baseType) ;

void releaseMPIRecvBuf(int* elements, JNIEnv *env, jobject buf, int offset,
                       int count, MPI_Datatype type, MPI_Comm comm,
                       void* bufptr, MPI_Status* status,
                       int baseType) ;

void releaseMPIReadBuf(void* bufptr) ;

#endif  /* GC_DOES_PINNING */

void releaseBuf(int* elements, JNIEnv *env, jobject req, MPI_Status* status) {

  int opTag = (*env)->GetIntField(env, req, opTagID) ;

  switch(opTag) {
    case 0 : {  /* Request.OP_SEND */

#ifdef GC_DOES_PINNING

      jobject buf   = (*env)->GetObjectField(env, req, bufSaveID) ;
      int baseType  = (*env)->GetIntField(env, req, baseTypeSaveID) ;
      void* bufbase =
	      (void*) (*env)->GetLongField(env, req, bufbaseSaveID) ;

      releaseBufPtr(env, buf, bufbase, baseType) ;

      /* Try not to create too many local references... */
      (*env)->DeleteLocalRef(env, buf) ;

#else

      void* bufptr = (void*) (*env)->GetLongField(env, req, bufptrSaveID) ;

      releaseMPIReadBuf(bufptr) ;

#endif  /* GC_DOES_PINNING */

      break ;
    }
    case 1 : {  /* Request.OP_RECV */

      jobject buf   = (*env)->GetObjectField(env, req, bufSaveID) ;
      int baseType  = (*env)->GetIntField(env, req, baseTypeSaveID) ;

#ifdef GC_DOES_PINNING

      void* bufbase =
	      (void*) (*env)->GetLongField(env, req, bufbaseSaveID) ;

      releaseBufPtr(env, buf, bufbase, baseType) ;

#else

      int offset = (*env)->GetIntField(env, req, offsetSaveID) ;
      int count  = (*env)->GetIntField(env, req, countSaveID) ;

      MPI_Comm mpi_comm =
	      (MPI_Comm) (*env)->GetLongField(env, req, commSaveID) ;
      MPI_Datatype mpi_type =
	      (MPI_Datatype) (*env)->GetLongField(env, req, typeSaveID) ;
      void* bufptr =
	      (void*) (*env)->GetLongField(env, req, bufptrSaveID) ;

      releaseMPIRecvBuf(elements, env, buf, offset, count, mpi_type,
			mpi_comm, bufptr, status, baseType) ;

#endif  /* GC_DOES_PINNING */

      /* Try not to create too many local references... */
      (*env)->DeleteLocalRef(env, buf) ;
    }
  }
}


/*
 * Class:     mpi_Request
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Request_init(JNIEnv *env, jclass thisClass) {

  reqhandleID    = (*env)->GetFieldID(env, thisClass, "handle", "J") ;

  opTagID        = (*env)->GetFieldID(env, thisClass, "opTag", "I") ;

  bufSaveID      = (*env)->GetFieldID(env, thisClass, "bufSave",
                                      "Ljava/lang/Object;") ;
  countSaveID    = (*env)->GetFieldID(env, thisClass, "countSave", "I") ;
  offsetSaveID   = (*env)->GetFieldID(env, thisClass, "offsetSave", "I") ;

  baseTypeSaveID = (*env)->GetFieldID(env, thisClass, "baseTypeSave", "I") ;
  bufbaseSaveID  = (*env)->GetFieldID(env, thisClass, "bufbaseSave", "J") ;
  bufptrSaveID   = (*env)->GetFieldID(env, thisClass, "bufptrSave", "J") ;
  commSaveID     = (*env)->GetFieldID(env, thisClass, "commSave", "J") ;
  typeSaveID     = (*env)->GetFieldID(env, thisClass, "typeSave", "J") ;
}                      

/*
 * Class:     mpi_Request
 * Method:    GetReq
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Request_GetReq
  (JNIEnv *env, jobject jthis, jint type)
{
  switch (type) {
    case 0:
      (*env)->SetLongField(env,jthis,reqhandleID,(jlong)MPI_REQUEST_NULL);
      break;
	default:
	  break;
  }
}

/*
 * Class:     mpi_Request
 * Method:    Cancel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Request_Cancel
  (JNIEnv *env, jobject jthis)
{
   MPI_Request req;

  clearFreeList(env) ;

   req=(MPI_Request)((*env)->GetLongField(env,jthis,reqhandleID));
   MPI_Cancel(&req);
}

/*
 * Class:     mpi_Request
 * Method:    Free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Request_Free
  (JNIEnv *env, jobject jthis)
{
   MPI_Request req;

  clearFreeList(env) ;

   req=(MPI_Request)((*env)->GetLongField(env,jthis,reqhandleID));
   MPI_Request_free(&req);
}

/*
 * Class:     mpi_Request
 * Method:    Is_null
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpi_Request_Is_1null
  (JNIEnv *env, jobject jthis)
{
  MPI_Request req;

  clearFreeList(env) ;

  req=(MPI_Request)((*env)->GetLongField(env,jthis,reqhandleID));
  if(req==MPI_REQUEST_NULL)
    return JNI_TRUE;
  else 
    return JNI_FALSE;
}

/*
 * Class:     mpi_Request
 * Method:    Wait
 * Signature: (Lmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Request_Wait
  (JNIEnv *env, jobject jthis, jobject stat)
{
  int elements ;

  MPI_Request req =
      (MPI_Request)((*env)->GetLongField(env,jthis,reqhandleID)) ;

  MPI_Status *status =
      (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  MPI_Wait(&req, status);

  clearFreeList(env) ;

  releaseBuf(&elements, env, jthis, status) ;

  (*env)->SetLongField(env,jthis,reqhandleID,(jlong)req);

  (*env)->SetIntField(env,stat, sourceID, status->MPI_SOURCE);
  (*env)->SetIntField(env,stat, tagID, status->MPI_TAG);

  (*env)->SetIntField(env, stat, elementsID, elements);

  return stat;
}

/*
 * Class:     mpi_Request
 * Method:    Test
 * Signature: (Lmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Request_Test
  (JNIEnv *env, jobject jthis, jobject stat)
{
  int flag;
  MPI_Request req = (MPI_Request)((*env)->GetLongField(env,jthis,reqhandleID));
  MPI_Status *status =
      (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  clearFreeList(env) ;

  MPI_Test(&req, &flag, status);

  (*env)->SetLongField(env,jthis,reqhandleID,(jlong)req);

  if(flag) {
    int elements ;

    releaseBuf(&elements, env, jthis, status) ;

    (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
    (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

    (*env)->SetIntField(env, stat, elementsID, elements);

    return stat;
  }
  else
    return NULL;
}

/*
 * Class:     mpi_Request
 * Method:    Waitany
 * Signature: ([Lmpi/Request;Lmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Request_Waitany
  (JNIEnv *env, jclass jthis, jobjectArray array_of_request, jobject stat)
{
  int i, index, elements ;
  int count=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(count, sizeof(MPI_Request));

  jobject req ;

  MPI_Status *status =
      (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  clearFreeList(env) ;

  for(i=0; i<count; i++)
    reqs[i]=(MPI_Request)((*env)->GetLongField(env,
	       (*env)->GetObjectArrayElement(env,array_of_request,i),
               reqhandleID)) ;

  MPI_Waitany(count, reqs, &index, status);

  for(i=0; i<count; i++) {
    jobject reqi = (*env)->GetObjectArrayElement(env,array_of_request,i) ;
    (*env)->SetLongField(env, reqi, reqhandleID, (jlong) reqs[i]) ;
    if(i == index) req = reqi ;
  }

  releaseBuf(&elements, env, req, status) ;

  (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
  (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

  (*env)->SetIntField(env,stat, indexID, index);
  (*env)->SetIntField(env, stat, elementsID, elements);

  free(reqs);

  return stat;
}

/*
 * Class:     mpi_Request
 * Method:    Testany
 * Signature: ([Lmpi/Request;Lmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Request_Testany
  (JNIEnv *env, jclass jthis, jobjectArray array_of_request, jobject stat)
{
  int i,flag,index;
  int count=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(count, sizeof(MPI_Request));
  MPI_Status *status =
      (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  clearFreeList(env) ;

  for(i=0; i<count; i++)
    reqs[i]=(MPI_Request)((*env)->GetLongField(env,
                (*env)->GetObjectArrayElement(env,array_of_request,i),
                reqhandleID));

  MPI_Testany(count, reqs, &index,&flag, status);
  
  for(i=0; i<count; i++)
    (*env)->SetLongField(env,
                (*env)->GetObjectArrayElement(env,array_of_request,i),
                reqhandleID, (jlong) reqs[i]);

  free(reqs);

  if(flag) {
    int elements ;

    jobject req = (*env)->GetObjectArrayElement(env, array_of_request, index) ;

    releaseBuf(&elements, env, req, status) ;

    (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
    (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

    (*env)->SetIntField(env,stat, indexID,index);
    (*env)->SetIntField(env, stat, elementsID, elements);

    return stat;
  }
  else
    return NULL;
}

/*
 * Class:     mpi_Request
 * Method:    waitall
 * Signature: ([Lmpi/Request;)[Lmpi/Status;
 */
JNIEXPORT jobjectArray JNICALL Java_mpi_Request_waitall
  (JNIEnv *env, jclass jthis, jobjectArray array_of_request)
{
  int i;
  int count=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(2 * count, sizeof(MPI_Request));
  MPI_Request *reqs_ini = reqs + count ;
  MPI_Status *stas=(MPI_Status*)calloc(count, sizeof(MPI_Status));

  jclass status_class = (*env)->FindClass(env,"mpi/Status");
  jobjectArray array_of_status = 
    (*env)->NewObjectArray(env,count,status_class,NULL);

  jmethodID handleConstructorID = 
    (*env)->GetMethodID(env, status_class, "<init>", "()V");

  clearFreeList(env) ;

  /* Copy initial native requests in Java `array_of_request' to `reqs'. */

  for(i=0; i<count; i++) {
    reqs [i] = (MPI_Request)((*env)->GetLongField(env,
                  (*env)->GetObjectArrayElement(env,array_of_request,i),
                  reqhandleID));
    reqs_ini [i] = reqs [i] ;
  }

  MPI_Waitall(count, reqs, stas);

  for(i=0; i<count; i++) 
    if(reqs_ini [i] != MPI_REQUEST_NULL) {
      int elements ;

      jobject req  = (*env)->GetObjectArrayElement(env,array_of_request,i) ;

      jobject jstas = (*env)->NewObject(env,status_class,handleConstructorID);

      MPI_Status *status =
          (MPI_Status *)((*env)->GetLongField(env,jstas,stathandleID));

      /* Copy final native request to `array_of_request'. */
      (*env)->SetLongField(env, req, reqhandleID, (jlong) reqs[i]) ;

      /* Copy final native status to Java `array_of_status'... */
      *status = stas [i] ;

      releaseBuf(&elements, env, req, status) ;

      (*env)->SetIntField(env,jstas,sourceID,status->MPI_SOURCE);
      (*env)->SetIntField(env,jstas,tagID,status->MPI_TAG);   

      (*env)->SetIntField(env, jstas, elementsID, elements);

      (*env)->SetObjectArrayElement(env,array_of_status,i,jstas);

      /* Try not to create too many local references... */
      (*env)->DeleteLocalRef(env, req) ;
      (*env)->DeleteLocalRef(env, jstas) ;
    }
    
  free(reqs);
  free(stas);

  return array_of_status;
}


/*
 * Class:     mpi_Request
 * Method:    testall
 * Signature: ([Lmpi/Request;)[Lmpi/Status;
 */
JNIEXPORT jobjectArray JNICALL Java_mpi_Request_testall
  (JNIEnv *env, jclass jthis, jobjectArray array_of_request)
{
  int i,flag;
  int count=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(2 * count, sizeof(MPI_Request));
  MPI_Request *reqs_ini = reqs + count ;
  MPI_Status *stas=(MPI_Status*)calloc(count, sizeof(MPI_Status));

  jclass status_class = (*env)->FindClass(env,"mpi/Status");
  jobjectArray array_of_status = 
    (*env)->NewObjectArray(env,count,status_class,NULL);

  jmethodID handleConstructorID = 
    (*env)->GetMethodID(env, status_class, "<init>", "()V");

  clearFreeList(env) ;

  /* Copy initial native requests in Java `array_of_request' to `reqs'. */

  for(i=0; i<count; i++) {
    reqs [i] =(MPI_Request)((*env)->GetLongField(env,
                 (*env)->GetObjectArrayElement(env,array_of_request,i),
                 reqhandleID)) ;
    reqs_ini [i] = reqs [i] ;
  }

  MPI_Testall(count, reqs, &flag, stas);

  if(flag)
    for(i=0; i<count; i++)
      if(reqs_ini [i] != MPI_REQUEST_NULL) {
        int elements ;

        jobject req  = (*env)->GetObjectArrayElement(env,array_of_request,i) ;

        jobject jstas = (*env)->NewObject(env,status_class,
                                          handleConstructorID);
        MPI_Status *status =
            (MPI_Status *)((*env)->GetLongField(env,jstas,stathandleID));

        /* Copy final native request to `array_of_request'. */

        (*env)->SetLongField(env, req, reqhandleID, (jlong) reqs[i]) ;

        /* Copy final native status to Java `array_of_status'... */

        *status = stas [i] ;

        releaseBuf(&elements, env, req, status) ;

        (*env)->SetIntField(env,jstas,sourceID,status->MPI_SOURCE);
        (*env)->SetIntField(env,jstas,tagID,status->MPI_TAG);   

        (*env)->SetIntField(env, jstas, elementsID, elements);

        (*env)->SetObjectArrayElement(env,array_of_status,i,jstas);

        /* Try not to create too many local references... */

        (*env)->DeleteLocalRef(env, req) ;
        (*env)->DeleteLocalRef(env, jstas) ;
      }
    
  free(reqs);
  free(stas);

  if(flag)
    return array_of_status ;
  else
    return NULL;
}

/*
 * Class:     mpi_Request
 * Method:    waitsome
 * Signature: ([Lmpi/Request;)[Lmpi/Status;
 */
JNIEXPORT jobjectArray JNICALL Java_mpi_Request_waitsome
  (JNIEnv *env, jclass jthis, jobjectArray array_of_request)
{
  int i;
  int incount=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(incount, sizeof(MPI_Request));
  MPI_Status *stas=(MPI_Status*)calloc(incount, sizeof(MPI_Status));
  int *array_of_indices=(int*)calloc(incount,sizeof(int));
  int outcount;

  jclass status_class = (*env)->FindClass(env,"mpi/Status");
  jobjectArray array_of_status;

  jmethodID handleConstructorID = 
    (*env)->GetMethodID(env, status_class, "<init>", "()V");

  clearFreeList(env) ;

  /* Copy initial native requests in Java `array_of_request' to `reqs'. */

  for(i=0; i<incount; i++)
    reqs[i] = (MPI_Request)((*env)->GetLongField(env,
	         (*env)->GetObjectArrayElement(env,array_of_request,i),
                 reqhandleID));

  MPI_Waitsome(incount, reqs, &outcount, array_of_indices, stas); 

  if(outcount!=MPI_UNDEFINED) {
    array_of_status=(*env)->NewObjectArray(env,outcount,status_class,NULL);

    for(i=0; i<outcount; i++) {
      int elements ;
      int index    = array_of_indices[i] ;

      jobject req  = (*env)->GetObjectArrayElement(env,array_of_request,
                                                   index) ;

      jobject jstas = (*env)->NewObject(env,status_class,handleConstructorID);
      MPI_Status *status =
          (MPI_Status *)((*env)->GetLongField(env,jstas,stathandleID));

      /* Copy final native request to `array_of_request'. */

      (*env)->SetLongField(env, req, reqhandleID, (jlong) reqs[index]) ;

      /* Copy final native status to Java `array_of_status'... */

      *status = stas [i] ;

      releaseBuf(&elements, env, req, status) ;

      (*env)->SetIntField(env,jstas,sourceID,status->MPI_SOURCE);
      (*env)->SetIntField(env,jstas,tagID,status->MPI_TAG);   

      (*env)->SetIntField(env,jstas,indexID, index);
      (*env)->SetIntField(env, jstas, elementsID, elements);


      (*env)->SetObjectArrayElement(env,array_of_status,i,jstas);

      /* Try not to create too many local references... */

      (*env)->DeleteLocalRef(env, req) ;
      (*env)->DeleteLocalRef(env, jstas) ;
    }
  }

  free(reqs);
  free(stas);
  free(array_of_indices);

  if(outcount==MPI_UNDEFINED)
    return NULL;
  else
    return array_of_status;
}

/*
 * Class:     mpi_Request
 * Method:    testsome
 * Signature: ([Lmpi/Request;)[Lmpi/Status;
 */
JNIEXPORT jobjectArray JNICALL Java_mpi_Request_testsome
 (JNIEnv *env, jclass jthis, jobjectArray array_of_request)
{
  int i;
  int incount=(*env)->GetArrayLength(env,array_of_request);
  MPI_Request *reqs=(MPI_Request*)calloc(incount, sizeof(MPI_Request));
  MPI_Status *stas=(MPI_Status*)calloc(incount, sizeof(MPI_Status));
  int *array_of_indices=(int*)calloc(incount,sizeof(int));
  int outcount;

  jclass status_class = (*env)->FindClass(env,"mpi/Status");
  jobjectArray array_of_status;

  jmethodID handleConstructorID = 
    (*env)->GetMethodID(env, status_class, "<init>", "()V");

  clearFreeList(env) ;

  for(i=0; i<incount; i++) {
    reqs[i]=(MPI_Request)( (*env)->GetLongField(env,
        (*env)->GetObjectArrayElement(env,array_of_request,i),
        reqhandleID) );
  }

  MPI_Testsome(incount,reqs,&outcount,array_of_indices, stas);

  if(outcount!=MPI_UNDEFINED) {  
    array_of_status=(*env)->NewObjectArray(env,outcount,status_class,NULL);

    for(i=0; i<outcount; i++) {
      int elements ;
      int index    = array_of_indices[i] ;

      jobject req  = (*env)->GetObjectArrayElement(env,array_of_request,
                                                   index) ;

      jobject jstas = (*env)->NewObject(env,status_class,handleConstructorID);
      MPI_Status *status =
          (MPI_Status *)((*env)->GetLongField(env,jstas,stathandleID));

      /* Copy final native request to `array_of_request'.
         Release buffer elements... */

      (*env)->SetLongField(env, req, reqhandleID, (jlong) reqs[index]) ;

      /* Copy final native status to Java `array_of_status'... */

      *status = stas [i] ;

      releaseBuf(&elements, env, req, status) ;

      (*env)->SetIntField(env,jstas,sourceID,status->MPI_SOURCE);
      (*env)->SetIntField(env,jstas,tagID,status->MPI_TAG);   

      (*env)->SetIntField(env,jstas,indexID, index);
      (*env)->SetIntField(env, jstas, elementsID, elements);

      (*env)->SetObjectArrayElement(env,array_of_status,i,jstas);

      /* Try not to create too many local references... */

      (*env)->DeleteLocalRef(env, req) ;
      (*env)->DeleteLocalRef(env, jstas) ;
    }
  }

  free(reqs);
  free(stas);
  free(array_of_indices);
  
  if(outcount==MPI_UNDEFINED)
    return NULL;
  else
    return array_of_status;
}

/*
 * Things to do:
 *
 *   `Free' should release the buffer, if an operation was in progress?
 *
 *   Should be able to cache a global reference to `status_class'.
 *       Doesn't work for some reason.  Why?
 *
 *   Should be able to cache handleConstructorID in a static variable.
 *       Doesn't work with Linux IBM-JDK1.1.6. Why?
 *
 *   `bufptr' currently unused---may be deleted.
 */

