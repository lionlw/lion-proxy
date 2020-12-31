package com.lion.utility.proxy.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * channel初始化
 * 
 * @author lion
 *
 */
class HttpProxyServerChannelInit extends ChannelInitializer<SocketChannel> {
	private HttpProxyServer httpProxyServer;

	public HttpProxyServerChannelInit(HttpProxyServer httpProxyServer) {
		this.httpProxyServer = httpProxyServer;
	}

	@Override
	protected void initChannel(SocketChannel s) throws Exception {
		ChannelPipeline pipeline = s.pipeline();

		// out
		pipeline.addLast(new HttpResponseEncoder());

		// in
		pipeline.addLast(new IPFilterFirewall(this.httpProxyServer));
		pipeline.addLast(new HttpRequestDecoder());
		pipeline.addLast(new HttpObjectAggregator(this.httpProxyServer.httpProxyServerConfig.getMessageRecieveMaxLength())); //此处字节数为允许传输的最大字节数（上传下载）
		pipeline.addLast(new HttpProxyServerHandler(this.httpProxyServer));
	}
}