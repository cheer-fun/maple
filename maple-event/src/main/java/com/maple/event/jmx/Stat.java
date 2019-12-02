package com.maple.event.jmx;

import com.maple.event.core.IEvent;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 事件总线状态
 *
 * @author lfy
 * @date 2019/11/28 20:20
 **/
@Data
public class Stat {

	private AtomicLong totalPackets = new AtomicLong();
	private AtomicLong totalTimes = new AtomicLong();

	private ConcurrentMap<Class<? extends IEvent>, EventStat> eventStatMap = new ConcurrentHashMap<>();


	private EventStat getEventStat(Class<? extends IEvent> clz) {
		EventStat eventStat = eventStatMap.get(clz);
		if (eventStat == null) {
			eventStat = new EventStat(clz.getName());
			EventStat pre = eventStatMap.put(clz, eventStat);
			if (pre != null) {
				eventStat = pre;
			}
		}
		return eventStat;
	}

	public void addEvent(Class<? extends IEvent> eventClazz, long use, boolean over) {
		getEventStat(eventClazz).add(use, over);
		totalPackets.incrementAndGet();
		totalTimes.addAndGet(use);
	}

	public String[] getEventInfo() {
		List<String> result = new LinkedList<>();
		long totalPacket = totalPackets.get();
		long totalTime = totalTimes.get() / 1000000;
		result.add("totalPacket : " + totalPacket);
		result.add("totalTime : " + totalTime);

		TreeSet<EventStat> sets = new TreeSet<>();

		for (Map.Entry<Class<? extends IEvent>, EventStat> entry : eventStatMap.entrySet()) {
			sets.add(entry.getValue());
		}
		for (EventStat stat : sets) {
			result.add(stat.toString());
		}
		return result.toArray(new String[result.size()]);
	}

	@Data
	public class EventStat implements Comparable<EventStat> {

		private final AtomicLong eventTimes = new AtomicLong();
		private final AtomicLong eventTotalTime = new AtomicLong();
		private final AtomicLong eventOverTime = new AtomicLong();

		private final String className;

		public EventStat(String className) {
			this.className = className;
		}

		@Override
		public int compareTo(EventStat o) {
			long result = o.eventTotalTime.get() - eventTotalTime.get();
			if (result > 0) {
				return 1;
			} else if (result < 0) {
				return -1;
			} else {
				return o.hashCode() - hashCode();
			}
		}


		public void add(long use, boolean over) {
			eventTimes.incrementAndGet();
			eventTotalTime.incrementAndGet();
			if (over) {
				eventOverTime.incrementAndGet();
			}
		}

		public String toString() {
			long totalPacket = totalPackets.get();
			long packetTimes = eventTimes.get();
			long eventTotalTimes = eventTotalTime.get() / 1000000;

			float packetTimeOps = packetTimes * 1.0f / totalPacket * 100;
			float averageTime = eventTotalTimes * 1.0f / packetTimes;
			long overTime = eventOverTime.get();

			return String
					.format("[name : %s]  [packetTimes : %d]  [packetTimeOps : %02.2f%%]  [averageTime : %02.2fms]  [totalTimes : %dms]  [overTime : %d]",
							className, packetTimes, packetTimeOps, averageTime, eventTotalTimes, overTime);
		}


	}
}
