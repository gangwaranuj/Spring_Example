package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.AddMessageDTO;
import com.workmarket.api.v2.worker.service.XAssignment;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.Note;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = {"Assignments"})
@Controller("MessagesController")
@RequestMapping("/worker/v2/assignments")
public class MessagesController extends ApiBaseController {

	private static final org.apache.commons.logging.Log logger = org.apache.commons.logging.LogFactory.getLog(
					MessagesController.class);

	@Autowired private XAssignment xAssignment;
	@Autowired private AssignmentMarshaller assignmentMarshaller;
	@Autowired private UserService userService;

	@ApiOperation("Get all messages for an assignment")
	@RequestMapping(value = "/{workNumber}/messages", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response getAssignmentMessages(@PathVariable final String workNumber,
																	 @RequestParam(required = false) final Integer page,
																	 @RequestParam(required = false) final Integer pageSize,
																	 final HttpServletRequest request) {

		final NotePagination notePagination = xAssignment.getMessages(getCurrentUser(), workNumber, page, pageSize);

		final List<Note> notes = notePagination.getResults();

		final Map<Long, User> userMap = new HashMap<>();

		for (final Note note : notes) {
			final User user = userService.findUserById(note.getCreatorId());
			userMap.put(note.getCreatorId(), user);
		}

		final List<ApiJSONPayloadMap> results = assignmentMarshaller.getDomainModelNotes(notes, userMap);

		final ApiV2Pagination apiPagination = new ApiV2Pagination.ApiPaginationBuilder().page(notePagination.getCurrentPage()
																																															.longValue())
						.pageSize(notePagination.getRowCount().longValue())
						.totalPageCount(notePagination.getNumberOfPages().longValue())
						.totalRecordCount(notePagination.getRowCount().longValue())
						.build(request);

		final ApiJSONPayloadMap metadataBuilder = new ApiJSONPayloadMap();
		metadataBuilder.put("code", HttpServletResponse.SC_OK);

		final ApiV2Response apiResponse = new ApiV2Response(metadataBuilder,
																												results,
																												apiPagination);

		return apiResponse;
	}

	@ApiOperation("Add a message to an assignment")
	@RequestMapping(value = "/{workNumber}/messages", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postAddAssignmentMessage(@PathVariable final String workNumber,
																	@Valid @RequestBody final AddMessageDTO addMessageDTO) {

		final Note note = xAssignment.addMessage(getCurrentUser(), workNumber, addMessageDTO);

		final List results = new ArrayList();
		//results.add(note);

		return ApiV2Response.OK(results);
	}
}
