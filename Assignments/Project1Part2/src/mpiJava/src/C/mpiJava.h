
/*
#define GC_DOES_PINNING 1
*/

/*
 * Define `GC_DOES_PINNING' if the JVM is known to support pinning
 * of arrays by native methods, i.e. if the value of `isCopy'
 * set by `Get<T>ArrayElements' is always false.
 *
 * In most cases setting this macro only affects efficiency,
 * not correctness, regardless of whether the GC actually supports pinning,
 * There is an exception in the case of non-blocking
 * receives.  If `GC_DOES_PINNING' is set, but the `Get<T>ArrayElements'
 * method returns a copy of the receive buffer, and the receive operation
 * is only supposed to write a subsection of the Java array originally passed
 * as a buffer, and some other user code updates a different part of the same
 * Java array before the receive completes, then the user's modification to
 * other parts of the array will be overwritten when the *whole* of the
 * Java array gets copied back by `Release<T>ArrayElements'.  This is
 * unlikely to be the expected or desired behavior.
 *
 * *Not* settting `GC_DOES_PINNING' is conservative, and should always
 * give the currect results, although it introduces unnecessary copying
 * if in fact the GC always pins.
 */

/*
 * Note added: this macro now set on the cc command line, in the
 * makefile, if the configure script thinks it is needed.
 */

