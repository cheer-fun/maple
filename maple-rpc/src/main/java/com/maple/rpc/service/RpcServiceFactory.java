package com.maple.rpc.service;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author lfy
 * @date 2019/11/29 15:23
 **/
public class RpcServiceFactory<T> implements FactoryBean<T> {

	private static Map<String, Object> beanCache = Maps.newConcurrentMap();

	private Class<T> interfaceType;


	public static void addBeans(String className, Object bean) {
		beanCache.put(className, bean);
	}

	public static Object getBean(String ClassName) {
		return beanCache.get(ClassName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getObject() throws Exception {
		return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, new RpcMethodInvokeHandler(interfaceType));
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
