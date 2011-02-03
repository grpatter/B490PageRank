import java.io.*;
import java.util.*;


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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello, world!");
		
		HashMap<Integer, ArrayList<Integer>> globalLinks = new HashMap<Integer, ArrayList<Integer>>();
		int rank;
		
		
		readPagerankInput(System.getProperty("user.dir") + "/src/PageRankData/pagerank.input.1000.0", globalLinks);
		
		System.out.println(globalLinks.size());
	}

}
