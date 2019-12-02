package com.maple.event.config;

import com.maple.event.annotation.ReceiverMethod;
import com.maple.event.core.EventBusManager;
import com.maple.event.monitor.MonitorEventBusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lfy
 * @date 2019/11/29 10:10
 **/
@Slf4j
public class EventBeanDefinitionParser implements BeanDefinitionParser {

	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
	private static final String MONITOR_ATTRIBUTE = "monitor";

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);


	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// register beanPostProcessor
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(EventReceiverBeanPostProcessor.class);
		String beanName = StringUtils.uncapitalize(EventReceiverBeanPostProcessor.class.getSimpleName());
		parserContext.getRegistry().registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

		// register has @Receiver bean
		String[] basePackages = StringUtils.tokenizeToStringArray(
				element.getAttribute(BASE_PACKAGE_ATTRIBUTE),
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		List<String> classNames = new LinkedList<>();
		for (String basePackage : basePackages) {
			classNames.addAll(findResourceClassNames(basePackage));
		}
		try {
			for (String className : classNames) {
				Class<?> aClass = Class.forName(className);
				if (aClass.isAnnotationPresent(ReceiverMethod.class)) {
					BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(aClass);
					// register
					parserContext.getRegistry().registerBeanDefinition(
							StringUtils.uncapitalize(aClass.getSimpleName()),
							builder.getBeanDefinition());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// register EventBusManager
		boolean monitorEnable = Boolean.valueOf(element.getAttribute(MONITOR_ATTRIBUTE));
		Class<?> clz = EventBusManager.class;
		if (monitorEnable) {
			clz = MonitorEventBusManager.class;
		}
		BeanDefinitionBuilder eventBusBuilder = BeanDefinitionBuilder.rootBeanDefinition(clz);
		String eventBusBeanName = StringUtils.uncapitalize(clz.getSimpleName());
		BeanDefinition beanDefinition = eventBusBuilder.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(eventBusBeanName, beanDefinition);
		return beanDefinition;
	}

	private List<String> findResourceClassNames(String basePackage) {
		List<String> result = new LinkedList<>();
		try {
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ resolveBasePackage(basePackage)
					+ "/"
					+ DEFAULT_RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
					result.add(metadataReader.getClassMetadata().getClassName());
				}
			}
		} catch (IOException e) {
			log.error("资源解析失败", e);
		}
		return result;

	}

	private String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
	}
}
