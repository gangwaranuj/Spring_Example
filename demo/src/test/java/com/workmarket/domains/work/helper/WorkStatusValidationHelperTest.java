package com.workmarket.domains.work.helper;

import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkStatusValidationHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkStatusValidationHelperTest {

	@Mock WorkService workService;
	@Mock Work work;
	@Mock MessageBundleHelper messageHelper;
	@InjectMocks WorkStatusValidationHelper workStatusValidationHelper;


	@Before
	public void setup() {
		when(workService.findWork(anyLong())).thenReturn(work);
	}

	@Test
	public void test_UpdateAssignmentWhenAlreadyInProgress() throws Exception {
		when(work.isActive()).thenReturn(true);
		List<ConstraintViolation> errors = workStatusValidationHelper.validateUpdateOnWorkStatus(1L);

		assertFalse(errors.isEmpty());
	}

	@Test
	public void test_UpdateAssignmentWhenNotInProgress() throws Exception {
		when(work.isActive()).thenReturn(false);
		List<ConstraintViolation> errors = workStatusValidationHelper.validateUpdateOnWorkStatus(1L);

		assertTrue(errors.isEmpty());
	}

}
