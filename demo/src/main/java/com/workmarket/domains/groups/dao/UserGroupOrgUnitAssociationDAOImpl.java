package com.workmarket.domains.groups.dao;

import com.google.common.collect.Lists;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.groups.model.UserGroupOrgUnitAssociation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserGroupOrgUnitAssociationDAOImpl extends AbstractDAO<UserGroupOrgUnitAssociation> implements UserGroupOrgUnitAssociationDAO {
	protected Class<UserGroupOrgUnitAssociation> getEntityClass() {
		return UserGroupOrgUnitAssociation.class;
	}

	@Override
	public UserGroupOrgUnitAssociation findUserGroupOrgUnitAssociationByUserGroupAndOrgUnitUuid(final String userGroupUuid, final String orgUnitUuid) {
		return (UserGroupOrgUnitAssociation) getFactory().getCurrentSession().createQuery(
				"FROM userGroupOrgUnitAssociation uoa " +
						"WHERE uoa.userGroupUuid= :userGroupUuid AND uoa.orgUnitUuid = :orgUnitUuid AND uoa.deleted = 0" +
						"ORDER BY created_on ASC")
				.setParameter("userGroupUuid", userGroupUuid)
				.setParameter("orgUnitUuid", orgUnitUuid)
				.uniqueResult();
	}

	@Override
	public List<UserGroupOrgUnitAssociation> findOrgUnitAssociationsByGroupId(final String userGroupUuid) {
		return innerFindAllOrgUnitAssociationsByGroupId(userGroupUuid, true);
	}

	@Override
	public List<UserGroupOrgUnitAssociation> findAllOrgUnitAssociationsByGroupId(final String userGroupUuid) {
		return innerFindAllOrgUnitAssociationsByGroupId(userGroupUuid, false);
	}

	private List<UserGroupOrgUnitAssociation> innerFindAllOrgUnitAssociationsByGroupId(final String userGroupUuid, final boolean filterOutDeleted) {
		String query = "FROM userGroupOrgUnitAssociation uoa " +
				"WHERE uoa.userGroupUuid = :userGroupUuid";

		if (filterOutDeleted) {
			query += " AND uoa.deleted = 0";
		}

		query += " ORDER BY created_on ASC";

		return (List<UserGroupOrgUnitAssociation>) getFactory().getCurrentSession()
				.createQuery(query)
				.setParameter("userGroupUuid", userGroupUuid)
				.list();
	}

	@Override
	public void setUserGroupOrgUnitAssociation(final String userGroupUuid, final List<String> orgUnitUuidsToSet) {
		final List<String> orgUnitsUuids =
				orgUnitUuidsToSet == null ? Lists.<String>newArrayList() : Lists.newArrayList(orgUnitUuidsToSet);
		final List<UserGroupOrgUnitAssociation> userGroupOrgUnitAssociations =
				findAllOrgUnitAssociationsByGroupId(userGroupUuid);

		if (CollectionUtils.isNotEmpty(userGroupOrgUnitAssociations)) {
			for (final UserGroupOrgUnitAssociation userGroupOrgUnitAssociation : userGroupOrgUnitAssociations) {
				final String preExistingAssociationOrgUnitUuid = userGroupOrgUnitAssociation.getOrgUnitUuid();
				if (orgUnitsUuids.contains(preExistingAssociationOrgUnitUuid)) {
					userGroupOrgUnitAssociation.setDeleted(false);
					orgUnitsUuids.remove(preExistingAssociationOrgUnitUuid);
				} else {
					userGroupOrgUnitAssociation.setDeleted(true);
				}
			}
		}

		final List<UserGroupOrgUnitAssociation> newUserGroupOrgUnitAssociations = Lists.newArrayList();
		for (final String orgUnitUuid : orgUnitsUuids) {
			newUserGroupOrgUnitAssociations.add(new UserGroupOrgUnitAssociation(userGroupUuid, orgUnitUuid));
		}

		if (CollectionUtils.isNotEmpty(newUserGroupOrgUnitAssociations)) {
			saveAll(newUserGroupOrgUnitAssociations);
		}
	}
}
