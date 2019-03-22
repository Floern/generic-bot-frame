/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.GenericBot;
import com.floern.genericbot.frame.stackexchange.api.model.Post;
import com.floern.genericbot.frame.stackexchange.api.model.PostsContainer;

import java.util.LinkedHashSet;
import java.util.Set;

public class PostLoaderService extends ApiLoaderService<PostsContainer> {

	/** unix timestamp of most recent known post */
	private long newestPostUnixTimestamp = 0;

	/** unix timestamp of newest post from previous scheduled run */
	private long newestPostFromPrevTimer;

	private Set<PostLoaderService.OnQuestionLoadedListener> onQuestionLoadedListeners = new LinkedHashSet<>();
	private Set<PostLoaderService.OnAnswerLoadedListener> onAnswerLoadedListeners = new LinkedHashSet<>();


	public PostLoaderService(GenericBot genericBot) {
		super(genericBot, PostsContainer.class);
		newestPostUnixTimestamp = (System.currentTimeMillis() - getTimeInterval()) / 1000;
	}


	public void addOnQuestionLoadedListener(PostLoaderService.OnQuestionLoadedListener onQuestionLoadedListener) {
		onQuestionLoadedListeners.add(onQuestionLoadedListener);
	}


	public void addOnAnswerLoadedListener(PostLoaderService.OnAnswerLoadedListener onAnswerLoadedListener) {
		onAnswerLoadedListeners.add(onAnswerLoadedListener);
	}


	@Override
	protected long getTimeInterval() {
		return TIME_1_MINUTE;
	}


	@Override
	protected void timerFired() {
		newestPostFromPrevTimer = newestPostUnixTimestamp;
	}


	@Override
	protected boolean onResult(PostsContainer result) {
		for (Post post : result.getItems()) {
			long lastActive = Math.max(post.getCreationDate(), post.getLastActivityDate());
			newestPostUnixTimestamp = Math.max(lastActive, newestPostUnixTimestamp);
			if (newestPostFromPrevTimer >= lastActive) {
				// we got all new/updated posts
				return false;
			}
			if (post.getLastActivityDate() == post.getCreationDate() ||
					post.getLastActivityDate() == post.getLastEditDate()) {
				System.out.println(post.getPostType() + " #" + post.getPostId());
				// ignore activity on a question that got a new answer or comment
				if (Post.TYPE_QUESTION.equals(post.getPostType())) {
					// invoke listeners
					for (PostLoaderService.OnQuestionLoadedListener listener : onQuestionLoadedListeners) {
						listener.onQuestionLoaded(post);
					}
				}
				else if (Post.TYPE_ANSWER.equals(post.getPostType())) {
					// invoke listeners
					for (PostLoaderService.OnAnswerLoadedListener listener : onAnswerLoadedListeners) {
						listener.onAnswerLoaded(post);
					}
				}
			}
		}
		return true;
	}


	@Override
	protected String getRequestUrl(int page) {
		return Requests.posts(page);
	}


	public interface OnQuestionLoadedListener {
		void onQuestionLoaded(Post question);
	}


	public interface OnAnswerLoadedListener {
		void onAnswerLoaded(Post question);
	}
}
