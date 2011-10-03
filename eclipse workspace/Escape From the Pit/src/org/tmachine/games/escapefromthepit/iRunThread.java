package org.tmachine.games.escapefromthepit;

public interface iRunThread extends Runnable
{
	public boolean isRunning();
	
	/**
	 * This method is depressing; it exists because ... Android doesn't support Thread.stop();
	 * I still believe that Sun's decisions to deprecate this method were unfounded, and unreasonable.
	 */
	public void waitUntilStoppedBecauseAndroidHasABrokenJVM();
	
	public void start();
}