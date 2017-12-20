package com.workmarket.domains.model.reporting;

import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.thrift.work.display.ColumnValuesRequest;
import com.workmarket.thrift.work.display.WorkDisplayException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFilter implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private String property;
	private FilteringType filteringType;
	private Calendar fromDate;
	private Calendar toDate;
	private BigDecimal fromValue;//perhaps a BigDecimal?
	private BigDecimal toValue;
	private String contains;
	private String fieldValue;
	private RelationalOperator fromOperator;
	private RelationalOperator toOperator;
	private RelationalOperator fieldValueOperator;
	private String inputFromValue;
	private String inputToValue;
	private boolean hasWorkCustomFields;
	private List<Long> workCustomFieldIds;

	private static final long serialVersionUID = -8299269483732846694L;

	private final DateFormat postSimpleDateformat = new SimpleDateFormat("MM/dd/yyyy");

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

	private static Map<Integer,AbstractReportFilterBuilder> reportFilterBuilders = new HashMap<Integer,AbstractReportFilterBuilder>();
	static{
		reportFilterBuilders.put(ReportFilterType.INPUT_TEXT.ordinal(), new InputTextFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.SELECT_OPTION.ordinal(), new SelectFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.MULTI_SELECT_OPTION.ordinal(), new MultiSelectFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.DATE.ordinal(), new DateFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.DATE_TIME.ordinal(), new DateTimeFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.TO_FROM_DATES.ordinal(), new DateToFromFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.NUMERIC.ordinal(), new NumericFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.NUMERIC_RANGE.ordinal(), new NumericRangeFilterBuilder());
		reportFilterBuilders.put(ReportFilterType.DISPLAY.ordinal(), new DisplayFilterBuilder());
	}

	public static ReportFilter createReportFilter(ColumnValuesRequest rawFilterData,Map<String,ColumnValuesRequest> filterDataMap) throws WorkDisplayException{
		Assert.hasText(rawFilterData.getKeyName());
		String columnKeys[] = rawFilterData.getKeyName().split("_");
		Assert.notNull(columnKeys[0]);
		AbstractReportFilterBuilder filterBuilder = reportFilterBuilders.get(Integer.parseInt(columnKeys[0]));
		Assert.notNull(filterBuilder);
		return filterBuilder.buildReportFilter(rawFilterData,filterDataMap);

	}

	public static ReportFilter createDisplayOnlyRequest(ColumnValuesRequest rawDisplayData) throws WorkDisplayException{

		DisplayFilterBuilder filterBuilder = new DisplayFilterBuilder();
		return filterBuilder.buildDisplayOnly(rawDisplayData);

	}

	public ReportFilter(){

	}

	public List<Long> getWorkCustomFieldIds() {
		return workCustomFieldIds;
	}

	public void setWorkCustomFieldIds(List<Long> workCustomFieldIds) {
		this.workCustomFieldIds = workCustomFieldIds;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public FilteringType getFilteringType() {
		return filteringType;
	}

	public void setFilteringType(FilteringType filteringType) {
		this.filteringType = filteringType;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	public BigDecimal getFromValue() {
		return fromValue;
	}

	public void setFromValue(BigDecimal fromValue) {
		this.fromValue = fromValue;
	}

	public BigDecimal getToValue() {
		return toValue;
	}

	public void setToValue(BigDecimal toValue) {
		this.toValue = toValue;
	}

	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public RelationalOperator getFromOperator() {
		return fromOperator;
	}

	public void setFromOperator(RelationalOperator fromOperator) {
		this.fromOperator = fromOperator;
	}

	public RelationalOperator getToOperator() {
		return toOperator;
	}

	public void setToOperator(RelationalOperator toOperator) {
		this.toOperator = toOperator;
	}

	public RelationalOperator getFieldValueOperator() {
		return fieldValueOperator;
	}

	public void setFieldValueOperator(RelationalOperator fieldValueOperator) {
		this.fieldValueOperator = fieldValueOperator;
	}

	/**
	 * @return the inputFromValue
	 */
	public String getInputFromValue() {
		return inputFromValue;
	}

	/**
	 * @param inputFromValue the inputFromValue to set
	 */
	public void setInputFromValue(String inputFromValue) {
		this.inputFromValue = inputFromValue;
	}

	/**
	 * @return the inputToValue
	 */
	public String getInputToValue() {
		return inputToValue;
	}

	/**
	 * @param inputToValue the inputToValue to set
	 */
	public void setInputToValue(String inputToValue) {
		this.inputToValue = inputToValue;
	}

	public boolean hasWorkCustomFields(){
		return this.hasWorkCustomFields;
	}

	public void setHasWorkCustomFields(boolean hasWorkCustomFields){

		this.hasWorkCustomFields = hasWorkCustomFields;

	}
}



