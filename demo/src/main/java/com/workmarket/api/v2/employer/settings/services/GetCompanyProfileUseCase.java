package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.employer.settings.models.TalentPoolDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRow;
import com.workmarket.domains.groups.model.ManagedCompanyUserGroupRowPagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("prototype")
public class GetCompanyProfileUseCase
	extends AbstractGetCompanyProfileUseCase {

	@Autowired private CompanyService companyService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private UserGroupService userGroupService;
	@Autowired private CompanyProfileService companyProfileService;


	public GetCompanyProfileUseCase(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public GetCompanyProfileUseCase() {}

	@Override
	public GetCompanyProfileUseCase execute() {
		ExtendedUserDetails userDetails = securityContextFacade.getCurrentUser();
		Company company;
		if (this.companyNumber == null) {
			company = companyService.findCompanyById(userDetails.getCompanyId());
			this.companyNumber = company.getCompanyNumber();
		} else {
			company = companyService.findCompanyByNumber(this.companyNumber);
		}
		populateBaseCompanyProfile();

		ManagedCompanyUserGroupRowPagination pagination =
			userGroupService.findCompanyGroupsOpenMembership(company.getId(), new ManagedCompanyUserGroupRowPagination());

		ArrayList<TalentPoolDTO> talentPools = new ArrayList<>();

		for (ManagedCompanyUserGroupRow group : pagination.getResults()) {
			if (group.getActiveFlag() && group.isOpenMembership() && !Constants.MY_COMPANY_FOLLOWERS.equals(group.getName())) {
				Eligibility eligibility = userGroupService.validateRequirementSets(group.getGroupId(), userDetails.getId());
				Integer requirementsMet = 0;
				for (Criterion criterion : eligibility.getCriteria()) {
					requirementsMet += criterion.isMet() ? 1 : 0;
				}

				talentPools.add(
					new TalentPoolDTO.Builder()
						.setId(group.getGroupId())
						.setName(group.getName())
						.setDescription(StringUtilities.stripHTML(group.getDescription()))
						.setRequirements(eligibility.getCriteria().size())
						.setRequirementsMet(requirementsMet)
						.setEligible(eligibility.isEligible())
						.build()
				);
			}
		}
		UserGroup followers = companyProfileService.getFollowers(company.getId(), company.getCreatorId());
		UserUserGroupAssociation association = userGroupService.findAssociationByGroupIdAndUserId(followers.getId(), userDetails.getId());
		companyProfileDTOBuilder
			.setFollowedByUser(association != null && !association.getDeleted())
			.setTalentPools(talentPools);

		return this;
	}
}
