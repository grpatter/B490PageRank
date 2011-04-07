package main.java;

@Deprecated
public class MonitorWorker implements Runnable {
	private int daemonNo = 0;
	private String clusterName;

	@Deprecated
	public MonitorWorker(String host, String port, String clusterName, int daemonNo) {
		this.clusterName = clusterName;
		this.daemonNo = daemonNo;
		
//		pubSubBase = new pubSubBase(host, port);
	}

	@Deprecated
	public void terminate() {
		// Close connection to server.
	}
	

	@Deprecated
	public void run() {
		// TODO Generate info read
		// TODO save info
		// TODO Publish updates to broker via MessageProducer
	}

}
