package main.java;

import java.util.Date;
import java.util.LinkedList;

import main.java.lifecycle.InfoPacket;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class MonitorDaemon implements Runnable {

	private volatile boolean running = false;
	private static Sigar sigar = null;
	private static LinkedList<InfoPacket> recordedData = new LinkedList<InfoPacket>();// TODO
																						// this
																						// needs
																						// to
																						// be
																						// synchronized

	private String host;
	private String port;
	private String clusterName;
	private String daemonNo;
	
	

	public MonitorDaemon(String host, String port, String clusterName,
			String daemonNo) {
		super();
		this.host = host;
		this.port = port;
		this.clusterName = clusterName;
		this.daemonNo = daemonNo;
	}

	public void start() {
		// TODO setup broker config
		// TODO setup broker connection

		// start config stuffs
//		SystemMonitor sysMon = new SystemMonitor('!', true);

		// Thread configurer = new Thread(sysMon);
		// configurer.setDaemon(true);
		// configurer.start();
	}

	public void run() {
		while (true) {
			InfoPacket curInfo = new InfoPacket();
			try {
				Sigar sigar = getSigar();
				curInfo.setCpuPerc(sigar.getCpuPerc());
				curInfo.setMemInfo(sigar.getMem());
				curInfo.setRecDate(new Date());
				recordedData.add(curInfo);
			} catch (UnsatisfiedLinkError e0) {
				System.out
						.println("Sigar encountered an unsatisfied link. Logging will fail from here forward. Stopping monitoring thread.");
				System.out
						.println("!!! WARNING !!! Swallowing stack trace !!! WARNING !!!");
				// e0.printStackTrace();
				// this.setRunning(false);
			} catch (SigarException e1) {
				System.out.println("SigarException caught, data missing.\n");
				// e1.printStackTrace();
			}

			try {
				System.out.println("SystemMonitor sleeping.\n");
				Thread.sleep((int) (MonitorConstants.SYS_MONITOR_INTERVAL));
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception caught, shutting down monitor.\n");
				this.setRunning(false);
				sigar.close();
				break;
			}
		}

	}

	public void shutdown() {
		System.out.println("Shutdown in progress.");
		// TODO close broker conn
		// TODO close sigar
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Sigar getSigar() throws SigarException {
		if (sigar == null) {
			sigar = new Sigar();
			// if (false) {
			// sigar.enableLogging(true);
			// }
		}
		return sigar;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getDaemonNo() {
		return daemonNo;
	}

	public void setDaemonNo(String daemonNo) {
		this.daemonNo = daemonNo;
	}

}
