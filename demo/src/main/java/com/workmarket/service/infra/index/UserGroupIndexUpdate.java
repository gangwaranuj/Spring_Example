package com.workmarket.service.infra.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.business.UserGroupService;


public class UserGroupIndexUpdate {

	private Long groupId;

	@Autowired private GroupSearchService groupSearchService;
	@Autowired private UserGroupService userGroupService;

	public UserGroupIndexUpdate() {}

	public UserGroupIndexUpdate(Long groupId) {
		this.groupId = groupId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void reindexGroup(JoinPoint joinpoint, UpdateUserGroupSearchIndex updateUserGroup) throws SecurityException, NoSuchMethodException {
		Log targetLogger = LogFactory.getLog(joinpoint.getSignature().getDeclaringTypeName());

		Long id = JoinPointUtils.getLongArgument(joinpoint, updateUserGroup.userGroupIdArgument());

		if (id > 0) {
			//does the group exists?
			UserGroup group = userGroupService.findGroupById(id);
			if (group != null) {
				targetLogger.debug(" Updating User Group Index " + id);
				if (updateUserGroup.updateUsers()) {
					groupSearchService.reindexGroupMembers(id);
				}
				groupSearchService.reindexGroup(id);
			}
		}
	}
}
