package com.finals.net;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.params.HttpParams;


import android.util.Log;

public class NetUtil {

	public static final String TAG = "Finals";

	public static final String HTTPS = "https";

	public static final String HTTP = "http";

	public static final String GET = "GET";

	public static final String POST = "POST";

	public static final String PUT = "PUT";

	public static int READTIMEOUT = 5 * 1000;

	public static int CONNECTTIMEOUT = 5 * 1000;

	/**
	 * 得到URL连接
	 * 
	 * @param url
	 * @param type
	 * @return
	 */
	public static HttpURLConnection GetHttpUrlConnection(String url, String type) {

		// 初始化连接
		HttpURLConnection connection = null;

		// 打开连接
		URL mUrl = OpenUrl(url);
		if (mUrl == null) {
			return connection;
		}

		// 得到对应的连接
		if (url.startsWith(HTTPS)) {
			connection = GetHttpsConnection(mUrl);
		} else if (url.startsWith(HTTP)) {
			connection = GetHttpConnection(mUrl);
		}

		if (connection == null) {
			return connection;
		}

		InitUrlConnection(connection, type);

		return connection;
	}

	/**
	 * 初始化网络连接
	 * 
	 * @param connection
	 */
	private static void InitUrlConnection(HttpURLConnection connection, String type) {
		connection.setConnectTimeout(CONNECTTIMEOUT);
		connection.setReadTimeout(READTIMEOUT);
		try {
			connection.setRequestMethod(type);
		} catch (ProtocolException e) {

		}
		if (type.equals(PUT) || type.equals(POST)) {
			connection.setDoOutput(true);
		}
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");

	}

	/**
	 * 打开网址
	 * 
	 * @param url
	 * @return
	 */
	private static URL OpenUrl(String url) {
		URL mUrl = null;
		try {
			mUrl = new URL(url);
		} catch (MalformedURLException e) {
			Log.e("Finals", "格式错误");
			return mUrl;
		}
		return mUrl;
	}

	/**
	 * 得到https连接
	 * 
	 * @param url
	 */
	private static HttpURLConnection GetHttpsConnection(URL url) {
		HttpURLConnection conn = null;

		HttpsURLConnection sconn;
		try {
			sconn = (HttpsURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e("Finals", "openConnection 出现异常");
			return conn;
		}

		// 去掉证书验证
		NoTrustManager manager = new NoTrustManager();
		TrustManager[] managers = { manager };
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "没有TLS算法");
			return conn;
		}
		try {
			ctx.init(null, managers, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			Log.e(TAG, "秘钥管理出错");
			return conn;
		}
		SSLSocketFactory factory = ctx.getSocketFactory();
		sconn.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		sconn.setSSLSocketFactory(factory);

		conn = sconn;
		return conn;
	}

	/**
	 * 得到http连接
	 * 
	 * @param url
	 * @return
	 */
	private static HttpURLConnection GetHttpConnection(URL url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e(TAG, "出现IO异常");
			return conn;
		}
		return conn;
	}

	public static String getMD5Hex(String str) {
		byte[] data = getMD5(str.getBytes());
		if (data == null) {
			return null;
		}

		BigInteger bi = new BigInteger(data).abs();

		String result = bi.toString(36);
		return result;
	}

	private static byte[] getMD5(byte[] data) {

		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
			byte[] hash = digest.digest();
			return hash;
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "MD5出现异常");
		}
		return null;
	}

	public static void PostExce(String url, String type, InputStream is, OnConnectionOver over) {

		String md5url = getMD5Hex(url);
		if (!NetConnection.linkedHashMap.containsKey(md5url)) {
			NetConnection.linkedHashMap.put(md5url, new NetConnection(url, type, is, over));
			fetchExe.execute(NetConnection.linkedHashMap.get(md5url));

		}
	}

	public static void CancelExce(String url) {
		String md5url = getMD5Hex(url);
		if (NetConnection.linkedHashMap.containsKey(md5url)) {
			NetConnection.linkedHashMap.get(md5url).CannelConnection();
		}
	}

	private static int NETWORK_POOL = 4;
	private static ExecutorService fetchExe = Executors.newFixedThreadPool(NETWORK_POOL);;

}
