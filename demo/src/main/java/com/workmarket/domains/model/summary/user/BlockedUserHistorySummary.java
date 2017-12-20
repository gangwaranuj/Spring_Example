package com.workmarket.domains.model.summary.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.workmarket.domains.model.summary.HistorySummaryEntity;

@Entity(name = "blockedUserHistorySummary")
@Table(name = "block_user_history_summary")
public class BlockedUserHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = 5790449382960772551L;

	private Long userId;
	private Long userCompanyId;
	private Long blockingCompanyId;
	private Long userIndustryId;
	private boolean deleted;

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

	public void setUserCompanyId(Long companyId) {
		this.userCompanyId = companyId;
	}

	@Column(name = "blocking_company_id", nullable = false, length = 11)
	public Long getBlockingCompanyId() {
		return blockingCompanyId;
	}

	public void setBlockingCompanyId(Long blockingCompanyId) {
		this.blockingCompanyId = blockingCompanyId;
	}

	@Column(name = "user_industry_id", nullable = false, length = 11)
	public Long getUserIndustryId() {
		return userIndustryId;
	}

	public void setUserIndustryId(Long userIndustryId) {
		this.userIndustryId = userIndustryId;
	}

	@Column(name = "deleted", nullable = false)
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}