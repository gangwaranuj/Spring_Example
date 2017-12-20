package com.workmarket.domains.model.summary.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.summary.HistorySummaryEntity;

@Entity(name = "userHistorySummary")
@Table(name = "user_history_summary")
public class UserHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = -7797332813470101157L;

	private Long userId;
	private Long companyId;
	private Long industryId;
	private boolean manageWork;
	private boolean findWork;
	private String userStatusTypeCode;

	@Column(name = "user_id", nullable = false, length = 11)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "company_id", nullable = false, length = 11)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "industry_id", nullable = false, length = 11)
	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	@Column(name = "manage_work")
	public boolean isManageWork() {
		return manageWork;
	}

	public void setManageWork(boolean manageWork) {
		this.manageWork = manageWork;
	}

	@Column(name = "find_work")
	public boolean isFindWork() {
		return findWork;
	}

	public void setFindWork(boolean findWork) {
		this.findWork = findWork;
	}

	@Column(name = "user_status_type_code", nullable = false, length = 15)
	public String getUserStatusTypeCode() {
		return userStatusTypeCode;
	}

	public void setUserStatusTypeCode(String userStatusTypeCode) {
		this.userStatusTypeCode = userStatusTypeCode;
	}

}
