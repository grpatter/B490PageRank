package main.java;

/**
 * Interface describing an object life cycle
 * 
 * @author grpatter
 */
public interface Lifecycle {
	public void start() throws Exception;
	public void stop() throws Exception;
	public boolean isStarted();
}
