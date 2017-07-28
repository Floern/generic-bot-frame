/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.model;

public abstract class Container {

	private boolean has_more;
	private int quota_max;
	private int quota_remaining;
	private int backoff;
	private int page;
	private int page_size;
	private int total;
	private String type;

	private int error_id;
	private String error_message;
	private String error_name;

	public int getBackoff() {
		return backoff;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return page_size;
	}

	public int getTotal() {
		return total;
	}

	public boolean hasMore() {
		return has_more;
	}

	public int getQuotaMax() {
		return quota_max;
	}

	public int getQuotaRemaining() {
		return quota_remaining;
	}

	public String getType() {
		return type;
	}

	public int getErrorId() {
		return error_id;
	}

	public String getErrorMessage() {
		return error_message;
	}

	public String getErrorName() {
		return error_name;
	}

}
