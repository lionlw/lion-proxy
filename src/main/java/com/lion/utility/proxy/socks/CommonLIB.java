package com.lion.utility.proxy.socks;

import com.lion.utility.tool.log.LogLIB;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
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
	 * 当所有的队列写请求都完成后，关闭通道
	 * 
	 * @param ch 通道
	 */
	public static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
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
