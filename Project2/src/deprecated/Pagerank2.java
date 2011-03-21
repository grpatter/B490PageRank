import java.io.*;
import java.util.*; 
import mpi.*;
import mpi.Intracomm.*;

 
/**
 * This program is used for generating the Pageranks of a given set of web pages 
 * @author Rochad Tlusty 
 * @author Greg Patterson 
 * @author Jonathan Stout 
 * @author Matt Sacks 
 */

public class Pagerank2 {

    /**
     * Calculates the Pagerank of each page in links
     * @param links The complete data structure of web page numbers and outgoing links.
     * @param finalPagerank The final data structure for Pagerank storage.  
     */
    
    public static void getPageRank(HashMap<Integer, ArrayList<Integer>> links, HashMap<Integer, Double> finalPagerank, Double damping) {
		Iterator<Integer> link_iter = links.keySet().iterator();
		ArrayList<Integer> tmpstack;
		Double dangling = 0.0;
				
		System.out.println("fpSize:" + finalPagerank.size());
		System.out.println("linksSize:" + links.keySet().size());
				
		//Create and populate the temporary storage for Pageranks
		HashMap<Integer, Double> tmpPagerank = new HashMap<Integer, Double>();
		while (link_iter.hasNext()) {
			Integer next = link_iter.next();
			tmpPagerank.put(next, 0.0);
			finalPagerank.put(next, 0.0);
	    	System.out.println("nextAdded"+next);
		}
		
		Iterator<Integer> link_iterr = links.keySet().iterator();
		
		//Iterate through each web page in links
		while(link_iterr.hasNext()) {
		    Integer linkKey = link_iterr.next();
			System.out.println("linkKey:"+linkKey);
		    tmpstack = links.get(linkKey);
		    int outgoingLinkCount = tmpstack.size();
		    Double tmpPr = 0.0;
		    //Iterate through each of this webpage's neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links
		    Iterator<Integer> j = tmpstack.iterator();
		    while (j.hasNext()) {
		    	int neighbor = j.next();
		    	System.out.println("nei:"+neighbor);
		    	System.out.println("tP:"+tmpPagerank.get(neighbor));
		    	System.out.println("fP:"+finalPagerank.get(linkKey));
		    	System.out.println("oLC:"+outgoingLinkCount);
		    	if(tmpPagerank.get(neighbor)!=null){
		    		tmpPr = tmpPagerank.get(neighbor) + finalPagerank.get(linkKey) / (double)outgoingLinkCount;
		    	}
		    	tmpPagerank.put(neighbor, tmpPr);
		    }
	
		    //If this webpage has no outgoing links, calculate its contribution to the overall dangling value 
		    if(outgoingLinkCount == 0){
		    	dangling += finalPagerank.get(linkKey);
		    }
		}
		
		//Calculate the dangling value per page, from the overall dangling value 
		//double dangling_value_per_page = dangling/(double)links.size();
		
		//After all Pageranks are calculated in the tmpPagerank structure, publish them to finalPagerank and add the dangling value per page
		//for (int i = 0; i < links.size(); i++) finalPagerank.put(i, tmpPagerank.get(i)+ dangling_value_per_page);
		    		
		/*Add the damping factor to each Pagerank 
		for(int k = 0; k < links.size(); k++) {
		    finalPagerank.put(k, (1-damping)/(double)links.size() + damping *
				      finalPagerank.get(k));
		}*/
    }
    
