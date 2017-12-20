package com.workmarket.dao.lane;

import java.util.List;
import java.util.Set;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;

public interface LaneAssociationDAO extends DAOInterface<LaneAssociation> {

	LaneAssociation addUserToLane(User user, Company company, LaneType laneType);

	LaneAssociation findActiveAssociationByUserIdAndCompanyId(Long contractorUserId, Long companyId);

	LaneAssociation findAssociationByUserIdAndCompanyId(Long contractorUserId, Long companyId, LaneType type);

	UserLaneRelationshipPagination findAllUserLaneRelationships(Long userId, UserLaneRelationshipPagination pagination);

	List<LaneAssociation> findAllAssociationsWithApprovalStatus(ApprovalStatus status);

	List<LaneAssociation> findAllAssociationsWhereUserIdIn(Long companyId, Set<Long> userIds);

	List<Long> findAllCompaniesWhereUserIsResource(Long userId, LaneType laneType);
}
