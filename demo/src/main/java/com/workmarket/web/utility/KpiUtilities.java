package com.workmarket.web.utility;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.domains.model.kpi.KPIReportAggregateInterval;
import com.workmarket.web.models.DataTablesResponse;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KpiUtilities {

	public enum Format{
		MONEY, NUMBER, PERCENTAGE, OTHER
	}

	public static final int MONTHS_IN_A_YEAR = 12;
	public static final int ZERO = 0;

	public static DataTablesResponse<List<String>, Map<String, Object>> getResponse(KPIChartResponse kpiChartResponse, KPIReportAggregateInterval kpiReportAggregateInterval, DataTablesResponse<List<String>, Map<String, Object>> response,  Format format) {

		int responseSize = kpiChartResponse.getChartData().size();
		int YEARS = (responseSize / MONTHS_IN_A_YEAR);
		if (responseSize % MONTHS_IN_A_YEAR != ZERO) {
			YEARS = YEARS + 1;
		}
		int MONTHS = 12 * YEARS;
		List<String> row = Lists.newArrayList();
		if (!kpiReportAggregateInterval.name().equals("MONTH_OF_YEAR")) {
			row.add("Total");
		}
		int count = ZERO;
		double total = ZERO;
		int year = Calendar.getInstance().get(Calendar.YEAR) - YEARS + 1;

		for(DataPoint datapoint : kpiChartResponse.getChartData()) {

			if (kpiReportAggregateInterval.name().equals("MONTH_OF_YEAR")) {
				if ((count % MONTHS_IN_A_YEAR) == 0) {
					row.add(new Integer(year).toString());
					year++;
				}
				setFormat(format, row, datapoint);
				total = total + datapoint.getY();

				if ((responseSize == count + 1) && (responseSize < MONTHS)) {
					for (int i = kpiChartResponse.getChartData().size(); i < MONTHS; i++ ) {
						row.add("-");
						count++;
					}
				}

				if (((count+1) % MONTHS_IN_A_YEAR) == 0) {
					datapoint.setY(total);
					total = ZERO;
					setFormat(format, row, datapoint);
					response.addRow(row);
					if (count == (MONTHS-1)) {
						return response;
					}
					else {
						row = Lists.newArrayList();
					}
				}
			}
			else {
				setFormat(format, row, datapoint);
			}
			count++;
		}
		response.addRow(row);
		return response;
	}

	public static void setFormat(Format format, List<String> row, DataPoint datapoint) {
		switch (format) {
			case MONEY:
				java.text.NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
				row.add(numberFormat.format(datapoint.getY()));
				break;
			case NUMBER:
				row.add(String.format("%d", (long) datapoint.getY()));
				break;
			case PERCENTAGE:
				row.add(String.format("%d", (long) (datapoint.getY() * 100)) + "%");
				break;
			case OTHER:
				row.add(String.valueOf(datapoint.getY()));
				break;
			default:
				break;
		}
	}

}
