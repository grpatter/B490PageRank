package main.java;

public class MonitorDaemon {
	
	public MonitorDaemon(String host, String port, String clusterName, int daemonNo) {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MonitorDaemon daemon = new MonitorDaemon(brokerHost, brokerPort, clusterName, daemonNo);
		daemon.run();
	}

}
