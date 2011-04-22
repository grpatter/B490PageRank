#!/bin/sh
# This is a comment!
echo "Beginning SSH Ops"
	unzip -o pr.zip
	mkdir main
	mkdir main/resources
	cp -rf url* main/resources/
	rm url*
	/opt/mpj-v0_38/bin/mpjdaemon_linux_x86_64 start
