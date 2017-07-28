/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpPostUrlEncoded extends HttpPost {

	public HttpPostUrlEncoded(String url, String... data) {
		this(url, IntStream.range(1, data.length)
				.filter(i -> (i & 1) == 1)
				.mapToObj(i -> new BasicNameValuePair(data[i - 1], data[i]))
				.collect(Collectors.toList()));
	}

	public HttpPostUrlEncoded(String url, List<NameValuePair> data) {
		super(url);

		try {
			StringEntity dataEntity = new UrlEncodedFormEntity(data);
			setEntity(dataEntity);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
