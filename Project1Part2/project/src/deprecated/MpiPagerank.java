import java.io.*;
import java.security.AllPermission;
import java.util.*;
import mpi.*;
import mpi.Intracomm.*;


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

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		MPI.Init(args);
		int nodeId = MPI.COMM_WORLD.Rank();
		int globalNodeCount = MPI.COMM_WORLD.Size();
		String pagerankInputFilename = "C:\\pagerank.input";
		int urlCount = 0;
//		Double PublicPR[] = new Double[1000];
//		Double PrivatePR[] = new Double[1000];
		double PublicPR[] = new double[1000];
		double PrivatePR[] = new double[1000];
		int[][] globalAdjacencyMatrix = new int[1000][100];
		Double dangling = 0.0;
		Intracomm mpiComm = MPI.COMM_WORLD;
		int iteration = 1;
		// Read in adjacency matrix from file
		urlCount = readPagerankInput(pagerankInputFilename, globalAdjacencyMatrix);
		int localUrlCount = urlCount / globalNodeCount;
		
		//Populate the initial pagerank
		for (int m = 0; m < globalAdjacencyMatrix.length; m++) {
		    PublicPR[m] = 1.0/urlCount;  
		    PrivatePR[m] = 1.0/urlCount;	

		}


		for (int iter = 0; iter < iteration; iter++) {
		    int tracker = 1;			    
		
		    if (nodeId == 0) {
				//System.out.println("Hello from king node " + nodeId + ". I rule over a kingdom of " + globalNodeCount + " nodes.");
				
				while (tracker <= localUrlCount) {
				    for (int j = 1; j < globalNodeCount; j++) {
						int toBeSent[][] = new int[globalAdjacencyMatrix.length / globalNodeCount][globalAdjacencyMatrix[0].length];
						
						for (int k = 0; k < globalAdjacencyMatrix[0].length; k++) {
						    toBeSent[j - 1][k] = globalAdjacencyMatrix[(j * tracker) - 1][k]; 
						}
						
						System.out.println("This is the tracker: " + tracker);
						System.out.println("This is the j: " + j);
						Object ad = (Object)toBeSent;
						Object send[] = new Object[1];
						send[0] = ad;
						System.out.println("Sending from: " + nodeId + " to: " + tracker + " a set of size: " + toBeSent.length);			
						mpiComm.Send(send, 0, send.length, MPI.OBJECT, j, 1);			    
				    }
				    tracker++;
				    break;
				}
			
		    } else {
				System.out.println("I'm just a worker node named " + nodeId);
				
				// TODO Receive data from root.
				Object in[] = new Object[1];
				System.out.println("Recieving...");
				mpiComm.Recv(in, 0, 1, MPI.OBJECT, 0, MPI.ANY_TAG);
				System.out.println("Recieved");
				int[][] setToWork = new int[1][1];
				setToWork = (int[][])in[0];//from object to 2d array
				
				//System.out.print(setToWork[0][1]);
				for(int x = 0; x < setToWork.length; x++){
				    int[] currentLinks = setToWork[x];
				    int outgoingLinkCount = currentLinks.length;			
				    //Iterate through each of this webpage's neighbors and calculate their Pagerank based on this web pages current Pagerank and number of outgoing links
				    for(int j = 0; j < outgoingLinkCount; j++){
					int neighbor = currentLinks[j];
					if(neighbor != -1){
					    PrivatePR[x] += PublicPR[neighbor]/outgoingLinkCount;	
//					    System.out.println("Setting PrPR["+x+"] to:" + PrivatePR[x]);
					} else { dangling += PrivatePR[x]; }
				    }
				}
				for(int l = 0; l < urlCount; l++) {
				    System.out.println("AFTER CALCING PrPR["+l+"] is: " + PrivatePR[l]);			
				}
//				System.out.println("dangling is: " + dangling);
		    }
		    

			try {
				MpiPagerank.allreduceTest(args, PrivatePR, PublicPR, urlCount, dangling);
			} catch (Exception e) {
				e.printStackTrace();
			}

			MPI.Finalize();
		}
	}
	
	  public static void allreduceTest(String[] args, double[] PrivatePR, double[]PublicPR, int urlCount, Double dangling) throws Exception {

		    int j;
		    int myself;
		    int cur = 0,prev = 0;
		    Double dangPerPage = dangling/urlCount;
		    Double damping = 0.85; //TEMP

		    myself = MPI.COMM_WORLD.Rank();
		    
		    for(int a = 0; a< 1000; a++){
		    	System.out.println("BEFORE REDUCE node: " + a + " :PubPr[]/PrPr[] is: " + PublicPR[a] + " / " + PrivatePR[a]);		    	
		    }

		    for(j=1;j<urlCount;j++)  {
		      cur = j;
		      
		      MPI.COMM_WORLD.Allreduce(PrivatePR,0,PublicPR,0,j,MPI.DOUBLE,MPI.SUM); //Send, offset, Receive, offset, size, type, operation	      
		      MPI.COMM_WORLD.Barrier(); // block the caller until all process in the group have called it.
		      
		      if(cur != prev){
//		    	  PublicPR[j] = PublicPR[j] + dangPerPage;//apply dangling
		    	  System.out.println("AFTER REDUCE node: " + j + " :PubPr[]/PrPr[] is: " + PublicPR[j] + " / " + PrivatePR[j]);
		      }else{
		    	  prev = cur;
		    	  cur++;
		      }
		    }
		    
		    for(int y = 0; y<urlCount;y++){
		    	  PublicPR[y] = PublicPR[y] + dangPerPage;//apply dangling		    	
		    }

		    MPI.COMM_WORLD.Barrier();
		    if(myself == 0) { System.out.println("Allreduce COMPLETE");}

		    Double totalPR = 0.0;//For verification
		    for(int z = 0; z<urlCount;z++){//apply damping
		      PublicPR[z] = PublicPR[z] * damping + ((1 - damping)/urlCount);
	    	  System.out.println("AFTER DANG+DAMP node: " + z + " :PubPr[]is: " + PublicPR[z]);
	    	  totalPR += PublicPR[z];
		    }
		    System.out.println("TotalPR value (SUM) is: " + totalPR);
		  }
}