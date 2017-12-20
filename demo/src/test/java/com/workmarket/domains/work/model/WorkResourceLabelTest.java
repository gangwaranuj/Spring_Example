package com.workmarket.domains.work.model;

import com.workmarket.domains.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class WorkResourceLabelTest {

	@Test
	public void ignore_setsIgnoredAndUserAndDate() {
		WorkResourceLabel workResourceLabel = new WorkResourceLabel();
		workResourceLabel.setIgnoredOn(null);
		User user = mock(User.class);

		workResourceLabel.ignore(user);

		assertTrue(workResourceLabel.isIgnored());
		assertEquals(workResourceLabel.getIgnoredBy(), user);
		assertNotNull(workResourceLabel.getIgnoredOn());
	}

	@Test
	public void unignore_setsIgnoredFalse_nullsIgnoredByAndOn() {
		WorkResourceLabel workResourceLabel = new WorkResourceLabel();
		workResourceLabel.setIgnoredOn(null);
		User user = mock(User.class);
		workResourceLabel.setIgnoredBy(user);
		workResourceLabel.setIgnoredOn(Calendar.getInstance());

		workResourceLabel.unIgnore();

		assertFalse(workResourceLabel.isIgnored());
		assertNull(workResourceLabel.getIgnoredBy());
		assertNull(workResourceLabel.getIgnoredOn());
	}
}
