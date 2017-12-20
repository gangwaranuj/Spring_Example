package com.workmarket.domains.work.service.audit;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.audit.WorkAuditType;
import org.springframework.util.Assert;

import java.util.Calendar;

public class WorkActionRequest {

	private WorkAuditType auditType;
	private Long workId;
	private Long modifierId;
	private Long masqueradeId;
	private Long onBehalfOfId;
	private Calendar lastActionOn = Calendar.getInstance();

	private String workNumber;
	private String resourceUserNumber;
	private String masqueradeUserNumber;
	private String onBehalfOfUserNumber;
	private long currentUserId;

	public WorkActionRequest() {
	}

	public WorkActionRequest(String workNumber) {
		this.workNumber = workNumber;
	}

	/**
	 * Convenience method - will set the modifier ID to the buyer's Id and the work ID
	 *
	 * @param work
	 */
	public WorkActionRequest(Work work) {
		this.workId = work.getId();
		this.modifierId = work.getBuyer().getId();
	}

	public WorkAuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(WorkAuditType auditType) {
		this.auditType = auditType;
	}

	public Long getWorkId() {
		return workId;
	}

	public WorkActionRequest setWorkId(Long workId) {
		this.workId = workId;
		return this;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	public Long getMasqueradeId() {
		return masqueradeId;
	}

	public void setMasqueradeId(Long masqueradeId) {
		this.masqueradeId = masqueradeId;
	}

	public Long getOnBehalfOfId() {
		return onBehalfOfId;
	}

	public void setOnBehalfOfId(Long onBehalfOfId) {
		this.onBehalfOfId = onBehalfOfId;
	}

	public Calendar getLastActionOn() {
		return lastActionOn;
	}

	public void setLastActionOn(Calendar lastActionOn) {
		this.lastActionOn = lastActionOn;
	}

	public long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public String getMasqueradeUserNumber() {
		return masqueradeUserNumber;
	}

	public void setMasqueradeUserNumber(String masqueradeUserNumber) {
		this.masqueradeUserNumber = masqueradeUserNumber;
	}

	public String getOnBehalfOfUserNumber() {
		return onBehalfOfUserNumber;
	}

	public WorkActionRequest setOnBehalfOfUserNumber(String onBehalfOfUserNumber) {
		this.onBehalfOfUserNumber = onBehalfOfUserNumber;
		return this;
	}

	public String getResourceUserNumber() {
		return resourceUserNumber;
	}

	public WorkActionRequest setResourceUserNumber(String resourceUserNumber) {
		this.resourceUserNumber = resourceUserNumber;
		return this;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public WorkActionRequest setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorkActionRequest)) return false;

		WorkActionRequest that = (WorkActionRequest) o;

		if (currentUserId != that.currentUserId) return false;
		if (auditType != that.auditType) return false;
		if (lastActionOn != null ? !lastActionOn.equals(that.lastActionOn) : that.lastActionOn != null) return false;
		if (masqueradeId != null ? !masqueradeId.equals(that.masqueradeId) : that.masqueradeId != null) return false;
		if (masqueradeUserNumber != null ? !masqueradeUserNumber.equals(that.masqueradeUserNumber) : that.masqueradeUserNumber != null)
			return false;
		if (modifierId != null ? !modifierId.equals(that.modifierId) : that.modifierId != null) return false;
		if (onBehalfOfId != null ? !onBehalfOfId.equals(that.onBehalfOfId) : that.onBehalfOfId != null) return false;
		if (onBehalfOfUserNumber != null ? !onBehalfOfUserNumber.equals(that.onBehalfOfUserNumber) : that.onBehalfOfUserNumber != null)
			return false;
		if (resourceUserNumber != null ? !resourceUserNumber.equals(that.resourceUserNumber) : that.resourceUserNumber != null)
			return false;
		if (workId != null ? !workId.equals(that.workId) : that.workId != null) return false;
		if (workNumber != null ? !workNumber.equals(that.workNumber) : that.workNumber != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = auditType != null ? auditType.hashCode() : 0;
		result = 31 * result + (workId != null ? workId.hashCode() : 0);
		result = 31 * result + (modifierId != null ? modifierId.hashCode() : 0);
		result = 31 * result + (masqueradeId != null ? masqueradeId.hashCode() : 0);
		result = 31 * result + (onBehalfOfId != null ? onBehalfOfId.hashCode() : 0);
		result = 31 * result + (lastActionOn != null ? lastActionOn.hashCode() : 0);
		result = 31 * result + (workNumber != null ? workNumber.hashCode() : 0);
		result = 31 * result + (resourceUserNumber != null ? resourceUserNumber.hashCode() : 0);
		result = 31 * result + (masqueradeUserNumber != null ? masqueradeUserNumber.hashCode() : 0);
		result = 31 * result + (onBehalfOfUserNumber != null ? onBehalfOfUserNumber.hashCode() : 0);
		result = 31 * result + (int) (currentUserId ^ (currentUserId >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "WorkActionAuditRequest [auditType=" + auditType + ", workId="
				+ workId + ", modifierId=" + modifierId + ", masqueradeId="
				+ masqueradeId + ", onBehalfOfId=" + onBehalfOfId
				+ "]";
	}

	public void validate() {
		Assert.notNull(this.auditType);
		Assert.notNull(this.workId);
	}

	public boolean isSetOnBehalfOfUserNumber() {
		return this.onBehalfOfUserNumber != null;
	}

	public boolean isSetMasqueradeUserNumber() {
		return this.masqueradeUserNumber != null;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}
}
