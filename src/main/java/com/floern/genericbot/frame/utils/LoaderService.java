/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.Timer;
import java.util.TimerTask;

public abstract class LoaderService {

	protected static final long TIME_1_MINUTE = 60 * 1000L;
	protected static final long TIME_2_MINUTES = 2 * 60 * 1000L;
	protected static final long TIME_2_5_MINUTES = 150 * 1000L;
	protected static final long TIME_3_MINUTES = 3 * 60 * 1000L;
	protected static final long TIME_4_MINUTES = 4 * 60 * 1000L;
	protected static final long TIME_5_MINUTES = 5 * 60 * 1000L;
	protected static final long TIME_30_MINUTES = 30 * 60 * 1000L;

	private Timer timer;

	private boolean started = false;
	private boolean stopped = false;


	public LoaderService() {
		this.timer = new Timer();
	}


	public void start() {
		if (started || stopped) {
			throw new IllegalStateException();
		}
		started = true;

		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				executeRequest();
			}
		}, 0, getTimeInterval());
	}


	protected abstract void executeRequest();


	protected abstract long getTimeInterval();


	public void stop() {
		if (!started || stopped) {
			throw new IllegalStateException();
		}
		stopped = true;

		timer.cancel();
		timer.purge();
		timer = null;
	}

}
