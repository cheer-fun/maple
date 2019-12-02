package com.maple.event.utils;

import com.maple.event.core.IEvent;
import com.maple.event.core.IReceiverInvoke;
import com.maple.event.core.ReceiverDefintion;
import javassist.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 字节码增强
 *
 * @author lfy
 * @date 2019/11/28 18:44
 **/
public class EnhanceUtils {

	private static final ClassPool classPool = ClassPool.getDefault();

	private static final AtomicInteger index = new AtomicInteger(0);


	@SuppressWarnings("unchecked")
	public static IReceiverInvoke createEnhanceReceiverInvoker(ReceiverDefintion defintion) throws Exception {
		Object bean = defintion.getBean();
		Method method = defintion.getMethod();
		String methodName = method.getName();
		Class<?> beanClazz = bean.getClass();
		// create clazz
		CtClass enhanceClazz = buildCtClass(IReceiverInvoke.class);

		// build method
		CtField ctField = new CtField(classPool.get(beanClazz.getCanonicalName()), "bean", enhanceClazz);
		ctField.setModifiers(Modifier.PRIVATE);
		enhanceClazz.addField(ctField);

		// build constructor
		String[] parameters = new String[]{beanClazz.getCanonicalName()};
		CtConstructor ctConstructor = new CtConstructor(classPool.get(parameters), enhanceClazz);
		ctConstructor.setBody("{this.bean = $1}");
		ctConstructor.setModifiers(Modifier.PUBLIC);
		enhanceClazz.addConstructor(ctConstructor);

		// build invoke method
		CtMethod ctMethod = new CtMethod(classPool.get(Void.class.getCanonicalName())
				, "invoke", classPool.get(new String[]{IEvent.class.getCanonicalName()})
				, enhanceClazz);
		ctMethod.setModifiers(Modifier.PUBLIC + Modifier.FINAL);
		StringBuilder methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("bean." + methodName + "((" + defintion.getEventClz().getCanonicalName() + ")$1);");
		methodBuilder.append("}");
		ctMethod.setBody(methodBuilder.toString());
		enhanceClazz.addMethod(ctMethod);

		// build equals method
		CtMethod ctEqualsMethod = new CtMethod(classPool.get(boolean.class.getCanonicalName())
				, "equals", classPool.get(new String[]{Object.class.getCanonicalName()})
				, enhanceClazz);
		ctMethod.setModifiers(Modifier.PUBLIC);
		methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("com.maple.event.core.IReceiverInvoke other = (com.maple.event.core.IReceiverInvoke)$1;");
		methodBuilder.append("return bean.equals(other.getBean())");
		methodBuilder.append("}");
		ctMethod.setBody(methodBuilder.toString());
		enhanceClazz.addMethod(ctMethod);

		// build hashcode method
		CtMethod ctHashCodeMethod = new CtMethod(classPool.get(int.class.getCanonicalName())
				, "hashCode", classPool.get(new String[]{})
				, enhanceClazz);
		ctMethod.setModifiers(Modifier.PUBLIC );
		methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("return bean.hashCode();");
		methodBuilder.append("}");
		ctMethod.setBody(methodBuilder.toString());
		enhanceClazz.addMethod(ctMethod);

		// build getBean method
		CtMethod ctGetBeanMethod = new CtMethod(classPool.get(Object.class.getCanonicalName())
				, "getBean", classPool.get(new String[]{})
				, enhanceClazz);
		ctMethod.setModifiers(Modifier.PUBLIC );
		methodBuilder = new StringBuilder();
		methodBuilder.append("{");
		methodBuilder.append("return this.bean");
		methodBuilder.append("}");
		ctMethod.setBody(methodBuilder.toString());
		enhanceClazz.addMethod(ctMethod);

		// build close  Then ctClass ---> Class
		Class rClz = enhanceClazz.toClass();

		// create instance of enhanceClazz
		Constructor<?> constructor = rClz.getConstructor(beanClazz);
		IReceiverInvoke result = (IReceiverInvoke) constructor.newInstance(bean);
		return result;
	}

	private static CtClass buildCtClass(Class<?> clazz) throws Exception {
		CtClass ctClass = classPool.makeClass(clazz.getSimpleName() + "Enhance" + index.incrementAndGet());
		ctClass.addInterface(classPool.get(clazz.getCanonicalName()));
		return ctClass;
	}
}
