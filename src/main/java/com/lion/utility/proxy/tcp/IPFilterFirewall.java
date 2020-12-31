package com.lion.utility.proxy.tcp;

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
	private TcpProxyServer tcpProxyServer;

	public IPFilterFirewall(TcpProxyServer tcpProxyServer) {
		this.tcpProxyServer = tcpProxyServer;
	}

	@Override
	protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
		if (!Tool.checkHaveValue(this.tcpProxyServer.tcpProxyServerConfig.getRequestIPSet())) {
			return true;
		}

		if (this.tcpProxyServer.tcpProxyServerConfig.getRequestIPSet().contains(remoteAddress.getAddress().getHostAddress())) {
			return true;
		}

		LogLIB.error("IPFilterFirewall failed, ip:" + remoteAddress.getAddress().getHostAddress());
		return false;
	}

}