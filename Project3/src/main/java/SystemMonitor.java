package main.java;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class SystemMonitor implements Runnable{
	char c;
	private volatile boolean running = false;
	 private static Sigar sigar = null;




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
    private static void printCpu(String prefix, CpuPerc cpu) {
        System.out.println(prefix +
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
	    	 Sigar sigar = getSigar();
	    	 try {
		    	 CpuInfo[] infos = sigar.getCpuInfoList();
	
		         for (int i=0; i<infos.length; i++) {
		             CpuInfo info = infos[i];
	
		             System.out.println("num=" + i);
		             System.out.println("vendor=" + info.getVendor());
		             System.out.println("model=" + info.getModel());
		             System.out.println("mhz=" + info.getMhz());
		             System.out.println("cache size=" + info.getCacheSize());
		             System.out.println("totalSockets" + info.getTotalSockets());
		             System.out.println("totalCores" + info.getTotalCores());
		             System.out.println(info.getTotalSockets() <= info.getTotalCores());
		         }
	
	//				printCpu("   ", sysSigar.getCpuPerc());
	//				String ioLoad = sysSigar.getLoadAverage().toString();
	//		        System.out.println("SystemMonitor(Load):" + ioLoad);
			} catch (SigarException e1) {
	            System.out.println("SigarException caught, data missing.\n");
				e1.printStackTrace();
			}
	        try{ 
	           Thread.sleep((int)(Math.random() * 100));
	        } catch( InterruptedException e ) {
	            System.out.println("Interrupted Exception caught\n");
	            this.setRunning(false);
	        }
	    }
	}
	
	   public Sigar getSigar() {
	        if (sigar == null) {
	            sigar = new Sigar();
	            if (true) {
	                sigar.enableLogging(true);
	            }
	        }
	        return sigar;
	    }


}
