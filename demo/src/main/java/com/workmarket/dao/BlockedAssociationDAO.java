package com.workmarket.dao;

import com.workmarket.domains.model.BlockedCompanyUserAssociationPagination;
import com.workmarket.domains.model.block.AbstractBlockedAssociation;
import com.workmarket.domains.model.block.BlockedCompanyCompanyAssociation;
import com.workmarket.domains.model.block.BlockedCompanyUserAssociation;
import com.workmarket.domains.model.block.BlockedUserCompanyAssociation;
import com.workmarket.domains.model.block.BlockedUserUserAssociation;

import java.util.List;
import java.util.Set;

public interface BlockedAssociationDAO extends DAOInterface<AbstractBlockedAssociation> {

	BlockedUserUserAssociation findActiveByUserAndBlockedUser(Long userId, Long blockedUserId);
		
	BlockedUserCompanyAssociation findByCompanyIdAndBlockedUser(Long companyId, Long blockedUserId);
	
	BlockedCompanyUserAssociation findByUserIdAndBlockedCompanyId(Long userId, Long blockedCompanyId);
	
	BlockedCompanyCompanyAssociation findByCompanyIdAndBlockedCompanyId(Long blockingCompanyId, Long blockedCompanyId);
	
	List<BlockedUserUserAssociation> findAllBlockedUsersByUser(Long userId, Long companyId);

	List<Long> findAllBlockedUserIdsByBlockingUserId(Long userId);

	List<String> findAllBlockedUserNumbersByBlockingUserId(Long userId);

	List<Long> findBlockedOrBlockedByCompanyIdsByUserId(Long userId);

	int deleteAllBlockedUserUserAssociationByBlockedUserAndCompanyId(Long userId, Long companyId);

	List<BlockedCompanyUserAssociation> findAllBlockedCompanyUserAssociationByBlockedCompanyAndBlockingCompany(Long blockingCompanyId, Long blockedCompanyId);

	/**
	 * Returns TRUE for the following cases: 
	 * 
	 * 1) The user is blocked by the company with blockCompanyId id.
	 * 2) The user's company is blocked by the company with blockCompanyId id.
	 * 3) The user blocked the company with blockCompanyId id.
	 * 
	 * @param userId
	 * @param blockCompanyId
	 * @return boolean
	 */
	boolean isUserBlockedForCompany(Long userId, Long userCompanyId, Long blockCompanyId);

	BlockedCompanyUserAssociationPagination findAllBlockedCompaniesByUser(Long userId, Long companyId, BlockedCompanyUserAssociationPagination pagination);
	/**
	 * Returns TRUE if the user (or the user's company) has blocked the company
	 * 
	 * @param userId
	 * @param blockedCompanyId
	 * @return boolean
	 */
	boolean isCompanyBlockedByUser(Long userId, Long id, Long blockedCompanyId);

	/**
	 * Returns TRUE if the user (or the user's company) is blocked by the company
	 *
	 * @param userId
	 * @param blockingCompanyId
	 * @return boolean
	 */
	boolean isUserBlockedByCompany(Long userId, Long userCompanyId, Long blockingCompanyId);

	/**
	 * Gets a list of the companies being blocked by the given blocking company id
	 * @param blockingCompanyId The company doing the blocking
	 * @return List The list of blocked companies
     */
	List<Long> listBlockedCompanies(Long blockingCompanyId);

	/**
	 * Gets a list of the users being blocked by the given blocking company id
	 * @param blockingCompanyId The company doing the blocking
	 * @return List The list of blocked users
	 */
	List<Long> listBlockedUsers(Long blockingCompanyId);


	/**
	 * Returns TRUE if the vendor is blocked by the company
	 *
	 * @param blockingCompanyId
	 * @param blockedCompanyId
	 * @return boolean
	 */
	boolean isVendorBlockedByCompany(Long blockingCompanyId, Long blockedCompanyId);
}

