import java.io.*;


public class MpiInput {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		int[] am = new int[1];
		int[] amIndex = new int[1];
		int localUrlCount = 0;
		//readPagerankInput(System.getProperty("user.dir") + "/project/src/pagerank.input", localUrlCount, am);
		file_read(System.getProperty("user.dir") + "/project/src/pagerank.input", am, amIndex);
	}
	
	public static int file_read(String fileName, int[] adjacencyMatrix, int[] amIndex) throws IOException {
		BufferedReader f = new BufferedReader(new FileReader(fileName));
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
		
		adjacencyMatrix = new int[totalUrlCount*maxLineLength];
		amIndex = new int[totalUrlCount];
		//System.out.println(adjacencyMatrix.length + " " + amIndex.length);
		
		f.close();
		f = new BufferedReader(new FileReader(fileName));
		
		int amPointer = 0;
		int amArrayPointer = 0;
		while (f.ready()) {
			String[] adjacencyInfo = f.readLine().split(" ");
			
			amIndex[amArrayPointer] = amPointer;
			
			for (int i = 0; i < adjacencyInfo.length; i++) {
				adjacencyMatrix[amPointer] = Integer.parseInt(adjacencyInfo[i]);
				amPointer++;
			}
			amArrayPointer++;
		}
		
		for (int i = 0; i < 100; i++)
			System.out.print(adjacencyMatrix[i] + " ");
		System.out.println();
		for (int i = 0; i < amIndex.length/2; i++)
			System.out.print(amIndex[i] + " ");
		
		f.close();
		return totalUrlCount;
	}
	
	/*
	public static void readPagerankInput(String file, int localUrlCount, int[] adjacencyMatrix) throws NumberFormatException, IOException {
		int nodeId = 0; //mpiComm.Rank();
		int worldNodeCount = 10; //mpiComm.Size();
		int[] luc = new int[1];
		
		// Read in adjacencyMatrix
		if (nodeId == 0) {
			BufferedReader f = new BufferedReader(new FileReader(file));
			int i = 0;
			
			while (f.ready()) {
				String nodeInfo = f.readLine();
				String[] s = nodeInfo.split(" ");
				
				for (int j = 0; j < s.length; j++) {
					
				}
				i++;
			}
			luc[0] = adjacencyMatrix.length;
		}
		
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 50; j++) {
				System.out.print(adjacencyMatrix[i][j]);
			}
			System.out.println();
		}
		
		// Broadcast total url count to all nodes.
		// mpiComm.Bcast(luc, 0, 1, MPI.INT, 0);
		
		localUrlCount = luc[0] / worldNodeCount;
		int remUrlCount = luc[0] / worldNodeCount;
		
		/*
		if (nodeId == 0) {
			int dest = 1;
			int counter = 0;
			
			for (int i = 0; i < luc[0]; i++) {
				counter++;
				
				if (counter >= 111) {
					dest++;
					counter = 0;
				}
				
				if (dest < worldNodeCount - 1) {
					//System.out.println("Sending... " + dest);
					mpiComm.Send(adjacencyMatrix[i], 0, 100, MPI.INT, dest, 1);
				} else {
					mpiComm.Send(adjacencyMatrix[luc[0] - (localUrlCount + remUrlCount)], 0, 100, MPI.INT, dest, 1);
					break;
				}					
			}
		} else {
			if (nodeId < worldNodeCount - 1) {
				for (int i = 0; i < localUrlCount; i++) {
					mpiComm.Recv(adjacencyMatrix[i], 0, 100, MPI.INT, 0, 1);
				}
				System.out.println("Worker node " + nodeId + ": recived data");
			} else {
				for (int i = 0; i < localUrlCount + remUrlCount; i++) {
					mpiComm.Recv(adjacencyMatrix[i], 0, 100, MPI.INT, 0, 1);
				}
				System.out.println("one");
			}
			
		}
	}*/
}