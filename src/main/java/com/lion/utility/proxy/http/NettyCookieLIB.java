package com.lion.utility.proxy.http;

import java.util.Set;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import com.lion.utility.tool.common.Tool;

/**
 * netty cookie处理类
 * 
 * @author lion
 */
class NettyCookieLIB {
	private NettyCookieLIB() {

	}

	/**
	 * 获取cookie值（获取的为当前domain、当前path的值）
	 * 
	 * @param request
	 *            request对象
	 * @param name
	 *            名
	 * @return cookie值
	 */
	public static String getCookieValue(FullHttpRequest request, String name) {
		Cookie cookie = NettyCookieLIB.getCookie(request, name);
		if (cookie != null) {
			return cookie.value();
		}

		return "";
	}

	/**
	 * 添加cookie
	 * 
	 * @param response
	 *            response对象
	 * @param name
	 *            名
	 * @param value
	 *            值
	 * @param maxAgeSecond
	 *            存活时间（单位：秒）
	 * @param domain
	 *            域名
	 * @param path
	 *            路径
	 */
	public static void addCookie(FullHttpResponse response, String name, String value, long maxAgeSecond, String domain, String path) {
		DefaultCookie cookie = new DefaultCookie(name, value);
		cookie.setMaxAge(maxAgeSecond);
		cookie.setDomain(domain);
		cookie.setPath(path);
		response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
	}

	/**
	 * 更新cookie（处理的为当前domain、当前path的值）
	 * 
	 * @param request
	 *            request对象
	 * @param response
	 *            response对象
	 * @param name
	 *            名
	 * @param value
	 *            值
	 * @param maxAgeSecond
	 *            存活时间（单位：秒）
	 */
	public static void updateCookie(FullHttpRequest request, FullHttpResponse response, String name, String value, int maxAgeSecond) {
		Cookie cookie = NettyCookieLIB.getCookie(request, name);
		if (cookie != null) {
			cookie.setValue(value);
			cookie.setMaxAge(maxAgeSecond);
			response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie)); // 覆盖同名cookie
		}
	}

	/**
	 * 删除cookie
	 * 
	 * @param request
	 *            request对象
	 * @param response
	 *            response对象
	 * @param name
	 *            名
	 */
	public static void delCookie(FullHttpRequest request, FullHttpResponse response, String name) {
		Cookie cookie = NettyCookieLIB.getCookie(request, name);
		if (cookie != null) {
			cookie.setValue(null);
			cookie.setMaxAge(0);
			response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie)); // 覆盖同名cookie
		}
	}

	/**
	 * 获取cookie（获取的为当前domain、当前path的cookie）
	 * 
	 * @param request
	 *            request对象
	 * @param name
	 *            名
	 * @return cookie
	 */
	public static Cookie getCookie(FullHttpRequest request, String name) {
		String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
		if (Tool.checkHaveValue(cookieString)) {
			Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieString);
			if (Tool.checkHaveValue(cookieSet)) {
				for (Cookie cookie : cookieSet) {
					if (cookie.name().equals(name)) {
						return cookie;
					}
				}
			}
		}

		return null;
	}
}
