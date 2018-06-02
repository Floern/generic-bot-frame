/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LetsEncryptSSLContextUtil {

	public static SSLContext createSslContext() {
		final SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			final TrustManagerFactory javaDefaultTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			javaDefaultTrustManager.init((KeyStore) null);
			final TrustManagerFactory customCaTrustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			customCaTrustManager.init(getKeyStore());

			sslContext.init(
					null,
					new TrustManager[]{
							new X509TrustManagerDelegate(
									(X509TrustManager) customCaTrustManager.getTrustManagers()[0],
									(X509TrustManager) javaDefaultTrustManager.getTrustManagers()[0]
							)
					},
					new SecureRandom()
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sslContext;
	}

	private static KeyStore getKeyStore() throws Exception {
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream is = LetsEncryptSSLContextUtil.class.getResourceAsStream("/certs/letsencrypt-truststore");
		try {
			ks.load(is, "letsencrypt".toCharArray());
		} finally {
			is.close();
		}
		return ks;
	}

}
