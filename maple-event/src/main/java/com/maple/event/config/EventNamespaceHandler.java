package com.maple.event.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author lfy
 * @date 2019/11/28 20:56
 **/
public class EventNamespaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser("config",new EventBeanDefinitionParser());
	}
}
