/*
 * File         : mpi_Graphcomm.c
 * Headerfile   : mpi_Graphcomm.h 
 * Author       : Xinying Li
 * Created      : Thu Apr  9 12:22:15 1998
 * Revision     : $Revision: 1.2 $
 * Updated      : $Date: 2003/01/16 16:39:34 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University 1998
 */

#include <mpi.h>
#include "mpi_Graphcomm.h"

extern jfieldID CommhandleID;

extern void clearFreeList(JNIEnv*) ;

/*
 * Class:     mpi_Graphcomm
 * Method:    Get
 * Signature: ()Lmpi/GraphParms;
 */
JNIEXPORT jobject JNICALL Java_mpi_Graphcomm_Get
  (JNIEnv *env, jobject jthis)
{
  jintArray index, edges;
  jint *ind, *edg;
  jboolean isCopy=JNI_TRUE;
  int maxind, maxedg;

  jclass graphparms_class=(*env)->FindClass(env,"mpi/GraphParms");
  jfieldID indexID,edgesID;
  jmethodID handleConstructorID = (*env)->GetMethodID(env,
     graphparms_class, "<init>", "()V");
  jobject graphparms=(*env)->NewObject(env,graphparms_class, handleConstructorID);

  clearFreeList(env) ;

  MPI_Graphdims_get((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),&maxind,&maxedg);
  index=(*env)->NewIntArray(env,maxind);
  edges=(*env)->NewIntArray(env,maxedg);
  ind=(*env)->GetIntArrayElements(env,index,&isCopy);
  edg=(*env)->GetIntArrayElements(env,edges,&isCopy);

  MPI_Graph_get((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
    maxind,maxedg, (int*)ind, (int*)edg);

  (*env)->ReleaseIntArrayElements(env,index,ind,0);
  (*env)->ReleaseIntArrayElements(env,edges,edg,0);

  indexID=(*env)->GetFieldID(env,graphparms_class,"index","[I");
  edgesID=(*env)->GetFieldID(env,graphparms_class , "edges", "[I");

  (*env)->SetObjectField(env, graphparms, indexID, index);
  (*env)->SetObjectField(env, graphparms, edgesID, edges);

  /* printf("Graphcomm Get finished.\n"); */
  return graphparms;

}

/*
 * Class:     mpi_Graphcomm
 * Method:    Neighbours
 * Signature: (I)[I
 */
JNIEXPORT jintArray JNICALL Java_mpi_Graphcomm_Neighbours
  (JNIEnv *env, jobject jthis, jint rank)
{
    jint *neighbors;
    jboolean isCopy=JNI_TRUE;
    jintArray jneighbors;
    int maxns;

  clearFreeList(env) ;

	MPI_Graph_neighbors_count((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),rank,&maxns);
    jneighbors=(*env)->NewIntArray(env,maxns);
    neighbors=(*env)->GetIntArrayElements(env,jneighbors,&isCopy);
    MPI_Graph_neighbors((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
        rank,maxns,(int*)neighbors);
    (*env)->ReleaseIntArrayElements(env,jneighbors,neighbors,0);
    return jneighbors;
}

/*
 * Class:     mpi_Graphcomm
 * Method:    Map
 * Signature: ([I[I)I
 */
JNIEXPORT jint JNICALL Java_mpi_Graphcomm_Map
  (JNIEnv *env, jobject jthis, jintArray index, jintArray edges)
{
    int newrank;
    jint *ind, *edg;
    jboolean isCopy=JNI_TRUE;
    int nnodes;

  clearFreeList(env) ;

    nnodes=(*env)->GetArrayLength(env,index);
    ind=(*env)->GetIntArrayElements(env,index,&isCopy);
	edg=(*env)->GetIntArrayElements(env,edges,&isCopy);

    MPI_Graph_map((MPI_Comm)((*env)->GetLongField(env,jthis,CommhandleID)),
        nnodes,(int*)index,(int*)edges, &newrank);
	(*env)->ReleaseIntArrayElements(env,index,ind,0);
	(*env)->ReleaseIntArrayElements(env,edges,edg,0);
	return newrank;
}


