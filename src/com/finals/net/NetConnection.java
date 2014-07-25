package com.finals.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;


public class NetConnection implements Runnable {

	boolean isCannel = false;

	HttpURLConnection conn = null;

	String url;
	String type;
	InputStream is;
	OnConnectionOver over;

	public NetConnection(String url, String type, InputStream is, OnConnectionOver over) {
		this.url = url;
		this.type = type;
		this.is = is;
		this.over = over;
	}

	public void startConnection() {
		conn = NetUtil.GetHttpUrlConnection(url, type);
		String result = null;
		if (conn != null) {

			// 持有连接
			try {
				conn.connect();
			} catch (IOException e) {

			}

			if (!type.equals(NetUtil.GET)) {
				UploadData();
			}
			if (conn != null) {
				result = downloadData();
				conn.disconnect();
			}

		}
		over.ConnectionOver(result, isCannel);
	}

	private String downloadData() {
		String result = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = null;
		byte[] buffer = new byte[4096];

		try {
			is = conn.getInputStream();

			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}

			is.close();
			is = null;

			bos.close();
			result = bos.toString();
			bos = null;
		} catch (Exception e) {
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
				is = null;
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {

				}
				bos = null;
			}
		}
		return result;
	}

	private void UploadData() {
		OutputStream os = null;
		byte[] buffer = new byte[4096];
		try {
			os = conn.getOutputStream();
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}

			os.close();
			os = null;

			is.close();
			is = null;

		} catch (IOException e) {
			return;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {

				}
				os = null;
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {

				}
				is = null;
			}
		}
	}

	public void CannelConnection() {
		isCannel = true;
		if (conn != null) {
			conn.disconnect();
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
		linkedHashMap.remove(NetUtil.getMD5Hex(url));
	}

	@Override
	public void run() {
		//启动连接
		startConnection();
		
		synchronized (linkedHashMap) {
			linkedHashMap.remove(NetUtil.getMD5Hex(url));
		}
	}

	static LinkedHashMap<String, NetConnection> linkedHashMap = new LinkedHashMap<String, NetConnection>();

}
