
/*
 * This is a simplified version of the standard JVM launcher command, `java'.
 *
 * For certain MPI platforms (SUNHPC version 4) it is necessary to recompile
 * the launcher command to link it with the standard MPI library (*not*
 * the the mpiJava wrapper libraries, note).
 *
 * There is no technical reason not to use the original `java' program.
 * However recompilation requires access to the source code, so it may
 * require mpiJava users to agree to Sun's Community Source Licence,
 * and possibly to download the JDK source code independently (depending on
 * how that licence is interpretted).  Hence the creation of this
 * unrestricted clone.
 *
 * The `jvmlauncher' command recognizes most of the options of the JDK 1.4
 * `java' command.  Exceptions are the -d32 and -d64 options on Solaris.
 * Presumably one can achieve the effect of -d64 by recompilation of
 * this program.  This `jvmlauncher' also does not internally set the
 * `LD_LIBRARY_PATH' variable.  On some OS's (Linux) this seems to require
 * that you explicitly put the native parts of the Java standard libraries
 * on the `LD_LIBRARY_PATH', eg
 * 
 *   LD_LIBRARY_PATH = $JAVA_HOME/jre/lib/$ARCH:\
 *                     $JAVA_HOME/jre/lib/$ARCH/client:\
 *                     $JAVA_HOME/jre/lib/$ARCH/server:...
 *
 * prior to invoking this command.
 */

/*
 * Macro `JRE_PATH' should be defined at compile-time.  Typically:
 *
 *         -DJRE_PATH="$JAVA_HOME/{jre/}lib/$ARCH/"
 *
 * where `ARCH' is "i386", "sparc", etc.
 */

#define PROG_NAME   "jvmlauncher"

#define JVM_LIB     "libjvm.so"
#define XUSAGE_FILE "Xusage.txt"

#include <stdlib.h>
#include <dlfcn.h>

#include "jni.h"


/* ******** Prototypes of utility functions defined in this file. ******** */

int parseArgs(int argc, char** argv,
              int *numOptions, JavaVMOption **options,
              char** jvmType, char** className, char** jarFileName,
              int* jArgc, char*** jArgv) ;

static void printUsage() ;
static void printXUsage(char* jvmdir) ;

static void addOption(int *numOptions, JavaVMOption **options,
                      char* optionString, char* extraInfo) ;
static void setClassPath(int *numOptions, JavaVMOption **options, char* path) ;

static void makeJVM(JavaVM** jvm, JNIEnv** env,
                    int numOptions, JavaVMOption *options, char* jvmDir) ;
static void abortJVM(JavaVM *jvm) ;

static void checkException(JNIEnv* env, JavaVM *jvm) ;

static void printVersion(JavaVM* jvm, JNIEnv* env) ;

static char* getMainClassNameFromJar(JavaVM* jvm, JNIEnv* env,
                                     char* jarFileName) ;

static jstring makeJavaString(JavaVM* jvm, JNIEnv* env, char* str) ;
static jarray makeJavaStringArray(JavaVM* jvm, JNIEnv* env,
                                  int nstrs, char** strs) ;

static int isPublicMethod(JavaVM* jvm, JNIEnv* env,
                          jclass class, jmethodID method) ;

static char* getDescriptorFromClassName(char* className) ;

/* ******** End of prototypes. ******** */


