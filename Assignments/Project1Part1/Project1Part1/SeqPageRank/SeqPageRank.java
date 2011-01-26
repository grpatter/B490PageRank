import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SeqPageRank {
	public static HashMap<Integer, ArrayList<Integer>> links;
	public static HashMap<Integer, Double> finalPagerank;
	public static int maximumHops;
	public static double damping;
	
	/**
	 * Calculates the page rank of pageId.
	 * @param pageId ID of the page whose pagerank you wish to find.
	 * @param maximumHops The maximum number of hops allowed when calculating pagerank.
	 * @return The calculated pagerank of pageId.
	 */
	public static void getPageRank(Integer pageId, Integer maximumHops) {
		double tmpPagerank = 0.0;
				
		Iterator<Integer> link_iter = links.keySet().iterator();
		while(link_iter.hasNext()) {
			Integer tmp = link_iter.next();
			if (tmp != pageId) {
				
				if (links.get(tmp).contains(pageId)) {
					Integer outgoingLinkCount = links.get(tmp).size();
					
					if (outgoingLinkCount != 0) {
						tmpPagerank += finalPagerank.get(tmp)/links.get(tmp).size();
					}
					else {
						//
						finalPagerank.put(tmp, tmpPagerank);
					}
				}
			}
		}
		//
		finalPagerank.put(pageId, tmpPagerank);
	}
	
	/**
	 * Stores input from <file> as a HashMap.
	 * @param file File path.
	 */
	public static HashMap<Integer, ArrayList<Integer>> readLinks(String file) {
		links = new HashMap<Integer, ArrayList<Integer>>();

		try {
			BufferedReader f = new BufferedReader(new FileReader(file));
			
			// Add each page's link description into our sparse matrix.
			while (f.ready()) {
				String page = f.readLine();
				String[] s = page.split(" ");
				ArrayList<Integer> outboundLinks = new ArrayList<Integer>();
				
				for (int i = 1; i < s.length; i++)
					outboundLinks.add(Integer.valueOf(s[i]));
				
				links.put(Integer.valueOf(s[0]), outboundLinks);
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return links;
	}
	
	/**
	 * Prints a string representation of the recorded links.
	 * @return A string representation of this LinkCounter
	 */
	public static void printLinks() {
		Iterator<Integer> k = links.keySet().iterator();
		Iterator<ArrayList<Integer>> v = links.values().iterator();
		String r = "";
		while(k.hasNext()) {
			r += k.next() + "\t" + v.next() + "\n";
		}
		System.out.println(r);
	}
	
	public static void printPagerank() {
		Iterator<Integer> k = finalPagerank.keySet().iterator();
		String r = "";
		
		while(k.hasNext()) {
			Integer d = k.next();
			r += d + "\t" + finalPagerank.get(d) + "\n";
		}
		System.out.println(r);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* Check for arguments
		if (args.length != 4) {
			System.out.println("Usage: [input file] [output file] [iterations] [damping]");
			System.exit(1);
		}*/
		
		// Construct adjacency matrix and global variables.
		readLinks(System.getProperty("user.dir") + "/SeqPageRank/pagerank.input");
		
		finalPagerank = new HashMap<Integer, Double>();
		maximumHops = 20;
		damping = 0.85;
		
		// Set initial pagerank value of all URLs.
		Iterator<Integer> ite = links.keySet().iterator();
		while(ite.hasNext()) {
			finalPagerank.put(ite.next(), 1/(double)links.size());
		}
		
		for (int i = 0; i < 5; i++) {
			// Iterate once over all URLs and calculate their pagerank.
			// We need i to be == to iterations
			Iterator<Integer> prIter = links.keySet().iterator();
			while (prIter.hasNext()) {
				getPageRank(prIter.next(), maximumHops);
			}
		}
		
		// Apply damping factor to each link to finalize result.
		Iterator<Integer> dIter = finalPagerank.keySet().iterator();
		while (dIter.hasNext()) {
			Integer i = dIter.next();
			finalPagerank.put(i, finalPagerank.get(i)*damping + (1 - damping) / links.size());
		}
		printLinks();
		printPagerank();
	}
}
