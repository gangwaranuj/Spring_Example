package com.workmarket.web.helpers;

import com.workmarket.domains.model.WorkStatusType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Set;


/**
 * Date: 9/4/13
 * Time: 10:52 AM
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class WorkStatusFilterHelperUnitTest {

	private Set<WorkStatusType> workStatusFilterList;

	@Test
	public void createWorkStatusFilter_sent() {
		workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter(WorkStatusType.SENT);
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.SENT)));
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.ACTIVE)));
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.INPROGRESS)));
	}

	@Test
	public void createWorkStatusFilter_active() {
		workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter(WorkStatusType.ACTIVE);
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.SENT)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.ACTIVE)));
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.INPROGRESS)));
	}

	@Test
	public void createWorkStatusFilter_inprogress() {
		workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter(WorkStatusType.INPROGRESS);
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.SENT)));
		assertFalse(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.ACTIVE)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.INPROGRESS)));
	}

	@Test
	public void createWorkStatusFilter_all() {
		workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter(WorkStatusType.ALL);
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.SENT)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.ACTIVE)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.INPROGRESS)));
	}

	@Test
	public void createWorkStatusFilter_empty() {
		workStatusFilterList = WorkStatusFilterHelper.createWorkStatusFilter("");
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.SENT)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.ACTIVE)));
		assertTrue(workStatusFilterList.contains(new WorkStatusType(WorkStatusType.INPROGRESS)));
	}

}
