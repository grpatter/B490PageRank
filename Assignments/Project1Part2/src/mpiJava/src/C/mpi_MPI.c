/*
 * File         : mpi_MPI.c
 * Headerfile   : mpi_MPI.h 
 * Author       : SungHoon Ko, Xinying Li (contributions from MAEDA Atusi)
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.17 $
 * Updated      : $Date: 2003/01/17 01:50:37 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <stdio.h>
#include <string.h>
#include <mpi.h>
#include "mpiJava.h"
#include "mpi_MPI.h"

int len = 0;
char** sargs = 0;

void init_native_Datatype() ;  /* defined "in mpi_Datatype.c" */

void clearFreeList(JNIEnv*) ;  /* defined below */

/*
 * Defining following symbols here stops the AIX linker complaining
 * (but doesn't do anything useful/harmful---symbols probably used
 * by Fortran run-time?).
 */

int p_xargc ;
char **p_xargv ;


/*
 * Class:     mpi_MPI
 * Method:    InitNative
 * Signature: ([Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_mpi_MPI_InitNative
    (JNIEnv *env, jclass obj, jobjectArray argv)
{
  jsize i;
  jstring jc;
  jclass string;
  jobject value;

  len = (*env)->GetArrayLength(env,argv);
  sargs = (char**)calloc(len+1, sizeof(char*));
  for (i=0; i<len; i++) {
    jc=(jstring)(*env)->GetObjectArrayElement(env,argv,i);
    sargs[i] = (char*)calloc(strlen((*env)->GetStringUTFChars(env,jc,0)) + 1,
                             sizeof(char));
    strcpy(sargs[i],(*env)->GetStringUTFChars(env,jc,0));
  }

  MPI_Init(&len, &sargs);

  string = (*env)->FindClass(env, "java/lang/String");
  value = (*env)->NewObjectArray(env, len, string, NULL);
  for (i = 0; i < len; i++) {
    jc = (*env)->NewStringUTF(env, sargs[i]);
    (*env)->SetObjectArrayElement(env, value, i, jc);
  }

  init_native_Datatype() ;

  return value;
}                                                   

/*
 * Class:     mpi_MPI
 * Method:    Finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_Finalize
  (JNIEnv *env, jclass obj) {

  clearFreeList(env) ;

  MPI_Finalize();
}
                                             
/*
 * Class:     mpi_MPI
 * Method:    Get_processor_name
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_mpi_MPI_Get_1processor_1name
  (JNIEnv *env, jclass obj, jbyteArray buf)
{
  int len;
  jboolean isCopy; 
  char* bufc = (char*)((*env)->GetByteArrayElements(env,buf,&isCopy)) ;

  clearFreeList(env) ;

  MPI_Get_processor_name(bufc, &len); 
  (*env)->ReleaseByteArrayElements(env,buf,bufc,0) ;
  return len;
}

/*
 * Class:     mpi_MPI
 * Method:    Wtime
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_mpi_MPI_Wtime
  (JNIEnv *env, jclass jthis)
{
  clearFreeList(env) ;

  return MPI_Wtime();
}

/*
 * Class:     mpi_MPI
 * Method:    Wtick
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_mpi_MPI_Wtick
  (JNIEnv *env, jclass jthis)
{
  clearFreeList(env) ;

	return MPI_Wtick();
}

/*
 * Class:     mpi_MPI
 * Method:    Initialized
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_mpi_MPI_Initialized
  (JNIEnv *env, jclass jthis)
{
	int flag;

  clearFreeList(env) ;

	MPI_Initialized(&flag);
	if(flag==0)
		return JNI_FALSE;
	else
		return JNI_TRUE;
}

/*
 * Class:     mpi_MPI
 * Method:    Buffer_attach_native
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_Buffer_1attach_1native
  (JNIEnv *env, jclass jthis, jbyteArray buf)
{
  jboolean isCopy;

  int size=(*env)->GetArrayLength(env,buf);
  jbyte* bufptr = (*env)->GetByteArrayElements(env,buf,&isCopy) ;

  clearFreeList(env) ;

  MPI_Buffer_attach(bufptr,size); 
}

/*
 * Class:     mpi_MPI
 * Method:    Buffer_detach_native
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_Buffer_1detach_1native
  (JNIEnv *env, jclass jthis, jbyteArray buf)
{
  /*jboolean isCopy;*/

  int size;
  /*char* bufptr ;*/
  jbyte* bufptr ;

  clearFreeList(env) ;

  MPI_Buffer_detach(&bufptr, &size);

  if(buf != NULL)
    (*env)->ReleaseByteArrayElements(env,buf,bufptr,0);
}

