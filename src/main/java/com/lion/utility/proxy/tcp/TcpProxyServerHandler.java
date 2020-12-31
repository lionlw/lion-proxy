package com.lion.utility.proxy.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.util.ReferenceCountUtil;
import com.lion.utility.proxy.tcp.entity.TcpAddress;
import com.lion.utility.proxy.tcp.entity.TcpProxyServerConfig;
import com.lion.utility.tool.log.LogLIB;

/**
 * 自定义命令请求处理
 * 
 * @author lion
 *
 */
class TcpProxyServerHandler extends ChannelInboundHandlerAdapter {
	private TcpProxyServerConfig tcpProxyServerConfig;
	private TcpAddress realServerAddress;
	private String logPre;

	/**
	 * real server通道
	 */
	private Channel realServerChannel;

	public TcpProxyServerHandler(TcpProxyServerConfig tcpProxyServerConfig, TcpAddress realServerAddress) {
		this.tcpProxyServerConfig = tcpProxyServerConfig;
		this.realServerAddress = realServerAddress;
	}

	@Override
	public void channelActive(ChannelHandlerContext proxyServerctx) {
		this.logPre = this.realServerAddress.getKey();

		// 连接命令处理
		LogLIB.info("connect real server start");

		Channel proxyServerChannel = proxyServerctx.channel();

		// real server通道与proxy server共用一个eventloop
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(proxyServerChannel.eventLoop())
				.channel(proxyServerctx.channel().getClass())
				.handler(new RealServerHandler(this.tcpProxyServerConfig, this.logPre, proxyServerChannel))
				.option(ChannelOption.AUTO_READ, false); // 设置手动读取消息
		ChannelFuture future = bootstrap.connect(this.realServerAddress.getIp(), this.realServerAddress.getPort());
		this.realServerChannel = future.channel();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					LogLIB.info("connect real server succeed");
					// real server连接成功则从proxy server开始读取数据
					proxyServerChannel.read();
				} else {
					LogLIB.error("connect real server failed");
					// real server连接失败则关闭代理proxy server
					proxyServerChannel.close();
				}
			}
		});
	}

	@Override
	public void channelRead(ChannelHandlerContext proxyServerctx, Object msg) throws Exception {
		try {
			if (this.realServerChannel.isActive()) {
				if (this.tcpProxyServerConfig.getIsDebug()) {
					LogLIB.info("TcpProxyServerHandler send msg, " + this.logPre);
				}

				// 将从proxy server获取到的数据，写入real server（writeAndFlush会自动释放msg）
				this.realServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						if (future.isSuccess()) {
							// 当real server数据写入完成后，proxy server开始读取下一个数据块
							proxyServerctx.channel().read();
						} else {
							// 失败则关闭real server通道
							future.channel().close();
						}
					}
				});
			} else {
				// 释放msg中的资源（ByteBuf）
				ReferenceCountUtil.release(msg);
			}
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
		if (this.realServerChannel != null) {
			// 关闭real server通道
			com.lion.utility.proxy.tcp.CommonLIB.closeOnFlush(this.realServerChannel);
		}
	}
}