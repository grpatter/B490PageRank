package main.java;

import java.util.Scanner;

public class MonitorDaemon {
	private MonitorWorker mWorker;
	
	public MonitorDaemon(String host, String port, String clusterName, int daemonNo) {
		this.mWorker = new MonitorWorker(host, port, clusterName, daemonNo);
		Thread daemonThread = new Thread(mWorker);
		daemonThread.start();
	}
	
	public void terminateWorker() {
		mWorker.terminate();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean running = true;
		Scanner in = new Scanner(System.in);
		
		MonitorDaemon daemon = new MonitorDaemon(brokerHost, brokerPort, clusterName, daemonNo);

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
