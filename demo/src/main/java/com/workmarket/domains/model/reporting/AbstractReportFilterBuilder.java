package com.workmarket.domains.model.reporting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.configuration.Constants;
import com.workmarket.thrift.work.display.ColumnValuesRequest;
import com.workmarket.thrift.work.display.WorkDisplayErrorType;
import com.workmarket.thrift.work.display.WorkDisplayException;
import com.workmarket.thrift.work.report.FilteringTypeThrift;
import com.workmarket.thrift.work.report.RelationalOperatorThrift;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

public abstract class AbstractReportFilterBuilder{

	protected static final String PLEASE_SELECT = "pleaseSelect";
	protected static final Integer HTML_TAG_TYPE_THRIFT_INDEX = 0;
	protected static final Integer WORK_REPORT_COLUMN_TYPE_INDEX = 1;
	protected static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/work-reporting-application-context.xml");
	public static BidiMap workReportColumnTypes = (BidiMap)ctx.getBean("work_report_column_types");
	public static BidiMap workReportInputTypes = (BidiMap)ctx.getBean("work_report_input_types");
	public static BidiMap relationalOperatorOptions = (BidiMap)ctx.getBean("relational_operator_options");

	public enum ReportFilterType{
		INPUT_TEXT,
		SELECT_OPTION,
		MULTI_SELECT_OPTION,
		DATE,
		DATE_TIME,
		TO_FROM_DATES,
		NUMERIC,
		NUMERIC_RANGE,
		DISPLAY
	}

	public static Map<Integer, ReportFilterType> reportFilterBuilders = new ImmutableMap.Builder<Integer, ReportFilterType>()
		.put(ReportFilterType.INPUT_TEXT.ordinal(), ReportFilterType.INPUT_TEXT)
		.put(ReportFilterType.SELECT_OPTION.ordinal(), ReportFilterType.MULTI_SELECT_OPTION)
		.put(ReportFilterType.MULTI_SELECT_OPTION.ordinal(), ReportFilterType.MULTI_SELECT_OPTION)
		.put(ReportFilterType.DATE.ordinal(), ReportFilterType.DATE)
		.put(ReportFilterType.DATE_TIME.ordinal(), ReportFilterType.DATE_TIME)
		.put(ReportFilterType.TO_FROM_DATES.ordinal(), ReportFilterType.TO_FROM_DATES)
		.put(ReportFilterType.NUMERIC.ordinal(), ReportFilterType.NUMERIC)
		.put(ReportFilterType.NUMERIC_RANGE.ordinal(), ReportFilterType.NUMERIC_RANGE)
		.put(ReportFilterType.DISPLAY.ordinal(), ReportFilterType.DISPLAY)
		.build();

	public static AbstractReportFilterBuilder getReportFilterBuilder(Integer filterType){
		ReportFilterType reportFilterType = reportFilterBuilders.get(filterType);
		switch(reportFilterType){
			case INPUT_TEXT:{
				return new InputTextFilterBuilder();
			}
			case SELECT_OPTION:{
				return new SelectFilterBuilder();
			}
			case MULTI_SELECT_OPTION:{
				return new MultiSelectFilterBuilder();
			}
			case DATE:{
				return new DateFilterBuilder();
			}
			case DATE_TIME:{
				return new DateTimeFilterBuilder();
			}
			case TO_FROM_DATES:{
				return new DateToFromFilterBuilder();
			}
			case NUMERIC:{
				return new NumericFilterBuilder();
			}
			case NUMERIC_RANGE:{
				return new NumericRangeFilterBuilder();
			}
			case DISPLAY:{
				return new DisplayFilterBuilder();
			}
		}
		return null;
	}

	public AbstractReportFilterBuilder() {}

	protected String validate(String fieldName, String fieldValue) throws WorkDisplayException {
		if (StringUtils.isBlank(fieldName)) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList(fieldName + " must be populated."));
		}
		return fieldValue.trim();
	}

	protected BigDecimal validateBigDecimal(String fieldName, String fieldValue) throws WorkDisplayException {
		fieldValue = validate(fieldName, fieldValue);
		try {
			return new BigDecimal(fieldValue);
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList(fieldName + " must be numeric."));
		}
	}

	protected String parseColumnName(String column){

		String parsedColumn;
		int index = column.indexOf('.');
		if (index > -1) {
			parsedColumn = column.substring(column.indexOf('.'));
			parsedColumn = parsedColumn.replace('.', '_');
		} else
			parsedColumn = "_" + column;

		return parsedColumn;
	}


	protected String getColumnName(String keyName){

		Assert.hasText(keyName);
		String[] reportKeys = keyName.split("_");

		WorkReportColumnType workReportColumnType = WorkReportColumnType.findByValue(Integer.parseInt(reportKeys[1]));
		return (String) workReportColumnTypes.get(workReportColumnType);
	}



	public abstract ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap) throws WorkDisplayException;

	public abstract CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter);

	public BidiMap getWorkReportInputTypes() {
		return workReportInputTypes;
	}

	public void setWorkReportInputTypes(BidiMap workReportInputTypes) {
		this.workReportInputTypes = workReportInputTypes;
	}

	public BidiMap getRelationalOperatorOptions() {
		return relationalOperatorOptions;
	}

	public void setRelationalOperatorOptions(BidiMap relationalOperatorOptions) {
		this.relationalOperatorOptions = relationalOperatorOptions;
	}

	public BidiMap getWorkReportColumnTypes() {
		return workReportColumnTypes;
	}

	public void setWorkReportColumnTypes(BidiMap workReportColumnTypes) {
		this.workReportColumnTypes = workReportColumnTypes;
	}
}


class DisplayFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.DISPLAY);
		return filter;
	}


	public ReportFilter buildDisplayOnly(ColumnValuesRequest reportRequestData){
		Assert.notNull(reportRequestData);
		WorkReportColumnType workReportColumnType;
		workReportColumnType = WorkReportColumnType.findByValue(Integer.parseInt(reportRequestData.getValue()));
		if(workReportColumnType.equals(WorkReportColumnType.WORK_SELECT_ALL)){
			return null;
		}
		ReportFilter filter = new ReportFilter();
		filter.setFilteringType(FilteringType.DISPLAY);
		String column = (String) workReportColumnTypes.get(workReportColumnType);
		if (workReportColumnType.equals(WorkReportColumnType.WORK_CUSTOM_FIELDS)) {
			filter.setHasWorkCustomFields(Boolean.TRUE);
		}
		filter.setProperty(column);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		response.setSuccess(true);
		return response;
	}

}

class InputTextFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap) throws WorkDisplayException {
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.FIELD_VALUE);
		filter.setFieldValue(reportRequestData.getValue());
		filter.setFieldValueOperator(RelationalOperator.EQUAL_TO);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		Assert.hasText(filterKey);
		Assert.notEmpty(rawFilterMap);
		Assert.notNull(filter);
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		response.setSuccess(true);
		if(StringUtils.isEmpty((String)filterValue) ||filterValue.equals(PLEASE_SELECT)){
			filter.setDeleted(true);
		} else {
			filter.setFieldValue((String)filterValue);
		}
		return response;
	}
}

class SelectFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap) throws WorkDisplayException {
		if(reportRequestData.getValue().equals(PLEASE_SELECT)){
			return null;
		}
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.FIELD_VALUE);
		filter.setFieldValue(reportRequestData.getValue());
		filter.setFieldValueOperator(RelationalOperator.EQUAL_TO);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		Assert.hasText(filterKey);
		Assert.notEmpty(rawFilterMap);
		Assert.notNull(filter);
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		if(StringUtils.isEmpty((String)filterValue)||filterValue.equals(PLEASE_SELECT)){
			filter.setDeleted(true);
		} else {
			filter.setFieldValue((String)filterValue);
			response.setSuccess(true);
		}
		return response;
	}
}

class MultiSelectFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {
		if(reportRequestData.getValue().equals(PLEASE_SELECT)){
			return null;
		}
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.FIELD_VALUE);
		ColumnValuesRequest selectFilter = rawRequestDataMap.get(reportRequestData.getKeyName());
		filter.setFieldValue(selectFilter.getValue());
		filter.setFieldValueOperator(RelationalOperator.EQUAL_TO);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey, Object filterValue, Map<String, Object> rawFilterMap, ReportingCriteriaFiltering filter){
		Assert.hasText(filterKey);
		Assert.notEmpty(rawFilterMap);
		Assert.notNull(filter);
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		List<String> filters = (List<String>)filterValue;
		if (CollectionUtils.isEmpty(filters) || CollectionUtils.containsInstance(filters,PLEASE_SELECT)){
			filter.setDeleted(true);
		} else {
			response.setSuccess(true);
			filter.setFieldValue((List<String>)filterValue);
		}
		return response;
	}

}

class DateFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.DATE_TIME);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		response.setSuccess(true);
		return response;
	}

}

class DateTimeFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.DATE_TIME);
		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		response.setSuccess(true);
		return response;
	}

}

//We have to find the to and from as they are two different filters in the list of filters
//we need to find them and build a single merged filter
class DateToFromFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {
		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		String[] reportFilterKeys = reportRequestData.getKeyName().split("_");

