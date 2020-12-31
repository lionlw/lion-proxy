package com.lion.utility.proxy.socks.proxyserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import com.lion.utility.tool.common.Tool;
import com.lion.utility.proxy.socks.entity.SocksAddress;
import com.lion.utility.proxy.socks.realserver.RealServerChannelInit;
import com.lion.utility.proxy.socks.realserver.RealServerChannelListener;
import com.lion.utility.tool.log.LogLIB;

/**
 * 自定义命令请求处理
 * 
 * @author lion
 *
 */
class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
	private SocksProxyServer socksProxyServer;

	public Socks5CommandRequestHandler(SocksProxyServer socksProxyServer) {
		this.socksProxyServer = socksProxyServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext proxyServerctx, DefaultSocks5CommandRequest msg) throws Exception {
		if (msg.type().equals(Socks5CommandType.CONNECT)) {
			String logPre = this.logPre(msg);

			// 连接命令处理
			LogLIB.info("connect real server start, " + logPre);

			// 校验访问是否许可ip端口
			if (!this.checkAuthAddress(msg.dstAddr(), msg.dstPort())) {
				LogLIB.error("connect real server failed, checkAuthAddress failed, " + logPre);
				proxyServerctx.close();
				return;
			}

			// 启动转发client，连接到real server
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(this.socksProxyServer.clientIOGroup)
					.channel(proxyServerctx.channel().getClass())
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new RealServerChannelInit(this.socksProxyServer.socksProxyServerConfig, logPre, proxyServerctx));

			ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
			// 监听链接事件
			future.addListener(new RealServerChannelListener(this.socksProxyServer.socksProxyServerConfig, logPre, proxyServerctx));
		} else {
			// 透传消息
			proxyServerctx.fireChannelRead(msg);
		}
	}

	/**
	 * 校验是否许可访问地址
	 * 
	 * @param ip   地址
	 * @param port 端口
	 * @return
	 */
	private boolean checkAuthAddress(String ip, int port) {
		if (!Tool.checkHaveValue(this.socksProxyServer.socksProxyServerConfig.getProxyAuthAddressSet())) {
			return true;
		}

		for (SocksAddress socksAddress : this.socksProxyServer.socksProxyServerConfig.getProxyAuthAddressSet()) {
			if (socksAddress.getKey().equals(ip + ":" + port)) {
				return true;
			}
		}

		LogLIB.error("checkAuthAddress failed, ip:" + ip + ", port:" + port);
		return false;
	}

	private String logPre(DefaultSocks5CommandRequest msg) {
		return "RealServer-" + msg.type() + " " + msg.dstAddr() + ":" + msg.dstPort();
	}
}