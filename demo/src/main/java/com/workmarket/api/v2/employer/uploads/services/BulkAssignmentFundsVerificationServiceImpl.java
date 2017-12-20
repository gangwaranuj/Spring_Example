package com.workmarket.api.v2.employer.uploads.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AssignmentInformationDTO;
import com.workmarket.api.v2.employer.assignments.services.AssignmentInformationService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BulkAssignmentFundsVerificationServiceImpl implements BulkAssignmentFundsVerificationService {
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private CompanyService companyService;
	@Autowired private AssignmentInformationService assignmentInformationService;

	@Override
	public boolean hasSufficientFundsForAssignments(List<AssignmentDTO> assignmentDTOs) throws Exception {
		BigDecimal totalCost = BigDecimal.ZERO;
		for (AssignmentDTO assignmentDTO : assignmentDTOs) {
			AssignmentInformationDTO assignmentInformationDTO = assignmentInformationService.get(assignmentDTO);
			totalCost = totalCost.add(assignmentInformationDTO.getCost());
		}
		User user = authenticationService.getCurrentUser();
		boolean hasPaymentTerms = companyService.hasPaymentTermsEnabled(user.getCompany().getId());
		WorkAuthorizationResponse workAuthorizationResponse = accountRegisterAuthorizationService.verifyFundsForAuthorization(user, totalCost, hasPaymentTerms);

		return workAuthorizationResponse.success();
	}
}
