package com.maple.event.core;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author lfy
 * @date 2019/11/28 18:14
 **/
@Data
public class ReceiverDefintion implements IReceiverInvoke {
	/**
	 * 事件接收者
	 */
	private final Object bean;


	/**
	 * 事件接收方法
	 */
	private final Method method;

	/**
	 * 事件类型
	 */
	private final Class<? extends IEvent> eventClz;


	public ReceiverDefintion(Object bean, Method method, Class<? extends IEvent> eventClz) {
		this.bean = bean;
		this.method = method;
		this.eventClz = eventClz;
	}

	@Override
	public Object getBean() {
		return bean;
	}

	@Override
	public void invoke(IEvent event) {
		ReflectionUtils.makeAccessible(method);
		ReflectionUtils.invokeMethod(method,bean,event);
	}

	/**
	 * 生成代理对象
	 *
	 * @param bean
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ReceiverDefintion generateInvoker(Object bean, Method method) {
		List<ReceiverDefintion> receivers = Lists.newArrayList();
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0) {
			throw new IllegalArgumentException("class " + bean.getClass().getSimpleName()
					+ "方法必须要有参数");
		}

		for (Class<?> parameterClz : parameterTypes) {
			if (IEvent.class.isAssignableFrom(parameterClz)) {
				receivers.add(new ReceiverDefintion(bean, method, (Class<? extends IEvent>) parameterClz));
			}
		}
		if (receivers.size() > 1) {
			throw new IllegalArgumentException("class " + bean.getClass().getSimpleName()
					+ "方法只能接收一种事件");
		}
		if (receivers.isEmpty()) {
			throw new IllegalArgumentException("class " + bean.getClass().getSimpleName()
					+ "方法必须要指定接收的事件类型");
		}
		return receivers.get(0);
	}

	@Override
	public int hashCode(){
		// TODO
		return 0;
	}

	@Override
	public boolean equals(Object obj){
		// TODO
		return true;
	}


}
