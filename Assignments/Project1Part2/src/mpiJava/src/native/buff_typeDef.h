/*
 * File         : buff_typeDef.h
 * Author       : Sang Lim
 * Created      : Tue Jan 29 12:24:40 2002
 * Revision     : $Revision: 1.1.2.1 $
 * Updated      : $Date: 2003/03/24 20:25:38 $
 */
#include<string.h>

typedef struct {
  int originalCapacity;
  int capacity;
  int size;
  int readPtr;
  char *message;
} Buffer;

enum encode {MPJDEV_BIG_ENDIAN, MPJDEV_LITTLE_ENDIAN};
extern enum encode mpjdev_encoding;

enum types {MPJDEV_BYTE, MPJDEV_CHAR, MPJDEV_SHORT, MPJDEV_BOOLEAN, MPJDEV_INT,
            MPJDEV_LONG, MPJDEV_FLOAT, MPJDEV_DOUBLE, MPJDEV_OBJECT};


#define MPJDEV_HEADER_SIZE 8

