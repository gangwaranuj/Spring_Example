package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

/**
 * Created by zhe.
 */

@Entity(name="taxServiceReportDownloadAudit")
@Table(name="tax_service_report_download_audit")
@AuditChanges
public class TaxServiceReportDownloadAudit extends AuditedEntity {
	private Long taxServiceReportId;
	private Calendar downloadedOn;
	private Long userId;

	@Column(name = "tax_service_report_id", nullable = false)
	public Long getTaxServiceReportId() {
		return taxServiceReportId;
	}

	public void setTaxServiceReportId(Long taxServiceReportId) {
		this.taxServiceReportId = taxServiceReportId;
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
