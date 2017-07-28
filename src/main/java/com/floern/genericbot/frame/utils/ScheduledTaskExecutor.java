/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExecutor {

	private static ScheduledExecutorService executor;

	private static synchronized ScheduledExecutorService getExecutor() {
		if (executor == null) {
			executor = Executors.newSingleThreadScheduledExecutor();
		}
		return executor;
	}


	/**
	 * Schedule a task to be executed after the specified amount of time.
	 * @param task Task to be executed.
	 * @param delay Delay until execution in milliseconds.
	 */
	public static synchronized void scheduleTask(Runnable task, long delay) {
		getExecutor().schedule(task, delay, TimeUnit.MILLISECONDS);
	}


	/**
	 * Cancel all pending tasks and shutdown the service.
	 */
	public static synchronized void cancelAll() {
		if (executor != null) {
			executor.shutdownNow();
			executor = null;
		}
	}

}
