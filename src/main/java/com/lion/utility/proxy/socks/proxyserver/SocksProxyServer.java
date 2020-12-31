package com.lion.utility.proxy.socks.proxyserver;

import io.netty.channel.EventLoopGroup;
import com.lion.utility.proxy.socks.entity.SocksAddress;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;

/**
 * socks代理服务器（基于netty），只支持socks5协议
 * 
 * @author lion
 *
 */
public class SocksProxyServer {
	// [start] 变量定义

	/**
	 * 服务监听地址
	 */
	protected SocksAddress socksAddress;

	/**
	 * socks proxy 服务配置
	 */
	protected SocksProxyServerConfig socksProxyServerConfig;

	/**
	 * 连接real server netty对象
	 */
	protected EventLoopGroup clientIOGroup;

	// [end]

	/**
	 * 构造
	 * 
	 * @param socksAddress
	 *            服务地址
	 * @throws Exception
	 *             异常
	 */
	public SocksProxyServer(SocksAddress socksAddress) throws Exception {
		this.socksAddress = socksAddress;
		this.socksProxyServerConfig = new SocksProxyServerConfig();
	}

	/**
	 * 设置twc服务端配置（需要在调用start前设置）
	 * 
	 * @param socksProxyServerConfig
	 *            socks proxy服务配置
	 * @throws Exception
	 *             异常
	 */
	public void setSocksProxyServerConfig(SocksProxyServerConfig socksProxyServerConfig) throws Exception {
		this.socksProxyServerConfig = socksProxyServerConfig;
	}

	/**
	 * 启动
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void start() throws Exception {
		// 异步启动服务，防止阻塞
		new Thread(new SocksProxyServerStartThread(this), "SocksProxyServer-SocksProxyServerStartThread").start();
	}

	/**
	 * 获取服务信息
	 * 
	 * @return 结果
	 */
	protected String getServerInfo() {
		return "SocksProxyServer-" + this.socksAddress.getIp() + ":" + this.socksAddress.getPort();
	}
}
