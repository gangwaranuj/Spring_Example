package com.workmarket.api.v2.worker.fulfillment;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.workmarket.api.v2.ApiV2Pagination;
import com.workmarket.api.v2.worker.marshaller.AssignmentMarshaller;
import com.workmarket.api.v2.worker.model.AssignmentsRequestDTO;
import com.workmarket.api.v2.worker.service.AssignmentService;
import com.workmarket.api.v2.worker.service.XWork;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.dashboard.MobileDashboardService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.AuthorizationContext;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.web.controllers.mobile.MobileWorkController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.exceptions.MobileHttpException403;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Delegates to data pulling services and data marshallers to assemble response payloads for REST endpoints that
 * operate on assignments
 */
@Service
public class AssignmentFulfillmentProcessor {
	// TODO API - what do we do with this


	private static final String WORK_CONTROLLER_ROWS_KEY = "rows";
	private static final String WORK_CONTROLLER_PAGINATION_KEY = "pagination";

	@Autowired private AssignmentService assignmentService;
	@Autowired private MobileWorkController workController;
	@Autowired private PartService partService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private MobileDashboardService mobileDashboardService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private XWork xWork;
	@Autowired private AssignmentMarshaller assignmentMarshaller;

	public FulfillmentPayloadDTO getListPage(final ExtendedUserDetails user,
																					 final AssignmentsRequestDTO assignmentsRequestDTO,
																					 final HttpServletRequest request) {

		final Map<String, Object> map = assignmentService.getList(assignmentsRequestDTO.getStatus(),
																															assignmentsRequestDTO.getFields().split(","),
																															assignmentsRequestDTO.getPage(),
																															assignmentsRequestDTO.getPageSize(),
																															assignmentsRequestDTO.getSort());

		return generateResponse(user, request, map, false);
	}

    /* This is how it _should_ have been done... */
		/*
			public FulfillmentResponse getListPage(final ExtendedUserDetails user,
      final AssignmentsRequestDTO assignmentsRequestDTO,
      final HttpServletRequest request) {

      WorkSearchRequest workSearchRequest = mobileDashboardService
      .buildSearchRequest
      (
      user.getUserNumber(),
      assignmentsRequestDTO.getStatus(),
      assignmentsRequestDTO.getPage(),
      assignmentsRequestDTO.getPageSize()
      );

      WorkSearchResponse workSearchResponse = workSearchService
      .searchAllWorkByUserId
      (
      user.getId(),
      workSearchRequest
      );

      DashboardResultList list = workSearchResponse.getDashboardResultList();

      for (final DashboardResult result : list) {

      }

      return new FulfillmentResponse();
      }
    */

	public FulfillmentPayloadDTO getFeedPage(final ExtendedUserDetails user,
																					 final HttpServletRequest request,
																					 final FeedRequestParams params) {

		final Map<String, Object> map = assignmentService.getWorkFeed(params);

		return generateResponse(user, request, map, false);
	}

	public FulfillmentPayloadDTO getFeedPage(final ExtendedUserDetails user,
																					 final HttpServletRequest request,
																					 final String keyword,
																					 final Integer industryId,
																					 final Double latitude,
																					 final Double longitude,
																					 final Double radius,
																					 final Boolean virtual,
																					 final String[] requestedFields,
																					 final Integer page,
																					 final Integer pageSize) {

		final Map<String, Object> map = assignmentService.getWorkFeed(keyword,
																																	industryId,
																																	latitude,
																																	longitude,
																																	radius,
																																	virtual,
																																	requestedFields,
																																	page,
																																	pageSize);

		return generateResponse(user, request, map, false);
	}

