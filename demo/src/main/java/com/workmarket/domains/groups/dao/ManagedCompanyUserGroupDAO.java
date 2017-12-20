package com.workmarket.domains.groups.dao;

import com.workmarket.api.v2.model.OrgUnitDTO;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ManagedCompanyUserGroupDAO {

	/**
	 * Finds all my company's groups (in the context of the userId)
	 *
	 * @param companyId
	 * @param userId
	 * @return
	 */
	ManagedCompanyUserGroupRowPagination findMyCompanyGroups(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 * Finds all my approved group memberships (in the context of the userId)
	 *
	 * @param companyId
	 * @param userId
	 * @return
	 */
	ManagedCompanyUserGroupRowPagination findMyGroupMemberships(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination);

	/**
	 * Finds all my approved and pending group memberships (in the context of the userId)
	 *
	 * @param companyId
	 * @param userId
	 * @param pagination
	 * @return
	 */
	ManagedCompanyUserGroupRowPagination findMyGroupMembershipsAndApplications(Long companyId, Long userId, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findVendorGroupMembershipsAndApplications(
		Long companyId,
		Long userId,
		Set<Long> groupIds,
		ManagedCompanyUserGroupRowPagination pagination
	);

	ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembershipByCompanyIdAndOrgUnits(
			long companyId,
			ManagedCompanyUserGroupRowPagination pagination,
			List<String> orgUnitsToFilterBy,
			boolean hasOrgStructureFeatureToggle);

	ManagedCompanyUserGroupRowPagination findCompanyGroupsOpenMembership(Long companyId, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findCompanyGroupsActiveOpenMembershipByGroupIds(Set<Long> groupIds, ManagedCompanyUserGroupRowPagination pagination);

	ManagedCompanyUserGroupRowPagination findSharedAndOwnedGroups(
			final Long companyId,
			final Long userId,
			final Boolean orgEnabled,
			final Map<String, OrgUnitDTO> orgUnitsToFilterBy,
			final ManagedCompanyUserGroupRowPagination pagination);

	List<ManagedCompanyUserGroupRow> findSharedAndOwnedGroups(Long companyId);
}
