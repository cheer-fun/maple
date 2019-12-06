package com.maple.rpc.client;

import com.maple.rpc.packet.DispatchPacket;

/**
 * @author lfy
 * @date 2019/12/6 15:34
 **/
public interface CrossClient extends Runnable {

	/**
	 * 发送消息
	 * TODO packet向上加一层
	 * @param packet
	 * @return
	 */
	boolean sendPacket(DispatchPacket packet);

	/**
	 * 客户端是否关闭
	 *
	 * @return
	 */
	boolean isActive();
}