	public FulfillmentPayloadDTO getAssignmentDetails(final ExtendedUserDetails user, final String workNumber) {

		final WorkResponse workResponse = assignmentService.getAssignmentDetails(workNumber);

		if (workResponse == null) {
			throw new HttpException404().setMessageKey("assignment.mobile.notfound");
		}

		if (workResponse.isWorkBundle()) {

			final long bundleId = workResponse.getWork().getId();

			if (!assignmentService.authorizeBundleView(bundleId, user)) {
				throw new HttpException401().setMessageKey("assignment_bundle.view.unauthorized")
								.setRedirectUri("redirect:/assignments");
			}

			if (!assignmentService.authorizeBundlePendingRouting(bundleId, user.getId())) {
				throw new HttpException401().setMessageKey("assignment_bundle.view.fail.pending_routing")
								.setRedirectUri("redirect:/assignments");
			}
		}

		final boolean isInternal = user.isInternal();
		java.util.Set<RequestContext> requestContexts = workResponse.getRequestContexts();
		java.util.Set<AuthorizationContext> authContexts = workResponse.getAuthorizationContexts();

		if (requestContexts == null ||
						(requestContexts.contains(RequestContext.UNRELATED) && !isInternal) ||
						authContexts.contains(AuthorizationContext.READ_ONLY)) {

			throw new MobileHttpException403().setMessageKey("assignment.mobile.notallowed");
		}

		final List<PartDTO> assignmentParts = assignmentService.getPartsForAssignmentDetails(workResponse);
		final List<CustomField> customFields = assignmentService.getHeaderCustomFieldsForAssignmentDetails(workResponse);

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		assignmentMarshaller.getDetailsFulfillmentResponse(workResponse,
																											 customFields,
																											 assignmentParts,
																											 response,
																											 user.getId());

		return response;
	}

	private FulfillmentPayloadDTO generateResponse(final ExtendedUserDetails extendedUserDetails,
																								 final HttpServletRequest request,
																								 final Map<String, Object> map,
																								 final boolean throwOnUnauthorized) {

		final List<Map<String, Object>> rows = getRows(map);
		final Map<String, Object> pagination = getPagination(map);

		final Map<String, Map<String, Object>> workNumberToRowMap = Maps.uniqueIndex(rows,
																																								 new Function<Map<String, Object>, String>() {
																																									 public String apply(Map<String, Object> obj) {
																																										 return (String) obj.get(
																																														 "work_number");
																																									 }
																																								 });

		final Map<String, WorkResponse> workNumberToWorkResponseMap = new HashMap<>();

		for (String workNumber : workNumberToRowMap.keySet()) {

			ImmutableSet<WorkRequestInfo> pricingWorkRequestInfo = ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.STATUS_INFO);

			ImmutableSet<WorkRequestInfo> noPricingWorkRequestInfo = ImmutableSet.of(WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
				WorkRequestInfo.PARTS_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.STATUS_INFO);


			final WorkResponse workResponse = xWork.getWork(
				extendedUserDetails,
				workNumber,
				extendedUserDetails.isEmployeeWorker() ? noPricingWorkRequestInfo : pricingWorkRequestInfo,
				ImmutableSet.of(
					AuthorizationContext.ACTIVE_RESOURCE,
					AuthorizationContext.ADMIN,
					AuthorizationContext.BUYER
				),
				throwOnUnauthorized
			);
			workNumberToWorkResponseMap.put(workNumber, workResponse);
		}

		final FulfillmentPayloadDTO response = new FulfillmentPayloadDTO();

		assignmentMarshaller.getListFulfillmentResponse(rows, workNumberToWorkResponseMap, response);

		ApiV2Pagination apiPagination = assignmentMarshaller.generatePaginationFromWorkControllerResponse(pagination,
																																																			request);

		response.setPagination(apiPagination);

		return response;
	}

	private List<Map<String, Object>> getRows(final Map map) {
		return (List<Map<String, Object>>) map.get(WORK_CONTROLLER_ROWS_KEY);
	}

	private Map<String, Object> getPagination(final Map map) {
		return (Map<String, Object>) map.get(WORK_CONTROLLER_PAGINATION_KEY);
	}
}
