package com.workmarket.domains.model.invoice;

import com.workmarket.domains.model.LookupEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="wmInvoiceNumberType")
@Table(name="wm_invoice_number_type")
public class WMInvoiceNumberType extends LookupEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * This is in case we need different types of invoices per department at WM
	 */
	public static final String WORK_MARKET_INC_INVOICE = "wm_inc";

	private Integer lastInvoiceNumber;
	private String prefix;
	private Integer optimisticLockVersion;

	public WMInvoiceNumberType() {
		super();
	}

	public WMInvoiceNumberType(String code) {
		super(code);
	}

	@Column(name = "last_invoice_number", nullable = false)
	public Integer getLastInvoiceNumber() {
		return lastInvoiceNumber;
	}

	public void setLastInvoiceNumber(Integer lastInvoiceNumber) {
		this.lastInvoiceNumber = lastInvoiceNumber;
	}

	@Column(name = "prefix", nullable = false)
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Version
	@Column(name = "optimistic_lock_version")
	public Integer getOptimisticLockVersion() {
		return optimisticLockVersion;
	}

	public void setOptimisticLockVersion(Integer optimisticLockVersion) {
		this.optimisticLockVersion = optimisticLockVersion;
	}
}
