package com.workmarket.api.v2.employer.assignments.services;

import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AssignmentInformationDTO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.thrift.core.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BuildAssignmentUseCase
	extends BaseCreateAssignmentUseCase<BuildAssignmentUseCase, AssignmentInformationDTO> {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private TWorkService tWorkService;
	@Autowired private WorkService workService;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl")
	private AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired @Qualifier("accountRegisterServicePaymentTermsImpl")
	private AccountRegisterService accountRegisterServicePaymentTermsImpl;

	private WorkCostDTO cost;

	public BuildAssignmentUseCase(AssignmentDTO assignmentDTO, boolean readyToSend) {
		this.assignmentDTO = assignmentDTO;
		this.readyToSend = readyToSend;
	}

	@Override
	protected BuildAssignmentUseCase me() {
		return this;
	}

	@Override
	protected void finish() {
		generateWorkSaveRequest();

		WorkDTO dto = tWorkService.buildWorkDTO(workSaveRequest, authenticationService.getCurrentUser());

		com.workmarket.domains.work.model.Work builtWork =
			workService.buildWork(user.getId(), dto, new com.workmarket.domains.work.model.Work(), true);

		if (builtWork.hasPaymentTerms()) {
			cost = accountRegisterServicePaymentTermsImpl.calculateCostOnSentWork(builtWork);
		} else {
			cost = accountRegisterServicePrefundImpl.calculateCostOnSentWork(builtWork);
		}
	}

	@Override
	public BuildAssignmentUseCase handleExceptions() throws ValidationException {
		handleValidationException();
		return this;
	}

	@Override
	public AssignmentInformationDTO andReturn() {
		return new AssignmentInformationDTO.Builder()
			.setCost(cost.getTotalBuyerCost())
			.build();
	}
}