int main(int argc, char* argv []) {
   
    int numOptions = 0 ;
    JavaVMOption *options = 0 ;

    char *jvmType, *className, *jarFileName ;
    int jArgc ;
    char** jArgv ;

    char* jvmDir ;

    JavaVM* jvm ;
    JNIEnv* env ;
  
    char* defaultCP ;

    int action ;

    defaultCP = getenv("CLASSPATH") ;
    if (!defaultCP) defaultCP = ".";

    setClassPath(&numOptions, &options, defaultCP) ;

    action = parseArgs(argc, argv, &numOptions, &options,
                       &jvmType, &className, &jarFileName,
                       &jArgc, &jArgv) ;

    /* jvmDir = "<JRE_PATH>/<jvmType>" */

    jvmDir = malloc(strlen(JRE_PATH) + 1 + strlen(jvmType) + 1) ;
    strcpy(jvmDir, JRE_PATH "/") ;
    strcat(jvmDir, jvmType) ;

    /* Cases that exit immediately (other cases require creation of JVM) */

    switch(action) {
        case 3 :
            printUsage() ;
            exit(0) ;
        case 4 :
            printXUsage(jvmDir) ;
            exit(0) ;
        default :
            break ;
    }

    if(jarFileName)
       setClassPath(&numOptions, &options, jarFileName) ;

    makeJVM(&jvm, &env, numOptions, options, jvmDir) ;

/* debug
{
    jclass system ;
    jmethodID getProperty ;
    jstring name, value ;
    char* str ;

    system = (*env)->FindClass(env, "java/lang/System");
    getProperty =
            (*env)->GetStaticMethodID(env, system, "getProperty",
                                     "(Ljava/lang/String;)Ljava/lang/String;");
    name = (*env)->NewStringUTF(env, "java.class.path");
    value =
       (*env)->CallStaticObjectMethod(env, system, getProperty, name);
    if (value != NULL) {
        str = (*env)->GetStringUTFChars(env, value, NULL);
        printf("property length = %d value = %s\n", strlen(str), str) ;
    }
    else
        printf("could not find property\n", str) ;
}
debug */

    if(action == 1 || action == 2)
        printVersion(jvm, env) ;

    if(action == 0 || action == 1) {

        /* Find main class and invoke main method */

        jclass mainClass ;
        jmethodID mainID ;

        if(jarFileName)
            className = getMainClassNameFromJar(jvm, env, jarFileName) ;

        mainClass = (*env)->FindClass(env,
                                      getDescriptorFromClassName(className)) ;
        checkException(env, jvm) ;

        mainID = (*env)->GetStaticMethodID(env, mainClass, "main",
                                           "([Ljava/lang/String;)V");
        checkException(env, jvm) ;
      
        if(!isPublicMethod(jvm, env, mainClass, mainID)) {
            fprintf(stderr, "%s: main method of class %s must be public.\n",
                    PROG_NAME, className) ;

            abortJVM(jvm) ;
        }

        (*env)->CallStaticVoidMethod(env, mainClass, mainID,
                                     makeJavaStringArray(jvm, env,
                                                         argc, argv)) ;
        checkException(env, jvm) ;
    }

/* find out what this actually does?? */
    if((*jvm)->DetachCurrentThread(jvm)) {
         fprintf(stderr, "%s: failed to detach main thread.\n", PROG_NAME) ;

         exit(1) ;
    }

    (*jvm)->DestroyJavaVM(jvm) ;
}


/****************************************************************************/
/*                                                                          */
/*                          Utility functions.                              */
/*                                                                          */
/****************************************************************************/
  
int parseArgs(int argc, char** argv,
              int *numOptions, JavaVMOption **options,
              char** jvmType, char** className, char** jarFileName,
              int* jArgc, char*** jArgv) {
   /*
    * Entry values:
    *   *argc, *argv: command line arguments.
    *
    * Return codes:
    *   0: OK.  Run Java main
    *   1: OK.  Print version and run Java main
    *   2: Print version and exit
    *   3: Print usage and exit
    *   4: Print extended usage and exit
    *
    * Exit values:
    *   *jvmType: JVM type: "client" or "server"
    *   *className: class name
    *   *jarFileName: jar file name
    *   *jArgc, *jArgv: arguments to Java main method.
    *
    * On exit with an OK status, exactly one of `*className', `*jarFileName'
    * is non-null.  On exit with any other status, only guarantee `*jvmType'
    * is initialized.
    */

    char* arg ;
    int result ;

    *jvmType = "client" ;
    *className = 0 ;
    *jarFileName = 0 ;

    argv++ ; argc-- ;
    arg = *argv ;

    result = 0 ;

    while(argc > 0) {
        if(!strcmp(arg, "-help") || !strcmp(arg, "-?")) {
            return 3 ;
        }
        else if(!strcmp(arg, "-X")) {
            return 4 ;
        }
        else if(!strcmp(arg, "-version")) {
            return 2 ;
        }
        else if(!strcmp(arg, "-showversion")) {
            result = 1 ;
        }
        else if(!strcmp(arg, "-classpath") || !strcmp(arg, "-cp")) {
            if(argc < 1) {
                fprintf(stderr, "%s: no class path after %s flag\n",
                        PROG_NAME, arg) ;
                printUsage() ;

                exit(1) ;
            }
            argv++ ; argc-- ;
            arg = *argv ;
            setClassPath(numOptions, options, arg) ;
        }
        else if(!strcmp(arg, "-jar")) {
            if(argc < 1) {
                fprintf(stderr, "%s: no jar file name after -jar flag\n",
                        PROG_NAME) ;
                printUsage() ;

                exit(1) ;
            }
            argv++ ; argc-- ;
            arg = *argv ;

            *jarFileName = arg ;
            break ;
        }
        else if(!strcmp(arg, "-Xfuture")) {
            addOption(numOptions, options, "-Xverify:all", 0) ;
        }
        else if(!strcmp(arg, "-client")) {
            *jvmType = "client" ;
        }
        else if(!strcmp(arg, "-server")) {
            *jvmType = "server" ;
        }
        else if(*arg == '-') {
            addOption(numOptions, options, arg, 0) ;
        }
        else {
            *className = arg ;
            break ;
        }
    
        argv++ ; argc-- ;
        arg = *argv ;
    }
  
    if(!*className && !*jarFileName) {
        fprintf(stderr, "%s: no class or jar file specified\n",
                PROG_NAME) ;
        printUsage() ;

        exit(1) ;
    }

    *jArgc = argc ;
    *jArgv = argv ;

    return result ;
}

