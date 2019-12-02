package com.maple.event.monitor;

import com.maple.event.core.EventBusManager;
import com.maple.event.core.IEvent;
import com.maple.event.jmx.EventMBean;
import com.maple.event.jmx.Stat;
import lombok.extern.slf4j.Slf4j;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * 可监控的事件总线
 *
 * @author lfy
 * @date 2019/11/28 20:19
 **/
@Slf4j
public class MonitorEventBusManager extends EventBusManager implements EventMBean {

	private static final int maxRuntimeInNanoWithoutWarning = 1000000;

	private static final Stat stat = new Stat();

	public MonitorEventBusManager() {
		super();
		registerMonitor();
	}

	/**
	 * 注册监控到jmx
	 */
	private void registerMonitor() {
		try {
			MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName objectName = new ObjectName(this + ":type=EventBus");
			platformMBeanServer.registerMBean(this, objectName);
		} catch (Exception e) {
			log.error("事件总线监控注册失败", e);
		}
	}

	@Override
	protected void doSubmitEvent(IEvent event) {
		long start = System.nanoTime();
		super.doSubmitEvent(event);
		long use = System.nanoTime() - start;
		stat.addEvent(event.getClass(), use, use > maxRuntimeInNanoWithoutWarning);
	}

	@Override
	public String[] getEventInfo() {
		return stat.getEventInfo();
	}
}
