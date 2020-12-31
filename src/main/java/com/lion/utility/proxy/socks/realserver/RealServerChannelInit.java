package com.lion.utility.proxy.socks.realserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;

/**
 * real server channel初始化
 * 
 * @author lion
 *
 */
public class RealServerChannelInit extends ChannelInitializer<SocketChannel> {
	private SocksProxyServerConfig socksProxyServerConfig;
	private String logPre;
	private ChannelHandlerContext proxyServerctx;

	public RealServerChannelInit(SocksProxyServerConfig socksProxyServerConfig, String logPre, ChannelHandlerContext proxyServerctx) {
		this.socksProxyServerConfig = socksProxyServerConfig;
		this.logPre = logPre;
		this.proxyServerctx = proxyServerctx;
	}

	@Override
	protected void initChannel(SocketChannel s) throws Exception {
		// in
		// 增加将real server信息转发给proxy server的处理
		s.pipeline().addLast(new RealServerToProxyServerHandler(this.socksProxyServerConfig, this.logPre, this.proxyServerctx));
	}
}