    /**
     * Stores input from the designated file as a HashMap.
     * @param file The file path.
     * @param links The complete data structure of web page numbers and outgoing links.
     */
    public static void readLinks(String file, HashMap<Integer, ArrayList<Integer>> links) {
	
	try {
	    BufferedReader f = new BufferedReader(new FileReader(file));
	    
	    while (f.ready()) {
		String page = f.readLine();
		String[] s = page.split(" ");
		ArrayList<Integer> outboundLinks = new ArrayList<Integer>();		
		for (int i = 1; i < s.length; i++) outboundLinks.add(Integer.valueOf(s[i]));		
		links.put(Integer.valueOf(s[0]), outboundLinks);
	    }
	} 
	catch (FileNotFoundException e) {
	    e.printStackTrace();
	} 
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Writes the pagerank to the designated outputFile.
     * @param finalPagerank The final data structure for Pagerank storage.  
     * @param outputFile The file designated by the user. 
     */
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
    

    /**
     * The main function takes in the users arguments and calculates the Pageranks for a given set of web pages. 
     * @param args[0] [input file]
     * @param args[1] [output file] 
     * @param args[2] [iteration count] 
     * @param args[3] [damping factor] 
     */
    public static void main(String[] args)  throws IOException, ClassNotFoundException {
	MPI.Init(args);
	int nodeId = MPI.COMM_WORLD.Rank();
	int globalNodeCount = MPI.COMM_WORLD.Size();
	Intracomm mpiComm = MPI.COMM_WORLD;
	HashMap<Integer, ArrayList<Integer>> cast = new HashMap<Integer, ArrayList<Integer>>();

	
	HashMap<Integer, ArrayList<Integer>> links = new HashMap<Integer, ArrayList<Integer>>();
	HashMap<Integer, Double> finalPagerank = new HashMap<Integer, Double>();
	HashMap<Integer, Double> localPagerank = new HashMap<Integer, Double>();
	int iterations = 0;
	Double damping = 0.0;
	String outputFile = "";
	String inputfile = "C:\\pagerank.input";
	
	outputFile = "pagerank.output";
	iterations = 1;
	damping = .85;


	
	if (nodeId == 0) {
		readLinks(inputfile, links);
		/*
	    Object ad = (Object)links;
	    Object send[] = new Object[1];
	    send[0] = ad;
	    System.out.println("Bcasting from: " + nodeId);
	    mpiComm.Send(send, 0, send.length, MPI.OBJECT, 1, 1);
	    System.out.println("Bcasting from: " + nodeId);
		*/
		distributeContent(links, globalNodeCount, mpiComm);
	    //Write Pageranks to output file
	}
	
	// Set initial Pagerank value of all web pages
	Iterator<Integer> ite = links.keySet().iterator();
	while(ite.hasNext()) {
		finalPagerank.put(ite.next(), 1/(double)links.size());
	}
	
	System.out.println("PRSIZE " + finalPagerank.size());
	
	if (nodeId == 0) {
		
	}
	else {
	    // Code for worker.                                                                  
	    System.out.println("I'm just a worker node named " + nodeId);
	    
	    // TODO Receive data from root.                                                      
	    Object in[] = new Object[1];
	    mpiComm.Recv(in, 0, 1, MPI.OBJECT, 0, MPI.ANY_TAG);
	    System.out.println("Adjacency matrix recieved from: " + nodeId);
	    cast = (HashMap<Integer, ArrayList<Integer>>)in[0];
	    
		getPageRank(cast, localPagerank, damping);
	}

	//allreduce here
	Iterator<Integer> xx = localPagerank.keySet().iterator();
	while(xx.hasNext()){
		Integer x = xx.next();
		System.out.println(x + " is: " + localPagerank.get(x));
	}
	
	writeLinks(finalPagerank, outputFile);	
	
	MPI.Finalize();
 
    }

    public static void distributeContent(HashMap<Integer, ArrayList<Integer>> links, int globalNodeCount, Intracomm mpiComm) {
    	HashMap<Integer, ArrayList<Integer>> outboundMatrix = new HashMap<Integer, ArrayList<Integer>>();
    	Iterator<Integer> iter = links.keySet().iterator();
    	int globalUrlCount = links.size();
    	int urlBlockSize = (globalUrlCount/globalNodeCount) + (globalUrlCount % globalNodeCount);
    	
    	for (int nodeId = 1; nodeId < globalNodeCount; nodeId++) {
    		int i = 0;
    		
    		while (iter.hasNext() && i < urlBlockSize) {
   				Integer tIter = iter.next();
    			outboundMatrix.put(tIter, links.get(tIter));
    			i++;
    		}
    		    		
    		Object ad = (Object)outboundMatrix;
    	    Object send[] = new Object[1];
    	    send[0] = ad;
    	    mpiComm.Send(send, 0, send.length, MPI.OBJECT, nodeId, 1);
    	    System.out.println("Sent adjacency matrix from: " + nodeId);
    	}
    }










    
}
