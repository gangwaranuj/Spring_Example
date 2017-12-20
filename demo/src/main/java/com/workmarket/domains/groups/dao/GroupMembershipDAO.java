package com.workmarket.domains.groups.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import com.workmarket.domains.groups.model.GroupMembershipPagination;
import com.workmarket.domains.model.request.Request;

import java.util.Map;
import java.util.Set;

public interface GroupMembershipDAO extends DAOInterface<Request> {

	GroupMembershipPagination findGroupMembersByUserGroupId(Long groupId, GroupMemberRequestType groupMemberRequestType, GroupMembershipPagination groupMembershipPagination, Long companyId);

	Integer countGroupMembersByUserGroupId(Long groupId, Long companyId, GroupMemberRequestType memberType);

	int countPendingMembershipsByCompany(Long companyId);

	Map<Long, String> getDerivedStatusesByGroupIdAndUserIds(long groupId, Set<Long> userId);
}
