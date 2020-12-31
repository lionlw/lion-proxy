package com.lion.utility.proxy.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.lion.utility.proxy.tcp.entity.TcpProxyServerConfig;
import com.lion.utility.tool.log.LogLIB;

/**
 * real server连接处理
 * 
 * @author lion
 */
class RealServerHandler extends ChannelInboundHandlerAdapter {
	private TcpProxyServerConfig tcpProxyServerConfig;
	private String logPre;
	private final Channel proxyServerChannel;

	public RealServerHandler(TcpProxyServerConfig tcpProxyServerConfig, String logPre, Channel proxyServerChannel) {
		this.tcpProxyServerConfig = tcpProxyServerConfig;
		this.logPre = logPre;
		this.proxyServerChannel = proxyServerChannel;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.read();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			if (this.tcpProxyServerConfig.getIsDebug()) {
				LogLIB.info("RealServerHandler send msg, " + this.logPre);
			}

			// 将从real server获取到的数据，写入proxy server（writeAndFlush会自动释放msg）
			this.proxyServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						// 当proxy server数据写入完成后，real server开始读取下一个数据块
						ctx.channel().read();
					} else {
						// 失败则关闭proxy server通道
						future.channel().close();
					}
				}
			});
		} catch (Exception e) {
			// 释放msg中的资源（ByteBuf）
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		LogLIB.error(CommonLIB.getNettyRequestIp(ctx) + ", exceptionCaught, " + this.logPre, cause);
		com.lion.utility.proxy.tcp.CommonLIB.closeOnFlush(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		LogLIB.error(CommonLIB.getNettyRequestIp(ctx) + ", channelInactive, " + this.logPre);
		if (this.proxyServerChannel != null) {
			// 管理proxy server通道
			com.lion.utility.proxy.tcp.CommonLIB.closeOnFlush(this.proxyServerChannel);
		}
	}
}
