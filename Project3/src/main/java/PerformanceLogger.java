package main.java;




/**
 * Records and logs performance information about an elapsed time period.
 * 
 */
public class PerformanceLogger {

    private long startTime;
    private Long nodeId;
    
    public PerformanceLogger() {
        recordStartTime();
    }
    
    public PerformanceLogger(Long nodeId) {
        this();
        this.nodeId = nodeId;
    }
    
    private void recordStartTime() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void log(String message) {
        log(message, false);
    }

    public void log(String message, boolean terminalPoint) {
	        long endTime = System.currentTimeMillis();
	        long totalTime = endTime - startTime;
	        String logMessage = "Time: "+totalTime+" ms, ";
	        if (nodeId != null) {
	            logMessage+="nodeId="+nodeId+", ";
	        }
	        logMessage += message;
	        if (terminalPoint) {
	            logMessage += "\n";
	        }
	        System.out.println(logMessage);
//	        LOG.info(logMessage);
    }
    
}