package com.maple.rpc.msg;

/**
 * @author lfy
 * @date 2019/12/6 14:35
 **/
public interface CrossMsgSender {

	void sendMsg(ICrossMsgBase<?> msg);

}
