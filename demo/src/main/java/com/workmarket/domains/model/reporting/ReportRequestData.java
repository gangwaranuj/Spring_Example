package com.workmarket.domains.model.reporting;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.thrift.work.display.ColumnValuesRequest;
import com.workmarket.thrift.work.display.FilteringEntityRequest;
import com.workmarket.thrift.work.display.WorkDisplayException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ReportRequestData implements java.io.Serializable {

	private String entityKey;
	private Long buyerId;
	private Long companyId;
	private Locale locale;
	private String userNumber;
	private String reportName;
	private PaginationPag paginationPag;
	private Boolean generateReport;
	private Boolean hasWorkCustomFields = Boolean.FALSE;
	private List<Long> workCustomFieldIds;
	private List<String> workCustomFieldNames;
	private Long masqueradeUserId;
	private Long reportId;

	private List<OrderBy> sortColumns = new ArrayList<OrderBy>();
	private Set<String> displayKeys = new LinkedHashSet<String>();
	private List<ReportFilter> reportFilters = new ArrayList<ReportFilter>();
	Map<String, CustomFieldEntity> callM = new HashMap<String, CustomFieldEntity>();
	public static final String WORK_ENTITY_KEY = "work";
	public static final String SELECT_ALL = "select_all";
	public static final String DISPLAY_KEYS_DELIMITER = ",";
	private static final long serialVersionUID = -465370361951522919L;
	private static final Log logger = LogFactory.getLog(ReportRequestData.class);

	public ReportRequestData(){

	}

	public ReportRequestData(FilteringEntityRequest filteringEntityRequest,Long companyId){
		if (filteringEntityRequest.getPaginationPageThrift() != null) {
			PaginationPag paginationPag = new PaginationPag();
			paginationPag.setPageSize(filteringEntityRequest.getPaginationPageThrift().getPageSize());
			paginationPag.setStartRow(filteringEntityRequest.getPaginationPageThrift().getStartRow());
			this.setPaginationPag(paginationPag);
		}

		this.setGenerateReport(filteringEntityRequest.isGenerateReport());
		this.setEntityKey(WORK_ENTITY_KEY);
		this.setCompanyId(companyId);
		this.setUserNumber(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber());
		this.setLocale(new Locale(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getLocale()));
		this.setReportName(filteringEntityRequest.getReportName());
		this.setMasqueradeUserId(filteringEntityRequest.getMasqueradeUserId());
		this.setWorkCustomFieldIds(filteringEntityRequest.getWorkCustomFieldIds());

	}

	public ReportRequestData(FilteringEntityRequest filteringEntityRequest, User currentUser){
		if (filteringEntityRequest.getPaginationPageThrift() != null) {
			PaginationPag paginationPag = new PaginationPag();
			paginationPag.setPageSize(filteringEntityRequest.getPaginationPageThrift().getPageSize());
			paginationPag.setStartRow(filteringEntityRequest.getPaginationPageThrift().getStartRow());
			this.setPaginationPag(paginationPag);
		}

		this.setGenerateReport(filteringEntityRequest.isGenerateReport());
		this.setEntityKey(WORK_ENTITY_KEY);
		this.setCompanyId(currentUser.getCompany().getId());
		this.setUserNumber(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber());
		this.setLocale(new Locale(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getLocale()));
		this.setReportName(filteringEntityRequest.getReportName());
		this.setMasqueradeUserId(filteringEntityRequest.getMasqueradeUserId());
		this.setWorkCustomFieldIds(filteringEntityRequest.getWorkCustomFieldIds());
	}

	public ReportRequestData(ReportingCriteria reportingCriteria,Company company,Locale locale){
		Assert.notNull(reportingCriteria);
		Assert.notNull(company);

		this.setReportName(reportingCriteria.getReportName());
		this.setCompanyId(company.getId());

		if (reportingCriteria.getDisplayKeys() != null && reportingCriteria.getDisplayKeys().length() > 0) {
			Set<String> displayKeys = new HashSet<String>(Arrays.asList(reportingCriteria.getDisplayKeys().split(DISPLAY_KEYS_DELIMITER)));
			this.setDisplayKeys(displayKeys);
		}
		this.setLocale(locale);
		this.setEntityKey(WORK_ENTITY_KEY);
		this.setHasWorkCustomFields(reportingCriteria.isCustomFieldsReport());

		List<Long> workCustomFieldIds = new ArrayList<>();
		if (reportingCriteria.getReportingCriteriaFiltering() != null) {
			for (ReportingCriteriaFiltering reportCriteriaFilter : reportingCriteria.getReportingCriteriaFiltering()) {
				if (CustomFieldEntity.WORK_CUSTOM_FIELDS.equals(reportCriteriaFilter.getProperty())) {
					this.setHasWorkCustomFields(true);
					continue;
				} else if (CustomFieldEntity.WORK_CUSTOM_FIELD_ID.equals(reportCriteriaFilter.getProperty())) {
					workCustomFieldIds.add(Long.parseLong(reportCriteriaFilter.getFieldValue()));
				}
				addFilter(reportCriteriaFilter);
			}
		}

		this.setWorkCustomFieldIds(workCustomFieldIds);
	}

	public ReportRequestData(ReportingCriteria reportingCriteria,Company company,Locale locale, String userNumber, Long masqueradeUserId){
		Assert.notNull(reportingCriteria);
		Assert.notNull(company);
		Assert.notNull(userNumber);

		this.setUserNumber(userNumber);
		this.setReportName(reportingCriteria.getReportName());
		this.setMasqueradeUserId(masqueradeUserId);
		this.setCompanyId(company.getId());

		if (reportingCriteria.getDisplayKeys() != null && reportingCriteria.getDisplayKeys().length() > 0) {
			Set<String> displayKeys = new HashSet<String>(Arrays.asList(reportingCriteria.getDisplayKeys().split(DISPLAY_KEYS_DELIMITER)));
			this.setDisplayKeys(displayKeys);
		}
		this.setLocale(locale);
		this.setEntityKey(WORK_ENTITY_KEY);
		this.setHasWorkCustomFields(reportingCriteria.isCustomFieldsReport());

		List<Long> workCustomFieldIds = new ArrayList<>();
		if (reportingCriteria.getReportingCriteriaFiltering() != null) {
			for (ReportingCriteriaFiltering reportCriteriaFilter : reportingCriteria.getReportingCriteriaFiltering()) {
				if (CustomFieldEntity.WORK_CUSTOM_FIELDS.equals(reportCriteriaFilter.getProperty())){
					this.setHasWorkCustomFields(true);
					continue;
				} else if (CustomFieldEntity.WORK_CUSTOM_FIELD_ID.equals(reportCriteriaFilter.getProperty())) {
					workCustomFieldIds.add(Long.parseLong(reportCriteriaFilter.getFieldValue()));
				}
				addFilter(reportCriteriaFilter);
			}
		}

		this.setWorkCustomFieldIds(workCustomFieldIds);
	}


	public void addFilter(ReportFilter filter){
		reportFilters.add(filter);
	}

	public void addFilter(ReportingCriteriaFiltering reportFilter){
		ReportFilter filter = new ReportFilter();
		DateFormat postSimpleDateformat = new SimpleDateFormat("MM/dd/yyyy");

		filter.setContains(reportFilter.getContains());
		filter.setFieldValue(reportFilter.getFieldValue());
		filter.setFromDate(reportFilter.getFromDate());
		filter.setFromValue(reportFilter.getFromValue());
		filter.setProperty(reportFilter.getProperty());
		filter.setToDate(reportFilter.getToDate());
		filter.setToValue(reportFilter.getToValue());

		if (reportFilter.getFromDate() != null)
			filter.setInputFromValue(postSimpleDateformat.format(reportFilter.getFromDate().getTime()));

		if (reportFilter.getToDate() != null)
			filter.setInputToValue(postSimpleDateformat.format(reportFilter.getToDate().getTime()));

		try{
			if (reportFilter.getFieldValueOperator() != null)
				filter.setFieldValueOperator(RelationalOperator.getRelationalOperator(reportFilter.getFieldValueOperator(), null));

			if (reportFilter.getFilteringType() != null)
				filter.setFilteringType(FilteringType.getInput(reportFilter.getFilteringType()));

			if (reportFilter.getFromOperator() != null)
				filter.setFromOperator(RelationalOperator.getRelationalOperator(reportFilter.getFromOperator(), null));

			if (reportFilter.getToOperator() != null)
				filter.setToOperator(RelationalOperator.getRelationalOperator(reportFilter.getToOperator(), null));

			reportFilters.add(filter);

		}catch(Exception e){
			logger.error("Could not parse relational operator " + e.getMessage());
		}

	}

	//if its a filter it will have integer_integer_string_text
	//if its a display it will have string_integer as the format
	private static boolean isFilterItem(ColumnValuesRequest reportRequestItem){

		String[] filterName = reportRequestItem.getKeyName().split("_");

		try {
			int result = Integer.parseInt(filterName[0]);
			try{
				ReportFilter.ReportFilterType filterType = ReportFilter.ReportFilterType.values()[result];
			}catch(ArrayIndexOutOfBoundsException e){
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		return true;

	}

	public void generateReportFilters(FilteringEntityRequest requestedReportFilters) throws WorkDisplayException{

		List<ColumnValuesRequest> reportRequestedFilterList = requestedReportFilters.getColumnValuesRequests();

		//for some filters we need to combine two or three field values from the user
		//so we need a map to quickly look these up
		//e.g. a date range with a to and from field or a to and from number range
		Map<String, ColumnValuesRequest> requestedFiltersMap = new HashMap<String, ColumnValuesRequest>();
		for (ColumnValuesRequest columnValuesRequest : reportRequestedFilterList) {
			if(columnValuesRequest.getKeyName().equals(SELECT_ALL)){
				continue;
			}
			// we need to handle conflicts for multi-selects and build a comma seperated list of values
			ColumnValuesRequest existingValue = requestedFiltersMap.get(columnValuesRequest.getKeyName());
			if(existingValue != null){
				existingValue.setValue(existingValue.getValue() + "," + columnValuesRequest.getValue());
				requestedFiltersMap.put(columnValuesRequest.getKeyName(),existingValue);
			}else{
				requestedFiltersMap.put(columnValuesRequest.getKeyName(),columnValuesRequest);
			}
		}

		//now iterate through and create each display or filter item requested for this filter
		for(Map.Entry<String,ColumnValuesRequest> entry : requestedFiltersMap.entrySet()){
			ColumnValuesRequest reportFilterRequest = entry.getValue();
			Assert.notNull(entry);
			if (isFilterItem(reportFilterRequest)){
				if(StringUtils.isEmpty(reportFilterRequest.getValue())) continue;       //if there's no value in the filter, skip it
				ReportFilter filter = ReportFilter.createReportFilter(reportFilterRequest,requestedFiltersMap);
				if(filter == null) continue;
				this.addFilter(filter);
			} else {  //its a display only item, not a filter
				ReportFilter filter = ReportFilter.createDisplayOnlyRequest(reportFilterRequest);
				if(filter == null) continue;
				if(filter.hasWorkCustomFields()){
					this.setHasWorkCustomFields(true);
				} else {
					this.displayKeys.add(filter.getProperty());
				}
				this.reportFilters.add(filter);
			}
		}
	}

	/**
	 * @return the entityKey
	 */
	public String getEntityKey() {
		return entityKey;
	}
	/**
	 * @param entityKey the entityKey to set
	 */
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	/**
	 * @return the displayKeys
	 */
	public Set<String> getDisplayKeys() {
		return displayKeys;
	}
	/**
	 * @param displayKeys the displayKeys to set
	 */
	public void setDisplayKeys(Set<String> displayKeys) {
		this.displayKeys = displayKeys;
	}
	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<ReportFilter> getReportFilterL() {
		return reportFilters;
	}

	public void setReportFilterL(List<ReportFilter> entityRequestForFilteringL) {
		this.reportFilters = entityRequestForFilteringL;
	}

	public List<OrderBy> getSortColumns() {
		return sortColumns;
	}

	public void setSortColumns(List<OrderBy> sortColumns) {
		this.sortColumns = sortColumns;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	/**
	 * @return the userNumber
	 */
	public String getUserNumber() {
		return userNumber;
	}
	/**
	 * @param userNumber the userNumber to set
	 */
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}
	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	/**
	 * @return the paginationPag
	 */
	public PaginationPag getPaginationPag() {
		return paginationPag;
	}
	/**
	 * @param paginationPag the paginationPag to set
	 */
	public void setPaginationPag(PaginationPag paginationPag) {
		this.paginationPag = paginationPag;
	}
	/**
	 * @return the generateReport
	 */
	public Boolean getGenerateReport() {
		return generateReport;
	}
	/**
	 * @param generateReport the generateReport to set
	 */
	public void setGenerateReport(Boolean generateReport) {
		this.generateReport = generateReport;
	}
	/**
	 * @return the callM
	 */
	public Map<String, CustomFieldEntity> getCallM() {
		return callM;
	}
	/**
	 * @param callM the callM to set
	 */
	public void setCallM(Map<String, CustomFieldEntity> callM) {
		this.callM = callM;
	}
	/**
	 * @return the hasWorkCustomFields
	 */
	public Boolean getHasWorkCustomFields() {
		return hasWorkCustomFields;
	}


	public void setHasWorkCustomFields(Boolean hasWorkCustomFields) {
		this.hasWorkCustomFields = hasWorkCustomFields;
	}

	public Long getMasqueradeUserId() {
		return masqueradeUserId;
	}

	public void setMasqueradeUserId(Long masqueradeUserId) {
		this.masqueradeUserId = masqueradeUserId;
	}

	public boolean hasMasqueradeUserId() {
		return masqueradeUserId != null;
	}

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public List<Long> getWorkCustomFieldIds() {
		return workCustomFieldIds;
	}

	public void setWorkCustomFieldIds(List<Long> workCustomFieldIds) {
		this.workCustomFieldIds = workCustomFieldIds;
	}

	public List<String> getWorkCustomFieldNames() {
		return workCustomFieldNames;
	}

	public void setWorkCustomFieldNames(List<String> workCustomFieldNames) {
		this.workCustomFieldNames = workCustomFieldNames;
	}
}
