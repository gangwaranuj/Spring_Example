package com.workmarket.reports;

import com.workmarket.domains.model.reporting.ReportFilter;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.thrift.work.display.ColumnValuesRequest;
import com.workmarket.thrift.work.display.WorkDisplayException;
import org.junit.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AbstractReportFilterBuilderIT {

	@Test
	public void testDisplay() throws WorkDisplayException{
		ColumnValuesRequest displayRequest = new ColumnValuesRequest("workResourceLastName_39","39");
		ReportFilter filter = ReportFilter.createDisplayOnlyRequest(displayRequest);
		Assert.isTrue(filter.getFilteringType() == FilteringType.DISPLAY);
		Assert.hasText(filter.getProperty());

	}

    /*
    Numeric Range
    "buyerFee_21","21"
    "7_21_select_from","3"
    "7_21_buyerFee_from","0"
    "7_21_select_to","4"
    "7_21_buyerFee_to","100"
     */

	@Test
	public void testNumericRange() throws WorkDisplayException{
		Map<String,ColumnValuesRequest> numericRangeRequests = new HashMap<String,ColumnValuesRequest>();
		numericRangeRequests.put("7_21_select_from",new ColumnValuesRequest("7_21_select_from","3"));
		numericRangeRequests.put("7_21_buyerFee_from",new ColumnValuesRequest("7_21_buyerFee_from","0"));
		numericRangeRequests.put("7_21_select_to",new ColumnValuesRequest("7_21_select_to","4"));
		numericRangeRequests.put("7_21_buyerFee_to",new ColumnValuesRequest("7_21_buyerFee_to","100"));

		ReportFilter filter = ReportFilter.createReportFilter(new ColumnValuesRequest("7_21_select_from","3"),numericRangeRequests);

		Assert.notNull(filter);
		Assert.isTrue(filter.getFilteringType() == FilteringType.NUMERIC);
		Assert.isTrue(filter.getFromValue().equals(new BigDecimal(0)));
		Assert.isTrue(filter.getToValue().equals(new BigDecimal(100)));
		Assert.isTrue(filter.getFromOperator() == RelationalOperator.GREATER_THAN_EQUAL_TO);
		Assert.isTrue(filter.getToOperator() == RelationalOperator.LESS_THAN);

	}

	@Test
	public void testCustomFields() throws WorkDisplayException{

		ColumnValuesRequest displayRequest = new ColumnValuesRequest("workCustomFields_60","60");
		ReportFilter filter = ReportFilter.createDisplayOnlyRequest(displayRequest);
		Assert.isTrue(filter.getFilteringType() == FilteringType.DISPLAY);
		Assert.hasText(filter.getProperty());
		Assert.isTrue(filter.hasWorkCustomFields());

	}


	/*
		DateRange
	"sentDate_62","61"
	"5_62_select_from","4"
	"5_62_sentDate_to","02/28/2013"
	"5_62_sentDate_from","02/01/2013"
	 */
	@Test
	public void testDateRange() throws WorkDisplayException{
		Map<String,ColumnValuesRequest> dateRangeRequests = new HashMap<String,ColumnValuesRequest>();
		dateRangeRequests.put("sendDate_62",new ColumnValuesRequest("sentDate_62","61"));
		dateRangeRequests.put("5_62_select_from",new ColumnValuesRequest("5_62_select_from","4"));
		dateRangeRequests.put("5_62_sentDate_to",new ColumnValuesRequest("5_62_sentDate_to","02/28/2013"));
		dateRangeRequests.put("5_62_sentDate_from",new ColumnValuesRequest("5_62_sentDate_from","02/01/2013"));
		ReportFilter filter = ReportFilter.createReportFilter(new ColumnValuesRequest("5_62_select_from","4"),dateRangeRequests);

		Assert.notNull(filter);
		Assert.isTrue(filter.getFilteringType() == FilteringType.DATE_RANGE);
		Assert.isTrue(filter.getInputFromValue().equals("02/01/2013"));
		Assert.isTrue(filter.getInputToValue().equals("02/28/2013"));


	}


	/*WorkStatusType
	"2_15_workStatusTypeCode_multiselect", "draft"
			"2_15_workStatusTypeCode_multiselect", "paid"
			"2_15_workStatusTypeCode_multiselect", "sent"   */
	@Test
	public void testMultiSelect() throws WorkDisplayException{
		Map<String,ColumnValuesRequest> multiSelectRequests = new HashMap<String,ColumnValuesRequest>();
		multiSelectRequests.put("2_15_workStatusTypeCode_multiselect",new ColumnValuesRequest("2_15_workStatusTypeCode_multiselect", "draft, paid, sent"));
		ReportFilter filter = ReportFilter.createReportFilter(new ColumnValuesRequest("2_15_workStatusTypeCode_multiselect","paid"),multiSelectRequests);

		Assert.notNull(filter);
		Assert.isTrue(filter.getFilteringType() == FilteringType.FIELD_VALUE);
		Assert.isTrue(filter.getFieldValue().equals("draft, paid, sent"));

	}


	/*
		"1_18_pricingStrategy_select", "FLAT"
	"1_108_workCountry_select","pleaseSelect"

	 */
	@Test
	public void testSelect() throws WorkDisplayException{
		ColumnValuesRequest pricingRequest = new ColumnValuesRequest("1_18_pricingStrategy_select", "FLAT");
		ColumnValuesRequest pleaseSelect = new ColumnValuesRequest("1_108_workCountry_select","pleaseSelect");

		Map<String,ColumnValuesRequest> selectRequests = new HashMap<String,ColumnValuesRequest>();
		selectRequests.put(pricingRequest.getKeyName(),pricingRequest);
		selectRequests.put(pleaseSelect.getKeyName(),pleaseSelect);

		ReportFilter filter = ReportFilter.createReportFilter(pricingRequest,selectRequests);

		Assert.isTrue(filter.getFilteringType() == FilteringType.FIELD_VALUE);
		Assert.isTrue(filter.getFieldValue().equals("FLAT"));

		filter = ReportFilter.createReportFilter(pleaseSelect,selectRequests);
		Assert.isNull(filter);


	}

	@Test
	public void testWorkUniqueIdDisplayName() throws WorkDisplayException{

		ColumnValuesRequest displayRequest = new ColumnValuesRequest("work.workUniqueId.displayname_155","155");
		ReportFilter filter = ReportFilter.createDisplayOnlyRequest(displayRequest);
		Assert.isTrue(filter.getFilteringType() == FilteringType.DISPLAY);
		Assert.hasText(filter.getProperty());

	}

	@Test
	public void testWorkUniqueIdValue() throws WorkDisplayException{

		ColumnValuesRequest displayRequest = new ColumnValuesRequest("work.workUniqueId.value_156","156");
		ReportFilter filter = ReportFilter.createDisplayOnlyRequest(displayRequest);
		Assert.isTrue(filter.getFilteringType() == FilteringType.DISPLAY);
		Assert.hasText(filter.getProperty());

	}
}
