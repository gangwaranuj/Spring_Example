package com.workmarket.thrift.work;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkSaveRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userId;
	private Long templateId;
	private Work work;
	private com.workmarket.thrift.core.User assignTo;
	private List<RoutingStrategy> routingStrategies;
	private String onBehalfOfUserNumber;
	private long labelId;
	private boolean useMboServices;
	private boolean partOfBulk;
	private List<Long> groupIds;
	private List<String> vendorCompanyNumbers;
	private String bundleTitle;
	private String bundleDescription;
	private boolean assignToFirstToAccept;

	public WorkSaveRequest() {}

	public WorkSaveRequest(long userId, Work work) {
		this();
		this.userId = userId;
		this.work = work;
		this.partOfBulk = false;
	}

	public boolean isBundle() { return bundleTitle != null && bundleDescription != null; }

	public boolean isPartOfBulk() {
		return partOfBulk;
	}

	public void setPartOfBulk(boolean partOfBulk) {
		this.partOfBulk = partOfBulk;
	}

	public long getUserId() {
		return this.userId;
	}

	public WorkSaveRequest setUserId(long userId) {
		this.userId = userId;
		return this;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public WorkSaveRequest setTemplateId(Long templateId) {
		this.templateId = templateId;
		return this;
	}

	public long getLabelId() {
		return labelId;
	}

	public WorkSaveRequest setLabelId(long labelId) {
		this.labelId = labelId;
		return this;
	}

	public boolean isSetLabelId() {
		return labelId > 0L;
	}

	public Work getWork() {
		return this.work;
	}

	public WorkSaveRequest setWork(Work work) {
		this.work = work;
		return this;
	}

	public WorkSaveRequest setGroupIds(List<Long> groupIds) {
		List<Long> newGroupIds = Lists.newArrayList(groupIds);
		this.groupIds= newGroupIds;
		return this;
	}

	public List<Long> getGroupIds(){
		return this.groupIds;
	}

	public List<String> getVendorCompanyNumbers() {
		return vendorCompanyNumbers;
	}

	public WorkSaveRequest setVendorCompanyNumbers(List<String> vendorCompanyNumbers) {
		this.vendorCompanyNumbers = Lists.newArrayList(vendorCompanyNumbers);
		return this;
	}

	public boolean isSetWork() {
		return this.work != null;
	}

	public com.workmarket.thrift.core.User getAssignTo() {
		return this.assignTo;
	}

	public WorkSaveRequest setAssignTo(com.workmarket.thrift.core.User assignTo) {
		this.assignTo = assignTo;
		return this;
	}

	public boolean isSetAssignTo() {
		return this.assignTo != null && this.assignTo.getId() > 0L;
	}

	public int getRoutingStrategiesSize() {
		return (this.routingStrategies == null) ? 0 : this.routingStrategies.size();
	}

	public java.util.Iterator<RoutingStrategy> getRoutingStrategiesIterator() {
		return (this.routingStrategies == null) ? null : this.routingStrategies.iterator();
	}

	public void addToRoutingStrategies(List<RoutingStrategy> routingStrategies) {
		if (this.routingStrategies == null) {
			this.routingStrategies = new ArrayList<>();
		}
		this.routingStrategies.addAll(routingStrategies);
	}

	public void addToRoutingStrategies(RoutingStrategy elem) {
		if (this.routingStrategies == null) {
			this.routingStrategies = new ArrayList<>();
		}
		this.routingStrategies.add(elem);
	}

	public List<RoutingStrategy> getRoutingStrategies() {
		return this.routingStrategies;
	}

	public WorkSaveRequest setRoutingStrategies(List<RoutingStrategy> routingStrategies) {
		this.routingStrategies = routingStrategies;
		return this;
	}

	public boolean isSetRoutingStrategies() {
		return this.routingStrategies != null;
	}

	public String getOnBehalfOfUserNumber() {
		return this.onBehalfOfUserNumber;
	}

	public WorkSaveRequest setOnBehalfOfUserNumber(String onBehalfOfUserNumber) {
		this.onBehalfOfUserNumber = onBehalfOfUserNumber;
		return this;
	}

	public boolean isSetOnBehalfOfUserNumber() {
		return this.onBehalfOfUserNumber != null;
	}

	public boolean isShowInFeed() {
		return this.getWork() != null &&
			this.getWork().getConfiguration() != null &&
			this.getWork().getConfiguration().isShowInFeed();
	}

	public boolean isSmartRoute() {
		return (this.getWork() != null && this.getWork().getConfiguration() != null && this.getWork().getConfiguration().isSmartRoute());
	}

	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkSaveRequest)
			return this.equals((WorkSaveRequest) that);
		return false;
	}

	private boolean equals(WorkSaveRequest that) {
		if (that == null)
			return false;

		boolean this_present_userId = true;
		boolean that_present_userId = true;
		if (this_present_userId || that_present_userId) {
			if (!(this_present_userId && that_present_userId))
				return false;
			if (this.userId != that.userId)
				return false;
		}

		boolean this_present_work = true && this.isSetWork();
		boolean that_present_work = true && that.isSetWork();
		if (this_present_work || that_present_work) {
			if (!(this_present_work && that_present_work))
				return false;
			if (!this.work.equals(that.work))
				return false;
		}

		boolean this_present_assignTo = true && this.isSetAssignTo();
		boolean that_present_assignTo = true && that.isSetAssignTo();
		if (this_present_assignTo || that_present_assignTo) {
			if (!(this_present_assignTo && that_present_assignTo))
				return false;
			if (!this.assignTo.equals(that.assignTo))
				return false;
		}

		boolean this_present_routingStrategies = true && this.isSetRoutingStrategies();
		boolean that_present_routingStrategies = true && that.isSetRoutingStrategies();
		if (this_present_routingStrategies || that_present_routingStrategies) {
			if (!(this_present_routingStrategies && that_present_routingStrategies))
				return false;
			if (!this.routingStrategies.equals(that.routingStrategies))
				return false;
		}

		boolean this_present_onBehalfOfUserNumber = true && this.isSetOnBehalfOfUserNumber();
		boolean that_present_onBehalfOfUserNumber = true && that.isSetOnBehalfOfUserNumber();
		if (this_present_onBehalfOfUserNumber || that_present_onBehalfOfUserNumber) {
			if (!(this_present_onBehalfOfUserNumber && that_present_onBehalfOfUserNumber))
				return false;
			if (!this.onBehalfOfUserNumber.equals(that.onBehalfOfUserNumber))
				return false;
		}

		if (!this.getVendorCompanyNumbers().containsAll(that.getVendorCompanyNumbers()) ||
			!that.getVendorCompanyNumbers().containsAll(this.getVendorCompanyNumbers())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_userId = true;
		builder.append(present_userId);
		if (present_userId)
			builder.append(userId);

		boolean present_work = true && (isSetWork());
		builder.append(present_work);
		if (present_work)
			builder.append(work);

		boolean present_assignTo = true && (isSetAssignTo());
		builder.append(present_assignTo);
		if (present_assignTo)
			builder.append(assignTo);

		boolean present_routingStrategies = true && (isSetRoutingStrategies());
		builder.append(present_routingStrategies);
		if (present_routingStrategies)
			builder.append(routingStrategies);

		boolean present_onBehalfOfUserNumber = true && (isSetOnBehalfOfUserNumber());
		builder.append(present_onBehalfOfUserNumber);
		if (present_onBehalfOfUserNumber)
			builder.append(onBehalfOfUserNumber);

		builder.append(vendorCompanyNumbers);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkSaveRequest(");
		boolean first = true;

		sb.append("userId:");
		sb.append(this.userId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("work:");
		if (this.work == null) {
			sb.append("null");
		} else {
			sb.append(this.work);
		}
		first = false;
		if (isSetAssignTo()) {
			if (!first) sb.append(", ");
			sb.append("assignTo:");
			if (this.assignTo == null) {
				sb.append("null");
			} else {
				sb.append(this.assignTo);
			}
			first = false;
		}
		if (isSetRoutingStrategies()) {
			if (!first) sb.append(", ");
			sb.append("routingStrategies:");
			if (this.routingStrategies == null) {
				sb.append("null");
			} else {
				sb.append(this.routingStrategies);
			}
			first = false;
		}
		if (isSetOnBehalfOfUserNumber()) {
			if (!first) sb.append(", ");
			sb.append("onBehalfOfUserNumber:");
			if (this.onBehalfOfUserNumber == null) {
				sb.append("null");
			} else {
				sb.append(this.onBehalfOfUserNumber);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}

	public boolean isUseMboServices() {
		return useMboServices;
	}

	public WorkSaveRequest setUseMboServices(boolean useMboServices) {
		this.useMboServices = useMboServices;
		return this;
	}

	public String getBundleTitle() {
		return bundleTitle;
	}

	public void setBundleTitle(String bundleTitle) {
		this.bundleTitle = bundleTitle;
	}

	public String getBundleDescription() {
		return bundleDescription;
	}

	public void setBundleDescription(String bundleDescription) {
		this.bundleDescription = bundleDescription;
	}
}
