package com.workmarket.redis;

import com.workmarket.service.business.dto.WorkResourceDetailPagination;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class RedisFiltersTest {

	@Test
	public void getWorkResourcesDetailDataKey_withCompanyFilter() throws Exception {
		Long workId = 1L;
		Long companyId = 2L;

		WorkResourceDetailPagination pagination = new WorkResourceDetailPagination();
		pagination.setRowCount(1);
		pagination.setSortColumn("");
		pagination.addFilter(
			WorkResourceDetailPagination.FILTER_KEYS.WORK_RESOURCE_COMPANY_ID, companyId
		);

		assertEquals(String.format("work:work_resource_detail:%d::ASC:0:500:false:false:false:%d", workId, companyId), RedisFilters.getWorkResourcesDetailDataKey(workId, pagination));
	}
}
