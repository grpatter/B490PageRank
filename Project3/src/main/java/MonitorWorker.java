package main.java;

@Deprecated
public class MonitorWorker implements Runnable {
	private int daemonNo = 0;
	private String clusterName;
	
	public MonitorWorker(String host, String port, String clusterName, int daemonNo) {
		this.clusterName = clusterName;
		this.daemonNo = daemonNo;
		
		pubSubBase = new pubSubBase(host, port);
	}
	
	public void terminate() {
		// Close connection to server.
	}
	
	@Override
	public void run() {
<<<<<<< HEAD
		// TODO Generate info read
		// TODO save info
		// TODO Publish updates to broker via MessageProducer
=======
		// TODO Auto-generated method stub
		// TODO Publish updates to broker (pubSubBase.publishEvent)
>>>>>>> 7c2c44a92a21dad88534155fbc117adcff59cea4
	}

}
