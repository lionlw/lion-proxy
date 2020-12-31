package com.lion.utility.proxy.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import com.lion.utility.proxy.tcp.entity.TcpAddress;

/**
 * channel初始化
 * 
 * @author lion
 *
 */
class TcpProxyServerChannelInit extends ChannelInitializer<SocketChannel> {
	private TcpProxyServer tcpProxyServer;
	private TcpAddress realServerAddress;

	public TcpProxyServerChannelInit(TcpProxyServer tcpProxyServer, TcpAddress realServerAddress) {
		this.tcpProxyServer = tcpProxyServer;
		this.realServerAddress = realServerAddress;
	}

	@Override
	protected void initChannel(SocketChannel s) throws Exception {
		ChannelPipeline pipeline = s.pipeline();

		// in
		pipeline.addLast(new IPFilterFirewall(this.tcpProxyServer));
		pipeline.addLast(new TcpProxyServerHandler(this.tcpProxyServer.tcpProxyServerConfig, this.realServerAddress));
	}
}