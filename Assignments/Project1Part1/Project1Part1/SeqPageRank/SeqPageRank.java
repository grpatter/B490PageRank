import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SeqPageRank {
	public static HashMap<Integer, ArrayList<Integer>> links;
	public static HashMap<Integer, Double> pagerank;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int maximumHops = 20;
		double d = 0.85;
		
		readLinks(System.getProperty("user.dir") + "/SeqPageRank/pagerank.input");
		System.out.println("Pages:\t" + links.size());
		printLinks();
		
		// BEGIN TODO
		for (int i = 0; i < links.size(); i++) {
			getPageRank(i, maximumHops);
		}
		// END TODO Auto-generated method stub
	}
	
	/**
	 * Calculates the page rank of pageId.
	 * @param pageId ID of the page whose pagerank you wish to find.
	 * @param maximumHops The maximum number of hops allowed when calculating pagerank.
	 * @return The calculated pagerank of pageId.
	 */
	public static double getPageRank(Integer pageId, Integer maximumHops) {
		// TODO
		return 0.0;
	}
	
	/**
	 * Stores input from parerank.input as a HashMap.
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
}
