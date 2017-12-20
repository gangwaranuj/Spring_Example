package com.workmarket.api.v2.worker.controllers;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.helpers.LinkRouterService;
import com.workmarket.api.helpers.PeopleSearchServiceAdapter;
import com.workmarket.api.model.resolver.ApiArgumentResolver;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.PaginationPage;
import com.workmarket.api.v2.worker.model.WorkersSearchRequest;
import com.workmarket.api.v2.worker.model.WorkersSearchResult;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Deprecated
@Api(tags = {"Search"})
@Controller
// TODO API - move this to employer package and update URL to e.g. /v2/employers/search/workers
@RequestMapping("/v2/workers")
public class WorkersSearchController extends ApiBaseController {
    @Autowired @Qualifier("workersSearchRequestValidator") Validator validator;
    @Autowired PeopleSearchServiceAdapter peopleSearchServiceAdapter;
    @Autowired LinkRouterService linkRouterService;
    @Autowired protected FeatureEvaluator featureEvaluator;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@ApiOperation(value = "Search for workers")
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<WorkersSearchResult> getSearchWorkers(@ApiParam @Valid @ApiArgumentResolver WorkersSearchRequest searchRequest) throws Exception {
		Long companyId = getCurrentUser().getCompanyId();

		if (!featureEvaluator.hasFeature(companyId, "workersSearchCompany")) {
			throw new ForbiddenException("Forbidden");
		}

		PaginationPage page = peopleSearchServiceAdapter.searchPeople(searchRequest, getCurrentUser().getId(), null);

		return buildResponse(page);
	}

	private ApiV2Response<WorkersSearchResult> buildResponse(PaginationPage page) {
		ApiJSONPayloadMap meta = new ApiJSONPayloadMap();
		meta.put("links", linkRouterService.buildLinks(page));
		meta.put("page", page.getPage());
		meta.put("pageSize", page.getPageSize());
		meta.put("totalPageCount", page.getTotalPageCount());
		meta.put("totalRecordCount", page.getTotalRecordCount());

		return new ApiV2Response(meta, page.getResults());
	}
}
