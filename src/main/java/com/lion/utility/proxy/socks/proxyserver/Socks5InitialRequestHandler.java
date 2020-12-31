package com.lion.utility.proxy.socks.proxyserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import com.lion.utility.tool.common.Tool;
import com.lion.utility.tool.log.LogLIB;

/**
 * 自定义初始化代码
 * 
 * @author lion
 *
 */
class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
	private SocksProxyServer socksProxyServer;

	public Socks5InitialRequestHandler(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) throws Exception {
		if (msg.version().equals(SocksVersion.SOCKS5)) {
			// socks5响应初始化
			Socks5InitialResponse response;
			if (Tool.checkHaveValue(this.socksProxyServer.socksProxyServerConfig.getProxyAuthMap())) {
				// 需要用户认证
				response = new DefaultSocks5InitialResponse(Socks5AuthMethod.PASSWORD);
			} else {
				// 不需要用户认证
				response = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
			}
			ctx.writeAndFlush(response);
		} else {
			LogLIB.error("version is " + msg.version() + ", not socks5");
			ctx.close();
		}
	}

}
