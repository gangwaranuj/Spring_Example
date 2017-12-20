package com.workmarket.domains.work.dao;

import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import com.workmarket.utility.sql.SQLBuilder;

public interface WorkResourceDetailSQLFactory {
	SQLBuilder getResourceListBuilder(Long workId, WorkResourceDetailPagination pagination);
	SQLBuilder getResourceBestPriceBuilder(Long workId);
}
