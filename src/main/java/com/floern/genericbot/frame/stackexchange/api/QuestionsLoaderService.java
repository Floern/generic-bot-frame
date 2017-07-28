/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.stackexchange.api.model.Question;
import com.floern.genericbot.frame.stackexchange.api.model.QuestionsContainer;
import com.floern.genericbot.frame.chat.ChatManager;

import java.util.HashSet;
import java.util.Set;

public class QuestionsLoaderService extends ApiLoaderService<QuestionsContainer> {

	/** unix timestamp of most recent known post */
	private long newestPostUnixTimestamp = 0;

	/** unix timestamp of newest post from previous scheduled run */
	private long newestPostFromPrevTimer;

	private Set<OnQuestionLoadedListener> onQuestionLoadedListeners = new HashSet<>();


	public QuestionsLoaderService(ChatManager chatManager) {
		super(chatManager, QuestionsContainer.class);
		newestPostUnixTimestamp = (System.currentTimeMillis() - getTimeInterval()) / 1000;
	}


	public void addOnQuestionLoadedListener(OnQuestionLoadedListener onQuestionLoadedListener) {
		onQuestionLoadedListeners.add(onQuestionLoadedListener);
	}


	@Override
	protected long getTimeInterval() {
		return TIME_2_MINUTES;
	}


	@Override
	protected void timerFired() {
		newestPostFromPrevTimer = newestPostUnixTimestamp;
	}


	@Override
	protected boolean onResult(QuestionsContainer result) {
		for (Question question : result.getItems()) {
			long lastActive = Math.max(question.getCreationDate(), question.getLastActivityDate());
			newestPostUnixTimestamp = Math.max(lastActive, newestPostUnixTimestamp);
			if (newestPostFromPrevTimer >= lastActive) {
				// we got all new/updated posts
				return false;
			}
			if (question.getLastActivityDate() == question.getCreationDate() ||
					question.getLastActivityDate() == question.getLastEditDate()) {
				// invoke listeners
				for (OnQuestionLoadedListener listener : onQuestionLoadedListeners) {
					listener.onQuestionLoaded(question);
				}
			}
		}
		return true;
	}


	@Override
	protected String getRequestUrl(int page) {
		return Requests.questions(page);
	}


	public interface OnQuestionLoadedListener {
		void onQuestionLoaded(Question question);
	}

}
