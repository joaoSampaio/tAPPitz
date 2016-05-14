package com.tappitz.app.rest;

import android.content.Context;
import android.util.Log;

//import com.squareup.okhttp.Interceptor;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.tappitz.app.Global;
import com.tappitz.app.app.AppController;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.client.OkClient;
//import retrofit.converter.GsonConverter;
//import okhttp3.ConnectionPool;
//import okhttp3.Dispatcher;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit.client.OkClient;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sampaio on 26-03-2015.
 */
public class RestClientV2 {

    private static String sessionId;

    private static ApiV2 api;

    private static OkHttpClient okHttpClient;

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        RestClientV2.sessionId = sessionId;
    }

    public static ApiV2 getApi() {
        return api;
    }

    private static HostnameVerifier hostNameVerifier = new X509HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            try {
                verifyHost(hostname);
                return true;
            } catch (SSLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            verifyHost(host);
        }

        @Override
        public void verify(String host, X509Certificate cert) throws SSLException {
            verifyHost(host);
        }

        @Override
        public void verify(String host, SSLSocket ssl) throws IOException {
            verifyHost(host);
        }

        private void verifyHost(String sourceHost) throws SSLException {
            if (!"176.111.104.39".equals(sourceHost)) { // THIS IS WHERE YOU AUTHENTICATE YOUR EXPECTED host (IN THIS CASE 192.168.0.56)
                throw new SSLException("Hostname '192.168.0.56' was not verified");
            }
        }
    };



    public static final OkHttpClient getOk() {
        if (okHttpClient == null) {

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-type", "application/json;charset=UTF-8")
                            .addHeader("Accept", "application/json");

                    if (null != sessionId && sessionId.length() > 0) {
                        newRequest.addHeader("Session-Id", sessionId);
                    }
                    Log.d("servico", "COOKIES_REQUEST_INTERCEPTOR");
                    return chain.proceed(newRequest.build());
                }
            };

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .interceptors().add(COOKIES_REQUEST_INTERCEPTOR);
            client.interceptors().add(interceptor);
            client.sslSocketFactory(loadServerCert(AppController.getAppContext()).getSocketFactory());
            client.hostnameVerifier(hostNameVerifier);
            okHttpClient = client.build();

        }

        return okHttpClient;
    }


    public static final ApiV2 getService() {
        if (RestClientV2.getApi() == null) {

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-type", "application/json;charset=UTF-8")
                            .addHeader("Accept", "application/json");

                    if (null != sessionId && sessionId.length() > 0) {
                        newRequest.addHeader("Session-Id", sessionId);
                    }
                    Log.d("servico", "COOKIES_REQUEST_INTERCEPTOR");
                    return chain.proceed(newRequest.build());
                }
            };





            Dispatcher dispatcher=new Dispatcher();
            dispatcher.setMaxRequests(10);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .interceptors().add(COOKIES_REQUEST_INTERCEPTOR);
            client.sslSocketFactory(loadServerCert(AppController.getAppContext()).getSocketFactory());

            client.hostnameVerifier(hostNameVerifier);

            client.interceptors().add(interceptor);
            client.dispatcher(dispatcher);

            client.connectionPool(new ConnectionPool(20, 5 * 60 ,TimeUnit.MINUTES));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Global.ENDPOINT)
                    .client(client.build())

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RestClientV2.api = retrofit.create(ApiV2.class);
        }


        return RestClientV2.getApi();
    }




    private static SSLContext loadServerCert(Context context){

// Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)

// From https://www.washington.edu/itconnect/security/ca/load-der.crt

        Certificate ca;
        InputStream caInput = null;
        try {


            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            } };



            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = context.getAssets().open("tappitz.cer");
//            InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));

            ca = cf.generateCertificate(caInput);
            Log.d("Pedro", "ca=" + ((X509Certificate) ca).getSubjectDN());
//            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());


            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


        // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext;

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                if(caInput != null)
                    caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }


}
