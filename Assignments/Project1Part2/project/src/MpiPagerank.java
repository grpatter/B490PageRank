import java.io.*;
import java.util.*;
import mpi.*;


public class MpiPagerank {

	public static int readPagerankInput(String file, int[][] AdjacencyMatrix) throws NumberFormatException, IOException {
		
	    BufferedReader f = new BufferedReader(new FileReader(file));
	    int totalUrlCount = 0;
	    int maxLineLength = 0;
	    
	    while (f.ready()) {
		String[] adjacencyInfo = f.readLine().split(" ");
		int infoLength = adjacencyInfo.length;
		totalUrlCount++;

		if (infoLength > maxLineLength) {
		    maxLineLength = infoLength;
		}
	    }

	    for (int i = 0; i < totalUrlCount; i++) {
		for (int j = 0; j < maxLineLength; j++) {
		    AdjacencyMatrix[i][j] = -1;
		}
	    }

	    f.close();
	    f = new BufferedReader(new FileReader(file));

	    int amPointer = 0;
	    while (f.ready()) {
		String[] adjacencyInfo = f.readLine().split(" ");

		for (int i = 0; i < adjacencyInfo.length; i++) {
		    AdjacencyMatrix[amPointer][i] = Integer.parseInt(adjacencyInfo[i]);
		}
		amPointer++;
	    }

	    f.close();
	    return totalUrlCount;
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
		int urlCount = 0;

		Double PublicPR[][] = new Double[1][1];
		Double PrivatePR[][] = new Double[1][1];
		

		// Declare global variables.
		int[][] globalAdjacencyMatrix = new int[1000][100];
		HashMap<Integer, Double> globalPagerank = new HashMap<Integer, Double>();
		int globalUrlCount = 0;
		// Declare local variables.
		HashMap<Integer, Double> localPagerank = new HashMap<Integer, Double>();
		int localUrlCount = 0;
		Double dangling = 0.0;//for worker threads
	
		// Read in adjacency matrix from file
		urlCount = readPagerankInput(pagerankInputFilename, globalAdjacencyMatrix);
				
		Intracomm mpiComm = MPI.COMM_WORLD;

		// TODO Broadcast initial pagerank value to workers
		if (nodeId == 0) {
		    // Code for root.
			System.out.println("Hello from king node " + nodeId + ". I rule over a kingdom of " + globalNodeCount + " nodes.");
			
			// TODO
			Object ad = (Object)globalAdjacencyMatrix;
			Object send[] = new Object[1];
			send[0] = ad;
			System.out.println("Bcasting from: " + nodeId);

			for(int i = 1; i < globalNodeCount; i++) {
			    mpiComm.Send(send, 0, send.length, MPI.OBJECT, i, 1);
			    System.out.println("Bcasting from: " + nodeId);
			}
			// TODO Receive slice of globalLinks back from workers and apply to globalLinks
			
			// TODO Receive dangle value from worker and add dangle/size to all nodes
			
			// TODO Apply damping factor to globalLinks
			
		} else {
			// Code for worker.
			System.out.println("I'm just a worker node named " + nodeId);

			// TODO Receive data from root.
			Object in[] = new Object[1];
			System.out.println("Recieving...");
			mpiComm.Recv(in, 0, 1, MPI.OBJECT, 0, MPI.ANY_TAG);
			System.out.println("Recieved");
			int[][] setToWork = new int[1][1];
			setToWork = (int[][])in[0];//from object to 2d array
			
			
			System.out.print(setToWork[0][1]);
			// TODO Find pagerank
//				Double dangling = 0.0;
				//iterate over given 2d array and update the PR values into privatepr
				for(int x = 0; x < setToWork.length; x++){
//					for(int y = 0; y<setToWork[x].length; y++){
						int[] currentLinks = setToWork[x];
					    int outgoingLinkCount = currentLinks.length;
					    
					    //Iterate through each of this webpage's neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links 
					    for(int j = 0; j < outgoingLinkCount; j++){
					    	int neighbor = currentLinks[j];
					    	if(neighbor != -1){
	//							tmp = tmpPagerank.get(neighbor) + finalPagerank.get(linkKey)/(double)outgoingLinkCount;
	//							tmpPagerank.put(neighbor, tmp);
						    	PrivatePR[x][neighbor] += PublicPR[x][j]/outgoingLinkCount;
						    	
					    	} else { dangling += PrivatePR[x][j]; }
					    }
//					}
				}
			
			// TODO Send slice of globalLinks to root
			
			// TODO Send dangle value
		}
		MPI.Finalize();
	}

}
