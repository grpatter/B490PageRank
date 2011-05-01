#!/bin/bash                                                                                                                                                                                                        
#PBS -q b534                                                                                                                                                                                                       
#PBS -l nodes=1:ppn=8                                                                                                                                                                                              
#PBS -l walltime=00:00:30                                                                                                                                                                                          
#PBS -o run_pagerank_bm$PBS_JOBID.out                                                                                                                                                                              

echo "Bare Metal Job Reserved"

cat $PBS_NODEFILE

echo $PBS_NODEFILE > /N/u/rtlustyo/Project4/machines
# run the MPI pagerank and monitoring application in Bare Metal Cluster                                                                                                                                            
# =========                                                                                                                                                                                                        

cd Project4

module load java

/N/u/rtlustyo/mpj-v0_38/bin/./mpjdaemon_linux_x86_32 start

java -Xms64m -Xmx128m -jar /N/u/rtlustyo/Project4/Daemon.jar >> Daemon.out &

echo "*** Test for 1 Node ***"
for i in 1 2 3
do
  echo "Test $i"
   mpjrun.sh -np 2 -Xms512M -Xmx2048m PagerankMpi_updated urls100k0 testout 10 .85
done

/N/u/rtlustyo/mpj-v0_38/bin/./mpjdaemon_linux_x86_32 stop

# =========                                                                                                                                                                                                        
echo "Bare Metal job Finished"