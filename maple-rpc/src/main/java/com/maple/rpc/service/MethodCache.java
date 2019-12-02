package com.maple.rpc.service;

import com.google.common.collect.Maps;
import com.maple.rpc.annotation.RpcMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lfy
 * @date 2019/11/29 15:06
 **/
@Slf4j
public class MethodCache {

	private static Map<Method, Integer> method2Uid = Maps.newConcurrentMap();

	private static ConcurrentMap<MethodInfo, Method> methods = Maps.newConcurrentMap();

	private static ConcurrentMap<Integer, Method> Uid2method = Maps.newConcurrentMap();


	public static void cacheUid(final Class<?> interfaceClass) {
		if (!interfaceClass.isInterface()) {
			return;
		}
		for (final Method method : interfaceClass.getMethods()) {
			RpcMethod annotation = method.getAnnotation(RpcMethod.class);
			if (annotation != null) {
				final int id = annotation.value();
				if (id == 0) {
					log.warn("{}的{} 全局唯一id为：0", interfaceClass, method);
				} else {
					Uid2method.compute(id, (integer, oldMethod) -> {
						if (oldMethod == null || oldMethod.equals(method)) {
							return method;
						}
						throw new RuntimeException("全局id重复：id=" + id + " oldMethod=" + oldMethod + " newMethod=" + method);
					});
					method2Uid.put(method, id);
				}

			}

		}
	}


	private static final class MethodInfo {

	}
}
