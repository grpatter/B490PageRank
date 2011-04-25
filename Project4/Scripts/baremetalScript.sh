#!/bin/bash
#PBS -q b534
#PBS -l nodes=1:ppn=8
#PBS -l walltime=00:00:30
#PBS -o run_pagerank_bm$PBS_JOBID.out

echo "Bare Metal Job Reserved"

# run the MPI pagerank and monitoring application in Bare Metal Cluster
# =========
module load java

/N/u/rtlustyo/mpj-v0_38/bin/./mpjdaemon_linux_x86_32 start

java -Xms64m -Xmx512m -jar Daemon.jar

mpjrun.sh -np 2 -dev niodev PagerankMpi_updated urls100k0 testout 10 .85

/N/u/rtlustyo/mpj-v0_38/bin/./mpjdaemon_linux_x86_32 stop
# =========
echo "Bare Metal job Finished"
