package com.maple.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author lfy
 * @date 2019/11/29 14:38
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcConsumer {
}
