package com.workmarket.api.v2.employer.search.worker.controllers;

import com.google.common.collect.Lists;
import com.workmarket.api.ApiBaseController;
import com.workmarket.api.ApiJSONPayloadMap;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerDetailsResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerFiltersResponseDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRecord;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchRequestDTO;
import com.workmarket.api.v2.employer.search.worker.model.WorkerSearchResponseDTO;
import com.workmarket.api.v2.employer.search.worker.services.WorkerSearchService;
import com.workmarket.service.web.WebRequestContext;
import com.workmarket.service.web.WebRequestContextProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller used to handle requests for finding workers and workers.
 */
@Api(tags = "Worker Search")
@Controller("WorkerSearchController")
@RequestMapping(value = {"/v2/employer/search/workers", "/employer/v2/search/workers"})
public class WorkerSearchController extends ApiBaseController {
	private static final Logger logger = LoggerFactory.getLogger(WorkerSearchController.class);

	@Autowired private WorkerSearchService workerSearchService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	/**
	 * Find a set of workers that meets our criteria.
	 *
	 * @param criteriaBuilder The builder holding our criteria used to identify the workers in question
	 * @return Response The set of workers that meet our criteria
	 */
	@ApiOperation(value = "Search workers")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/query", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response<WorkerSearchRecord> postSearchWorkers(@RequestBody final WorkerSearchRequestDTO criteriaBuilder) {
		final WebRequestContext requestContext = webRequestContextProvider.getWebRequestContext();
		logger.info("User {} api query request. RequestId: {}", getCurrentUser().getId(), requestContext.getRequestId());

		try {
			WorkerSearchResponseDTO workerSearchResponseDTO =
				workerSearchService.searchWorkers(criteriaBuilder, getCurrentUser());

			ApiJSONPayloadMap metadata = new ApiJSONPayloadMap();
			metadata.put("offset", workerSearchResponseDTO.getOffset());
			metadata.put("limit", workerSearchResponseDTO.getSize());
			metadata.put("resultCount", workerSearchResponseDTO.getResultCount());
			metadata.put("queryTimeMillis", workerSearchResponseDTO.getQueryTimeMillis());
			metadata.put("requestId", requestContext.getRequestId());

			// TODO API - not done
			return new ApiV2Response<>(metadata, workerSearchResponseDTO.getSearchResults());
		}
		catch (Exception e) {
			logger.error("Failed making worker query request. Request Id: " + requestContext.getRequestId(), e);
			throw new BadRequestApiException("Failed making worker query: " + e.getMessage());

		}

	}

	/**
	 * Get the set of filters holding our summary counts for the data available with the given
	 * criteria.
	 *
	 * @param criteriaBuilder The builder holding our criteria used to identify the dataset in question
	 * @return Response The set of filters based on the criteria
	 */
	@ApiOperation(value = "List filters")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/filters", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postListWorkerSearchFilters(@RequestBody final WorkerSearchRequestDTO criteriaBuilder) {
		final WebRequestContext requestContext = webRequestContextProvider.getWebRequestContext();
		logger.info("User {} api filters request. RequestId: {}", getCurrentUser().getId(), requestContext.getRequestId());

		try {
			WorkerFiltersResponseDTO workerFiltersResponseDTO =
				workerSearchService.searchWorkerFilters(criteriaBuilder, getCurrentUser());
			ApiJSONPayloadMap metadata = new ApiJSONPayloadMap();
			metadata.put("resultCount", workerFiltersResponseDTO.getResultCount());
			metadata.put("queryTimeMillis", workerFiltersResponseDTO.getQueryTimeMillis());

			// TODO API - need to implement a DTO for this response
			if (workerFiltersResponseDTO.getFilters() != null) {
				return new ApiV2Response<>(metadata, Lists.newArrayList(workerFiltersResponseDTO.getFilters()));
			}
			else {
				return ApiV2Response.valueWithMeta(metadata);
			}
		}
		catch (Exception e) {
			logger.error("Failed making worker filter request. Request Id: " + requestContext.getRequestId(), e);
			throw new BadRequestApiException("Failed making worker query: " + e.getMessage());
		}
	}

	/**
	 * Gets a the details around a set of workers for a given view type (i.e. hydration).
	 *
	 * @param criteriaBuilder The builder holding our set of workers and the view we are retrieving for
	 * @return Response The worker details for the given view
	 */
	@ApiOperation(value = "Fetch Worker Profiles")
	@ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
	@RequestMapping(value = "/view", method = POST, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	@ResponseBody
	public ApiV2Response postListWorkerProfiles(@RequestBody final WorkerDetailsRequestDTO criteriaBuilder) {
		final WebRequestContext requestContext = webRequestContextProvider.getWebRequestContext();
		logger.info("User {} api view request. RequestId: {}", getCurrentUser().getId(), requestContext.getRequestId());

		try {
			WorkerDetailsResponseDTO workerDetailsResponseDTO =
				workerSearchService.getWorkers(criteriaBuilder, getCurrentUser());

			// TODO API - need to implement a DTO for this response
			return ApiV2Response.OK(workerDetailsResponseDTO.getWorkers());
		}
		catch (Exception e) {
			logger.error("Failed making worker view request. Request Id: " + requestContext.getRequestId(), e);
			throw new BadRequestApiException("Failed making worker view: " + e.getMessage());
		}
	}


}
