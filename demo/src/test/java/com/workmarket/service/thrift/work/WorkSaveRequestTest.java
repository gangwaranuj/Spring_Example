package com.workmarket.service.thrift.work;

import com.google.common.collect.ImmutableList;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.WorkSaveRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class WorkSaveRequestTest {

	@Test
	public void addToRoutingStrategies_forList() {
		RoutingStrategy routingStrategy = mock(RoutingStrategy.class);
		List<RoutingStrategy> routingStrategies = ImmutableList.of(routingStrategy);
		WorkSaveRequest workSaveRequest = new WorkSaveRequest();
		workSaveRequest.setRoutingStrategies(null);

		workSaveRequest.addToRoutingStrategies(routingStrategies);

		assertTrue(workSaveRequest.getRoutingStrategies().containsAll(routingStrategies));
		assertTrue(routingStrategies.containsAll(workSaveRequest.getRoutingStrategies()));
	}
}
