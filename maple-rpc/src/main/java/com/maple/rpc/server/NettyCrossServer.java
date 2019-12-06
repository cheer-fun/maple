package com.maple.rpc.server;

import com.maple.rpc.client.CrossClient;
import com.maple.rpc.msg.ICrossMsgBase;
import com.maple.rpc.packet.DispatchPacket;
import io.netty.channel.Channel;

/**
 * @author lfy
 * @date 2019/12/6 15:15
 **/
public class NettyCrossServer implements RemoteServer{

	@Override
	public void sendCrossMsg(Channel channel, ICrossMsgBase msg) {
		channel.writeAndFlush(getCrossPacket(msg));
	}

	public void sendCrossMsg(CrossClient crossClient , ICrossMsgBase msg){
		crossClient.sendPacket(getCrossPacket(msg));
	}

	private DispatchPacket getCrossPacket(ICrossMsgBase msg) {
		// TODO 封装转发包
		return null;
	}
}
