package com.lion.utility.proxy.http;

import java.util.concurrent.Executors;

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
import com.lion.utility.tool.thread.CustomThreadFactory;
import com.lion.utility.tool.log.LogLIB;

/**
 * 服务异步启动类，防止阻塞
 * 
 * @author lion
 *
 */
class HttpProxyServerStartThread implements Runnable {
	private HttpProxyServer httpProxyServer;

	public HttpProxyServerStartThread(HttpProxyServer httpProxyServer) {
		this.httpProxyServer = httpProxyServer;
	}

	@Override
	public void run() {
		EventLoopGroup acceptorGroup = null;
		EventLoopGroup ioGroup = null;

		try {
			this.httpProxyServer.bizThreadPool = Executors.newFixedThreadPool(
					this.httpProxyServer.httpProxyServerConfig.getBizThreadPoolThreadTotal(),
					new CustomThreadFactory("httpProxyServer", "biz"));

			Class<? extends ServerChannel> serverChannel = null;
			String logpre = "";

			if (Epoll.isAvailable()) {
				acceptorGroup = new EpollEventLoopGroup(this.httpProxyServer.httpProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("httpProxyServer1", true));
				ioGroup = new EpollEventLoopGroup(this.httpProxyServer.httpProxyServerConfig.getIoThreads(), new DefaultThreadFactory("httpProxyServer2", true));
				serverChannel = EpollServerSocketChannel.class;
				logpre = "start-Epoll";
			} else {
				acceptorGroup = new NioEventLoopGroup(this.httpProxyServer.httpProxyServerConfig.getAcceptorThreads(), new DefaultThreadFactory("httpProxyServer1", true));
				ioGroup = new NioEventLoopGroup(this.httpProxyServer.httpProxyServerConfig.getIoThreads(), new DefaultThreadFactory("httpProxyServer2", true));
				serverChannel = NioServerSocketChannel.class;
				logpre = "start-Nio";
			}

			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(acceptorGroup, ioGroup)
					.channel(serverChannel)
					.childHandler(new HttpProxyServerChannelInit(this.httpProxyServer))
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			// ChannelOption.SO_KEEPALIV 心跳保活机制，由于需2个小时没数据传输才会生效，此处可去除

			ChannelFuture future = bootstrap.bind(this.httpProxyServer.httpAddress.getIp(), this.httpProxyServer.httpAddress.getPort()).sync();

			LogLIB.info("httpServer " + logpre + " listen, " + this.httpProxyServer.httpAddress.getKey());

			// 阻塞
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			LogLIB.error(this.httpProxyServer.getServerInfo() + ", httpServerStartThread exception", e);
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
