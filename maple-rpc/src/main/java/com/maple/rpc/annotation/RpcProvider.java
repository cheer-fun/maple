package com.maple.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author lfy
 * @date 2019/11/29 14:39
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcProvider {

	Class<?> clazz();
}
