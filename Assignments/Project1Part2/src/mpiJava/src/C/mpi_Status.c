/*
 * File         : mpi_Status.c
 * Headerfile   : mpi_Status.h 
 * Author       : Sung-Hoon Ko, Xinying Li
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.9 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Status.h"
#include "mpiJava.h"

jfieldID stathandleID;
jfieldID sourceID, tagID, elementsID, indexID;

/* Field IDs in `Datatype' */
extern jfieldID DatatypehandleID, DatatypebaseTypeID ;

extern int* dt_sizes ;


/*jmethodID handleConstructorID ;*/

/* jclass status_class ; */

extern void clearFreeList(JNIEnv*) ;

/*
 * Class:     mpi_Status
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Status_init
  (JNIEnv *env, jclass jthis)
{
  stathandleID = (*env)->GetFieldID(env,jthis,"handle","J");

  sourceID     = (*env)->GetFieldID(env,jthis,"source","I");
  tagID        = (*env)->GetFieldID(env,jthis,"tag","I");
  indexID      = (*env)->GetFieldID(env,jthis,"index","I");
  elementsID   = (*env)->GetFieldID(env,jthis,"elements","I");

  /* handleConstructorID = (*env)->GetMethodID(env, jthis, "<init>", "()V");*/

  /* status_class = (*env)->NewGlobalRef(env, jthis) ; */
}

/*
 * Class:     mpi_Status
 * Method:    alloc
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Status_alloc
  (JNIEnv *env, jobject jthis) {
  MPI_Status *status = (MPI_Status*) malloc(sizeof(MPI_Status));

  (*env)->SetLongField(env, jthis, stathandleID, (jlong)status);
}

/*
 * Class:     mpi_Status
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Status_free
  (JNIEnv *env, jobject jthis) {
   MPI_Status *status =
      (MPI_Status *)((*env)->GetLongField(env,jthis,stathandleID));
   free(status) ;
}

/*
 * Class:     mpi_Status
 * Method:    get_count
 * Signature: (Lmpi/Datatype;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Status_get_1count(JNIEnv *env, jobject jthis,
                                                  jobject type) {
    int count;

    MPI_Datatype datatype =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    MPI_Status *stat =
            (MPI_Status*)((*env)->GetLongField(env,jthis,stathandleID));

#ifdef GC_DOES_PINNING

  clearFreeList(env) ;

    MPI_Get_count(stat, datatype, &count) ;
    return count;

#else

    int elements = (*env)->GetIntField(env, jthis, elementsID) ;

    int dt_size ;

  clearFreeList(env) ;

    MPI_Type_size(datatype, &dt_size) ;

    if(elements != -1) {
        count = elements / dt_size ;  /* Cached at start of send buffer. */

        if(count * dt_size == elements)
            return count ;
        else
            return MPI_UNDEFINED ;
    }
    else {
        /* Status object returned by IPROBE or PROBE.
         *
         * Didn't have access to data buffer to find `elements' value,
         * so only way to find `count' is to invert `MPI_PACK_SIZE'.
         */

        int bsize, bsizeTrial ;
        MPI_Get_count(stat, MPI_BYTE, &bsize) ;

        bsize -= sizeof(int) ;

        count = bsize / dt_size ;
        MPI_Pack_size(count, datatype, MPI_COMM_WORLD, &bsizeTrial) ;
            /* Strictly, we should use the communicator the message was
             * received on, but I'm too lazy to cache it.
             */

        while(bsizeTrial > bsize) {
            count-- ;
            MPI_Pack_size(count, datatype, MPI_COMM_WORLD, &bsizeTrial) ;
        }

        if(bsizeTrial == bsize) 
            return count ;
        else
            return MPI_UNDEFINED ;
    }

#endif  /* GC_DOES_PINNING */
}

/*
 * Class:     mpi_Status
 * Method:    Test_cancelled
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpi_Status_Test_1cancelled
  (JNIEnv *env, jobject jthis)
{
   int flag;
   MPI_Status *stat;  /*shko*/

  clearFreeList(env) ;

   stat=(MPI_Status *)((*env)->GetLongField(env,jthis,stathandleID));/*shko*/

   MPI_Test_cancelled(stat, &flag);
   if(flag==0)
        return JNI_FALSE;
    else
        return JNI_TRUE;
}

/*
 * Class:     mpi_Status
 * Method:    get_elements
 * Signature: (Lmpi/Datatype;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Status_get_1elements(JNIEnv *env,
        jobject jthis, jobject type) {

    int count;

    MPI_Datatype datatype =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    MPI_Status *stat =
            (MPI_Status*)((*env)->GetLongField(env,jthis,stathandleID));

#ifdef GC_DOES_PINNING

  clearFreeList(env) ;

    MPI_Get_elements(stat, datatype, &count) ;
    return count;

#else

    int elements = (*env)->GetIntField(env, jthis, elementsID) ;
    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;
 
    int dt_size = dt_sizes [baseType] ;
 
  clearFreeList(env) ;

    if(elements != -1) {
        count = elements / dt_size ;
 
        if(count * dt_size == elements)
           return count ;
        else
           return MPI_UNDEFINED ;
               /* Can only happen if illegal base type mismatch between
                * sender and receiver?
                */
    }
    else {
        /* Status object returned by IPROBE or PROBE.
         * 
         * Didn't have access to data buffer to find `elements' value,
         * so only way to find `count' is to invert `MPI_PACK_SIZE'.
         */
 
        int bsize, bsizeTrial ;
        MPI_Get_count(stat, MPI_BYTE, &bsize) ;
 
        bsize -= sizeof(int) ;
 
        count = bsize / dt_size ;
        MPI_Pack_size(count, datatype, MPI_COMM_WORLD, &bsizeTrial) ;
           /* Strictly, we should use the communicator the message was
            * received on, but I'm too lazy to cache it.
            */
 
        while(bsizeTrial > bsize) {
           count-- ;
           MPI_Pack_size(count, datatype, MPI_COMM_WORLD, &bsizeTrial) ;
        }
 
        if(bsizeTrial == bsize) 
           return count ;
        else
           return MPI_UNDEFINED ;
                    /* Can only happen if illegal base type mismatch between
                     * sender and receiver?
                     */
    }

#endif GC_DOES_PINNING
}

