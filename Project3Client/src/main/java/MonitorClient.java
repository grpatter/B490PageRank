package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class MonitorClient {
	private MonitorDaemon mDaemon;
	
	
	public MonitorDaemon getmDaemon() {
		return mDaemon;
	}

	public void setmDaemon(MonitorDaemon mDaemon) {
		this.mDaemon = mDaemon;
	}

	public MonitorClient(Properties configProperties) {
		this.mDaemon = new MonitorDaemon(configProperties);
		this.mDaemon.setRunning(true);
		Thread daemonThread = new Thread(mDaemon);
		daemonThread.setDaemon(true);
		daemonThread.start();
	}
	
	public void terminateWorker() {
		mDaemon.shutdown();
	}
	
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		boolean running = true;
//		
//		Properties props = new Properties();
//		InputStream inStream = null;
//		try{
//			  inStream = System.class.getResourceAsStream("/main/resources/"+"config.properties");
//			  props.load(inStream);
//		}catch(FileNotFoundException e){
//			System.out.println("ERROR: Unable to find properties file.");
//			e.printStackTrace();
//		}catch(IOException e1){
//			System.out.println("ERROR: Unable to load properties file.");
//			e1.printStackTrace();			
//		}finally {
//			if( null != inStream ) try { inStream.close(); } catch( IOException e ) { /* .... */ }
//		}
//		MonitorClient daemon = new MonitorClient(props);
//		
//
//		Scanner in = new Scanner(System.in);
//		String input = "";
//		while(running) {
//			input = in.nextLine();
//			
//			if (input.compareTo("exit") == 0) {
//				daemon.terminateWorker();
//				running = false;
//			} else {
//				System.out.println(input);
//				
//			}
//		}
//		if( null != in ) { in.close(); }
//	}
}
