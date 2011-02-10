import java.io.*;
import java.util.*;
import mpi.*;

public class MpiPagerank {
	public static void readPagerankInput(String file, int localUrlCount, int[][] adjacencyMatrix, Intracomm mpiComm) throws NumberFormatException, IOException {
		int nodeId = mpiComm.Rank();
		int worldNodeCount = mpiComm.Size();
		int[] luc = new int[1];
		
		// Read in adjacencyMatrix
		if (nodeId == 0) {
			BufferedReader f = new BufferedReader(new FileReader(file));
			int i = 0;
			
			while (f.ready()) {
				String nodeInfo = f.readLine();
				String[] s = nodeInfo.split(" ");
				int[] adjacencyMatrixPart = new int[s.length];
				
				for (int j = 0; j < s.length; j++) {
					adjacencyMatrix[i][j] = Integer.parseInt(s[j]);
				}
			}
			luc[0] = adjacencyMatrix.length;
		}
		
		// Broadcast total url count to all nodes.
		mpiComm.Bcast(luc, 0, 1, MPI.INT, 0);
		
		localUrlCount = luc[0] / worldNodeCount;
		int remUrlCount = luc[0] / worldNodeCount;
		
		if (nodeId == 0) {
			int dest = 1;
			int counter = 0;
			
			for (int i = 0; i < luc[0]; i++) {
				counter++;
				
				if (counter >= 111) {
					dest++;
					counter = 0;
				}
				
				if (dest < worldNodeCount - 1) {
					//System.out.println("Sending... " + dest);
					mpiComm.Send(adjacencyMatrix[i], 0, 100, MPI.INT, dest, 1);
				} else {
					mpiComm.Send(adjacencyMatrix[luc[0] - (localUrlCount + remUrlCount)], 0, 100, MPI.INT, dest, 1);
					break;
				}					
			}
		} else {
			if (nodeId < worldNodeCount - 1) {
				for (int i = 0; i < localUrlCount; i++) {
					mpiComm.Recv(adjacencyMatrix[i], 0, 100, MPI.INT, 0, 1);
				}
				System.out.println("Worker node " + nodeId + ": recived data");
			} else {
				for (int i = 0; i < localUrlCount + remUrlCount; i++) {
					mpiComm.Recv(adjacencyMatrix[i], 0, 100, MPI.INT, 0, 1);
				}
				System.out.println("one");
			}
			
		}
		
		// TODO Assign part of the problem to localPagerank.
	}

	public static Double getPagerank(HashMap<Integer, ArrayList<Integer>> globalLinks, HashMap<Integer, Double> output) {
		Iterator<Integer> link_iter = globalLinks.keySet().iterator();
		ArrayList<Integer> tmpstack;
		Double dangling = 0.0;

		//Create and populate the temporary storage for Pageranks 
		HashMap<Integer, Double> tmpPagerank  = new HashMap<Integer, Double>();
		for(Integer i = 0; i < globalLinks.size(); i++){
		    tmpPagerank.put(i, 0.0);
		}

		//Iterate through each web page in links
		while(link_iter.hasNext()) {
		    int linkKey = link_iter.next();
		    tmpstack = globalLinks.get(linkKey);
		    int outgoingLinkCount = tmpstack.size();
		    Double tmp = 0.0;

		    //Iterate through each of this webpage's neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links 
		    for(int j = 0; j < outgoingLinkCount; j++){
			int neighbor = tmpstack.get(j);
			tmp = tmpPagerank.get(neighbor) + output.get(linkKey)/(double)outgoingLinkCount;
			tmpPagerank.put(neighbor, tmp);
		    }

		    //If this webpage has no outgoing links, calculate its contribution to the overall dangling value 
		    if(outgoingLinkCount == 0){
			dangling += output.get(linkKey);
		    }
		}
		
		// Return dangling value
		return dangling;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		MPI.Init(args);
		int nodeId = MPI.COMM_WORLD.Rank();
		int globalNodeCount = MPI.COMM_WORLD.Size();
		
		String pagerankInputFilename = "pagerank.input";
		
		// Declare global variables.
		int[][] globalAdjacencyMatrix = new int[1000][100];
		HashMap<Integer, Double> globalPagerank = new HashMap<Integer, Double>();
		int globalUrlCount = 0;
		// Declare local variables.
		HashMap<Integer, Double> localPagerank = new HashMap<Integer, Double>();
		int localUrlCount = 0;
		Double localDanglingSum = 0.0;
		
		// Read in adjacency matrix from file
		//readPagerankInput(pagerankInputFilename, globalAdjacencyMatrix, localPagerank);
		//globalUrlCount = globalAdjacencyMatrix.size();
		
		Intracomm mpiComm = MPI.COMM_WORLD;
		
		readPagerankInput(pagerankInputFilename, localUrlCount, globalAdjacencyMatrix, mpiComm);
		
		// TODO Broadcast initial pagerank value to workers
		
		if (nodeId == 0) {
			// Code for root.
			System.out.println("Hello from king node " + nodeId + ". I rule over a kingdom of " + globalNodeCount + " nodes.");

			// TODO
			
			// TODO Receive slice of globalLinks back from workers and apply to globalLinks
			
			// TODO Receive dangle value from worker and add dangle/size to all nodes
			
			// TODO Apply damping factor to globalLinks
			
		} else {
			// Code for worker.
			System.out.println("I'm just a worker node named " + nodeId);
			// TODO Receive data from root.

			// TODO Find pagerank
			//danglingValue = getPagerank(globalLinks, finalPagerank);
			
			// TODO Send slice of globalLinks to root
			
			// TODO Send dangle value
		}
		MPI.Finalize();
	}

}
