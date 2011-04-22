while IFS= read -r line
do
  echo "SCP to Machine $line"
   scp -i ~/.ssh/id_rsa pr.zip root@$line:~/
   sleep 1
 done < machines
