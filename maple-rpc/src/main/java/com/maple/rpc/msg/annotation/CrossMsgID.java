package com.maple.rpc.msg.annotation;

import java.lang.annotation.*;

/**
 * @author lfy
 * @date 2019/11/29 14:48
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossMsgID {

	short value();
}
