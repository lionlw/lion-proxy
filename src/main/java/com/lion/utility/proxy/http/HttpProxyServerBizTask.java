package com.lion.utility.proxy.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import com.lion.utility.tool.code.Base64LIB;
import com.lion.utility.tool.code.SEncryptLIB;
import com.lion.utility.tool.common.Tool;
import com.lion.utility.proxy.http.constant.Constant;
import com.lion.utility.tool.log.LogLIB;

/**
 * 业务逻辑处理线程
 * 
 * @author lion
 *
 */
class HttpProxyServerBizTask implements Runnable {
	private ChannelHandlerContext ctx;
	private Object msg;
	private HttpProxyServer httpProxyServer;
	private String topDomain;
	private String encryptCookieValue;

	public HttpProxyServerBizTask(ChannelHandlerContext ctx, Object msg, HttpProxyServer httpProxyServer) {
		this.ctx = ctx;
		this.msg = msg;
		this.httpProxyServer = httpProxyServer;
		this.encryptCookieValue = "";
	}

	@Override
	public void run() {
		try {
			// 请求有效性验证
			if (!(msg instanceof FullHttpRequest)) {
				this.sendException("", HttpResponseStatus.BAD_REQUEST);
				return;
			}

			FullHttpRequest httpRequest = (FullHttpRequest) this.msg;

			try {
				this.topDomain = Tool.getTopDomain(httpRequest.uri());

				// basic认证
				if (!this.checkBasicAuth(httpRequest)) {
					this.sendBasicAuth();
					return;
				}

				// 域名认证
				if (!this.checkDomainAuth(httpRequest)) {
					this.sendException("invalid domain", HttpResponseStatus.BAD_REQUEST);
					return;
				}

				// 请求方法验证
				HttpMethod method = httpRequest.method();
				if (!HttpMethod.GET.equals(method) && !HttpMethod.POST.equals(method)) {
					this.sendException("invalid method", HttpResponseStatus.BAD_REQUEST);
					return;
				}

				this.sendHttpProxy(httpRequest);
			} catch (Exception e) {
				LogLIB.error(this.httpProxyServer.getServerInfo() + ", exception, " + httpRequest.uri(), e);
				this.sendException("exception", HttpResponseStatus.BAD_REQUEST);
			}
		} finally {
			// 释放msg中的资源（ByteBuf）--msg若做了writeAndFlush，则会自动释放
			ReferenceCountUtil.release(this.msg);
		}
	}

