package com.maple.event.jmx;

import javax.management.MXBean;

/**
 * @author lfy
 * @date 2019/11/28 20:17
 **/
@MXBean
public interface EventMBean {

	public String[] getEventInfo();
}
