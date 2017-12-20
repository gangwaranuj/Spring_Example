package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name="earningReportDownloadAudit")
@Table(name="earning_report_download_audit")
@AuditChanges
public class EarningReportDownloadAudit extends AuditedEntity {

	private static final long serialVersionUID = 1L;

	private Long earningReportId;
	private Calendar downloadedOn;
	private Long userId;

	@Column(name = "earning_report_id", nullable = false)
	public Long getEarningReportId() {
		return earningReportId;
	}

	public void setEarningReportId(Long earningReportId) {
		this.earningReportId = earningReportId;
	}

	@Column(name = "downloaded_on", nullable = false)
	public Calendar getDownloadedOn() {
		return downloadedOn;
	}

	public void setDownloadedOn(Calendar downloadedOn) {
		this.downloadedOn = downloadedOn;
	}

	@Column(name = "user_id", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
