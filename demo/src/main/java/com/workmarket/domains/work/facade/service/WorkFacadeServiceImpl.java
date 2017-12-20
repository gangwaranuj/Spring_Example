package com.workmarket.domains.work.facade.service;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.CancellationReasonType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import com.workmarket.domains.work.service.WorkActionRequestFactory;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.WorkValidationService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.CancelWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.business.event.work.WorkInvoiceGenerateEvent;
import com.workmarket.service.business.event.work.WorkInvoiceSendType;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.business.integration.hooks.webhook.WebHookEventService;
import com.workmarket.service.infra.URIService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.thrift.work.WorkResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

// This operates outside a transaction
// The methods defined in here should call their
// transactional counterparts AND those counterparts
// should do the minimum work that must be done inside
// a transaction.
@Service
public class WorkFacadeServiceImpl implements WorkFacadeService {

	private static final Log logger = LogFactory.getLog(WorkFacadeServiceImpl.class);

	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private URIService uriService;
	@Autowired private EventRouter eventRouter;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private WorkActionRequestFactory workActionRequestFactory;
	@Autowired private WorkBundleService workBundleService;
	@Autowired protected WorkSearchService workSearchService;
	@Autowired private WorkValidationService workValidationService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private WebHookEventService webHookEventService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private WorkNoteService workNoteService;

	@Override
	public Work saveOrUpdateWork(Long userId, WorkDTO workDTO) {
		Work work = workService.saveOrUpdateWork(userId, workDTO);

		final List<Long> companyIdsToReindex = Lists.newArrayList();

		final Long buyerId = workDTO.getBuyerId();
		if (buyerId != null) {
			User buyerUser = userService.getUser(buyerId);
			if (buyerUser != null && buyerUser.getCompany() != null) {
				companyIdsToReindex.add(buyerUser.getCompany().getId());
			}
		}

		final User seller = userService.getUser(userId);
		if (seller != null && seller.getCompany() != null) {
			companyIdsToReindex.add(seller.getCompany().getId());
		}

		if (StringUtils.isBlank(work.getShortUrl())) {
			work.setShortUrl(uriService.getShortUrl(work.getRelativeURI()));
			workService.saveOrUpdateWork(work);
		} else {
			logger.error("Unable to get a short url: " + work.getId());
		}

		return work;
	}

	@Override
	public List<ConstraintViolation> voidWork(final long workId, final String message) {
		Assert.notNull(workId);

		final WorkActionRequest request = workActionRequestFactory.create(workId, WorkAuditType.VOID);

		final List<ConstraintViolation> violations = workStatusService.transitionToVoid(request);

		logger.warn("LOCK Released API void");

		if (violations.isEmpty()) {
			if (StringUtils.isNotBlank(message)) {
				workNoteService.addNoteToWork(workId, message);
			}

			final Long workCompanyId = workService.findBuyerCompanyId(workId);
			Assert.notNull(workCompanyId, "Work should have a companyId");

			webHookEventService.onWorkVoided(workId, workCompanyId);
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
		return violations;
	}

	@Override
	public List<ConstraintViolation> cancelWork(CancelWorkDTO cancelWorkDTO) {
		Assert.isTrue(CancellationReasonType.cancellationReasons.contains(cancelWorkDTO.getCancellationReasonTypeCode()));
		Assert.notNull(cancelWorkDTO.getPrice());
		Assert.isTrue(cancelWorkDTO.getPrice() >= 0);

		WorkActionRequest workRequest = workActionRequestFactory.create(cancelWorkDTO.getWorkId(), WorkAuditType.CANCEL);

		final Long workId = workRequest.getWorkId();
		Assert.notNull(workId, "Work ID is required");
		Work work = workService.findWork(workId);
		Assert.notNull(work, "Work is required");

		List<ConstraintViolation> violations = workValidationService.validateCancel(work);

		if (isNotEmpty(violations)) {
			return violations;
		}

		boolean isAuthorized = false;

		if (cancelWorkDTO.isPaid()) {

			WorkStatusType newWorkStatus;
			if (work.hasPaymentTerms()) {
				newWorkStatus = new WorkStatusType(WorkStatusType.CANCELLED_PAYMENT_PENDING);
			} else {
				newWorkStatus = new WorkStatusType(WorkStatusType.CANCELLED_WITH_PAY);
			}

			try {
				accountRegisterAuthorizationService.authorizeOnCompleteWork(workId, cancelWorkDTO.getPrice());
				isAuthorized = true;
				workService.transitionWorkToCanceledState(workId, cancelWorkDTO, workRequest, newWorkStatus);

			} catch (Exception e) {
				return returnError(work, isAuthorized, e, violations);
			}

		} else {
			try {
				accountRegisterAuthorizationService.voidWork(work);
				isAuthorized = true;
				workService.transitionWorkToCanceledState(workId, cancelWorkDTO, workRequest, WorkStatusType.newWorkStatusType(WorkStatusType.CANCELLED));

			} catch (Exception e) {
				return returnError(work, isAuthorized, e, violations);
			}
		}

		webHookEventService.onWorkCancelled(workId, work.getCompany().getId());
		if (WorkStatusType.CANCELLED_WITH_PAY.equals(work.getWorkStatusType().getCode())) {
			webHookEventService.onWorkPaid(workId, work.getCompany().getId());
		}

		if (work.getInvoice() != null) {
			eventRouter.sendEvent(new WorkInvoiceGenerateEvent(work.getInvoice().getId(), work.getId(), WorkInvoiceSendType.ALL));
		}

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));

		if (workRequest.getMasqueradeId() != null) {
			eventRouter.sendEvent(new UserSearchIndexEvent(workRequest.getMasqueradeId()));
		}

		return violations;
	}
	
	// Mark that something bad happened and "undo" account register stuff
	private List<ConstraintViolation> returnError(Work work, boolean isAuthorized, Exception e, List<ConstraintViolation> violations) {
		logger.error(String.format("[WorkFacadeService] There was an unexpected error canceling work %s. Rolling back changes to account register", work.getWorkNumber()), e);

		// Accept work will void and re-authorize work, if necessary
		if (isAuthorized) {
			accountRegisterAuthorizationService.acceptWork(work.getId());
		}

		violations.add(new ConstraintViolation(MessageKeys.Work.UNEXPECTED_ERROR));
		return violations;
	}
}
