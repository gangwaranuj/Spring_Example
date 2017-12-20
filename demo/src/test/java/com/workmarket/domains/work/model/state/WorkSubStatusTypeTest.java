package com.workmarket.domains.work.model.state;

import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.model.WorkStatusType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkSubStatusTypeTest {

	private WorkSubStatusDescriptor workSubStatusDescriptor;

	@Before
	public void setup() {
		workSubStatusDescriptor = mock(WorkSubStatusDescriptor.class);
	}

	@Test
	public void isRemoveOnPaid_passesThrough_workSubStatusDescriptorIsRemoveOn() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		when(workSubStatusDescriptor.isRemoveOnPaid()).thenReturn(false);
		assertFalse(workSubStatusType.isRemoveOnPaid());

		when(workSubStatusDescriptor.isRemoveOnPaid()).thenReturn(true);
		assertTrue(workSubStatusType.isRemoveOnPaid());
	}

	@Test
	public void isRemoveOnVoidOrCancelled_passesThrough_workSubStatusDescriptorisRemoveOnVoidOrCancelled() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		when(workSubStatusDescriptor.isRemoveOnVoidOrCancelled()).thenReturn(false);
		assertFalse(workSubStatusType.isRemoveOnVoidOrCancelled());

		when(workSubStatusDescriptor.isRemoveOnVoidOrCancelled()).thenReturn(true);
		assertTrue(workSubStatusType.isRemoveOnVoidOrCancelled());
	}

	@Test
	public void isWorkSubStatusApplicableForStatusType_false_isRemoveOnPaid_and_workStatusTypeIsPaid() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		WorkStatusType workStatusType = mock(WorkStatusType.class);
		when(workStatusType.isPaid()).thenReturn(true);
		when(workSubStatusDescriptor.isRemoveOnPaid()).thenReturn(true);

		assertFalse(workSubStatusType.isWorkSubStatusApplicableForWorkStatusType(workStatusType));
	}

	@Test
	public void isWorkSubStatusApplicableForStatusType_false_isRemoveOnVoidOrCancelled_and_workStatusTypeisVoidOrCancelled() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		WorkStatusType workStatusType = mock(WorkStatusType.class);
		when(workStatusType.isVoidOrCancelled()).thenReturn(true);
		when(workSubStatusDescriptor.isRemoveOnVoidOrCancelled()).thenReturn(true);

		assertFalse(workSubStatusType.isWorkSubStatusApplicableForWorkStatusType(workStatusType));
	}

	@Test
	public void isWorkSubStatusApplicableForStatusType_false_() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		WorkStatusType workStatusType = mock(WorkStatusType.class);

		WorkSubStatusTypeWorkStatusScope workSubStatusTypeWorkStatusScope = mock(WorkSubStatusTypeWorkStatusScope.class);
		workSubStatusType.setWorkSubStatusTypeWorkStatusScopes(ImmutableSet.of(workSubStatusTypeWorkStatusScope));

		assertFalse(workSubStatusType.isWorkSubStatusApplicableForWorkStatusType(workStatusType));
	}

	@Test
	public void isWorkSubStatusApplicableForStatusType() {
		WorkSubStatusType workSubStatusType = new WorkSubStatusType();
		workSubStatusType.setSubStatusDescriptor(workSubStatusDescriptor);

		WorkStatusType workStatusType = mock(WorkStatusType.class);

		assertTrue(workSubStatusType.isWorkSubStatusApplicableForWorkStatusType(workStatusType));
	}




}
