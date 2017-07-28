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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedundaService {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedundaService.class);

	private static final String REDUNDA_API_URL = "https://redunda.sobotics.org/status.json";

	private String apikey;

	private ScheduledExecutorService executorService;

	private OnStandBystatusChangedListener standbyStatusChangedListener;

	private int checkInterval = 30;
	private TimeUnit checkIntervalUnit = TimeUnit.SECONDS;

	private Status lastKnownStatus;


	/**
	 * Create a Resunda service instance.
	 * @param apikey Redunda API key
	 */
	public RedundaService(String apikey) {
		this.apikey = apikey;
	}


	public RedundaService setStandbyStatusChangedListener(OnStandBystatusChangedListener standbyStatusChangedListener) {
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


	public String getStatusLocation() {
		return lastKnownStatus == null ? null : lastKnownStatus.getLocation();
	}


	public boolean getStatusStandby() {
		return lastKnownStatus == null || lastKnownStatus.shouldStandby();
	}


	public void stop() {
		executorService.shutdown();
		executorService = null;
		LOGGER.info("RedundaService stopped");
	}


	public interface OnStandBystatusChangedListener {
		void onStandByStatusChanged(boolean standby);
	}


}
