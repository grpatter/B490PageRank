
/*
 * File         : mpi_Op.c
 * Headerfile   : mpi_Op.h 
 * Author       : Xinying Li, Bryan Carpenter
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.7 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Op.h"

jfieldID OphandleID;

/*
 * Class:     mpi_Op
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Op_init
  (JNIEnv *env, jclass thisClass) 
{
  OphandleID=(*env)->GetFieldID(env,thisClass,"handle","J");
}

/*
 * Class:     mpi_Op
 * Method:    GetOp
 * Signature: (I)J
 */
JNIEXPORT void JNICALL Java_mpi_Op_GetOp
  (JNIEnv *env, jobject jthis, jint type) 
{
  static MPI_Op Ops[] = { MPI_OP_NULL, MPI_MAX, MPI_MIN, MPI_SUM,
    MPI_PROD, MPI_LAND, MPI_BAND, MPI_LOR, MPI_BOR, MPI_LXOR,
    MPI_BXOR, MPI_MINLOC, MPI_MAXLOC};
  (*env)->SetLongField(env,jthis, OphandleID, (jlong)Ops[type]);
}

/*
 * Class:     mpi_Op
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Op_free
  (JNIEnv *env, jobject jthis)
{
  MPI_Op op;
  op=(MPI_Op)((*env)->GetLongField(env,jthis,OphandleID));
  if(op != MPI_OP_NULL)
    MPI_Op_free(&op);
}

