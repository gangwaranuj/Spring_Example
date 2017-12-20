package com.workmarket.service.business.requirementsets;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.model.requirementset.EligibilityUser;
import com.workmarket.domains.model.requirementset.RequirementSetable;
import com.workmarket.thrift.work.Work;

import java.util.List;
import java.util.Set;

public interface EligibilityService {
	Eligibility getEligibilityFor(Long userId, Work work);

	Eligibility getEligibilityFor(Long userId, UserGroup userGroup);

	Eligibility getEligibilityFor(EligibilityUser eligibilityUser, RequirementSetable objWithRequirements);

	List<ContractVersion> getMissingContractVersions(Long groupId, ExtendedUserDetails user);

	boolean hasNonAgreementRequirements(Eligibility validation);

	Eligibility makeEligibility(Set<Criterion> criteria);
}
