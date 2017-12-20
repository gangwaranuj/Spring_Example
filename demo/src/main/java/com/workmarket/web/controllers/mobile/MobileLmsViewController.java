package com.workmarket.web.controllers.mobile;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptLimitExceededException;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.assessment.AttemptStartRequest;
import com.workmarket.thrift.assessment.AuthorizationContext;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.controllers.lms.BaseLmsController;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping({"/mobile/surveys"})
public class MobileLmsViewController extends BaseLmsController {

	private static final Logger logger = LoggerFactory.getLogger(MobileLmsViewController.class);

	@Autowired private JsonSerializationService jsonSerializationService;

	@ModelAttribute("enableBack")
	protected boolean getEnableBackButton() {
		return true;
	}

	@RequestMapping(
		value = "/take/{id}",
		method = GET)
	public String take(
		@PathVariable("id") Long assessmentId,
		@RequestParam(value = "assignment", required = false) Long workId,
		@RequestParam(value = "onBehalfOf", required = false) String onBehalfOfNumber,
		Model model) throws Exception {

		// TODO: does this expose workId to the user? It should use workNumber instead
		AssessmentResponse assessmentResponse = getAssessment(assessmentId, workId, null, AuthorizationContext.ATTEMPT);
		Assessment assessment = assessmentResponse.getAssessment();
		ExtendedUserDetails user = getCurrentUser();
		User userBehalfOf = null;
		if (onBehalfOfNumber != null) {
			userBehalfOf = userService.findUserByUserNumber(onBehalfOfNumber);
		}

		// If the user has never taken the assessment, or has completed the assessment
		// but is authorized to reattempt, start a new one.
		boolean startAttempt = false;

		if (assessmentResponse.getLatestAttempt() == null) {
			startAttempt = true;
		} else if (!AttemptStatusType.INPROGRESS.equals(assessmentResponse.getLatestAttempt().getStatus().getCode()) &&
			assessmentResponse.getAuthorizationContexts().contains(AuthorizationContext.REATTEMPT)) {
			startAttempt = true;
		}

		if (startAttempt) {
			try {
				AttemptStartRequest request = new AttemptStartRequest();
				request.setUserId(user.getId());
				request.setAssessmentId(assessmentId);

				if (workId != null) {
					request.setWorkId(workId);
				}
				if (userBehalfOf != null) {
					request.setBehalfOfId(userBehalfOf.getId());
				}

				thriftAssessmentService.startAttempt(request);
			} catch (AssessmentAttemptLimitExceededException ex) {
				MessageBundle bundle = messageHelper.newBundle();
				messageHelper.addError(bundle, "lms.view.take.attempt.limit.error");
				return "redirect:/lms/view/details/" + assessmentId;
			} catch (Exception ex) {
				logger.error("An unknown error occurred while trying to start course for userId={}, assessmentId={} and workId={}",
					new Object[]{user.getId(), assessmentId, workId}, ex);
				MessageBundle bundle = messageHelper.newBundle();
				messageHelper.addError(bundle, "lms.view.take.unknown.error");
				return "redirect:/lms/view/details/" + assessmentId;
			}
		} else if (!AttemptStatusType.INPROGRESS.equals(assessmentResponse.getLatestAttempt().getStatus().getCode())) {
			MessageBundle bundle = messageHelper.newBundle();
			messageHelper.addError(bundle, "lms.view.take.course.taken.error");
			return "redirect:/lms/view/details/" + assessmentId;
		}

		// Check if this is an assignment survey.
		if ((AssessmentType.SURVEY.equals(assessmentResponse.getAssessment().getType()) && (workId != null))) {
			WorkRequest workRequest = new WorkRequest();
			workRequest.setUserId(user.getId());
			workRequest.setWorkId(workId);

			try {
				WorkResponse workResponse = tWorkFacadeService.findWorkDetail(workRequest);
				model.addAttribute("work", workResponse); // ToDo -- this should probably be changed to workResponse.getWork() for consitency
			} catch (Exception ex) {
				logger.warn("exception caught while executing findWorkDetail for userId={} and workId={}",
					new Object[]{user.getId(), workId}, ex);
			}
		}

		model.addAttribute("assessment", assessment);
		model.addAttribute("assessmentItemsJson", jsonSerializationService.toJson(ThriftUtilities.serializeToJson(assessment.getItems(), "correct")));
		model.addAttribute("latestAttempt", assessmentResponse.getLatestAttempt());
		model.addAttribute("latestAttemptResponsesJson", assessmentResponse.isSetLatestAttempt() ? jsonSerializationService.toJson(ThriftUtilities.serializeToJson(assessmentResponse.getLatestAttempt().getResponses())) : "''");
		model.addAttribute("graded", AssessmentType.GRADED.equals(assessment.getType()));
		model.addAttribute("survey", AssessmentType.SURVEY.equals(assessment.getType()));
		model.addAttribute("timeZoneId", getCurrentUser().getTimeZoneId());
		model.addAttribute("hasAssetItems", assessment.isHasAssetItems());
		model.addAttribute("AssessmentItemTypeJSON", jsonSerializationService.toJson(ModelEnumUtilities.assessmentItemTypes));

		model.addAttribute("title", "Take Survey");

		return "mobile/pages/v2/surveys/take";
	}
}
