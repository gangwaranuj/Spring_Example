package com.workmarket.domains.work.service.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.configuration.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkRoutingWorkResourcesTest {

	private List<Long> potentialWorkResources = ImmutableList.of(1234L, 2345L, 3456L, 4567L);
	private List<Long> resourcesAlreadyOnWork = ImmutableList.of(5678L, 6789L, 7890L, 8901L);
	private List<Long> fullAssignmentWorkResources = new ArrayList<>();
	private List<Long> fullButOneAssignmentWorkResources = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		for (long i = 0; i < Constants.MAX_RESOURCES_PER_ASSIGNMENT; i++) {
			fullAssignmentWorkResources.add(i);
		}

		for (long i = 1; i < Constants.MAX_RESOURCES_PER_ASSIGNMENT; i++) {
			fullButOneAssignmentWorkResources.add(i);
		}
	}

	@Test
	public void exceedsMaxResourceLimit_onePotential_false() {
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(ImmutableSet.of(1L), resourcesAlreadyOnWork);

		assertFalse(workRoutingWorkResources.exceedsMaxResourceLimit());
	}

	@Test
	public void exceedsMaxResourceLimit_true() {
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(potentialWorkResources, fullAssignmentWorkResources);

		assertTrue(workRoutingWorkResources.exceedsMaxResourceLimit());
	}

	@Test
	public void exceedsMaxResourceLimit_false() {
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(potentialWorkResources, resourcesAlreadyOnWork);

		assertFalse(workRoutingWorkResources.exceedsMaxResourceLimit());
	}

	@Test
	public void getExcess_empty() {
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(potentialWorkResources, resourcesAlreadyOnWork);

		List<Long> excess = workRoutingWorkResources.getExcess();

		assertTrue(excess.isEmpty());
	}

	@Test
	public void getExcess_fullAssignment() {
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(potentialWorkResources, fullAssignmentWorkResources);

		List<Long> excess = workRoutingWorkResources.getExcess();

		assertTrue(excess.containsAll(potentialWorkResources));
		assertTrue(potentialWorkResources.containsAll(excess));
	}

	@Test
	public void getExcess_fullButOneAssignment() {
		potentialWorkResources = ImmutableList.of(1234L, 2345L, 3456L, 4567L);
		List<Long> expectedExcess = ImmutableList.of(2345L, 3456L, 4567L);
		WorkRoutingWorkResources<Long> workRoutingWorkResources =
			new WorkRoutingWorkResources(potentialWorkResources, fullButOneAssignmentWorkResources);

		List<Long> excess = workRoutingWorkResources.getExcess();

		assertTrue(excess.containsAll(expectedExcess));
		assertTrue(expectedExcess.containsAll(excess));
	}
}
