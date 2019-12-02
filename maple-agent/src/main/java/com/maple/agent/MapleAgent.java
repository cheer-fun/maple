package com.maple.agent;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * 代理入口
 *
 * @author lfy
 * @date 2019/11/28 16:24
 **/
@Slf4j
public class MapleAgent {

	/**
	 * @param args 热更类路径
	 * @param instrumentation
	 * 2
	 */
	public static void agentmain(String args, Instrumentation instrumentation) {
		try {
			// file ---> byte[]
			File file = new File(args);
			int length = (int)file.length();
			byte[] targetClassFile = new byte[length];
			DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
			dataInputStream.readFully(targetClassFile);
			dataInputStream.close();
			// generate Class object
			DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
			Class<?> targetClass = dynamicClassLoader.findClass(targetClassFile);
			// old Class in cur JVM
			Class<?> oldClass = Class.forName(targetClass.getName());
			// update class and reload
			ClassDefinition classDefinition = new ClassDefinition(oldClass, targetClassFile);
			instrumentation.redefineClasses(classDefinition);
			log.info("reload  " + args + "  success");
		} catch (Exception e) {
			log.error("reload  " + args + "  fail");
		}
	}
}
