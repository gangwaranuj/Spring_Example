package com.workmarket.service.business;

import com.workmarket.dto.TalentPoolMembershipDTO;
import com.workmarket.service.business.wrapper.WorkRoutingResponseSummary;
import com.workmarket.service.exception.work.WorkNotFoundException;
import com.workmarket.web.forms.work.WorkFormRouting;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface VendorService {

	void inviteVendorsToWork(WorkFormRouting workFormRouting, long workId);

	WorkRoutingResponseSummary inviteVendorsToWork(Set<String> vendorNumbersToInvite, Long workId, boolean assignToFirstToAccept, Collection<Long> groupIdsForRoute) throws WorkNotFoundException;

	List<Long> copyVendorsFromWorkToWork(Long fromWorkId, Long toWorkId);

	boolean hasInvitedAtLeastOneVendor(Long workId);

	boolean isVendorInvitedToWork(Long companyId, Long workId);

	List<Long> getDeclinedVendorIdsByWork(Long workId);

	List<String> getDeclinedVendorNumbersByWork(Long workId);

	List<Long> getVendorIdsByWork(Long workId);

	List<Long> getAssignToFirstToAcceptVendorIdsByWork(Long workId);

	List<Long> getNotDeclinedVendorIdsByWork(Long workId);

	List<String> getNotDeclinedVendorNumbersByWork(Long workId);

	List<String> getVendorNumbersByWork(Long workId);

	public int getMaxVendorsPerAssignment();

	void sendVendorsInvitedNotifications(Long workId, Set<Long> vendorIds);

	void declineWork(Long workId, Long vendorId, Long userId);

	void blockVendor(Long userId, Long blockedCompanyId);

	void unblockVendor(Long userId, Long blockedCompanyId);

	boolean isVendorBlockedByCompany(Long blockingCompanyId, Long blockedCompanyId);

	void removeAutoAssign(long companyId, long workId);

	TalentPoolMembershipDTO getAllVendorUserGroupMemberships(Long userId);
}
