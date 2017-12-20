package com.workmarket.web.controllers.lms.admin;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.Profile;
import com.workmarket.service.business.IndustryService;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentSaveRequest;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.assessment.NotificationType;
import com.workmarket.thrift.assessment.NotificationTypeConfiguration;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Skill;
import com.workmarket.thrift.core.Status;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.editors.NullSafeNumberEditor;
import com.workmarket.web.editors.ThriftEnumEditor;
import com.workmarket.web.editors.ThriftObjectEditor;
import com.workmarket.web.editors.ThriftStatusObjectEditor;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@PreAuthorize("!principal.companyIsLocked")
@RequestMapping("/lms/manage/step1")

public class AssessmentFormController extends BaseAssessmentAdminController {

	public static final int DESCRIPTION_LIMIT = 1000;

	@Autowired private IndustryService industryService;

	@InitBinder("assessment")
	@SuppressWarnings("unchecked")
	private void initAssessmentBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
		binder.registerCustomEditor(int.class, new NullSafeNumberEditor(Integer.class, false));
		binder.registerCustomEditor(long.class, new NullSafeNumberEditor(Long.class));
		binder.registerCustomEditor(double.class, new NullSafeNumberEditor(Double.class, false));
		binder.registerCustomEditor(AssessmentType.class, new ThriftEnumEditor(AssessmentType.class));
		binder.registerCustomEditor(NotificationType.class, new ThriftEnumEditor(NotificationType.class));
		binder.registerCustomEditor(User.class, new ThriftObjectEditor(User.class));
		binder.registerCustomEditor(Industry.class, new ThriftObjectEditor(Industry.class));
		binder.registerCustomEditor(Skill.class, new ThriftObjectEditor(Skill.class));
		binder.registerCustomEditor(Status.class, new ThriftStatusObjectEditor());
	}

	@ModelAttribute("NotificationType")
	private Map<String, Object> getNotificationTypes() {
		return ModelEnumUtilities.assessmentNotificationTypes;
	}

	@ModelAttribute("users")
	private Map<String, String> getUsers() {
		Map<String, String> values = Maps.newLinkedHashMap();
		for (Map.Entry<Long, String> entry : getDataHelper().getActiveUsers(getCurrentUser()).entrySet())
			values.put(entry.getKey().toString(), entry.getValue());
		return values;
	}

	@ModelAttribute("industries")
	private Map<String, String> getIndustries() {
		Map<String, String> values = Maps.newLinkedHashMap();
		for (Map.Entry<Long, String> entry : getDataHelper().getIndustries().entrySet())
			values.put(entry.getKey().toString(), entry.getValue());
		return values;
	}

	private Long getDefaultIndustry() {
		Long profileId = profileService.findProfileDTO(getCurrentUser().getId()).getProfileId();
		return industryService.getDefaultIndustryForProfile(profileId).getId();
	}

	/**
	 * Builds a lookup table for configured assessment notification schemes.
	 * Required due to the way the <code>notifications</code> data structure is defined as a <code>List</code>,
	 * that operates like a <code>Map</code> of <code>NotificationTypeConfiguration</code> keyed on the
	 * configuration's type. The Thrift model could be tweaked in order to simplify this arrangement.
	 *
	 * @param a
	 * @return
	 */
	private Map<String, NotificationTypeConfiguration> getNotificationTypeConfigurationLookup(Assessment a) {
		if (!a.isSetConfiguration() || !a.getConfiguration().isSetNotifications())
			return null;

		Map<String, NotificationTypeConfiguration> lookup = Maps.newHashMap();
		for (NotificationTypeConfiguration c : a.getConfiguration().getNotifications())
			lookup.put(String.valueOf(c.getType().getValue()), c);
		return lookup;
	}

	/**
	 * New test form
	 */
	@RequestMapping(
		value = "",
		method = GET)
	public String newForm(Model model) {

		Assessment assessment = new Assessment()
			.setType(AssessmentType.GRADED)
			.setIndustry(new Industry().setId(getDefaultIndustry()));
		model.addAttribute("assessment", assessment);
		model.addAttribute("notificationTypeLookup", getNotificationTypeConfigurationLookup(assessment));

		return "web/pages/lms/manage/step1";
	}

	/**
	 * Edit an existing test
	 */
	@RequestMapping(
		value = "/{id}",
		method = GET)
	public String testForm(
		@PathVariable("id") Long id,
		Model model) {

		Assessment assessment = getAssessment(id);
		model.addAttribute("assessment", assessment);
		model.addAttribute("notificationTypeLookup", getNotificationTypeConfigurationLookup(assessment));

		return "web/pages/lms/manage/step1";
	}

	/**
	 * New survey form
	 */
	@RequestMapping(
		value = "/survey",
		method = GET)
	public String surveyForm(Model model) {

		Assessment assessment = new Assessment()
			.setType(AssessmentType.SURVEY)
			.setIndustry(new Industry().setId(getDefaultIndustry()));
		model.addAttribute("assessment", assessment);
		model.addAttribute("notificationTypeLookup", getNotificationTypeConfigurationLookup(assessment));

		return "web/pages/lms/manage/step1";
	}

	/**
	 * Edit an existing survey
	 */
	@RequestMapping(
		value = "/survey/{id}",
		method = GET)
	public String surveyForm(
		@PathVariable("id") Long id,
		Model model) {

		Assessment assessment = getAssessment(id);
		model.addAttribute("assessment", assessment);
		model.addAttribute("notificationTypeLookup", getNotificationTypeConfigurationLookup(assessment));

		return "web/pages/lms/manage/step1";
	}

	/**
	 * Process the assessment save request.
	 * Handles both tests and surveys.
	 */
	@RequestMapping(method = POST)
	@ResponseBody
	public AjaxResponseBuilder doForm(
		Assessment assessment,
		BindingResult bindingResult,
		MessageBundle messages) {

		AjaxResponseBuilder response = AjaxResponseBuilder.fail();

		if (assessment.getDescription() != null) {
			String plainTextDescription = Jsoup.parse(assessment.getDescription()).text();
			if (StringUtils.length(plainTextDescription) > DESCRIPTION_LIMIT) {
				messageHelper.addError(messages, "lms.description.too.long");
				response.setMessages(messages.getAllMessages());
				return response;
			}
		}

		if (bindingResult.hasErrors()) {
			messageHelper.setErrors(messages, bindingResult);
			response.setMessages(messages.getAllMessages());
			return response;
		}

		AssessmentSaveRequest saveRequest = new AssessmentSaveRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessment(assessment);

		try {
			AssessmentResponse saveResponse = thriftAssessmentService.saveOrUpdateAssessment(saveRequest);
			response.addData("assessment_id", saveResponse.getAssessment().getId());
			return response.setSuccessful(true);

		} catch (ValidationException e) {
			bindingResult = ThriftValidationMessageHelper.newBindingResult("lms");
			for (ConstraintViolation v : e.getErrors()) {
				ThriftValidationMessageHelper.rejectViolation(v, bindingResult);
			}
			messageHelper.setErrors(messages, bindingResult);

			response.setMessages(messages.getAllMessages());
			return response;
		} catch (Exception e) {
			return response;
		}
	}
}
