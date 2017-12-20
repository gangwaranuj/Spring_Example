package com.workmarket.domains.work.service.dashboard;

import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.search.response.work.DashboardAddress;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.search.response.work.DashboardResultFlags;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.SearchUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

@Service
public class WorkDashboardResultParserImpl implements WorkDashboardResultParser {

	@Override
	public DashboardResultList parseResult(Collection<? extends SolrWorkData> workList, DashboardResultList resultList) {
		Assert.notNull(resultList);
		for (SolrWorkData row : workList) {
			resultList.addToResults(parseResult(row));
		}
		return resultList;
	}

	private DashboardResult parseResult(SolrWorkData row) {
		DashboardResult result = new DashboardResult();
		result.setId(row.getId());
		result.setAddress(parseAddress(row));
		if (row.getAmountEarned() != null) {
			result.setAmountEarned(row.getAmountEarned());
		}
		if (row.getBuyerFullName() != null) {
			result.setBuyerFullName(row.getBuyerFullName());
		}
		if (StringUtils.isNotBlank(row.getBuyerUserId())) {
			result.setBuyerId(SearchUtilities.decodeId(row.getBuyerUserId()));
		}
		if (row.getWorkStatusTypeCode() != null) {
			result.setWorkStatusTypeCode(row.getWorkStatusTypeCode());
		}
		if (row.getWorkStatusTypeDescription() != null) {
			result.setWorkStatusTypeDescription(row.getWorkStatusTypeDescription());
		}
		if (row.getClientCompanyName() != null) {
			result.setClient(row.getClientCompanyName());
		}
		if (row.getCreatedOn() != null) {
			result.setCreatedOn(row.getCreatedOn().getTimeInMillis());
		}
		if (row.getSendDate() != null) {
			result.setSentDate(row.getSendDate().getTime());
		}
		if (row.getCompletedDate() != null) {
			result.setCompletedDate(row.getCompletedDate().getTime());
		}

		Map<String, String> customFieldMap = CollectionUtilities.extractKeyValues(row.getCustomFields(), "fieldName", "fieldValue");
		result.setCustomFieldMap(customFieldMap);
		result.setUnresolvedWorkSubStatuses(row.getWorkSubStatusTypes());
		if (row.getDueDate() != null) {
			result.setDueDate(row.getDueDate().getTime());
		}
		if (row.getPaymentTermsDays() != null) {
			result.setPaymentTermsDays(row.getPaymentTermsDays());
		}
		if (row.getInvoiceId() != null) {
			result.setInvoiceId(row.getInvoiceId());
		}
		if (row.getInvoiceNumber() != null) {
			result.setInvoiceNumber(row.getInvoiceNumber());
		}
		result.setModifiedOn(row.getModifiedOn());
		if (row.getModifierFirstName() != null) {
			result.setModifierFirstName(row.getModifierFirstName());
		}
		if (row.getModifierLastName() != null) {
			result.setModifierLastName(row.getModifierLastName());
		}
		if (row.getCompanyId() != null) {
			result.setOwnerCompanyId(row.getCompanyId());
		}
		if (row.getCompanyName() != null) {
			result.setOwnerCompanyName(row.getCompanyName());
		}
		if (row.getPaidDate() != null) {
			result.setPaidOn(row.getPaidDate().getTime());
		}
		parseResource(row, result);
		if (row.getProjectName() != null) {
			result.setProjectName(row.getProjectName());
		}
		if (row.getProjectId() != null) {
			result.setProjectId(row.getProjectId());
		}
		if (row.getScheduleFrom() != null) {
			result.setScheduleFrom(row.getScheduleFrom().getTimeInMillis());
		} else if (row.getScheduleFromDate() != null) {
			result.setScheduleFrom(row.getScheduleFromDate().getTime());
		}
		if (row.getScheduleThrough() != null) {
			result.setScheduleThrough(row.getScheduleThrough().getTimeInMillis());
		} else if (row.getScheduleThroughDate() != null) {
			result.setScheduleThrough(row.getScheduleThroughDate().getTime());
		}
		if (row.getSpendLimit() != null) {
			result.setSpendLimit(row.getSpendLimit());
		}
		if (row.getSpendLimitWithFee() != null) {
			result.setSpendLimitWithFee(row.getSpendLimitWithFee());
		}
		if (row.getTimeZoneId() != null) {
			result.setTimeZoneId(row.getTimeZoneId());
		}
		if (row.getTitle() != null) {
			result.setTitle(row.getTitle());
		}
		if (row.getPricingType() != null ) {
			result.setPricingType(row.getPricingType());
		}
		if (row.getBuyerTotalCost() != null) {
			result.setBuyerTotalCost(row.getBuyerTotalCost());
		}
		if (row.getWorkNumber() != null) {
			result.setWorkNumber(row.getWorkNumber());
		}
		if (row.getParentId() != null) {
			result.setParentId(row.getParentId());
		}
		if (row.getParentTitle() != null) {
			result.setParentTitle(row.getParentTitle());
		}
		if (row.getParentDescription() != null) {
			result.setParentDescription(row.getParentDescription());
		}
		if (row.getAssignedResourceAppointmentFrom() != null) {
			result.setAppointmentFrom(row.getAssignedResourceAppointmentFrom().getTime());
		}
		if (row.getAssignedResourceAppointmentThrough() != null) {
			result.setAppointmentThrough(row.getAssignedResourceAppointmentThrough().getTime());
		}
		if (row.getDispatchCandidateNames() != null) {
			result.setDispatchCandidateNames(row.getDispatchCandidateNames());
		}
		if (row.getRecurrenceUUID() != null) {
			result.setRecurrenceUUID(row.getRecurrenceUUID());
		}

		DashboardResultFlags flags = new DashboardResultFlags();
		flags.setAddressOnsiteFlag(!row.isOffSite());
		flags.setConfirmed(row.isConfirmed());
		flags.setInternal(row.isInternal());
		flags.setResourceConfirmationRequired(row.isResourceConfirmationRequired());
		flags.setScheduleRangeFlag(row.getScheduleThrough() != null);
		flags.setPaymentTermsEnabled(row.isPaymentTermsEnabled());
		flags.setAutoPayEnabled(row.getAutoPayEnabled());
		flags.setAssignToFirstResource(row.isAssignToFirstResource());
		flags.setApplied(row.isApplied());
		flags.setApplicationsPending(row.isApplicationsPending());
		result.setResultFlags(flags);
		return result;
	}

