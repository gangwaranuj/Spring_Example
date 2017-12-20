package com.workmarket.domains.model.account.pricing.subscription;

import com.google.common.collect.Sets;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionFeeConfigurationTest {

	private SubscriptionFeeConfiguration feeConfiguration1;
	private SubscriptionFeeConfiguration feeConfiguration2;
	private SubscriptionFeeConfiguration feeConfiguration3;

	private Calendar date1;
	private Calendar date2;
	private Calendar date3;

	@Before
	public void setUp() throws Exception {
		feeConfiguration1 = new SubscriptionFeeConfiguration();
		feeConfiguration2 = new SubscriptionFeeConfiguration();
		feeConfiguration3 = new SubscriptionFeeConfiguration();

		date1 = DateUtilities.newCalendar(2014, Calendar.JUNE, 1, 0, 0, 0);
		date2 = DateUtilities.newCalendar(2014, Calendar.NOVEMBER, 1, 0, 0, 0);
		date3 = DateUtilities.newCalendar(2015, Calendar.JANUARY, 1, 0, 0, 0);

		feeConfiguration1.setEffectiveDate(date1);
		feeConfiguration2.setEffectiveDate(date2);
		feeConfiguration3.setEffectiveDate(date3);
	}

	@Test
	public void compareTo() {
		Set<SubscriptionFeeConfiguration> feeConfigurationSet = Sets.newHashSet();
		feeConfigurationSet.add(feeConfiguration2);
		feeConfigurationSet.add(feeConfiguration3);
		feeConfigurationSet.add(feeConfiguration1);

		List<SubscriptionFeeConfiguration> orderedConfigurations = CollectionUtilities.asSortedList(feeConfigurationSet);
		assertEquals(orderedConfigurations.get(0).getEffectiveDate(), date1);
		assertEquals(orderedConfigurations.get(1).getEffectiveDate(), date2);
		assertEquals(orderedConfigurations.get(2).getEffectiveDate(), date3);
	}
}