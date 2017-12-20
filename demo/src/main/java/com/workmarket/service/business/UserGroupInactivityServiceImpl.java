package com.workmarket.service.business;

import com.google.common.collect.ImmutableList;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.service.search.group.GroupSearchService;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserGroupInactivityServiceImpl implements UserGroupInactivityService {

	private static final int MAX_MONTHS_SINCE_ACTIVITY_FOR_ACTIVE_GROUP = 6;
	private static final Date OLDEST_CONSIDERED_ACTIVE =
		new LocalDate().minusMonths(MAX_MONTHS_SINCE_ACTIVITY_FOR_ACTIVE_GROUP).toDate();


	@Resource private GroupSearchService groupSearchService;
	@Resource private UserGroupDAO userGroupDAO;
	@Resource private UserNotificationService userNotificationService;

	@Override
	public void deactivateInactiveGroups() {
		final List<Long> groupIdsToDeactivate = ImmutableList.copyOf(getGroupIdsToDeactivate());
		userGroupDAO.deactivateGroupIds(groupIdsToDeactivate);
		groupSearchService.reindexGroups(groupIdsToDeactivate);
		userNotificationService.onDeactivateInactiveUserGroups(groupIdsToDeactivate);
	}

	@Override
	public List<Long> getGroupIdsToDeactivate() {
		final List<Long> neverRoutedToGroupIds = userGroupDAO.getGroupIdsNeverRoutedToOlderThan(OLDEST_CONSIDERED_ACTIVE);
		final List<Long> oldRoutedToGroupIds = userGroupDAO.getGroupIdsNotRoutedToSince(OLDEST_CONSIDERED_ACTIVE);
		final List<Long> recentlyInvitedToGroupIds = userGroupDAO.getGroupIdsInvitedToSince(OLDEST_CONSIDERED_ACTIVE);

		final List<Long> groupIdsToDeactivate = new ArrayList<>();
		groupIdsToDeactivate.addAll(neverRoutedToGroupIds);
		groupIdsToDeactivate.addAll(oldRoutedToGroupIds);
		groupIdsToDeactivate.removeAll(recentlyInvitedToGroupIds);

		return groupIdsToDeactivate;
	}
}
