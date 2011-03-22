#!/bin/sh
# This is a comment!
echo "Beginning SCP Ops"
for i in 10.0.2.131 10.0.2.132
do
  echo "SCP to Machine $i"
  scp -i ~/.ssh/id_rsa pr.zip root@$i:~/
  echo "SSH to machine $i"
  ssh $i
  shopt -s extglob
  rm !(pr.zip)
  unzip pr.zip .
  echo "Unzipped our stuffz"
  logout  
done
echo "Done...hopefully!"