/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

//blog.novoj.net/2016/02/29/how-to-make-apache-httpclient-trust-lets-encrypt-certificate-authority/
public class X509TrustManagerDelegate implements X509TrustManager {

	private final X509TrustManager mainTrustManager;
	private final X509TrustManager fallbackTrustManager;

	public X509TrustManagerDelegate(X509TrustManager mainTrustManager, X509TrustManager fallbackTrustManager) {
		this.mainTrustManager = mainTrustManager;
		this.fallbackTrustManager = fallbackTrustManager;
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
		try {
			mainTrustManager.checkClientTrusted(x509Certificates, authType);
		} catch (CertificateException ignored) {
			this.fallbackTrustManager.checkClientTrusted(x509Certificates, authType);
		}
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
		try {
			mainTrustManager.checkServerTrusted(x509Certificates, authType);
		} catch (CertificateException ignored) {
			this.fallbackTrustManager.checkServerTrusted(x509Certificates, authType);
		}
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return this.fallbackTrustManager.getAcceptedIssuers();
	}

}
