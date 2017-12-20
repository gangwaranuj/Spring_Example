package com.workmarket.service.web;

import com.workmarket.service.business.event.Event;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EventQueueContext {
	public class Container {
		private boolean throttleEvents;
		private Set<Event> events = new HashSet<>();

		public Set<Event> getEvents() {
			return events;
		}

		public void setEvents(Set<Event> events) {
			this.events = events;
		}

		public boolean isThrottleEvents() {
			return throttleEvents;
		}

		public void setThrottleEvents(boolean throttleEvents) {
			this.throttleEvents = throttleEvents;
		}
	}

	static ThreadLocal<Container> threadLocalStore = new ThreadLocal<>();

	/** Should only be used by things that need to execute things in background threads. */
	public Container getContainer() {
		if (threadLocalStore.get() == null) {
			threadLocalStore.set(new Container());
		}

		return threadLocalStore.get();
	}

	/** Should only be used by things that need to execute things in background threads. */
	public void setContainer(Container container) {
		threadLocalStore.set(container);
	}

	public Set<Event> getEvents() {
		return getContainer().getEvents();
	}

	public void clearEvents() {
		threadLocalStore.remove();
	}

	public boolean isThrottlingEvents() {
		return getContainer().isThrottleEvents();
	}

	public void stopThrottlingEvents() {
		getContainer().setThrottleEvents(false);
	}

	public void startThrottlingEvents() {
		getContainer().setThrottleEvents(true);
	}
}
