package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableSet;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.Status;
import com.workmarket.common.template.NotificationTemplateFactory;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.service.business.dto.ApproveWorkDTO;
import com.workmarket.service.business.dto.CloseWorkDTO;
import com.workmarket.service.business.status.CloseWorkStatus;
import com.workmarket.service.business.wrapper.CloseWorkResponse;
import com.workmarket.service.decisionflow.DecisionFlowService;
import com.workmarket.service.exception.account.InsufficientFundsException;
import com.workmarket.service.infra.notification.NotificationService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.converters.ThriftWorkToWorkFormConverter;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments")
public class WorkPayController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkPayController.class);
	@Autowired WorkSearchService workSearchService;
	@Autowired private DecisionFlowService decisionFlowService;
	@Autowired private NotificationService notificationService;
	@Autowired private NotificationTemplateFactory notificationTemplateFactory;
	@Autowired protected ThriftWorkToWorkFormConverter toWorkFormConverter;

	/**
	 * Approve an assignment for payment
	 */
	@RequestMapping(
		value="/pay/{workNumber}",
		method = POST,
		produces = TEXT_HTML_VALUE)
	@PreAuthorize("(principal.approveWorkCustomAuth OR hasAnyRole('PERMISSION_APPROVEWORK')) AND !principal.isMasquerading()")
	public String pay(
		@PathVariable("workNumber") String workNumber,
		@ModelAttribute("form") CloseWorkDTO form,
		@ModelAttribute("approval") ApproveWorkDTO approval,
		RedirectAttributes flash) {

		MessageBundle bundle = messageHelper.newFlashBundle(flash);
		if (hasFeature(getCurrentUser().getCompanyId(), Constants.MULTIPLE_APPROVALS_FEATURE)) {
			final Status status = decisionFlowService.approve(approval.getDecisionUuid(), approval.getDeciderUuid());
			if (!status.getSuccess()) {
				bundle.setErrors(status.getMessageList());
				return "redirect:/assignments#status/complete/managing";
			}
			final List<Decision> remainingDecisions = getRemainingDecisions(approval.getFlowUuid());
			if (!CollectionUtils.isEmpty(remainingDecisions)) {
				bundle.addSuccess("Assignment approved.");
				return "redirect:/assignments#status/complete/managing";
			}
		}
		return payWorkResource(bundle, workNumber, form);
	}

	private List<Decision> getRemainingDecisions(final String flowUuid) {
		final GetDoableDecisionsRequest doableDecisionsRequest = GetDoableDecisionsRequest.newBuilder()
				.setDecisionFlowUuid(flowUuid)
				.build();
		return decisionFlowService.getDoableDecisions(doableDecisionsRequest);
	}

	/**
	 * Dashboard/Ajax handler to approve an assignment for payment
	 * TODO This is dumb. We only need the work number and not a giant JSON data structure. Update dashboard JS implementation.
	 */
	@RequestMapping(
		value="/pay",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("(principal.approveWorkCustomAuth OR hasAnyRole('PERMISSION_APPROVEWORK')) AND !principal.isMasquerading()")
	public @ResponseBody AjaxResponseBuilder payViaAjax(@RequestParam("model") String modelData) {

		MessageBundle bundle = messageHelper.newBundle();

		String workNumber;
		try {
			String model = StringEscapeUtils.unescapeHtml4(modelData);
			JSONObject data = new JSONObject(model);
			workNumber = data.getString("id");
		} catch (Exception e) {
			messageHelper.addError(bundle, "assignment.pay.exception");
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(bundle.getAllMessages());
		}

		payWorkResource(
			bundle,
			workNumber,
			null
		);

		return new AjaxResponseBuilder()
			.setSuccessful(!bundle.hasErrors()).setMessages(bundle.getAllMessages());
	}

	protected String payWorkResource(MessageBundle bundle, String workNumber, CloseWorkDTO dto) {
		ExtendedUserDetails user = getCurrentUser();

		final Company company = profileService.findCompanyById(user.getCompanyId());

		if (company.isSuspended()) {
			throw new HttpException401()
				.setMessageKey("assignment.pay.suspended")
				.setRedirectUri("redirect:/home");
		}

		final WorkResponse workResponse = getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.STATUS_INFO,
			WorkRequestInfo.CONTEXT_INFO,
			WorkRequestInfo.PAYMENT_INFO,
			WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
			WorkRequestInfo.COMPANY_INFO,
			WorkRequestInfo.ACTIVE_RESOURCE_INFO,
			WorkRequestInfo.PRICING_INFO
		), ImmutableSet.of(AuthorizationContext.PAY), "pay");

		final Work work = workResponse.getWork();

		if (
			!user.getApproveWorkCustomAuth()
			|| !workResponse.getWork().getConfiguration().isSetPaymentTermsDays() && getCurrentUser().isMasquerading()) {
			throw new HttpException401()
				.setMessageKey("assignment.accept_negotiation.401");
		}

		List<Rating> oldRatings = ratingService.findRatingsForUserForWork(work.getActiveResource().getUser().getId(), work.getId());
		for (Rating rating : oldRatings) {
			ratingService.deleteRating(rating.getId());
		}

		if (!WorkStatusType.COMPLETE.equals(work.getStatus().getCode())) {
			throw new HttpException400().setMessageKey("assignment.pay.incomplete");
		}

		if (dto != null && dto.hasRating() && dto.isShareRating()) {
			dto.getRating().setRatingSharedFlag(Boolean.TRUE);
			dto.getRating().setReviewSharedFlag(Boolean.TRUE);
		}

		try {
			CloseWorkResponse response = (dto == null ? workService.closeWork(work.getId()) : workService.closeWork(work.getId(), dto));

			if(!work.getPricing().isOfflinePayment()) {
				if (CloseWorkStatus.CLOSED_AND_AUTOPAID.equals(response.getStatus())) {
					messageHelper.addNotice(bundle, "assignment.pay.closed_invoice_autopay", response.getInvoiceNumber(), response.getPaymentTermsDays());
				} else if (CloseWorkStatus.CLOSED_AND_PAID.equals(response.getStatus())) {
					messageHelper.addNotice(bundle, "assignment.pay.closed_invoice", response.getInvoiceNumber(), response.getPaymentTermsDays());
				} else if (CloseWorkStatus.CLOSED.equals(response.getStatus())) {
					messageHelper.addNotice(bundle, "assignment.pay.closed", response.getPaymentTermsDays(), "/payments/invoices/payables");
				} else if (CloseWorkStatus.CLOSED_BY_EMPLOYEE.equals(response.getStatus())) {
					messageHelper.addNotice(bundle, "assignment.pay.closed.employee");
				} else if (CloseWorkStatus.CLOSED_IMMEDIATELY.equals(response.getStatus())) {
					messageHelper.addNotice(bundle, "assignment.pay.closed.immediately");
				} else if (!response.isSuccessful()) {
					if (isNotEmpty(response.getMessages())) {
						bundle.setErrors(response.getMessages());
					} else {
						messageHelper.addNotice(bundle, "assignment.pay.exception_closing");
					}
				}
			}
			if (response.isSuccessful() &&
				!work.getPricing().isOfflinePayment() &&
				companyService.isFastFundsEnabled(work.getCompany().getId())) {

				com.workmarket.domains.work.model.Work workModel = workService.findWork(work.getId());
				WorkResource resource = workService.findActiveWorkResource(workModel.getId());
				notificationService.sendNotification(
					notificationTemplateFactory.buildWorkCompletedByBuyerNotificationTemplate(
						workModel.getBuyer().getId(), resource.getUser().getId(), workModel, true
					)
				);
			}

			bundle.setSuccess(response.getMessages());
		} catch (InsufficientFundsException e) {
			logger.info(e.getMessage());
			messageHelper.addError(bundle, "assignment.pay_now.insufficient_funds");
		} catch (Exception e) {
			logger.info(e.getMessage());
			messageHelper.addError(bundle, "assignment.pay.exception_closing");
		}

		return "redirect:/assignments#status/complete/managing";
	}
}
