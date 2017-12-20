package com.workmarket.service.business.dto.account;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

/**
 * Author: rocio
 */
public class PastDueCompaniesDTO {

	private Set<Long> companiesToLock = Sets.newHashSet();
	/* Days since account was overdue -> Companies to warn */
	private Map<Integer, Set<Long>> companiesToOverdueWarn = Maps.newHashMap();
	private Map<Long, Calendar> companiesTo24HsWarn = Maps.newHashMap();
	private Map<Long, Long> invoiceIdsToWarnOwners = Maps.newHashMap();

	public Map<Long, Calendar> getCompaniesTo24HsWarn() {
		return companiesTo24HsWarn;
	}

	public void setCompaniesTo24HsWarn(Map<Long, Calendar> companiesTo24HsWarn) {
		this.companiesTo24HsWarn = companiesTo24HsWarn;
	}

	public Set<Long> getCompaniesToLock() {
		return companiesToLock;
	}

	public void setCompaniesToLock(Set<Long> companiesToLock) {
		this.companiesToLock = companiesToLock;
	}

	public Map<Integer, Set<Long>> getCompaniesToOverdueWarn() {
		return companiesToOverdueWarn;
	}

	public void setCompaniesToOverdueWarn(Map<Integer, Set<Long>> companiesToOverdueWarn) {
		this.companiesToOverdueWarn = companiesToOverdueWarn;
	}

	public Map<Long, Long> getInvoiceIdsToWarnOwners() {
		return invoiceIdsToWarnOwners;
	}

	public void setInvoiceIdsToWarnOwners(Map<Long, Long> invoiceIdsToWarnOwners) {
		this.invoiceIdsToWarnOwners = invoiceIdsToWarnOwners;
	}
}
