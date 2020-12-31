package com.lion.utility.proxy.socks.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lion.utility.proxy.socks.constant.Constant;

/**
 * socks proxy server配置
 * 
 * @author lion
 *
 */
public class SocksProxyServerConfig {
	/**
	 * 是否debug模式
	 */
	private Boolean isDebug = false;

	/**
	 * ip限制白名单（为空则表示不验证）
	 */
	private Set<String> requestIPSet = new HashSet<>();
	/**
	 * 代理认证map（key：username，value：password）（为空则表示不验证）
	 */
	private Map<String, String> proxyAuthMap = new HashMap<>();
	/**
	 * 代理许可ip端口（为空则表示不验证）
	 */
	private Set<SocksAddress> proxyAuthAddressSet = new HashSet<>();

	/**
	 * 代理服务Acceptor线程数
	 */
	private Integer acceptorThreads = Constant.SOCKSPROXY_SERVER_ACCEPTORTHREADS_DEFAULT;
	/**
	 * 代理服务io线程数
	 */
	private Integer ioThreads = Constant.SOCKSPROXY_SERVER_IOTHREADS_DEFAULT;

	/**
	 * 连接real server io线程数
	 */
	private Integer clientIOThreads = Constant.CLIENT_IOTHREADS_DEFAULT;

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
	 * 设置代理认证map（key：username，value：password）（为空则表示不验证）
	 * 
	 * @param proxyAuthMap
	 *            代理认证map（key：username，value：password）（为空则表示不验证）
	 */
	public void setProxyAuthMap(Map<String, String> proxyAuthMap) {
		this.proxyAuthMap = proxyAuthMap;
	}

	/**
	 * 获取代理认证map（key：username，value：password）（为空则表示不验证）
	 * 
	 * @return 代理认证map（key：username，value：password）（为空则表示不验证）
	 */
	public Map<String, String> getProxyAuthMap() {
		return proxyAuthMap;
	}

	/**
	 * 设置代理许可ip端口（为空则表示不验证）
	 * 
	 * @param proxyAuthAddressSet
	 *            代理许可ip端口（为空则表示不验证）
	 */
	public void setProxyAuthAddressSet(Set<SocksAddress> proxyAuthAddressSet) {
		this.proxyAuthAddressSet = proxyAuthAddressSet;
	}

	/**
	 * 获取代理许可ip端口（为空则表示不验证）
	 * 
	 * @return 代理许可ip端口（为空则表示不验证）
	 */
	public Set<SocksAddress> getProxyAuthAddressSet() {
		return proxyAuthAddressSet;
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

	/**
	 * 设置连接real server io线程数
	 * 
	 * @param clientIOThreads
	 *            连接real server io线程数
	 */
	public void setClientIOThreads(Integer clientIOThreads) {
		this.clientIOThreads = clientIOThreads;
	}

	/**
	 * 获取连接real server io线程数
	 * 
	 * @return 连接real server io线程数
	 */
	public Integer getClientIOThreads() {
		return clientIOThreads;
	}

}
