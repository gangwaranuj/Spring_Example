package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="taxReportSetStatusType")
@Table(name="tax_report_set_status_type")
public class TaxReportSetStatusType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	public static final String PROCESSING = "processing";
	public static final String ISSUED = "issued";
	public static final String PUBLISHED = "published";
	public static final String ARCHIVED = "archived";
	public static final String NEW = "new";

	public TaxReportSetStatusType() {}
	public TaxReportSetStatusType(String code){
		super(code);
	}

	@Transient
	public boolean isPublished(){
		return PUBLISHED.equals(getCode());
	}

	@Transient
	public boolean isArchived() {
		return ARCHIVED.equals(getCode());
	}

	@Transient
	public boolean isIssued() {
		return ISSUED.equals(getCode());
	}

	@Transient
	public boolean isProcessing() {
		return PROCESSING.equals(getCode());
	}

	@Transient
	public boolean isNew() {
		return NEW.equals(getCode());
	}
}
