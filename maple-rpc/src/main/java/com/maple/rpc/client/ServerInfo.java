package com.maple.rpc.client;

/**
 * @author lfy
 * @date 2019/12/6 17:11
 **/
public class ServerInfo {

	private String ip;

	private int port;

	private transient String ipAndPort;

	public ServerInfo() {
	}

	public ServerInfo(String host) {
		String[] split = host.split(":");
		this.ip = split[0];
		this.port = Integer.parseInt(split[1]);
		this.ipAndPort = host;
	}

	public ServerInfo(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIpAndPort() {
		if (ipAndPort == null) {
			ipAndPort = ip + ':' + port;
		}
		return ipAndPort;
	}

	public void setIpAndPort(String ipAndPort) {
		this.ipAndPort = ipAndPort;
	}
}
