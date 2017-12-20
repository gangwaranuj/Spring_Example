package com.workmarket.service.business;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserLaneRelationshipPagination;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.service.infra.security.LaneContext;

import java.util.List;
import java.util.Set;

public interface LaneService {

	void addUserToCompanyLane1(Long workerId, Long companyId);

	void addUserToCompanyLane2(Long workerId, Long companyId);

	void addUsersToCompanyLane2(List<Long>workerIds, Long companyId);

	void addUserToCompanyLane3(Long workerId, Long companyId);

	void addUsersToCompanyLane3(List<Long> workerIds, Long companyId);

	void removeUserFromCompanyLane(Long workerId, Long companyId);

	void updateUserCompanyLaneAssociation(Long workerId, Long companyId, LaneType newLaneType);

	public void updateLanesForUserOnGroupApply(User user, UserGroup userGroup);

	LaneAssociation findActiveAssociationByUserIdAndCompanyId(Long workerId, Long companyId);

	LaneAssociation findAssociationByUserIdAndCompanyId(Long workerId, Long companyId, LaneType laneType);

	LaneAssociation updateLaneAssociationApprovalStatus(Long workerId, Long companyId, ApprovalStatus status);

	boolean isUserPartOfLane123(Long workerId, Long companyId);

	/**
	 * Determines the lane relationships between user and company. Takes into consideration lane associations and user permissions and approvals.
	 *
	 * @param userId
	 * @param companyId
	 * @return Lane context
	 */
	LaneContext getLaneContextForUserAndCompany(Long userId, Long companyId);

	LaneContext getLaneContextForUserAndCompany(Long userId, Long companyId, boolean requireActiveAccount);

	LaneType getLaneTypeForUserAndCompany(Long userId, Long companyId);

	/**
	 * Returns all the Employees of the particular Company. Provides only last name, first name, last login and the list of assigned roles.
	 *
	 * @param companyId
	 * @param pagination
	 * @return {@link CompanyResourcePagination CompanyResourcePagination}
	 */
	CompanyResourcePagination findAllEmployeesByCompany(Long companyId, CompanyResourcePagination pagination);

	/**
	 * Returns all the Contractors of the particular Company. Provides only last name, first name, last login, YTD assignments and YTD payments.
	 *
	 * @param companyId
	 * @param pagination
	 * @return {@link CompanyResourcePagination CompanyResourcePagination}
	 */
	CompanyResourcePagination findAllContractorsByCompany(Long companyId, CompanyResourcePagination pagination);

	// WORK-977
	/**
	 * Returns all the Lane 2 and Lane 3 Associations, a particular user has with other companies. Data includes last assignment date if any, total # of assignments and company name.
	 *
	 * @param userId
	 * @param pagination
	 * @return {@link com.workmarket.domains.model.UserLaneRelationshipPagination UserLaneRelationshipPagination}
	 */
	UserLaneRelationshipPagination findAllLaneRelationshipsByUserId(Long userId, UserLaneRelationshipPagination pagination);

	void addUserToLane(Long workerUserId, Long companyId, LaneType laneType);

	/**
	 * Find all lane associations with approval status
	 *
	 * @param status
	 * @return list
	 */
	List<LaneAssociation> findAllAssociationsWithApprovalStatus(ApprovalStatus status);

	/**
	 * Find all, even deleted, associations for company and all userIds
	 *
	 * @param companyId
	 * @param userIds
	 * @return list
	 */
	Set<LaneAssociation> findAllAssociationsWhereUserIdIn(Long companyId, Set<Long> userIds);

	List<Long> findAllCompaniesWhereUserIsResource(Long userId, LaneType laneType);

	void addUserToWorkerPool(long companyId, String userNumber, String resourceUserNumber);

	void addUsersToWorkerPool(String userNumber, Set<String> resourceUserNumbers);

	boolean isLane3Active(Long userId);

}
