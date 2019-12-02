package com.maple.rpc.service;

import com.google.common.collect.Maps;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lfy
 * @date 2019/11/29 16:02
 **/
public class RpcConsumers {


	private static ConcurrentMap<Class, Object> class2Object = Maps.newConcurrentMap();


	private static Object createProxyInstance(final Class<?> interfaceClass) {
		MethodCache.cacheUid(interfaceClass);
		return class2Object.computeIfAbsent(interfaceClass, (clazz) -> Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass}, new RpcMethodInvokeHandler(interfaceClass)));
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> type) {
		Object object = class2Object.get(type);
		if (object == null) {
			object = createProxyInstance(type);
		}
		return (T) object;
	}
}
