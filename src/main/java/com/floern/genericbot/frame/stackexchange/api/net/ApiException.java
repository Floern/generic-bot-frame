/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.net;

import com.floern.genericbot.frame.stackexchange.api.model.Container;

public class ApiException extends Exception {

	private int errorId;
	private String errorName;
	private String errorMessage;

	public ApiException(Container container) {
		this(container, null);
	}

	public ApiException(Container container, Throwable cause) {
		super("API Error " + container.getErrorId() + " " + container.getErrorName() + ": " + container.getErrorMessage(), cause);
		errorId = container.getErrorId();
		errorName = container.getErrorName();
		errorMessage = container.getErrorMessage();
	}

	public int getErrorId() {
		return errorId;
	}

	public String getErrorName() {
		return errorName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static int getErrorId(Exception e) {
		if (e instanceof ApiException) {
			return ((ApiException)e).getErrorId();
		}
		return 0;
	}

}
