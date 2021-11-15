package com.fronde.server.utils;

import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public class SSLUtils {

  public static void trustAllCertificates() throws GeneralSecurityException {

    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509ExtendedTrustManager() {
          @Override
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
              String authType) {
          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
              String authType) {
          }

          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string,
              Socket socket) throws CertificateException {

          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string,
              Socket socket) throws CertificateException {

          }

          @Override
          public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string,
              SSLEngine ssle) throws CertificateException {

          }

          @Override
          public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string,
              SSLEngine ssle) throws CertificateException {

          }

        }
    };

    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

  }
}
