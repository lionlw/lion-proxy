package com.lion.utility.proxy.tcp;

import java.util.Map.Entry;

import com.lion.utility.proxy.tcp.entity.TcpAddress;
import com.lion.utility.proxy.tcp.entity.TcpProxyServerConfig;

/**
 * tcp端口转发代理服务器（基于netty）
 * 
 * @author lion
 *
 */
public class TcpProxyServer {
	// [start] 变量定义

	/**
	 * 服务监听ip
	 */
	protected String ip;

	/**
	 * tcp proxy 服务配置
	 */
	protected TcpProxyServerConfig tcpProxyServerConfig;

	// [end]

	/**
	 * 构造
	 * 
	 * @param ip
	 *            服务ip
	 * @throws Exception
	 *             异常
	 */
	public TcpProxyServer(String ip) throws Exception {
		this.ip = ip;
		this.tcpProxyServerConfig = new TcpProxyServerConfig();
	}

	/**
	 * 设置twc服务端配置（需要在调用start前设置）
	 * 
	 * @param tcpProxyServerConfig
	 *            tcp proxy服务配置
	 * @throws Exception
	 *             异常
	 */
	public void setTcpProxyServerConfig(TcpProxyServerConfig tcpProxyServerConfig) throws Exception {
		this.tcpProxyServerConfig = tcpProxyServerConfig;
	}

	/**
	 * 启动
	 * 
	 * @throws Exception
	 *             异常
	 */
	public void start() throws Exception {
		// 异步启动服务，防止阻塞
		for (Entry<Integer, TcpAddress> entry : this.tcpProxyServerConfig.getProxyMappingMap().entrySet()) {
			new Thread(new TcpProxyServerStartThread(this, entry.getKey(), entry.getValue()), "TcpProxyServer-TcpProxyServerStartThread-" + entry.getKey()).start();
		}
	}
}
