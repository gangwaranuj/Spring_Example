package com.workmarket.service.thrift.work;

import com.google.common.collect.ImmutableSet;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.WorkFormRoutingAdapter;
import com.workmarket.web.forms.work.WorkFormRouting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkFormRoutingAdapterTest {

	private static long USER_ID = 1L;
	private static Set<String> FIRST_TO_ACCEPT_USER_NUMBERS = ImmutableSet.of("1", "2", "3");
	private static Set<String> NEED_TO_APPLY_USER_NUMBERS = ImmutableSet.of("9", "8", "7");
	private static Set<Long> FIRST_TO_ACCEPT_USER_IDS = ImmutableSet.of(1L, 2L, 3L);
	private static Set<Long> NEED_TO_APPLY_USER_IDS = ImmutableSet.of(9L, 8L, 7L);
	private static Set<String> FIRST_TO_ACCEPT_COMPANY_NUMBERS = ImmutableSet.of("3", "4", "5");
	private static Set<String> NEED_TO_APPLY_COMPANY_NUMBERS = ImmutableSet.of("8", "7", "6");

	@Test
	public void asRoutingStrategiesForUsers_firstToAccept() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getAssignToFirstToAcceptUserNumbers()).thenReturn(FIRST_TO_ACCEPT_USER_NUMBERS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForUsers();
		Set<String> userNumbers = routingStrategies.get(0).getRoutingUserNumbers();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(userNumbers.containsAll(FIRST_TO_ACCEPT_USER_NUMBERS));
		assertTrue(FIRST_TO_ACCEPT_USER_NUMBERS.containsAll(userNumbers));
	}

	@Test
	public void asRoutingStrategiesForUsers_needToApply() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getNeedToApplyUserNumbers()).thenReturn(NEED_TO_APPLY_USER_NUMBERS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForUsers();
		Set<String> userNumbers = routingStrategies.get(0).getRoutingUserNumbers();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(userNumbers.containsAll(NEED_TO_APPLY_USER_NUMBERS));
		assertTrue(NEED_TO_APPLY_USER_NUMBERS.containsAll(userNumbers));
	}

	@Test
	public void asRoutingStrategiesForGroups_firstToAccept() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getAssignToFirstToAcceptGroupIds()).thenReturn(FIRST_TO_ACCEPT_USER_IDS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForGroups(USER_ID);
		Set<Long> groupIds = routingStrategies.get(0).getFilter().getGroupFilter();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(groupIds.containsAll(FIRST_TO_ACCEPT_USER_IDS));
		assertTrue(FIRST_TO_ACCEPT_USER_IDS.containsAll(groupIds));
	}

	@Test
	public void asRoutingStrategiesForGroups_needToApply() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getNeedToApplyGroupIds()).thenReturn(NEED_TO_APPLY_USER_IDS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForGroups(USER_ID);
		Set<Long> groupIds = routingStrategies.get(0).getFilter().getGroupFilter();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(groupIds.containsAll(NEED_TO_APPLY_USER_IDS));
		assertTrue(NEED_TO_APPLY_USER_IDS.containsAll(groupIds));
	}

	@Test
	public void asRoutingStrategiesForVendors_firstToAccept() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getAssignToFirstToAcceptVendorCompanyNumbers()).thenReturn(FIRST_TO_ACCEPT_COMPANY_NUMBERS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForVendors();
		Set<String> vendorCompanyNumbers = routingStrategies.get(0).getVendorCompanyNumbers();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(vendorCompanyNumbers.containsAll(FIRST_TO_ACCEPT_COMPANY_NUMBERS));
		assertTrue(FIRST_TO_ACCEPT_COMPANY_NUMBERS.containsAll(vendorCompanyNumbers));
	}

	@Test
	public void asRoutingStrategiesForVendors_needToApply() {
		WorkFormRouting workFormRouting = mock(WorkFormRouting.class);
		when(workFormRouting.getNeedToApplyVendorCompanyNumbers()).thenReturn(NEED_TO_APPLY_COMPANY_NUMBERS);
		WorkFormRoutingAdapter workFormRoutingAdapter = new WorkFormRoutingAdapter(workFormRouting);

		List<RoutingStrategy> routingStrategies = workFormRoutingAdapter.asRoutingStrategiesForVendors();
		Set<String> vendorCompanyNumbers = routingStrategies.get(0).getVendorCompanyNumbers();

		assertEquals(routingStrategies.size(), 1);
		assertTrue(vendorCompanyNumbers.containsAll(NEED_TO_APPLY_COMPANY_NUMBERS));
		assertTrue(NEED_TO_APPLY_COMPANY_NUMBERS.containsAll(vendorCompanyNumbers));
	}

}
