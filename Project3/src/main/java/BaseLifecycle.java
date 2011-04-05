
package main.java;


/**
 * An abstract superclass to aid in implementing lifeycles.
 *
 * @author grpatter
 */
public abstract class BaseLifecycle implements Lifecycle {

	private boolean started = false;

	public boolean isStarted() {
		return this.started;
	}

	public void start() throws Exception {
		setStarted(true);
	}

	public void stop() throws Exception {
		setStarted(false);
	}

	protected void setStarted(boolean started) {
		this.started = started;
	}

}
