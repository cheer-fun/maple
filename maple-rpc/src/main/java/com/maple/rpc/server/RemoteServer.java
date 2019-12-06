package com.maple.rpc.server;

import com.maple.rpc.msg.ICrossMsgBase;
import io.netty.channel.Channel;

/**
 * 远程服务
 *
 * @author lfy
 * @date 2019/12/6 14:40
 **/
public interface RemoteServer {

	void sendCrossMsg(Channel channel, ICrossMsgBase msg);
}
