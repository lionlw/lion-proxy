package com.lion.utility.proxy.socks.proxyserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import com.lion.utility.tool.common.Tool;
import com.lion.utility.tool.log.LogLIB;

/**
 * 自定义认证代码
 * 
 * @author lion
 *
 */
class Socks5PasswordAuthRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5PasswordAuthRequest> {
	private SocksProxyServer socksProxyServer;

	public Socks5PasswordAuthRequestHandler(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5PasswordAuthRequest msg) throws Exception {
		Socks5PasswordAuthResponse response;
		if (this.checkAuth(msg.username(), msg.password())) {
			response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS);
		} else {
			response = new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE);
		}
		ctx.writeAndFlush(response);
	}

	/**
	 * 校验用户名密码
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	private boolean checkAuth(String username, String password) {
		if (!Tool.checkHaveValue(this.socksProxyServer.socksProxyServerConfig.getProxyAuthMap())) {
			return true;
		}

		String value = this.socksProxyServer.socksProxyServerConfig.getProxyAuthMap().get(username);
		if (Tool.checkHaveValue(value) && value.equals(password)) {
			return true;
		}

		LogLIB.error("checkAuth failed, username:" + username + ", password:" + password);
		return false;
	}

}