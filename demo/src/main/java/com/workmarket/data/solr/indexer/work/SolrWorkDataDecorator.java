package com.workmarket.data.solr.indexer.work;

import ch.lambdaj.function.convert.PropertyExtractor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.work.dao.WorkResourceDAO;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.dao.state.WorkSubStatusDAO;
import com.workmarket.domains.work.dao.RoutingStrategyDAO;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.data.solr.indexer.SolrDataDecorator;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.service.business.dto.WorkResourceDTO;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class SolrWorkDataDecorator implements SolrDataDecorator<SolrWorkData> {

	@Autowired private WorkResourceDAO workResourceDAO;
	@Autowired private WorkSubStatusDAO workSubStatusDAO;
	@Autowired private WorkCustomFieldDAO workCustomFieldDAO;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private WorkNegotiationDAO workNegotiationDAO;
	@Autowired private RoutingStrategyDAO routingStrategyDAO;
	@Autowired private WorkVendorInvitationDAO workVendorInvitationDAO;

	@SuppressWarnings("unchecked") @Override
	public Collection<SolrWorkData> decorate(Collection<SolrWorkData> solrDataList) {
		if (CollectionUtils.isEmpty(solrDataList)) {
			return solrDataList;
		}
		List<Long> workIds = convert(solrDataList, new PropertyExtractor("workId"));

		CustomFieldReportFilters customFieldReportFilters = new CustomFieldReportFilters();
		customFieldReportFilters.setVisibleToBuyer(true);
		customFieldReportFilters.setWorkIds(workIds);
		Map<Long, List<CustomFieldReportRow>> customFieldsMap = workCustomFieldDAO.getWorkCustomFieldsMap(customFieldReportFilters);

		Map<Long, List<WorkSubStatusType>> workUnresolvedLabelsMap = workSubStatusDAO.findAllUnresolvedSubStatusType(workIds);

		Map<Long, List<Long>> groupsRoutedTo = routingStrategyDAO.findAllGroupsRoutedByWork(workIds);

		for (SolrWorkData solrData : solrDataList) {
			decorateWorkStatusType(workUnresolvedLabelsMap, solrData);
			decorateWorkResources(solrData);
			decorateWorkCustomFields(customFieldsMap, solrData);
			decorateWorkFollowers(solrData);
			decorateRouting(groupsRoutedTo, solrData);
		}
		return solrDataList;
	}

	@Override
	public SolrWorkData decorate(SolrWorkData solrData) {
		if (solrData != null) {
			Collection<SolrWorkData> solrWorkDataList = decorate(Lists.newArrayList(solrData));
			if (isNotEmpty(solrWorkDataList)) {
				return ((List<SolrWorkData>) solrWorkDataList).get(0);
			}
		}
		return solrData;
	}

	@Async
	private void decorateWorkStatusType(Map<Long, List<WorkSubStatusType>> workUnresolvedLabelsMap, SolrWorkData solrData) {
		Set<String> searchableWorkStatus = Sets.newHashSet(solrData.getWorkStatusTypeCode());
		Set<String> resourceWorkStatus = Sets.newHashSet();
		List<WorkSubStatusType> subStatusTypes = Lists.newArrayList();
		if (workUnresolvedLabelsMap.containsKey(solrData.getWorkId())) {
			subStatusTypes = workUnresolvedLabelsMap.get(solrData.getWorkId());
		}

		List<Long> applicants = workNegotiationDAO.findAllApplicantsPendingApproval(solrData.getWorkId());
		if (WorkStatusType.SENT.equals(solrData.getWorkStatusTypeCode())) {
			resourceWorkStatus.add(WorkStatusType.AVAILABLE);
			if (solrData.isOpenNegotiations() && isNotEmpty(applicants)) {
				searchableWorkStatus.add(WorkStatusType.SENT_WITH_OPEN_NEGOTIATIONS);
			}
			if (solrData.isOpenQuestions()) {
				searchableWorkStatus.add(WorkStatusType.SENT_WITH_OPEN_QUESTIONS);
			}
			if (isNotEmpty(applicants)) {
				solrData.setApplicantIds(applicants);
			}
		}
		if (WorkStatusType.CANCELLED_PAYMENT_PENDING.equals(solrData.getWorkStatusTypeCode())) {
			solrData.setWorkStatusTypeCode(WorkStatusType.CANCELLED);
			searchableWorkStatus.add(WorkStatusType.CANCELLED);
		}
		if (WorkStatusType.CANCELLED_WITH_PAY.equals(solrData.getWorkStatusTypeCode())) {
			solrData.setWorkStatusTypeCode(WorkStatusType.PAID);
			searchableWorkStatus.add(WorkStatusType.PAID);
		}

		//Labels
		List<String> buyerLabelsIdDescription = Lists.newArrayListWithExpectedSize(subStatusTypes.size());
		List<String> buyerLabelsWorkStatusIdDescription = Lists.newArrayListWithExpectedSize(subStatusTypes.size());
		List<Long> buyerLabelsIds = Lists.newArrayListWithExpectedSize(subStatusTypes.size());

		for (WorkSubStatusType workSubStatusType : subStatusTypes) {
			//Eg. 1_Alert_1001
			buyerLabelsIdDescription.add(solrData.getCompanyId() + "_" + workSubStatusType.getDescription() + "_" + workSubStatusType.getId());
			buyerLabelsIds.add(workSubStatusType.getId());
			if (workSubStatusType.isAlert() && !WorkStatusType.HIDE_ALERT_WORK_STATUS_TYPES.contains(solrData.getWorkStatusTypeCode())) {
				searchableWorkStatus.add(WorkStatusType.EXCEPTION);
			}
			for (String workStatus : searchableWorkStatus) {
				//Eg. 1_active_Alert_1001
				buyerLabelsWorkStatusIdDescription.add(solrData.getCompanyId() + "_" + workStatus + "_" + workSubStatusType.getDescription() + "_" + workSubStatusType.getId());
			}
		}
		solrData.setBuyerLabelsId(buyerLabelsIds);
		solrData.setBuyerLabelsIdDescription(buyerLabelsIdDescription);
		solrData.setBuyerLabelsWorkStatusIdDescription(buyerLabelsWorkStatusIdDescription);
		solrData.setSearchableWorkStatusTypeCode(Lists.newArrayList(searchableWorkStatus));
		solrData.setResourceWorkStatusTypeCode(Lists.newArrayList(resourceWorkStatus));

	}

	@Async
	private void decorateWorkResources(SolrWorkData solrData) {
		List<WorkResourceDTO> resources = workResourceDAO.findAllResourcesForWorkSolrReindexOnly(solrData.getWorkId());
		List<Long> cancelledResourceIds = Lists.newArrayList();
		for (WorkResourceDTO resource : resources) {
			if (WorkResourceStatusType.CANCELLED.equals(resource.getWorkResourceStatusTypeCode()) ||
					WorkResourceStatusType.DECLINED.equals(resource.getWorkResourceStatusTypeCode())) {
				cancelledResourceIds.add(resource.getUserId());
			}
			solrData.getWorkResourceIds().add(resource.getUserId());
			solrData.getWorkResourceCompanyIds().add(resource.getCompanyId());
			solrData.getWorkResourceNames().add(resource.getFullName());
			if (resource.isAssignedToWork()) {
				solrData.setAssignedResourceFirstName(resource.getFirstName());
				solrData.setAssignedResourceLastName(resource.getLastName());
				solrData.setAssignedResourceId(resource.getUserId());
				solrData.setAssignedResourceCompanyId(resource.getCompanyId());
				solrData.setAssignedResourceUserNumber(resource.getUserNumber());
				solrData.setAssignedResourceCompanyName(resource.getCompanyName());
				solrData.setAssignedResourceAppointmentFrom(resource.getAppointmentFrom());
				solrData.setAssignedResourceAppointmentThrough(resource.getAppointmentThrough());
				solrData.setAssignedResourceMobile(resource.getMobilePhoneNumber());
				solrData.setAssignedResourceWorkPhoneNumber(resource.getWorkPhoneNumber());
				solrData.setAssignedResourceWorkPhoneExtension(resource.getWorkPhoneExtension());
			}
		}
		// add invited vendors after those associated with workers, which are expected to be paired by hydrateCandidates()
		List<Long> workVendorIds = workVendorInvitationDAO.getNotDeclinedVendorIdsByWork(solrData.getWorkId());
		if (!workVendorIds.isEmpty()) {
			solrData.getWorkResourceCompanyIds().addAll(workVendorIds);
		}
		solrData.setCancelledWorkResourceIds(cancelledResourceIds);
	}

	@Async
	private void decorateWorkCustomFields(Map<Long, List<CustomFieldReportRow>> customFieldsMap, SolrWorkData solrData) {
		if (customFieldsMap.containsKey(solrData.getWorkId())) {
			List<String> customFieldNames = Lists.newArrayList();
			List<String> customFieldValues = Lists.newArrayList();
			List<CustomFieldReportRow> customFields = customFieldsMap.get(solrData.getWorkId());
			for (CustomFieldReportRow customFieldReportRow : customFields) {
				customFieldNames.add(customFieldReportRow.getFieldName());
				customFieldValues.add(customFieldReportRow.getFieldValue());
			}
			solrData.setBuyerCustomFieldNames(customFieldNames);
			solrData.setBuyerCustomFieldValues(customFieldValues);
		}
	}

	@Async
	private void decorateWorkFollowers(SolrWorkData solrData) {
		List<WorkFollow> workFollows = workFollowService.getWorkFollowers(solrData.getId());
		List<String> followerIds = Lists.newArrayList();

		for (WorkFollow workFollow : workFollows) {
			followerIds.add(SearchUtilities.encodeId(workFollow.getUser().getId()));
		}
		solrData.setFollowerIds(followerIds);
	}

	@Async
	private void decorateRouting(Map<Long, List<Long>> groupsRoutedTo, SolrWorkData solrData) {
		if (groupsRoutedTo.containsKey(solrData.getWorkId())) {
			solrData.setRoutedToGroups(groupsRoutedTo.get(solrData.getWorkId()));
		}
	}
}
