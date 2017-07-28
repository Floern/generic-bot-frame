/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.stackexchange.api.model.Answer;
import com.floern.genericbot.frame.stackexchange.api.model.AnswersContainer;

import java.util.HashSet;
import java.util.Set;

public class AnswersLoaderService extends ApiLoaderService<AnswersContainer> {

	/** unix timestamp of most recent known post */
	private long newestPostUnixTimestamp = 0;

	/** unix timestamp of newest post from previous scheduled run */
	private long newestPostFromPrevTimer;

	private Set<OnAnswerLoadedListener> onAnswerLoadedListeners = new HashSet<>();


	public AnswersLoaderService(ChatManager chatManager) {
		super(chatManager, AnswersContainer.class);
		newestPostUnixTimestamp = (System.currentTimeMillis() - getTimeInterval()) / 1000;
	}


	public void addOnAnswerLoadedListener(OnAnswerLoadedListener onAnswerLoadedListener) {
		onAnswerLoadedListeners.add(onAnswerLoadedListener);
	}


	@Override
	protected void timerFired() {
		newestPostFromPrevTimer = newestPostUnixTimestamp;
	}


	@Override
	protected boolean onResult(AnswersContainer result) {
		for (Answer answer : result.getItems()) {
			long lastActive = Math.max(answer.getCreationDate(), answer.getLastActivityDate());
			newestPostUnixTimestamp = Math.max(lastActive, newestPostUnixTimestamp);
			if (newestPostFromPrevTimer >= lastActive) {
				// we got all new/updated posts
				return false;
			}
			// invoke listeners
			for (OnAnswerLoadedListener listener : onAnswerLoadedListeners) {
				listener.onAnswerLoaded(answer);
			}
		}
		return true;
	}


	@Override
	protected String getRequestUrl(int page) {
		return Requests.answers(page);
	}


	public interface OnAnswerLoadedListener {
		void onAnswerLoaded(Answer answer);
	}

}
