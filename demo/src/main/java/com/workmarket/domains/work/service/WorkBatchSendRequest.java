package com.workmarket.domains.work.service;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class WorkBatchSendRequest {

	private List<String> workNumbers;
	private List<Long> assignToFirstToAcceptGroupIds;
	private List<Long> needToApplyGroupIds;
	private List<String> assignToFirstToAcceptUserNumbers;
	private List<String> needToApplyUserNumbers;
	private List<String> assignToFirstToAcceptVendorCompanyNumbers;
	private List<String> needToApplyVendorCompanyNumbers;

	public boolean isMissingGroupIds() {
		return CollectionUtils.isEmpty(assignToFirstToAcceptGroupIds) && CollectionUtils.isEmpty(needToApplyGroupIds);
	}

	public boolean isMissingUserNumbers() {
		return CollectionUtils.isEmpty(assignToFirstToAcceptUserNumbers) && CollectionUtils.isEmpty(needToApplyUserNumbers);
	}

	public boolean isMissingVendorCompanyNumbers() {
		return CollectionUtils.isEmpty(assignToFirstToAcceptVendorCompanyNumbers) &&
			CollectionUtils.isEmpty(needToApplyVendorCompanyNumbers);
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public void setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
	}

	public List<Long> getAssignToFirstToAcceptGroupIds() {
		return assignToFirstToAcceptGroupIds;
	}

	public void setAssignToFirstToAcceptGroupIds(List<Long> assignToFirstToAcceptGroupIds) {
		this.assignToFirstToAcceptGroupIds = assignToFirstToAcceptGroupIds;
	}

	public List<Long> getNeedToApplyGroupIds() {
		return needToApplyGroupIds;
	}

	public void setNeedToApplyGroupIds(List<Long> needToApplyGroupIds) {
		this.needToApplyGroupIds = needToApplyGroupIds;
	}

	public List<String> getNeedToApplyUserNumbers() {
		return needToApplyUserNumbers;
	}

	public void setNeedToApplyUserNumbers(List<String> needToApplyUserNumbers) {
		this.needToApplyUserNumbers = needToApplyUserNumbers;
	}

	public List<String> getAssignToFirstToAcceptVendorCompanyNumbers() {
		return assignToFirstToAcceptVendorCompanyNumbers;
	}

	public void setAssignToFirstToAcceptVendorCompanyNumbers(List<String> assignToFirstToAcceptVendorCompanyNumbers) {
		this.assignToFirstToAcceptVendorCompanyNumbers = assignToFirstToAcceptVendorCompanyNumbers;
	}

	public List<String> getAssignToFirstToAcceptUserNumbers() {
		return assignToFirstToAcceptUserNumbers;
	}

	public void setAssignToFirstToAcceptUserNumbers(List<String> assignToFirstToAcceptUserNumbers) {
		this.assignToFirstToAcceptUserNumbers = assignToFirstToAcceptUserNumbers;
	}

	public List<String> getNeedToApplyVendorCompanyNumbers() {
		return needToApplyVendorCompanyNumbers;
	}

	public void setNeedToApplyVendorCompanyNumbers(List<String> needToApplyVendorCompanyNumbers) {
		this.needToApplyVendorCompanyNumbers = needToApplyVendorCompanyNumbers;
	}
}
