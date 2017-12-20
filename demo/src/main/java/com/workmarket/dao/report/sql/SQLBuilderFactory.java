package com.workmarket.dao.report.sql;

import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;

public interface SQLBuilderFactory {

	AccountStatementSQLBuilder buildAccountStatementSQL(Long userId, Long companyId, boolean isAdminOrController, AccountStatementDetailPagination pagination,
			AccountStatementFilters accountStatementFilters);

}
