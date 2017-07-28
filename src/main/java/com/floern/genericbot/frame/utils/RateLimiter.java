/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.utils;

import java.util.concurrent.TimeUnit;

public class RateLimiter {

	private final long[] values;
	private final long timeIntervalMillis;

	private int newestIndex = -1;

	private long lastSentNotice = 0;


	/**
	 * @param maxRequestCount maximum number of requests allowed within the given time interval.
	 * @param timeInterval
	 * @param timeUnit
	 * @param addRequestNow true to register the first request already.
	 */
	public RateLimiter(int maxRequestCount, int timeInterval, TimeUnit timeUnit, boolean addRequestNow) {
		values = new long[maxRequestCount];
		timeIntervalMillis = timeUnit.toMillis(timeInterval);

		if (addRequestNow) {
			addNewRequestNow();
		}
	}


	/**
	 * Register another request.
	 */
	public void addNewRequestNow() {
		newestIndex = (newestIndex + 1) % values.length;
		values[newestIndex] = System.currentTimeMillis();
	}


	/**
	 * Check if we got hit by rate limit.
	 * @return
	 */
	public boolean isBlocked() {
		int oldestIndex = (newestIndex + 1) % values.length;
		return values[newestIndex] - values[oldestIndex] < timeIntervalMillis;
	}


	/**
	 * If blocked, determince whether to send a rate limit notice.
	 * @return tru if a notice should be sent.
	 */
	public boolean sendNotice() {
		if (lastSentNotice < System.currentTimeMillis() - timeIntervalMillis) {
			lastSentNotice = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
