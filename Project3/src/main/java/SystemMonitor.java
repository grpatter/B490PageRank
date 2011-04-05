package main.java;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class SystemMonitor implements Runnable{
	char c;
	private volatile boolean running = false;
	private static Sigar sigar = null;
	private final static int SYS_MONITOR_INTERVAL = 100;




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
    private static String stringCpu(String prefix, CpuPerc cpu) {
        return new String(prefix +
                           CpuPerc.format(cpu.getUser()) + "\t" +
                           CpuPerc.format(cpu.getSys()) + "\t" +
                           CpuPerc.format(cpu.getWait()) + "\t" +
                           CpuPerc.format(cpu.getNice()) + "\t" +
                           CpuPerc.format(cpu.getIdle()) + "\t" +
                           CpuPerc.format(cpu.getCombined()));
    }
	// override run() method in interface
	public void run() {
	    while(running){
	    	 String report = "***SystemMonitor BEGIN Report***.\n";
	    	 try {
		    	 Sigar sigar = getSigar();
					report += stringCpu("   ", sigar.getCpuPerc());
					report += "\n Mem:\t" + sigar.getMem().toString();
	    	 }catch(UnsatisfiedLinkError e0){
	    		 System.out.println("Sigar encountered an unsatisfied link. Logging will fail from here forward. Stopping monitoring thread.");
	    		 System.out.println("!!! WARNING !!! Swallowing stack trace !!! WARNING !!!");
//	    		 e0.printStackTrace();
//	    		 this.setRunning(false);
			} catch (SigarException e1) {
	            System.out.println("SigarException caught, data missing.\n");
//				e1.printStackTrace();
			}
            report += "\n***SystemMonitor END Report***.\n";
            System.out.println(report);
            
	        try{ 
	            System.out.println("SystemMonitor sleeping.\n");
	           Thread.sleep((int)(SYS_MONITOR_INTERVAL));
	        } catch( InterruptedException e ) {
	            System.out.println("Interrupted Exception caught, shutting down monitor.\n");
	            this.setRunning(false);
	            sigar.close();
	            break;
	        }
	    }
	}
	
	   public Sigar getSigar() throws SigarException{
	        if (sigar == null) {
	            sigar = new Sigar();
	            if (false) {
	                sigar.enableLogging(true);
	            }
	        }
	        return sigar;
	    }


}
