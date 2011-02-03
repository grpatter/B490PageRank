/*
 * File         : mpi_Intercomm.c
 * Headerfile   : mpi_Intercomm.h 
 * Author       : Xinying Li
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Intercomm.h"

extern jfieldID CommhandleID;

extern void clearFreeList(JNIEnv*) ;

/*
 * Class:     mpi_Intercomm
 * Method:    Remote_size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Intercomm_Remote_1size
  (JNIEnv *env, jobject jthis)
{
    int size;

  clearFreeList(env) ;

    MPI_Comm_remote_size((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
		&size);
	return size;
}
/*
 * Class:     mpi_Intercomm
 * Method:    remote_group
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_mpi_Intercomm_remote_1group
  (JNIEnv *env, jobject jthis)
{
  MPI_Group group;

  clearFreeList(env) ;

  MPI_Comm_remote_group((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
		&group);
  return (jlong)group;
}

/*
 * Class:     mpi_Intercomm
 * Method:    merge
 * Signature: (Z)Lmpi/Intracomm;
 */
JNIEXPORT jlong JNICALL Java_mpi_Intercomm_merge
  (JNIEnv *env, jobject jthis, jboolean high)
{
  MPI_Comm newintracomm;

  clearFreeList(env) ;

  MPI_Intercomm_merge((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)), high,
	&newintracomm);
  return (jlong)newintracomm;
}
