
/*
 * This is specifically to support the IBM JVM, which uses the
 * SIGTRAP signal handler (and where we don't yet have a standard
 * mechanism for signal chaining).
 */
 
#include <stdio.h>
#include <signal.h>
#include "mpi_MPI.h"

/*
#define JVM_SIGNAL2 SIGBUS
*/

static void (*handlers [32])() = {0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0} ;

/*
 * Class:     mpi_MPI
 * Method:    saveSignalHandlers
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_saveSignalHandlers
  (JNIEnv * env, jclass obj)
{

#ifdef JVM_SIGNAL1
  handlers [JVM_SIGNAL1] = signal(JVM_SIGNAL1, SIG_DFL);
  signal(JVM_SIGNAL1, handlers [JVM_SIGNAL1]);
#endif

#ifdef JVM_SIGNAL2
  handlers [JVM_SIGNAL2] = signal(JVM_SIGNAL2, SIG_DFL);
  signal(JVM_SIGNAL2, handlers [JVM_SIGNAL2]);
#endif

}

/*
 * Class:     mpi_MPI
 * Method:    restoreSignalHandlers
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_mpi_MPI_restoreSignalHandlers
  (JNIEnv *env, jclass obj)
{

#ifdef JVM_SIGNAL1
  signal(JVM_SIGNAL1, handlers [JVM_SIGNAL1]);
#endif

#ifdef JVM_SIGNAL2
  signal(JVM_SIGNAL2, handlers [JVM_SIGNAL2]);
#endif

}

