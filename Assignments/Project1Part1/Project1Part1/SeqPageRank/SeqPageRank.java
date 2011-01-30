import java.io.*;
import java.util.*; 

/**
 * TODO before submittal:
 * global variables need to be removed // Is this done?
 * javadocs and proper documentation need to be completed 
 * writing to the output file will be fixed (i.e. actually happen) 
 */

public class SeqPageRank {
    public static String outputFile;
    public static int iterations;
    public static Double damping;

    /**
     * @param args
     */
    public static void main(String[] args) {
        HashMap<Integer, ArrayList<Integer>> links = new HashMap<Integer, ArrayList<Integer>>();
        HashMap<Integer, Double> finalPagerank = new HashMap<Integer, Double>();

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

        // Set initial pagerank value of all URLs.
        Iterator<Integer> ite = links.keySet().iterator();
        while(ite.hasNext()) {
            finalPagerank.put(ite.next(), 1/(double)links.size());
        }

        // Loop for iterations 
        for(Integer j = 0; j < iterations; j++) {
            getPageRank(links, finalPagerank);
        }

        printPagerank(finalPagerank);
        
        // TODO
        // writePageRank(finalPagerank);
    }

    /**
     * Calculates the page rank of pageId.
     * @param links Original list of nodes and links
     * @param finalPagerank Calculated Pagerank
     * @return The calculated pagerank of pageId.
     */
    public static void getPageRank(HashMap<Integer, ArrayList<Integer>> links, HashMap<Integer, Double> finalPagerank) {
        // Iterate over each page checking to see if it contains a link to page <pageId>.
        Iterator<Integer> link_iter = links.keySet().iterator();
        ArrayList<Integer> tmpstack; // ???
        Double dangling = 0.0; // ???
        HashMap<Integer, Double> tmpPagerank  = new HashMap<Integer, Double>();

        // Put initial PageRank in temporary HashMap
        for(Integer i = 0; i < links.size(); i++){
            tmpPagerank.put(i, 0.0);
        }

        while(link_iter.hasNext()) {
            int linkKey = link_iter.next();
            tmpstack = links.get(linkKey);
            int outgoingLinkCount = tmpstack.size();
            Double tmp = 0.0; // TODO change variable name

            for(int j = 0; j < outgoingLinkCount; j++) {
                int neighbor = tmpstack.get(j);
                tmp = tmpPagerank.get(neighbor) + finalPagerank.get(linkKey)/(double)outgoingLinkCount;
                tmpPagerank.put(neighbor, tmp);
            } if( outgoingLinkCount == 0) {
                dangling += finalPagerank.get(linkKey);
            }
        }

        double dangling_value_per_page = dangling/(double)links.size(); // TODO what is this?

        for (int i = 0; i < links.size(); i++) {
            finalPagerank.put(i, tmpPagerank.get(i)+ dangling_value_per_page);
            finalPagerank.put(i, (1-damping)/(double)links.size() + damping *
                    finalPagerank.get(i));
        }
    }

    /**
     * Stores input from <file> as a HashMap.
     * @param file File path.
     * @param links Original list of nodes and links
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
        catch (FileNotFoundException e) { e.printStackTrace(); } 
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Print final PageRank to stdout
     * @param finalPagerank The final calculated PageRank
     * @return All calculations printed
     */
    public static void printPagerank(HashMap<Integer, Double> finalPagerank) {
        Iterator<Integer> k = finalPagerank.keySet().iterator();
        String r = ""; // Result

        while(k.hasNext()) {
            Integer d = k.next();
            r += d + "\t" + finalPagerank.get(d) + "\n";
        }
        System.out.println(r);
    }
}