static void addOption(int *numOptions, JavaVMOption **options,
                      char* optionString, char* extraInfo) {

    /* Rellocate `options' array in batches of 10, as needed */

    if(*numOptions % 10 == 0) {

        int i ;
        JavaVMOption *newOptions =
            (JavaVMOption*) malloc(sizeof(JavaVMOption) * (*numOptions + 10)) ;

        for(i = 0 ; i < *numOptions ; i++) {
            newOptions [i].optionString = (*options) [i].optionString ;
            newOptions [i].extraInfo    = (*options) [i].extraInfo    ;
        }

        if(*options) free(*options) ;

        (*options) = newOptions ;
    }

    (*options) [*numOptions].optionString = optionString ;
    (*options) [*numOptions].extraInfo    = extraInfo    ;

    (*numOptions)++ ;
}


#define CP_OPTION_PREFIX "-Djava.class.path="

static void setClassPath(int *numOptions, JavaVMOption **options, char* path) {

    /* Note if multiple VM options set same system property, all but last
       are ignored */

    char* option = malloc(strlen(CP_OPTION_PREFIX) + strlen(path) + 1) ;

    strcpy(option, CP_OPTION_PREFIX) ;
    strcat(option, path) ;
    addOption(numOptions, options, option, 0) ;
}


static void printUsage() {
    fprintf(stdout,
        "Usage: %s [ options ] class [ argument ... ]\n"
        "       %s [ options ] -jar file.jar [ argument ... ]\n\n"
        "where:\n"
        "    options:  Command-line options.\n"
        "    class:    Name of the class to be invoked.\n"
        "    file.jar: Name of the jar file to be invoked.\n"
        "    argument: Argument passed to the main function.\n\n"
        "Options include:\n"
        "    -client\n"
        "            Use \"client\" JVM (default).\n"
        "    -server\n"
        "            Use \"server\" JVM.\n"
        "    -classpath <classpath>\n"
        "    -cp <classpath>\n"
        "            Add directories, JAR/ZIP archives to class search path.\n"
        "    -D<property>=<value>\n"
        "            Set a system property value.\n"
        "    -enableassertions[:<package name>\"...\" | :<class name> ]\n"
        "    -ea[:<package name>\"...\" | :<class name> ]\n"
        "            Enable assertions.\n"
        "    -disableassertions[:<package name>\"...\" | :<class name> ]\n"
        "    -da[:<package name>\"...\" | :<class name> ]\n"
        "            Disable assertions.\n"
        "    -enablesystemassertions\n"
        "    -esa\n"
        "            Enable system assertions.\n"
        "    -disablesystemassertions\n"
        "    -dsa\n"
        "            Disable system assertions.\n"
        "    -verbose\n"
        "    -verbose:class\n"
        "           Display information about each class loaded.\n"
        "    -verbose:gc\n"
        "           Report on each garbage collection event.\n"
        "    -verbose:jni\n"
        "           Report information about use of native methods.\n"
        "    -version\n"
        "           Display version information and exit.\n"
        "    -showversion\n"
        "           Display version information and continue.\n"
        "    -?\n"
        "    -help\n"
        "           Display usage information and exit.\n"
        "    -X\n"
        "           Display information about non-standard options and exit.\n",
    PROG_NAME, PROG_NAME) ;
}

static void printXUsage(char* jvmDir) {

    char* xUsagePath ;
    char buf[128] ;
    int n ;
    FILE* fp ;

    /* xUsagePath = "<jvmDir>/<XUSAGE_FILE>" */

    xUsagePath = malloc(strlen(jvmDir) + 1 + strlen(XUSAGE_FILE) + 1) ;
    strcpy(xUsagePath, jvmDir) ;
    strcat(xUsagePath, "/" XUSAGE_FILE) ;

    fp = fopen(xUsagePath, "r") ;
    if(!fp) {
        fprintf(stderr, "%s: failed to open file %s\n",
                PROG_NAME, xUsagePath) ;

        exit(1) ;
    }

    while (n = fread(buf, 1, sizeof(buf), fp))
        fwrite(buf, 1, n, stdout) ;

    fclose(fp) ;
}

