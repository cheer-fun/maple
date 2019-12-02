package com.maple.rpc.service;

import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author lfy
 * @date 2019/11/29 15:27
 **/
@Slf4j
public class RpcMethodInvokeHandler extends AbstractInvocationHandler {

	private static final Object NULL = new Object();

	private Class<?> interfaceClass;

	private Map<Method,String> desc = Maps.newConcurrentMap();

	public RpcMethodInvokeHandler(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Override
	protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}
}
