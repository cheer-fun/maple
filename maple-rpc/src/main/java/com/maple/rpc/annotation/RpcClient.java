package com.maple.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author lfy
 * @date 2019/11/29 14:34
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {

	String value() default "";
}
