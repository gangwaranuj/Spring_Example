package com.workmarket.dao.summary.company;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.company.CompanySummary;

/**
 * Author: rocio
 */
public interface CompanySummaryDAO extends DAOInterface<CompanySummary> {

	CompanySummary findByCompany(long companyId);
}
