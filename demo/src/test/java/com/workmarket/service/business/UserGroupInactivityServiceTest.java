package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.service.search.group.GroupSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserGroupInactivityServiceTest {

	@Mock private GroupSearchService groupSearchService;
	@Mock private UserGroupDAO userGroupDAO;
	@Mock private UserNotificationService userNotificationService;

	@InjectMocks private UserGroupInactivityService userGroupInactivityService = new UserGroupInactivityServiceImpl();

	@Test
	public void getGroupIdsToDeactivate_includes_neverRouted_and_oldRouted_not_recentlyInvited() {
		final List<Long> neverRoutedToGroupIds = ImmutableList.of(1L, 2L, 3L);
		final List<Long> oldRoutedToGroupIds = ImmutableList.of(4L, 5L, 6L);
		final List<Long> recentlyInvitedToGroupIds = ImmutableList.of(1L, 4L);
		final List<Long> expected = ImmutableList.of(2L, 3L, 5L, 6L);
		when(userGroupDAO.getGroupIdsNeverRoutedToOlderThan((Date) any())).thenReturn(neverRoutedToGroupIds);
		when(userGroupDAO.getGroupIdsNotRoutedToSince((Date) any())).thenReturn(oldRoutedToGroupIds);
		when(userGroupDAO.getGroupIdsInvitedToSince((Date) any())).thenReturn(recentlyInvitedToGroupIds);

		final List<Long> results = userGroupInactivityService.getGroupIdsToDeactivate();

		assertEquals(expected, results);
	}
}
