/*
 * File         : main.c
 * Headerfile   : 
 * Author       : Mark Baker
 * Created      : Wednesday July  15th 1615 1998
 * Revision     : $Revision: 1.3 $
 * Updated      : $Date: 1999/09/16 19:38:25 $
 * Copyright: Northeast Parallel Architectures Center
 *            at Syracuse University and University of Portsmouth 
 *            1998 and 1999
 */

#include <jni.h>
#include <stdlib.h>
#include <string.h>

/* Prototype Decclarations */

void getClassName(char *, char *, char []);	    /* strip path and ext of the exe */
int readClassPath(char *, char *, char *);		/* open mpiJava.ini and read the CLASSPATH variable */
void stripCR(char *);													/* Strip of trailing carriage-returns */

/* JNI Main */

void main(int argc, char **argv) {

/* JNI Variable Declaratiuon */

  jclass javaClass;
  jint ret;
  jmethodID javaMethod;
  jstring jstr;
  jobjectArray;
  JavaVM *jvm;
  JNIEnv *env;
  JavaVMInitArgs jvmArgs;
  JavaVMOption options[4];
  jobjectArray str_array;
  
/* Stand C Decalation */
	
  int rtn, i, noBytes;
  char *ClassName, *CLASSPATH, *PathName;
  char *FileName = "mpiJava.ini";
  char optionClasspath[256];

/* Allocated some memory of the ClassName and the PathName */

  noBytes = sizeof(argv[0]) * sizeof(char);
  ClassName  = (char *) malloc(noBytes);
  PathName  = (char *) malloc(128);

  getClassName(ClassName, PathName, argv[0]);

/* get CLASSPATH out of mpiJava.ini file */

  CLASSPATH  = (char *) malloc(256);

  rtn = readClassPath(CLASSPATH, PathName, FileName);
  if (rtn == 1)
	printf("Can't find a mpiJava.ini file \n");
  else if (rtn == 2)
    printf("Can't read contents of mpiJava.ini file \n");

  strcpy(optionClasspath, "-Djava.class.path=");
  strcat(optionClasspath, CLASSPATH);

  options[0].optionString = "-Djava.compiler=NONE";  // disable JIT
  options[1].optionString = optionClasspath;         // my Class path
  options[2].optionString = "-Djava.library,path=.";
  options[3].optionString = "-verbose:";

/* IMPORTANT: specify vm_args version # if you use JDK1.1.2 and beyond */
  
  jvmArgs.version  = JNI_VERSION_1_2;
  jvmArgs.options  = options;
  jvmArgs.nOptions = 4;
  jvmArgs.ignoreUnrecognized = JNI_FALSE;

/* Create Java VM */
 
  /* Set the CLASSPATH environmental variable */
  
  if (options[1].optionString == '\0') {
    printf("ERROR: The CLASSPATH has not been set \n");
  }

 /* Invoke the Java Virtual Machine */

  ret = JNI_CreateJavaVM(&jvm, (void ** )&env, &jvmArgs);
  
  if (ret < 0) {
     fprintf(stderr, "Can't create Java VM.  Error: %ld\n", ret);
  }

 /* Find the JavaWindow class */
 
  javaClass = (*env)->FindClass(env, ClassName);

  if (javaClass == 0) {
    printf("\nCan't find Class -- %s ", ClassName); 
    printf(" on the CLASSPATH -- %s \n", jvmArgs.options[1].optionString);
	exit(1);
  }


/* Find the main method */
 
  javaMethod  = (*env)->GetStaticMethodID(env, javaClass, "main", "([Ljava/lang/String;)V");
  if (javaMethod == 0) {
    fprintf(stderr, "Can't find %s.main\n", javaClass);
    exit(1);
  }

/* Process command-line arguments */

  jstr = (*env)->NewStringUTF (env, "");

  str_array = (*env)->NewObjectArray (env, argc, (*env)->FindClass(env, "java/lang/String"), jstr);
     
/* The rest SetObjectArrayElements are the command-line args */

  for (i = 0; i < argc; i++) {
    if ((jstr = (*env)->NewStringUTF (env, argv [i])) == 0) {
     fprintf (stderr, "Out of memory\n");
     exit(1);
	}
    (*env)->SetObjectArrayElement (env, str_array, i, jstr);
  }
    
/* call the main method */

  (*env)->CallStaticVoidMethod(env, javaClass, javaMethod, str_array);
  
  /* Destroy the VM */

  ret = (*jvm)->DestroyJavaVM(jvm);
  if (ret < 0)
		printf(" DestroyJavaVM failed - call return %d \n", ret); 

/* free() malloc'ed arrays */

	free(ClassName);
	free(PathName);
	free(CLASSPATH);
  
} /* End of Main */

int readClassPath(char * CLASSPATH, char * pathname, char * filename)
{
  
  FILE *stream;
  char line [128];
  char *pdest = 0;
	int  ch = '=';

  memset(CLASSPATH, 0, 256);
  memset(line, 0, 128);

	/* cat pathname and filename to give full path to file */

  strcat(pathname, filename);

  /* get current shell's CLASSPATH */

  //strcat(CLASSPATH, getenv("CLASSPATH"));

  /* This adds ";" so that we can append mpiJava.ini CLASSPATH on correctly */ 
	
  //strcat(CLASSPATH, ";");
  
  if( (stream = fopen(pathname, "r" )) != NULL ) {
	if( fgets(line, 128, stream ) == NULL) {
      printf("fgets error\n" );
	  return 2;
	}
		
/*  Return a pointer to the the occurance of '=' */

	pdest = strchr(line, ch);

    pdest++;	/* increment the pointer by 1 as we don't want '=' */

 /* strcat the contents of mpiJava.ini CLASSPATH to the system one */

	stripCR(pdest);

	strncat(CLASSPATH, pdest, strlen(pdest) - 1);
	
    //strcat(CLASSPATH, "c:\\Langs\\Java\\HPJava\\mpiJava\\tests\\PingPong");


	  fclose(stream);
	} else {
      printf("NOT FOUND mpiJava.ini \n");
	  return 1 ;
	}
  
  return 0;

}

void getClassName(char * ClassName, char * PathName, char * argv)
{

  int Length, len;
  char tmp[256];
  int ch = '\\';
  char *pdest;

  memset(ClassName, 0, 256);
  memset(tmp, 0, 256);
  memset(PathName, 0, 256);

  Length = strlen(argv);

  /* Copy all argv[0] apart from the last 4 char (.exe) to a tmp string */

  strncpy(tmp, argv, Length - 4);
  
  /* Reverse search the string for '\' */ 

  pdest = strrchr(tmp, ch);

  if (pdest != NULL) {
	pdest++;            /* increment the pointer */
    strcat(ClassName, pdest);
  } else
	strcat(ClassName, tmp);

	len = strlen(ClassName);

  strncpy(PathName, argv, Length - (4 + len));

}

/* Function to strip of the CR at the end of a line */ 

void stripCR(char * string) {

  int i, len;
  len = strlen(string);
  i = 0;
	while  (i < len) {
    if (string[i] == '\n') {
      strncpy(string, string, i - 1);
	  i = len;
	}
    i++;
	}

}


