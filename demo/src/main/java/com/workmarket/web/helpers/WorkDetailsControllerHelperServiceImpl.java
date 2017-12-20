package com.workmarket.web.helpers;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.compliance.model.BaseComplianceCriterion;
import com.workmarket.domains.compliance.model.Compliance;
import com.workmarket.domains.compliance.service.ComplianceService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.audit.WorkActionRequest;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AcceptWorkOfferRequest;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkActionResponse;
import com.workmarket.thrift.work.WorkActionResponseCodeType;
import com.workmarket.web.controllers.assignments.BaseWorkController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

@Service
public class WorkDetailsControllerHelperServiceImpl extends BaseWorkController implements WorkDetailsControllerHelperService {
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired private ComplianceService complianceService;
	@Autowired private UserService userService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private WorkService workService;

	@Override
	public AjaxResponseBuilder acceptWorkOnBehalf(String workNumber, String note, String workerNumber, HttpServletRequest request, Model model, ExtendedUserDetails extendedUserDetails) {
		MessageBundle bundle = messageHelper.newBundle();
		ExtendedUserDetails userDetails = extendedUserDetails == null ? getCurrentUser() : extendedUserDetails;
		AjaxResponseBuilder responseBody = AjaxResponseBuilder.fail().setRedirect("/assignments/details/" + workNumber);

		if (!userService.existsBy("userNumber", workerNumber)) {
			throw new HttpException404("Unknown user number");
		}

		getWork(workNumber, ImmutableSet.of(
			WorkRequestInfo.CONTEXT_INFO
		), ImmutableSet.of(
			AuthorizationContext.ADMIN,
			AuthorizationContext.DISPATCHER
		), "details", extendedUserDetails);

		// Further authorization
		if (!workService.isAuthorizedToAccept(workNumber, userDetails.getId())) {
			throw new HttpException401()
				.setMessageKey("assignment.not_authorized")
				.setRedirectUri("/assignments/details/" + workNumber);
		}

		if (bundle.hasErrors()) {
			return responseBody.setMessages(bundle.getAllMessages());
		}

		Compliance compliance = complianceService.getComplianceFor(workerNumber, workNumber);
		if (!compliance.isCompliant()) {
			messageHelper.addError(bundle, "assignment.accept_negotiation.compliance");
			for (BaseComplianceCriterion complianceCriterion : compliance.getComplianceCriteria()) {
				if (!complianceCriterion.isMet()) {
					for (String message : complianceCriterion.getMessages()) {
						messageHelper.addError(bundle, message);
					}
				}
			}
			return responseBody.setMessages(bundle.getAllMessages());
		}

		try {
			WorkActionRequest actionRequest = new WorkActionRequest(workNumber);
			actionRequest.setResourceUserNumber(workerNumber);
			actionRequest.setOnBehalfOfUserNumber(userDetails.getUserNumber());

			if (userDetails.isMasquerading()) {
				ExtendedUserDetails masqueradeUser = userDetails.getMasqueradeUser();
				actionRequest.setMasqueradeUserNumber(masqueradeUser.getUserNumber());
			}

			AcceptWorkOfferRequest acceptWorkOfferRequest = new AcceptWorkOfferRequest();
			acceptWorkOfferRequest.setWorkAction(actionRequest);
			acceptWorkOfferRequest.setNote(note);

			WorkActionResponse actionResponse = tWorkFacadeService.acceptWorkOnBehalf(acceptWorkOfferRequest);

			if (actionResponse.getResponseCode() == WorkActionResponseCodeType.SUCCESS) {

				if (StringUtils.isNotEmpty(note)) {
					final Long workId = workService.findWorkId(workNumber);
					Assert.notNull(workId);

					User onBehalfOfUser = userService.findUserById(userDetails.getId());
					if (onBehalfOfUser == null) {
						throw new WorkActionException("There was no on behalf of user " + request);
					}

					NoteDTO noteDTO = new NoteDTO();
					noteDTO.setContent(note);
					noteDTO.setPrivileged(Boolean.TRUE);
					workNoteService.addNoteToWork(workId, noteDTO, onBehalfOfUser);
				}

				responseBody.setSuccessful(true);
				messageHelper.addSuccess(bundle, "assignment.accept.success");
				sendAcceptedWorkDetailsPDFtoResource(workNumber, userService.findUserId(workerNumber), request, model);
			} else {
				messageHelper.addError(bundle, actionResponse.getMessage());
			}
		} catch (Exception ex) {
			messageHelper.addError(bundle, "assignment.accept.exception");
		}

		return responseBody.setMessages(bundle.getAllMessages());
	}
}
