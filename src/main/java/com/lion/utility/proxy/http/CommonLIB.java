package com.lion.utility.proxy.http;

import com.lion.utility.tool.log.LogLIB;

import io.netty.channel.ChannelHandlerContext;

/**
 * 工具类
 * 
 * @author lion
 */
public class CommonLIB {
	private CommonLIB() {

	}

	/**
	 * 获取客户端ip
	 * 
	 * @param ctx 上下文
	 * @return 结果
	 */
	public static String getNettyRequestIp(ChannelHandlerContext ctx) {
		try {
			// 返回的格式：/114.80.117.180:48534

			return ctx.channel().remoteAddress().toString().replace("/", "");
		} catch (Exception e) {
			LogLIB.error("", e);
		}

		return "";
	}
}
