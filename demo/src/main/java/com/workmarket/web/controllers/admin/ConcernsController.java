
package com.workmarket.web.controllers.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;
import com.workmarket.service.business.ClientSvcService;
import com.workmarket.service.business.CommentService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.UserCommentDTO;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/admin/concerns")
public class ConcernsController extends BaseController {

	@Autowired private ClientSvcService clientSvcService;
	@Autowired private CommentService commentService;
	@Autowired private UserService userService;

	@RequestMapping(
		value = {"", "/", "/index"},
		method = GET)
	public String index() {

		return "web/pages/admin/concerns/index";
	}

	@RequestMapping(
		value="/list",
		method = GET,
		produces = APPLICATION_JSON_VALUE)
	public void list(Model model, HttpServletRequest httpRequest) throws Exception {

		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);

		request.setSortableColumnMapping(ImmutableMap.of(
			0, ConcernPagination.SORTS.CREATED_ON.toString(),
			1, ConcernPagination.SORTS.LAST_NAME.toString(),
			2, ConcernPagination.SORTS.CONTENT.toString()
		));

		ConcernPagination pagination = new ConcernPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());

		if (!httpRequest.getParameter("filters_resolved").isEmpty()) {
			pagination.addFilter(ConcernPagination.FILTER_KEYS.RESOLVED.toString(), httpRequest.getParameter("filters_resolved"));
		} else {
			pagination.addFilter(ConcernPagination.FILTER_KEYS.RESOLVED.toString(), "false");
		}

		pagination = clientSvcService.findAllConcerns(pagination);

		DataTablesResponse<List<String>,Map<String,Object>> response = DataTablesResponse.newInstance(request, pagination);
		List<Concern> concerns = pagination.getResults();

		for (Concern concern : concerns) {
			String createdOn = DateUtilities.format("MM/dd/yyyy", concern.getCreatedOn(), getCurrentUser().getTimeZoneId());
			User creator = userService.findUserById(concern.getCreatorId());
			Hibernate.initialize(creator.getCompany());

			List<String> row = Lists.newArrayList(
				createdOn,
				creator.getFullName(),
				concern.getContent(),
				null
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", concern.getId(),
				"type", concern.getType(),
				"entity_id", concern.getEntityId(),
				"entity_number", concern.getEntityNumber(),
				"resolved", concern.isResolved(),
				"creator_id", creator.getId(),
				"creator_user_number", creator.getUserNumber(),
				"creator_fullname", creator.getFullName(),
				"creator_company_name", creator.getCompany().getName()
			);

			response.addRow(row, meta);
		}

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/resolve/{id}",
		method = GET)
	public String resolve(Model model, @PathVariable("id") Long id) {

		model.addAttribute("id", id);

		return "web/pages/admin/concerns/resolve";
	}

	@RequestMapping(
		value="/resolve/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public void resolveSubmit(@PathVariable("id") Long id, Model model, HttpServletRequest httpRequest) throws Exception {

		Map<String, Object> response = Maps.newHashMap();

		ArrayList<String> messages = new ArrayList<String>();

		Long userId = userService.findUserId(httpRequest.getParameter("creator_user_number"));

		if (userId == null) {
			messages.add("Creator user ID is a required field.");
		}
		if (httpRequest.getParameter("comment").isEmpty()) {
			messages.add("Comment is a required field.");
		}

		if (messages.isEmpty()) {
			UserCommentDTO dto = new UserCommentDTO();
			dto.setComment(httpRequest.getParameter("comment"));
			dto.setUserId(userId);
			commentService.saveOrUpdateClientServiceUserComment(dto);

			clientSvcService.updateConcernResolvedStatus(id, true);

			response.put("successful", true);
			messages.add("Successfully resolved the concern.");
		} else {
			response.put("successful", false);
		}
		response.put("messages", messages);

		model.addAttribute("response", response);
	}

	@RequestMapping(
		value="/reopen/{id}",
		method = POST,
		produces = APPLICATION_JSON_VALUE)
	public void reopen(@PathVariable("id") Long id, Model model) {

		Map<String, Object> response = Maps.newHashMap();

		try {
			clientSvcService.updateConcernResolvedStatus(id, false);
			response.put("successful", true);
			response.put("messages", new ArrayList<String>() {{
				add("Successfully re-opened the concern.");
			}});
		} catch (Exception e) {
			response.put("successful", false);
			response.put("messages", new ArrayList<String>() {{
				add("There was an error re-opening the concern. Please try again.");
			}});
		}

		model.addAttribute("response", response);
	}

}
