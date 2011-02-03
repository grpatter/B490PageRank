/*
 * File         : mpjdev_Request.c
 * Author       : Sang Lim
 * Created      : Sun Jan 20 16:51:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:20 $
 */

#include "mpjdev_Request.h"
#include "buff_typeDef.h"

#include <mpi.h>
#include <stdlib.h>
#include <inttypes.h>

extern jfieldID mpjdev_jBufferId;
extern jfieldID mpjdev_handleID;
/*
extern const int MPJDEV_HEADER_SIZE;
*/

extern jfieldID mpjdev_fid;
/* Field and Method IDs in `Status' */
extern jfieldID mpjdev_sourceID, mpjdev_tagID, mpjdev_indexID;
extern jmethodID mpjdev_status_constructorID;
extern jobject mpjdev_staticStatusObject;

/* Fields and Constructor ID in 'Request'*/
extern jfieldID mpjdev_reqhandleID, mpjdev_typeTagID, mpjdev_opTagID, mpjdev_hdrReqID; 
extern jfieldID mpjdev_bufferID, mpjdev_commID, mpjdev_r_tagID;
extern jmethodID mpjdev_iwaitID;

void mpjdev_complete(JNIEnv * env, jobject jthis, int source, MPI_Status status)
{
  jint typeTag = (*env)-> GetIntField(env, jthis, mpjdev_typeTagID);
  switch(typeTag) {
    case 0 : break;  /* TYPE_NORMAL(primitive type case)*/
    case 1 : {
      jint opTag = (*env)-> GetIntField(env, jthis, mpjdev_opTagID);
      switch(opTag) {
        case 0 : { /* OP_SEND*/

          /*Data has already gone, but must still do `wait' on header send.*/
          jobject hdrReq = (*env)-> GetObjectField(env, jthis, mpjdev_hdrReqID);
          (*env)-> CallObjectMethod(env, hdrReq, mpjdev_iwaitID);

          break;
        }
        case 1 : { /*OP_RECV*/

          jobject buffer = (*env)-> GetObjectField(env, jthis, mpjdev_bufferID);
          Buffer *pBuffer = 
            (Buffer *)(intptr_t)(*env)->GetLongField(env, buffer, mpjdev_jBufferId);

          char* pmessage = pBuffer-> message;
          char* pObjHeader;
          jint objMsgSize, primarySize;

          if (pmessage[0] == (char)mpjdev_encoding) {

            primarySize = ((jint*)pmessage)[1];
            pObjHeader  = pmessage + ((jint*)pmessage)[1] + MPJDEV_HEADER_SIZE;
            objMsgSize  = ((jint*)pObjHeader)[1];

          } else {

            ((char*)primarySize)[0] = pmessage[7];
            ((char*)primarySize)[1] = pmessage[6];
            ((char*)primarySize)[2] = pmessage[5];
            ((char*)primarySize)[3] = pmessage[4];
            
            pObjHeader = pmessage + primarySize + MPJDEV_HEADER_SIZE;
            
            ((char*)objMsgSize)[0] = pObjHeader[7];
            ((char*)objMsgSize)[1] = pObjHeader[6];
            ((char*)objMsgSize)[2] = pObjHeader[5];
            ((char*)objMsgSize)[3] = pObjHeader[4];
          }

          if (objMsgSize != 0) {

            jbyteArray byte_array = (*env)-> NewByteArray(env, objMsgSize);
            jbyte* pbyte_array = (*env)-> GetByteArrayElements(env, byte_array,
                                                               NULL);
            int recv_size;
            MPI_Get_count(&status, MPI_BYTE, &recv_size);
            
            if ((primarySize + 16) < recv_size) {
              
              int i;
              for (i = 0; i < objMsgSize; i++)
                pbyte_array[i] = pObjHeader[MPJDEV_HEADER_SIZE + i];
              
            } else {
              MPI_Status  status;
              jint tag = (*env)-> GetIntField(env, jthis, mpjdev_r_tagID);
              
              MPI_Comm mpi_comm = 
                (MPI_Comm)(intptr_t)((*env)->GetLongField(env, jthis, mpjdev_commID));
              
              MPI_Recv(pbyte_array, objMsgSize, MPI_BYTE, source, tag, 
                       mpi_comm,  &status);
            }      
            
            (*env)-> SetObjectField(env, buffer, mpjdev_fid, byte_array);
            (*env)-> ReleaseByteArrayElements(env, byte_array, pbyte_array, 0);
          }
          
          break;
        }

        break;
      }
    }
  }
}

