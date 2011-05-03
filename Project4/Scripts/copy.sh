#!/bin/sh

# Extract files
echo "unzipping archive"
unzip -o Source.zip
tar xvfz mpi_pagerank.tar.gz

echo "Starting monitor Daemons"
/N/soft/jdk1.6.0_20-x86_64/jre/bin/java -verbose -Djava.library.path=~/Project4/Daemon/Daemon_linux_lib -jar ~/Project4/Daemon/Daemon.jar &

#############################
# FIXME
#############################
echo "Compiling mpi-pagerank"
#javac -cp .:$MPJ_HOME/lib/mpj.jar Pagerank/java/PagerankMpi_updated.java Pagerank/java/PerformanceLogger.java -Xlint:unchecked

#/N/soft/jdk1.6.0_20-x86_64/bin/javac -cp .:$MPJ_HOME/lib/mpj.jar Pagerank/java/PagerankMpi_updated.java Pagerank/java/PerformanceLogger.java -Xlint:unchecked

#############################
# FIXME
#############################
echo "Running mpi-pagerank"
#mpjrun.sh -Xms512M -Xmx2048m --mca btl_tcp_if_exclude lo,eth1 -np 2 --hostfile machines.txt Pagerank/java/PagerankMpi_updated Pagerank/resources/urls100k0 testout 10 .85

mpirun --mca btl_tcp_if_exclude lo,eth1 -np 2 --hostfile machines.txt B534Project2/mpi_main -i B534Project2/pagerank.input0 -n 10 -t 0.000001

# I don't think this is the correct version.
#mpjrun.sh -Xms512M -Xmx2048m --mca btl_tcp_if_exclude lo,eth1 -np 2 Pagerank/java/PagerankMpi_updated Pagerank/resources/urls100k0 testout 10 .85
