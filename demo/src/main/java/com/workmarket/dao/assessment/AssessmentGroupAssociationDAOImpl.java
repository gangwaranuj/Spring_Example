package com.workmarket.dao.assessment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.workmarket.dao.request.RequestDAO;
import com.workmarket.domains.groups.model.UserGroupRowMapper;
import com.workmarket.domains.model.request.RequestStatusType;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.utility.sql.SQLBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;

@Repository
public class AssessmentGroupAssociationDAOImpl implements AssessmentGroupAssociationDAO {

	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired private RequestDAO requestDAO;

	private static 	Set<String> allowedStatuses = ImmutableSet.of(RequestStatusType.ACCEPTED, RequestStatusType.SENT);


	private static final String[] selectFields = new String[] {
		"ug.id as user_group_id",
		"ug.open_membership as open_membership",
		"ug.searchable as searchable"
	};

	private final Matcher<UserGroupDTO> searchableAndOpenMembershipMatcher = new BaseMatcher<UserGroupDTO>() {
		@Override
		public boolean matches(Object o) {
			UserGroupDTO userGroupDTO = (UserGroupDTO)o;
			return (userGroupDTO.isSearchable() && userGroupDTO.getOpenMembership());
		}

		@Override
		public void describeTo(Description description) {}
	};

	private final Matcher<UserGroupDTO> openMembershipMatcher = new BaseMatcher<UserGroupDTO>() {
		@Override
		public boolean matches(Object o) {
			UserGroupDTO userGroupDTO = (UserGroupDTO)o;
			return userGroupDTO.getOpenMembership();
		}

		@Override
		public void describeTo(Description description) {}
	};

	public List<UserGroupDTO> findGroupAssociationsByAssessmentId(Long assessmentId) {

		SQLBuilder builder =
			new SQLBuilder()
				.addTable("user_group ug")
				.addColumns(selectFields)
				.addJoin("LEFT JOIN user_group_requirement_set_association ugrs on ugrs.user_group_id = ug.id")
				.addJoin("LEFT JOIN requirement_set rs on rs.id = ugrs.requirement_set_id")
				.addJoin("LEFT JOIN requirement r on r.requirement_set_id = rs.id")
				.addJoin("JOIN test_requirement tr on tr.id = r.id")
				.addWhereClause("ug.deleted = false")
				.addWhereClause("ug.active_flag = true")
				.addWhereClause("tr.test_id = :assessmentId");
		return
			jdbcTemplate.query(
				builder.build(),
				new MapSqlParameterSource().addValue("assessmentId", assessmentId),
				new UserGroupRowMapper()
			);
	}


	@Override
	public Boolean isUserAllowedToTakeAssessment(Long assessmentId, Long userId) {
		List<UserGroupDTO> results = findGroupAssociationsByAssessmentId(assessmentId);

		// if ANY of the groups are searchable, then user can take the test
		List<UserGroupDTO> searchables = filter(searchableAndOpenMembershipMatcher, results);

		if (searchables.size() > 0) {
			return true;
		}

		for (UserGroupDTO userGroupDTO : results) {
			List<UserGroupInvitation> invites =
				requestDAO.findUserGroupInvitationsAllStatusByInvitedUsersAndUserGroup(
					ImmutableList.of(userId), userGroupDTO.getUserGroupId()
				);
			if (invites.size() > 0) {
				for (UserGroupInvitation invite : invites) {
					if (allowedStatuses.contains(invite.getRequestStatusType().getCode()))
						return true;
				}
			}
		}

		return false;
	}
}
