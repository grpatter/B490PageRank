import java.io.*;
import java.util.*;
import mpi.*;
import mpi.Intracomm.*;

public class MpiPagerank {
	public static void readPagerankInput(String file, HashMap<Integer, ArrayList<Integer>> links) {
		try {
			BufferedReader f = new BufferedReader(new FileReader(file));

			while (f.ready()) {
				String page = f.readLine();
				String[] s = page.split(" ");
				ArrayList<Integer> outboundLinks = new ArrayList<Integer>();

				for (int j = 1; j < s.length; j++) {
					outboundLinks.add(Integer.valueOf(s[j]));
				}

				links.put(Integer.valueOf(s[0]), outboundLinks);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		// Declare global variables.
		HashMap<Integer, ArrayList<Integer>> globalLinks = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<PagerankData> finalLinks = new ArrayList<PagerankData>();
		HashMap<Integer, Double> finalPagerank = new HashMap<Integer, Double>();
		Double danglingValue = 0.0;
		String file = "pagerank.input";
		
		MPI.Init(args);
		int nodeId = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		
		if (nodeId == 0) {
			// Code for root.
			System.out.println("Hello from king node " + nodeId + ". I rule over a kingdom of " + size + " nodes.");
			
			// TODO Read in globalLinks from file
			readPagerankInput(file, globalLinks);
			System.out.println(file + " " + globalLinks.size());
			
			// TODO Broadcast data to workers			
			MPI.COMM_WORLD.Bcast(new int[]{1,2,3}, 0, 3, MPI.INT, nodeId);
						
			// TODO Receive slice of globalLinks back from workers and apply to globalLinks
			
			// TODO Receive dangle value from worker and add dangle/size to all nodes
			
			// TODO Apply damping factor to globalLinks
			
		} else {
			// Code for worker.
			System.out.println("I'm just a worker node named " + nodeId);
			byte[] yourBytes = new byte[100000];
			// TODO Receive data from root.

			// TODO Find pagerank
			danglingValue = getPagerank(globalLinks, finalPagerank);
			
			// TODO Send slice of globalLinks to root
			
			// TODO Send dangle value
		}
		MPI.Finalize();
	}

}
