package com.maple.rpc.msg;

/**
 * @author lfy
 * @date 2019/12/6 14:37
 **/
public interface ICrossMsgBase<T> {

	void execute(T t);
}
