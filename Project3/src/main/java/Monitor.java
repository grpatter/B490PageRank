package main.java;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class Monitor {
	private MonitorDaemon mWorker;
	
	public Monitor(String host, String port, String clusterName, String daemonNo) {
		this.mWorker = new MonitorDaemon(host, port, clusterName, daemonNo);
		Thread daemonThread = new Thread(mWorker);
		daemonThread.start();
	}
	
	public void terminateWorker() {
		mWorker.shutdown();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Monitor daemon = null;
		String brokerHost = "";
		String brokerPort = "";
		String clusterName = "";
		String daemonNo = "";
		boolean running = true;
				
		try {
			Scanner f = new Scanner(new FileReader(System.getProperty("user.dir") + "/src/main/resources/config.txt"));
			brokerHost = f.nextLine();
			brokerPort = f.nextLine();
			clusterName = f.nextLine();
			daemonNo = f.nextLine();
			f.close();
			System.out.println("Using: " + brokerHost + " " + brokerPort + " " + clusterName + " " + daemonNo);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		daemon = new Monitor(brokerHost, brokerPort, clusterName, daemonNo);
		
		Scanner in = new Scanner(System.in);

		while(running) {
			String input = in.nextLine();
			
			if (input.compareTo("exit") == 0) {
				daemon.terminateWorker();
				running = false;
			} else {
				System.out.println(input);
				
			}
		}
	}
}
