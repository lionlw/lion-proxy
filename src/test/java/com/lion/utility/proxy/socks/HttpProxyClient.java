package com.lion.utility.proxy.socks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public class HttpProxyClient {
	public static void main(String[] args) throws Exception {
		final String user = "lion";
		final String password = "111";

		java.net.Authenticator.setDefault(new java.net.Authenticator() {
			private PasswordAuthentication authentication = new PasswordAuthentication(user, password.toCharArray());

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		});

		HttpURLConnection httpConnection = null;

		try {
			URL url = new URL("https://www.baidu.com");

			// 处理绑定ip端口
			// Proxy proxy = new Proxy(Proxy.Type.SOCKS, new
			// InetSocketAddress("172.16.1.46", 8800));
			// Proxy proxy = new Proxy(Proxy.Type.SOCKS, new
			// InetSocketAddress("192.168.2.131", 8800));
			Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("172.16.1.46", 8800));
			// 打开连接
			httpConnection = (HttpURLConnection) url.openConnection(proxy);

			httpConnection.setConnectTimeout(3000);
			httpConnection.setReadTimeout(10000);
			httpConnection.setRequestMethod("GET");

			// 保存http状态码
			int code = httpConnection.getResponseCode();
			if (code == 200) {
				try (InputStream inputStream = httpConnection.getInputStream()) {
					if (inputStream != null) {
						StringBuilder sb = new StringBuilder();

						try (BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
							char[] buf = new char[1024];
							int len;
							while ((len = bf.read(buf)) > 0) {
								sb.append(new String(buf, 0, len));
							}
						}

						System.out.println(sb.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
	}
}
