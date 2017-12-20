package com.workmarket.web.helpers;

import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 2/15/12
 * Time: 4:29 PM
 *
 * This is a master helper class for any form helpers that are shared between controllers.
 */
@Component
public class FormHelperImpl implements FormHelper {

	@Autowired UserGroupService userGroupService;

	@Override
	public Map<String, String> getCompanyUserGroupNamesForSelect(Long userId, Long companyId, Boolean prefixWithCompanyId) {
		ManagedCompanyUserGroupRowPagination pagination = new ManagedCompanyUserGroupRowPagination();

		pagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		pagination.setSortColumn(ManagedCompanyUserGroupRowPagination.SORTS.GROUP_NAME);
		pagination.setReturnAllRows(true);

		ManagedCompanyUserGroupRowPagination resultPagination = userGroupService.findMyCompanyGroups(userId, pagination);
		List<ManagedCompanyUserGroupRow> results = resultPagination.getResults();
		Map<String, String> groups = CollectionUtilities.newStringMap();

		if (CollectionUtils.isNotEmpty(results)) {
			String keyPrefix = (prefixWithCompanyId && companyId != null) ? "" : companyId + "_";

			for (ManagedCompanyUserGroupRow row : results) {
				groups.put(keyPrefix + row.getGroupId(), row.getName());
			}
		}

		return groups;
	}
}