	private void parseResource(SolrWorkData row, DashboardResult result) {
		if (row.getAssignedResourceId() == null) {
			return;
		}
		DashboardResource resource = new DashboardResource();
		resource.setResourceCompanyName(row.getAssignedResourceCompanyName());
		resource.setResourceFirstName(row.getAssignedResourceFirstName());
		resource.setResourceId(row.getAssignedResourceId());
		resource.setResourceCompanyId(row.getAssignedResourceCompanyId());
		resource.setResourceUserNumber(row.getAssignedResourceUserNumber());
		resource.setResourceLastName(row.getAssignedResourceLastName());
		resource.setMobilePhone(row.getAssignedResourceMobile());
		resource.setWorkPhone(row.getAssignedResourceWorkPhoneNumber());
		result.setResource(resource);
	}

	private DashboardAddress parseAddress(SolrWorkData row) {
		DashboardAddress address = new DashboardAddress();
		address.setCity(row.getCity());
		if (row.getClientLocationName() != null) {
			address.setLocationName(row.getClientLocationName());
		}
		if (row.getClientLocationNumber() != null) {
			address.setLocationNumber(row.getClientLocationNumber());
		}
		if (row.getPostalCode() != null) {
			address.setPostalCode(row.getPostalCode());
		}
		if (row.getState() != null) {
			address.setState(row.getState());
		}
		if (row.getCountry() != null) {
			address.setCountry(row.getCountry());
		}
		address.setLatitude(row.getLatitude());
		address.setLongitude(row.getLongitude());
		return address;
	}
}
