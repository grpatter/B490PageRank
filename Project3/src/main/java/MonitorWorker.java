package main.java;

public class MonitorWorker implements Runnable {
	private int daemonNo = 0;
	private ClientService connection;
	private PubSubBase pubSubBase;
	private String clusterName;
	
	public MonitorWorker(String host, String port, String clusterName, int daemonNo) {
		
	}
	
	public void terminate() {
		// Close connection to server.
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// TODO Publish updates to broker
	}

}
