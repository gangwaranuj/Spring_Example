package com.workmarket.dao.report.work;

import com.workmarket.domains.reports.util.WorkDashboardReportBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.Assert;

@RunWith(BlockJUnit4ClassRunner.class)
public class WorkDashboardReportBuilderTest {

	@Test
	public void getSpendLimitCaseClause_WithSum() {
		Assert.assertEquals(
			WorkDashboardReportBuilder.getSpendLimitCaseClause("work", WorkDashboardReportBuilder.WITH_SUM),
			getInefficientBuilder("work", WorkDashboardReportBuilder.WITH_SUM)
		);
	}

	@Test
	public void getSpendLimitCaseClause_WithoutSum() {
		Assert.assertEquals(
			WorkDashboardReportBuilder.getSpendLimitCaseClause("work", WorkDashboardReportBuilder.WITHOUT_SUM),
			getInefficientBuilder("work", WorkDashboardReportBuilder.WITHOUT_SUM)
		);
	}

	private String getInefficientBuilder(String prefix, boolean withSum) {
		return ((withSum)?"SUM(":"") + "CASE " + prefix + ".pricing_strategy_type \n" +
			"WHEN 'FLAT' THEN COALESCE(" + prefix + ".flat_price, 0) + IF(" + prefix + ".work_status_type_code = 'complete', COALESCE(assignedResource.additional_expenses, 0), " + prefix + ".additional_expenses) + " + prefix + ".bonus \n" +
			"WHEN 'PER_HOUR' THEN (" + prefix + ".per_hour_price * " + prefix + ".max_number_of_hours) + IF(" + prefix + ".work_status_type_code = 'complete', COALESCE(assignedResource.additional_expenses, 0), " + prefix + ".additional_expenses) + " + prefix + ".bonus \n" +
			"WHEN 'PER_UNIT' THEN (" + prefix + ".per_unit_price * " + prefix + ".max_number_of_units) + IF(" + prefix + ".work_status_type_code = 'complete', COALESCE(assignedResource.additional_expenses, 0), " + prefix + ".additional_expenses) + " + prefix + ".bonus \n" +
			"WHEN 'BLENDED_PER_HOUR' THEN ((" + prefix + ".initial_per_hour_price * " + prefix + ".initial_number_of_hours) + (" + prefix + ".additional_per_hour_price * " + prefix + ".max_blended_number_of_hours)) + IF(" + prefix + ".work_status_type_code = 'complete', COALESCE(assignedResource.additional_expenses, 0), " + prefix + ".additional_expenses) + " + prefix + ".bonus \n" +
			"WHEN 'BLENDED_PER_UNIT' THEN ((" + prefix + ".initial_per_unit_price * " + prefix + ".initial_number_of_units) + (" + prefix + ".additional_per_unit_price * " + prefix + ".max_blended_number_of_units)) + IF(" + prefix + ".work_status_type_code = 'complete', COALESCE(assignedResource.additional_expenses, 0), " + prefix + ".additional_expenses) + " + prefix + ".bonus \n" +
			"WHEN 'INTERNAL' THEN 0 \n" +
			"ELSE NULL \n" +
			"END" + ((withSum)?")":"") + " AS spend_limit \n";
	}
}
