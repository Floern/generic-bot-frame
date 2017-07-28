/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class HttpPostGson extends HttpPost {

	public HttpPostGson(String url, Object data) {
		super(url);
		String json = new Gson().toJson(data);
		StringEntity dataEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		setEntity(dataEntity);
	}

}
