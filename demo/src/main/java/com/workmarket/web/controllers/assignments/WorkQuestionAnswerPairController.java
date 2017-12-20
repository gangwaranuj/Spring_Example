package com.workmarket.web.controllers.assignments;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.utility.CollectionUtilities;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("WorkQuestionAnswerPairController")
@RequestMapping("/assignments")
public class WorkQuestionAnswerPairController extends BaseWorkController {

	private static final Logger logger = LoggerFactory.getLogger(WorkDetailsController.class);

	@Autowired private WorkQuestionService workQuestionService;

	@ApiOperation("Get all questions for an assignment")
	@RequestMapping(
		value = "/{workNumber}/questions",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAssignmentQuestions(@PathVariable String workNumber, HttpServletResponse response) {
		Long workId = workService.findWorkId(workNumber);
		List<Map<String, Object>> pairs = new ArrayList<>();

		if (workId != null) {
			pairs = maskQuestions(workQuestionService.findQuestionAnswerPairs(workId, getCurrentUser().getId(), getCurrentUser().getCompanyId()));
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching workId for assignment %s", workNumber));
		}

		return CollectionUtilities.newObjectMap("results", pairs);
	}

	@ApiOperation("Create questions for an assignment")
	@RequestMapping(
		value = "/{workNumber}/questions",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> postCreateAssignmentQuestions(
		@PathVariable String workNumber,
		@RequestBody WorkQuestionAnswerPair question,
		HttpServletResponse response) {

		Long workId = workService.findWorkId(workNumber);
		Map<String, Object> pair = null;

		if (workId != null) {
			try {
				pair = maskQuestions(workQuestionService.saveQuestion(workId, getCurrentUser().getId(), question.getQuestion()));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(String.format("Error saving question %s: ", question.getQuestion()), e);
			}
		} else {
			pair = CollectionUtilities.newObjectMap();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching workId for assignment %s", workNumber));
		}

		return pair;
	}

	@ApiOperation("Update questions for an assignment")
	@RequestMapping(
		value = "/{workNumber}/questions/{questionId}",
		method = { RequestMethod.PUT, RequestMethod.PATCH },
		produces = APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> putUpdateAssignmentQuestions(
		@PathVariable String workNumber,
		@PathVariable Long questionId,
		@RequestBody WorkQuestionAnswerPair answer,
		HttpServletResponse response) {

		Long workId = workService.findWorkId(workNumber);
		Map<String, Object> pair = null;

		if (workId != null) {
			try {
				pair = maskQuestions(workQuestionService.saveAnswerToQuestion(questionId, getCurrentUser().getId(), answer.getAnswer(), workId));
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				logger.error(String.format("Error saving answer to question %d: ", questionId), e);
			}
		} else {
			pair = CollectionUtilities.newObjectMap();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.error(String.format("Error fetching workId for assignment %s", workNumber));
		}

		return pair;
	}

	/* Note: We need questions/answers to be structured the same as messages.
	 * We need to build an editor to do the type conversion automagically with Spring.
	 */
	private List<Map<String, Object>> maskQuestions(List<WorkQuestionAnswerPair> pairs) {
		List<Map<String, Object>> results = Lists.newArrayListWithExpectedSize(pairs.size());

		for (WorkQuestionAnswerPair pair : pairs) {
			results.add(maskQuestions(pair));
		}

		return results;
	}

	private Map<String, Object> maskQuestions(WorkQuestionAnswerPair pair) {
		List<Object> responses = new ArrayList<>();

		if (pair.getAnsweredOn() != null) {
			responses.add(new ImmutableMap.Builder<String, Object>()
				.put("id", pair.getId())
				.put("creatorNumber", Long.valueOf(userService.findUserNumber(pair.getAnswererId())))
				.put("createdOn", pair.getAnsweredOn())
				.put("content", pair.getAnswer())
				.build());
		}

		return new ImmutableMap.Builder<String, Object>()
			.put("id", pair.getId())
			.put("creatorNumber", Long.valueOf(userService.findUserNumber(pair.getQuestionerId())))
			.put("createdOn", pair.getCreatedOn())
			.put("content", pair.getQuestion())
			.put("responses", responses)
			.put("isQuestion", true)
			.build();
	}
}
