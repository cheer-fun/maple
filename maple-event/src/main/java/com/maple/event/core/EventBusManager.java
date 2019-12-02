package com.maple.event.core;

import com.google.common.collect.Maps;
import com.maple.event.annotation.ReceiverMethod;
import com.maple.event.utils.EnhanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 事件总线
 *
 * @author lfy
 * @date 2019/11/28 18:08
 **/
@Slf4j
public class EventBusManager implements IEventBusManager {

	private static IEventBusManager self;

	/**
	 * 事件 -->事件接收者
	 */
	private Map<Class<? extends IEvent>, List<IReceiverInvoke>> receiverDefinitionMap = Maps.newHashMap();


	public EventBusManager() {
		init();
	}


	private synchronized void init() {
		self = this;
	}

	public static IEventBusManager getInstance() {
		return self;
	}

	/**
	 * 注册事件接收者
	 *
	 * @param bean
	 */
	public void registerReceiver(Object bean) {
		Class<?> aClass = bean.getClass();
		ReflectionUtils.doWithMethods(aClass, method -> {
			if (method.isAnnotationPresent(ReceiverMethod.class)) {
				ReceiverDefintion receiverDefintion = ReceiverDefintion.generateInvoker(bean, method);
				try {
					doRegister(receiverDefintion.getEventClz(), EnhanceUtils.createEnhanceReceiverInvoker(receiverDefintion));
					log.info("事件注册成功 : " + bean.getClass().getSimpleName());
				} catch (Exception e) {
					log.error("事件注册失败 : " + bean.getClass().getSimpleName(), e);
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void doRegister(Class<? extends IEvent> eventClz, IReceiverInvoke enhanceReceiverInvoker) {
		if (!receiverDefinitionMap.containsKey(eventClz)) {
			receiverDefinitionMap.put(eventClz, new CopyOnWriteArrayList<>());
		}
		List<IReceiverInvoke> receiverInvokes = receiverDefinitionMap.get(eventClz);
		if (!receiverInvokes.contains(enhanceReceiverInvoker)) {
			receiverInvokes.add(enhanceReceiverInvoker);
		}
	}


	@Override
	public void submit(IEvent event, String eventName, int dispatcherCode) {
		// TODO 线程池异步执行
	}

	@Override
	public void synSubmit(IEvent event) {
		doSubmitEvent(event);
	}
	protected void doSubmitEvent(IEvent event) {
		List<IReceiverInvoke> receiverInvokes = receiverDefinitionMap.get(event.getClass());
		if (receiverInvokes == null || receiverInvokes.isEmpty()) {
			return;
		}
		for (IReceiverInvoke receiverInvoke : receiverInvokes) {
			try {
				receiverInvoke.invoke(event);
			} catch (Exception e) {
				log.error(event.getClass().getSimpleName() + "事件处理异常:" + receiverInvoke.getBean().getClass().getSimpleName(), e);
			}
		}
	}

	@Override
	public int hashCode() {
		// TODO
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		// TODO
		return false;
	}
}
