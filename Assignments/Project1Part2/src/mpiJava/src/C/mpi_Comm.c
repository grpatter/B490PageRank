/*
 * File         : mpi_Comm.c
 * Headerfile   : mpi_Comm.h 
 * Author       : Sung-Hoon Ko, Xinying Li, Sang Lim, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.17 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include <stdlib.h>

#include "mpi_Comm.h"
#include "mpiJava.h"

jfieldID CommhandleID;
extern jfieldID ErrhandleID, GrouphandleID;

/* Field IDs in `Datatype' */
extern jfieldID DatatypehandleID, DatatypebaseTypeID ;

/* Field IDs in `Status' */
extern jfieldID stathandleID ;
extern jfieldID sourceID, tagID, indexID, elementsID ;

/* Field IDs in `Request' */
extern jfieldID reqhandleID;
extern jfieldID opTagID ;
extern jfieldID bufSaveID, countSaveID, offsetSaveID ;
extern jfieldID baseTypeSaveID, bufbaseSaveID, bufptrSaveID ;
extern jfieldID commSaveID, typeSaveID ;

extern void clearFreeList(JNIEnv*) ;


/* `getBufPtr' is used in 
     Send, Recv ...
   for getting pointer from `jobject buf'
*/
void* getBufPtr(void** bufbase,
                JNIEnv *env, jobject buf, int baseType, int offset) {

    jboolean isCopy ;
    void* bufptr = 0 ;

    *bufbase = 0 ;

    switch (baseType) {
        case 0:   /* NULL */
            break;
        case 1: {
            jbyte* els = (*env)->GetByteArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 2: {
            jchar* els = (*env)->GetCharArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 3: {
            jshort* els = (*env)->GetShortArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 4: {
            jboolean* els = (*env)->GetBooleanArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 5: {
            jint* els = (*env)->GetIntArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 6: {
            jlong* els = (*env)->GetLongArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 7: {
            jfloat* els = (*env)->GetFloatArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        } 
        case 8: {
            jdouble* els = (*env)->GetDoubleArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 9: {
            jbyte* els = (*env)->GetByteArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        case 10:
            break;
        case 11:
            break;
        case 12: {
            jbyte* els = (*env)->GetByteArrayElements(env,buf,&isCopy) ;
            *bufbase = els ;
            bufptr   = els + offset ;
            break;
        }
        default:
            break;  /* `UNDEFINED' */
    }

    return bufptr ;
}


/* `releaseBufPtr' is used in 
     Send, Recv ...
   to release pointer obtained by `getBufPtr'.
*/

void releaseBufPtr(JNIEnv *env, jobject buf, void* bufbase, int baseType) {

    switch (baseType) {
        case 0:
            break;
        case 1:
            (*env)->ReleaseByteArrayElements(env,buf,(jbyte*)bufbase,0);
            break;
        case 2:
            (*env)->ReleaseCharArrayElements(env,buf,(jchar*)bufbase,0);
            break;
        case 3:
            (*env)->ReleaseShortArrayElements(env,buf,(jshort*)bufbase,0);
            break;
        case 4:
            (*env)->ReleaseBooleanArrayElements(env,buf,(jboolean*)bufbase,0);
            break;
        case 5:
            (*env)->ReleaseIntArrayElements(env,buf,(jint*)bufbase,0);
            break;
        case 6:
            (*env)->ReleaseLongArrayElements(env,buf,(jlong*)bufbase,0);
            break;
        case 7:
            (*env)->ReleaseFloatArrayElements(env,buf,(jfloat*)bufbase,0);
            break;
        case 8:
            (*env)->ReleaseDoubleArrayElements(env,buf,(jdouble*)bufbase,0);
            break;
        case 9:
            (*env)->ReleaseByteArrayElements(env,buf,(jbyte*)bufbase,0);
            break;
        case 10:
            break;
        case 11:
            break;
        case 12:
           (*env)->ReleaseByteArrayElements(env,buf,(jbyte*)bufbase,0);
            break;
        default:
            break;
    }
}

#ifndef GC_DOES_PINNING

/* `getBufCritical' is used in 
     getMPIBuf, releaseMPIBuf...
   for getting pointer from `jobject buf'
*/
void* getBufCritical(void** bufbase,
                     JNIEnv *env, jobject buf, int baseType, int offset) {

  jboolean isCopy ;
  void* bufptr ;

  *bufbase = (jbyte*) (*env)->GetPrimitiveArrayCritical(env,buf,&isCopy) ;

  switch (baseType) {
    case 0:
      break;
    case 1: {
      bufptr  = ((jbyte*) *bufbase) + offset ;
      break;
    }
    case 2: {
      bufptr  = ((jchar*) *bufbase) + offset ;
      break;
    }
    case 3: {
      bufptr  = ((jshort*) *bufbase) + offset ;
      break;
    }
    case 4: {
      bufptr  = ((jboolean*) *bufbase) + offset ;
      break;
    }
    case 5: {
      bufptr  = ((jint*) *bufbase) + offset ;
      break;
    }
    case 6: {
      bufptr  = ((jlong*) *bufbase) + offset ;
      break;
    }
    case 7: {
      bufptr  = ((jfloat*) *bufbase) + offset ;
      break;
    } 
    case 8: {
      bufptr  = ((jdouble*) *bufbase) + offset ;
      break;
    }
    case 9: {
      bufptr  = ((jbyte*) *bufbase) + offset ;
      break;
    }
    case 10:
      break;
    case 11:
      break;
    case 12: {
      bufptr  = ((jbyte*) *bufbase) + offset ;
      break;
    }
    default:
      break;
  }

  return bufptr ;
}

extern MPI_Datatype Dts[] ;
extern int* dt_sizes ;

/*
 * If the VM doesn't support pinning of arrays by
 * native methods, we allocate a private buffer "by hand", and copy
 * just the required data to that buffer.
 *
 * This avoids having the VM copy the whole of the Java array, when
 * a message may only be a small portion of that array.  It also
 * avoids potentially erroneous behaviour when we have overlapping
 * operations on disjoint segments of the same Java array.
 *
 * `getMPIBuf' uses `Get/ReleasePrimitiveArrayCritical' to access the
 * Java array, hopefully without copying, and `MPI_Pack' to copy just
 * the required segment.
 *
 * (Note packed messages are sent and received using `MPI_BYTE',
 * not `MPI_PACKED'.  In some ways `MPI_PACKED' would be more natural
 * and flexible, but the specification of how it works is pretty
 * vague, and the reference implementation seems inconsistent,
 * and after spending too long trying to make it work I gave up... dbc)
 */

void* getMPIBuf(int* bsize, JNIEnv *env, jobject buf, int offset,
                int count, MPI_Datatype type, MPI_Comm comm,
                int baseType) {

    int dt_size ;

    void *bufptr, *javaptr, *bufbase ;
    int pos ;

    MPI_Pack_size(count, type, comm, bsize) ;
    *bsize += sizeof(int) ;

    bufptr = malloc(*bsize) ;

    MPI_Type_size(type, &dt_size) ;
    ((int*) bufptr) [0] = count * dt_size ;
         /* Append "elements" count to start of buffer.
          * (In practise this is a count of actual number of data bytes, 
          * excluding packing overheads.)
          */

    pos = sizeof(int) ;
    javaptr = getBufCritical(&bufbase, env, buf, baseType, offset);
    if(count != 0 && *bsize != pos)  /* LAM doesn't like count = 0 */
         MPI_Pack(javaptr, count, type, bufptr, *bsize, &pos, comm) ;
    (*env)->ReleasePrimitiveArrayCritical(env, buf, bufbase, 0);

    return bufptr ;
}

/*
 * Optimization of `getMPIBuf' for the case of receive buffers, where
 * we don't need to copy the original value.
 */
void* getMPIWriteBuf(int* bsize, int count, MPI_Datatype type, MPI_Comm comm) {

    MPI_Pack_size(count, type, comm, bsize) ;
    *bsize += sizeof(int) ;

    return malloc(*bsize) ;
}

/*
 * `releaseMPIBuf' uses `Get/ReleasePrimitiveArrayCritical' to access the
 * Java array, hopefully without copying, and `MPI_Unpack' to write just
 * the necessary segment.
 */
void releaseMPIBuf(JNIEnv *env, jobject buf, int offset,
                   int count, MPI_Datatype type, MPI_Comm comm,
                   void* bufptr, int bsize, int baseType) {

    void *bufbase ;
    int pos = sizeof(int) ;

    void *javaptr = getBufCritical(&bufbase, env, buf, baseType, offset);
    MPI_Unpack(bufptr, bsize, &pos, javaptr, count, type, comm) ;
    (*env)->ReleasePrimitiveArrayCritical(env, buf, bufbase, 0);

    free(bufptr) ;
}

/*
 * `releaseMPIRecvBuf' is a variant of `releaseMPIBuf' that retrieves
 * the number of elements to be copied back from an `MPI_Status' object.
 */
void releaseMPIRecvBuf(int* elements, JNIEnv *env, jobject buf, int offset,
                       int count, MPI_Datatype type, MPI_Comm comm,
                       void* bufptr, MPI_Status* status,
                       int baseType) {


    void *bufbase, *javaptr ;
    int bsize ;
    int pos ;
    
#ifdef UNPACK_ALLOWS_SHORT_BUFFER 

    MPI_Get_count(status, MPI_BYTE, &bsize) ;
    
    *elements = ((int*) bufptr) [0] ;

    pos = sizeof(int) ;

    javaptr = getBufCritical(&bufbase, env, buf, baseType, offset);
    MPI_Unpack(bufptr, bsize, &pos, javaptr, count, type, comm) ;
    (*env)->ReleasePrimitiveArrayCritical(env, buf, bufbase, 0);

#else

    /*
     * See thread "mpiJava on Sun HPC", Sep 2002, on java-mpi mailing list.
     *
     * MPICH and LAM allow short buffers.  Sun HPC and SP2 don't.
     *
     * As discussed on the mailing list, this solution will fail for
     * some (hopefully unusual) cases, but it is the best we can do for now.
     */

    int tsize ;

    MPI_Type_size(type, &tsize) ;

    MPI_Get_count(status, MPI_BYTE, &bsize) ;
    
    *elements = ((int*) bufptr) [0] ;

    pos = sizeof(int) ;

    javaptr = getBufCritical(&bufbase, env, buf, baseType, offset);
    MPI_Unpack(bufptr, bsize, &pos, javaptr, *elements / tsize, type, comm) ;
    (*env)->ReleasePrimitiveArrayCritical(env, buf, bufbase, 0);

#endif  /* UNPACK_ALLOWS_SHORT_BUFFER */

    free(bufptr) ;
}


/*
 * Optimization of `releaseMPIBuf' for the case of send buffers, where
 * we don't need to write back modified data.
 */
void releaseMPIReadBuf(void* bufptr) {

    free(bufptr) ;
}

#endif /* GC_DOES_PINNING */

/*
 * Class:     mpi_Comm
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_init(JNIEnv *env, jclass thisClass) {

    jfieldID nullHandleID =
            (*env)->GetStaticFieldID(env,thisClass,"nullHandle","J");
    (*env)->SetStaticLongField(env,thisClass,nullHandleID,MPI_COMM_NULL);

    CommhandleID = (*env)->GetFieldID(env,thisClass,"handle","J");
}

/*
 * Class:     mpi_Comm
 * Method:    GetComm
 * Signature: (I)J
 */
JNIEXPORT void JNICALL Java_mpi_Comm_GetComm(JNIEnv *env, jobject jthis,
                                             jint type) {
  clearFreeList(env) ;

    switch (type) {
        case 0:
            (*env)->SetLongField(env,jthis, CommhandleID,(jlong)MPI_COMM_NULL);
            break;
        case 1:
            (*env)->SetLongField(env,jthis, CommhandleID,(jlong)MPI_COMM_SELF); 
            break;
        case 2:
            (*env)->SetLongField(env,jthis, CommhandleID,(jlong)MPI_COMM_WORLD);
            break;
    }
}


/*
 * Class:     mpi_Comm
 * Method:    Size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Size(JNIEnv *env, jobject jthis) {

    int size;

  clearFreeList(env) ;

    MPI_Comm_size((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                  &size);
    return size;
}

/*
 * Class:     mpi_Comm
 * Method:    Rank
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Rank(JNIEnv *env, jobject jthis) {

    int rank;

  clearFreeList(env) ;

    MPI_Comm_rank((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                  &rank);
    return rank;
}

/*
 * Class:     mpi_Comm
 * Method:    Dup
 * Signature: ()Lmpi/Comm;
 */
JNIEXPORT jlong JNICALL Java_mpi_Comm_dup(JNIEnv *env, jobject jthis) {

    MPI_Comm newcomm;

  clearFreeList(env) ;

    MPI_Comm_dup((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)), 
                 &newcomm);

    return (jlong)newcomm;
}

/*
 * Class:     mpi_Comm
 * Method:    Compare
 * Signature: (Lmpi/Comm;Lmpi/Comm;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Compare(JNIEnv *env, jclass jthis,
                                             jobject comm1, jobject comm2) {

    int result;

  clearFreeList(env) ;

    MPI_Comm_compare((MPI_Comm)((*env)->GetLongField(env,comm1,CommhandleID)),
                     (MPI_Comm)((*env)->GetLongField(env,comm2,CommhandleID)),
                     &result);
    return result;
}

/*
 * Class:     mpi_Comm
 * Method:    Free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_Free(JNIEnv *env, jobject jthis) {

  MPI_Comm comm=(MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID));

  clearFreeList(env) ;

  MPI_Comm_free(&comm);
  (*env)->SetLongField(env,jthis, CommhandleID, MPI_COMM_NULL);
}

/*
 * Class:     mpi_Comm
 * Method:    Is_null
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpi_Comm_Is_1null(JNIEnv *env, jobject jthis) {

    MPI_Comm comm;

  clearFreeList(env) ;

    comm=(MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID));
    if(comm==MPI_COMM_NULL)
      return JNI_TRUE;
    else
      return JNI_FALSE;
}

/*
 * Class:     mpi_Comm
 * Method:    group
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_mpi_Comm_group(JNIEnv *env, jobject jthis) {

    MPI_Group group;

  clearFreeList(env) ;

    MPI_Comm_group((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                   &group);
    return (jlong)group;
}

/*
 * Class:     mpi_Comm
 * Method:    Test_inter
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpi_Comm_Test_1inter(JNIEnv *env,
                                                     jobject jthis) {

    int flag;

  clearFreeList(env) ;

    MPI_Comm_test_inter((MPI_Comm)((*env)->GetLongField(env,jthis,
                                                        CommhandleID)),&flag);
    if(flag==0)
        return JNI_FALSE;
    else
        return JNI_TRUE;
}

/*
 * Class:     mpi_Comm
 * Method:    GetIntercomm
 * Signature: (Lmpi/Comm;III)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Comm_GetIntercomm(JNIEnv *env, jobject jthis,
        jobject local_comm, jint local_leader, jint remote_leader, jint tag) {

    MPI_Comm newintercomm;

  clearFreeList(env) ;

    MPI_Intercomm_create((MPI_Comm)
                             (*env)->GetLongField(env,local_comm, CommhandleID),
                         local_leader,
                         (MPI_Comm)
                             (*env)->GetLongField(env,jthis,CommhandleID), 
                         remote_leader, tag, &newintercomm);
    return (jlong)newintercomm;
}

/*
 * Class:     mpi_Comm
 * Method:    send
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;II)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_send(JNIEnv *env, jobject jthis,
                                          jobject buf, jint offset, jint count,
                                          jobject type, jint dest, jint tag) {

    MPI_Comm mpi_comm =
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
             (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Send(bufptr, count, mpi_type, dest, tag, mpi_comm) ;
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;

    MPI_Send(bufptr, size, MPI_BYTE, dest, tag, mpi_comm) ;
    releaseMPIReadBuf(bufptr) ;

#endif  /* GC_DOES_PINNING */

}

/*
 * Class:     mpi_Comm
 * Method:    Recv
 * Signature:
(Ljava/lang/Object;IILmpi/Datatype;IILmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Recv(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint source, jint tag, jobject stat) {

    MPI_Status *status =
            (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Recv(bufptr, count, mpi_type, source, tag, mpi_comm, status);
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size, elements ;

  clearFreeList(env) ;

    bufptr = getMPIWriteBuf(&size, count, mpi_type, mpi_comm) ;
    MPI_Recv(bufptr, size, MPI_BYTE, source, tag, mpi_comm, status);
    releaseMPIRecvBuf(&elements, env, buf, offset, count, mpi_type,
                      mpi_comm, bufptr, status, baseType) ;

    (*env)->SetIntField(env, stat, elementsID, elements);

#endif  /* GC_DOES_PINNING */

    (*env)->SetIntField(env, stat, sourceID, status->MPI_SOURCE);
    (*env)->SetIntField(env, stat, tagID, status->MPI_TAG);

    return stat;
}

/*
 * Class:     mpi_Comm
 * Method:    Sendrecv
 * Signature:
(Ljava/lang/Object;IILmpi/Datatype;IILjava/lang/Object;IILmpi/Datatype;IILmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Sendrecv(JNIEnv *env, jobject jthis,
        jobject sbuf, jint soffset, jint scount, 
        jobject stype, jint dest, jint stag, 
        jobject rbuf, jint roffset, jint rcount, 
        jobject rtype, jint source, jint rtag, jobject stat) {

    MPI_Status *status =
            (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;

    MPI_Datatype mpi_stype =
            (MPI_Datatype)((*env)->GetLongField(env,stype,DatatypehandleID)) ;
    MPI_Datatype mpi_rtype =
            (MPI_Datatype)((*env)->GetLongField(env,rtype,DatatypehandleID)) ;

    int sbaseType = (*env)->GetIntField(env, stype, DatatypebaseTypeID) ;
    int rbaseType = (*env)->GetIntField(env, rtype, DatatypebaseTypeID) ;

    void *sbufptr, *rbufptr ;

#ifdef GC_DOES_PINNING 

    void *sbufbase, *rbufbase ;

  clearFreeList(env) ;

    rbufptr = getBufPtr(&rbufbase, env, rbuf, rbaseType, roffset) ;
    sbufptr = getBufPtr(&sbufbase, env, sbuf, sbaseType, soffset) ;

    MPI_Sendrecv(sbufptr, scount, mpi_stype, dest, stag, 
                 rbufptr, rcount, mpi_rtype, source, rtag,
                 mpi_comm, status);

    releaseBufPtr(env, sbuf, sbufbase, sbaseType) ;
    releaseBufPtr(env, rbuf, rbufbase, rbaseType) ;

           /* Important to release receive buffer after send buffer,
              in case actually the GC is not supporting pinning.
              If order was reversed and buffers were part of *same*
              Java array, copy-back of send buffer would overwrite
              modifications made copying back the receive buffer! */

#else

    int rsize, ssize, elements ;

  clearFreeList(env) ;

    rbufptr = getMPIWriteBuf(&rsize, rcount, mpi_rtype, mpi_comm) ;
    sbufptr = getMPIBuf(&ssize, env, sbuf, soffset,
                        scount, mpi_stype, mpi_comm, sbaseType) ;

    MPI_Sendrecv(sbufptr, ssize, MPI_BYTE, dest, stag, 
                 rbufptr, rsize, MPI_BYTE, source, rtag,
                 mpi_comm, status);

    releaseMPIReadBuf(sbufptr) ;
    releaseMPIRecvBuf(&elements, env, rbuf, roffset, rcount, mpi_rtype,
                      mpi_comm, rbufptr, status, rbaseType) ;

    (*env)->SetIntField(env, stat, elementsID, elements);

#endif  /* GC_DOES_PINNING */

    (*env)->SetIntField(env, stat, sourceID,status->MPI_SOURCE);
    (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

    return stat;
}

/*
 * Class:     mpi_Comm
 * Method:    Sendrecv_replace
 * Signature:
(Ljava/lang/Object;IILmpi/Datatype;IIILmpi/Datatype;IILmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Sendrecv_1replace(JNIEnv *env,
        jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint stag, jint source, jint rtag, jobject stat) {

    MPI_Status *status =
            (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Sendrecv_replace(bufptr, count, mpi_type,
                         dest, stag, source, rtag, mpi_comm, status);
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size, elements ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;

    MPI_Sendrecv_replace(bufptr, size, MPI_BYTE,
                         dest, stag, source, rtag, mpi_comm, status);

    releaseMPIRecvBuf(&elements, env, buf, offset, count, mpi_type,
                      mpi_comm, bufptr, status, baseType) ;

    (*env)->SetIntField(env, stat, elementsID, elements);

#endif  /* GC_DOES_PINNING */

    (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
    (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

    return stat;
}

/*
 * Class:     mpi_Comm
 * Method:    bsend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;II)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_bsend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag) {

    MPI_Comm mpi_comm =
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
             (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Bsend(bufptr, count, mpi_type, dest, tag, mpi_comm) ;
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;
    MPI_Bsend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm) ;
    releaseMPIReadBuf(bufptr) ;

#endif  /* GC_DOES_PINNING */
}

/*
 * Class:     mpi_Comm
 * Method:    ssend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;II)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_ssend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag) {

    MPI_Comm mpi_comm =
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
             (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Ssend(bufptr, count, mpi_type, dest, tag, mpi_comm) ;
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;
    MPI_Ssend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm) ;
    releaseMPIReadBuf(bufptr) ;

#endif  /* GC_DOES_PINNING */
}

/*
 * Class:     mpi_Comm
 * Method:    rsend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;II)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_rsend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag) {

    MPI_Comm mpi_comm =
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
             (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;
    MPI_Rsend(bufptr, count, mpi_type, dest, tag, mpi_comm) ;
    releaseBufPtr(env, buf, bufbase, baseType) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;
    MPI_Rsend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm) ;
    releaseMPIReadBuf(bufptr) ;

#endif  /* GC_DOES_PINNING */
}

/*
 * Class:     mpi_Comm
 * Method:    Isend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;IILmpi/Request;)Lmpi/Request;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Isend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag, jobject req) {

    MPI_Request request;

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;

    MPI_Isend(bufptr, count, mpi_type, dest, tag, mpi_comm, &request);

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetObjectField(env, req, bufSaveID, buf) ;
    (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;
    (*env)->SetLongField(env, req, bufbaseSaveID, (jlong) bufbase) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;

    MPI_Isend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm,
              &request) ;

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetLongField(env, req, bufptrSaveID, (jlong) bufptr) ;

#endif  /* GC_DOES_PINNING */

    (*env)->SetLongField(env,req,reqhandleID,(jlong)request);
    return req;
}

/*
 * Class:     mpi_Comm
 * Method:    Ibsend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;IILmpi/Request;)Lmpi/Request;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Ibsend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag, jobject req) {

    MPI_Request request;

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;

    MPI_Ibsend(bufptr, count, mpi_type, dest, tag, mpi_comm, &request);

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetObjectField(env, req, bufSaveID, buf) ;
    (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;
    (*env)->SetLongField(env, req, bufbaseSaveID, (jlong) bufbase) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                           count, mpi_type, mpi_comm, baseType) ;

    MPI_Ibsend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm,
               &request) ;

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetLongField(env, req, bufptrSaveID, (jlong) bufptr) ;

#endif  /* GC_DOES_PINNING */

    (*env)->SetLongField(env,req,reqhandleID,(jlong)request);
    return req;
}

/*
 * Class:     mpi_Comm
 * Method:    Issend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;IILmpi/Request;)Lmpi/Request;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Issend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag, jobject req) {

    MPI_Request request;

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

        void *bufbase ;

  clearFreeList(env) ;

        bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;

        MPI_Issend(bufptr, count, mpi_type, dest, tag, mpi_comm, &request);

        /* Cache information needed to release the buffer */

        (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

        (*env)->SetObjectField(env, req, bufSaveID, buf) ;
        (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;
        (*env)->SetLongField(env, req, bufbaseSaveID, (jlong) bufbase) ;

#else

        int size ;

  clearFreeList(env) ;

        bufptr = getMPIBuf(&size, env, buf, offset,
                           count, mpi_type, mpi_comm, baseType) ;

        MPI_Issend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm,
                   &request) ;

        /* Cache information needed to release the buffer */

        (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

        (*env)->SetLongField(env, req, bufptrSaveID, (jlong) bufptr) ;

#endif  /* GC_DOES_PINNING */

    (*env)->SetLongField(env,req,reqhandleID,(jlong)request);
    return req;
}

/*
 * Class:     mpi_Comm
 * Method:    Irsend
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;IILmpi/Request;)Lmpi/Request;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Irsend(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint dest, jint tag, jobject req) {

    MPI_Request request;

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;

    MPI_Irsend(bufptr, count, mpi_type, dest, tag, mpi_comm, &request);

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetObjectField(env, req, bufSaveID, buf) ;
    (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;
    (*env)->SetLongField(env, req, bufbaseSaveID, (jlong) bufbase) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIBuf(&size, env, buf, offset,
                       count, mpi_type, mpi_comm, baseType) ;

    MPI_Irsend(bufptr, size, MPI_BYTE, dest, tag, mpi_comm,
               &request) ;

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 0) ;  /* Request.OP_SEND */

    (*env)->SetLongField(env, req, bufptrSaveID, (jlong) bufptr) ;

#endif  /* GC_DOES_PINNING */

    (*env)->SetLongField(env,req,reqhandleID,(jlong)request);
    return req;
}

/*
 * Class:     mpi_Comm
 * Method:    Irecv
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;IILmpi/Status;)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Irecv(JNIEnv *env, jobject jthis,
        jobject buf, jint offset, jint count, jobject type,
        jint source, jint tag, jobject req) {

    MPI_Request request;

    MPI_Comm mpi_comm =
            (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)) ;
    MPI_Datatype mpi_type =
            (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)) ;

    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void *bufptr ;

#ifdef GC_DOES_PINNING 

    void *bufbase ;

  clearFreeList(env) ;

    bufptr = getBufPtr(&bufbase, env, buf, baseType, offset) ;

    MPI_Irecv(bufptr, count, mpi_type, source, tag, mpi_comm, &request);

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 1) ;  /* Request.OP_RECV ; */

    (*env)->SetObjectField(env, req, bufSaveID, buf) ;
    (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;

    (*env)->SetLongField(env, req, bufbaseSaveID, (jlong) bufbase) ;

#else

    int size ;

  clearFreeList(env) ;

    bufptr = getMPIWriteBuf(&size, count, mpi_type, mpi_comm) ;

    MPI_Irecv(bufptr, size, MPI_BYTE, source, tag, mpi_comm,
              &request) ;

    /* Cache information needed to release the buffer */

    (*env)->SetIntField(env, req, opTagID, 1) ;  /* Request.OP_RECV ; */

    (*env)->SetObjectField(env, req, bufSaveID, buf) ;
    (*env)->SetIntField(env, req, baseTypeSaveID, baseType) ;

    (*env)->SetIntField(env, req, offsetSaveID, offset) ;
    (*env)->SetIntField(env, req, countSaveID, count) ;
    (*env)->SetLongField(env, req, typeSaveID, (jlong) mpi_type) ;
    (*env)->SetLongField(env, req, commSaveID, (jlong) mpi_comm) ;
    (*env)->SetLongField(env, req, bufptrSaveID, (jlong) bufptr) ;

#endif  /* GC_DOES_PINNING */

    (*env)->SetLongField(env,req,reqhandleID,(jlong)request);
    return req;
}


/*
 * Class:     mpi_Comm
 * Method:    pack
 * Signature: (Ljava/lang/Object;IILmpi/Datatype;[BI)I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_pack(JNIEnv *env, jobject jthis,
        jobject inbuf, jint offset, jint incount, jobject type, 
        jbyteArray outbuf, jint position) {

    jboolean isCopy=JNI_TRUE;
    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    jbyte* obufptr=(*env)->GetByteArrayElements(env,outbuf,&isCopy);
    int outsize=(*env)->GetArrayLength(env,outbuf);

    void *ibufbase ;
    void *ibufptr = getBufPtr(&ibufbase, env, inbuf, baseType, offset);

  clearFreeList(env) ;

    if(incount != 0 && outsize != (int) position)
                                              /* LAM doesn't like count = 0 */
        MPI_Pack(ibufptr, incount,
             (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)),
             obufptr, outsize, (int*)&position,
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)));

    releaseBufPtr(env, inbuf, ibufbase, baseType);

    (*env)->ReleaseByteArrayElements(env,outbuf,obufptr,0);

    return position;
}

