package com.workmarket.domains.model.summary.work;

import com.workmarket.domains.model.summary.HistorySummaryEntity;
import com.workmarket.domains.work.model.Work;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "workStatusTransitionHistorySummary")
@Table(name = "work_status_transition_history_summary")
public class WorkStatusTransitionHistorySummary extends HistorySummaryEntity {

	private static final long serialVersionUID = -478678136718786412L;
	private Long workId;
	private Long companyId;
	private String fromWorkStatusTypeCode;
	private String toWorkStatusTypeCode;
	private int transitionTimeInSeconds;
	private boolean showInWorkFeed = false;

	public WorkStatusTransitionHistorySummary() {
	}

	public WorkStatusTransitionHistorySummary(Work work, Long dateId) {
		if (work != null) {
			this.companyId = work.getCompany().getId();
			this.workId = work.getId();
		}
		this.setDateId(dateId);
	}

	@Column(name = "company_id", nullable = false, length = 11)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "from_work_status_type_code", nullable = true, length = 20)
	public String getFromWorkStatusTypeCode() {
		return fromWorkStatusTypeCode;
	}

	public void setFromWorkStatusTypeCode(String fromWorkStatusTypeCode) {
		this.fromWorkStatusTypeCode = fromWorkStatusTypeCode;
	}

	@Column(name = "to_work_status_type_code", nullable = false, length = 20)
	public String getToWorkStatusTypeCode() {
		return toWorkStatusTypeCode;
	}

	public void setToWorkStatusTypeCode(String toWorkStatusTypeCode) {
		this.toWorkStatusTypeCode = toWorkStatusTypeCode;
	}

	@Column(name = "work_id", nullable = false, length = 11)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "transition_time_in_seconds", nullable = false)
	public int getTransitionTimeInSeconds() {
		return transitionTimeInSeconds;
	}

	public void setTransitionTimeInSeconds(int transitionTimeInSeconds) {
		this.transitionTimeInSeconds = transitionTimeInSeconds;
	}

	@Column(name = "show_in_feed", nullable = false)
	public boolean isShowInWorkFeed() {
		return showInWorkFeed;
	}

	public void setShowInWorkFeed(boolean showInWorkFeed) {
		this.showInWorkFeed = showInWorkFeed;
	}
}
