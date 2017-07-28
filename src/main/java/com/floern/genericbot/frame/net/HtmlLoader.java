/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import com.google.common.io.CharStreams;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HtmlLoader extends HttpLoader<String> {

	public HtmlLoader(HttpUriRequest request) {
		super(request, String.class);
	}


	@Override
	protected String parseResult(InputStream is) throws IOException {
		return CharStreams.toString(new InputStreamReader(is, "UTF-8"));
	}

}
