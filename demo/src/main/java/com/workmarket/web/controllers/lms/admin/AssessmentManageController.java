package com.workmarket.web.controllers.lms.admin;

import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentCopyRequest;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentStatusUpdateRequest;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.core.Status;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.models.MessageBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@PreAuthorize("!principal.companyIsLocked")
@RequestMapping("/lms/manage")
public class AssessmentManageController extends BaseAssessmentAdminController {

	private static final Logger logger = LoggerFactory.getLogger(AssessmentManageController.class);

	@ModelAttribute("assessment")
	protected Assessment getAssessment(@PathVariable("id") Long id) {
		return super.getAssessment(id);
	}

	@RequestMapping(
		value = "/{id}/delete_assessment",
		method = RequestMethod.POST)
	public String deleteAssessment(
		@ModelAttribute("assessment") Assessment assessment,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		List<String> response = assessmentService.getActiveAssessmentForGroup(getCurrentUser().getCompanyId(), assessment.getId());
		if (!response.isEmpty()) {
			messageHelper.addError(
				messages,
				"lms.admin.removed.assessment_in_group_use",
				response.size() > 1 ? "s" : "",
				CollectionUtilities.join(response, ", "),
				response.size() > 1 ? "these" : "this"
			);
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/lms/manage/list_view";
		}

		List<String> reqResponse = assessmentService.getActiveAssessmentForReqSet(getCurrentUser().getCompanyId(), assessment.getId());
		if (!reqResponse.isEmpty()) {
			messageHelper.addError(messages, "lms.admin.removed.assessment_in_req_set_use", CollectionUtilities.join(reqResponse, ", "));
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/lms/manage/list_view";
		}

		List<String> workResponse = assessmentService.getActiveAssessmentForAssignment(getCurrentUser().getCompanyId(), assessment.getId());
		if (!workResponse.isEmpty()) {
			messageHelper.addError(messages, "lms.admin.removed.assessment_in_work_use", CollectionUtilities.join(workResponse, ", "));
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/lms/manage/surveys";
		}

		return updateStatus(AssessmentStatusType.REMOVED, assessment, redirectAttributes);
	}

	@RequestMapping(
		value = "/{id}/activate_assessment",
		method = RequestMethod.POST)
	public String activateAssessment(
		@ModelAttribute("assessment") Assessment assessment,
		RedirectAttributes redirectAttributes) {

		updateStatus(AssessmentStatusType.ACTIVE, assessment, redirectAttributes);

		return String.format("redirect:/lms/view/details/%s", assessment.getId());
	}

	@RequestMapping(
		value = "/{id}/deactivate_assessment",
		method = RequestMethod.POST)
	public String deactivateAssessment(
		@ModelAttribute("assessment") Assessment assessment,
		RedirectAttributes redirectAttributes) {

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		List<String> workResponse = assessmentService.getActiveAssessmentForAssignment(getCurrentUser().getCompanyId(), assessment.getId());
		if (!workResponse.isEmpty()) {
			messageHelper.addError(messages, "lms.admin.deactivate.assessment_in_work_use", CollectionUtilities.join(workResponse, ", "));
			redirectAttributes.addFlashAttribute("bundle", messages);
			return "redirect:/lms/manage/surveys";
		}

		return updateStatus(AssessmentStatusType.INACTIVE, assessment, redirectAttributes);
	}

	@RequestMapping(
		value = "/{id}/copy_assessment",
		method = RequestMethod.POST)
	public String copyAssessment(
		@ModelAttribute("assessment") Assessment assessment,
		RedirectAttributes redirectAttributes) {

		AssessmentCopyRequest request = new AssessmentCopyRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId());

		try {
			AssessmentResponse response = thriftAssessmentService.copyAssessment(request);
			return String.format("redirect:/lms/manage/step1/%s", response.getAssessment().getId());
		} catch (Exception e) {
			logger.debug("Error copying assessment", e);
			MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);
			messageHelper.addError(messages, "lms.admin.copy.failure");
			return "redirect:/lms/manage";
		}
	}

	private String updateStatus(String statusCode, Assessment assessment, RedirectAttributes redirectAttributes) {
		AssessmentStatusUpdateRequest request = new AssessmentStatusUpdateRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId())
			.setStatus(new Status().setCode(statusCode));

		MessageBundle messages = messageHelper.newFlashBundle(redirectAttributes);

		String type = (assessment.getType().equals(AssessmentType.SURVEY)) ? "survey" : "test";
		try {
			thriftAssessmentService.updateAssessmentStatus(request);
			messageHelper.addSuccess(messages, String.format("lms.admin.%s.success", statusCode), type);
		} catch (Exception e) {
			messageHelper.addError(messages, String.format("lms.admin.%s.failure", statusCode), type);
		}

		redirectAttributes.addFlashAttribute("bundle", messages);

		return (assessment.getType().equals(AssessmentType.SURVEY)) ?
			"redirect:/lms/manage/surveys" :
			"redirect:/lms/manage/list_view";
	}
}
