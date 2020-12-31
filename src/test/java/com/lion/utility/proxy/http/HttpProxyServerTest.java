package com.lion.utility.proxy.http;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lion.utility.proxy.http.entity.HttpAddress;
import com.lion.utility.proxy.http.entity.HttpProxyServerConfig;
import com.lion.utility.proxy.http.HttpProxyServer;

public class HttpProxyServerTest {

	public static void main(String[] args) throws Exception {
		HttpAddress serverAddress = new HttpAddress();
		serverAddress.setIp("127.0.0.1");
		serverAddress.setPort(8888);

		Map<String, String> proxyAuthMap = new HashMap<>();
		proxyAuthMap.put("lion", "111");

		Set<String> proxyAuthDomainSet = new HashSet<>();
		proxyAuthDomainSet.add("pms.ismartv.com.cn");
		proxyAuthDomainSet.add("rpcmanage.admin.tvxio.com");
		proxyAuthDomainSet.add("evergreen.tvxio.com");

		HttpProxyServerConfig httpProxyServerConfig = new HttpProxyServerConfig();
		httpProxyServerConfig.setIsDebug(true);
		httpProxyServerConfig.setProxyAuthMap(proxyAuthMap);
		httpProxyServerConfig.setProxyAuthDomainSet(proxyAuthDomainSet);

		HttpProxyServer httpProxyServer = new HttpProxyServer(serverAddress);
		httpProxyServer.setHttpProxyServerConfig(httpProxyServerConfig);
		httpProxyServer.start();

		while (true) {
			Thread.sleep(1000);
		}

	}

}
