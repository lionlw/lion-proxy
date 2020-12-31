package com.lion.utility.proxy.http.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lion.utility.proxy.http.constant.Constant;

/**
 * http proxy server配置
 * 
 * @author lion
 *
 */
public class HttpProxyServerConfig {
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
	 * 代理许可域名set（不含协议）（为空则表示不验证）
	 */
	private Set<String> proxyAuthDomainSet = new HashSet<>();

	/**
	 * 业务线程池中线程个数
	 */
	private Integer bizThreadPoolThreadTotal = Constant.HTTPPROXY_BIZTHREAD_TOTAL_DEFAULT;

	/**
	 * http代理连接超时秒数
	 */
	private Integer connectTimeoutSecond = Constant.HTTPPROXY_CONNECTTIMEOUT_SECOND_DEFAULT;
	/**
	 * http代理读取超时秒数
	 */
	private Integer readTimeoutSecond = Constant.HTTPPROXY_READTIMEOUT_SECOND_DEFAULT;

	/**
	 * 最大接收消息字节数
	 */
	private Integer messageRecieveMaxLength = Constant.HTTPPROXY_MESSAGERECIEVE_MAXLENGTH_DEFAULT;

	/**
	 * Acceptor线程数
	 */
	private Integer acceptorThreads = Constant.HTTPPROXY_SERVER_ACCEPTORTHREADS_DEFAULT;
	/**
	 * io线程数
	 */
	private Integer ioThreads = Constant.HTTPPROXY_SERVER_IOTHREADS_DEFAULT;

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
	 * 设置代理许可域名set（不含协议）（为空则表示不验证）
	 * 
	 * @param proxyAuthDomainSet
	 *            代理许可域名set（不含协议）（为空则表示不验证）
	 */
	public void setProxyAuthDomainSet(Set<String> proxyAuthDomainSet) {
		this.proxyAuthDomainSet = proxyAuthDomainSet;
	}

	/**
	 * 获取代理许可域名set（不含协议）（为空则表示不验证）
	 * 
	 * @return 代理许可域名set（不含协议）（为空则表示不验证）
	 */
	public Set<String> getProxyAuthDomainSet() {
		return proxyAuthDomainSet;
	}

	/**
	 * 设置业务线程池中线程个数
	 * 
	 * @param bizThreadPoolThreadTotal
	 *            业务线程池中线程个数
	 */
	public void setBizThreadPoolThreadTotal(Integer bizThreadPoolThreadTotal) {
		this.bizThreadPoolThreadTotal = bizThreadPoolThreadTotal;
	}

	/**
	 * 获取业务线程池中线程个数
	 * 
	 * @return 业务线程池中线程个数
	 */
	public Integer getBizThreadPoolThreadTotal() {
		return bizThreadPoolThreadTotal;
	}

	/**
	 * 设置http代理连接超时秒数
	 * 
	 * @param connectTimeoutSecond
	 *            http代理连接超时秒数
	 */
	public void setConnectTimeoutSecond(Integer connectTimeoutSecond) {
		this.connectTimeoutSecond = connectTimeoutSecond;
	}

	/**
	 * 获取http代理连接超时秒数
	 * 
	 * @return http代理连接超时秒数
	 */
	public Integer getConnectTimeoutSecond() {
		return connectTimeoutSecond;
	}

	/**
	 * 设置http代理读取超时秒数
	 * 
	 * @param readTimeoutSecond
	 *            http代理读取超时秒数
	 */
	public void setReadTimeoutSecond(Integer readTimeoutSecond) {
		this.readTimeoutSecond = readTimeoutSecond;
	}

	/**
	 * 获取http代理读取超时秒数
	 * 
	 * @return http代理读取超时秒数
	 */
	public Integer getReadTimeoutSecond() {
		return readTimeoutSecond;
	}

	/**
	 * 设置最大接收消息字节数
	 * 
	 * @param messageRecieveMaxLength
	 *            最大接收消息字节数
	 */
	public void setMessageRecieveMaxLength(Integer messageRecieveMaxLength) {
		this.messageRecieveMaxLength = messageRecieveMaxLength;
	}

	/**
	 * 获取最大接收消息字节数
	 * 
	 * @return 最大接收消息字节数
	 */
	public Integer getMessageRecieveMaxLength() {
		return messageRecieveMaxLength;
	}

	/**
	 * 设置Acceptor线程数
	 * 
	 * @param acceptorThreads
	 *            Acceptor线程数
	 */
	public void setAcceptorThreads(Integer acceptorThreads) {
		this.acceptorThreads = acceptorThreads;
	}

	/**
	 * 获取Acceptor线程数
	 * 
	 * @return Acceptor线程数
	 */
	public Integer getAcceptorThreads() {
		return acceptorThreads;
	}

	/**
	 * 设置io线程数
	 * 
	 * @param ioThreads
	 *            io线程数
	 */
	public void setIoThreads(Integer ioThreads) {
		this.ioThreads = ioThreads;
	}

	/**
	 * 获取io线程数
	 * 
	 * @return io线程数
	 */
	public Integer getIoThreads() {
		return ioThreads;
	}

}
