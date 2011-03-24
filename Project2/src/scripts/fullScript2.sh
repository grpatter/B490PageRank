while IFS= read -r line
do
  echo "SCP to Machine $line"
   ssh -i ~/.ssh/id_rsa root@$line 'bash -s' < ./sshHelp.sh
done < machines
