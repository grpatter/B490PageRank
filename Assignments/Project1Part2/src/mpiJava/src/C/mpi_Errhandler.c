
/*
 * File         : mpi_Errhandler.c
 * Headerfile   : mpi_Errhandler.h 
 * Author       : Bryan Carpenter
 * Created      : 1999
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2001/08/07 16:36:15 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include  <mpi.h>
#include "mpi_Errhandler.h"

jfieldID ErrhandleID;

/*
 * Class:     mpi_Errhandler
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Errhandler_init
  (JNIEnv *env, jclass thisClass)
{
  ErrhandleID = (*env)->GetFieldID(env,thisClass,"handle","J");                      
}

/*
 * Class:     mpi_Errhandler
 * Method:    GetErrhandler
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Errhandler_GetErrhandler
  (JNIEnv *env, jobject jthis, jint type)
{
  switch (type) {
  case 0:
      (*env)->SetLongField(env,jthis, ErrhandleID, (jlong)MPI_ERRORS_RETURN);
  case 1:
      (*env)->SetLongField(env,jthis, ErrhandleID, (jlong)MPI_ERRORS_ARE_FATAL);
  }
}


