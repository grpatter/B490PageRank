////// ********* B534 Course *********
This folder includes the files needed to compile and run the parallel pages rank calculations.
The files are:
-MPIMain.java: This file includes the main function.
-MPIRead.java: This file includes the MPIread function to read from the input file and convert it to adjacency
-MPIPageRank.java: This file inclludes the page rank calucation function that is run by each process.

To compile and run the program:

1)Install OpenMPI:sudo apt-get install openmpi-bin

2) Download MPJ Express and unpack it.

3)Set MPJ_HOME and PATH environmental variables:   
 export MPJ_HOME=/path/to/mpj/    
 export PATH=$PATH:$MPJ_HOME/bin      
 (These above two lines can be added to ~/.bashrc)

4) Start the MPJ Daemon using the following command: ./mpjdaemon_linux_x86_32 start

5) Compile the project: javac -cp .:$MPJ_HOME/lib/mpj.jar MPIMain.java

6) Execute the project : mpjrun.sh -np 3 MPIMain /home/user/Desktop/B534/Project1Part2/pagerank.input.1000.0 /home/user/Desktop/B534/Project1Part2/pagerank.output.1000.0 20 0.001

In the previous command the -np 3 indicates the number of processes.
This will take the following arguments:
1. Input file containing the adjacency matrix.
2. Output file to write the output rank values.
3. Iteration count that specifies the number of iterations for the calculation of rank.
(the higher the number of iterations, the more accurate the results would be)
4. The threshold value.



