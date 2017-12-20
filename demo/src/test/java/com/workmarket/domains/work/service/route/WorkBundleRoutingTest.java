package com.workmarket.domains.work.service.route;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.infra.event.EventRouter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anySetOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class WorkBundleRoutingTest {

	@Mock AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Mock WorkBundleService workBundleService;
	@Mock WorkResourceService workResourceService;
	@Mock WorkRoutingService workRoutingService;
	@Mock EventRouter eventRouter;
	@Mock EventFactory eventFactory;
	@Mock WorkSearchService workSearchService;
	@Mock VendorService vendorService;
	@InjectMocks WorkBundleRoutingImpl workBundleRouting = spy(new WorkBundleRoutingImpl());

	private WorkBundle workBundle;
	private Work work1;
	private Work work2;
	private Work work3;

	Set<WorkResource> emptyWorkResources = Sets.newHashSet();
	Set<WorkResource> existingWorkResources = Sets.newHashSet();
	Set<Long> newlyInvitedWorkerIds = Sets.newHashSet();
	Set<Work> bundledAssignments = Sets.newHashSet();
	Long workBundleId = 999L;
	Set<Long> workIdsInBundle = Sets.newHashSet(101L, 102L, 103L);

	@Before
	public void setUp() throws Exception {
		workBundle = mock(WorkBundle.class);
		when(workBundle.isOpenable()).thenReturn(true);
		when(workBundle.isRoutable()).thenReturn(true);

		work1 = mock(Work.class);
		work2 = mock(Work.class);
		work3 = mock(Work.class);

		bundledAssignments.add(work1);
		bundledAssignments.add(work2);
		bundledAssignments.add(work3);

		when(work1.isInBundle()).thenReturn(true);
		when(work2.isInBundle()).thenReturn(true);
		when(work3.isInBundle()).thenReturn(true);

		when(work1.getId()).thenReturn(101L);
		when(work2.getId()).thenReturn(102L);
		when(work3.getId()).thenReturn(103L);

		when(accountRegisterAuthorizationService.registerWorkInBundleAuthorization(anyLong())).thenReturn(WorkAuthorizationResponse.SUCCEEDED);

		WorkResource workResource1 = mock(WorkResource.class);
		WorkResource workResource2 = mock(WorkResource.class);
		WorkResource workResource3 = mock(WorkResource.class);

		User user1 = mock(User.class);
		User user2 = mock(User.class);
		User user3 = mock(User.class);

		when(user1.getId()).thenReturn(1L);
		when(user2.getId()).thenReturn(2L);
		when(user3.getId()).thenReturn(3L);

		when(workResource1.getUser()).thenReturn(user1);
		when(workResource2.getUser()).thenReturn(user2);
		when(workResource3.getUser()).thenReturn(user3);

		when(workResource1.getScore()).thenReturn(27);
		when(workResource2.getScore()).thenReturn(45);
		when(workResource3.getScore()).thenReturn(45);

		existingWorkResources.add(workResource1);
		existingWorkResources.add(workResource2);
		existingWorkResources.add(workResource3);
		existingWorkResources.add(workResource3);

		when(workBundleService.getAllWorkIdsInBundle(workBundle)).thenReturn(workIdsInBundle);

		when(accountRegisterAuthorizationService.findRemainingAuthorizedAmountByWorkBundle(anyLong())).thenReturn(BigDecimal.TEN);
		doReturn(newlyInvitedWorkerIds).when(workBundleRouting).makeSet();
	}

	@Test
	public void isWorkBundlePendingRouting_withNonExistingBundle_returnsFalse() {
		when(workBundleService.findById(anyLong())).thenReturn(null);
		assertFalse(workBundleRouting.isWorkBundlePendingRouting(workBundleId));
	}

	@Test
	public void isWorkBundlePendingRouting_success() {
		when(workBundleService.findById(anyLong())).thenReturn(workBundle);
		assertTrue(workBundleRouting.isWorkBundlePendingRouting(workBundleId));
	}

	@Test(expected = IllegalArgumentException.class)
	public void routeWorkBundle_withNonExistingBundle_fails() throws Exception {
		workBundleRouting.routeWorkBundle(workBundleId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void routeWorkBundle_withNullBundle_fails() {
		when(workBundleService.findById(anyLong())).thenReturn(null);
		workBundleRouting.routeWorkBundle(workBundleId);
	}

	@Test
	public void routeWorkBundle_withNoWorkBundled_success() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, times(1)).openBundle(eq(workBundleId), anySetOf(Long.class));
	}

	@Test
	public void routeWorkBundle_withNoSuccess_wontOpenTheBundle() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), anyLong())).thenReturn(emptyWorkResources);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, never()).openWork(eq(workBundleId));
		verify(workRoutingService, never()).openBundle(eq(workBundleId), anySetOf(Long.class));
	}

	@Test
	public void routeWorkBundle_withSomeSuccess_success() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), eq(101L))).thenReturn(existingWorkResources);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), eq(102L))).thenReturn(emptyWorkResources);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), eq(103L))).thenReturn(existingWorkResources);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, times(1)).openBundle(eq(workBundleId), eq(newlyInvitedWorkerIds));
	}

	@Test
	public void routeWorkBundle_withNoResources_wontOpenTheBundle() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, never()).openWork(eq(workBundle.getId()));
		verify(workRoutingService, never()).openBundle(eq(workBundleId), anySetOf(Long.class));
	}

	@Test
	public void routeWorkBundle_withResourcesAndNonSuccessfulRegisterTx_success() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), anyLong())).thenReturn(existingWorkResources);
		when(accountRegisterAuthorizationService.registerWorkInBundleAuthorization(anyLong())).thenReturn(WorkAuthorizationResponse.FAILED);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, never()).openWork(eq(workBundle.getId()));
		verify(workRoutingService, never()).openBundle(eq(workBundleId), anySetOf(Long.class));
	}

	@Test
	public void routeWorkBundle_success() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);
		when(workResourceService.saveAllResourcesFromWorkToWork(eq(workBundle.getId()), anyLong())).thenReturn(existingWorkResources);

		workBundleRouting.routeWorkBundle(workBundleId);

		verify(workRoutingService, times(workBundle.getBundle().size())).openWork(any(Work.class));
		verify(workRoutingService, times(1)).openBundle(eq(workBundleId), eq(newlyInvitedWorkerIds));
		verify(workSearchService).reindexWorkAsynchronous(workIdsInBundle);
	}

	@Test
	public void routeWorkBundleToVendor_success() {
		when(workBundleService.findById(anyLong(), anyBoolean())).thenReturn(workBundle);
		when(workBundle.getBundle()).thenReturn(bundledAssignments);

		workBundleRouting.routeWorkBundleToVendor(workBundleId);

		verify(workRoutingService, times(workBundle.getBundle().size() + 1)).openWork(anyLong());
		verify(vendorService, times(3)).copyVendorsFromWorkToWork(eq(workBundleId), any(Long.class));
		verify(workSearchService).reindexWorkAsynchronous(workIdsInBundle);
	}
}
