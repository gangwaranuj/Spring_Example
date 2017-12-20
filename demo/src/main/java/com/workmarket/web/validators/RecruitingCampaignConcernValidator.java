package com.workmarket.web.validators;

import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component("recruitingCampaignConcern")
public class RecruitingCampaignConcernValidator extends ConcernValidator {
	@Override
	public void validateEntityId(Concern concern, Errors errors) {
		RecruitingCampaignConcern recruitingCampaignConcern = (RecruitingCampaignConcern)concern;

		if (recruitingCampaignConcern == null) {
			errors.reject("NotEmpty", "Campaign Concern");
			return;
		}

		RecruitingCampaign campaign = recruitingCampaignConcern.getCampaign();

		if ((campaign == null) || (campaign.getId() == null)) {
			errors.rejectValue("campaign", "NotEmpty");
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return (RecruitingCampaignConcern.class == clazz);
	}
}
