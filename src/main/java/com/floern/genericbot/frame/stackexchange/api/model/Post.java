/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public class Post {

	public static final String TYPE_QUESTION = "question";
	public static final String TYPE_ANSWER = "answer";

	private long post_id;
	private String post_type;
	private User owner;
	private int score;
	private long creation_date;
	private long last_activity_date;
	private User last_editor;
	private long last_edit_date;
	private String body;

	public long getPostId() {
		return post_id;
	}

	public String getPostType() {
		return post_type;
	}

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

}
