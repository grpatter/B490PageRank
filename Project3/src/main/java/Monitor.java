package main.java;

import java.util.Scanner;

public class Monitor {
	private MonitorWorker mWorker;
	
	public Monitor(String host, String port, String clusterName, int daemonNo) {
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
		Monitor daemon = null;
		boolean running = true;
		Scanner in = new Scanner(System.in);
		
		// TODO Add argument parsing
		if (args.length < 5) {
			System.out.println("[Broker host] [Broker port] [Cluster name] [Daemon number]");
			running = false;
		} else {
			daemon = new Monitor(args[1], args[2], args[3], Integer.parseInt(args[4]));
		}

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
