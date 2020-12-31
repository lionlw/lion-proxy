package com.lion.utility.proxy.http.constant;

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
	 * http代理业务线程池-默认线程个数
	 */
	public final static int HTTPPROXY_BIZTHREAD_TOTAL_DEFAULT = 8;

	/**
	 * http代理服务端默认Acceptor线程数
	 */
	public final static int HTTPPROXY_SERVER_ACCEPTORTHREADS_DEFAULT = 2;
	/**
	 * http代理服务端默认io线程数
	 */
	public final static int HTTPPROXY_SERVER_IOTHREADS_DEFAULT = 4;

	/**
	 * http代理连接超时默认秒数
	 */
	public final static int HTTPPROXY_CONNECTTIMEOUT_SECOND_DEFAULT = 10;
	/**
	 * http代理读取超时默认秒数
	 */
	public final static int HTTPPROXY_READTIMEOUT_SECOND_DEFAULT = 60;
	/**
	 * http代理最大接收消息默认字节数
	 */
	public final static int HTTPPROXY_MESSAGERECIEVE_MAXLENGTH_DEFAULT = 30 * 1024 * 1024;

	/**
	 * aes加密模式
	 */
	public final static String AES_SENCRYPTMODE = "AES/CBC/PKCS5Padding";
	/**
	 * aes加密key
	 */
	public final static String AES_KEY = "1234proxyauth111";
	/**
	 * aes加密iv
	 */
	public final static String AES_IV = "lion888proxyauth";
	/**
	 * cookie名
	 */
	public final static String COOKIE_NAME = "proxyServerAuth2";
	/**
	 * cookie基础值
	 */
	public final static String COOKIE_BASICVALUE = "lion_2020";
	/**
	 * cookie过期时间
	 */
	public final static long COOKIE_MAXAGESECOND = 3L * 24L * 60L * 60L;
}
