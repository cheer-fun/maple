package com.maple.event.config;

import com.maple.event.core.EventBusManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * 注册事件接受者
 *
 * @author lfy
 * @date 2019/11/28 18:01
 **/
public class EventReceiverBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware, Ordered {

	private ApplicationContext applicationContext;

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		EventBusManager eventBusManager = applicationContext.getBean(EventBusManager.class);
		try {
			eventBusManager.registerReceiver(bean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return super.postProcessAfterInstantiation(bean,beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}
}
