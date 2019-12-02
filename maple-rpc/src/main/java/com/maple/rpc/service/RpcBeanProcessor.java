package com.maple.rpc.service;

import com.maple.rpc.annotation.RpcConsumer;
import com.maple.rpc.annotation.RpcMethod;
import com.maple.rpc.annotation.RpcProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;


/**
 * @author lfy
 * @date 2019/11/29 15:01
 **/
@Slf4j
@Component
public class RpcBeanProcessor implements BeanPostProcessor , PriorityOrdered ,AnnoScannerProcessor{


	@Override
	public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
		return o;
	}

	@Override
	public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
		// handler RpcProvider
		Class<?> targetClass = AopUtils.getTargetClass(o);
		if (targetClass.isAnnotationPresent(RpcProvider.class)) {
			RpcProvider annotation = targetClass.getAnnotation(RpcProvider.class);
			Class<?> clazz = annotation.clazz();
			MethodCache.cacheUid(clazz);
			RpcServiceFactory.addBeans(clazz.getName(), o);
		}

		// handler RpcConsumer
		ReflectionUtils.doWithFields(targetClass, field -> {
			RpcConsumer rpcConsumer = field.getAnnotation(RpcConsumer.class);
			if (rpcConsumer != null) {
				Class<?> type = field.getType();
				ReflectionUtils.makeAccessible(field);
				Object consumer = RpcConsumers.get(type);
				field.set(o, consumer);
			}
		});

		return o;
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void process(CachingMetadataReaderFactory factory, Resource[] resources) throws Exception {
		for(Resource resource: resources){
			if(resource == null){
				continue;
			}
			MetadataReader metadataReader = factory.getMetadataReader(resource);
			if(metadataReader.getClassMetadata().isInterface()){
				String className = metadataReader.getClassMetadata().getClassName();
				boolean methods = metadataReader.getAnnotationMetadata().hasAnnotatedMethods(RpcMethod.class.getName());
				if(methods){
					Class<?> aClass = Class.forName(className);
					MethodCache.cacheUid(aClass);
				}
			}
		}
	}
}
