package com.lion.utility.proxy.socks.proxyserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import com.lion.utility.tool.common.Tool;

/**
 * channel初始化
 * 
 * @author lion
 *
 */
class SocksProxyServerChannelInit extends ChannelInitializer<SocketChannel> {
	private SocksProxyServer socksProxyServer;

	public SocksProxyServerChannelInit(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	protected void initChannel(SocketChannel s) throws Exception {
		ChannelPipeline pipeline = s.pipeline();

		// out
		pipeline.addLast(Socks5ServerEncoder.DEFAULT); // 输出

		// in
		pipeline.addLast(new IPFilterFirewall(this.socksProxyServer));
		pipeline.addLast(new Socks5InitialRequestDecoder()); // 初始化
		pipeline.addLast(new Socks5InitialRequestHandler(this.socksProxyServer)); // 自定义初始化代码

		if (Tool.checkHaveValue(this.socksProxyServer.socksProxyServerConfig.getProxyAuthMap())) {
			// 需要用户认证则加入handler
			pipeline.addLast(new Socks5PasswordAuthRequestDecoder()); // 认证
			pipeline.addLast(new Socks5PasswordAuthRequestHandler(this.socksProxyServer)); // 自定义认证代码
		}

		pipeline.addLast(new Socks5CommandRequestDecoder()); // 命令请求
		pipeline.addLast(new Socks5CommandRequestHandler(this.socksProxyServer)); // 自定义命令请求代码
	}
}