/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.redunda;

import com.floern.genericbot.frame.net.GsonLoader;
import com.floern.genericbot.frame.net.HttpPostUrlEncoded;
import com.floern.genericbot.frame.redunda.model.Status;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedundaService {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedundaService.class);

	private static final String REDUNDA_API_URL = "https://redunda.sobotics.org/status.json";

	private String apikey;

	private ScheduledExecutorService executorService;

	private OnStandbyStatusChangedListener standbyStatusChangedListener;

	private int checkInterval = 30;
	private TimeUnit checkIntervalUnit = TimeUnit.SECONDS;

	private Status lastKnownStatus;

	private boolean failedOver = false;


	/**
	 * Create a Resunda service instance.
	 * @param apikey Redunda API key
	 */
	public RedundaService(String apikey) {
		this.apikey = apikey;
	}


	public RedundaService setStandbyStatusChangedListener(OnStandbyStatusChangedListener standbyStatusChangedListener) {
		this.standbyStatusChangedListener = standbyStatusChangedListener;
		return this;
	}


	public RedundaService setCheckInterval(int interval, TimeUnit unit) {
		checkInterval = interval;
		checkIntervalUnit = unit;
		return this;
	}


	public void start() {
		if (executorService != null) {
			throw new IllegalStateException();
		}
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this::execute, 0, checkInterval, checkIntervalUnit);
		LOGGER.info("RedundaService started");
	}


	private void execute() {
		try {
			HttpPost request = new HttpPostUrlEncoded(REDUNDA_API_URL, "key", apikey);

			new GsonLoader<>(request, Status.class)
					.onResult(newStatus -> {
						LOGGER.info(newStatus.getLocation() + " standby: " + Boolean.toString(newStatus.shouldStandby()));
						if (newStatus.shouldStandby()) {
							failedOver = false;
						}
						else if (!newStatus.shouldStandby() && lastKnownStatus != null && lastKnownStatus.shouldStandby()) {
							failedOver = true;
						}
						if (standbyStatusChangedListener != null) {
							if ((lastKnownStatus != null && lastKnownStatus.shouldStandby() != newStatus.shouldStandby())
									|| (lastKnownStatus == null && !newStatus.shouldStandby())) {
								standbyStatusChangedListener.onStandByStatusChanged(newStatus.shouldStandby());
							}
						}
						lastKnownStatus = newStatus;
					})
					.onError(Throwable::printStackTrace)
					.load();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Get the location that was set by Redunda for this instance.
	 * @return
	 */
	public String getStatusLocation() {
		return lastKnownStatus == null ? null : lastKnownStatus.getLocation();
	}


	/**
	 * Whether we are on standby.
	 */
	public boolean getStatusStandby() {
		return lastKnownStatus == null || lastKnownStatus.shouldStandby();
	}


	/**
	 * Whether we're running after a failover signal.
	 */
	public boolean failedOver() {
		return failedOver;
	}


	public void stop() {
		executorService.shutdown();
		executorService = null;
		LOGGER.info("RedundaService stopped");
	}


	/**
	 * Start the Redunda service and block until we are not on standby.
	 * @param apikey
	 * @param standbyStatusChangedListener
	 * @return
	 */
	public static RedundaService startAndWaitForGo(String apikey, OnStandbyStatusChangedListener standbyStatusChangedListener) {
		CountDownLatch redundaHold = new CountDownLatch(1);
		RedundaService redundaService = new RedundaService(apikey);
		redundaService.setStandbyStatusChangedListener(standby -> {
			standbyStatusChangedListener.onStandByStatusChanged(standby);
			if (standby) {
				LOGGER.info("STANDBY MODE ACTIVATED");
			}
			else {
				LOGGER.info("STANDBY MODE DISABLED");
				redundaHold.countDown();
			}
		});
		redundaService.start();
		try {
			redundaHold.await();
		}
		catch (InterruptedException e) {
			LOGGER.error("redunda hold await", e);
			throw new RuntimeException(e);
		}

		return redundaService;
	}



	public interface OnStandbyStatusChangedListener {
		void onStandByStatusChanged(boolean standby);
	}


}
