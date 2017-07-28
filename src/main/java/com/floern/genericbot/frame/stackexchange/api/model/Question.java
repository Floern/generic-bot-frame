/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public class Question extends Post {

	private int question_id;

	public int getQuestionId() {
		return question_id;
	}

	@Override
	public int getPostId() {
		return getQuestionId();
	}

}