/*
 * Class:     mpi_Comm
 * Method:    unpack
 * Signature: ([BILjava/lang/Object;IILmpi/Datatype;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_unpack(JNIEnv *env, jobject jthis,
        jbyteArray inbuf, jint position, jobject outbuf, jint offset,
        jint outcount, jobject type) {

    jboolean isCopy=JNI_TRUE;
    int baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    void* ibufptr=(*env)->GetByteArrayElements(env,inbuf,&isCopy);
    int insize=(*env)->GetArrayLength(env,inbuf);

    void *obufbase ;
    void *obufptr = getBufPtr(&obufbase, env, outbuf, baseType, offset);

  clearFreeList(env) ;

    MPI_Unpack(ibufptr,
               insize, (int*)&position,
               obufptr, outcount,
               (MPI_Datatype)((*env)->GetLongField(env,type,DatatypehandleID)),
               (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)));

    (*env)->ReleaseByteArrayElements(env,inbuf,ibufptr,0);

    releaseBufPtr(env, outbuf, obufbase, baseType);

    return position;
}

/*
 * Class:     mpi_Comm
 * Method:    Pack_size
 * Signature: (ILmpi/Datatype;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Pack_1size(JNIEnv *env, jobject jthis,
        jint incount, jobject type) {

     int size;

  clearFreeList(env) ;

     MPI_Pack_size(incount, 
             (MPI_Datatype) ((*env)->GetLongField(env,type,DatatypehandleID)),
             (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
             &size);

     return size;
}

/*
 * Class:     mpi_Comm
 * Method:    Iprobe
 * Signature: (II)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Iprobe(JNIEnv *env, jobject jthis,
        jint source, jint tag, jobject stat) {

    int flag;
    MPI_Status *status =
        (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  clearFreeList(env) ;

    MPI_Iprobe(source, tag,
               (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
               &flag, status);

    if(flag != 0) {
      (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
      (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

      (*env)->SetIntField(env, stat, elementsID, -1);

      return stat;
    }
    else
      return NULL;
}

/*
 * Class:     mpi_Comm
 * Method:    Probe
 * Signature: (II)Lmpi/Status;
 */
