/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.GenericBot;
import com.floern.genericbot.frame.stackexchange.api.model.Container;
import com.floern.genericbot.frame.stackexchange.api.net.ApiLoader;
import com.floern.genericbot.frame.utils.ChatPrinter;
import com.floern.genericbot.frame.utils.LoaderService;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class ApiLoaderService<T extends Container> extends LoaderService {

	private final GenericBot genericBot;
	private final Class<T> returnType;


	public ApiLoaderService(GenericBot genericBot, Class<T> returnType) {
		this.genericBot = genericBot;
		this.returnType = returnType;
	}


	@Override
	protected long getTimeInterval() {
		return TIME_2_MINUTES;
	}


	protected void timerFired() {
		// noop
	}


	@Override
	protected void executeRequest() {
		timerFired();

		AtomicBoolean hasMore = new AtomicBoolean(false);
		int page = 1;
		do {
			HttpUriRequest request = new HttpGet(getRequestUrl(page));
			new ApiLoader<>(request, returnType)
					.onResult((result) -> {
						hasMore.set(ApiLoaderService.this.onResult(result));
					})
					.onError(e -> {
						hasMore.set(false);
						ApiLoaderService.this.onError(e);
					})
					.load();
			page++;
		} while (hasMore.get());
	}


	protected abstract boolean onResult(T result);


	protected void onError(Exception e) {
		genericBot.getChatManager().getDevChatRoom().send(ChatPrinter.formatException(e));
	}


	protected abstract String getRequestUrl(int page);

}
