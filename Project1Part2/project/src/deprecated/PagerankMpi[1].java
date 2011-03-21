package main.java;

import java.io.*;
import java.util.*;
import mpi.*;

public class PagerankMpi {
	public static HashMap<Integer, ArrayList<Integer>> readInput(String filename, Intracomm mpiComm, int nodeId) {
		// Get globalUrlCount
		int globalUrlCount = 0;
						
		if (nodeId == 0) {
			try {
				BufferedReader f = new BufferedReader(new FileReader(filename));
								
				while (f.ready()) {
					f.readLine();
					globalUrlCount++;
				}				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Transmit portion of globalAdjacencyMatrix to all nodes.
		Object[] localAdjacencyMatrixB = new Object[1];
		HashMap<Integer, ArrayList<Integer>> localAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> globalAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		
		if (nodeId == 0) {
			try {
				BufferedReader f = new BufferedReader(new FileReader(filename));
				int blockSize = globalUrlCount/(mpiComm.Size() - 1);
				int remBlockSize = globalUrlCount % (mpiComm.Size() - 1);
				
				int outputNodeId = 1;
				while (f.ready()) {					
					if (remBlockSize > 0) {						
						for (int i = 0; i < remBlockSize+blockSize; i++) {
							ArrayList<Integer> tmpAdjacencyMatrix = new ArrayList<Integer>();
							String[] adjacencyMatrix = f.readLine().split(" ");
														
							for (int j = 1; j < adjacencyMatrix.length; j++)
								tmpAdjacencyMatrix.add(Integer.parseInt(adjacencyMatrix[j]));
							
							localAdjacencyMatrix.put(Integer.parseInt(adjacencyMatrix[0]), tmpAdjacencyMatrix);
							globalAdjacencyMatrix.put(Integer.parseInt(adjacencyMatrix[0]), tmpAdjacencyMatrix);
						}
						localAdjacencyMatrixB[0] = (Object)localAdjacencyMatrix;
						mpiComm.Send(localAdjacencyMatrixB, 0, 1, MPI.OBJECT, outputNodeId, 0);						
						System.out.println(remBlockSize+blockSize + " adjacency lines sent to: " + outputNodeId);
						
						outputNodeId++;
						remBlockSize = 0;
						localAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
					} else {
						for (int i = 0; i < blockSize; i++) {
							String[] adjacencyMatrix = f.readLine().split(" ");
							ArrayList<Integer> tmpAdjacencyMatrix = new ArrayList<Integer>();
							
							for (int j = 1; j < adjacencyMatrix.length; j++)
								tmpAdjacencyMatrix.add(Integer.parseInt(adjacencyMatrix[j]));
							
							localAdjacencyMatrix.put(Integer.parseInt(adjacencyMatrix[0]), tmpAdjacencyMatrix);	
							globalAdjacencyMatrix.put(Integer.parseInt(adjacencyMatrix[0]), tmpAdjacencyMatrix);
						}
						localAdjacencyMatrixB[0] = (Object)localAdjacencyMatrix;
						mpiComm.Send(localAdjacencyMatrixB, 0, 1, MPI.OBJECT, outputNodeId, 0);
						System.out.println(blockSize + " adjacency lines sent to: " + outputNodeId);
						
						outputNodeId++;
						localAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			mpiComm.Recv(localAdjacencyMatrixB, 0, 1, MPI.OBJECT, 0, 0);
			localAdjacencyMatrix = (HashMap<Integer, ArrayList<Integer>>)localAdjacencyMatrixB[0];				
		}
		
		// Return adjacency matrix.
		if (nodeId == 0)
			return globalAdjacencyMatrix;
		else
			return localAdjacencyMatrix;
	}
	
	public static void writeLinks(HashMap<Integer, Double> finalPagerank, String outputFile) {
		Iterator<Integer> k = finalPagerank.keySet().iterator();
		String r = "";

		try {
		    FileWriter fstream = new FileWriter(outputFile);
		    BufferedWriter out = new BufferedWriter(fstream);

		    while(k.hasNext()) {
			Integer d = k.next();
			r += d + "\t" + finalPagerank.get(d) + "\n";
		    }
		    out.write(r);
		    out.close();
		} 
		catch (Exception e) {
		    System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static double getPagerank(HashMap<Integer, ArrayList<Integer>> links, HashMap<Integer, Double> finalPagerank) {
		Iterator<Integer> link_iter = links.keySet().iterator();
		ArrayList<Integer> tmpstack;
		double dangling = 0.0;

		//Create and populate the temporary storage for Pageranks 
		HashMap<Integer, Double> tmpPagerank  = new HashMap<Integer, Double>();
		for(Integer i = 0; i < finalPagerank.size(); i++){
		    tmpPagerank.put(i, 0.0);
		}

		//Iterate through each web page in links
		while(link_iter.hasNext()) {
		    int linkKey = link_iter.next();
		    tmpstack = links.get(linkKey);
		    int outgoingLinkCount = tmpstack.size();
		    Double tmp = 0.0;

		    //Iterate through each of this webpage's neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links 
		    for(int j = 0; j < outgoingLinkCount; j++){
				int neighbor = tmpstack.get(j);
				tmp = tmpPagerank.get(neighbor) + finalPagerank.get(linkKey)/(double)outgoingLinkCount;
				tmpPagerank.put(neighbor, tmp);
		    }

		    //If this webpage has no outgoing links, calculate its contribution to the overall dangling value 
		    if(outgoingLinkCount == 0){
		    	dangling += finalPagerank.get(linkKey);
		    }
		}
		
		// Publish to finalPagerank.
		Iterator<Integer> prIter = tmpPagerank.keySet().iterator();
		while (prIter.hasNext()) {
			Integer i = prIter.next();
			finalPagerank.put(i, tmpPagerank.get(i));
		}
		return dangling;
	}
	
	public static List sortByValue(final Map m) {
        List keys = new ArrayList();
        keys.addAll(m.keySet());
        Collections.sort(keys, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = m.get(o1);
                Object v2 = m.get(o2);
                if (v1 == null) {
                    return (v2 == null) ? 0 : 1;
                }
                else if (v1 instanceof Comparable) {
                    return ((Comparable) v1).compareTo(v2);
                }
                else {
                    return 0;
                }
            }
        });
        return keys;
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Pagerank using MPI:");
		String filename = "pagerank.input";
		String outFilename = "pagerank.output";
		
		HashMap<Integer, ArrayList<Integer>> localAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, ArrayList<Integer>> globalAdjacencyMatrix = new HashMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, Double> localPagerank  = new HashMap<Integer, Double>();
		HashMap<Integer, Double> globalPagerank  = new HashMap<Integer, Double>();
		double globalPagerankB[] = null;
		
		int[] globalUrlCountB = new int[1];
		int globalUrlCount = 0;
		int iterations = 5;
		double totalPr = 0.0;
		double[] globalDangling = {0.0};
		double[] localDangling = {0.0};
		double dangling = 0.0;
		double damping = 0.85;
		
		// Change this if needed. Eclipse hack?
		if (args.length == 7) {
			filename = args[3];
			outFilename = args[4];
			iterations = Integer.parseInt(args[5]);
			damping = Double.parseDouble(args[6]);
		} else {
			for (int l = 0; l < args.length; l++)
				System.out.println(args[l]);
			System.exit(1);
		}
		// End change this.

		MPI.Init(args);
		Intracomm mpiComm = MPI.COMM_WORLD;
		int size = mpiComm.Size();
		int nodeId = mpiComm.Rank();
		
		for (int k = 0; k < iterations; k++) {		
			if (nodeId == 0) {
				// Get global adjacency matrix and distribute pieces to worker nodes.
				globalAdjacencyMatrix = readInput(System.getProperty("user.dir") + "/src/main/resources/" + filename, mpiComm, nodeId);
				
				// Broadcast globalUrlCount
				globalUrlCountB[0] = globalAdjacencyMatrix.size();
				mpiComm.Bcast(globalUrlCountB, 0, 1, MPI.INT, nodeId);
				globalUrlCount = globalUrlCountB[0];
				System.out.println(nodeId + " sent a globalUrlCount of: " + globalUrlCount);
											
				// AllReduce dangling value.
				mpiComm.Allreduce(localDangling, 0, globalDangling, 0, 1, MPI.DOUBLE, MPI.SUM);
				mpiComm.Barrier();
				dangling = globalDangling[0];
				
				// Allreduce localPagerankB
				double localPagerankB[] = new double[globalUrlCount];
				globalPagerankB = new double[globalUrlCount];
				mpiComm.Allreduce(localPagerankB, 0, globalPagerankB, 0, globalUrlCount, MPI.DOUBLE, MPI.SUM);
				mpiComm.Barrier();
								
				// Apply dangling and damping.
				double dvp = dangling/(double)globalUrlCount;
				for (int i = 0; i < globalUrlCount; i++) {
					globalPagerankB[i] += dvp;
					globalPagerankB[i] = (1 - damping)/(double)globalUrlCount + damping*globalPagerankB[i];
				}
				
				// Pull into HashMap.
				for (int i = 0; i < globalUrlCount; i++) {
					globalPagerank.put(i, globalPagerankB[i]);
				}				
			}
			else {
				// Get local adjacency matrix.
				localAdjacencyMatrix = readInput(System.getProperty("user.dir") + "/src/main/resources/" + filename, mpiComm, nodeId);
				
				// Broadcast globalUrlCount
				mpiComm.Bcast(globalUrlCountB, 0, 1, MPI.INT, 0);
				globalUrlCount = globalUrlCountB[0];
				System.out.println(nodeId + " recieved globalUrlCount of: " + globalUrlCount + " localUrls: " + localAdjacencyMatrix.size());
				
				for (int i = 0; i <globalUrlCount; i++) {
					localPagerank.put(i, 1/(double)globalUrlCount);
				}
				
				double localPagerankB[] = new double[globalUrlCount];
				globalPagerankB = new double[globalUrlCount];
				
				// Get pagerank values.
				dangling += getPagerank(localAdjacencyMatrix, localPagerank);
								
				// AllReduce dangling value.
				localDangling[0] = dangling;
				mpiComm.Allreduce(localDangling, 0, globalDangling, 0, 1, MPI.DOUBLE, MPI.SUM);
				mpiComm.Barrier();
				dangling = globalDangling[0];
				System.out.println(nodeId + " recieved a global dangling value of: " + dangling);
				
				Iterator<Integer> j = localPagerank.keySet().iterator();
				while (j.hasNext()) {
					Integer iVal = j.next();
					localPagerankB[iVal] = localPagerank.get(iVal);
				}
				
				// Allreduce localPagerankB
				mpiComm.Allreduce(localPagerankB, 0, globalPagerankB, 0, globalUrlCount, MPI.DOUBLE, MPI.SUM);
				
				//System.out.println(globalPagerankB.length);
				dangling = 0.0;
				mpiComm.Barrier();				
			}
		}
		// Write to file.
		writeLinks(globalPagerank, System.getProperty("user.dir") + "/src/main/resources/" + outFilename);
		
		// Print top 10 sites.			
		int count = 0;
		for (Iterator<Integer> i = sortByValue(globalPagerank).iterator(); i.hasNext(); ) {
			int key = i.next();
			
			if (count >= globalUrlCount-10)
				System.out.println("Key: " + key + " value: " + globalPagerank.get(key));
			count++;
			totalPr += globalPagerank.get(key);
		}
		System.out.println("Toatl pagerank value: " + totalPr);
		MPI.Finalize();
	}
}
