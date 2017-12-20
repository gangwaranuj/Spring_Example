package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name="earning_detail_report_download_audit")
@Table(name="earning_detail_report_download_audit")
@AuditChanges
public class EarningDetailReportDownloadAudit extends AuditedEntity {

	private static final long serialVersionUID = 3755229618970497717L;

	private Long earningDetailReportSetId;
	private Calendar downloadedOn;
	private Long userId;

	@Column(name = "earning_detail_report_set_id", nullable = false)
	public Long getEarningDetailReportSetId() {
		return earningDetailReportSetId;
	}

	public void setEarningDetailReportSetId(Long earningDetailReportSetId) {
		this.earningDetailReportSetId = earningDetailReportSetId;
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
