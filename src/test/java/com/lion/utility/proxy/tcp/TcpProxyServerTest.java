package com.lion.utility.proxy.tcp;

import java.util.HashMap;
import java.util.Map;

import com.lion.utility.proxy.tcp.TcpProxyServer;
import com.lion.utility.proxy.tcp.entity.TcpAddress;
import com.lion.utility.proxy.tcp.entity.TcpProxyServerConfig;

public class TcpProxyServerTest {

	public static void main(String[] args) throws Exception {
		Map<Integer, TcpAddress> proxyMappingMap = new HashMap<>();
		proxyMappingMap.put(8800, new TcpAddress("192.168.2.200", 3306));
		proxyMappingMap.put(8801, new TcpAddress("192.168.2.131", 22));

		TcpProxyServerConfig tcpProxyServerConfig = new TcpProxyServerConfig();
		tcpProxyServerConfig.setIsDebug(true);
		tcpProxyServerConfig.setProxyMappingMap(proxyMappingMap);

		TcpProxyServer tcpProxyServer = new TcpProxyServer("127.0.0.1");
		tcpProxyServer.setTcpProxyServerConfig(tcpProxyServerConfig);
		tcpProxyServer.start();

		while (true) {
			Thread.sleep(1000);
		}

	}

}