	/**
	 * 处理http代理
	 * 
	 * @param httpRequest http请求对象
	 * @throws Exception 异常
	 */
	private void sendHttpProxy(FullHttpRequest httpRequest) throws Exception {
		byte[] result = null;
		HttpResponseStatus httpStatus = null;
		Map<String, List<String>> responseHeader = new HashMap<>();
		HttpURLConnection httpConnection = null;

		try {
			// 打开连接
			httpConnection = (HttpURLConnection) (new URL(httpRequest.uri())).openConnection();
			httpConnection.setConnectTimeout(this.httpProxyServer.httpProxyServerConfig.getConnectTimeoutSecond() * 1000);
			httpConnection.setReadTimeout(this.httpProxyServer.httpProxyServerConfig.getReadTimeoutSecond() * 1000);
			httpConnection.setRequestMethod(httpRequest.method().name());
			httpConnection.setInstanceFollowRedirects(false); // 设定自行处理302

			// post则设置指定header，写在此处，是为了防止覆盖请求的header（请求header优先）
			if (HttpMethod.POST.equals(httpRequest.method())) {
				httpConnection.setRequestProperty(HttpHeaderNames.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");
			}

			// header处理（将代理请求头转发到后端请求头上）
			if (httpRequest.headers() != null && httpRequest.headers().size() > 0) {
				for (Entry<String, String> entry : httpRequest.headers().entries()) {
					// 存在key为null的情况
					if (entry.getKey() != null) {
						httpConnection.setRequestProperty(entry.getKey(), entry.getValue());
					}
				}
			}

			// post处理, 设置post参数（将代理请求内容转发到后端请求上）
			if (HttpMethod.POST.equals(httpRequest.method())) {
				ByteBuf buf = httpRequest.content(); // 下述不做手动释放bytebuf，在顶部代码做统一释放
				byte[] array = new byte[buf.readableBytes()];
				buf.readBytes(array);

				httpConnection.setRequestProperty(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(array.length));
				httpConnection.setDoOutput(true);
				httpConnection.getOutputStream().write(array);
				httpConnection.getOutputStream().flush();
				httpConnection.getOutputStream().close();
			}

			// 处理返回结果
			// 保存http状态码
			httpStatus = HttpResponseStatus.valueOf(httpConnection.getResponseCode());
			responseHeader = httpConnection.getHeaderFields();

			if (httpStatus.equals(HttpResponseStatus.OK)) {
				// 200处理
			} else if (httpStatus.equals(HttpResponseStatus.FOUND)) {
				// 302处理
				String newurl = httpConnection.getHeaderField("Location");
				this.sendRedirect(newurl);
				return;
			} else {
				// 非200处理
				this.sendException("request failed", httpStatus);
				if (this.httpProxyServer.httpProxyServerConfig.getIsDebug()) {
					LogLIB.info(httpRequest.uri() + " " + httpStatus.toString());
				}
				return;
			}

			// 处理返回内容
			try (InputStream in = httpConnection.getInputStream();
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				result = out.toByteArray();
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}

		// 200处理
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(result != null ? result : new byte[0]));
		// header处理（将后端响应头转发到代理响应头上）
		if (responseHeader != null) {
			for (Entry<String, List<String>> entry : responseHeader.entrySet()) {
				// 存在key为null的情况
				if (entry.getKey() != null) {
					response.headers().set(entry.getKey(), entry.getValue());
				}
			}
		}
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		this.outputResponse(response);

		if (this.httpProxyServer.httpProxyServerConfig.getIsDebug()) {
			LogLIB.info(httpRequest.uri() + " " + httpStatus.toString());
		}
	}

	/**
	 * basic认证（每个顶级域名cookie不通用，因此需要分别代理认证）
	 * 
	 * @param httpRequest 请求对象
	 * @return 结果
	 */
	private boolean checkBasicAuth(FullHttpRequest httpRequest) {
		try {
			if (!Tool.checkHaveValue(this.httpProxyServer.httpProxyServerConfig.getProxyAuthMap())) {
				return true;
			}

			String authResult = NettyCookieLIB.getCookieValue(httpRequest, Constant.COOKIE_NAME);
			if (Tool.checkHaveValue(authResult)) {
				String plain = SEncryptLIB.decrypt(SEncryptLIB.SENCRYPTTYPE_AES, Constant.AES_SENCRYPTMODE, Constant.AES_KEY, Constant.AES_IV, authResult, Constant.ENCODING);
				// 校验cookie中的内容是否包含基础值
				if (plain.indexOf(Constant.COOKIE_BASICVALUE) == 0) {
					return true;
				}
			}

			// 登录认证
			String auth = httpRequest.headers().get("Authorization");
			if (Tool.checkHaveValue(auth) && auth.length() > 6) {
				auth = auth.substring(6, auth.length());
				String decodedAuth = Base64LIB.decrypt(auth, Constant.ENCODING);

				String[] array = decodedAuth.split(":");
				if (array != null && array.length == 2) {
					String username = array[0].trim();
					String password = array[1].trim();

					// 校验用户名密码
					String value = this.httpProxyServer.httpProxyServerConfig.getProxyAuthMap().get(username);
					if (Tool.checkHaveValue(value) && value.equals(password)) {
						String cookieValue = Constant.COOKIE_BASICVALUE + "_" + username + "_" + Tool.getRndnum();
						this.encryptCookieValue = SEncryptLIB.encrypt(SEncryptLIB.SENCRYPTTYPE_AES, Constant.AES_SENCRYPTMODE, Constant.AES_KEY, Constant.AES_IV, cookieValue, Constant.ENCODING);
						return true;
					}
				}
			}
		} catch (Exception e) {
			LogLIB.error(this.httpProxyServer.getServerInfo() + ", exception", e);
		}

		LogLIB.error("checkBasicAuth failed");
		return false;
	}

	/**
	 * 域名校验
	 * 
	 * @param httpRequest 请求对象
	 * @return 结果
	 * @throws Exception
	 */
	private boolean checkDomainAuth(FullHttpRequest httpRequest) throws Exception {
		if (!Tool.checkHaveValue(this.httpProxyServer.httpProxyServerConfig.getProxyAuthDomainSet())) {
			return true;
		}

		URL url = new URL(httpRequest.uri());
		String domain = url.getHost();
		if (this.httpProxyServer.httpProxyServerConfig.getProxyAuthDomainSet().contains(domain)) {
			return true;
		}

		LogLIB.error("checkDomainAuth failed, domain:" + domain);
		return false;
	}

	/**
	 * 401处理（弹出浏览器basic登录框）
	 */
	private void sendBasicAuth() {
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1,
				HttpResponseStatus.UNAUTHORIZED,
				Unpooled.copiedBuffer(HttpResponseStatus.UNAUTHORIZED.toString(), CharsetUtil.UTF_8));

		response.headers().set("Pragma", "No-cache");
		response.headers().set("Cache-Control", "no-cache");
		response.headers().set("Expires", 0);
		response.headers().set("WWW-authenticate", "Basic Realm=\"please input proxy username/password\"");
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		this.outputResponse(response);
	}

	/**
	 * 302处理
	 * 
	 * @param url 重定向地址
	 */
	private void sendRedirect(String url) {
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1,
				HttpResponseStatus.FOUND,
				Unpooled.copiedBuffer("redirect", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.LOCATION, url);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		this.outputResponse(response);
	}

	/**
	 * 设置http异常状态
	 * 
	 * @param info   消息
	 * @param status 状态
	 */
	private void sendException(String info, HttpResponseStatus status) {
		if (!Tool.checkHaveValue(info)) {
			info = status.toString();
		}

		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1,
				status,
				Unpooled.copiedBuffer(info, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		this.outputResponse(response);
	}

	/**
	 * 输出响应
	 * 
	 * @param response 响应
	 */
	private void outputResponse(FullHttpResponse response) {
		// 设置认证cookie
		if (Tool.checkHaveValue(this.encryptCookieValue)) {
			NettyCookieLIB.addCookie(
					response,
					Constant.COOKIE_NAME,
					this.encryptCookieValue,
					Constant.COOKIE_MAXAGESECOND,
					this.topDomain,
					"/");
		}
		this.ctx.writeAndFlush(response);
	}
}
