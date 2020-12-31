package com.lion.utility.proxy.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import com.lion.utility.proxy.tcp.entity.TcpAddress;
import com.lion.utility.tool.log.LogLIB;

/**
 * 服务异步启动类，防止阻塞
 * 
 * @author lion
 *
 */
class TcpProxyServerStartThread implements Runnable {
	private TcpProxyServer tcpProxyServer;
	private int proxyServerPort;
	private TcpAddress realServerAddress;

	public TcpProxyServerStartThread(TcpProxyServer tcpProxyServer, int proxyServerPort, TcpAddress realServerAddress) {
		this.tcpProxyServer = tcpProxyServer;
		this.proxyServerPort = proxyServerPort;
		this.realServerAddress = realServerAddress;
	}

	@Override
	public void run() {
		EventLoopGroup acceptorGroup = null;
		EventLoopGroup ioGroup = null;

		try {
			Class<? extends ServerChannel> serverChannel = null;
			String logpre = "";

			if (Epoll.isAvailable()) {
				acceptorGroup = new EpollEventLoopGroup(this.tcpProxyServer.tcpProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("tcpProxyServer1", true));
				ioGroup = new EpollEventLoopGroup(this.tcpProxyServer.tcpProxyServerConfig.getIoThreads(), new DefaultThreadFactory("tcpProxyServer2", true));
				serverChannel = EpollServerSocketChannel.class;
				logpre = "start-Epoll";
			} else {
				acceptorGroup = new NioEventLoopGroup(this.tcpProxyServer.tcpProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("tcpProxyServer1", true));
				ioGroup = new NioEventLoopGroup(this.tcpProxyServer.tcpProxyServerConfig.getIoThreads(), new DefaultThreadFactory("tcpProxyServer2", true));
				serverChannel = NioServerSocketChannel.class;
				logpre = "start-Nio";
			}

			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(acceptorGroup, ioGroup)
					.channel(serverChannel)
					.childHandler(new TcpProxyServerChannelInit(this.tcpProxyServer, this.realServerAddress))
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.AUTO_READ, false); // 设置手动读取消息
			// ChannelOption.SO_KEEPALIV 心跳保活机制，由于需2个小时没数据传输才会生效，此处可去除

			ChannelFuture future = bootstrap.bind(this.tcpProxyServer.ip, this.proxyServerPort).sync();

			LogLIB.info("tcpServer " + logpre + " listen, " + this.tcpProxyServer.ip + " " + this.proxyServerPort);

			// 阻塞
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			LogLIB.error(this.tcpProxyServer.ip + " " + this.proxyServerPort + ", tcpServerStartThread exception", e);
		} finally {
			if (acceptorGroup != null) {
				acceptorGroup.shutdownGracefully();
			}
			if (ioGroup != null) {
				ioGroup.shutdownGracefully();
			}
		}
	}
}
