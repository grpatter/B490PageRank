/*
 * File         : mpi_Group.c
 * Headerfile   : mpi_Group.h 
 * Author       : Xinying Li
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Group.h"

jfieldID GrouphandleID;

extern void clearFreeList(JNIEnv*) ;

/* 
 * Class:     mpi_Group
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Group_init
  (JNIEnv *env, jclass thisClass)
{
  GrouphandleID = (*env)->GetFieldID(env,thisClass,"handle","J");
}

/*
 * Class:     mpi_Group
 * Method:    GetGroup
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_mpi_Group_GetGroup
  (JNIEnv *env, jobject jthis, jint type)
{
  switch (type) {
    case 0:
      (*env)->SetLongField(env,jthis, GrouphandleID, (jlong)MPI_GROUP_EMPTY);
      break;
    default:
      break;
  }
}

/*
 * Class:     mpi_Group
 * Method:    Size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Group_Size
  (JNIEnv *env, jobject jthis)
{
  int size;

  clearFreeList(env) ;

  MPI_Group_size((MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
&size);
  return size;
}

/* * Class:     mpi_Group
 * Method:    Rank
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_mpi_Group_Rank
  (JNIEnv *env, jobject jthis) {
  int rank;

  clearFreeList(env) ;

  MPI_Group_rank((MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
&rank);
  return rank;
}

/*
 * Class:     mpi_Group
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_Group_free
  (JNIEnv *env, jobject jthis)
{
  MPI_Group group=(MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID));

  MPI_Group_free(&group);
  (*env)->SetLongField(env,jthis, GrouphandleID,(jlong)MPI_GROUP_NULL);
}

/*
 * Class:     mpi_Group
 * Method:    Translate_ranks
 * Signature: (Lmpi/Group;[ILmpi/Group;)[I
 */
JNIEXPORT jintArray JNICALL Java_mpi_Group_Translate_1ranks
  (JNIEnv *env, jclass jthis, jobject group1, jintArray ranks1, jobject
group2)
{
    jboolean isCopy=JNI_TRUE;
    int n=(*env)->GetArrayLength(env,ranks1);
    jint *rks1,*rks2;
    jintArray jranks2;

  clearFreeList(env) ;

    rks1=(*env)->GetIntArrayElements(env,ranks1,&isCopy);
    jranks2=(*env)->NewIntArray(env,n);
    rks2=(*env)->GetIntArrayElements(env,jranks2,&isCopy);
	MPI_Group_translate_ranks(
		(MPI_Group)((*env)->GetLongField(env,group1,GrouphandleID)),
		n, (int*)rks1,
 		(MPI_Group)((*env)->GetLongField(env,group2,GrouphandleID)),
		(int*)rks2);
	(*env)->ReleaseIntArrayElements(env,ranks1,rks1,0);
	(*env)->ReleaseIntArrayElements(env,jranks2,rks2,0);
	return jranks2;
}

/*
 * Class:     mpi_Group
 * Method:    Compare
 * Signature: (Lmpi/Group;Lmpi/Group;)I
 */
JNIEXPORT jint JNICALL Java_mpi_Group_Compare
  (JNIEnv *env, jclass jthis, jobject group1, jobject group2)
{
	int result;

  clearFreeList(env) ;

	MPI_Group_compare(
		(MPI_Group)((*env)->GetLongField(env,group1,GrouphandleID)),
		(MPI_Group)((*env)->GetLongField(env,group2,GrouphandleID)),
		&result);
	return result;
}

