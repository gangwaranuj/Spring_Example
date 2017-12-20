package com.workmarket.common.template.email;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.requirementset.Criterion;

public class TalentPoolRequirementExpirationEmailTemplate extends EmailTemplate {

	private static final long serialVersionUID = -4221261976374808314L;
		Criterion criterion;
		UserGroup userGroup;
		String expirationDate;
		String verb;

		public TalentPoolRequirementExpirationEmailTemplate(Criterion criterion, UserGroup userGroup, String expirationDate, String verb) {
			super(Constants.EMAIL_USER_ID_TRANSACTIONAL, criterion.getUser().getEmail());
			this.criterion = criterion;
			this.userGroup = userGroup;
			this.expirationDate = expirationDate;
			this.verb = verb;
		}

	public Criterion getCriterion() {
		return criterion;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public String getVerb() {
		return verb;
	}
}
