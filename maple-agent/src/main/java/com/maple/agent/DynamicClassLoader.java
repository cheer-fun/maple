package com.maple.agent;

/**
 * @author lfy
 * @date 2019/11/28 16:21
 **/
public class DynamicClassLoader extends ClassLoader {

	/**
	 * 获取Class对象
	 * @param b
	 * @return
	 */
	public Class<?> findClass(byte[] b) {
		return defineClass(null, b, 0, b.length);
	}
}
