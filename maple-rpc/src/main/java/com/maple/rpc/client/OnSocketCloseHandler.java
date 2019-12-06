package com.maple.rpc.client;

/**
 * @author lfy
 * @date 2019/12/6 15:38
 **/
@FunctionalInterface
public interface OnSocketCloseHandler {

	/**
	 * Client 关闭之后后续操作
	 * @param crossClient
	 */
	void onClose(CrossClient crossClient);
}
