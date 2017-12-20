package com.workmarket.service.orgstructure;

import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import com.workmarket.business.gen.Messages.OrgUnit;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.common.service.status.BaseStatus;

import java.util.List;
import java.util.Map;

public interface OrgStructureService {
	List<OrgUnit> findUserOrgChildren(long userId, long companyId);

	Map<String, List<OrgUnit>> findDirectMembershipsForUsersInCompany(final List<Long> userIds, final String companyUuid);

	boolean assignUsersFromBulk(final List<String> orgUnitPaths, final String userUuid, final String companyUuid, final String userEmail, final String orgChartUuid);

	String getOrgModeSetting(final long userId);

	BaseStatus setOrgModeSettingForUser(final long userId, final String selectedOrgUnitUuid);

	List<OrgUnitPath> getOrgModeOptions(final long userId);

	String getOrgChartUuidFromCompanyUuid(final String companyUuid);

	List<UserDTO> getOrgUnitMembers(final List<String> orgUnitUuid);

	List<OrgUnitDTO> getSubtreePaths(final long userId, final long companyId, final String orgUnitUuid);

	List<String> getUserOrgUnitUuids(final String userUuid);

	List<String> setUserMemberships(final List<String> orgUnitUuids, final String userUuid);

	List<String> getSubtreePathOrgUnitUuidsForCurrentOrgMode(final long userId, final long companyId);
}
