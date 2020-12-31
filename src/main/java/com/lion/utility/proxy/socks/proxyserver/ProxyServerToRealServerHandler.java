package com.lion.utility.proxy.socks.proxyserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.lion.utility.proxy.socks.CommonLIB;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;
import com.lion.utility.tool.log.LogLIB;

/**
 * proxy server消息转发给real server
 * 
 * @author lion
 *
 */
public class ProxyServerToRealServerHandler extends ChannelInboundHandlerAdapter {
	private SocksProxyServerConfig socksProxyServerConfig;
	private String logPre;
	/**
	 * real server连接
	 */
	private ChannelFuture realServerChannelFuture;

	public ProxyServerToRealServerHandler(SocksProxyServerConfig socksProxyServerConfig, String logPre, ChannelFuture realServerChannelFuture) {
		this.socksProxyServerConfig = socksProxyServerConfig;
		this.logPre = logPre;
		this.realServerChannelFuture = realServerChannelFuture;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (this.socksProxyServerConfig.getIsDebug()) {
				LogLIB.info("ProxyServerToRealServerHandler send msg, " + this.logPre);
			}

			// 将proxy server消息转发给real server（writeAndFlush会自动释放msg）
			this.realServerChannelFuture.channel().writeAndFlush(msg);
		} catch (Exception e) {
			// 释放msg中的资源（ByteBuf）
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogLIB.error(CommonLIB.getNettyRequestIp(ctx) + ", exceptionCaught, " + this.logPre, cause);
		com.lion.utility.proxy.socks.CommonLIB.closeOnFlush(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LogLIB.error(CommonLIB.getNettyRequestIp(ctx) + ", channelInactive, " + this.logPre);
		if (this.realServerChannelFuture != null) {
			com.lion.utility.proxy.socks.CommonLIB.closeOnFlush(this.realServerChannelFuture.channel());
		}
	}
}
