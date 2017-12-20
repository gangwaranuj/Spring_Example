package com.workmarket.domains.model;

import com.google.api.client.util.Sets;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class DateRangeTest {
	DateRange dateRange1;
	DateRange dateRange2;
	Calendar from1;
	Calendar through1;
	Calendar from2;
	Calendar through2;

	@Before
	public void setup() {
		dateRange1 = new DateRange();
		dateRange2 = new DateRange();
		from1 = Calendar.getInstance();
		through1 = Calendar.getInstance();
		from2 = Calendar.getInstance();
		through2 = Calendar.getInstance();

		from1.add(Calendar.DATE, 1);
		through1.add(Calendar.DATE, 3);
		from2.add(Calendar.DATE, 1);
		through2.add(Calendar.DATE, 3);
		dateRange1.setFrom(from1);
		dateRange1.setThrough(through1);
		dateRange2.setFrom(from1);
		dateRange2.setThrough(through1);
	}

	@Test
	public void equals_isEqual() {
		EqualsVerifier
			.forClass(DateRange.class)
			.suppress(Warning.NONFINAL_FIELDS)
			.usingGetClass()
			.verify();
	}

	@Test
	public void hashcode_isEqual() {
		assertEquals(dateRange1.hashCode(), dateRange2.hashCode());
	}

	@Test
	public void set_sameObject_False() {
		Set<DateRange> dateRanges = Sets.newHashSet();
		dateRanges.add(dateRange1);
		assertFalse(dateRanges.add(dateRange1));
	}

	@Test
	public void set_differentButEqualObject_False() {
		Set<DateRange> dateRanges = Sets.newHashSet();
		dateRanges.add(dateRange1);
		assertFalse(dateRanges.add(dateRange2));
	}
}
