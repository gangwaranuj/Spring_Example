package com.workmarket.service.business;

import com.workmarket.domains.model.UserPagination;
import com.workmarket.test.BrokenTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class PaginationIT extends BaseServiceIT {
	
	@Test
	public void test_pagination() throws Exception {
		UserPagination pagination = new UserPagination(true);
		pagination.setRowCount(45);
		pagination.setResultsLimit(25);
		Assert.assertEquals(2, pagination.getNumberOfPages().intValue());
		Assert.assertEquals(1, pagination.getCurrentPage().intValue());
		
		Assert.assertTrue(pagination.hasNextPage());
		
		pagination = new UserPagination(true);
		pagination.setRowCount(1);
		pagination.setResultsLimit(1);
		Assert.assertEquals(1, pagination.getNumberOfPages().intValue());
		Assert.assertEquals(1, pagination.getCurrentPage().intValue());
		
		Assert.assertFalse(pagination.hasNextPage());
		
		pagination = new UserPagination(true);
		pagination.setRowCount(80);
		pagination.setResultsLimit(20);
		Assert.assertEquals(4, pagination.getNumberOfPages().intValue());
		Assert.assertEquals(1, pagination.getCurrentPage().intValue());
		
		Assert.assertTrue(pagination.hasNextPage());
		
		pagination = new UserPagination(true);
		pagination.setRowCount(0);
		pagination.setResultsLimit(25);
		Assert.assertEquals(0, pagination.getNumberOfPages().intValue());
		Assert.assertEquals(0, pagination.getCurrentPage().intValue());
		
		Assert.assertFalse(pagination.hasNextPage());
		
		pagination = new UserPagination(true);
		pagination.setRowCount(80);
		pagination.setResultsLimit(20);
		pagination.setStartRow(21);
		Assert.assertEquals(3, pagination.getNumberOfPages().intValue());
		Assert.assertEquals(2, pagination.getCurrentPage().intValue());
		
		Assert.assertTrue(pagination.hasNextPage());
	}
	
}
