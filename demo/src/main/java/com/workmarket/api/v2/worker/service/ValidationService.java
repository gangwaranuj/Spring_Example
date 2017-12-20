package com.workmarket.api.v2.worker.service;

import com.workmarket.domains.model.requirementset.Eligibility;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.service.business.requirementsets.EligibilityService;
import com.workmarket.thrift.work.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain user funds related data and make
 * funds related processing calls. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should
 * give way to service classes that call on microservices for this type of work.
 */
@Service
public class ValidationService {

	@Autowired WorkValidationService workValidationService;
	@Autowired EligibilityService eligibilityService;

	public Boolean isUserValidForWork(final Long userId,
									  final Long userCompanyId,
									  final Long workCompanyId) {

		return workValidationService.isWorkResourceValidForWork(userId, userCompanyId, workCompanyId);
	}

	public Boolean isUserEligibilityForWork(final Long userId,
											final Work work) {

		final Eligibility eligibility = eligibilityService.getEligibilityFor(userId, work);
		return eligibility.isEligible();
	}
}
