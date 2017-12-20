package com.workmarket.api.v2.worker.service;

import com.workmarket.api.v2.worker.model.NegotiationDTO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.controllers.mobile.MobileWorkNegotiationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service interacts with existing mobile "v1" API monolith controllers to obtain assignment negotiation related data
 * and make negotiation requests. Basically a wrapper for the UGLY part of our V2 implementation. In time, this should
 * give way to service classes that call on microservices for this type of work.
 */
@Service
public class NegotiationService {

	@Autowired MobileWorkNegotiationController negotiationController;
	@Autowired WorkBundleService workBundleService;
	@Autowired WorkNegotiationService workNegotiationService;

	public Work getWorkForNegotiation(final String workNumber,
									  final String searchMessageKey) {

		return negotiationController.getWorkForActiveResourceNegotiation(workNumber,
																		 searchMessageKey);
	}

	public WorkResponse getWorkForApply(final String workNumber) {

		return negotiationController.getWorkForApplication(workNumber);
	}

	public WorkNegotiationResponse createBudgetIncreaseNegotiation(final Work work,
																   final NegotiationDTO negotiationDTO)
			throws Exception {

		return negotiationController.createBudgetIncreaseNegotiation(work.getId(),
																	 negotiationDTO.toBudgetIncreaseDTO(work));
	}

	public WorkNegotiationResponse createReimbursementNegotiation(final Work work,
																  final NegotiationDTO negotiationDTO)
			throws Exception {

		return negotiationController.createExpenseIncreaseNegotiation(work.getId(),
																	  negotiationDTO.toReimburseRequestDTO(work));
	}

	public WorkNegotiationResponse createBonusNegotiation(final Work work,
														  final NegotiationDTO negotiationDTO)
			throws Exception {

		return negotiationController.createBonusNegotiation(work.getId(),
															negotiationDTO.toBonusRequestDTO(work));
	}

	public void submitApplyToBundle(final Work work,
									final User user) {

		workBundleService.applySubmitBundle(work.getId(), user);
	}

	public WorkNegotiationResponse applyForWork(final Long workId,
												final Long userId,
												final WorkNegotiationDTO applyRequest)
			throws Exception {

		return workNegotiationService.createApplyNegotiation(workId,
															 userId,
															 applyRequest);
	}
}
