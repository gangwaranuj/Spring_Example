package com.workmarket.api.v2.worker.fulfillment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.api.exceptions.MessageSourceApiException;
import com.workmarket.api.v2.worker.model.AssignmentApplicationDTO;
import com.workmarket.api.v2.worker.model.NegotiationDTO;
import com.workmarket.api.v2.worker.service.NegotiationService;
import com.workmarket.api.v2.worker.service.SearchService;
import com.workmarket.api.v2.worker.service.UserService;
import com.workmarket.api.v2.worker.validators.NegotiationValidator;
import com.workmarket.domains.model.User;
import com.workmarket.domains.velvetrope.guest.ThriftGuest;
import com.workmarket.domains.velvetrope.rope.AvoidScheduleConflictsThriftRope;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.wrapper.WorkNegotiationResponse;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.velvetrope.Doorman;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.assignments.WorkNegotiationForm;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Delegates to data pulling services and data marshallers to assemble response payloads for REST endpoints that
 * operate on assignments
 */
@Service
public class NegotiationFulfillmentProcessor {
	@Autowired private NegotiationService negotiationService;
	@Autowired protected WorkNegotiationService workNegotiationService;
	@Autowired private UserService userService;
	@Autowired private SearchService searchService;
	@Autowired private NegotiationValidator negotiationValidator;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired private WorkService workService;
	@Autowired private WorkResourceDAO workResourceDAO;
	@Qualifier("avoidScheduleConflictsThriftDoorman")
	@Autowired private Doorman doorman;

	public FulfillmentPayloadDTO requestBudgetIncrease(
		final String workNumber,
		final NegotiationDTO negotiationDTO) throws Exception {

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();
		final Work work = negotiationService.getWorkForNegotiation(workNumber, "mobile.budgetincrease");
		final List<String> validationErrors = negotiationValidator.validateBudgetIncrease(negotiationDTO, work);

		if (CollectionUtils.isNotEmpty(validationErrors)) {
			response.setPayload(validationErrors);
			response.setSuccessful(Boolean.FALSE);
			return response;
		}

		final WorkNegotiationResponse result = negotiationService.createBudgetIncreaseNegotiation(work, negotiationDTO);

		return processNegotiationResult(result, response);
	}

	public FulfillmentPayloadDTO requestReimbursement(
		final String workNumber,
		final NegotiationDTO negotiationDTO) throws Exception {

		final Work work = negotiationService.getWorkForNegotiation(workNumber, "mobile.reimbursement");
		final WorkNegotiationResponse result = negotiationService.createReimbursementNegotiation(work, negotiationDTO);
		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		return processNegotiationResult(result, response);
	}

	public FulfillmentPayloadDTO requestBonus(
		final String workNumber,
		final NegotiationDTO negotiationDTO) throws Exception {

		final Work work = negotiationService.getWorkForNegotiation(workNumber, "mobile.bonus");
		final WorkNegotiationResponse result = negotiationService.createBonusNegotiation(work, negotiationDTO);
		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		return processNegotiationResult(result, response);
	}

	public FulfillmentPayloadDTO applyForAssignment(
		final String workNumber,
		final AssignmentApplicationDTO applicationDTO) throws Exception {

		final User user = userService.getCurrentUser();

		final WorkResponse workResponse = negotiationService.getWorkForApply(workNumber);
		final Work work = workResponse.getWork();

		if (work == null) {
			throw new HttpException404("assignment.notfound");
		}

		final WorkNegotiationForm form = applicationDTO.toWorkNegotiationForm(user, work);

		if (!form.validScheduleNegotiation(work.getTimeZone())) {
			throw new MessageSourceApiException("assignment.apply.schedule_negotiation.error");
		}

		if (!form.validOfferExpiration(work.getTimeZone())) {
			throw new MessageSourceApiException("assignment.apply.offer_expiration.error");
		}

		if (workResponse.isWorkBundle()) {
			negotiationService.submitApplyToBundle(work, user);
		}

		List<AbstractWork> conflicts = Lists.newArrayList();
		doorman.welcome(
			new ThriftGuest(work.getBuyer()),
			new AvoidScheduleConflictsThriftRope(
				workResourceDAO,
				workService,
				work,
				user.getId(),
				conflicts
			)
		);
		if (!conflicts.isEmpty()) {
			throw new MessageSourceApiException("assignment.apply.user_has_conflicts");
		}

		final WorkNegotiationDTO dto = form.toDTO(work);

		final Boolean valid = negotiationValidator.isUserValidForWork(user, work);
		if (!valid) {
			throw new MessageSourceApiException(
				"assignment.accept.invalid_resource",
				new Object[]{"You", "are", "you", work.getCompany().getName()});
		}

		final Boolean eligible = negotiationValidator.isUserEligibleForWork(user, work);
		if (!eligible) {
			throw new MessageSourceApiException("assignment.apply.not_eligible.error");
		}

		final User worker = user;

		try {
			workNegotiationService.createApplyNegotiation(work.getId(), worker.getId(), dto);
			searchService.reindexWork(work.getId());
		} catch (final IllegalStateException ise) {
			throw new MessageSourceApiException(ise.getMessage());
		}

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		response.setSuccessful(Boolean.TRUE);
		response.addResponseResult(messageHelper.getMessage("assignment.mobile.apply.success"));

		return response;
	}

	private FulfillmentPayloadDTO processNegotiationResult(
		final WorkNegotiationResponse result,
		final FulfillmentPayloadDTO response) {

		if (result.isSuccessful()) {

			List payload = ImmutableList.of(ImmutableMap.of("id", result.getWorkNegotiationId()));
			response.setPayload(payload);
			response.setSuccessful(Boolean.TRUE);
		} else {
			response.setPayload(result.getMessages());
			response.setSuccessful(Boolean.FALSE);
		}

		return response;
	}

	/**
	 * Utility methods for unit tests
	 */
	protected void setNegotiationService(NegotiationService negotiationService) {
		this.negotiationService = negotiationService;
	}

	protected void setDoorman(Doorman doorman) {
		this.doorman = doorman;
	}

	protected void setWorkNegotiationService(WorkNegotiationService workNegotiationService) {
		this.workNegotiationService = workNegotiationService;
	}

	protected void setNegotiationValidator(NegotiationValidator negotiationValidator) {
		this.negotiationValidator = negotiationValidator;
	}

	protected void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	protected void setMessageHelper(MessageBundleHelper messageHelper) {
		this.messageHelper = messageHelper;
	}
}
