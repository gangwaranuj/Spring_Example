package com.workmarket.data.solr.query;

import com.workmarket.data.solr.query.location.LocationQueryCreationService;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.model.query.SearchQuery;
import com.workmarket.search.model.query.WorkSearchQuery;
import com.workmarket.search.request.SearchRequest;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.request.work.WorkSearchType;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.external.GeocodingException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
@Component
public class WorkSearchQueryCreator implements SearchQueryCreator<WorkSearchTransientData> {

	@Autowired LocationQueryCreationService locationQueryCreationService;

	@Override
	public SearchQuery createSearchQuery(WorkSearchTransientData data) throws SearchException {
		return createSearchQuery(data, null);
	}

	@Override
	public <S extends SearchRequest> SearchQuery createSearchQuery(WorkSearchTransientData data, S request) throws SearchException {
		Assert.isInstanceOf(WorkSearchRequest.class, request);
		WorkSearchRequest workRequest = (WorkSearchRequest) request;
		WorkSearchQuery query = new WorkSearchQuery(workRequest);

		if (data.getCurrentUser() != null) {
			if (data.getCurrentUser().getId() != null) {
				query.add(SolrMetricConstants.USER, data.getCurrentUser().getId().toString());
			}
			if (data.getCurrentUser().getCompanyId() != null) {
				query.add(SolrMetricConstants.COMPANY, data.getCurrentUser().getCompanyId().toString());
			}
		}

		if (((WorkSearchRequest) request).isMobile()) {
			query.add(SolrMetricConstants.REQUEST_SOURCE, SolrMetricConstants.MOBILE_REQUEST);
		} else {
			query.add(SolrMetricConstants.REQUEST_SOURCE, SolrMetricConstants.WEB_REQUEST);
		}
		if (data.getUserType() != null) {
			if (data.getUserType() == WorkSearchRequestUserType.CLIENT) {
				query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.EMPLOYER_PERSONA);
			} else if (data.getUserType() == WorkSearchRequestUserType.RESOURCE){
				query.add(SolrMetricConstants.PERSONA, SolrMetricConstants.WORKER_PERSONA);
			}
		}

		WorkSearchType workSearchType = ((WorkSearchRequest) request).getWorkSearchType();
		if (workSearchType != null) {
			query.add(SolrMetricConstants.SEARCH_TYPE, workSearchType.name().toLowerCase());
		}

		if (data.isSetGeopoint()) {
			try {
				query.addLocationQuery(locationQueryCreationService.addLocationQuery(data, query));
			} catch (GeocodingException e) {
				locationQueryCreationService.setGeoCodingWarning(data, e, query);
			}
		}

		if (SearchRequest.ALL.equals(request.getType()) || StringUtils.isBlank(request.getType())) {
			query.addWorkSearchStaticQueryOptions(data)
				.addWorkKeywordFilter(data)
				.addBaseFilterQueries(data)
				.addMultipleWorkStatusFilterQuery()
				.addIgnoreVirtualWorkQuery()
				.addWorkSubStatusTypeFilter()
				.addWorkBeginEndDateFilter()
				.addWorkClientFilter()
				.addWorkProjectFilter()
				.addWorkInternalOwnerFilter()
				.addWorkAssignedResourceFilter()
				.addBundleFilter()
				.addFollowingAssignmentsFilter()
				.addDispatcherFilter()
				.addDecisionFlowsFilter()
				.setFilters()

				//SORTS
				.addSort()
				.setStart(workRequest.getStartRow())
				.setRows(workRequest.getPageSize());

		} else if (SearchType.WORK_KPI.toString().equals(request.getType())) {
			query
				.addFacetFieldsWithDefaults()
				.addWorkBeginEndDateFilter()
				.addGenericFilterQuery(WorkSearchableFields.WORK_STATUS_TYPE_CODE.getName(), WorkStatusType.PAID)
				.addGenericFilterQuery(WorkSearchableFields.COMPANY_ID.getName(), data.getCompanyId().toString())
				.addGenericFilterQuery(WorkSearchableFields.COUNTY_ID.getName(), "[* TO *]"); // Filter out null entries
		} else {
			String keyword = request.getKeyword();
			query.addBaseFilterQueries(data)
				.setQuery(String.format("%s:%s", request.getType(), keyword))
				.set("qf", request.getType())
				.set("rows", 10)
				.set("op", "AND");
		}
		return query;
	}
}