		ColumnValuesRequest selectFilter = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_from");
		FilteringTypeThrift filteringTypeThrift = FilteringTypeThrift.findByValue(Integer.valueOf(selectFilter.getValue()));
		FilteringType filteringType = (FilteringType) workReportInputTypes.get(filteringTypeThrift);
		filter.setFilteringType(filteringType);
		if (filteringType.equals(FilteringType.PLEASE_SELECT)){
			return null;
		}

		String inputFrom;
		String inputTo;
		if (FilteringTypeThrift.WORK_DATE_RANGE.equals(filteringTypeThrift)) {
			try {
				inputFrom = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
					reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + parseColumnName(filter.getProperty()) + "_from").getValue();
				inputTo = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
					reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + parseColumnName(filter.getProperty()) + "_to").getValue();
			} catch (Exception e) {
				List<String> messagesL = new ArrayList<>();
				messagesL.add("Must have valid inputs and relational operators.");
				throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, messagesL);
			}
			Calendar fromCalendar;
			Calendar toCalendar;

			try {
				fromCalendar = DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarFromDateString(inputFrom, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
				toCalendar = DateUtilities.getMidnightNextDayRelativeToTimezone(DateUtilities.getCalendarFromDateString(inputTo, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
				toCalendar.add(Calendar.MILLISECOND, -1);

				filter.setFromDate(fromCalendar);
				filter.setInputFromValue(inputFrom);

				filter.setToDate(toCalendar);
				filter.setInputToValue(inputTo);
			} catch (Exception e) {
				// TODO: refactor /generate_report endpoint to return useful messages for cases like this (invalid DateRange filter)
				return null;
			}
			filter.setFromOperator(RelationalOperator.GREATER_THAN_EQUAL_TO);
			filter.setToOperator(RelationalOperator.LESS_THAN_EQUAL_TO);
		}
		return filter;

	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){

		CustomReportUpdateResponse response = new CustomReportUpdateResponse();

		String key = filterKey.substring(0,filterKey.lastIndexOf("_"));
		key = key.substring(0,key.lastIndexOf("_"));
		//get selector values from map
		String selectValue = (String)rawFilterMap.get(key + "_select_from");
		FilteringTypeThrift filteringTypeThrift = FilteringTypeThrift.findByValue(Integer.valueOf(selectValue));
		FilteringType filteringType = (FilteringType) workReportInputTypes.get(filteringTypeThrift);
		if (filteringType.equals(FilteringType.PLEASE_SELECT)){
			filter.setDeleted(true);
			return response;
		}
		filter.setFilteringType(filteringType.getType());
		if (FilteringTypeThrift.WORK_DATE_RANGE.equals(filteringTypeThrift)){
			String rootKey = key + parseColumnName(filter.getProperty());
			String inputFrom = (String)rawFilterMap.get(rootKey + "_from");
			String inputTo = (String)rawFilterMap.get(rootKey + "_to");
			if(inputFrom == null|| inputTo == null){
				response.setSuccess(false);
				response.addError(new ConstraintViolation("reports.custom.could_not_save",filter.getProperty()));
				return response;
			}
			Calendar fromCalendar = DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarFromDateString(inputFrom, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
			Calendar toCalendar = DateUtilities.getCalendarWithLastMinuteOfDay(DateUtilities.getCalendarFromDateString(inputTo, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
			filter.setFromDate(fromCalendar);
			filter.setToDate(toCalendar);
			filter.setFromOperator(RelationalOperator.GREATER_THAN_EQUAL_TO.getOperator());
			filter.setToOperator(RelationalOperator.LESS_THAN_EQUAL_TO.getOperator());
			response.setSuccess(true);
		}
		return response;
	}
}

class NumericFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {

		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));

		filter.setFilteringType(FilteringType.NUMERIC);// TODO verify NUMERIC_RANGE vs. NUMERIC in backend API.
		String inputFilter;
		String selectFilter;
		try {
			String[] reportFilterKeys = reportRequestData.getKeyName().split("_");
			inputFilter = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + parseColumnName(filter.getProperty()) + "_filter").getValue();
			selectFilter = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_filter").getValue();
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid inputs and relational operators."));
		}

		try {
			RelationalOperatorThrift relationalOperatorThriftFrom = RelationalOperatorThrift.findByValue(Integer.parseInt(selectFilter));
			if (relationalOperatorThriftFrom == null)
				throw new Exception("The relationalOperatorThriftFrom must not be null");

			if (relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT))
				return null;

			filter.setFromOperator((RelationalOperator) relationalOperatorOptions.get(relationalOperatorThriftFrom));
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid relational operators."));
		}
		filter.setFromValue(validateBigDecimal("The input", inputFilter));

		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		response.setSuccess(true);
		return response;
	}

}

class NumericRangeFilterBuilder extends AbstractReportFilterBuilder{

