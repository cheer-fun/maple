package com.maple.event.annotation;

import java.lang.annotation.*;

/**
 *
 * 事件接收者
 *
 * @author lfy
 * @date 2019/11/28 17:51
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {
		ElementType.TYPE,
		ElementType.METHOD
})
public @interface ReceiverMethod {
}
