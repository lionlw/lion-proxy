package com.lion.utility.proxy.http;

import java.util.concurrent.ExecutorService;

import com.lion.utility.proxy.http.entity.HttpAddress;
import com.lion.utility.proxy.http.entity.HttpProxyServerConfig;

/**
 * http代理服务器（基于netty）
 * 
 * @author lion
 *
 */
public class HttpProxyServer {
	// [start] 变量定义

	/**
	 * 服务监听地址
	 */
	protected HttpAddress httpAddress;

	/**
	 * http proxy 服务配置
	 */
	protected HttpProxyServerConfig httpProxyServerConfig;

	/**
	 * 业务处理线程池
	 */
	protected ExecutorService bizThreadPool;

	// [end]

	/**
	 * 构造
	 * 
	 * @param httpAddress
	 *            服务地址
	 * @throws Exception
	 *             异常
	 */
	public HttpProxyServer(HttpAddress httpAddress) throws Exception {
		this.httpAddress = httpAddress;
		this.httpProxyServerConfig = new HttpProxyServerConfig();
	}

	/**
	 * 设置http代理服务器配置（需要在调用start前设置）
	 * 
	 * @param httpProxyServerConfig
	 *            http proxy服务配置
	 * @throws Exception
	 *             异常
	 */
	public void setHttpProxyServerConfig(HttpProxyServerConfig httpProxyServerConfig) throws Exception {
		this.httpProxyServerConfig = httpProxyServerConfig;
	}

	/**
	 * 启动
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void start() throws Exception {
		// 异步启动服务，防止阻塞
		new Thread(new HttpProxyServerStartThread(this), "HttpProxyServer-HttpProxyServerStartThread").start();
	}

	/**
	 * 获取服务信息
	 * 
	 * @return 结果
	 */
	protected String getServerInfo() {
		return "HttpProxyServer-" + this.httpAddress.getIp() + ":" + this.httpAddress.getPort();
	}
}
