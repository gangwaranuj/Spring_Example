package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: zhe
 */
@Entity(name="taxServiceReportSet")
@Table(name="tax_service_detail_report_set")
@AuditChanges
public class TaxServiceReportSet extends AbstractTaxReportSet {

	private static final long serialVersionUID = 1L;

}
