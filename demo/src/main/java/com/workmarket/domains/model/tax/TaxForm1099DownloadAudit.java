package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name="tax_form_1099_download_audit")
@Table(name="tax_form_1099_download_audit")
@AuditChanges
public class TaxForm1099DownloadAudit extends AuditedEntity {

	private static final long serialVersionUID = 3755229618970497717L;

	private Long taxForm1099Id;
	private Calendar downloadedOn;
	private Long userId;

	@Column(name = "tax_form_1099_id", nullable = false)
	public Long getTaxForm1099Id() {
		return taxForm1099Id;
	}

	public void setTaxForm1099Id(Long taxForm1099Id) {
		this.taxForm1099Id = taxForm1099Id;
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
