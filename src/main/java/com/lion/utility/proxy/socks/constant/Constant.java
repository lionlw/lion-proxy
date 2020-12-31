package com.lion.utility.proxy.socks.constant;

/**
 * 常量
 * 
 * @author lion
 */
public class Constant {
	/**
	 * 编码
	 */
	public final static String ENCODING = "UTF-8";

	/**
	 * socks代理服务端默认Acceptor线程数
	 */
	public final static int SOCKSPROXY_SERVER_ACCEPTORTHREADS_DEFAULT = 2;
	/**
	 * socks代理服务端默认io线程数
	 */
	public final static int SOCKSPROXY_SERVER_IOTHREADS_DEFAULT = 4;

	/**
	 * 连接real server 默认io线程数
	 */
	public final static int CLIENT_IOTHREADS_DEFAULT = 4;
}
