package com.workmarket.domains.model.tax;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: rocio
 */
@Entity(name="taxForm1099Set")
@Table(name="tax_form_1099_set")
@AuditChanges
public class TaxForm1099Set extends AbstractTaxReportSet {

	private static final long serialVersionUID = 1L;
}
