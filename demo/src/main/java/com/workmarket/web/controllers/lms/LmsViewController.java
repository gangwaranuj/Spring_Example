package com.workmarket.web.controllers.lms;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.AssessmentStatistics;
import com.workmarket.domains.model.assessment.AttemptStatusType;
import com.workmarket.domains.model.assessment.ManagedAssessment;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.ATTEMPT_STATUS_FILTER_KEYS;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.INVITATION_FILTER_KEYS;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.OWNER_FILTER_KEYS;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.REQUEST_INFO;
import com.workmarket.domains.model.assessment.ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.thrift.ThriftUtilities;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptLimitExceededException;
import com.workmarket.thrift.assessment.AssessmentAttemptTimedOutException;
import com.workmarket.thrift.assessment.AssessmentGradingRequest;
import com.workmarket.thrift.assessment.AssessmentOptions;
import com.workmarket.thrift.assessment.AssessmentRequestException;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentType;
import com.workmarket.thrift.assessment.Attempt;
import com.workmarket.thrift.assessment.AttemptCompleteRequest;
import com.workmarket.thrift.assessment.AttemptMultipleItemResponsesRequest;
import com.workmarket.thrift.assessment.AttemptStartRequest;
import com.workmarket.thrift.assessment.AuthorizationContext;
import com.workmarket.thrift.assessment.Choice;
import com.workmarket.thrift.assessment.Item;
import com.workmarket.thrift.assessment.ItemResponses;
import com.workmarket.thrift.assessment.ItemType;
import com.workmarket.thrift.assessment.RequestContext;
import com.workmarket.thrift.assessment.Response;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.exceptions.HttpException400;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.forms.lms.GradeResponseLookupForm;
import com.workmarket.web.forms.lms.ResponseLookup;
import com.workmarket.web.forms.lms.SubmitResponseForm;
import com.workmarket.web.forms.lms.SubmitResponseFormResponse;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.views.HTML2PDFView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping({"/lms", "/lms/view"})
public class LmsViewController extends BaseLmsController {

	private static final Logger logger = LoggerFactory.getLogger(BaseLmsController.class);

	@Autowired JsonSerializationService jsonService;

	@RequestMapping(
		value = "",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String index(Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		model.addAttribute("graded", AbstractAssessment.GRADED_ASSESSMENT_TYPE);
		model.addAttribute("manageTypes", manageTypes);
		model.addAttribute("myTestsTypes", myTestTypes);

		return "web/pages/lms/view/index";
	}

	@RequestMapping(
		value = "/index.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> listCourses(HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, ManagedAssessmentPagination.SORTS.NAME.toString(),
			1, ManagedAssessmentPagination.SORTS.COMPANY_NAME.toString(),
			2, ManagedAssessmentPagination.SORTS.INVITATION_DATE.toString(),
			3, ManagedAssessmentPagination.SORTS.STATUS.toString()
		));

		ExtendedUserDetails user = getCurrentUser();
		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination();
		pagination.setOwnerFilter(ManagedAssessmentPagination.OWNER_FILTER_KEYS.COMPANY.setValue(user.getCompanyId()));

