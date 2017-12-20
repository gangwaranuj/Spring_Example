package com.workmarket.service.business;

import com.workmarket.domains.model.Invitation;

public interface InvitationService {
	/**
	 * Find an an invitation by its ID.
	 *
	 * @param invitationId
	 * @return The invitation
	 */
	Invitation findInvitationById(Long invitationId);

	Invitation findInvitationRecruitingCampaign(Long recruitingCampaignId, String emailAddress);

	/**
	 * Count the invitations with status = SENT and Today's date
	 * @param companyId
	 * @return 
	 */
	Integer countInvitationsSentTodayByCompany(Long companyId);
}