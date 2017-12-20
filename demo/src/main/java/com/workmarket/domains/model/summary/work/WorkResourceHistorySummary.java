package com.workmarket.domains.model.summary.work;

import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.summary.HistorySummaryEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "workResourceHistorySummary")
@Table(name = "work_resource_history_summary")
public class WorkResourceHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = -1783860136739545156L;
	
	private Long workResourceId;
	private Long userId;
	private Long userCompanyId;
	private Long userIndustryId = Industry.NO_INDUSTRY_CODE;
	private Long workId;
	private String workResourceStatusTypeCode;

	@Column(name = "work_resource_id", nullable = false, length = 11)
	public Long getWorkResourceId() {
		return workResourceId;
	}
	
	public void setWorkResourceId(Long workResourceId) {
		this.workResourceId = workResourceId;
	}

	@Column(name = "user_id", nullable = false, length = 11)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "user_company_id", nullable = false, length = 11)
	public Long getUserCompanyId() {
		return userCompanyId;
	}

	public void setUserCompanyId(Long userCompanyId) {
		this.userCompanyId = userCompanyId;
	}

	@Column(name = "user_industry_id", nullable = false, length = 11)
	public Long getUserIndustryId() {
		return userIndustryId;
	}

	public void setUserIndustryId(Long userIndustryId) {
		if (userIndustryId != null) {
			this.userIndustryId = userIndustryId;
		}
	}

	@Column(name = "work_id", nullable = false, length = 11)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "work_resource_status_type_code", nullable = false, length = 10)
	public String getWorkResourceStatusTypeCode() {
		return workResourceStatusTypeCode;
	}

	public void setWorkResourceStatusTypeCode(String workResourceStatusTypeCode) {
		this.workResourceStatusTypeCode = workResourceStatusTypeCode;
	}
}