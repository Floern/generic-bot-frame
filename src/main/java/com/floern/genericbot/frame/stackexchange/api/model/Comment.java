/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public class Comment {

	private User owner;
	private long creation_date;
	private long post_id;
	private String post_type;
	private long comment_id;

	public User getOwner() {
		return owner;
	}

	public long getCreationDate() {
		return creation_date;
	}

	public long getPostId() {
		return post_id;
	}

	public long getCommentId() {
		return comment_id;
	}

	public String getPostType() {
		return post_type;
	}

}
