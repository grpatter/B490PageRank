package main.java;


public class SystemMonitor implements Runnable{
	char c;
	private volatile boolean running = false;




	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	SystemMonitor(char c, boolean running) {
	    this.c = c;
	    this.setRunning(running);
	}    

	// override run() method in interface
	public void run() {
	    while(running){
	        System.out.println("SystemMonitor Thread output (" + c + ")");
	        
	        try{ 
	           Thread.sleep((int)(Math.random() * 100));
	        } catch( InterruptedException e ) {
	            System.out.println("Interrupted Exception caught\n");
	            this.setRunning(false);
	        }
	    }
	}   


}
