import java.io.*;
import java.util.*; 
 
/**
 * This program is used for generating the Pageranks of a given set of web pages 
 * @author Rochad Tlusty 
 * @author Greg Patterson 
 * @author Jonathan Stout 
 * @author Matt Sacks 
 */

public class SeqPageRank {

    /**
     * Calculates the Pagerank of each page in links
     * @param links The complete data structure of web page numbers and outgoing links.
     * @param finalPagerank The final data structure for Pagerank storage.  
     */
    
    public static void getPageRank(HashMap<Integer, ArrayList<Integer>> links, HashMap<Integer, Double> finalPagerank, Double damping) {
	Iterator<Integer> link_iter = links.keySet().iterator();
	ArrayList<Integer> tmpstack;
	Double dangling = 0.0;

	//Create and populate the temporary storage for Pageranks 
	HashMap<Integer, Double> tmpPagerank  = new HashMap<Integer, Double>();
	for(Integer i = 0; i < links.size(); i++){
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
	
	//Calculate the dangling value per page, from the overall dangling value 
	double dangling_value_per_page = dangling/(double)links.size();
	
	//After all Pageranks are calculated in the tmpPagerank structure, publish them to finalPagerank and add the dangling value per page
	for (int i = 0; i < links.size(); i++) finalPagerank.put(i, tmpPagerank.get(i)+ dangling_value_per_page);
	    		
	//Add the damping factor to each Pagerank 
	for(int k = 0; k < links.size(); k++) {
	    finalPagerank.put(k, (1-damping)/(double)links.size() + damping *
			      finalPagerank.get(k));
	}
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
    public static void main(String[] args) {
	HashMap<Integer, ArrayList<Integer>> links = new HashMap<Integer, ArrayList<Integer>>();
	HashMap<Integer, Double> finalPagerank = new HashMap<Integer, Double>();
	int iterations = 0;
	Double damping = 0.0;
	String outputFile = "";
	
	if (args.length == 4) {
	    readLinks(System.getProperty("user.dir") + "//" + args[0], links);
	    finalPagerank = new HashMap<Integer, Double>();
	    outputFile = args[1];
	    iterations = Integer.valueOf(args[2]);
	    damping = Double.valueOf(args[3]);
	} else {
	    System.out.println("Error - Usage: [input file] [output file] [iteration count] [damping factor]");	    
	    System.exit(1);
	}
	
	// Set initial Pagerank value of all web pages
	Iterator<Integer> ite = links.keySet().iterator();
	while(ite.hasNext()) {
	    finalPagerank.put(ite.next(), 1/(double)links.size());
	}
	
	//Loop for iteration count 
	for(Integer j = 0; j < iterations; j++) {
	    getPageRank(links, finalPagerank, damping);
	}

	//Write Pageranks to output file 
	writeLinks(finalPagerank, outputFile);
    }
    
}