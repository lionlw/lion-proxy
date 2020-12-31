package com.lion.utility.proxy.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.lion.utility.tool.log.LogLIB;

/**
 * 业务处理handler（继承ChannelInboundHandlerAdapter，需手动释放bytebuf）
 * 
 * @author lion
 */
class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {
	private HttpProxyServer httpProxyServer;

	public HttpProxyServerHandler(HttpProxyServer httpProxyServer) {
		this.httpProxyServer = httpProxyServer;
	}

	/*
	 * 收到消息时，返回信息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//业务线程池处理
		this.httpProxyServer.bizThreadPool.execute(new HttpProxyServerBizTask(ctx, msg, this.httpProxyServer));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LogLIB.error(this.httpProxyServer.getServerInfo() + ", requestIp:" + CommonLIB.getNettyRequestIp(ctx) + ", exceptionCaught", cause);
		ctx.close();
	}
}