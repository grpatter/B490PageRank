# Generated automatically from Makefile.in by configure.
# File         : Makefile
# Author       : Sung-Hoon Ko
# Created      : Thu Aug 1998
# Revision     : ver 1.1
# Updated      : $Date: 2003/03/25 18:41:00 $
# Copyright: Northeast Parallel Architectures Center at Syracuse University 1998

SHELL       = /bin/sh


srcdir = .
top_srcdir = ..

SUBDIR  = Java mpjdev native C

VPATH = .:${srcdir}


all: clean Makefile
	@echo "Recursively making subdir:" ${SUBDIR}
	@for i in ${SUBDIR}; \
	do (cd $$i; \
	echo "--- Making" $$i;\
	$(MAKE) "MAKE=$(MAKE)"); done


Makefile: ${srcdir}/Makefile.in
	@echo
	@echo Please configure the package to generate new makefile ...
	@exit

clean:
	@for i in ${SUBDIR}; \
	do (cd $$i; $(MAKE) "MAKE=$(MAKE)" clean); done
