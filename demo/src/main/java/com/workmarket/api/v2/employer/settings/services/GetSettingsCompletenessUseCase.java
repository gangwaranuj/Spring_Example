package com.workmarket.api.v2.employer.settings.services;


import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.settings.models.SettingsCompletenessDTO;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.settings.AssignmentSettingsCompletenessPredicate;
import com.workmarket.domains.model.settings.BankAccountCompletenessPredicate;
import com.workmarket.domains.model.settings.CompanyProfileCompletenessPredicate;
import com.workmarket.domains.model.settings.CompletenessPredicate;
import com.workmarket.domains.model.settings.FundingCompletenessPredicate;
import com.workmarket.domains.model.settings.SettingsActionTypes;
import com.workmarket.domains.model.settings.TaxInfoCompletenessPredicate;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
public class GetSettingsCompletenessUseCase extends
	AbstractSettingsUseCase<GetSettingsCompletenessUseCase, SettingsCompletenessDTO> {

	private SettingsCompletenessDTO settingsCompletenessDTO;
	private SettingsCompletenessDTO.Builder settingsCompletenessDTOBuilder = new SettingsCompletenessDTO.Builder();
	private Set<SettingsActionTypes> completedActions = Sets.newHashSet();
	private Set<SettingsActionTypes> missingActions = Sets.newHashSet();
	private Float percentage = 0.0f;

	@Override
	protected GetSettingsCompletenessUseCase me() {
		return this;
	}

	@Override
	protected GetSettingsCompletenessUseCase handleExceptions() {
		return this;
	}

	@Override
	protected void init() {
		getUser();
		getCompany();
	}

	@Override
	protected void process() {
		checkCompanyDetailsCompleteness();
		checkBankAccountLinkageCompleteness();
		checkFundingCompleteness();
		checkTaxInfoCompleteness();
		checkAssignmentSettingsCompleteness();
		calculatePercentage();
	}

	@Override
	protected void finish() {
		loadSettingsCompletenessDTO();
	}

	@Override
	public SettingsCompletenessDTO andReturn() {
		return settingsCompletenessDTO;
	}

	private void checkCompanyDetailsCompleteness() {
		loadCompanyProfileDTO();
		CompletenessPredicate predicate = new CompanyProfileCompletenessPredicate();
		if (predicate.test(companyProfileDTO)) {
			completedActions.add(SettingsActionTypes.OVERVIEW);
		} else {
			missingActions.add(SettingsActionTypes.OVERVIEW);
		}
	}

	private void checkBankAccountLinkageCompleteness() {
		Assert.notNull(user);
		List<? extends AbstractBankAccount> accounts = bankingService.findACHBankAccounts(user.getId());
		CompletenessPredicate bankAccountCompletenessPredicate = new BankAccountCompletenessPredicate();
		if (bankAccountCompletenessPredicate.test(accounts)) {
			completedActions.add(SettingsActionTypes.BANK);
		} else {
			missingActions.add(SettingsActionTypes.BANK);
		}
	}

	private void checkFundingCompleteness() {
		Assert.notNull(user);
		BigDecimal availableCash = accountRegisterServicePrefundImpl.calcAvailableCash(user.getId());
		CompletenessPredicate fundingCompletenessPredicate = new FundingCompletenessPredicate();
		if (fundingCompletenessPredicate.test(availableCash)) {
			completedActions.add(SettingsActionTypes.FUNDS);
		} else {
			missingActions.add(SettingsActionTypes.FUNDS);
		}
	}

	private void checkTaxInfoCompleteness() {
		Assert.notNull(user);
		taxEntity = taxService.findActiveTaxEntity(user.getId());
		CompletenessPredicate predicate = new TaxInfoCompletenessPredicate();
		if (predicate.test(taxEntity)) {
			completedActions.add(SettingsActionTypes.TAX);
		} else {
			missingActions.add(SettingsActionTypes.TAX);
		}
	}

	private void checkAssignmentSettingsCompleteness() {
		Assert.notNull(company);
		ManageMyWorkMarket mmw = company.getManageMyWorkMarket();
		CompletenessPredicate predicate = new AssignmentSettingsCompletenessPredicate();
		if (predicate.test(mmw)) {
			completedActions.add(SettingsActionTypes.ASSIGNMENT_SETTINGS);
		} else {
			missingActions.add(SettingsActionTypes.ASSIGNMENT_SETTINGS);
		}
	}

	private void calculatePercentage() {
		if (CollectionUtils.isNotEmpty(completedActions) || CollectionUtils.isNotEmpty(missingActions)) {
			percentage = 100f * completedActions.size() / (completedActions.size() + missingActions.size());
		}
	}

	private void loadSettingsCompletenessDTO(){
		settingsCompletenessDTOBuilder.setCompletedActions(completedActions)
			.setMissingActions(missingActions)
			.setPercentage(percentage);
		settingsCompletenessDTO = settingsCompletenessDTOBuilder.build();
	}
}
