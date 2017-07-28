/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public class Answer extends Post {

	private int answer_id;

	public int getAnswerId() {
		return answer_id;
	}

	@Override
	public int getPostId() {
		return getAnswerId();
	}

}
