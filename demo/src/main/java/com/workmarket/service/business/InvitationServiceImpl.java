package com.workmarket.service.business;

import com.workmarket.dao.InvitationDAO;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.InvitationStatusType;
import com.workmarket.utility.DateUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class InvitationServiceImpl implements InvitationService {

	@Autowired
	private InvitationDAO invitationDAO;

	@Override
	public Invitation findInvitationById(Long invitationId) {
		return invitationDAO.findInvitationById(invitationId);
	}

	@Override
	public Invitation findInvitationRecruitingCampaign(Long recruitingCampaignId, String emailAddress) {
		return invitationDAO.findInvitationByRecruitingCampaign(recruitingCampaignId, emailAddress);
	}

	@Override
	public Integer countInvitationsSentTodayByCompany(Long companyId) {
		Assert.notNull(companyId);
		return invitationDAO.countInvitationsByCompanyStatusAndDate(companyId, InvitationStatusType.SENT, DateUtilities.getMidnightToday());
	}
}