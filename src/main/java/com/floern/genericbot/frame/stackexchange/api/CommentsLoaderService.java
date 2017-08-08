/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.GenericBot;
import com.floern.genericbot.frame.stackexchange.api.model.Comment;
import com.floern.genericbot.frame.stackexchange.api.model.CommentsContainer;

import java.util.HashSet;
import java.util.Set;

public class CommentsLoaderService extends ApiLoaderService<CommentsContainer> {

	/** unix timestamp of most recent known comment */
	private long newestPostUnixTimestamp = 0;

	/** unix timestamp of newest comment from previous scheduled run */
	private long newestPostFromPrevTimer;

	private Set<OnCommentLoadedListener> onCommentLoadedListeners = new HashSet<>();


	public CommentsLoaderService(GenericBot genericBot) {
		super(genericBot, CommentsContainer.class);
		newestPostUnixTimestamp = (System.currentTimeMillis() - getTimeInterval()) / 1000;
	}


	public void addOnCommentLoadedListener(OnCommentLoadedListener onAnswerLoadedListener) {
		onCommentLoadedListeners.add(onAnswerLoadedListener);
	}


	@Override
	protected long getTimeInterval() {
		return TIME_4_MINUTES;
	}


	@Override
	protected void timerFired() {
		newestPostFromPrevTimer = newestPostUnixTimestamp;
	}


	@Override
	protected boolean onResult(CommentsContainer result) {
		for (Comment comment : result.getItems()) {
			newestPostUnixTimestamp = Math.max(comment.getCreationDate(), newestPostUnixTimestamp);
			if (newestPostFromPrevTimer >= comment.getCreationDate()) {
				// we got all new/updated posts
				return false;
			}
			// invoke listeners
			for (OnCommentLoadedListener listener : onCommentLoadedListeners) {
				listener.onCommentLoaded(comment);
			}
		}
		return true;
	}


	@Override
	protected String getRequestUrl(int page) {
		return Requests.comments(page);
	}


	public interface OnCommentLoadedListener {
		void onCommentLoaded(Comment comment);
	}

}
