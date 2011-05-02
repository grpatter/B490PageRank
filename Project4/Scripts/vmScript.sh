#!/bin/sh
#
# vmScript.sh
#
# Set up grid environment
#PBS -q b534
#PBS -l nodes=i87+i88:ppn=8,walltime=00:13:00
#PBS -o run_pagerank_bm$PBS_JOBID.out
#PBS -V

#print the time and date
date

echo "Starting VM's"
. /usr/local/bin/start_vms

echo "Waiting for VM's"
/usr/local/bin/wait_for_vms $VM_NODEFILE
cat $VM_NODEFILE > machines.txt

echo "Distributing source"
for line in `cat $VM_NODEFILE`
do
  echo "Copy files to: $line"
  scp -v -i ~/.ssh/id_rsa source34.zip jonstout@$line:~/

  echo "ssh for copy.sh"
  ssh -v -i ~/.ssh/id_rsa jonstout@$line 'bash -s' < copy.sh
done

# OUR CODE HERE
echo "Shutting down VM's"
/usr/local/bin/shutdown_vms

echo "VM Job finished"
