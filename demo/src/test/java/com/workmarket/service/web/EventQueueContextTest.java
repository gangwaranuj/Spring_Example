package com.workmarket.service.web;

import com.google.common.collect.Lists;
import com.workmarket.service.business.event.Event;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventQueueContextTest {
	public static int threadCount;
	public static Map<String, Set<Event>> threadEventMap;
	public static int count;

	@Before
	public void setup() {
		threadEventMap = new HashMap<>();
	}

	@Test
	public void shouldEmptyOnClear() {
		EventQueueContext context = new EventQueueContext();

		context.getEvents().add(new WorkUpdateSearchIndexEvent());
		context.clearEvents();
		assertTrue(context.getEvents().isEmpty());
	}

	@Test
	public void runMany() throws Exception {
		for (int i=0; i < 100; i++) {
			shouldDedupEventsAndPreventSpillageAcrossDifferentThreads();
		}
	}

	public void shouldDedupEventsAndPreventSpillageAcrossDifferentThreads() throws Exception {
		threadCount = new Random().nextInt(9) + 2; // random number between 2 to 9 inclusive
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		final List<Event> allEvents = Collections.synchronizedList(new ArrayList<Event>());

		for (int i=0; i < (threadCount+51); i++) {
			pool.submit(new Runnable() {
				@Override
				public void run() {
					EventQueueContext eventQueueContext = new EventQueueContext();

					for (int j = 0; j < new Random().nextInt(10) + 2; j++) {
						WorkUpdateSearchIndexEvent event = new WorkUpdateSearchIndexEvent();
						event.setWorkNumbers(Lists.newArrayList(UUID.randomUUID().toString()));
						eventQueueContext.getEvents().add(event);
						allEvents.add(event);

						if (j % 3 == 0) {
							eventQueueContext.getEvents().add(event); // add dupe
						}
					}

					threadEventMap.put(UUID.randomUUID().toString(), eventQueueContext.getEvents());
					eventQueueContext.clearEvents();
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.SECONDS);

		Set<Event> queuedSet = new HashSet<>();
		for (Set<Event> set : threadEventMap.values()) {
			queuedSet.addAll(set);
		}

		threadEventMap.clear();
		assertEquals(queuedSet.size(), allEvents.size());
	}
}