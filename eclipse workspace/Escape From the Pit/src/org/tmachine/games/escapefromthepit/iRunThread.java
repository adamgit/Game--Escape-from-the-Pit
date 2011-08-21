package org.tmachine.games.escapefromthepit;

public interface iRunThread extends Runnable
{
	public boolean isRunning();
	
	/**
	 * This method is depressing; it exists because the Android maintainers are evil, and couldn't be bothered to implement a core Java Library method which Sun's arrogant language-maintainers marked as "deprecated" about 10 years ago and were too lazy to provide alternatives for - but Android doesn't throw an Error, it simply - SILENTLY - doesn't implement this core method; that's a pretty major bug!
	 */
	public void waitUntilStoppedBecauseAndroidHasABrokenJVM();
	
	public void start();
}