JNIEXPORT jobject JNICALL Java_mpi_Comm_Probe(JNIEnv *env, jobject jthis,
        jint source, jint tag,jobject stat) {

    MPI_Status *status =
        (MPI_Status *)((*env)->GetLongField(env,stat,stathandleID));

  clearFreeList(env) ;

    MPI_Probe(source, tag,
              (MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
              status);

    (*env)->SetIntField(env,stat, sourceID,status->MPI_SOURCE);
    (*env)->SetIntField(env,stat, tagID,status->MPI_TAG);

    (*env)->SetIntField(env, stat, elementsID, -1);

    return stat;
}

/*
 * Class:     mpi_Comm
 * Method:    Errhandler_set
 * Signature: (Lmpi/Errhandler;)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_Errhandler_1set(JNIEnv *env,
        jobject jthis, jobject errhandler) {

  clearFreeList(env) ;

    MPI_Errhandler_set((MPI_Comm)
                           ((*env)->GetLongField(env,jthis,CommhandleID)),
                       (MPI_Errhandler)
                           ((*env)->GetLongField(env,errhandler,ErrhandleID)));
}


/*
 * Class:     mpi_Comm
 * Method:    errorhandler_get
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_mpi_Comm_errorhandler_1get(JNIEnv *env,
        jobject jthis) {

    MPI_Errhandler errhandler;

  clearFreeList(env) ;

    MPI_Errhandler_get((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                       &errhandler);
    return (jlong)errhandler;
}

/*
 * Class:     mpi_Comm
 * Method:    Abort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_Abort(JNIEnv *env, jobject jthis,
        jint errorcode) {

  clearFreeList(env) ;

    MPI_Abort((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
              errorcode);
}


/*
 * Class:     mpi_Comm
 * Method:    Topo_test
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Topo_1test(JNIEnv *env, jobject jthis) {

    int status;

  clearFreeList(env) ;

    MPI_Topo_test((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                  &status);
    return status;
}

/*
   !! Attr_put and Attr_get are dealing with int attribute_val only now.
   xli 3/26/98
*/


/*
 * Class:     mpi_Comm
 * Method:    Attr_put
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_Attr_1put(JNIEnv *env, jobject jthis,
        jint keyval, jint attribute_val) {

  clearFreeList(env) ;

    MPI_Attr_put((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                 keyval, &attribute_val);
}

/*
 * Class:     mpi_Comm
 * Method:    Attr_get
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_mpi_Comm_Attr_1get(JNIEnv *env, jobject jthis,
        jint keyval) {

    int *attribute_val; 
    int flag;

  clearFreeList(env) ;

    MPI_Attr_get((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                 keyval, &attribute_val, &flag);

    if(flag != 0)
        return *attribute_val;
    else
        return (int) NULL;
}

/*
 * Class:     mpi_Comm
 * Method:    Attr_delete
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Comm_Attr_1delete(JNIEnv *env, jobject jthis,
        jint keyval) {

  clearFreeList(env) ;

    MPI_Attr_delete((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
                    keyval);
}

/*
 * Things to do:
 *
 *     Handle exceptions!!
 */

