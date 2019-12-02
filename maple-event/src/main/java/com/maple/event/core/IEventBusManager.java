package com.maple.event.core;

/**
 *
 * 事件总线
 *
 * @author lfy
 * @date 2019/11/28 19:46
 **/
public interface IEventBusManager {

	/**
	 * 异步提交事件，由线程池中的线程去执行事件处理方法
	 * @param event 事件
	 * @param eventName 事件名称
	 * @param dispatcherCode 分发code
	 */
	void submit(final IEvent event, final String eventName , final int dispatcherCode);


	/**
	 * 同步提交事件,由提交事件的线程去执行事件处理方法
	 * @param event 事件
	 */
	void synSubmit(final IEvent event);


}
