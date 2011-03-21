steps to run MPI PageRank. Assume the OpenMPI has already been installed on target cluster. https://kb.iu.edu/data/autn.html

1)run "make" to compile MPI PageRank
2)run "mpirun -np 1 mpi_main" to get help information
3)run "mpirun -np 2 mpi_main -i pagerank.input -n 10 -t 0.000001"

Cluster mode
4)run "mpirun -hostfile [cluster nodes ip address file] -np [number of processes] mpi_main -i pagerank.input -n 10 -t 0.000001"
  i.e. "mpirun -hostfile nodes -np 6 mpi_main -i pagerank.input -n 10 -t 0.000001"

notes: 
1) "am.input" is the PageRank input file in adjacency matrix format.
2) "pagerank.output" is the name of default output file name. 