/*
 * Class:     mpi_MPI
 * Method:    SetConstant
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_SetConstant
  (JNIEnv *env, jclass jthis)
{
   jfieldID anysourceID=(*env)->GetStaticFieldID(env,jthis,"ANY_SOURCE","I");
   jfieldID anytagID=(*env)->GetStaticFieldID(env,jthis,"ANY_TAG","I");
   jfieldID procnullID=(*env)->GetStaticFieldID(env,jthis,"PROC_NULL","I");  
   jfieldID graphID=(*env)->GetStaticFieldID(env,jthis,"GRAPH","I");
   jfieldID cartID=(*env)->GetStaticFieldID(env,jthis,"CART","I");
   jfieldID bsendoverID=(*env)->GetStaticFieldID(env,jthis,"BSEND_OVERHEAD","I");
   jfieldID undefinedID=(*env)->GetStaticFieldID(env,jthis,"UNDEFINED","I");
   
   jfieldID identID=(*env)->GetStaticFieldID(env,jthis,"IDENT","I");
   jfieldID congruentID=(*env)->GetStaticFieldID(env,jthis,"CONGRUENT","I");
   jfieldID similarID=(*env)->GetStaticFieldID(env,jthis,"SIMILAR","I");
   jfieldID unequalID=(*env)->GetStaticFieldID(env,jthis,"UNEQUAL","I");
   jfieldID tagubID=(*env)->GetStaticFieldID(env,jthis,"TAG_UB","I");
   jfieldID hostID=(*env)->GetStaticFieldID(env,jthis,"HOST","I");
   jfieldID ioID=(*env)->GetStaticFieldID(env,jthis,"IO","I");

   (*env)->SetStaticIntField(env,jthis,anysourceID,MPI_ANY_SOURCE);
   (*env)->SetStaticIntField(env,jthis,anytagID,MPI_ANY_TAG);
   (*env)->SetStaticIntField(env,jthis,procnullID,MPI_PROC_NULL);
   (*env)->SetStaticIntField(env,jthis,graphID,MPI_GRAPH);
   (*env)->SetStaticIntField(env,jthis,cartID,MPI_CART);
#ifdef GC_DOES_PINNING
   (*env)->SetStaticIntField(env,jthis,bsendoverID,MPI_BSEND_OVERHEAD);
#else
   (*env)->SetStaticIntField(env,jthis,bsendoverID,
                             MPI_BSEND_OVERHEAD + sizeof(int));
#endif  /* GC_DOES_PINNING */

   (*env)->SetStaticIntField(env,jthis,undefinedID,MPI_UNDEFINED);
    
   (*env)->SetStaticIntField(env,jthis,identID,MPI_IDENT);
   (*env)->SetStaticIntField(env,jthis,congruentID,MPI_CONGRUENT);
   (*env)->SetStaticIntField(env,jthis,similarID,MPI_SIMILAR);
   (*env)->SetStaticIntField(env,jthis,unequalID,MPI_UNEQUAL);
   (*env)->SetStaticIntField(env,jthis,tagubID,MPI_TAG_UB);
   (*env)->SetStaticIntField(env,jthis,hostID,MPI_HOST);
   (*env)->SetStaticIntField(env,jthis,ioID,MPI_IO);
}

void clearFreeList(JNIEnv *env) {
    jclass mpi ;
    jmethodID clearID ;

    mpi = (*env)->FindClass(env, "mpi/MPI");
    clearID = (*env)->GetStaticMethodID(env, mpi, "clearFreeList", "()V");
    (*env)->CallStaticVoidMethod(env, mpi, clearID) ;
}