		pagination.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED);
		pagination = assessmentService.findAssessmentsForUser(user.getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ManagedAssessment assessment : pagination.getResults()) {
			String invitationDate = null;

			if (assessment.getInvitationDate() != null) {
				invitationDate = DateUtilities.format("MM/dd/yyyy", assessment.getInvitationDate(), user.getTimeZoneId());
			}

			List<String> data = Lists.newArrayList(
				assessment.getAssessmentName(),
				assessment.getCompanyName(),
				invitationDate,
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", assessment.getAssessmentId(),
				"description", assessment.getDescription(),
				"passing_score", assessment.getPassingScore(),
				"approximate_minutes_duration", assessment.getApproximateMinutesDuration(),
				"score", assessment.getScore(),
				"completed_on", (assessment.getCompletedOn() != null) ? DateUtilities.format("MM/dd/yyyy", assessment.getCompletedOn(), user.getTimeZoneId()) : null,
				"is_completed", assessment.isCompleted(),
				"is_passed", assessment.isPassed(),
				"is_reattempt_allowed", assessment.isReattemptAllowed(),
				"invitation_status", assessment.getInvitationStatus(),
				"attempt_status", assessment.getAttemptStatusTypeCode()
			);

			response.addRow(data, meta);
		}

		return CollectionUtilities.newObjectMap(
			"response", response
		);
	}

	@RequestMapping(
		value = "/surveys",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String surveys(Model model) {
		model.addAttribute("time", System.currentTimeMillis());
		return "web/pages/lms/view/surveys";
	}

	@RequestMapping(
		value = "/surveys.json",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> listSurveys(HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			0, ManagedAssessmentPagination.SORTS.NAME.toString(),
			1, ManagedAssessmentPagination.SORTS.COMPANY_NAME.toString()
		));

		// TODO(sgomez): This should be changed somewhere in the future
		ExtendedUserDetails user = getCurrentUser();
		ManagedAssessmentPagination pagination = request.newPagination(ManagedAssessmentPagination.class);
		pagination.setOwnerFilter(ManagedAssessmentPagination.OWNER_FILTER_KEYS.COMPANY.setValue(user.getCompanyId()));
		pagination.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.SURVEY);
		pagination = assessmentService.findAssessmentsForUser(user.getId(), pagination);

		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		for (ManagedAssessment assessment : pagination.getResults()) {
			List<String> data = Lists.newArrayList(
				assessment.getAssessmentName(),
				assessment.getCompanyName(),
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", assessment.getAssessmentId(),
				"description", assessment.getDescription(),
				"passing_score", assessment.getPassingScore(),
				"approximate_minutes_duration", assessment.getApproximateMinutesDuration(),
				"score", assessment.getScore(),
				"completed_on", (assessment.getCompletedOn() != null) ? DateUtilities.format("MM/dd/yyyy", assessment.getCompletedOn(), user.getTimeZoneId()) : null,
				"is_passed", assessment.isPassed(),
				"is_reattempt_allowed", assessment.isReattemptAllowed(),
				"invitation_status", assessment.getInvitationStatus(),
				"attempt_status", assessment.getAttemptStatusTypeCode()
			);

			response.addRow(data, meta);
		}

		return CollectionUtilities.newObjectMap(
			"response", response
		);
	}

	//TODO: remove /manage/grade -- which is put to avoid breaking any previous links
	@RequestMapping(
		value = {
			"/grade/{assessmentId}/{attemptId}",
			"/manage/grade/{assessmentId}/{attemptId}"
		}, method = GET)
	public String grade(
		@PathVariable("assessmentId") Long id,
		@PathVariable("attemptId") Long attemptId,
		@RequestParam(value="popup", defaultValue="false") boolean isPopup,
		Model model) {

		AssessmentGradingRequest gradingRequest = new AssessmentGradingRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(id)
			.setAttemptId(attemptId);

		AssessmentResponse gradingResponse;
		try {
			gradingResponse = getThriftAssessmentService().findAssessmentForGrading(gradingRequest);
		} catch (AssessmentRequestException e) {
			throw new HttpException404();
		}

		boolean isGrader = CollectionUtilities.containsAny(gradingResponse.getRequestContexts(), RequestContext.OWNER, RequestContext.COMPANY_OWNED);

		if (!isGrader) {
			if (!getCurrentUser().getId().equals(gradingResponse.getRequestedAttempt().getUser().getId()))
				throw new HttpException401();
		}

		// Build a lookup of item responses for easier rendering of the view

		Set<Long> graded = Sets.newHashSet();
		GradeResponseLookupForm gradingForm = new GradeResponseLookupForm();

		Attempt attempt = gradingResponse.getRequestedAttempt();
		Map<Long,Item> responseItems = Maps.newLinkedHashMap();
		if (attempt != null && attempt.getResponses() != null) {
			for (Response r : attempt.getResponses()) {
				ResponseLookup lookup = gradingForm.getResponse(r.getItem().getId());
				lookup.setItemId(r.getItem().getId());
				lookup.setGraded(r.isSetGradedOn());
				lookup.setCorrect(r.isCorrect());
				lookup.setAssets(r.getAssets());

				switch (r.getItem().getType()) {
					case SINGLE_CHOICE_RADIO:
					case SINGLE_CHOICE_LIST:
						if (r.isSetValue()) {
							lookup.setOther(r.getValue());
						} else if (r.isSetChoice()) {
							lookup.setAnswer(String.valueOf(r.getChoice().getId()));
						}
						break;
					case MULTIPLE_CHOICE:
						if (r.isSetValue()) {
							lookup.setOther(r.getValue());
						} else if (r.isSetChoice()) {
							lookup.addToAnswers(String.valueOf(r.getChoice().getId()));
						}
						break;
					default:
						lookup.setAnswer(r.getValue());
				}

				gradingForm.addToResponses(r.getItem().getId(), lookup);

				if (r.getItem().isGraded())
					graded.add(r.getItem().getId());
			}

			// Mark whether or not the full response is correct
			for (Item i : attempt.getItemsForResponses()) {
				responseItems.put(i.getId(), i);
			}

			for (ResponseLookup l : gradingForm.getResponses().values()) {
				l.setCorrectBasedOnItem(responseItems.get(l.getItemId()));
			}
		}

		AssessmentOptions options = gradingResponse.getAssessment().getConfiguration();

		model.addAttribute("gradedCount", graded.size());
		model.addAttribute("gradingForm", gradingForm);
		model.addAttribute("gradingResponse", gradingResponse);
		model.addAttribute("responseItems", responseItems);
		model.addAttribute("isGrader", isGrader);
		model.addAttribute("showQuestionResults", attempt.isPassed() ? options.isResultsSharedWithPassers() : options.isResultsSharedWithFailers());

		return (isPopup) ?
			"web/pages/lms/manage/results" :
			"web/pages/lms/manage/grade";
	}


	@RequestMapping(
		value = "/details/{id}",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String details(@PathVariable("id") Long assessmentId, Model model, RedirectAttributes flash) {

		AssessmentResponse assessmentResponse = getAssessment(assessmentId, null, null);
		Assessment assessment = assessmentResponse.getAssessment();

		final ExtendedUserDetails currentUser = getCurrentUser();

		if(userService.isUserBlockedByCompany(currentUser.getId(), currentUser.getCompanyId(), assessment.getCompany().getId())) {
			throw new HttpException401();
		}
		model.addAttribute("assessment", assessment);
		model.addAttribute("createdOn", new Date(assessment.getCreatedOn()));

		AssessmentStatistics statistics = assessmentService.getAssessmentStatistics(assessmentId);
		model.addAttribute("statistics", statistics);

		Set<String> authorizationContexts = new HashSet<>();
		model.addAttribute("authorizationContexts", authorizationContexts);

		if (assessmentResponse.getAuthorizationContexts() != null) {
			for (AuthorizationContext authorizationContext : assessmentResponse.getAuthorizationContexts()) {
				authorizationContexts.add(authorizationContext.name());
			}
		}

		model.addAttribute("graded", AssessmentType.GRADED.equals(assessment.getType()));
		model.addAttribute("survey", AssessmentType.SURVEY.equals(assessment.getType()));
		model.addAttribute("userId", currentUser.getId());
		model.addAttribute("timeZoneId", currentUser.getTimeZoneId());
		model.addAttribute("userCompanyId", currentUser.getCompanyId());
		model.addAttribute("companyBlocked", userService.isCompanyBlockedByUser(currentUser.getId(), currentUser.getCompanyId(), assessment.getCompany().getId()));
		model.addAttribute("hasAssetItems", assessment.isHasAssetItems());

		if (assessmentResponse.getLatestAttempt() != null) {
			Attempt latestAttempt = assessmentResponse.getLatestAttempt();
			model.addAttribute("latestAttempt", latestAttempt);
			model.addAttribute("latestAttemptCreatedOn", new Date(latestAttempt.getCreatedOn()));
			model.addAttribute("latestAttemptCompleteOn", new Date(latestAttempt.getCompleteOn()));
		}

		return "web/pages/lms/view/details";
	}

	@RequestMapping(
		value = "/take/{id}",
		method = GET)
	public String take(
		@PathVariable("id") Long assessmentId,
		@RequestParam(value = "assignment", required = false) Long workId,
		@RequestParam(value = "onBehalfOf", required = false) String onBehalfOfNumber,
		Model model,
		SitePreference site) throws Exception {

		// TODO: does this expose workId to the user? It should use workNumber instead

		User userBehalfOf = null;
		if (onBehalfOfNumber != null) {
			userBehalfOf = userService.findUserByUserNumber(onBehalfOfNumber);
		}

		AssessmentResponse assessmentResponse = getAssessment(assessmentId, workId, userBehalfOf, AuthorizationContext.ATTEMPT);
		Assessment assessment = assessmentResponse.getAssessment();
		ExtendedUserDetails user = getCurrentUser();

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
					request.setUserId(userBehalfOf.getId());
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
				model.addAttribute("work", workResponse); // ToDo: shouldn't this be workResponse.getWork()?
			} catch (Exception ex) {
				logger.warn("exception caught while executing findWorkDetail for userId={} and workId={}",
					new Object[]{user.getId(), workId}, ex);
			}
		}

		model.addAttribute("assessment", assessment);
		model.addAttribute("durationMinutes", assessment.getConfiguration().getDurationMinutes());
		model.addAttribute("timeLeft", assessmentService.getTimeUntilAttemptExpires(assessmentId, user.getId()));
		model.addAttribute("assessmentItemsJson", jsonService.toJson(ThriftUtilities.serializeToJson(assessment.getItems(), "correct")));
		model.addAttribute("latestAttempt", assessmentResponse.getLatestAttempt());
		model.addAttribute("latestAttemptResponsesJson", assessmentResponse.isSetLatestAttempt() ? jsonService.toJson(ThriftUtilities.serializeToJson(assessmentResponse.getLatestAttempt().getResponses())) : "''");
		model.addAttribute("graded", AssessmentType.GRADED.equals(assessment.getType()));
		model.addAttribute("survey", AssessmentType.SURVEY.equals(assessment.getType()));
		model.addAttribute("timeZoneId", getCurrentUser().getTimeZoneId());
		model.addAttribute("hasAssetItems", assessment.isHasAssetItems());
		model.addAttribute("AssessmentItemTypeJSON", jsonService.toJson(ModelEnumUtilities.assessmentItemTypes));
		model.addAttribute("uploadTypes", MimeTypeUtilities.getMimeTypesForPage("/lms/view/take"));
		model.addAttribute("allQuestionsAnswered", assessmentService.isAttemptFinished(assessmentId, getCurrentUser().getId()));

		return "web/pages/lms/view/take";
	}

	@RequestMapping(
		value = "/print/{assessmentId}",
		method = GET)
	public View printTest(
		@PathVariable(value = "assessmentId") Long assessmentId,
		@RequestParam(value = "assignment", required = false) String workNumber,
		Model model) {

		WorkResponse workResponse = null;

		if (StringUtils.isNotEmpty(workNumber)) {
			WorkRequest workRequest = new WorkRequest();
			workRequest.setUserId(getCurrentUser().getId());
			workRequest.setWorkNumber(workNumber);
			try {
				workResponse = tWorkFacadeService.findWorkDetail(workRequest);
			} catch (WorkActionException e) {
				logger.warn(String.format("error on /lms/print for userId={%d} and workNumber={%s}", getCurrentUser().getId(), workNumber), e);
			}
			model.addAttribute("work", workResponse);
		}
		AssessmentResponse assessmentResponse = getAssessment(assessmentId, (workResponse == null) ? null : workResponse.getWork().getId(), null, AuthorizationContext.ATTEMPT);

		// TODO: don't allow printing of tests yet because it could be used to cheat
		if (!assessmentResponse.isSetAssessment() || !AssessmentType.SURVEY.equals(assessmentResponse.getAssessment().getType()))
			return new RedirectView("/lms/view");

		model.addAttribute("assessment", assessmentResponse.getAssessment());
		model.addAttribute("AssessmentItemType", ModelEnumUtilities.assessmentItemTypes);

		return new HTML2PDFView("/pdf/lms/survey", StringUtilities.convertToFilename(assessmentResponse.getAssessment().getName()) + ".pdf");
	}

	// For submitting individual test answers only
	@RequestMapping(
		value = "/submit_answer/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder submitResponse(
		@PathVariable("id") Long assessmentId,
		SubmitResponseForm form) {

		try {
			List<Response> responses =
				thriftAssessmentService.submitMultipleItemResponses(
					buildMultipleItemResponsesRequest(getCurrentUser(), assessmentId, form)
				);

			return AjaxResponseBuilder.success()
				.addData("responses", responses)
				.addData("allQuestionsAnswered", assessmentService.isAttemptFinished(assessmentId, getCurrentUser().getId()));
		} catch (AssessmentAttemptTimedOutException ex) {
			return AjaxResponseBuilder.fail()
				.addData("timeout", true)
				.addData("allQuestionsAnswered", assessmentService.isAttemptFinished(assessmentId, getCurrentUser().getId()));
		} catch (ValidationException ex) {
			return getAjaxResponseBuilder(ex)
				.addData("allQuestionsAnswered", assessmentService.isAttemptFinished(assessmentId, getCurrentUser().getId()));
		} catch (Exception ex) {
			return AjaxResponseBuilder.fail()
				.addData("errors", ex.getMessage())
				.addData("allQuestionsAnswered", assessmentService.isAttemptFinished(assessmentId, getCurrentUser().getId()));
		}
	}

	private AjaxResponseBuilder getAjaxResponseBuilder(ValidationException ex) {
		Map<Long, String> errors = Maps.newHashMap();
		MessageBundle messages = messageHelper.newBundle();
		BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult("lms");

		for (ConstraintViolation v : ex.getErrors()) {
			ThriftValidationMessageHelper.rejectViolation(v, bindingResult);
			messageHelper.setErrors(messages, bindingResult);
			errors.put(StringUtilities.parseLong(v.getParams().get(0)), CollectionUtilities.last(messages.getErrors()));
		}
		return AjaxResponseBuilder.fail().addData("errors", errors);
	}

	private AttemptMultipleItemResponsesRequest buildMultipleItemResponsesRequest(ExtendedUserDetails user, Long assessmentId, SubmitResponseForm form) {

		AttemptMultipleItemResponsesRequest itemRequest = new AttemptMultipleItemResponsesRequest();
		itemRequest
			.setUserId(user.getId())
			.setAssessmentId(assessmentId);

		AssessmentResponse assessmentResponse = getAssessment(assessmentId, null, null, AuthorizationContext.ATTEMPT);

		if (assessmentResponse.getAssessment().getItemsSize() > 0) {
			for (SubmitResponseFormResponse r : form.getResponses()) {
				Long itemId = r.getItemId();
				// Lookup question item.
				Item question = null;

				for (Item item : assessmentResponse.getAssessment().getItems()) {
					if (item.getId() == itemId) {
						question = item;
						break;
					}
				}

				// Make sure we found the question.
				if (question == null) {
					throw new HttpException400("Could not locate question item.");
				}

				// Don't submit a response for segment breaks.
				if (question.getType() == ItemType.DIVIDER) {
					continue;
				}

				ItemResponses item = new ItemResponses();
				item.setItemId(itemId);

				if (question.getType() == ItemType.MULTIPLE_CHOICE) {
					if (CollectionUtils.isNotEmpty(r.getChoices())) {
						r.getChoices().removeAll(Collections.singletonList(null));
						for (String c : r.getChoices()) {
							Response response = new Response();

							if ("other".equalsIgnoreCase(c)) {
								response.setValue(r.getValue());
							} else if (StringUtils.isNumeric(c)) {
								Choice choice = new Choice();
								choice.setId(Long.parseLong(c));
								response.setChoice(choice);
							}

							item.addToResponses(response);
						}
					}
				} else if ((question.getType() == ItemType.SINGLE_CHOICE_RADIO) || (question.getType() == ItemType.SINGLE_CHOICE_LIST)) {
					if (CollectionUtils.isNotEmpty(r.getChoices())) {
						String c = CollectionUtilities.first(r.getChoices());
						Response response = new Response();

						if ("other".equalsIgnoreCase(c)) {
							response.setValue(r.getValue());
						} else if (StringUtils.isNumeric(c)) {
							Choice choice = new Choice();
							choice.setId(Long.parseLong(c));
							response.setChoice(choice);
						}

						item.addToResponses(response);
					}
				} else if (question.getType() == ItemType.ASSET) {
					Response response = new Response();

					if (CollectionUtils.isNotEmpty(r.getAssets())) {
						for (Asset asset : r.getAssets()) {
							response.addToAssets(asset);
						}
					}

					if (CollectionUtils.isNotEmpty(r.getUploads())) {
						for (Upload upload : r.getUploads()) {
							response.addToUploads(upload);
						}
					}

					item.addToResponses(response);
				} else {
					Response response = new Response();
					response.setValue(r.getValue());
					item.addToResponses(response);
				}

				itemRequest.addToItemResponses(item);
			}
		}

		User userBehalfOf;

		// If we have an assignment specified, use it.
		if (form.getAssignment() != null) {
			itemRequest.setWorkId(form.getAssignment());
			if (form.getOnBehalfOf() != null) {
				userBehalfOf = userService.findUserByUserNumber(String.valueOf(form.getOnBehalfOf()));
				if (userBehalfOf != null) {
					itemRequest.setUserId(userBehalfOf.getId());
				}
			}
		}

		return itemRequest;
	}

	// For submitting a final test attempt and for completing a survey
	@RequestMapping(
		value = "/complete_assessment/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder completeAssessment(
		@PathVariable("id") Long assessmentId,
		SubmitResponseForm form) {

		try {
			authorizeAssessment(assessmentId, null, AuthorizationContext.ATTEMPT);
			// If the user is submitting from a test question page, make sure to log those answers before submitting the final answers
			thriftAssessmentService.submitMultipleItemResponses(
				buildMultipleItemResponsesRequest(getCurrentUser(), assessmentId, form)
			);
			thriftAssessmentService.completeAttempt(
				buildAttemptCompleteRequest(getCurrentUser(), assessmentId, form)
			);
			return AjaxResponseBuilder.success();

		} catch (AssessmentAttemptTimedOutException ex) {
			return AjaxResponseBuilder.fail().addData("timeout", true);
		} catch (ValidationException ex) {
			return getAjaxResponseBuilder(ex);
		} catch (Exception ex) {
			return AjaxResponseBuilder.fail();
		}
	}

	private AttemptCompleteRequest buildAttemptCompleteRequest(ExtendedUserDetails user, Long assessmentId, SubmitResponseForm form) {
		AttemptCompleteRequest completeRequest = new AttemptCompleteRequest();
		completeRequest.setUserId(user.getId());
		completeRequest.setAssessmentId(assessmentId);
		if (form.getAssignment() != null) {
			completeRequest.setWorkId(form.getAssignment());
		}
		if (form.getOnBehalfOf() != null) {
			User userBehalfOf = userService.findUserByUserNumber(String.valueOf(form.getOnBehalfOf()));
			if (userBehalfOf != null) {
				completeRequest.setUserId(userBehalfOf.getId());
			}
		}
		return completeRequest;
	}

	@RequestMapping(
		value = "/browse/{pageNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder browse(
		@PathVariable("pageNumber") Integer pageNumber) throws Exception {

		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination();
		pagination.setTakeabilityFilter(ManagedAssessmentPagination.TAKEABILITY_FILTER_KEYS.TAKEABLE);
		pagination.setActivityFilter(ManagedAssessmentPagination.ACTIVITY_FILTER_KEYS.ACTIVE);
		pagination.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED);
		pagination.setPrivacyFilter(ManagedAssessmentPagination.PRIVACY_FILTER_KEYS.PRIVACY_PUBLIC);
		pagination.setInvitationFilter(INVITATION_FILTER_KEYS.INDUSTRY_INVITED);
		pagination.getRequestInformation().add(REQUEST_INFO.PROGRESS);
		pagination.addSort(ManagedAssessmentPagination.SORTS.CREATED_ON.getSort(SORT_DIRECTION.DESC));
		pagination.setResultsLimit(BROWSE_PAGINATION_MAX_RESULTS);
		pagination.setPage(pageNumber);
		pagination = assessmentService.findAssessmentsForUser(getCurrentUser().getId(), pagination);
		return AjaxResponseBuilder
			.success()
			.addData("assessments", setCompanyLogos(pagination.getResults()))
			.addData("rowCount", pagination.getRowCount());
	}

	@RequestMapping(
		value = "/recommended",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder recommended() throws Exception {

		ManagedAssessmentPagination pagination = assessmentService.findRecommendedAssessmentsForUser(getCurrentUser().getId());
		return AjaxResponseBuilder
			.success()
			.addData("assessments", setCompanyLogos(pagination.getResults()))
			.addData("rowCount", pagination.getRowCount());
	}

	@RequestMapping(
		value = "/manage",
		method = GET,
		produces = TEXT_HTML_VALUE)
	public String showManage() {

		return "web/pages/lms/manage/index";
	}

	@RequestMapping(
		value = "/manage/{pageNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder manage(
		@RequestParam("type") String type,
		@PathVariable("pageNumber") Integer pageNumber) throws Exception {

		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination()
			.addRequestInformation(REQUEST_INFO.STATISTICS)
			.setOwnerFilter(OWNER_FILTER_KEYS.COMPANY.setValue(getCurrentUser().getCompanyId()))
			.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED);
		pagination.addSort(ManagedAssessmentPagination.SORTS.CREATED_ON.getSort(SORT_DIRECTION.DESC));
		pagination.setResultsLimit(MANAGE_PAGINATION_MAX_RESULTS);
		pagination.setPage(pageNumber);

		if (type != null) {
			switch (MANAGE_TYPES.valueOf(type)) {
				case ALL:
					pagination.setActivityFilter(null); // Default filter is ACTIVE assessments
					break;
				case TESTS_I_OWN:
					pagination
						.setActivityFilter(null)
						.setOwnerFilter(OWNER_FILTER_KEYS.USER.setValue(getCurrentUser().getId()));
					break;
				case INACTIVE:
					pagination.setActivityFilter(ACTIVITY_FILTER_KEYS.INACTIVE);
					break;
			}
		}

		pagination = assessmentService.findAssessmentsForUser(getCurrentUser().getId(), pagination);
		return AjaxResponseBuilder
			.success()
			.addData("assessments", setCompanyLogos(pagination.getResults()))
			.addData("rowCount", pagination.getRowCount());
	}

	@RequestMapping(
		value = "/mytests/{pageNumber}",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody AjaxResponseBuilder mytests(
		@RequestParam("type") String type,
		@PathVariable("pageNumber") Integer pageNumber) throws Exception {

		ManagedAssessmentPagination pagination = new ManagedAssessmentPagination()
			.setTypeFilter(ManagedAssessmentPagination.TYPE_FILTER_KEYS.GRADED)
			.addRequestInformation(REQUEST_INFO.PROGRESS);
		pagination.setResultsLimit(MYTESTS_PAGINATION_MAX_RESULTS);
		pagination.setPage(pageNumber);

		if (type != null) {
			switch (MYTESTS_TYPES.valueOf(type)) {
				case INVITATIONS:
					pagination
						.setTakeabilityFilter(TAKEABILITY_FILTER_KEYS.TAKEABLE)
						.setInvitationFilter(INVITATION_FILTER_KEYS.DIRECTLY_OR_GROUP_INVITED)
						.addSort(ManagedAssessmentPagination.SORTS.INVITATION_DATE.getSort(SORT_DIRECTION.DESC));
					break;
				case IN_PROGRESS:
					pagination
						.setAttemptFilter(ATTEMPT_STATUS_FILTER_KEYS.IN_PROGRESS)
						.addSort(ManagedAssessmentPagination.SORTS.STARTED_ON.getSort(SORT_DIRECTION.DESC));
					break;
				case PASSED:
					pagination
						.setAttemptFilter(ATTEMPT_STATUS_FILTER_KEYS.PASSED)
						.addSort(ManagedAssessmentPagination.SORTS.COMPLETED_ON.getSort(SORT_DIRECTION.DESC));
					break;
				case FAILED:
					pagination
						.setAttemptFilter(ATTEMPT_STATUS_FILTER_KEYS.FAILED)
						.addSort(ManagedAssessmentPagination.SORTS.COMPLETED_ON.getSort(SORT_DIRECTION.DESC));
					break;
				case GRADE_PENDING:
					pagination
						.setAttemptFilter(ATTEMPT_STATUS_FILTER_KEYS.GRADE_PENDING)
						.addSort(ManagedAssessmentPagination.SORTS.COMPLETED_ON.getSort(SORT_DIRECTION.DESC));
					break;
			}
		}

		pagination = assessmentService.findAssessmentsForUser(getCurrentUser().getId(), pagination);
		return AjaxResponseBuilder
			.success()
			.addData("assessments", setCompanyLogos(pagination.getResults()))
			.addData("rowCount", pagination.getRowCount());
	}

}
