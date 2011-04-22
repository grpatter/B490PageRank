#!/bin/sh
# This is a comment!
echo "Beginning SCP Ops"
for i in 10.0.2.131 10.0.2.132
do
  echo "SCP to Machine $i"
  scp -i ~/.ssh/id_rsa pr.zip root@$i:~/
  scp -i ~/.ssh/id_rsa sshHelp.sh root@$i:~/
done
echo "Done...hopefully!"