package com.workmarket.dao;

import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserPagination;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.domains.model.user.RecentUserPagination;
import com.workmarket.dto.CompanyResourcePagination;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.search.model.SearchUser;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.UserIdentityDTO;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserDAO extends PaginatableDAOInterface<User> {

	Set<User> findAllByUserNumbers(Collection<String> userIds);

	User findCreatorByAssetIdInHibernateTransaction(Long assetId);

	User findUser(String emailAddress);

	/**
	 *
	 * @param id -
	 * @return - User with no associations
	 */
	User getUser(Long id);

	SearchUser getSearchUser(Long id);

	User getUserWithRoles(Long id);

	User findUserById(Long id);

	Long findUserId(String userNumber);

	String findUserUuidById(Long id);

	Long findUserIdByUuid(String userUuid);

	Long findUserIdByEmail(String email);

	User findUserByEmail(String email);

	User findCreatorByAssetId(Long assetId);

	User findModifierByAssetId(Long assetId);

	User findCreatorByWorkLabelAssociationId(Long id);

	User findModifierByWorkLabelAssociationId(Long workLabelId);

	User findCreatorByWorkNegotiationId(Long workNegotiationId);

	String findUserNumber(Long id);

	User findUserByUserNumber(String userNumber, boolean initialize);

	User findUserByUserNumber(String masqueradeUserNumber, boolean initialize, boolean nullable);

	List<User> findInternalUsers();

	Set<User> findActiveInternalUsers();

	Set<User> findActiveInternalContractors();

	List<User> findSoleProprietors(Collection<Long> companyIds);

	Map<Long, String> findAllUsersByCompanyId(long companyId);

	Map<Long, String> findAllActiveUsersByCompanyId(long companyId);

	List<User> findAllUsersByCompanyIdAndStatus(Long companyId, List<String> userStatusTypeCodes);

	List<User> findApprovedLane3UsersByCompanyId(Long companyId);

	UserPagination findAllPendingLane33Users(UserPagination pagination);

	void updateLane3ApprovalStatus(Long userId, ApprovalStatus approvalStatus);

	Integer countAllPendingUsers();

	Integer countAllProfilesPendingApproval();

	CompanyResourcePagination findAllContractorsByCompanyId(Long companyId, CompanyResourcePagination pagination);

	CompanyResourcePagination findAllEmployeesByCompanyId(Long companyId, CompanyResourcePagination pagination);

	RecruitingCampaignUserPagination findAllRecruitingCampaignUsers(RecruitingCampaignUserPagination pagination);

	List<User> findByPhoneNumber(String phoneNumber);

	List<UserDTO> findUserDTOsOfAllActiveEmployees(final Long companyId, final boolean includeApiUsers);

	List<User> findAllActiveEmployees(Long companyId);

	UserPagination findAllActiveEmployees(Long companyId, UserPagination pagination);

	List<User> findByAclPermissionCode(Long companyId, String permissionCode);

	List<User> findAllUsersByACLRoleAndCompany(Long companyId, Long aclRoleId);

	List<UserSuggestionDTO> suggest(String prefix);

	List<User> suggest(String prefix, Long companyId, boolean internalOnly, boolean externalOnly);

	List<UserSuggestionDTO> suggestWorkers(String pattern, Long companyId, boolean internalOnly, boolean externalOnly, boolean hasMarketplace);

	UserPagination findAllUsersByUserStatusType(UserPagination pagination, final UserStatusType userStatusType);

	UserPagination findAllActiveAdminUsers(Long companyId, UserPagination pagination);

	RecentUserPagination findAllRecentUsers(RecentUserPagination pagination);

	CompanyUserPagination findAllCompanyUsers(Long companyId, CompanyUserPagination pagination, Calendar fromDate, Calendar toDate);

	Set<Long> findAllUserIdsByUserNumbers(Collection<String> userNumbers);

	Set<String> findAllUserNumbersByUserIds(Collection<Long> userIds);

	Map<String, String> findAllUserNamesByUserNumbers(Collection<String> usersToGet);

	Collection<User> findAllByUserIds(Collection<Long> userIds);

	Collection<User> findAllWithProfileAndCompanyByUserIds(Collection<Long> userIds);

    Collection<User> findAllWithCompanyByUserIds(Collection<Long> userIds);

	Map<String, Long> findActiveUserIdsByUserNumbers(Collection<String> userNumbers);

	Integer getMaxUserId();

	User findInternalOwnerByWorkId(Long workId);

	Map<String, String> getUserNamesByUserNumbers(Set<String> selectedResourcesUserNumbers);

	User findByUuid(String uuid);

	List<UserDTO> findUserDTOsByUuids(final List<String> uuid);

	Set<Long> findAllUserIdsByUuids(Collection<String> uuids);

	Set<String> findAllUserUuidsByIds(Collection<Long> ids);

	List<UserIdentityDTO> findUserIdentitiesByUuids(Collection<String> uuids);

	List<UserIdentityDTO> findUserIdentitiesByIds(final Collection<Long> ids);

	User findDeletedUsersByEmail(String email);
}