/*
 * Class:     mpi_Group
 * Method:    union
 * Signature: (Lmpi/Group;Lmpi/Group;)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_union
  (JNIEnv *env, jclass jthis, jobject group1, jobject group2)
{
	MPI_Group newgroup;

  clearFreeList(env) ;

	MPI_Group_union(
        (MPI_Group)((*env)->GetLongField(env,group1,GrouphandleID)),
        (MPI_Group)((*env)->GetLongField(env,group2,GrouphandleID)),
		&newgroup);
	return (jlong)newgroup;
}
/*
 * Class:     mpi_Group
 * Method:    intersection
 * Signature: (Lmpi/Group;Lmpi/Group;)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_intersection
  (JNIEnv *env, jclass jthis, jobject group1, jobject group2)
{
    MPI_Group newgroup;

  clearFreeList(env) ;

    MPI_Group_intersection(
        (MPI_Group)((*env)->GetLongField(env,group1,GrouphandleID)),
        (MPI_Group)((*env)->GetLongField(env,group2,GrouphandleID)),
        &newgroup);
    return (jlong)newgroup;
}

/*
 * Class:     mpi_Group
 * Method:    difference
 * Signature: (Lmpi/Group;Lmpi/Group;)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_difference
  (JNIEnv *env, jclass jthis, jobject group1, jobject group2)
{
    MPI_Group newgroup;

  clearFreeList(env) ;

    MPI_Group_difference(
        (MPI_Group)((*env)->GetLongField(env,group1,GrouphandleID)),
        (MPI_Group)((*env)->GetLongField(env,group2,GrouphandleID)),
        &newgroup);
    return (jlong)newgroup;
}

/*
 * Class:     mpi_Group
 * Method:    incl
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_incl
  (JNIEnv *env, jobject jthis, jintArray ranks)
{
    int n;
    jint *rks;
    jboolean isCopy=JNI_TRUE;
    MPI_Group newgroup;

  clearFreeList(env) ;

    n=(*env)->GetArrayLength(env,ranks);
    rks=(*env)->GetIntArrayElements(env,ranks,&isCopy);
    MPI_Group_incl(
        (MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
        n, (int*)rks,
        &newgroup);
    (*env)->ReleaseIntArrayElements(env,ranks,rks,0);
    return (jlong)newgroup;
}

/*
 * Class:     mpi_Group
 * Method:    excl
 * Signature: ([I)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_excl
  (JNIEnv *env, jobject jthis, jintArray ranks)
{
	int n;
	jint *rks;
	jboolean isCopy=JNI_TRUE;
	MPI_Group newgroup;

  clearFreeList(env) ;

	n=(*env)->GetArrayLength(env,ranks);
	rks=(*env)->GetIntArrayElements(env,ranks,&isCopy);
	MPI_Group_excl(
		(MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
		n, (int*)rks,
		&newgroup);
	(*env)->ReleaseIntArrayElements(env,ranks,rks,0);
	return (jlong)newgroup;
}

/*
 * Class:     mpi_Group
 * Method:    range_incl
 * Signature: ([[I)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_range_1incl
  (JNIEnv *env, jobject jthis, jobjectArray ranges)
{
    int i;
    int n=(*env)->GetArrayLength(env,ranges);
    jboolean isCopy=JNI_TRUE;
    MPI_Group newgroup;
    /*    jint **rngs=(jint**)calloc(n,sizeof(jint[3])); */
    int (*rngs) [3] =(int (*) [3])calloc(n,sizeof(int[3]));
    jintArray *jrngs=(jobject*)calloc(n,sizeof(jintArray));

  clearFreeList(env) ;

	for(i=0;i<n;i++) {
	        jint *vec ;
		    jrngs[i]=(*env)->GetObjectArrayElement(env,ranges,i);
    	    vec=(*env)->GetIntArrayElements(env, jrngs[i],&isCopy);
                rngs [i] [0] = vec [0] ;
                rngs [i] [1] = vec [1] ;
                rngs [i] [2] = vec [2] ;
			(*env)->ReleaseIntArrayElements(env,jrngs[i],vec,0);
		}

	MPI_Group_range_incl(
		(MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
        n,rngs,&newgroup);

	free(rngs);
	free(jrngs);
	return (jlong)newgroup;
}

/*
 * Class:     mpi_Group
 * Method:    range_excl
 * Signature: ([[I)J
 */
JNIEXPORT jlong JNICALL Java_mpi_Group_range_1excl
  (JNIEnv *env, jobject jthis, jobjectArray ranges)
{
    int i;
    int n=(*env)->GetArrayLength(env,ranges);
    jboolean isCopy=JNI_TRUE;
    MPI_Group newgroup;
    /*    jint **rngs=(jint**)calloc(n,sizeof(jint*)); */
    int (*rngs) [3] =(int (*) [3])calloc(n,sizeof(int[3]));
    jintArray *jrngs=(jobject*)calloc(n,sizeof(jintArray));

  clearFreeList(env) ;

    for(i=0;i<n;i++) {
        jint* vec;
        jrngs[i]=(*env)->GetObjectArrayElement(env,ranges,i);
	vec=(*env)->GetIntArrayElements(env,
			jrngs[i],&isCopy);
        rngs [i] [0] = vec [0] ;
        rngs [i] [1] = vec [1] ;
        rngs [i] [2] = vec [2] ;
    	        
	(*env)->ReleaseIntArrayElements(env,jrngs[i],vec,0);
        }
    MPI_Group_range_excl(
        (MPI_Group)((*env)->GetLongField(env,jthis,GrouphandleID)),
        n, rngs,&newgroup);

    free(rngs);
    free(jrngs);
    return (jlong)newgroup;
}

