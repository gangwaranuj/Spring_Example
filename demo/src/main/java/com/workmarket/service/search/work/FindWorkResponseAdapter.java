package com.workmarket.service.search.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.search.gen.Common.Status;
import com.workmarket.search.gen.Common.Facet;
import com.workmarket.search.gen.Common.FacetCount;
import com.workmarket.search.gen.WorkMessages.FindWorkResponse;
import com.workmarket.search.gen.WorkMessages.Work;
import com.workmarket.search.model.SearchType;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.work.WorkFacetResultType;
import com.workmarket.search.response.work.WorkSearchResponse;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.workmarket.service.search.SearchResultConstants.STATIC_FACET_FIELD_NAMES;
import static com.workmarket.service.search.work.WorkSearchResultParser.findFacetFieldName;
import static com.workmarket.utility.DateUtilities.getDateFromISO8601;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class FindWorkResponseAdapter {

	public WorkSearchResponse buildWorkSearchResponse(
		final WorkSearchTransientData transientData,
		final FindWorkResponse response
	) {
		final WorkSearchResponse workSearchResponse = new WorkSearchResponse();
		final Status status = response.getStatus();
		if (!status.getSuccess() || response.getNumFound() == 0) {
			workSearchResponse.setTotalResultsCount(0);
			return workSearchResponse;
		}
		// set matched docs
		workSearchResponse.setTotalResultsCount((int) response.getNumFound());
		List<SolrWorkData> docs = Lists.newArrayList();
		for (Work work : response.getDocsList()) {
			docs.add(buildSolrWorkData(work, transientData));
		}
		workSearchResponse.setResults(docs);
		// set facets
		final String searchType = transientData.getOriginalRequest().getType();
		boolean isWorkKpiSearch = SearchType.WORK_KPI.toString().equals(searchType);
		final Map<WorkFacetResultType, List<FacetResult>> facets =
			parseFacetResult(response.getFacetsList(), isWorkKpiSearch);
		for (Map.Entry<WorkFacetResultType, List<FacetResult>> entry : facets.entrySet()) {
			workSearchResponse.putToFacets(entry.getKey(), entry.getValue());
		}
		return workSearchResponse;
	}



	// Can we have an easy way to do this?
	private SolrWorkData buildSolrWorkData(final Work work, final WorkSearchTransientData transientData) {
		SolrWorkData solrWorkData = new SolrWorkData();
		solrWorkData.setId(work.getId());
		solrWorkData.setWorkNumber(work.getWorkNumber());
		solrWorkData.setCompanyId(work.getCompanyId());
		solrWorkData.setCompanyName(work.getCompanyName());
		solrWorkData.setTitle(work.getTitle());
		solrWorkData.setTimeZoneId(work.getTimeZoneId());
		solrWorkData.setPublicTitle(work.getPublicTitle());
		solrWorkData.setDescription(work.getDescription());
		solrWorkData.setInstructions(work.getInstructions());
		solrWorkData.setSkills(work.getSkills());
		solrWorkData.setIndustryId(work.getIndustryId());
		solrWorkData.setShowInFeed(work.getShowInFeed());

		// buyer id missing
		solrWorkData.setBuyerFullName(work.getBuyerFullName());

		solrWorkData.setWorkStatusTypeCode(work.getWorkStatusTypeCode());
		solrWorkData.setWorkStatusTypeDescription(work.getWorkStatusTypeDescription());
		solrWorkData.setSearchableWorkStatusTypeCode(work.getSearchableWorkStatusTypeCodeList());
		solrWorkData.setResourceWorkStatusTypeCode(work.getResourceWorkStatusTypeCodeList());

		solrWorkData.setContactName(work.getContactName());
		solrWorkData.setContactPhone(work.getContactPhone());
		solrWorkData.setContactEmail(work.getContactEmail());
		solrWorkData.setSupportName(work.getSupportName());
		solrWorkData.setSupportPhone(work.getSupportPhone());
		solrWorkData.setSupportEmail(work.getSupportEmail());

		solrWorkData.setWorkResourceIds(work.getWorkResourceIdsList());
		solrWorkData.setWorkResourceCompanyIds(work.getWorkResourceCompanyIdsList());
		solrWorkData.setWorkResourceNames(work.getWorkResourceNamesList());
		solrWorkData.setApplicantIds(work.getApplicantIdsList());
		solrWorkData.setCancelledWorkResourceIds(work.getCancelledWorkResourceIdsList());
		solrWorkData.setBuyerCustomFieldNames(work.getBuyerCustomFieldNamesList());
		solrWorkData.setBuyerCustomFieldValues(work.getBuyerCustomFieldValuesList());

		solrWorkData.setBuyerUserId(work.getBuyerUserId());
		solrWorkData.setCreatorUserId(work.getCreatorUserId());
		solrWorkData.setDispatcherId(work.getDispatcherId());

		solrWorkData.setClientLocationId(work.getClientLocationId());
		solrWorkData.setClientLocationName(work.getClientLocationName());
		solrWorkData.setClientLocationNumber(work.getClientLocationNumber());

		solrWorkData.setCreatedDate(getDateFromISO8601(work.getCreatedDate()));
		solrWorkData.setScheduleFromDate(getDateFromISO8601(work.getScheduleFromDate()));
		solrWorkData.setApprovedDate(getDateFromISO8601(work.getApprovedDate()));
		solrWorkData.setSendDate(getDateFromISO8601(work.getSendDate()));
		solrWorkData.setPaidDate(getDateFromISO8601(work.getPaidDate()));
		solrWorkData.setDueDate(getDateFromISO8601(work.getDueDate()));
		solrWorkData.setCompletedDate(getDateFromISO8601(work.getCompletedDate()));
		solrWorkData.setScheduleFromDate(getDateFromISO8601(work.getScheduleFromDate()));

		solrWorkData.setOffSite(work.getOffSite());
		solrWorkData.setCity(work.getCity());
		solrWorkData.setState(work.getState());
		solrWorkData.setPostalCode(work.getPostalCode());
		solrWorkData.setCountry(work.getCountry());
		solrWorkData.setLongitude(work.getLongitude());
		solrWorkData.setLatitude(work.getLatitude());
		solrWorkData.setLocation(work.getLocation());
		solrWorkData.setCountyId(work.getCountyId());
		solrWorkData.setCountyName(work.getCountyName());

		solrWorkData.setProjectId(work.getProjectId());
		solrWorkData.setProjectName(work.getProjectName());

		solrWorkData.setBuyerFee(work.getBuyerFee());
		solrWorkData.setWorkPrice(work.getWorkPrice());
		solrWorkData.setAmountEarned(work.getAmountEarned());
		solrWorkData.setWorkFeePercentage(work.getWorkFeePercentage());
		solrWorkData.setAutoPayEnabled(work.getAutoPayEnabled());
		solrWorkData.setPricingType(work.getPricingType());

		solrWorkData.setModifierFirstName(work.getModifierFirstName());
		solrWorkData.setModifierLastName(work.getModifierLastName());
		solrWorkData.setLastModifiedDate(getDateFromISO8601(work.getLastModifiedDate()));

		//hydrate assignment resources if work is not cancelled or work is from my company or assigned to my company
		final Long companyId = transientData.getCurrentUser().getCompanyId();
		if ((isNotEmpty(solrWorkData.getSearchableWorkStatusTypeCode())
			 && !solrWorkData.getSearchableWorkStatusTypeCode().contains(WorkStatusType.CANCELLED))
			|| companyId == work.getCompanyId() || companyId == work.getAssignedResourceCompanyId()) {
			solrWorkData.setAssignedResourceId(work.getAssignedResourceId());
			solrWorkData.setAssignedResourceCompanyId(work.getAssignedResourceCompanyId());
			solrWorkData.setAssignedResourceCompanyName(work.getAssignedResourceCompanyName());
			solrWorkData.setAssignedResourceFirstName(work.getAssignedResourceFirstName());
			solrWorkData.setAssignedResourceLastName(work.getAssignedResourceLastName());
			solrWorkData.setAssignedResourceUserNumber(work.getAssignedResourceUserNumber());
			solrWorkData.setAssignedResourceMobile(work.getAssignedResourceMobile());
			solrWorkData.setAssignedResourceWorkPhoneNumber(work.getAssignedResourceWorkPhoneNumber());
			solrWorkData.setAssignedResourceWorkPhoneExtension(work.getAssignedResourceWorkPhoneExtension());
		}

		solrWorkData.setBuyerLabelsId(work.getBuyerLabelsIdList());
		solrWorkData.setBuyerLabelsIdDescription(work.getBuyerLabelsIdDescriptionList());
		solrWorkData.setBuyerLabelsWorkStatusIdDescription(work.getBuyerLabelsWorkStatusIdDescriptionList());

		solrWorkData.setConfirmed(work.getConfirmed());
		solrWorkData.setResourceConfirmationRequired(work.getResourceConfirmationRequired());
		solrWorkData.setAssignToFirstResource(work.getAssignToFirstResource());
		// applied and applicationsPending are missing

		solrWorkData.setParentId(work.getParentId());
		solrWorkData.setParentTitle(work.getParentTitle());
		solrWorkData.setParentDescription(work.getParentDescription());

		solrWorkData.setAssignedResourceAppointmentFrom(getDateFromISO8601(work.getAssignedResourceAppointmentFrom()));
		solrWorkData.setAssignedResourceAppointmentThrough(getDateFromISO8601(work.getAssignedResourceAppointmentThrough()));

		solrWorkData.setFollowerIds(work.getFollowerIdsList());
		solrWorkData.setRoutedToGroups(work.getRoutedToGroupsList());
		solrWorkData.setUniqueExternalId(work.getUniqueExternalId());
		solrWorkData.setRecurrenceUUID(work.getRecurrenceUUID());
		solrWorkData.setExternalUniqueIds(work.getExternalUniqueIdsList());

		return solrWorkData;
	}

	private Map<WorkFacetResultType, List<FacetResult>> parseFacetResult(
		final List<Facet> facets,
		final boolean isWorkKpSearch
	) {
		Map<WorkFacetResultType, List<FacetResult>> result = Maps.newHashMap();
		for (Facet facet : facets) {
			if (facet.getFacetCountsCount() == 0) {
				continue;
			}
			final String facetField = facet.getField();
			final WorkFacetResultType resultType = getWorkFacetResultType(facetField);
			if (resultType == null) {
				continue;
			}
			boolean isStaticFacetField =  ArrayUtils.contains(STATIC_FACET_FIELD_NAMES, facetField);
			boolean shouldExtractFacet = isStaticFacetField || isWorkKpSearch;
			if (shouldExtractFacet) {
				List<FacetResult> facetResults = extractFacetResult(facet.getFacetCountsList(), isStaticFacetField);
				if (!facetResults.isEmpty()) {
					result.put(resultType, facetResults);
				}
			}
		}
		return result;
	}

	private WorkFacetResultType getWorkFacetResultType(final String facetField) {
		for (WorkFacetResultType resultType : WorkFacetResultType.values()) {
			if (findFacetFieldName(resultType).equals(facetField)) {
				return resultType;
			}
		}
		return null;
	}

	private List<FacetResult> extractFacetResult(final List<FacetCount> facetCounts, final boolean includeZeroCount) {
		List<FacetResult> facetResults = Lists.newArrayList();
		for (FacetCount facetCount : facetCounts) {
			if (StringUtils.isNotBlank(facetCount.getValue()) && !"null".equals(facetCount.getValue()) //lucene quirkiness
				&& (includeZeroCount || facetCount.getCount() > 0)) {
				FacetResult facetResult = new FacetResult()
					.setFacetCount(facetCount.getCount())
					.setFacetId(facetCount.getValue());
				facetResults.add(facetResult);
			}
		}
		return facetResults;
	}
}
