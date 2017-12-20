package com.workmarket.domains.work.service.dashboard;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.data.report.work.WorkSubStatusTypeReportRow;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.domains.model.DateRangeUtilities;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.user.UserDashboardInfo;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.request.work.WorkSearchRequestUserType;
import com.workmarket.search.response.work.DashboardAddressUtilities;
import com.workmarket.search.response.work.DashboardResource;
import com.workmarket.search.response.work.DashboardResult;
import com.workmarket.search.response.work.DashboardResultFlags;
import com.workmarket.search.response.work.DashboardResultList;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.UserService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.SpamSlayer;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.work.WorkDashboardForm;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Service
public class DashboardResultServiceImpl implements DashboardResultService {

	@Autowired private WorkFollowService workFollowService;
	@Autowired private UserService userService;
	@Autowired private WorkResourceService workResourceService;

	public enum Field {
		ID("id") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getId());
			}
		},

		PARENT_ID("parent_id") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getParentId());
			}
		},

		PARENT_TITLE("parent_title") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getParentTitle());
			}
		},

		PARENT_DESCRIPTION("parent_description") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getParentDescription());
			}
		},

		TITLE("title") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getTitle());
			}
		},

		TITLE_SHORT("title_short") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getTitleShort());
			}
		},

		SCHEDULED_DATE("scheduled_date") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getScheduledDate());
			}
		},

		SCHEDULED_DATE_FROM_IN_MILLIS("scheduled_date_from_in_millis") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getScheduledDateFromInMillis());
			}
		},

		SCHEDULED_DATE_THROUGH_IN_MILLIS("scheduled_date_though_in_millis") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getScheduledDateThroughInMillis());
			}
		},

		ADDRESS("address") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getAddress());
			}
		},

		LOCATION_NAME("location_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getLocationName());
			}
		},

		LOCATION_NUMBER("location_number") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getLocationNumber());
			}
		},

		LOCATION_OFFSITE("location_offsite") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getLocationOffsite());
			}
		},

		PRICE("price") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getPrice());
			}
		},

		WORK_AMOUNT("work_amount") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getWorkAmount());
			}
		},

		AMOUNT_EARNED("amount_earned") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getAmountEarned());
			}
		},

		PAID_ON("paid_on") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getPaidOn());
			}
		},

		AUTO_PAY_ENABLED("auto_pay_enabled") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getAutoPayEnabled());
			}
		},

		CUSTOM_FIELDS("custom_fields") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getCustomFields());
			}
		},

		TYPE("type") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getType());
			}
		},

		STATUS("status") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getStatus());
			}
		},

		RAW_STATUS("raw_status") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getRawStatus());
			}
		},

		SUBSTATUSES("substatuses") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getSubstatuses());
			}
		},

		BUYER("buyer") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getBuyer());
			}
		},

		BUYER_ID("buyer_id") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getBuyerId());
			}
		},

		COMPANY_ID("company_id") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getCompanyId());
			}
		},

		OWNER_COMPANY_NAME("owner_company_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getOwnerCompanyName());
			}
		},

		CLIENT("client") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getClient());
			}
		},

		LANE("lane") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getLane());
			}
		},

		RESOURCE("resource") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResource());
			}
		},

		PROJECT_NAME("project_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getProjectName());
			}
		},

		RESOURCE_ID("resource_id") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceId());
			}
		},

		RESOURCE_USER_NUMBER("resource_user_number") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceUserNumber());
			}
		},

		RESOURCE_FULL_NAME("resource_full_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceFullName());
			}
		},

		RESOURCE_COMPANY_NAME("resource_company_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceCompanyName());
			}
		},

		RESOURCE_MOBILE_PHONE("resource_mobile_phone") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceMobilePhone());
			}
		},

		RESOURCE_WORK_PHONE("resource_work_phone") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getResourceWorkPhone());
			}
		},

		LAST_MODIFIED_ON("last_modified_on") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getLastModifiedOn());
			}
		},

		MODIFIER_FIRST_NAME("modifier_first_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getModifierFirstName());
			}
		},

		MODIFIER_LAST_NAME("modifier_last_name") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getModifierLastName());
			}
		},

		IS_ADMIN("is_admin") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsAdmin());
			}
		},

		IS_RESOURCE("is_resource") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsResource());
			}
		},

		IS_ME("is_me") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsMe());
			}
		},

		CAN_APPROVE("can_approve") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getCanApprove());
			}
		},

		IS_OWNERS_COMPANY("is_owners_company") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsOwnersCompany());
			}
		},

		IS_ASSIGN_TO_FIRST_RESOURCE("is_assign_to_first_resource") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsAssignToFirstResource());
			}
		},

		IS_APPLIED("is_applied") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsApplied());
			}
		},

		IS_APPLICATIONS_PENDING("is_applications_pending") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsApplicationsPending());
			}
		},

		IS_FOLLOWING("is_following") {
			@Override
			public void addToMap(Map<String, Object> map, WorkItem workItem) {
				map.put(this.keyName, workItem.getIsFollowing());
			}
		};

		protected final String keyName;

		Field(String keyName) {
			this.keyName = keyName;
		}

		public abstract void addToMap(Map<String, Object> map, WorkItem workItem);
	}

	private class WorkItem {
		private final DashboardResult item;
		private final WorkDashboardForm form;
		private final UserDashboardInfo userDashboardInfo;
		private EnumSet<Field> exclusions = EnumSet.noneOf(Field.class);
		private EnumSet<Field> inclusions = EnumSet.noneOf(Field.class);

		boolean isMe() {
			return userDashboardInfo.getId().equals(item.getBuyerId());
		}

		boolean isResource() {
			return !isMe() && !isAdmin();
		}

		boolean isAdmin() {
			return userDashboardInfo.isCanManageWork() && isOwnersCompany();
		}

		boolean isOwnersCompany() {
			return userDashboardInfo.getCompanyId().equals(item.getOwnerCompanyId());
		}

		boolean canSeeClientInfo() {
			return isAdmin() || Lists.newArrayList(
				WorkStatusType.ACTIVE,
				WorkStatusType.COMPLETE,
				WorkStatusType.PAYMENT_PENDING,
				WorkStatusType.PAID
			).contains(item.getWorkStatusTypeCode());
		}

		boolean canApprove() {
				return userDashboardInfo.isCanApproveWork() && isOwnersCompany();
			}

		WorkSearchRequestUserType getWorkSearchRequestUserType() {
			return form.getStatusType();
		}

		public String getId() {
			return item.getWorkNumber();
		}

		public Long getParentId() {
			return item.getParentId();
		}

		public String getParentTitle() {
			return StringUtilities.truncate(item.getParentTitle(), 30, "...");
		}

		public String getParentDescription() {
			return item.getParentDescription();
		}

		public String getTitle() {
				return item.getTitle();
		}

		public String getTitleShort() {
			return StringUtilities.truncate(item.getTitle(), 70);
		}

		public String getScheduledDate() {
			return item.getAssignmentAppointment() != null ? DateRangeUtilities.format("MMM dd hh:mm aa", "MMM dd hh:mm aa z", " - ", item.getAssignmentAppointment(), item.getTimeZoneId()) : null;
		}

		public Long getScheduledDateFromInMillis() {
			return item.getAssignmentAppointment() != null ? item.getAssignmentAppointment().getFrom().getTime().getTime() : null;
		}

		public Long getScheduledDateThroughInMillis() {
			return item.getAssignmentAppointment() != null && item.getAssignmentAppointment().isRange() ? item.getAssignmentAppointment().getThrough().getTime().getTime() : null;
		}

		public String getAddress() {
			return DashboardAddressUtilities.formatAddressShort(item.getAddress());
		}

		public String getLocationName() {
			return (canSeeClientInfo() && item.isSetAddress()) ? item.getAddress().getLocationName() : null;
		}

		public String getLocationNumber() {
			return (isAdmin() && item.isSetAddress()) ? item.getAddress().getLocationNumber() : null;
		}

		public boolean getLocationOffsite() {
			return !item.getResultFlags().isAddressOnsiteFlag();
		}

		public String getPrice() {
			return item.getFormattedPrice(isResource());
		}

		public double getWorkAmount() {
			return item.getBuyerTotalCost();
		}

		public String getAmountEarned() {
			return NumberUtilities.currency(item.getAmountEarned());
		}

		public String getPaidOn() {
			return (item.isSetPaidOn()) ? DateUtilities.formatMillis("MMM dd, yyyy hh:mm aa z", item.getPaidOn(), item.getTimeZoneId()) : null;
		}

		public boolean getAutoPayEnabled() {
			return item.getResultFlags().isAutoPayEnabled();
		}

		public Map<String, String> getCustomFields() {
			return item.getCustomFieldMap();
		}

		public String getType() {
			return (WorkSearchRequestUserType.RESOURCE.equals(getWorkSearchRequestUserType()) ? "working" : "managing");
		}

		public String getStatus() {
			return item.getFormattedWorkStatusType(getWorkSearchRequestUserType(), userDashboardInfo.getCompanyId(), form.getStatus());
		}

		public String getRawStatus() {
			return item.getWorkStatusTypeCode();
		}

		public List<WorkSubStatusTypeReportRow> getSubstatuses() {
			return item.getUnresolvedWorkSubStatuses();
		}

		public String getBuyer() {
			return item.getBuyerFullName(); // assigned to (w/in buyer company)
		}

		public long getBuyerId() {
			return item.getBuyerId();
		}

		public long getCompanyId() {
			return item.getOwnerCompanyId();
		}

		public String getOwnerCompanyName() {
			return item.getOwnerCompanyName();
		}

		public String getClient() {
			return (canSeeClientInfo()) ? item.getClient() : null;
		}

		public String getLane() {
			return null;
		}

		public DashboardResource getResource() {
			return item.getResource();
		}

		public String getProjectName() {
			return item.getProjectName();
		}

		public Long getResourceId() {
			return (item.isSetResource()) ? item.getResource().getResourceId() : null;
		}

		public String getResourceUserNumber() {
			return (item.isSetResource()) ? item.getResource().getResourceUserNumber() : null;
		}

		public String getResourceFullName() {
			return (item.isSetResource() && item.getResource().isSetResourceFirstName()) ? StringUtilities.fullName(item.getResource().getResourceFirstName(), item.getResource().getResourceLastName()) : null;
		}

		public String getResourceCompanyName() {
			return (item.isSetResource()) ? item.getResource().getResourceCompanyName() : null;
		}

		public String getResourceMobilePhone() {
			return item.isSetResource() ? SpamSlayer.slay(StringUtilities.formatPhoneNumber(item.getResource().getMobilePhone())) : null;
		}

		public String getResourceWorkPhone() {
			return item.isSetResource() ? SpamSlayer.slay(StringUtilities.formatPhoneNumber(item.getResource().getWorkPhone())) : null;
		}

		public String getLastModifiedOn() {
			return item.getFormattedLastModifiedDate();
		}

		public String getModifierFirstName() {
			return item.isSetModifierFirstName() ? StringUtils.substring(item.getModifierFirstName(), 0, 1) : "";
		}

		public String getModifierLastName() {
			return item.getModifierLastName();
		}

		public boolean getIsAdmin() {
			return isAdmin();
		}

		public boolean getIsResource() {
			return isResource();
		}

		public boolean getIsMe() {
			return isMe();
		}

		public boolean getCanApprove() {
			return canApprove();
		}

		public boolean getIsOwnersCompany() {
			return isOwnersCompany();
		}

		public boolean getIsAssignToFirstResource() {
			return item.getResultFlags().isAssignToFirstResource();
		}

		public boolean getIsApplied() {
			return item.getResultFlags().isApplied();
		}

		public boolean getIsApplicationsPending() {
			return item.getResultFlags().isApplicationsPending();
		}

		public boolean getIsFollowing() {
			return isMe() || workFollowService.isFollowingWork(item.getId(), userDashboardInfo.getId());
		}

		public WorkItem(WorkDashboardForm form, DashboardResult item, UserDashboardInfo userDashboardInfo) {
			this.item              = item;
			this.form              = form;
			this.userDashboardInfo = userDashboardInfo;
		}

		public WorkItem exclude(Field field) {
			exclusions.add(field);
			return this;
		}

		public WorkItem include(Field field) {
			inclusions.add(field);
			return this;
		}

		public Map<String, Object> asMap() {
			Map<String, Object> map = Maps.newHashMap();

			for (Field field : inclusions) {
				if (exclusions.contains(field)) { continue; }
				field.addToMap(map, this);
			}

			return map;
		}
	}

	@Override
	public Map<String, Object> getMappedWorkItem(WorkDashboardForm form, DashboardResult item, UserDashboardInfo userDashboardInfo) {
		WorkItem workItem = new WorkItem(form, item, userDashboardInfo);

		if (form.isFast()) {
			workItem
				.include(Field.ID)
				.include(Field.TITLE)
				.include(Field.SCHEDULED_DATE)
				.include(Field.CLIENT)
				.include(Field.ADDRESS)
				.include(Field.RESOURCE_FULL_NAME)
				.include(Field.STATUS)
				.include(Field.PRICE)
				.include(Field.BUYER)
				.include(Field.PROJECT_NAME)
				.include(Field.SUBSTATUSES)
				.include(Field.LAST_MODIFIED_ON);
		}

		return workItem.asMap();
	}

	@Override
	public void decorateDashBoardResultFlags(WorkSearchRequest request, WorkSearchResponse response, DashboardResultList results) {
		if (results.getResultsSize() == 0) {
			return;
		}

		boolean isUserResource = request.getWorkSearchRequestUserType() == WorkSearchRequestUserType.RESOURCE;
		List<Long> workIds = extract(response.getResults(), on(SolrWorkData.class).getWorkId());
		List<String> assignToFirstWorkNumbers;

		if (isUserResource)
			assignToFirstWorkNumbers = workResourceService.findWorkerAssignToFirstResourceWorkNumbers(request.getUserNumber(), workIds);
		else
			assignToFirstWorkNumbers = workResourceService.findBuyerAssignToFirstResourceWorkNumbers(workIds);

		if (!assignToFirstWorkNumbers.isEmpty()) {
			for (DashboardResult result : results.getResults()) {
				DashboardResultFlags flags = result.getResultFlags();
				flags.setAssignToFirstResource(assignToFirstWorkNumbers.contains(result.getWorkNumber()));
			}
		}
	}

	@Override
	public int getPendingApprovalsCount(final WorkSearchRequest request, final WorkSearchResponse response) {
		if (!WorkSearchRequestUserType.CLIENT.equals(request.getWorkSearchRequestUserType())) {
			return 0;
		}
		if (CollectionUtils.isEmpty(request.getDecisionFlowUuids())) {
			return 0;
		}
		int numOfPendingApprovalsInSearchResults = 0;
		final Set<String> myPendingApprovalUuids = request.getDecisionFlowUuids();
		for (String myPendingApprovalUuid : myPendingApprovalUuids) {
			if (response.getAggregates().getCounts().containsKey(myPendingApprovalUuid)) {
				numOfPendingApprovalsInSearchResults++;
			}
		}
		return numOfPendingApprovalsInSearchResults;
	}
}
