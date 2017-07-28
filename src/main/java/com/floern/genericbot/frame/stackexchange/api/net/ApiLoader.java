/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.net;

import com.floern.genericbot.frame.net.GsonLoader;
import com.floern.genericbot.frame.net.HttpLoader;
import com.floern.genericbot.frame.stackexchange.api.model.Container;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiLoader<T extends Container> extends GsonLoader<T> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiLoader.class);

	/** Current SE API quota remaining */
	private static int quotaRemaining = -1;

	/** SE API quota maximum */
	private static int quotaMax = -1;

	private long nextRequestEarliestTime = 0;


	public ApiLoader(HttpUriRequest request, Class<T> returnType) {
		super(request, returnType);
	}


	@Override
	public ApiLoader onResult(OnResultCallback<T> onResultCallback) {
		super.onResult(response -> {
			// adjust backoff if necessary
			nextRequestEarliestTime = System.currentTimeMillis() + Math.max(1, Math.min(response.getBackoff() + 1, 120)) * 1000L;

			// handle a potential API error
			if (response.getErrorId() != 0) {
				throw new ApiException(response);
			}

			// update API quota info
			if (response.getQuotaMax() > 0) {
				quotaMax = response.getQuotaMax();
				quotaRemaining = response.getQuotaRemaining();
				LOGGER.info("API Quota: " + quotaRemaining + " / " + quotaMax);
			}

			onResultCallback.onResult(response);
		});
		return this;
	}


	@Override
	public HttpLoader onError(OnErrorCallback onErrorCallback) {
		return super.onError(exception -> {
			if (exception instanceof StatusCodeException) {
				try {
					StatusCodeException sce = (StatusCodeException) exception;
					T response = new Gson().fromJson(sce.getBody(), getReturnType());
					if (response.getErrorId() != 0) {
						exception = new ApiException(response, sce);
					}
				}
				catch (JsonParseException jpe) {
					// ignore
				}
			}

			onErrorCallback.onError(exception);
		});
	}


	@Override
	public void load() {
		synchronized (ApiLoader.class) {
			// handle API request backoff
			long remainingBackoff = nextRequestEarliestTime - System.currentTimeMillis();
			if (remainingBackoff > 0) {
				try {
					Thread.sleep(remainingBackoff);
				}
				catch (InterruptedException e) {
					LOGGER.debug("backoff sleep interrupted", e);
				}
			}

			super.load();
		}
	}


	public static int getQuotaRemaining() {
		return quotaRemaining;
	}


	public static int getQuotaMax() {
		return quotaMax;
	}

}