	public ReportFilter buildReportFilter(ColumnValuesRequest reportRequestData,Map<String,ColumnValuesRequest> rawRequestDataMap)  throws WorkDisplayException {

		ReportFilter filter = new ReportFilter();
		filter.setProperty(getColumnName(reportRequestData.getKeyName()));
		filter.setFilteringType(FilteringType.NUMERIC);// TODO verify NUMERIC_RANGE vs. NUMERIC in backend API.
		String inputFrom;
		String inputTo;
		String selectFrom;
		String selectTo;
		try {
			String[] reportFilterKeys = reportRequestData.getKeyName().split("_");
			inputFrom = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + parseColumnName(filter.getProperty()) + "_from").getValue();
			inputTo = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + parseColumnName(filter.getProperty()) + "_to").getValue();
			selectFrom = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_from").getValue();
			selectTo = rawRequestDataMap.get(reportFilterKeys[HTML_TAG_TYPE_THRIFT_INDEX] + "_" +
				reportFilterKeys[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_to").getValue();
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid inputs and relational operators."));
		}
		try {
			RelationalOperatorThrift relationalOperatorThriftFrom = RelationalOperatorThrift.findByValue(Integer.parseInt(selectFrom));
			RelationalOperatorThrift relationalOperatorThriftTo = RelationalOperatorThrift.findByValue(Integer.parseInt(selectTo));
			if (relationalOperatorThriftFrom == null || relationalOperatorThriftTo == null)
				throw new Exception();
			if (relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT) && relationalOperatorThriftTo.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT))
				return null;
			if(!relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT)){
				filter.setFromOperator((RelationalOperator) relationalOperatorOptions.get(relationalOperatorThriftFrom));
				filter.setFromValue(validateBigDecimal("The input", inputFrom));
			}

			if(!relationalOperatorThriftTo.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT)){
				filter.setToOperator((RelationalOperator) relationalOperatorOptions.get(relationalOperatorThriftTo));
				filter.setToValue(validateBigDecimal("The input", inputTo));
			}
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid relational operators."));
		}

		return filter;
	}

	public CustomReportUpdateResponse updateReportingCriteriaFilter(String filterKey,Object filterValue,Map<String,Object> rawFilterMap,ReportingCriteriaFiltering filter){
		CustomReportUpdateResponse response = new CustomReportUpdateResponse();
		String key = filterKey.substring(0,filterKey.lastIndexOf("_"));
		key = key.substring(0,key.lastIndexOf("_"));

		String selectFrom = (String)rawFilterMap.get(key + "_select_from");
		String inputFrom = (String)rawFilterMap.get(key + parseColumnName(filter.getProperty()) + "_from");
		String selectTo = (String)rawFilterMap.get(key + "_select_to");
		String inputTo = (String)rawFilterMap.get(key + parseColumnName(filter.getProperty()) + "_to");
		if(inputFrom == null || inputTo == null){
			response.setSuccess(false);
			response.addError(new ConstraintViolation("reports.custom.could_not_save",filter.getProperty()));
			return response;
		}
		RelationalOperator relationalOperatorFrom =
			(RelationalOperator)relationalOperatorOptions.get(RelationalOperatorThrift.findByValue(Integer.parseInt(selectFrom)));
		RelationalOperator relationalOperatorTo =
			(RelationalOperator)relationalOperatorOptions.get(RelationalOperatorThrift.findByValue(Integer.parseInt(selectTo)));
		if(relationalOperatorFrom.equals(RelationalOperator.PLEASE_SELECT)){
			filter.setFromOperator(null);
			filter.setFromValue(null);
		}else{
			filter.setFromOperator(relationalOperatorFrom.getOperator());
			try{
				filter.setFromValue(validateBigDecimal(filter.getProperty(),inputFrom));
			}catch (WorkDisplayException e){
				response.setSuccess(false);
				response.addError(new ConstraintViolation("reports.custom.not_a_number",filter.getProperty()));
				return response;
			}
		}
		if(relationalOperatorTo.equals(RelationalOperator.PLEASE_SELECT)){
			filter.setToOperator(null);
			filter.setToValue(null);
			if(relationalOperatorFrom.equals(RelationalOperator.PLEASE_SELECT)){
				filter.setDeleted(true);
				return response;
			}
		}else{
			filter.setToOperator(relationalOperatorTo.getOperator());
			try{
				filter.setToValue(validateBigDecimal(filter.getProperty(),inputTo));
			}catch (WorkDisplayException e){
				response.setSuccess(false);
				response.addError(new ConstraintViolation("reports.custom.not_a_number",filter.getProperty()));
				return response;
			}
		}
		response.setSuccess(true);
		return response;
	}

}
