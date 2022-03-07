package com.ex.master.util;

import android.content.Context;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class SSLConnect {
    public static String TAG = "SSLConnect";

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - don't check for any certificate
     */
    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[Exception] SSLConnect >> trustAllHosts() 1");
        } catch (KeyManagementException e) {
            System.out.println("[Exception] SSLConnect >> trustAllHosts() 2");
        }

    }

    public static HttpsURLConnection postHttps(String url, int connTimeout, int readTimeout) {
        trustAllHosts();

        HttpsURLConnection https = null;
        try {
            https = (HttpsURLConnection) new URL(url).openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            https.setConnectTimeout(connTimeout);
            https.setReadTimeout(readTimeout);
        }
        catch (MalformedURLException e) {
            System.out.println("[Exception] SSLConnect >> postHttps() 1");
            return null;
        }
        catch (IOException e) {
            System.out.println("[Exception] SSLConnect >> postHttps() 2");
            return null;
        }
        return https;
    }

    public static void trustAllHosts2(final Context context) {
        LogUtil.d("SSLConnect", "***** trustAllHosts2()");
        final X509HostnameVerifier verifier = org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                /*if ((chain != null) && (chain.length == 1)) {
                    X509Certificate certificate = chain[0];
                    certificate.checkValidity();
                    String serverURL = "serv url";
                    try {
                        verifier.verify(serverURL, certificate);
                    } catch (SSLException e) {
                        e.printStackTrace();
                        throw new CertificateException(e.getMessage());
                    }
                }*/
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)  {
            }

        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[Exception] SSLConnect >> trustAllHosts2() 1");
        } catch (KeyManagementException e) {
            System.out.println("[Exception] SSLConnect >> trustAllHosts2() 2");
        }
    }
}


