package com.maple.event.core;

/**
 * @author lfy
 * @date 2019/11/28 18:14
 **/
public interface IReceiverInvoke {


	public Object getBean();

	public void invoke(IEvent event);
}
