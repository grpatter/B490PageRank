#!/bin/sh
# This is a comment!
echo "Beginning SSH Ops"
	shopt -s extglob
	rm !(pr.zip)
	unzip -u pr.zip
	/opt/mpj-v0_38/bin/mpjdaemon_linux_x86_64 start
	logout