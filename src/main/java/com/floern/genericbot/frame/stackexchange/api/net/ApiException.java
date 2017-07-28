/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.stackexchange.api.net;

import com.floern.genericbot.frame.stackexchange.api.model.Container;

public class ApiException extends Exception {

	public ApiException(Container container) {
		this(container, null);
	}

	public ApiException(Container container, Throwable cause) {
		super("API Error " + container.getErrorId() + " " + container.getErrorName() + ": " + container.getErrorMessage(), cause);
	}

}
