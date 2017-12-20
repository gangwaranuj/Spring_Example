package com.workmarket.data.report.work;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class StandardDecoratedWorkReportRowTest {

	@Test
	public void calculateSpendLimitWithFee_workFeePercentageIsNull_returnSpendLimit() throws Exception {
		assertEquals(StandardDecoratedWorkReportRow.calculateSpendLimitWithFee(new BigDecimal(1), null), 1d, 0);
	}

	@Test
	public void calculateSpendLimitWithFee_spendLimitIsNull_returnZero() throws Exception {
		assertEquals(StandardDecoratedWorkReportRow.calculateSpendLimitWithFee(null, new BigDecimal(1)), 0d, 0);
	}

	@Test
	public void calculateSpendLimitWithFee_spendLimitIsNonPositive_returnSpendLimit() throws Exception {
		assertEquals(StandardDecoratedWorkReportRow.calculateSpendLimitWithFee(new BigDecimal(0), new BigDecimal(1)), 0d, 0);
	}

	@Test
	public void calculateSpendLimitWithFee_workFeePercentageIsNonPositive_returnSpendLimit() throws Exception {
		assertEquals(StandardDecoratedWorkReportRow.calculateSpendLimitWithFee(new BigDecimal(1), new BigDecimal(0)), 1d, 0);
	}
}
