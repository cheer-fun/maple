package com.maple.rpc.msg.impl;

import com.maple.rpc.msg.CrossMsgSender;
import com.maple.rpc.msg.ICrossMsgBase;
import io.netty.channel.Channel;

/**
 * @author lfy
 * @date 2019/12/6 14:35
 **/
public class ChannelCrossMsgSender  implements CrossMsgSender {
	private final Channel channel;
	@Override
	public void sendMsg(ICrossMsgBase<?> msg) {
		// TODO
	}

	public ChannelCrossMsgSender(Channel channel){
		this.channel = channel;
	}
}
