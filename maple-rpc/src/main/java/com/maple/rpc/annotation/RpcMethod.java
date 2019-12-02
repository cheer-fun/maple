package com.maple.rpc.annotation;

import com.maple.rpc.constant.RpcConstant;

import java.lang.annotation.*;

/**
 * @author lfy
 * @date 2019/11/29 14:38
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcMethod {

	/**
	 * 超时时间
	 * @return 秒
	 */
	int value() default RpcConstant.DEFAULT_TIME_OUT;
}
