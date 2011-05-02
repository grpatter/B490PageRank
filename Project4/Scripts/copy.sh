#!/bin/sh

#module load java

# Extract files
echo "unzipping archive"
unzip -o source34.zip

echo "Starting monitor Daemons"
/N/soft/jdk1.6.0_20-x86_64/jre/bin/java -verbose -Djava.library.path=~/Project4/Daemon/Daemon_linux_lib -jar ~/Project4/Daemon/Daemon.jar

echo "Starting mpi-pagerank"
