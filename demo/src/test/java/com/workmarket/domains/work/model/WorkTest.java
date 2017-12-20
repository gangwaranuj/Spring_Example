package com.workmarket.domains.work.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class WorkTest {

	@Test
	public void isBundleOrInBundle_trueIf_isWorkBundle() {
		AbstractWork bundle = new WorkBundle();
		Work work = (Work) bundle;

		assertTrue(work.isBundleOrInBundle());
	}

	@Test
	public void isBundleOrInBundle_trueIf_isInBundle() {
		Work work = new Work();
		WorkBundle workBundle = new WorkBundle();
		work.setParent(workBundle);

		assertTrue(work.isBundleOrInBundle());
	}

	@Test
	public void isBundleOrInBundle_falseIf_notIsWorkBundle_and_notIsInBundle() {
		Work work = new Work();

		assertFalse(work.isBundleOrInBundle());
	}
}
