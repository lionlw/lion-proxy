package com.lion.utility.proxy.socks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lion.utility.proxy.socks.entity.SocksAddress;
import com.lion.utility.proxy.socks.entity.SocksProxyServerConfig;
import com.lion.utility.proxy.socks.proxyserver.SocksProxyServer;

public class SocksProxyServerTest {

	public static void main(String[] args) throws Exception {
		SocksAddress serverAddress = new SocksAddress();
		serverAddress.setIp("127.0.0.1");
		serverAddress.setPort(8800);

		Map<String, String> proxyAuthMap = new HashMap<>();
		proxyAuthMap.put("lion", "111");

		Set<SocksAddress> proxyAuthAddressSet = new HashSet<>();
		proxyAuthAddressSet.add(new SocksAddress("127.0.0.1", 80));

		SocksProxyServerConfig socksProxyServerConfig = new SocksProxyServerConfig();
		socksProxyServerConfig.setIsDebug(true);
		socksProxyServerConfig.setProxyAuthMap(proxyAuthMap);
		// socksProxyServerConfig.setProxyAuthAddressSet(proxyAuthAddressSet);

		SocksProxyServer socksProxyServer = new SocksProxyServer(serverAddress);
		socksProxyServer.setSocksProxyServerConfig(socksProxyServerConfig);
		socksProxyServer.start();

		while (true) {
			Thread.sleep(1000);
		}

	}

}
