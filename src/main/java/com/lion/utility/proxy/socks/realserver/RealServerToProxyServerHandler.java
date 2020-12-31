package com.lion.utility.proxy.socks.realserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.lion.utility.proxy.socks.CommonLIB;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;
import com.lion.utility.tool.log.LogLIB;

/**
 * real server消息转发给proxy server
 * 
 * @author lion
 *
 */
class RealServerToProxyServerHandler extends ChannelInboundHandlerAdapter {
	private SocksProxyServerConfig socksProxyServerConfig;
	private String logPre;
	/**
	 * proxy server连接
	 */
	private ChannelHandlerContext proxyServerctx;

	public RealServerToProxyServerHandler(SocksProxyServerConfig socksProxyServerConfig, String logPre, ChannelHandlerContext proxyServerctx) {
		this.socksProxyServerConfig = socksProxyServerConfig;
		this.logPre = logPre;
		this.proxyServerctx = proxyServerctx;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (this.socksProxyServerConfig.getIsDebug()) {
				LogLIB.info("RealServerToProxyServerHandler send msg, " + this.logPre);
			}

			// 将real server消息转发给proxy server（writeAndFlush会自动释放msg）
			this.proxyServerctx.writeAndFlush(msg);
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
		if (this.proxyServerctx != null) {
			com.lion.utility.proxy.socks.CommonLIB.closeOnFlush(this.proxyServerctx.channel());
		}
	}
}