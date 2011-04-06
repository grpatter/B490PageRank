package main.java;

@Deprecated
public class MonitorWorker implements Runnable {
	private int daemonNo = 0;
	private String clusterName;
	
	public MonitorWorker(String host, String port, String clusterName, int daemonNo) {
		
	}
	
	public void terminate() {
		// Close connection to server.
	}
	
	@Override
	public void run() {
		// TODO Generate info read
		// TODO save info
		// TODO Publish updates to broker via MessageProducer
	}

}
