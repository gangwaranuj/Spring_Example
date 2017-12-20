package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * Author: rocio
 */
@MappedSuperclass
public abstract class AbstractTaxReportSet extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private Integer taxYear;
	private TaxReportSetStatusType taxReportSetStatusType = new TaxReportSetStatusType(TaxReportSetStatusType.NEW);
	private Calendar publishedOn;
	private User publishedBy;

	@Column(name = "tax_year", nullable = false)
	public Integer getTaxYear() {
		return taxYear;
	}

	public void setTaxYear(Integer taxYear) {
		this.taxYear = taxYear;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "tax_report_set_status_type_code", referencedColumnName = "code")
	public TaxReportSetStatusType getTaxReportSetStatusType() {
		return taxReportSetStatusType;
	}

	public void setTaxReportSetStatusType(TaxReportSetStatusType taxReportSetStatusType) {
		this.taxReportSetStatusType = taxReportSetStatusType;
	}

	@Column(name = "published_on")
	public Calendar getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(Calendar publishedOn) {
		this.publishedOn = publishedOn;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "published_by")
	public User getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(User publishedBy) {
		this.publishedBy = publishedBy;
	}

	@Transient
	public boolean isPublished(){
		return getTaxReportSetStatusType() != null && getTaxReportSetStatusType().isPublished();
	}

	@Transient
	public boolean isIssued(){
		return getTaxReportSetStatusType() != null && getTaxReportSetStatusType().isIssued();
	}

	@Transient
	public boolean isProcessing(){
		return getTaxReportSetStatusType() != null && getTaxReportSetStatusType().isProcessing();
	}
}