/*
 * Load dynamic library for chosen JVM, and create the JVM.
 */

typedef jint (JNICALL *CreateJVM_t)(JavaVM **jvm, void **env, void *args);

static void makeJVM(JavaVM** jvm, JNIEnv** env,
                    int numOptions, JavaVMOption *options, char* jvmDir) {

    char* jvmPath ;
    void* libjvm ;

    CreateJVM_t createJVM ;

    JavaVMInitArgs vm_args ;

    /* jvmPath = "<jvmDir>/<JVM_LIB>" */

    jvmPath = malloc(strlen(jvmDir) + 1 + strlen(JVM_LIB) + 1) ;

    strcpy(jvmPath, jvmDir) ;
    strcat(jvmPath, "/" JVM_LIB) ;

    /* Load the JVM library */

    fflush(stdout) ; /* Bug! */
                     /* No idea why this is needed, but unless there is
                        some such operation prior to `dlopen', the call
                        to `createJVM' fails on my Linux machine! */

    libjvm = dlopen(jvmPath, RTLD_NOW + RTLD_GLOBAL) ;
    if(!libjvm) {
        fprintf(stderr, "%s: failed to open library %s\n",
                PROG_NAME, jvmPath) ;
        exit(1) ;
    }

    createJVM = (CreateJVM_t) dlsym(libjvm, "JNI_CreateJavaVM") ;
    if(!createJVM) {
        fprintf(stderr, "%s: symbol JNI_CreateJavaVM not found\n",
                PROG_NAME) ;
        exit(1) ;
    }

    /* Create the JVM */

    vm_args.version = JNI_VERSION_1_2 ;
    vm_args.options = options ;
    vm_args.nOptions = numOptions ;
    vm_args.ignoreUnrecognized = JNI_FALSE ; 

    if((*createJVM)(jvm, (void**)env, &vm_args)) {
        fprintf(stderr, "%s: failed to launch virtual machine.\n",
                PROG_NAME) ;
        exit(1) ;
    }
}

static void abortJVM(JavaVM *jvm) {
    (*jvm)->DestroyJavaVM(jvm) ;

    exit(1) ;
}

static void checkException(JNIEnv* env, JavaVM *jvm) {

    if((*env)->ExceptionOccurred(env)) {
	(*env)->ExceptionDescribe(env) ;

        abortJVM(jvm) ;
    }
}

static void printVersion(JavaVM* jvm, JNIEnv* env) {

    jclass version ;
    jmethodID print ;

    version = (*env)->FindClass(env, "sun/misc/Version") ;
    checkException(env, jvm) ;

    print = (*env)->GetStaticMethodID(env, version, "print", "()V") ;
    checkException(env, jvm) ;
      
    (*env)->CallStaticVoidMethod(env, version, print) ;
    checkException(env, jvm) ;
}

