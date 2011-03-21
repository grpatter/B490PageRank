import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class MpiInput2 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		int[][] am = new int[1][1];
		int totalUrlCount = 0;
		
		totalUrlCount = file_read(System.getProperty("user.dir") + "/src/pagerank.input", am);
	}
	
	public static int file_read(String fileName, int[][] adjacencyMatrix) throws IOException {
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
		
		// Declare adjacencyMatrix with -1 as a default value.
		adjacencyMatrix = new int[totalUrlCount][maxLineLength];
		for (int i = 0; i < totalUrlCount; i++) {
			for (int j = 0; j < maxLineLength; j++) {
				adjacencyMatrix[i][j] = -1;
			}
		}
		
		f.close();
		f = new BufferedReader(new FileReader(fileName));
		
		int amPointer = 0;
		while (f.ready()) {
			String[] adjacencyInfo = f.readLine().split(" ");
			
			for (int i = 0; i < adjacencyInfo.length; i++) {
				adjacencyMatrix[amPointer][i] = Integer.parseInt(adjacencyInfo[i]);
			}
			amPointer++;
		}
		
		f.close();
		
		
		/*
		// * Text print outs.
		// * 
		
		for (int i = 0; i < totalUrlCount; i++) {
			for (int j = 0; j < maxLineLength; j++) {
				System.out.print(adjacencyMatrix[i][j]);
			}
			System.out.println();
		}		
		System.out.println();
		System.out.println(totalUrlCount);
		*/

		return totalUrlCount;
	}
}