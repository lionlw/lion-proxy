package com.lion.utility.proxy.socks.proxyserver;

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
import com.lion.utility.tool.log.LogLIB;

/**
 * 服务异步启动类，防止阻塞
 * 
 * @author lion
 *
 */
class SocksProxyServerStartThread implements Runnable {
	private SocksProxyServer socksProxyServer;

	public SocksProxyServerStartThread(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	public void run() {
		EventLoopGroup acceptorGroup = null;
		EventLoopGroup ioGroup = null;

		try {
			Class<? extends ServerChannel> serverChannel = null;
			String logpre = "";

			if (Epoll.isAvailable()) {
				acceptorGroup = new EpollEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("socksProxyServer1", true));
				ioGroup = new EpollEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getIoThreads(), new DefaultThreadFactory("socksProxyServer2", true));
				serverChannel = EpollServerSocketChannel.class;
				logpre = "start-Epoll";

				this.socksProxyServer.clientIOGroup = new EpollEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getClientIOThreads(), new DefaultThreadFactory("socksProxyClient", true));
			} else {
				acceptorGroup = new NioEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("socksProxyServer1", true));
				ioGroup = new NioEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getIoThreads(), new DefaultThreadFactory("socksProxyServer2", true));
				serverChannel = NioServerSocketChannel.class;
				logpre = "start-Nio";

				this.socksProxyServer.clientIOGroup = new NioEventLoopGroup(this.socksProxyServer.socksProxyServerConfig.getClientIOThreads(), new DefaultThreadFactory("socksProxyClient", true));
			}

			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(acceptorGroup, ioGroup)
					.channel(serverChannel)
					.childHandler(new SocksProxyServerChannelInit(this.socksProxyServer))
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			// ChannelOption.SO_KEEPALIV 心跳保活机制，由于需2个小时没数据传输才会生效，此处可去除

			ChannelFuture future = bootstrap.bind(this.socksProxyServer.socksAddress.getIp(), this.socksProxyServer.socksAddress.getPort()).sync();

			LogLIB.info("socksServer " + logpre + " listen, " + this.socksProxyServer.socksAddress.getKey());

			// 阻塞
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			LogLIB.error(this.socksProxyServer.getServerInfo() + ", socksServerStartThread exception", e);
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
