package com.lion.utility.proxy.socks.realserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;
import com.lion.utility.proxy.socks.proxyserver.ProxyServerToRealServerHandler;
import com.lion.utility.tool.log.LogLIB;

/**
 * real server连接channel监听
 * 
 * @author lion
 *
 */
public class RealServerChannelListener implements ChannelFutureListener {
	private SocksProxyServerConfig socksProxyServerConfig;
	private String logPre;
	private ChannelHandlerContext proxyServerctx;

	public RealServerChannelListener(SocksProxyServerConfig socksProxyServerConfig, String logPre, ChannelHandlerContext proxyServerctx) {
		this.socksProxyServerConfig = socksProxyServerConfig;
		this.logPre = logPre;
		this.proxyServerctx = proxyServerctx;
	}

	@Override
	public void operationComplete(ChannelFuture realServerChannelFuture) {
		Socks5CommandResponse response;
		if (realServerChannelFuture.isSuccess()) {
			LogLIB.info("connect real server succeed, " + this.logPre);
			// 连接real server成功，则proxy server连接增加处理handler
			this.proxyServerctx.pipeline().addLast(new ProxyServerToRealServerHandler(this.socksProxyServerConfig, this.logPre, realServerChannelFuture));
			response = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
		} else {
			LogLIB.error("connect real server failed, " + this.logPre);
			response = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
		}
		// 将结果消息，发给proxy server
		this.proxyServerctx.writeAndFlush(response);
	}
}
