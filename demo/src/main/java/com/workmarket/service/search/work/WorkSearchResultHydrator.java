package com.workmarket.service.search.work;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.report.work.WorkSubStatusTypeCompanyConfig;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.user.PersonaPreference;
import com.workmarket.search.model.AbstractSearchTransientData;
import com.workmarket.search.model.WorkSearchTransientData;
import com.workmarket.search.response.FacetResult;
import com.workmarket.search.response.work.WorkFacetResultType;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.search.SearchResultHydrator;
import com.workmarket.service.search.SearchResultHydratorImpl;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkSearchResultHydrator extends SearchResultHydratorImpl implements SearchResultHydrator<WorkSearchResponse> {

	@Autowired private UserService userService;

	@Override
	public WorkSearchResponse hydrateSearchResult(WorkSearchResponse response, AbstractSearchTransientData hydrateData) {
		Assert.notNull(response);
		return hydrateWorkSearchResult(response, (WorkSearchTransientData) hydrateData);
	}

	private WorkSearchResponse hydrateWorkSearchResult(WorkSearchResponse response, WorkSearchTransientData hydrateData) {
		hydrateWorkStatusTypeCounts(response, hydrateData);
		hydrateWorkSubStatusTypes(response, hydrateData);
		hydrateWorkCustomFields(response, hydrateData);

		Optional<PersonaPreference> personaPreferenceOptional = userService.getPersonaPreference(hydrateData.getCurrentUser().getId());
		boolean isDispatcher = personaPreferenceOptional.isPresent() && personaPreferenceOptional.get().isDispatcher();

		if (isDispatcher) {
			hydrateCandidates(response, hydrateData);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Async
	private void hydrateWorkStatusTypeCounts(WorkSearchResponse response, WorkSearchTransientData hydrateData) {
		WorkAggregatesDTO statusAggregates = new WorkAggregatesDTO();
		Map<String, WorkAggregatesDTO> aggregatesBuyerWorkStatusDrillDownBySubStatus = Maps.newLinkedHashMap();

		List<FacetResult> workStatusFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.WORK_STATUS_TYPE_CODE, Lists.newArrayList());
		List<FacetResult> resourceWorkStatusFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.RESOURCE_WORK_STATUS_TYPE_CODE, Lists.newArrayList());
		List<FacetResult> workSubStatusFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.WORK_SUB_STATUS_TYPE_CODE, Lists.newArrayList());
		List<FacetResult> workSubStatusDrillDownFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.WORK_SUB_STATUS_TYPE_CODE_DRILL_DOWN, Lists.newArrayList());
		List<FacetResult> workApplicantsUsersIdFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.APPLICANT_IDS, Lists.newArrayList());
		List<FacetResult> externalUniqueIdsFacetResult = (List<FacetResult>) MapUtils.getObject(response.getFacets(), WorkFacetResultType.EXTERNAL_UNIQUE_IDS, Lists.newArrayList());
		int appliedAssignments = 0;

		//Parse work statuses
		if (isNotEmpty(workStatusFacetResult)) {
			for (FacetResult facetResult : workStatusFacetResult) {
				statusAggregates.addToStatusCount(facetResult.getFacetId(), Long.valueOf(facetResult.getFacetCount()).intValue());
			}
		}

		if (isNotEmpty(externalUniqueIdsFacetResult)) {
			for (FacetResult facetResult : externalUniqueIdsFacetResult) {
				statusAggregates.addToStatusCount(facetResult.getFacetId(), Long.valueOf(facetResult.getFacetCount()).intValue());
			}
		}

		if (isNotEmpty(workApplicantsUsersIdFacetResult)) {
			for (FacetResult facetResult : workApplicantsUsersIdFacetResult) {
				if (Long.valueOf(facetResult.getFacetId()).equals(hydrateData.getCurrentUser().getId())) {
					appliedAssignments = Long.valueOf(facetResult.getFacetCount()).intValue();
					statusAggregates.addToStatusCount(WorkStatusType.APPLIED, appliedAssignments);
				}
			}
		}

		//Parse resource work statuses
		if (isNotEmpty(resourceWorkStatusFacetResult)) {
			for (FacetResult facetResult : resourceWorkStatusFacetResult) {
				statusAggregates.addToStatusCount(facetResult.getFacetId(), Long.valueOf(facetResult.getFacetCount()).intValue());
			}
		}

		int available = statusAggregates.getCountForStatus(WorkStatusType.AVAILABLE);
		statusAggregates.setCountForStatus(WorkStatusType.AVAILABLE, available - appliedAssignments);

		int sent = statusAggregates.getCountForStatus(WorkStatusType.SENT);
		int newSent = sent - appliedAssignments - available;
		if (newSent >0 ) {
			statusAggregates.setCountForStatus(WorkStatusType.SENT, newSent);
		} else {
			statusAggregates.getCounts().remove(WorkStatusType.SENT);
		}

		//Parse work sub statuses
		if (isNotEmpty(workSubStatusFacetResult)) {
			for (FacetResult facetResult : workSubStatusFacetResult) {
				WorkSubStatusTypeReportRow labelRow = parseCompanyWorkSubStatusTypeReportRow(facetResult.getFacetId(), hydrateData);
				labelRow.setCount(Long.valueOf(facetResult.getFacetCount()).intValue());
				statusAggregates.setCountForWorkSubStatus(labelRow.getWorkSubStausTypeId(), labelRow);
			}
		}

		//Parse work sub status by work status
		if (isNotEmpty(workSubStatusDrillDownFacetResult)) {
			for (FacetResult facetResult : workSubStatusDrillDownFacetResult) {
				//Example: 1_active_1234_iyogi = <companyId>_<status>_<labelId>_<labelName>
				String labelWithStatus = StringUtils.substringAfter(facetResult.getFacetId(), FIELD_SEPARATOR);
				if (StringUtils.isNotBlank(labelWithStatus)) {
					String statusTypeCode = StringUtils.substringBefore(labelWithStatus, FIELD_SEPARATOR);
					WorkSubStatusTypeReportRow labelRow = parseWorkSubStatusTypeReportRow(StringUtils.substringAfter(labelWithStatus, FIELD_SEPARATOR), hydrateData);
					labelRow.setCount(Long.valueOf(facetResult.getFacetCount()).intValue());

					if (aggregatesBuyerWorkStatusDrillDownBySubStatus.containsKey(statusTypeCode)) {
						aggregatesBuyerWorkStatusDrillDownBySubStatus.get(statusTypeCode).setCountForWorkSubStatus(labelRow.getWorkSubStausTypeId(), labelRow);
					} else {
						WorkAggregatesDTO dto = new WorkAggregatesDTO();
						dto.setCountForWorkSubStatus(labelRow.getWorkSubStausTypeId(), labelRow);
						aggregatesBuyerWorkStatusDrillDownBySubStatus.put(statusTypeCode, dto);
					}
				}
			}
		}

		response.setAggregates(statusAggregates);
		response.setAggregatesBuyerWorkStatusDrillDownBySubStatus(aggregatesBuyerWorkStatusDrillDownBySubStatus);
	}

	private WorkSubStatusTypeReportRow parseWorkSubStatusTypeReportRow(String labelIdDescriptionCombo, WorkSearchTransientData hydrateData) {
		WorkSubStatusTypeReportRow labelRow = new WorkSubStatusTypeReportRow();
		if (StringUtils.isNotBlank(labelIdDescriptionCombo)) {
			String labelId = StringUtils.substringAfter(labelIdDescriptionCombo, FIELD_SEPARATOR);
			if (StringUtils.isNumeric(labelId)) {
				String description = StringUtils.substringBefore(labelIdDescriptionCombo, FIELD_SEPARATOR);
				labelRow.setWorkSubStausTypeId(Long.valueOf(labelId));
				labelRow.setDescription(description);

				WorkSubStatusTypeCompanyConfig labelConfig = hydratorCache.getLabelDisplayInfo(Long.valueOf(labelId), hydrateData.getCurrentUser().getCompanyId());
				labelRow.setColorRgb(labelConfig.getColorRgb());
				labelRow.setDashboardDisplayType(labelConfig.getDashboardDisplayType());
			}
		}
		return labelRow;
	}

	private WorkSubStatusTypeReportRow parseCompanyWorkSubStatusTypeReportRow(String companyIdLabelIdDescriptionCombo, WorkSearchTransientData hydrateData) {
		WorkSubStatusTypeReportRow labelRow = new WorkSubStatusTypeReportRow();
		if (StringUtils.isNotBlank(companyIdLabelIdDescriptionCombo)) {
			String label = StringUtils.substringAfter(companyIdLabelIdDescriptionCombo, FIELD_SEPARATOR);
			return parseWorkSubStatusTypeReportRow(label, hydrateData);
		}
		return labelRow;
	}

	@SuppressWarnings("unchecked")
	@Async
	private void hydrateWorkCustomFields(WorkSearchResponse response, WorkSearchTransientData hydrateData) {
		List<Long> workIds = convert(response.getResults(), new PropertyExtractor("id"));
		if (isEmpty(workIds)) {
			return;
		}
		long companyId = hydrateData.getCurrentUser().getCompanyId();
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = hydratorCache.getDashboardOwnerWorkCustomFieldsMap(companyId, workIds);

		for (SolrWorkData solrWorkData : response.getResults()) {
			List<CustomFieldReportRow> customFields = (List<CustomFieldReportRow>) MapUtils.getObject(customFieldsMap, solrWorkData.getWorkId(), Collections.EMPTY_LIST);
			if (isNotEmpty(customFields)) {
				if (solrWorkData.getCompanyId() == companyId) {
					solrWorkData.setCustomFields(customFields);
				} else {
					List<CustomFieldReportRow> resourceCustomFields = Lists.newArrayListWithExpectedSize(customFields.size());
					for (CustomFieldReportRow customField : customFields) {
						boolean isResource = CollectionUtilities.contains(solrWorkData.getWorkResourceIds(), hydrateData.getCurrentUser().getId());
						boolean isActiveResource = hydrateData.getCurrentUser().getId().equals(solrWorkData.getAssignedResourceId());
						boolean visibleToActiveWorker = customField.isVisibleToResource() && isActiveResource;
						boolean visibleToSentWorkers = customField.isVisibleToResource() && solrWorkData.isSent() && customField.isShowOnSent() && isResource;
						if (visibleToActiveWorker || visibleToSentWorkers) {
							resourceCustomFields.add(customField);
						}
					}
					solrWorkData.setCustomFields(resourceCustomFields);
				}
			}
		}
	}

	@Async
	private void hydrateWorkSubStatusTypes(WorkSearchResponse response, WorkSearchTransientData hydrateData) {
		for (SolrWorkData solrWorkData : response.getResults()) {
			if (isNotEmpty(solrWorkData.getBuyerLabelsIdDescription())) {
				List<WorkSubStatusTypeReportRow> workSubStatusTypeReportRows = Lists.newArrayListWithExpectedSize(solrWorkData.getBuyerLabelsIdDescription().size());
				for (String labelIdDescriptionCombo : solrWorkData.getBuyerLabelsIdDescription()) {
					if (solrWorkData.getCompanyId().equals(hydrateData.getCurrentUser().getCompanyId())) {
						workSubStatusTypeReportRows.add(parseCompanyWorkSubStatusTypeReportRow(labelIdDescriptionCombo, hydrateData));
					}
				}
				solrWorkData.setWorkSubStatusTypes(workSubStatusTypeReportRows);
			}
		}
	}

	@Async
	private void hydrateCandidates(WorkSearchResponse response, WorkSearchTransientData hydrateData) {
		for (SolrWorkData solrWorkData : response.getResults()) {
			String workStatusTypeCode = solrWorkData.getWorkStatusTypeCode();
			if (!WorkStatusType.SENT.equals(workStatusTypeCode) && !WorkStatusType.AVAILABLE.equals(workStatusTypeCode)) {
				continue;
			}
			List<String> workResourceNames = solrWorkData.getWorkResourceNames();
			if (isNotEmpty(workResourceNames)) {
				List<String> dispatchCandidateNames = Lists.newArrayListWithExpectedSize(workResourceNames.size());
				List<Long> workResourceCompanyIds = solrWorkData.getWorkResourceCompanyIds();
				Long currentUserCompanyId = hydrateData.getCurrentUser().getCompanyId();
				for (int ix = 0; ix < workResourceNames.size(); ix++) {
					Long workResourceCompanyId = workResourceCompanyIds.get(ix);
					if (currentUserCompanyId.equals(workResourceCompanyId)) {
						dispatchCandidateNames.add(workResourceNames.get(ix));
					}
				}
				solrWorkData.setDispatchCandidateNames(dispatchCandidateNames);
			}
		}
	}
}