/*
 * Class:     mpjdev_Request
 * Method:    iwait
 * Signature: ()Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Request_iwait (JNIEnv * env, jobject jthis)
{

  MPI_Request req;
  MPI_Status  status;
  jclass      statusClass;
  jobject     statusObject;

  req = (MPI_Request)(intptr_t)((*env)->GetLongField(env, jthis, mpjdev_reqhandleID));

  MPI_Wait(&req, &status);

  (*env)-> SetLongField(env, jthis, mpjdev_reqhandleID, (long)req);

  statusClass  = (*env)-> FindClass(env, "mpjdev/Status");  
  statusObject = (*env)-> NewObject(env, statusClass, mpjdev_status_constructorID);

  (*env)-> SetIntField(env, statusObject, mpjdev_sourceID, status.MPI_SOURCE);
  (*env)-> SetIntField(env, statusObject, mpjdev_tagID,    status.MPI_TAG);

  mpjdev_complete(env, jthis, status.MPI_SOURCE, status);
  
  return statusObject;
}

/*
 * Class:     mpjdev_Request
 * Method:    iwaitany
 * Signature: ([Lmpjdev/Request;)Lmpjdev/Status;
 */
JNIEXPORT jobject JNICALL 
Java_mpjdev_Request_iwaitany (JNIEnv * env, jobject jthis, 
                                   jobjectArray array_of_request)
{
  int i, index, count=(*env)->GetArrayLength(env,array_of_request);

  MPI_Status status;
  jclass     reqNullClass;
  jobject    statusObject, reqNullObject, request;
  jfieldID   reqNullId;

  MPI_Request *reqs = (MPI_Request*)calloc(count, sizeof(MPI_Request));

  reqNullClass = (*env)-> FindClass(env, "mpjdev/Request");
  reqNullId    = (*env)-> GetStaticFieldID(env, reqNullClass, "MPI_REQUEST_NULL", 
                                           "Lmpjdev/Request;");

  reqNullObject = (*env)->GetStaticObjectField(env, reqNullClass, reqNullId);

  for(i=0; i<count; i++) {

    jobject rq = (*env)->GetObjectArrayElement(env, array_of_request, i);
    
    if ((*env)->IsSameObject(env, rq, reqNullObject) == JNI_TRUE) {
        reqs[i] = MPI_REQUEST_NULL;
    } else {
        reqs[i] = (MPI_Request)(intptr_t)((*env)->GetLongField(env, rq,
                                                               mpjdev_reqhandleID));
    }
  }

  MPI_Waitany(count, reqs, &index, &status);
/*
  statusClass  = (*env)-> FindClass(env, "mpjdev/Status");  
  statusObject = (*env)-> NewObject(env, statusClass, mpjdev_status_constructorID);
*/
  statusObject = (*env)-> NewLocalRef(env, mpjdev_staticStatusObject);

  (*env)-> SetIntField(env, statusObject, mpjdev_sourceID, status.MPI_SOURCE);
  (*env)-> SetIntField(env, statusObject, mpjdev_tagID,    status.MPI_TAG);
  (*env)-> SetIntField(env, statusObject, mpjdev_indexID,  index);

  for(i=0; i<count; i++) {
    jobject reqi = (*env)->GetObjectArrayElement(env,array_of_request,i) ;
    (*env)->SetLongField(env, reqi, mpjdev_reqhandleID, (jlong) reqs[i]) ;
  }

  request = (*env)->GetObjectArrayElement(env, array_of_request, index);
  mpjdev_complete(env, request, status.MPI_SOURCE, status);

  free(reqs);
  
  return statusObject;
}



