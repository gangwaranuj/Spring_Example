package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: rocio
 */
@Entity(name="earningReportSet")
@Table(name="earning_report_set")
@AuditChanges
public class EarningReportSet extends AbstractTaxReportSet {

	private static final long serialVersionUID = 1L;

}
