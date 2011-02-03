/*
 * File         : mpi_Datatype.c
 * Headerfile   : mpi_Datatype.h 
 * Author       : Sung-Hoon Ko, Xinying Li, Sang Lim, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.10 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Datatype.h"

jfieldID DatatypehandleID;
jfieldID DatatypebaseTypeID;
jfieldID DatatypebaseSizeID;

extern void clearFreeList(JNIEnv*) ;


/*
 * public class Datatype {
 *   private final static int UNDEFINED = -1;
 *   public final static int NULL    = 0;
 *   public final static int BYTE    = 1;
 *   public final static int CHAR    = 2;
 *
 *   public final static int SHORT   = 3;
 *   public final static int BOOLEAN = 4;
 *   public final static int INT     = 5;
 *
 *   public final static int LONG    = 6;
 *   public final static int FLOAT   = 7;
 *   public final static int DOUBLE  = 8;  
 *
 *   public final static int PACKED  = 9;
 *   public final static int LB      =10;
 *   public final static int UB      =11;
 *
 *   public final static int OBJECT  =12;
 *
 *   ...
 * }
 */


MPI_Datatype Dts[] = { MPI_DATATYPE_NULL, MPI_BYTE,  MPI_SHORT, 
                       MPI_SHORT,         MPI_BYTE,  MPI_INT, 
                       MPI_LONG_INT,      MPI_FLOAT, MPI_DOUBLE,
                       MPI_PACKED,        MPI_LB,    MPI_UB, 
                       MPI_BYTE };
int* dt_sizes ;

void init_native_Datatype() {

    /* Initialization that can only be done after MPI_Init() has
     * been called.  Called from `mpi_MPI.c'.
     */

  int i ;

  dt_sizes = (int*) malloc(13 * sizeof(int)) ;
  for(i = 1 ; i < 13 ; i++) {
      MPI_Type_size(Dts [i], &(dt_sizes [i])) ;
  }
}

/*
 * Class:     mpi_Datatype
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_init
  (JNIEnv *env, jclass thisClass)
{
  DatatypehandleID   = (*env)->GetFieldID(env,thisClass,"handle","J");
  DatatypebaseTypeID = (*env)->GetFieldID(env,thisClass,"baseType","I");
  DatatypebaseSizeID = (*env)->GetFieldID(env,thisClass,"baseSize","I");
}

/*
 * Class:     mpi_Datatype
 * Method:    GetDatatype
 * Signature: (I)J
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetDatatype
  (JNIEnv *env, jobject jthis, jint type) 
{

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)Dts[type]);
}

/*
 * Class:     mpi_Datatype
 * Method:    size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Datatype_size
  (JNIEnv *env, jobject jthis)
{
  int result;

  clearFreeList(env) ;

  MPI_Type_size(
         (MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID)), 
         &result );

  return result;
}

/*
 * Class:     mpi_Datatype
 * Method:    extent
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Datatype_extent
  (JNIEnv *env, jobject jthis)
{
  MPI_Aint  result;

  clearFreeList(env) ;

  MPI_Type_extent(
      (MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID)),
      &result);

  return result;
}

/*
 * Class:     mpi_Datatype
 * Method:    lB
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Datatype_lB
  (JNIEnv *env, jobject jthis)
{
  MPI_Aint  result;

  clearFreeList(env) ;

  MPI_Type_lb(
      (MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID)),
      &result);

  return result;
}

/*
 * Class:     mpi_Datatype
 * Method:    uB
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Datatype_uB
  (JNIEnv *env, jobject jthis)
{
  MPI_Aint  result;

  clearFreeList(env) ;

  MPI_Type_ub(
              (MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID)),
              &result);

  return result;
}

/*
 * Class:     mpi_Datatype
 * Method:    commit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_commit
  (JNIEnv *env, jobject jthis)
{
  MPI_Datatype type;

  clearFreeList(env) ;

  type=(MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID));
  MPI_Type_commit(&type);
}

/*
 * Class:     mpi_Datatype
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_free
  (JNIEnv *env, jobject jthis)
{
  MPI_Datatype type;
  type=(MPI_Datatype)((*env)->GetLongField(env,jthis,DatatypehandleID));
  if(type != MPI_DATATYPE_NULL)
    MPI_Type_free(&type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetContiguous
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetContiguous
  (JNIEnv *env, jobject jthis, jint count,jobject oldtype)
{
  MPI_Datatype type;

  clearFreeList(env) ;

  MPI_Type_contiguous(count, 
           (MPI_Datatype)((*env)->GetLongField(env,oldtype,DatatypehandleID)),
           &type);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetVector
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetVector
                             (JNIEnv *env, jobject jthis,
                             jint count, jint blocklength, jint stride,
                             jobject oldtype) {
  MPI_Datatype type;

  clearFreeList(env) ;

  MPI_Type_vector(count, blocklength, stride, 
          (MPI_Datatype)((*env)->GetLongField(env,oldtype,DatatypehandleID)),
          &type);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetHvector
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetHvector
                             (JNIEnv *env, jobject jthis,
                              jint count, jint blocklength, jint stride,
                              jobject oldtype) {
  MPI_Datatype type;
  jint baseSize = (*env)->GetIntField(env, jthis, DatatypebaseSizeID) ;

  clearFreeList(env) ;

  MPI_Type_hvector(count, blocklength, baseSize * stride, 
            (MPI_Datatype)((*env)->GetLongField(env,oldtype,DatatypehandleID)),
            &type);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetIndexed
 * Signature: (I[I[I)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetIndexed
  (JNIEnv *env, jobject jthis, jintArray blocklengths, jintArray
displacements, jobject oldtype)
{
  MPI_Datatype type;
  int count=(*env)->GetArrayLength(env,blocklengths);
  jboolean isCopy=JNI_TRUE;
  jint *lengths; jint *disps;

  clearFreeList(env) ;

  lengths=(*env)->GetIntArrayElements(env,blocklengths,&isCopy);
  disps = (*env)->GetIntArrayElements(env,displacements,&isCopy);
  MPI_Type_indexed(count, (int*)lengths, (int*)disps, 
    (MPI_Datatype)((*env)->GetLongField(env,oldtype,DatatypehandleID)), &type);
  (*env)->ReleaseIntArrayElements(env,blocklengths,lengths,0);
  (*env)->ReleaseIntArrayElements(env,displacements,disps,0);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetHindexed
 * Signature: (I[I[I)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetHindexed
      (JNIEnv *env, jobject jthis,
       jintArray blocklengths, jintArray displacements,jobject oldtype)
{
  MPI_Datatype type ;
  int count = (*env)->GetArrayLength(env,blocklengths);
  jboolean isCopy ;
  jint *lengths; jint *disps;
  jint baseSize = (*env)->GetIntField(env, jthis, DatatypebaseSizeID) ;
  MPI_Aint* cdisps ;
  int i ;

  clearFreeList(env) ;

  lengths=(*env)->GetIntArrayElements(env,blocklengths,&isCopy);
  disps = (*env)->GetIntArrayElements(env,displacements,&isCopy);

  cdisps = (MPI_Aint*) calloc(count, sizeof(MPI_Aint)) ;
  for(i = 0 ; i < count ; i++)
    cdisps [i] = baseSize * disps [i] ;

  MPI_Type_hindexed(count, (int*)lengths, cdisps,
          (MPI_Datatype)((*env)->GetLongField(env,oldtype,DatatypehandleID)),
          &type);

  free(cdisps) ;

  (*env)->ReleaseIntArrayElements(env,blocklengths,lengths,0);
  (*env)->ReleaseIntArrayElements(env,displacements,disps,0);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}

/*
 * Class:     mpi_Datatype
 * Method:    GetStruct
 * Signature: ([I[I[Lmpi/Datatype;ZIZI)V
 */
