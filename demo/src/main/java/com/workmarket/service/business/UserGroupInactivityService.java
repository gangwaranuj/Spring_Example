package com.workmarket.service.business;

import java.util.List;

public interface UserGroupInactivityService {

	void deactivateInactiveGroups();

	List<Long> getGroupIdsToDeactivate();

}
