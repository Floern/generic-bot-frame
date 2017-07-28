/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public class User {

	private int reputation;
	private int user_id;
	private String user_type;
	private String display_name;

	public int getReputation() {
		return reputation;
	}

	public int getUserId() {
		return user_id;
	}

	public String getUserType() {
		return user_type;
	}

	public String getDisplayName() {
		return display_name;
	}

}
