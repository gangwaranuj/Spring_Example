package com.workmarket.dao.report.sql;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.workmarket.domains.model.Sort;
import com.workmarket.data.report.work.AccountStatementDetailPagination;
import com.workmarket.data.report.work.AccountStatementFilters;

@Component
public class SQLBuilderFactoryImpl implements SQLBuilderFactory {
	@Override
	public AccountStatementSQLBuilder buildAccountStatementSQL(Long userId, Long companyId, boolean isAdminOrController, AccountStatementDetailPagination pagination,
			AccountStatementFilters accountStatementFilters) {
		AccountStatementSQLBuilder builder = new AccountStatementSQLBuilder(userId, companyId, isAdminOrController, accountStatementFilters);
		if (pagination.getSorts() != null && !pagination.getSorts().isEmpty()) {
			for (Sort sort : pagination.getSorts()) {
				builder.addOrderBy(AccountStatementDetailPagination.SORTS.valueOf(sort.getSortColumn()).getColumn(), sort.getSortDirection().toString());
			}
		} else if (StringUtils.isNotBlank(pagination.getSortColumn())) {
			builder.addOrderBy(AccountStatementDetailPagination.SORTS.valueOf(pagination.getSortColumn()).getColumn(), pagination.getSortDirection().toString());
		} else {
			builder.addOrderBy(AccountStatementDetailPagination.SORTS.INVOICE_STATUS.getColumn(), AccountStatementDetailPagination.SORT_DIRECTION.DESC.toString());
			builder.addOrderBy(AccountStatementDetailPagination.SORTS.INVOICE_DUE_DATE.getColumn(), AccountStatementDetailPagination.SORT_DIRECTION.DESC.toString());
		}
		builder.setStartRow(pagination.getStartRow());
		builder.setPageSize(pagination.isLimitMaxRows() ? pagination.getResultsLimit() : null);
		return builder;
	}

}
