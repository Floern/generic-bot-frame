/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import com.google.gson.Gson;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.InputStream;
import java.io.InputStreamReader;

public class GsonLoader<T> extends HttpLoader<T> {

	public GsonLoader(HttpUriRequest request, Class<T> returnType) {
		super(request, returnType);
		request.setHeader(HttpHeaders.ACCEPT, "application/json");
	}


	@Override
	protected T parseResult(InputStream is) {
		return new Gson().fromJson(new InputStreamReader(is), getReturnType());
	}

}