JNIEXPORT void JNICALL Java_mpi_Datatype_GetStruct(JNIEnv *env, jobject jthis,
      jintArray blocklengths, jintArray displacements, jobjectArray datatypes,
      jboolean lbSet, jint lb, jboolean ubSet, jint ub) {

  MPI_Datatype type;
  int count, ptr, i ;
  jboolean isCopy ;
  jint *lengths, *disps ;
  MPI_Datatype *ctypes ;
  int *clengths ;
  MPI_Aint *cdisps ;
  jint baseSize = (*env)->GetIntField(env, jthis, DatatypebaseSizeID) ;

  clearFreeList(env) ;

  count = (*env)->GetArrayLength(env,blocklengths);

  lengths = (*env)->GetIntArrayElements(env,blocklengths,&isCopy);
  disps   = (*env)->GetIntArrayElements(env,displacements,&isCopy);

  /* Remove components with UNDEFINED base type, but add upper bound
     and lower bound markers if required. */

  ctypes   = (MPI_Datatype*) calloc(count + 2, sizeof(MPI_Datatype)) ;
  clengths = (int*) calloc(count + 2, sizeof(int)) ;
  cdisps   = (MPI_Aint*) calloc(count + 2, sizeof(MPI_Aint)) ;

  ptr = 0 ;
  for(i = 0 ; i < count ; i++) {
    jobject type  = (*env)->GetObjectArrayElement(env, datatypes, i) ;
    jint baseType = (*env)->GetIntField(env, type, DatatypebaseTypeID) ;

    if(baseType != -1) {
      jlong handle = (*env)->GetLongField(env, type, DatatypehandleID) ;
      ctypes   [ptr] = (MPI_Datatype) handle ;
      clengths [ptr] = lengths [i] ;
      cdisps   [ptr] = baseSize * disps [i] ;
      ptr++ ;
    }
  }
  if(lbSet == JNI_TRUE) {
    ctypes   [ptr] = MPI_LB ;
    clengths [ptr] = 1 ;
    cdisps   [ptr] = baseSize * lb ;
    ptr++ ;
  }
  if(ubSet == JNI_TRUE) {
    ctypes   [ptr] = MPI_UB ;
    clengths [ptr] = 1 ;
    cdisps   [ptr] = baseSize * ub ;
    ptr++ ;
  }

  MPI_Type_struct(ptr, clengths, cdisps, ctypes, &type);

  free(cdisps);
  free(clengths);
  free(ctypes);

  (*env)->ReleaseIntArrayElements(env,blocklengths,lengths,0);
  (*env)->ReleaseIntArrayElements(env,displacements,disps,0);

  (*env)->SetLongField(env,jthis, DatatypehandleID, (jlong)type);
}
