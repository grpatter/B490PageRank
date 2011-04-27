#!/bin/sh
#
# p4-test.sh
#
# Set up grid environment
#PBS -q b534
#PBS -l nodes=2:ppn=8,walltime=00:13:00
#PBS -o run_pagerank_bm$PBS_JOBID.out

#print the time and date
date

echo "Starting VM's"
. /usr/local/bin/start_vms

echo "Waiting for VM's"
/usr/local/bin/wait_for_vms $VM_NODEFILE

echo "Distributing source"

for line in `cat $VM_NODEFILE`
do

  echo "copy to: $line"
  scp -v -i ~/.ssh/id_rsa source34.zip jonstout@$line:~/

  echo "ssh for copy.sh"
  ssh -v -i ~/.ssh/id_rsa jonstout@$line 'bash -s' < copy.sh
done

sleep 10

# OUR CODE HERE
echo "Shutting down VM's"
/usr/local/bin/shutdown_vms

echo "VM Job finished"
