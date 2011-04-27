#!/bin/sh

#module load java

# Extract files
echo "unzipping archive"
unzip -o source34.zip

echo "running java"
/N/soft/jdk1.6.0_20-x86-64/jre/bin/java -verbose -Djava.library.path=~/Project4/Daemon/Daemon_linux_lib -jar ~/Project4/Daemon/Daemon.jar

#java -Xms64m -Xmx512m -jar Daemon.jar
