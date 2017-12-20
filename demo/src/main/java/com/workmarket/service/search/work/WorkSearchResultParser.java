package com.workmarket.service.search.work;

import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.repository.WorkSearchableFields;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.WorkFacetResultType;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.SearchResultParser;
import com.workmarket.service.search.SearchResultParserImpl;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkSearchResultParser extends SearchResultParserImpl implements SearchResultParser<WorkSearchResponse> {

	@Override
	public WorkSearchResponse parseSolrQueryResponse(WorkSearchResponse searchResponse, AbstractSearchTransientData hydrateData, QueryResponse queryResponse) throws SearchException {
		return parseWorkSearchResponse(searchResponse, (WorkSearchTransientData)hydrateData, queryResponse);
	}

	private WorkSearchResponse parseWorkSearchResponse(WorkSearchResponse searchResponse, WorkSearchTransientData hydrateData, QueryResponse solrQueryResponse) {
		Integer totalCount = 0;
		if (MapUtils.isNotEmpty(solrQueryResponse.getFacetQuery())) {
			totalCount = solrQueryResponse.getFacetQuery().get("*:*");
		}
		searchResponse.setTotalResultsCount(totalCount);
		List<SolrWorkData> results = solrQueryResponse.getBeans(SolrWorkData.class);
		searchResponse.setResults(results);
		obfuscateAssignedResourceInfo(searchResponse, hydrateData);

		List<FacetField> facets = solrQueryResponse.getFacetFields();
		String searchType = hydrateData.getOriginalRequest().getType();
 		boolean isWorkKpiSearch = false;
		if (SearchType.WORK_KPI.toString().equals(searchType)) {
			isWorkKpiSearch = true;
		}
		WorkSearchRequest request = (WorkSearchRequest)hydrateData.getOriginalRequest();

		if (facets != null) {
			for (FacetField facetField : facets) {
				if (isStaticFacetFieldName(facetField.getName())) {
					parseFacetResult(facetField, searchResponse, true);
				} else if (isWorkKpiSearch) {
					for (WorkSearchableFields field : request.getFacetFields()) {
						if ( facetField.getName().equals(field.getName()) ) {
							parseFacetResult(facetField, searchResponse, false);
						}
					}
				}
			}
		}
		return searchResponse;
	}

	private void obfuscateAssignedResourceInfo(WorkSearchResponse searchResponse, WorkSearchTransientData hydrateData) {
		for (SolrWorkData solrWorkData : searchResponse.getResults()) {
			long companyId = hydrateData.getCurrentUser().getCompanyId();
			// Don't show resource info if the assignment is cancelled
			if (CollectionUtilities.contains(solrWorkData.getSearchableWorkStatusTypeCode(), WorkStatusType.CANCELLED)) {
				if (companyId != solrWorkData.getCompanyId() && solrWorkData.getAssignedResourceCompanyId() != null && companyId != solrWorkData.getAssignedResourceCompanyId()) {
					solrWorkData.clearAssignedResourceData();
				}
			}
		}
	}

	@Override
	protected Enum findFacetResultType(FacetField facetField) {
		for (WorkFacetResultType resultType : WorkFacetResultType.values()) {
			if (facetField.getName().equals(findFacetFieldName(resultType))) {
				return resultType;
			}
		}
		return null;
	}

	static String findFacetFieldName(WorkFacetResultType resultType) {
		switch (resultType) {
			case BUYER_USER_ID:
				return WorkSearchableFields.BUYER_USER_ID.getName();
			case WORK_STATUS_TYPE_CODE:
				return WorkSearchableFields.SEARCHABLE_WORK_STATUS_TYPE_CODE.getName();
			case WORK_SUB_STATUS_TYPE_CODE:
				return WorkSearchableFields.BUYER_LABELS_ID_DESCRIPTION.getName();
			case ASSIGNED_RESOURCE:
				return WorkSearchableFields.ASSIGNED_RESOURCE_ID.getName();
			case WORK_SUB_STATUS_TYPE_CODE_DRILL_DOWN:
				return WorkSearchableFields.BUYER_LABELS_WORK_STATUS_ID_DESCRIPTION.getName();
			case RESOURCE_WORK_STATUS_TYPE_CODE:
				return WorkSearchableFields.RESOURCE_WORK_STATUS_TYPE_CODE.getName();
			case APPLICANT_IDS:
				return WorkSearchableFields.APPLICANT_IDS.getName();
			case COUNTY_ID:
				return WorkSearchableFields.COUNTY_ID.getName();
			case COUNTY_NAME:
				return WorkSearchableFields.COUNTY_NAME.getName();
			case EXTERNAL_UNIQUE_IDS:
				return WorkSearchableFields.EXTERNAL_UNIQUE_IDS.getName();
			default:
				return "NOT SUPPORTED FACET";
		}
	}

}
