import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mpi.MPI;

public class MpiPagerank {
	
	private static Integer TEST_ARRAY_X = 5;
	private static Integer TEST_ARRAY_Y = 10;
	
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
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// Declare global variables.
		HashMap<Integer, ArrayList<Integer>> globalLinks = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<PagerankData> finalLinks = new ArrayList<PagerankData>();
		HashMap<Integer, Double> finalPagerank = new HashMap<Integer, Double>();
		Double danglingValue = 0.0;
		String file = "C:\\pagerank.input";
		
		MPI.Init(args);
		int nodeId = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		
		
		if (nodeId == 0) {
			// Code for root.
			System.out.println("Hello from king node " + nodeId + ". I rule over a kingdom of " + size + " nodes.");
			
			// VERIFY Read in globalLinks from file
			readPagerankInput(file, globalLinks);
			System.out.println(file + " " + globalLinks.size());
			
			//TODO fill this with actual values
			//fill with garbage since its a TEST
			double [][]test = MpiPagerank.fillDoubleArray(TEST_ARRAY_X, TEST_ARRAY_Y);
			
			Object[] testO = new Object[1];
			testO[0] = (Object)test;		
						
			// TODO Broadcast data to workers
			Thread.currentThread().sleep(2000);

			MPI.COMM_WORLD.Send(testO, 0, 1, MPI.OBJECT, 1, 10);
			System.out.println("sent");
			MPI.COMM_WORLD.Bcast(testO, 0, 1, MPI.OBJECT, nodeId);
			System.out.println("bcasted");
						
			// TODO Receive slice of globalLinks back from workers and apply to globalLinks
			
			// TODO Receive dangle value from worker and add dangle/size to all nodes
			
			// TODO Apply damping factor to globalLinks
			
		} else {
			// Code for worker.
			System.out.println("I'm just a worker node named " + nodeId);
			byte[] yourBytes = new byte[100000];
			
			// TODO Receive data from root.
			Object[]testRO = new Object[1];

			MPI.COMM_SELF.Recv(testRO, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG);
			System.out.println("received");
			double [][] testCheck = MpiPagerank.getNewDoubleArray();
			testCheck = (double[][]) testRO[0];
			
			
			// TODO Find pagerank
			danglingValue = getPagerank(globalLinks, finalPagerank);
			
			// TODO Send slice of globalLinks to root
			
			// TODO Send dangle value
		}
		MPI.Finalize();
	}
	
	/**
	 * This is an internal test function.
	 * @param x dimension 1
	 * @param y dimension 2
	 * @return 2d array of type double
	 */
	private static double[][] fillDoubleArray(int x, int y){
		double [][]test = new double[x][y];
		for(int i = 0; i < x;i++)
			for(int j = 0; j< y; j++)
				test[i][j] = j+1*i+1;
		
		return test;		
	}
	
	/**
	 * This is an internal test function.
	 * @param x dimension 1
	 * @param y dimension 2
	 * @return 2d array of type double
	 */
	private static double[][] getNewDoubleArray(){
		double [][]test = new double[TEST_ARRAY_X][TEST_ARRAY_Y];
		for(int i = 0; i < TEST_ARRAY_X;i++)
			for(int j = 0; j< TEST_ARRAY_Y; j++)
				test[i][j] = 0;
		
		return test;		
	}
	
	

}
