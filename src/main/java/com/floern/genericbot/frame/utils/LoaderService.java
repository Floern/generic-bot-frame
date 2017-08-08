/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public abstract class LoaderService {

	private final static Logger LOGGER = LoggerFactory.getLogger(LoaderService.class);

	protected static final long TIME_1_MINUTE = 60 * 1000L;
	protected static final long TIME_2_MINUTES = 2 * 60 * 1000L;
	protected static final long TIME_2_5_MINUTES = 150 * 1000L;
	protected static final long TIME_3_MINUTES = 3 * 60 * 1000L;
	protected static final long TIME_4_MINUTES = 4 * 60 * 1000L;
	protected static final long TIME_5_MINUTES = 5 * 60 * 1000L;
	protected static final long TIME_30_MINUTES = 30 * 60 * 1000L;

	private Timer timer;


	public synchronized void start() {
		if (timer != null) {
			throw new IllegalStateException("LoaderService already started");
		}

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					executeRequest();
				}
				catch (Exception e) {
					LOGGER.error("Unhandled Exception in TimerTask", e);
					throw e;
				}
			}
		}, 0, getTimeInterval());
	}


	protected abstract void executeRequest();


	protected abstract long getTimeInterval();


	public synchronized void stop() {
		if (timer == null) {
			throw new IllegalStateException("LoaderService is not running");
		}

		timer.cancel();
		timer.purge();
		timer = null;
	}

}