static char* getMainClassNameFromJar(JavaVM* jvm, JNIEnv* env,
                                     char* jarFileName) {

    jclass jarFileClass, manifestClass, attributesClass ;
    jmethodID constructor, getManifest, getMainAttributes, getValue ;
    jobject jarFile, manifest, attributes ;
    jstring mainClassName ;

    char* result ;

    /* JarFile jarFile = new java.util.jar.JarFile("<jarFileName>") ; */

    jarFileClass = (*env)->FindClass(env, "java/util/jar/JarFile") ;
    checkException(env, jvm) ;

    constructor = (*env)->GetMethodID(env, jarFileClass, "<init>",
                                      "(Ljava/lang/String;)V") ;
    checkException(env, jvm) ;
    
    jarFile = (*env)->NewObject(env, jarFileClass, constructor,
                                makeJavaString(jvm, env, jarFileName)) ;
    checkException(env, jvm) ;

    
    /* Manifest manifest = jarFile.getManifest() */

    getManifest = (*env)->GetMethodID(env, jarFileClass, "getManifest",
                                      "()Ljava/util/jar/Manifest;") ;
    checkException(env, jvm) ;

    manifest = (*env)->CallObjectMethod(env, jarFile, getManifest) ;
    checkException(env, jvm) ;

    if(!manifest) {
        fprintf(stderr, "%s: Jar file %s has no manifest.\n",
                PROG_NAME, jarFileName) ;

	(*jvm)->DestroyJavaVM(jvm) ;
	exit(1) ;
    }


    /* Attributes attributes = manifest.getMainAttributes() ; */

    manifestClass = (*env)->GetObjectClass(env, manifest) ;
    checkException(env, jvm) ;

    getMainAttributes =
            (*env)->GetMethodID(env, manifestClass, "getMainAttributes",
                                "()Ljava/util/jar/Attributes;") ;
    checkException(env, jvm) ;

    attributes = (*env)->CallObjectMethod(env, manifest, getMainAttributes) ;
    checkException(env, jvm) ;


    /* mainClassName = attributes.getValue("Main_Class") ; */

    if(attributes) {
        attributesClass = (*env)->GetObjectClass(env, attributes) ;
        checkException(env, jvm) ;

        getValue =
            (*env)->GetMethodID(env, attributesClass, "getValue",
                                "(Ljava/lang/String;)Ljava/lang/String;") ;
        checkException(env, jvm) ;

        mainClassName = 
                (*env)->CallObjectMethod(env, attributes, getValue,
                                         makeJavaString(jvm, env,
                                                        "Main-Class")) ;
        checkException(env, jvm) ;
    }


    if(!attributes || !mainClassName) {
        fprintf(stderr,
                "%s: Manifest of jar file %s has no Main-Class attribute.\n",
                PROG_NAME, jarFileName) ;

        abortJVM(jvm) ;
    }


    result = (char*) (*env)->GetStringUTFChars(env, mainClassName, 0) ;
    checkException(env, jvm) ;

    return result ;
}

static jstring makeJavaString(JavaVM* jvm, JNIEnv* env, char* str) {

    int len = strlen(str) ;

    jclass stringClass ;
    jmethodID constructor ;
    jbyteArray byteArray ;
    jstring string ;

    /* String string = new String(new byte [] {...bytes from str...}") */

    stringClass = (*env)->FindClass(env, "java/lang/String") ;
    checkException(env, jvm) ;

    constructor = (*env)->GetMethodID(env, stringClass, "<init>", "([B)V") ;
    checkException(env, jvm) ;
    
    byteArray = (*env)->NewByteArray(env, len) ;
    checkException(env, jvm) ;

    (*env)->SetByteArrayRegion(env, byteArray, 0, len, (jbyte*) str) ;
    checkException(env, jvm) ;

    string = (*env)->NewObject(env, stringClass, constructor, byteArray) ;
    checkException(env, jvm) ;

    (*env)->DeleteLocalRef(env, byteArray) ;
    checkException(env, jvm) ;

    return string ;
}

static jarray makeJavaStringArray(JavaVM* jvm, JNIEnv* env,
                                  int nstrs, char** strs) {

    jclass stringClass ;
    jarray stringArray ;

    int i ;

    stringClass = (*env)->FindClass(env, "java/lang/String") ;
    checkException(env, jvm) ;

    stringArray = (*env)->NewObjectArray(env, nstrs, stringClass, 0) ;
    checkException(env, jvm) ;

    for(i = 0 ; i < nstrs ; i++) {
        jstring string = makeJavaString(jvm, env, strs [i]) ;

        (*env)->SetObjectArrayElement(env, stringArray, i, string) ;
        checkException(env, jvm) ;
    
        (*env)->DeleteLocalRef(env, string) ;
        checkException(env, jvm) ;
    }

    return stringArray ;
}

static int isPublicMethod(JavaVM* jvm, JNIEnv* env,
                          jclass class, jmethodID method) {

    jclass methodClass ;
    jobject methodObject ;
    jmethodID getModifiers ;
    jint modifiers ;

    methodObject = (*env)->ToReflectedMethod(env, class, method, JNI_TRUE) ;
    checkException(env, jvm) ;

    methodClass = (*env)->GetObjectClass(env, methodObject) ;
    checkException(env, jvm) ;

    getModifiers = (*env)->GetMethodID(env, methodClass,
                                       "getModifiers", "()I") ;
    checkException(env, jvm) ;

    modifiers = (*env)->CallIntMethod(env, methodObject, getModifiers) ;
    checkException(env, jvm) ;

    return (modifiers & 1) != 0 ;
}

static char* getDescriptorFromClassName(char* className) {

    /* Class descriptor has /'s in place of .'s */

    char* classDesc = malloc(strlen(className) + 1) ;
    char* ptr = classDesc ;
    char ch ;

    while(ch = *(className++))
        *(ptr++) = (ch == '.') ? '/' : ch ;
    *ptr = 0 ;
    
    return classDesc ;
}

