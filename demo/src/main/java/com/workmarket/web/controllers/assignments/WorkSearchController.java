package com.workmarket.web.controllers.assignments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchType;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.search.SearchService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.search.WorkSearchForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/assignments/search")
public class WorkSearchController extends BaseController {

	private final static int SEARCH_LIMIT = 50;

	@Autowired private SearchService searchService;

	@RequestMapping(method = GET)
	public String index(
		Model model,
		@RequestParam(value="search-text", required=false) String query1,
		@RequestParam(value="keyword", required=false) String query2) {

		String query = StringUtils.defaultString(query1, query2);

		model.addAttribute("defaultsearch", query);

		return "web/pages/assignments/search";
	}

	@RequestMapping(
		value = "/retrieve",
		method = POST)
	public void retrieve(Model model, @ModelAttribute WorkSearchForm form) {

		WorkSearchRequest request = new WorkSearchRequest();
		request.setStartRow(form.getPaging() * SEARCH_LIMIT);
		request.setPageSize(SEARCH_LIMIT);
		request.setKeyword(form.getKeywords());
		request.setType(form.getType());
		request.setWorkSearchType(WorkSearchType.GLOBAL);

		if (getCurrentUser().isBuyer()) {
			request.setWorkSearchRequestUserType(WorkSearchRequestUserType.CLIENT);
		} else {
			request.setWorkSearchRequestUserType(WorkSearchRequestUserType.RESOURCE);
		}

		Map<String,Object> response = Maps.newHashMap();

		WorkSearchResponse workSearchResponse = searchService.searchAllWork(getCurrentUser().getId(), request);

		List<Map<String,Object>> results = Lists.newArrayList();

		for (SolrWorkData r : workSearchResponse.getResults()) {
			results.add(CollectionUtilities.newObjectMap(
				"id", r.getId(),
				"work_number", r.getWorkNumber(),
				"title", r.getTitle(),
				"description", StringUtilities.limitWords(StringUtilities.stripHTML(r.getDescription()), 40, "…"), // TODO limit to 40 words and strip tags
				"work_status_type_code", r.getWorkStatusTypeDescription(),
				"instruction", r.getInstructions()
			));
		}

		CollectionUtilities.addToObjectMap(response,
			"results", results,
			"total_rows", workSearchResponse.getTotalResultsCount(),
			"keywords", form.getKeywords()
		);

		CollectionUtilities.addToObjectMap(response,
			"paging", CollectionUtilities.newObjectMap("paging", form.getPaging() + 1)
		);

		model.addAttribute("response", response);
	}
}
