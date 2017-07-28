/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public abstract class Post {

	private User owner;
	private int score;
	private long creation_date;
	private long last_activity_date;
	private User last_editor;
	private long last_edit_date;
	private String body;

	public User getOwner() {
		return owner;
	}

	public int getScore() {
		return score;
	}

	public long getCreationDate() {
		return creation_date;
	}

	public long getLastActivityDate() {
		return last_activity_date;
	}

	public User getLastEditor() {
		return last_editor;
	}

	public long getLastEditDate() {
		return last_edit_date;
	}

	public String getBody() {
		return body;
	}

	public abstract int getPostId();

}
