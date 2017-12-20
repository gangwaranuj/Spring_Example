package com.workmarket.common.template;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.common.template.email.EmailTemplate;

public class RecruitingCampaignInvitationEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = 6101655127889810436L;
	private RecruitingCampaign campaign;
	private User invitedBy;
	private String inviteeFirstName;
	private String inviteeEmail;

	public RecruitingCampaignInvitationEmailTemplate(
			Long fromId,
			String toEmail,
			RecruitingCampaign campaign,
			User invitedBy,
			String inviteeFirstName) {
		super(fromId, toEmail);
		this.inviteeEmail = toEmail;
		this.campaign = campaign;
		this.invitedBy = invitedBy;
		this.inviteeFirstName = inviteeFirstName;
	}

	public RecruitingCampaign getCampaign() {
		return campaign;
	}

	public User getInvitedBy() {
		return invitedBy;
	}

	public String getInviteeFirstName() {
		return inviteeFirstName;
	}

	public String getInviteeEmail() {
		return inviteeEmail;
	}
}
