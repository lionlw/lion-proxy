package com.lion.utility.proxy.socks.proxyserver;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import com.lion.utility.tool.common.Tool;
import com.lion.utility.tool.log.LogLIB;

/**
 * ip防火墙（用于server）
 * 
 * @author lion
 *
 */
class IPFilterFirewall extends AbstractRemoteAddressFilter<InetSocketAddress> {
	private SocksProxyServer socksProxyServer;

	public IPFilterFirewall(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
		if (!Tool.checkHaveValue(this.socksProxyServer.socksProxyServerConfig.getRequestIPSet())) {
			return true;
		}

		if (this.socksProxyServer.socksProxyServerConfig.getRequestIPSet().contains(remoteAddress.getAddress().getHostAddress())) {
			return true;
		}

		LogLIB.error("IPFilterFirewall failed, ip:" + remoteAddress.getAddress().getHostAddress());
		return false;
	}

}