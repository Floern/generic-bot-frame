/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api;

import com.floern.genericbot.frame.GenericBot;
import com.floern.genericbot.frame.stackexchange.api.model.SuggestedEdit;
import com.floern.genericbot.frame.stackexchange.api.model.SuggestedEditsContainer;

import java.util.HashSet;
import java.util.Set;

public class SuggestedEditsLoaderService extends ApiLoaderService<SuggestedEditsContainer> {

	/** unix timestamp of most recent known post */
	private long newestEditUnixTimestamp = 0;

	/** unix timestamp of newest post from previous scheduled run */
	private long newestPostFromPrevTimer;

	private Set<OnSuggestedEditLoadedListener> onSuggestedEditLoadedListeners = new HashSet<>();


	public SuggestedEditsLoaderService(GenericBot genericBot) {
		super(genericBot, SuggestedEditsContainer.class);
		newestEditUnixTimestamp = (System.currentTimeMillis() - getTimeInterval()) / 1000;
	}


	public void addOnPostLoadedListener(OnSuggestedEditLoadedListener onPostLoadedListener) {
		onSuggestedEditLoadedListeners.add(onPostLoadedListener);
	}


	@Override
	protected long getTimeInterval() {
		return TIME_4_MINUTES;
	}


	@Override
	protected void timerFired() {
		newestPostFromPrevTimer = newestEditUnixTimestamp;
	}


	@Override
	protected boolean onResult(SuggestedEditsContainer result) {
		for (SuggestedEdit suggestedEdit : result.getItems()) {
			long lastActive = suggestedEdit.getCreationDate();
			newestEditUnixTimestamp = Math.max(lastActive, newestEditUnixTimestamp);
			if (newestPostFromPrevTimer >= lastActive) {
				// we got all new/updated posts
				return false;
			}
			// invoke listeners
			for (OnSuggestedEditLoadedListener listener : onSuggestedEditLoadedListeners) {
				listener.onSuggestedEditLoaded(suggestedEdit);
			}
		}
		return true;
	}


	@Override
	protected String getRequestUrl(int page) {
		return Requests.suggestedEdits(page);
	}


	public interface OnSuggestedEditLoadedListener {
		void onSuggestedEditLoaded(SuggestedEdit suggestedEdit);
	}

}
