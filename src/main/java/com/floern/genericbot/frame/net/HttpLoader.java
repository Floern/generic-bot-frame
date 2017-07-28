/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class HttpLoader<T> {

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpLoader.class);

	private final HttpUriRequest request;
	private HttpContext httpContext;

	private final Class<T> returnType;

	private HttpLoader.OnResultCallback<T> onResultCallback;
	private HttpLoader.OnErrorCallback onErrorCallback;


	public HttpLoader(HttpUriRequest request, Class<T> returnType) {
		this.request = request;
		this.returnType = returnType;
	}


	public HttpLoader<T> setHttpContext(HttpContext context) {
		this.httpContext = context;
		return this;
	}


	public HttpLoader<T> onResult(HttpLoader.OnResultCallback<T> onResultCallback) {
		this.onResultCallback = onResultCallback;
		return this;
	}


	public HttpLoader<T> onError(HttpLoader.OnErrorCallback onErrorCallback) {
		this.onErrorCallback = onErrorCallback;
		return this;
	}


	public void load() {
		LOGGER.info(request.getMethod() + " " + request.getURI().toASCIIString());

		CloseableHttpResponse response = null;
		try {
			// execute request
			response = HttpClientSingleton.getClient().execute(request, getHttpContext());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// handle gzip compression
				Header contentEncodingHeader = entity.getContentEncoding();
				if (contentEncodingHeader != null) {
					HeaderElement[] encodings = contentEncodingHeader.getElements();
					for (HeaderElement encoding : encodings) {
						if (encoding.getName().equalsIgnoreCase("gzip")) {
							entity = new GzipDecompressingEntity(entity);
							break;
						}
					}
				}

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					throw new StatusCodeException(statusCode, response.getStatusLine().getReasonPhrase(), CharStreams
							.toString(new InputStreamReader(entity.getContent(), Charsets.UTF_8)));
				}

				T result;
				try (InputStream is = entity.getContent()) {
					// read the result
					result = parseResult(is);
				}

				// handle result
				if (onResultCallback != null) {
					onResultCallback.onResult(result);
				}
			}
			else {
				throw new IOException("HttpResponse has no HttpEntity");
			}
		}
		catch (Exception e) {
			// handle request error
			LOGGER.warn("request failed", e);
			if (onErrorCallback != null) {
				onErrorCallback.onError(e);
			}
			else {
				throw new RuntimeException(e);
			}
		}
		finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					LOGGER.debug("response close failed", e);
				}
			}
		}
	}


	protected HttpContext getHttpContext() {
		return httpContext;
	}


	protected abstract T parseResult(InputStream is) throws Exception;


	public Class<T> getReturnType() {
		return returnType;
	}


	public HttpUriRequest getRequest() {
		return request;
	}


	public static void cancelAll() {
		HttpLoader.HttpClientSingleton.stopClient();
	}


	public interface OnResultCallback<R> {
		void onResult(R response) throws Exception;
	}


	public interface OnErrorCallback {
		void onError(Exception exception);
	}


	private static class HttpClientSingleton {

		private static CloseableHttpClient httpClient;

		private static synchronized CloseableHttpClient getClient() {
			if (httpClient == null) {
				httpClient = HttpClientBuilder.create()
						.setRedirectStrategy(new LaxRedirectStrategy())
						.setDefaultRequestConfig(RequestConfig.custom()
								.setSocketTimeout(12345)
								.setConnectTimeout(12345)
								.build())
						.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/53.0 GenericBot")
						.setSSLSocketFactory(new SSLConnectionSocketFactory(LetsEncryptSSLContextUtil.createSslContext()))
						.setDefaultHeaders(Lists.newArrayList(
								new BasicHeader(HttpHeaders.ACCEPT, "*/*"),
								new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"),
								new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip")
						))
						.build();
			}
			return httpClient;
		}

		private static synchronized void stopClient() {
			if (httpClient != null) {
				try {
					httpClient.close();
				}
				catch (IOException e) {
					LOGGER.debug("http client close failed", e);
				}
				httpClient = null;
			}
		}

	}


	public static class StatusCodeException extends IOException {

		private final int statusCode;
		private final String statusLine;
		private final String body;

		public StatusCodeException(int statusCode, String statusLine, String body) {
			super("HTTP Status Code " + statusCode + ": " + statusLine + " -- " +
					body.substring(0, Math.min(body.length(), 200)).replaceAll("\\s+", " ").trim());
			this.statusCode = statusCode;
			this.statusLine = statusLine;
			this.body = body;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public String getStatusLine() {
			return statusLine;
		}

		public String getBody() {
			return body;
		}

	}

}
