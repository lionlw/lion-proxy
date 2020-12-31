package com.lion.utility.proxy.tcp.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lion.utility.proxy.tcp.constant.Constant;

/**
 * tcp proxy server配置
 * 
 * @author lion
 *
 */
public class TcpProxyServerConfig {
	/**
	 * 是否debug模式
	 */
	private Boolean isDebug = false;

	/**
	 * ip限制白名单（为空则表示不验证）
	 */
	private Set<String> requestIPSet = new HashSet<>();
	/**
	 * 代理映射map（key：代理服务端口，value：TcpAddress real server地址）
	 */
	private Map<Integer, TcpAddress> proxyMappingMap = new HashMap<>();

	/**
	 * 代理服务Acceptor线程数
	 */
	private Integer acceptorThreads = Constant.TCPPROXY_SERVER_ACCEPTORTHREADS_DEFAULT;
	/**
	 * 代理服务io线程数
	 */
	private Integer ioThreads = Constant.TCPPROXY_SERVER_IOTHREADS_DEFAULT;

	/**
	 * 设置是否debug模式
	 * 
	 * @param isDebug
	 *            是否debug模式
	 */
	public void setIsDebug(Boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * 获取是否debug模式
	 * 
	 * @return 是否debug模式
	 */
	public Boolean getIsDebug() {
		return isDebug;
	}

	/**
	 * 设置ip限制白名单（为空则表示不验证）
	 * 
	 * @param requestIPSet
	 *            ip限制白名单（为空则表示不验证）
	 */
	public void setRequestIPSet(Set<String> requestIPSet) {
		this.requestIPSet = requestIPSet;
	}

	/**
	 * 获取ip限制白名单（为空则表示不验证）
	 * 
	 * @return ip限制白名单（为空则表示不验证）
	 */
	public Set<String> getRequestIPSet() {
		return requestIPSet;
	}

	/**
	 * 设置代理映射map（key：代理服务端口，value：TcpAddress real server地址）
	 * 
	 * @param proxyMappingMap
	 *            代理映射map（key：代理服务端口，value：TcpAddress real server地址）
	 */
	public void setProxyMappingMap(Map<Integer, TcpAddress> proxyMappingMap) {
		this.proxyMappingMap = proxyMappingMap;
	}

	/**
	 * 获取代理映射map（key：代理服务端口，value：TcpAddress real server地址）
	 * 
	 * @return 代理映射map（key：代理服务端口，value：TcpAddress real server地址）
	 */
	public Map<Integer, TcpAddress> getProxyMappingMap() {
		return proxyMappingMap;
	}

	/**
	 * 设置代理服务Acceptor线程数
	 * 
	 * @param acceptorThreads
	 *            代理服务Acceptor线程数
	 */
	public void setAcceptorThreads(Integer acceptorThreads) {
		this.acceptorThreads = acceptorThreads;
	}

	/**
	 * 获取代理服务Acceptor线程数
	 * 
	 * @return 代理服务Acceptor线程数
	 */
	public Integer getAcceptorThreads() {
		return acceptorThreads;
	}

	/**
	 * 设置代理服务io线程数
	 * 
	 * @param ioThreads
	 *            代理服务io线程数
	 */
	public void setIoThreads(Integer ioThreads) {
		this.ioThreads = ioThreads;
	}

	/**
	 * 获取代理服务io线程数
	 * 
	 * @return 代理服务io线程数
	 */
	public Integer getIoThreads() {
		return ioThreads;
	}

}
