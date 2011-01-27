import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SeqPageRank {
	
	/**
	 * Calculates the page rank of pageId.
	 * @param pageId ID of the link whose pagerank you wish to find.
	 * @return The calculated pagerank of pageId.
	 */
	public static void getPageRank(Integer pageId) {
		double tmpPagerank = 0.0;
		
		// Iterate over each page checking to see if it contains a link to page <pageId>.
		Iterator<Integer> link_iter = links.keySet().iterator();
		while(link_iter.hasNext()) {
			Integer linkKey = link_iter.next();
			
			// If the page contains a link to <pageId>, add to the new pagerank.
			if (linkKey != pageId && links.get(linkKey).contains(pageId)) {
				Double pagerank = finalPagerank.get(linkKey);
				Integer outgoingLinkCount = links.get(linkKey).size();
				
				if (outgoingLinkCount != 0) {
					tmpPagerank += (1 - damping)/links.size() + damping * (pagerank/outgoingLinkCount);
				} else {
					tmpPagerank += (1 - damping)/links.size();
				}
			}
		}
		// Publish pagerank to <finalPagerank>
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
		// BEGIN: Read in arguments if provided, otherwise use the default values.
		if (args.length == 4) {
			links = readLinks(System.getProperty("user.dir") + args[0]);
			finalPagerank = new HashMap<Integer, Double>();
			outputFile = args[1];
			iterations = Integer.valueOf(args[2]);
			damping = Double.valueOf(args[3]);
		} else {
			System.out.println("Usage: [input file] [output file] [iterations] [damping]");
			
			links = readLinks(System.getProperty("user.dir") + "/SeqPageRank/pagerank.input");
			finalPagerank = new HashMap<Integer, Double>();
			outputFile = null;
			iterations = 1;
			damping = 0.85;
			//System.exit(1);
		}
		// END: Read in arguments...
		
		// Set initial pagerank value of all URLs.
		Iterator<Integer> ite = links.keySet().iterator();
		while(ite.hasNext()) {
			finalPagerank.put(ite.next(), 1/(double)links.size());
		}
		
		// Re-calculate the pagerank of all links i times.
		for (int i = iterations; i > 0; i--) {
			// Calculate the pagerank of each link contained in <links>.
			Iterator<Integer> prIter = links.keySet().iterator();
			while (prIter.hasNext()) {
				getPageRank(prIter.next());
			}
			
			// Apply damping factor to the links after each iteration.
			Iterator<Integer> dIter = finalPagerank.keySet().iterator();
			while (dIter.hasNext()) {
				Integer j = dIter.next();
				finalPagerank.put(j, (1 - damping) / links.size() + damping*finalPagerank.get(j));
			}
		}
		
		// Find total pagerank.
		Iterator<Integer> prCount = finalPagerank.keySet().iterator();
		Double prR = 0.0;
		while (prCount.hasNext()) {
			prR += finalPagerank.get(prCount.next());
		}
		
		printPagerank();
		// Print total pagerank.
		System.out.println(prR);
	}
	
	// Global variables.
	public static HashMap<Integer, ArrayList<Integer>> links;
	public static HashMap<Integer, Double> finalPagerank;
	public static String outputFile;
	public static int	iterations;
	public static Double damping;
}
