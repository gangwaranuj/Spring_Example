package com.workmarket.service.thrift.transactional.work;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.ReportingCriteriasDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.datetime.TimeZoneDAO;
import com.workmarket.dao.report.ReportRecurrenceDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.reporting.service.WorkReportGeneratorServiceImpl;
import com.workmarket.reporting.util.ReportingContextComparator;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ReportRecurrenceDTO;
import com.workmarket.thrift.work.display.*;
import com.workmarket.thrift.work.report.FilteringTypeThrift;
import com.workmarket.thrift.work.report.RelationalOperatorThrift;
import com.workmarket.thrift.work.report.WorkReportColumnType;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WorkDisplayHandler implements WorkDisplay.Iface {

	@Autowired private UserDAO userDAO;
	@Autowired private ReportingCriteriasDAO reportingCriteriaDAO;
	@Autowired private ReportRecurrenceDAO reportRecurrenceDAO;
	@Autowired private TimeZoneDAO timeZoneDAO;
	@Autowired private UserService userService;

	@Autowired @Qualifier("readOnlyJdbcTemplate") private NamedParameterJdbcTemplate jdbcTemplate;

	@Resource(name = "workReportGeneratorService") private WorkReportGeneratorServiceImpl workReportGeneratorServiceImpl;
	@Resource(name = "work_report_column_types") private BidiMap workReportColumnTypes;
	@Resource(name = "reporting_context") private BidiMap reportingContext;
	@Resource(name = "html_tag_type_options") private BidiMap htmlTagTypeOptions;
	@Resource(name = "relational_operator_options") private BidiMap relationalOperatorOptions;
	@Resource(name = "work_report_input_types") private BidiMap workReportInputTypes;

	public static final Integer HTML_TAG_TYPE_THRIFT_INDEX = 0;
	public static final Integer WORK_REPORT_COLUMN_TYPE_INDEX = 1;
	public static final String DISPLAY_KEYS_DELIMITER = ",";
	public static final String PLEASE_SELECT = "pleaseSelect";
	public static final String WORK_ENTITY_KEY = "work";

	private static final Log logger = LogFactory.getLog(WorkDisplayHandler.class);
	private final DateFormat postSimpleDateformat = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public WorkReportEntityBucketsCompositeResponse getWorkReportEntityBuckets(ReportingTypeRequest request) throws WorkDisplayException, RuntimeException {
		try {
			logger.debug("reportingReportType:" + request.getReportingReportType() + " userNumber:" + request.getReportingTypesInitialRequest().getUserNumber());
			Locale locale = new Locale(request.getReportingTypesInitialRequest().getLocale());

			ReportingContext context = (ReportingContext) getReportingContext().get(request.getReportingReportType());
			context.setCompanyId(request.getReportingTypesInitialRequest().getCompanyId());

			List<WorkReportEntityBucketResponse> entityBucketResponses = constructWorkReportEntityBucketResponses(context, locale, null, null);
			WorkReportEntityBucketsCompositeResponse compositeResponse = new WorkReportEntityBucketsCompositeResponse();
			compositeResponse.setWorkReportEntityBucketResponses(entityBucketResponses);

			return compositeResponse;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteCustomReport(ReportingTypesInitialRequest request, long criteriaId) throws WorkDisplayException, RuntimeException {

		try {
			Assert.notNull(request, "request can't be null");

			ReportingCriteria criteria = getReportingCriteriaDAO().get(criteriaId);
			if (criteria != null) {
				criteria.setDeleted(true);
				getReportingCriteriaDAO().saveOrUpdate(criteria);
				return true;
			} else {
				throw new RuntimeException("Can't delete ReportingCriteria with id:" + criteriaId + " and companyId:" + request.getCompanyId());
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public ReportRecurrence findReportRecurrence(long reportKey) {
		return reportRecurrenceDAO.findByReportKey(reportKey);
	}

	@Override public List<ReportRecurrence> findReportRecurrencesByCompanyId(long companyId) {
		return reportRecurrenceDAO.findReportRecurrencesByCompanyId(companyId);
	}

	@Override
	public SavedCustomReportResponse saveCustomReportRecurrence(ReportRecurrenceDTO dto) {

		Assert.notNull(dto, "report recurrence can't be null");
		Assert.notNull(dto.getCompanyId(), "company id can't be null");

		ReportRecurrence recurrence = null;

		if (dto.getReportKey() != null) {
			recurrence = reportRecurrenceDAO.findByReportKey(dto.getReportKey());

			if (recurrence != null) {
				Assert.isTrue(recurrence.getCompanyId().equals(dto.getCompanyId()), "company id must match existing");
				//Assert.isTrue(recurrence.getUserId().equals(dto.getUserId()), "user id must match existing");
				dto.copyToReportRecurrence(recurrence);
			}
		}
		if (recurrence == null) {
			recurrence = dto.copyToReportRecurrence(new ReportRecurrence());
		}

		TimeZone timeZone = timeZoneDAO.findTimeZonesByTimeZoneId(dto.getTimeZoneId());
		Assert.notNull(timeZone);

		recurrence.setTimeZone(timeZone); // this is a bit stinky but need to map between TimeZone and time zone id string
		reportRecurrenceDAO.saveRecurrence(recurrence);

		return new SavedCustomReportResponse().setReportKey(recurrence.getReportKey());
	}

	@Override
	public List<WorkReportEntityBucketResponse> constructWorkReportEntityBucketResponses(
			ReportingContext context,
			Locale locale,
			Map<String, ReportFilter> entityRequestMap,
			Map<String, ReportFilter> displayEntityRequestMap) throws Exception {

		List<WorkReportEntityBucketResponse> bucketResponses = new ArrayList<WorkReportEntityBucketResponse>();

		for (EntityBucket entityBucket : context.getEntityBuckets()) {
			WorkReportEntityBucketResponse bucketResponse = new WorkReportEntityBucketResponse();
			bucketResponse.setDisplayName(entityBucket.getDisplayNameM().get(locale));
			bucketResponse.setKeyName(entityBucket.getKeyName());
			LocationOrderResponse locationOrderResponse = new LocationOrderResponse();
			BeanUtils.copyProperties(entityBucket.getLocationOrder(), locationOrderResponse);
			bucketResponse.setLocationOrderResponse(locationOrderResponse);

			List<FilteringEntityResponse> filteringEntityResponses = new ArrayList<FilteringEntityResponse>();
			for (Entity entity : entityBucket.getEntities()) {
				FilteringEntityResponse filteringEntityResponse = constructFilteringEntityResponse(entity, locale, context);
				if (entityRequestMap != null) {
					ReportFilter entityRequestForFiltering = entityRequestMap.get(entity.getFullKeyName());
					if (entityRequestForFiltering != null) {
						InputValues inputValues = constructInputValues(entityRequestForFiltering);
						filteringEntityResponse.setInputValues(inputValues);
					}
				}

				if (displayEntityRequestMap != null) {
					ReportFilter displayEntityRequest = displayEntityRequestMap.get(entity.getFullKeyName());
					if (displayEntityRequest != null)
						filteringEntityResponse.setIsDisplay(true);
					else
						filteringEntityResponse.setIsDisplay(false);
				}

				filteringEntityResponses.add(filteringEntityResponse);
			}

			bucketResponse.setFilteringEntityResponses(filteringEntityResponses);
			bucketResponses.add(bucketResponse);
		}
		return bucketResponses;
	}

	private FilteringEntityResponse constructFilteringEntityResponse(Entity entity, Locale locale, ReportingContext context) {

		logger.debug("keyName:" + entity.getKeyName() + " displayName:" + entity.getDisplayNameM().get(locale));

		WorkReportEntityResponse entityResponse = new WorkReportEntityResponse();
		entityResponse.setDisplayName(entity.getDisplayNameM().get(locale));
		entityResponse.setFuture(entity.getFilterInputTag().getFuture());
		entityResponse.setKeyName(entity.getKeyName());
		entityResponse.setFilterable(entity.getFilterInputTag().getFilterable());
		entityResponse.setWorkReportColumnType((WorkReportColumnType) getWorkReportColumnTypes().getKey(entity.getFullKeyName()));

		LocationOrderResponse locationOrderResponseEntity = new LocationOrderResponse();
		BeanUtils.copyProperties(entity.getLocationOrder(), locationOrderResponseEntity);
		entityResponse.setLocationOrderResponse(locationOrderResponseEntity);

		FilteringEntityResponse filteringEntityResponse = constructFilteringEntityResponse(entity, context);
		filteringEntityResponse.setWorkReportEntityResponse(entityResponse);

		return filteringEntityResponse;
	}

	@Override
	public ReportingTypesCompositeResponse getWorkReportTypes(ReportingTypesInitialRequest request) throws WorkDisplayException, RuntimeException {
		try {
			logger.debug("locale:" + request.getLocale());

			@SuppressWarnings("unchecked")
			List<? extends ReportingContext> reportingContexts = Lists.newArrayList(getReportingContext().values());
			Collections.sort(reportingContexts, new ReportingContextComparator());

			List<ReportingTypeResponse> reportingTypeResponses = new ArrayList<ReportingTypeResponse>();
			Locale locale = new Locale(request.getLocale());
			for (ReportingContext context : reportingContexts) {
				String displayName = context.getDisplayNameM().get(locale);
				ReportingTypeResponse reportingTypeResponse = new ReportingTypeResponse();
				reportingTypeResponse
						.setDisplayName(displayName)
						.setReportingReportType((ReportingReportType) getReportingContext().getKey(context));
				reportingTypeResponses.add(reportingTypeResponse);
				logger.debug("displayName:" + displayName);
			}

			ReportingTypesCompositeResponse result = new ReportingTypesCompositeResponse();
			result.setReportingTypeResponses(reportingTypeResponses);

			return result;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new WorkDisplayException(WorkDisplayErrorType.INVALID_REQUEST, Lists.newArrayList(e.getMessage()));
		}
	}

	@Override
	public ReportResponse getGenerateReport(FilteringEntityRequest requestedReportFilters) throws Exception{


		Assert.notNull(requestedReportFilters);
		ReportRequestData reportRequestData = new ReportRequestData(requestedReportFilters,
				getUserDAO().findUserByUserNumber(
						requestedReportFilters.
								getReportingTypeRequest().
								getReportingTypesInitialRequest().
								getUserNumber(),
						false)
		);

		reportRequestData.generateReportFilters(requestedReportFilters);

		PaginationThrift paginationThrift = new PaginationThrift();

		Optional<EntityResponseForReport> response = getWorkReportGeneratorServiceImpl().generate(reportRequestData);
		if(!response.isPresent()) return new ReportResponse();
		EntityResponseForReport entityResponse = response.get();

		if (entityResponse.getPagination() != null) {
			paginationThrift.setTotal(entityResponse.getPagination().getTotal());
			List<PaginationPageThrift> paginationPageThrifts = new ArrayList<PaginationPageThrift>();
			for (PaginationPag paginationPag : entityResponse.getPagination().getPaginationPags()) {
				PaginationPageThrift paginationPageThrift = new PaginationPageThrift();
				paginationPageThrift.setStartRow(paginationPag.getStartRow());
				paginationPageThrift.setPageSize(paginationPag.getPageSize());
				paginationPageThrifts.add(paginationPageThrift);
			}
			paginationThrift.setPaginationPageThrifts(paginationPageThrifts);
		}

		ReportRow reportRowHeaders = null;
		List<ReportRow> row = null;

		if (!reportRequestData.getGenerateReport()) {
			reportRowHeaders = new ReportRow(0, entityResponse.getHeaders());
			row = getReportRows(entityResponse);
		}

		WorkReportEntityBucketsCompositeResponse compositeResponse =
				constructWorkReportEntityBucketsCompositeResponse(reportRequestData, requestedReportFilters.getReportingTypeRequest().getReportingReportType());

		if (compositeResponse.getWorkReportEntityBucketResponses() != null && compositeResponse.getWorkReportEntityBucketResponses().size() > 0){
			for (FilteringEntityResponse fer : compositeResponse.getWorkReportEntityBucketResponses().get(0).getFilteringEntityResponses()){
				if (fer.getInputValues().getToValue() != null && fer.getHtmlTagTypeThrift() == HtmlTagTypeThrift.TO_FROM_DATES) {
					Calendar correctedDate = DateUtilities.getCalendarFromDateString(fer.getInputValues().getToValue(), Constants.WM_TIME_ZONE);
					correctedDate.add(Calendar.DATE, 1);
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
					fer.getInputValues().setToValue(simpleDateFormat.format(correctedDate.getTime()));
				}
			}
		}

		return new ReportResponse(entityResponse.getFileName(), reportRowHeaders, row, compositeResponse, paginationThrift);


	}




	private ReportRequestData extractReportRequestData(FilteringEntityRequest filteringEntityRequest, Locale locale) throws WorkDisplayException, RuntimeException {

		ReportRequestData entityRequest = new ReportRequestData();

		try {

			if (filteringEntityRequest.getPaginationPageThrift() != null) {
				PaginationPag paginationPag = new PaginationPag();
				paginationPag.setPageSize(filteringEntityRequest.getPaginationPageThrift().getPageSize());
				paginationPag.setStartRow(filteringEntityRequest.getPaginationPageThrift().getStartRow());
				entityRequest.setPaginationPag(paginationPag);
			}

			entityRequest.setGenerateReport(filteringEntityRequest.isGenerateReport());
			entityRequest.setEntityKey(WORK_ENTITY_KEY);
			User user = getUserDAO().findUserByUserNumber(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber(), false);
			entityRequest.setCompanyId(user.getCompany().getId());
			entityRequest.setUserNumber(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber());
			entityRequest.setLocale(new Locale(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getLocale()));
			entityRequest.setReportName(filteringEntityRequest.getReportName());
			entityRequest.setMasqueradeUserId(filteringEntityRequest.getMasqueradeUserId());

			Set<String> entityKeys = new HashSet<String>();
			Set<String> displayKeys = new HashSet<String>();
			logger.debug("reportingReportType:" + filteringEntityRequest.getReportingTypeRequest().getReportingReportType() + ", userNumber:"
					+ filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber());

			List<ColumnValuesRequest> columnValuesRequests = filteringEntityRequest.getColumnValuesRequests();
			Map<String, String> nameValueMap = new HashMap<String, String>();

			for (ColumnValuesRequest columnValuesRequest : columnValuesRequests) {
				nameValueMap.put(columnValuesRequest.getKeyName(), columnValuesRequest.getValue());
			}

			for (ColumnValuesRequest columnValuesRequest : columnValuesRequests) {

				String columnValuesRequestKey[] = columnValuesRequest.getKeyName().split("_");
				try {
					HtmlTagTypeThrift htmlTagTypeThrift = HtmlTagTypeThrift.findByValue(Integer.parseInt(columnValuesRequestKey[0]));

					WorkReportColumnType workReportColumnType = WorkReportColumnType.findByValue(Integer.parseInt(columnValuesRequestKey[1]));
					String column = (String) getWorkReportColumnTypes().get(workReportColumnType);

					if (entityKeys.add(column)) {
						ReportFilter entityRequestForFiltering =
								createReportFilter(columnValuesRequestKey, htmlTagTypeThrift, column, nameValueMap,columnValuesRequest.getValue(), locale);
						if (entityRequestForFiltering != null) {
							entityRequest.getReportFilterL().add(entityRequestForFiltering);
						}
					}
				} catch (RuntimeException nfe) {
					WorkReportColumnType workReportColumnType = WorkReportColumnType.findByValue(Integer.parseInt(columnValuesRequest.getValue()));
					if (!workReportColumnType.equals(WorkReportColumnType.WORK_SELECT_ALL)) {
						String column = (String) getWorkReportColumnTypes().get(workReportColumnType);
						if (workReportColumnType.equals(WorkReportColumnType.WORK_CUSTOM_FIELDS)) {
							entityRequest.setHasWorkCustomFields(Boolean.TRUE);
						} else {
							displayKeys.add(column);
						}
						ReportFilter entityRequestForFiltering = new ReportFilter();
						entityRequestForFiltering.setFilteringType(FilteringType.DISPLAY);
						entityRequestForFiltering.setProperty(column);
						entityRequest.getReportFilterL().add(entityRequestForFiltering);
					}
				}
			}// end columnValuesRequestI while

			if (CollectionUtils.isEmpty(displayKeys)) {
				throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have display criteria."));
			}

			if (CollectionUtils.isNotEmpty(filteringEntityRequest.getWorkCustomFieldIds())) {
				entityRequest.setWorkCustomFieldIds(filteringEntityRequest.getWorkCustomFieldIds());
			}

			entityRequest.setDisplayKeys(displayKeys);

		} catch (WorkDisplayException wde) {
			logger.debug(wde.getMessages());
			throw wde;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return entityRequest;
	}

	/*
		@Deprecated
		@Override
		public ReportResponse getGenerateReport(FilteringEntityRequest filteringEntityRequest) throws Exception {


			Assert.notNull(filteringEntityRequest, "filteringEntityRequest can't be null");

			Locale locale = new Locale(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getLocale());

			ReportRequestData entityRequest = extractReportRequestData(filteringEntityRequest, locale);


			PaginationThrift paginationThrift = new PaginationThrift();

			Optional<EntityResponseForReport> response = getWorkReportGeneratorServiceImpl().generate(entityRequest);
			if (!response.isPresent())
				return null;         // TODO: when Thrift is gone, refactor this method to return an Optional<ReportResponse>

			EntityResponseForReport entityResponse = response.get();

			if (entityResponse.getPagination() != null) {
				paginationThrift.setTotal(entityResponse.getPagination().getTotal());
				List<PaginationPageThrift> paginationPageThrifts = new ArrayList<PaginationPageThrift>();
				for (PaginationPag paginationPag : entityResponse.getPagination().getPaginationPags()) {
					PaginationPageThrift paginationPageThrift = new PaginationPageThrift();
					paginationPageThrift.setStartRow(paginationPag.getStartRow());
					paginationPageThrift.setPageSize(paginationPag.getPageSize());
					paginationPageThrifts.add(paginationPageThrift);
				}
				paginationThrift.setPaginationPageThrifts(paginationPageThrifts);
			}

			ReportRow reportRowHeaders = null;
			List<ReportRow> row = null;

			if (!filteringEntityRequest.isGenerateReport()) {
				reportRowHeaders = new ReportRow(0, entityResponse.getHeaders());
				row = getReportRows(entityResponse);
			}

			WorkReportEntityBucketsCompositeResponse compositeResponse =
					constructWorkReportEntityBucketsCompositeResponse(entityRequest, filteringEntityRequest.getReportingTypeRequest().getReportingReportType());

			return new ReportResponse(entityResponse.getFileName(), reportRowHeaders, row, compositeResponse, paginationThrift);



		}      */

	@Override
	public ReportingCriteria getCustomReportCriteria(Long reportKey) {
		if (reportKey == null) return null;
		return reportingCriteriaDAO.findByReportKey(reportKey);
	}

	@Override
	public WorkReportEntityBucketsCompositeResponse constructWorkReportEntityBucketsCompositeResponse(
			ReportRequestData entityRequestForReport,
			ReportingReportType reportingReportType) throws Exception {

		List<WorkReportEntityBucketResponse> bucketResponseList = new ArrayList<WorkReportEntityBucketResponse>();
		ReportingContext workReportingContext = (ReportingContext) getReportingContext().get(reportingReportType);
		Map<String, Entity> entities = workReportingContext.getEntities();
		WorkReportEntityBucketResponse bucketResponse = new WorkReportEntityBucketResponse();

		if (org.springframework.util.StringUtils.hasText(entityRequestForReport.getReportName())) {
			bucketResponse.setDisplayName(entityRequestForReport.getReportName());
		} else {
			bucketResponse.setDisplayName("Report Options");
		}
		bucketResponse.setKeyName("mockKeyName");
		List<FilteringEntityResponse> filteringEntityResponses = new ArrayList<FilteringEntityResponse>();

		Set<String> entityKeys = new HashSet<String>();
		if (entityRequestForReport.getReportFilterL() != null) {
			for (ReportFilter entityRequestForFiltering : entityRequestForReport.getReportFilterL()) {
				if (!entityRequestForFiltering.getFilteringType().equals(FilteringType.DISPLAY)) {
					if (entityKeys.add(entityRequestForFiltering.getProperty())) {
						Entity entity = entities.get(entityRequestForFiltering.getProperty());
						FilteringEntityResponse filteringEntityResponse = constructFilteringEntityResponse(entity, entityRequestForReport.getLocale(), workReportingContext);
						InputValues inputValues = constructInputValues(entityRequestForFiltering);
						filteringEntityResponse.setInputValues(inputValues);
						filteringEntityResponse.setDisplayName(entity.getDisplayNameM().get(Locale.ENGLISH));
						filteringEntityResponses.add(filteringEntityResponse);
					}
				}
			}
		}

		bucketResponse.setFilteringEntityResponses(filteringEntityResponses);
		bucketResponseList.add(bucketResponse);
		WorkReportEntityBucketsCompositeResponse entityBucketsCompositeResponse = new WorkReportEntityBucketsCompositeResponse();
		entityBucketsCompositeResponse.setWorkReportEntityBucketResponses(bucketResponseList);

		return entityBucketsCompositeResponse;
	}

	private InputValues constructInputValues(ReportFilter entityRequestForFiltering) throws Exception {
		InputValues inputValues = new InputValues();
		inputValues.setContains(entityRequestForFiltering.getContains());
		List<String> fieldValues = new ArrayList<String>();
		if(StringUtils.isNotEmpty(entityRequestForFiltering.getFieldValue())){
			fieldValues.addAll(Arrays.asList(entityRequestForFiltering.getFieldValue().split(",")));
		}
		inputValues.setFieldValue(fieldValues);
		if (entityRequestForFiltering.getFromValue() != null) {
			inputValues.setFromValue(entityRequestForFiltering.getFromValue().toString());
		} else if (entityRequestForFiltering.getInputFromValue() != null) {
			inputValues.setFromValue(entityRequestForFiltering.getInputFromValue());
		}
		if (entityRequestForFiltering.getToValue() != null) {
			inputValues.setToValue(entityRequestForFiltering.getToValue().toString());
		} else if (entityRequestForFiltering.getInputToValue() != null) {
			Calendar toCalendar = DateUtilities.getCalendarFromDateString(entityRequestForFiltering.getInputToValue(), Constants.WM_TIME_ZONE);
			/* An End Date of Nov 1 11:59:59 (EST) is stored as Nov 2 4:59:59 (UTC) on our system.
			 * This is why we need to subtract one day (from our stored end date) before we display to the end user.
			 */
			toCalendar.add(Calendar.DATE, -1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			inputValues.setToValue(simpleDateFormat.format(toCalendar.getTime()));
		}

		inputValues.setToOperator((RelationalOperatorThrift) getRelationalOperatorOptions().getKey(entityRequestForFiltering.getToOperator()));
		inputValues.setFromOperator((RelationalOperatorThrift) getRelationalOperatorOptions().getKey(entityRequestForFiltering.getFromOperator()));
		if (entityRequestForFiltering.getFilteringType() != null && entityRequestForFiltering.getFilteringType().getType().length() > 0)
			inputValues.setFilteringTypeThrift((FilteringTypeThrift) getWorkReportInputTypes().getKey(FilteringType.getInput(entityRequestForFiltering.getFilteringType().getType())));

		return inputValues;
	}

	private FilteringEntityResponse constructFilteringEntityResponse(Entity entity, ReportingContext context) {
		FilteringEntityResponse filteringEntityResponse = new FilteringEntityResponse();
		filteringEntityResponse.setToolTip(entity.getToolTip());
		FilterInputTag filterInputTag = entity.getFilterInputTag();
		HtmlTagTypeThrift htmlTagTypeThrift = (HtmlTagTypeThrift) getHtmlTagTypeOptions().getKey(filterInputTag.getHtmlTagType());
		filteringEntityResponse.setHtmlTagTypeThrift(htmlTagTypeThrift);

		filteringEntityResponse.setRelationalOperatorThrift((RelationalOperatorThrift) getRelationalOperatorOptions().getKey(filterInputTag.getRelationalOperator()));
		filteringEntityResponse.setRelationalOperatorThriftOptional((RelationalOperatorThrift) getRelationalOperatorOptions().getKey(filterInputTag.getRelationalOperatorOptional()));

		// For dynamic guys, execute the SQL and build the options list.
		// TODO Refactor into a more appropriate spot. Just getting shit working for now.
		if (filterInputTag instanceof DynamicFilterInputTag) {
			DynamicFilterInputTag dynamicTag = (DynamicFilterInputTag) filterInputTag;
			Assert.notNull(dynamicTag.getSelectOptionsSql(), "Must have a SQL query configured for the select options.");
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("companyId", context.getCompanyId());

			List<SelectOption> options = Lists.newArrayList(new SelectOption(PLEASE_SELECT, "Select to Filter"));
			logger.debug("[dynamicTag.getSelectOptionsSql()] " + dynamicTag.getSelectOptionsSql());
			options.addAll(jdbcTemplate.query(dynamicTag.getSelectOptionsSql(), params, new RowMapper<SelectOption>() {
				@Override
				public SelectOption mapRow(ResultSet rs, int rowNum) throws SQLException {
					SelectOption o = new SelectOption();
					o.setValue(rs.getString("key"));
					o.setLabel(rs.getString("value"));
					return o;
				}
			}));

			filterInputTag.setSelectOptions(options);
		}

		if (filterInputTag.getSelectOptions() != null && filterInputTag.getSelectOptions().size() > 0) {

			List<SelectOptionThrift> selectOptionThrifts = new ArrayList<SelectOptionThrift>();
			for (SelectOption selectOption : filterInputTag.getSelectOptions()) {
				SelectOptionThrift selectOptionThrift = new SelectOptionThrift();
				BeanUtils.copyProperties(selectOption, selectOptionThrift);
				selectOptionThrifts.add(selectOptionThrift);
			}

			filteringEntityResponse.setSelectOptionThrifts(selectOptionThrifts);
		}

		return filteringEntityResponse;
	}

	private ReportingCriteria saveReportingCriteria(ReportingCriteria reportingCriteria) throws Exception {
		getReportingCriteriaDAO().saveOrUpdate(reportingCriteria);
		return reportingCriteria;
	}

	private String getDelimitedString(Set<String> setS, String delimiter) {
		return StringUtils.join(setS, delimiter);
	}




	private List<ReportingCriteria> getReportingCriteriaByCompanyId(Long companyId) throws Exception {
		return getReportingCriteriaDAO().findByCompanyId(companyId);
	}

	private ReportingCriteria getReportingCriteriaById(Long id) throws Exception {
		return getReportingCriteriaDAO().get(id);
	}

	@Override
	public SavedCustomReportsCompositeResponse getCompanyCustomReports(ReportingTypesInitialRequest reportingTypesInitialRequest) throws WorkDisplayException, RuntimeException {
		try {
			Assert.notNull(reportingTypesInitialRequest, "reportingTypesInitialRequest can't be null");
			User user = getUserDAO().findUserByUserNumber(reportingTypesInitialRequest.getUserNumber(), false);
			Assert.notNull(user, "userNumber must be valid");
			Long companyId = user.getCompany().getId();
			Assert.notNull(companyId, "companyId must be valid");

			List<ReportingCriteria> reportingCriteria = getReportingCriteriaByCompanyId(companyId);
			List<SavedCustomReportResponse> savedCustomReportResponses = new ArrayList<SavedCustomReportResponse>();
			for (ReportingCriteria reportingCriterion : reportingCriteria) {
				SavedCustomReportResponse savedCustomReportResponse = new SavedCustomReportResponse();
				savedCustomReportResponse.setReportKey(reportingCriterion.getId());
				savedCustomReportResponse.setCreator((userService.findUserById(reportingCriterion.getCreatorId())).getFullName());
				savedCustomReportResponse.setReportName(reportingCriterion.getReportName());
				savedCustomReportResponses.add(savedCustomReportResponse);
			}
			setRecurrencesOnCustomReportResponseList(savedCustomReportResponses, findReportRecurrencesByCompanyId(companyId));

			SavedCustomReportsCompositeResponse compositeResponse = new SavedCustomReportsCompositeResponse();
			compositeResponse.setSavedCustomReportResponses(savedCustomReportResponses);

			return compositeResponse;

		} catch (WorkDisplayException wde) {
			logger.debug(wde.getMessages());
			throw wde;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void setRecurrencesOnCustomReportResponseList(List<SavedCustomReportResponse> responses, List<ReportRecurrence> recurrences) {
		if (CollectionUtils.isEmpty(responses) || CollectionUtils.isEmpty(recurrences)) return;

		for (final ReportRecurrence rr : recurrences) {
			SavedCustomReportResponse resp = Iterables.find(responses, new Predicate<SavedCustomReportResponse>() {
				@Override public boolean apply(SavedCustomReportResponse response) {
					return response.getReportKey() == rr.getReportKey();
				}
			}, null);
			if (resp != null)
				resp.setRecurrence(rr);
		}
	}


	@Override
	public SavedCustomReportResponse saveCustomReportType(FilteringEntityRequest filteringEntityRequest) throws WorkDisplayException, RuntimeException {
		try {
			Locale locale = new Locale(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getLocale());
			User user = getUserDAO().findUserByUserNumber(filteringEntityRequest.getReportingTypeRequest().getReportingTypesInitialRequest().getUserNumber(), false);
			Assert.notNull(user, "userNumber must be valid");

			ReportRequestData entityRequestForReport = extractReportRequestData(filteringEntityRequest, locale);
			ReportingCriteria reportingCriteria;
			boolean isCreate = filteringEntityRequest.getReportKey() == null || filteringEntityRequest.getReportKey().isEmpty();

			if (isCreate) {
				reportingCriteria = new ReportingCriteria();
			} else {
				reportingCriteria = getReportingCriteriaById(Long.valueOf(filteringEntityRequest.getReportKey()));
			}

			reportingCriteria.setDisplayKeys(getDelimitedString(entityRequestForReport.getDisplayKeys(), DISPLAY_KEYS_DELIMITER));
			reportingCriteria.setModifiedOn(Calendar.getInstance());
			reportingCriteria.setModifierId(user.getId());
			reportingCriteria.setReportName(filteringEntityRequest.getReportName());

			if (isCreate) {
				reportingCriteria.setCompany(user.getCompany());
				reportingCriteria.setCreatorId(user.getId());
				reportingCriteria.setCreatedOn(Calendar.getInstance());
				reportingCriteria.setCustomFieldsReport(filteringEntityRequest.hasCustomFields());
				if (reportingCriteria.getReportName() == null || reportingCriteria.getReportName().length() < 2) {
					throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have report name criteria."));
				}
			}

			if (entityRequestForReport.getReportFilterL() != null) {
				List<ReportingCriteriaFiltering> reportingCriteriaFilterings;
				if (isCreate) {
					reportingCriteriaFilterings = new ArrayList<ReportingCriteriaFiltering>();
					reportingCriteria.setReportingCriteriaFiltering(reportingCriteriaFilterings);
				} else {
					reportingCriteriaFilterings = reportingCriteria.getReportingCriteriaFiltering();
					// when saving existing report, first mark all filters/display fields deleted
					for (ReportingCriteriaFiltering reportingCriteriaFiltering : reportingCriteriaFilterings) {
						reportingCriteriaFiltering.setDeleted(true);
					}
				}
				for (ReportFilter entityRequestForFiltering : entityRequestForReport.getReportFilterL()) {
					ReportingCriteriaFiltering filtering = null;
					if (!isCreate) {
						// if the filter/display field was already in the existing report, mark it as active again
						for (ReportingCriteriaFiltering reportingCriteriaFiltering : reportingCriteriaFilterings) {
							if (reportingCriteriaFiltering.getProperty().equals(entityRequestForFiltering.getProperty()) &&
								reportingCriteriaFiltering.getFilteringType().equals(entityRequestForFiltering.getFilteringType())) {
								reportingCriteriaFiltering.setDeleted(false);
								filtering = reportingCriteriaFiltering;
							}
						}
					}

					if (filtering == null) {
						filtering = new ReportingCriteriaFiltering();
						filtering.setCreatorId(user.getId());
						filtering.setCreatedOn(Calendar.getInstance());
						filtering.setProperty(entityRequestForFiltering.getProperty());
						if (entityRequestForFiltering.getFilteringType() != null) {
							filtering.setFilteringType(entityRequestForFiltering.getFilteringType().getType());
						}
						reportingCriteriaFilterings.add(filtering);
						filtering.setReportingCriteria(reportingCriteria);
					}
					filtering.setFromDate(entityRequestForFiltering.getFromDate());
					filtering.setToDate(entityRequestForFiltering.getToDate());
					filtering.setFromValue(entityRequestForFiltering.getFromValue());
					filtering.setToValue(entityRequestForFiltering.getToValue());
					filtering.setContains(entityRequestForFiltering.getContains());
					filtering.setFieldValue(entityRequestForFiltering.getFieldValue());
					if (entityRequestForFiltering.getFromOperator() != null) {
						filtering.setFromOperator(entityRequestForFiltering.getFromOperator().getOperator());
					}
					if (entityRequestForFiltering.getToOperator() != null) {
						filtering.setToOperator(entityRequestForFiltering.getToOperator().getOperator());
					}
					if (entityRequestForFiltering.getFieldValueOperator() != null) {
						filtering.setFieldValueOperator(entityRequestForFiltering.getFieldValueOperator().getOperator());
					}
					filtering.setModifiedOn(Calendar.getInstance());
					filtering.setModifierId(user.getId());
				}
			}

			if (CollectionUtils.isNotEmpty(entityRequestForReport.getWorkCustomFieldIds())) {
				// report might contain only custom fields, and if so, the list doesn't exist yet
				if (reportingCriteria.getReportingCriteriaFiltering() == null) {
					reportingCriteria.setReportingCriteriaFiltering(new ArrayList<ReportingCriteriaFiltering>());
				}
				for (Long workCustomFieldId : entityRequestForReport.getWorkCustomFieldIds()) {
					ReportingCriteriaFiltering filtering = null;
					for (ReportingCriteriaFiltering reportingCriteriaFiltering : reportingCriteria.getReportingCriteriaFiltering()) {
						if (reportingCriteriaFiltering.getProperty().equals((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELD_ID)) &&
							reportingCriteriaFiltering.getFieldValue().equals(workCustomFieldId.toString()))
						{
							reportingCriteriaFiltering.setDeleted(false);
							filtering = reportingCriteriaFiltering;
						}
					}
					if (filtering == null) {
						filtering = new ReportingCriteriaFiltering();
						filtering.setCreatorId(user.getId());
						filtering.setCreatedOn(Calendar.getInstance());
						filtering.setProperty((String) AbstractReportFilterBuilder.workReportColumnTypes.get(WorkReportColumnType.WORK_CUSTOM_FIELD_ID));
						filtering.setReportingCriteria(reportingCriteria);
						reportingCriteria.getReportingCriteriaFiltering().add(filtering);
					}
					filtering.setFilteringType(FilteringType.FIELD_VALUE.getType());
					filtering.setFieldValue(workCustomFieldId.toString());
					filtering.setFieldValueOperator(RelationalOperator.EQUAL_TO.getOperator());
					filtering.setModifiedOn(Calendar.getInstance());
					filtering.setModifierId(user.getId());
				}
			}

			reportingCriteria = saveReportingCriteria(reportingCriteria);

			return new SavedCustomReportResponse()
					.setReportName(reportingCriteria.getReportName())
					.setReportKey(reportingCriteria.getId());

		} catch (WorkDisplayException wde) {
			logger.debug(wde.getMessages());
			throw wde;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public WorkReportEntityBucketsCompositeResponse getGenerateCustomReport(ReportingTypesInitialRequest reportingTypesInitialRequest, long reportKey) throws WorkDisplayException, RuntimeException {
		try {
			Assert.notNull(reportingTypesInitialRequest, "reportingTypesInitialRequest can't be null");
			if (reportKey < 1)
				throw new RuntimeException("reportKey must be greater than 0:" + reportKey);

			// TODO: user isn't even used
			User user = getUserDAO().findUserByUserNumber(reportingTypesInitialRequest.getUserNumber(), false);
			Assert.notNull(user, "user can't be null");

			Locale locale = new Locale(reportingTypesInitialRequest.getLocale());
			ReportRequestData entityRequestForReport = extractReportRequestData(reportKey, locale, user.getCompany());

			Map<String, ReportFilter> entityRequestForFilteringMap = new HashMap<String, ReportFilter>();
			Map<String, ReportFilter> displayReportFilterMap = new HashMap<String, ReportFilter>();
			if (entityRequestForReport.getReportFilterL() != null) {
				for (ReportFilter entityRequestForFiltering : entityRequestForReport.getReportFilterL()) {
					if (!entityRequestForFiltering.getFilteringType().equals(FilteringType.DISPLAY))
						entityRequestForFilteringMap.put(entityRequestForFiltering.getProperty(), entityRequestForFiltering);
					else
						displayReportFilterMap.put(entityRequestForFiltering.getProperty(), entityRequestForFiltering);
				}
			}

			ReportingContext workReportingContext = (ReportingContext) getReportingContext().get(ReportingReportType.WORK_ASSIGNMENTS);
			workReportingContext.setCompanyId(reportingTypesInitialRequest.getCompanyId());

			List<WorkReportEntityBucketResponse> workReportEntityBucketResponses =
					constructWorkReportEntityBucketResponses(workReportingContext, locale, entityRequestForFilteringMap, displayReportFilterMap);

			WorkReportEntityBucketsCompositeResponse workReportEntityBucketsResponse = new WorkReportEntityBucketsCompositeResponse();
			workReportEntityBucketsResponse.setWorkReportEntityBucketResponses(workReportEntityBucketResponses);

			return workReportEntityBucketsResponse;

		} catch (WorkDisplayException wde) {
			logger.debug(wde.getMessages());
			throw wde;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public ReportRequestData extractReportRequestData(long reportKey, Locale locale, Company company) throws Exception {
		ReportingCriteria reportingCriteria = getReportingCriteriaById(reportKey);
		ReportRequestData entityRequestForReport = new ReportRequestData();
		entityRequestForReport.setReportName(reportingCriteria.getReportName());
		entityRequestForReport.setCompanyId(company.getId());

		if (reportingCriteria.getDisplayKeys() != null && reportingCriteria.getDisplayKeys().length() > 0) {
			Set<String> displayKeys = new HashSet<String>(Arrays.asList(reportingCriteria.getDisplayKeys().split(DISPLAY_KEYS_DELIMITER)));
			entityRequestForReport.setDisplayKeys(displayKeys);
		}
		entityRequestForReport.setLocale(locale);
		entityRequestForReport.setEntityKey(WORK_ENTITY_KEY);
		entityRequestForReport.setHasWorkCustomFields(reportingCriteria.isCustomFieldsReport());

		if (reportingCriteria.getReportingCriteriaFiltering() != null) {
			List<ReportFilter> filterings = new ArrayList<ReportFilter>();
			for (ReportingCriteriaFiltering reportingCriteriaFiltering : reportingCriteria.getReportingCriteriaFiltering()) {
				ReportFilter filter = new ReportFilter();
				filter.setContains(reportingCriteriaFiltering.getContains());
				filter.setFieldValue(reportingCriteriaFiltering.getFieldValue());
				filter.setFromDate(reportingCriteriaFiltering.getFromDate());
				filter.setFromValue(reportingCriteriaFiltering.getFromValue());
				filter.setProperty(reportingCriteriaFiltering.getProperty());
				filter.setToDate(reportingCriteriaFiltering.getToDate());
				filter.setToValue(reportingCriteriaFiltering.getToValue());
				if (reportingCriteriaFiltering.getFromDate() != null)
					filter.setInputFromValue(postSimpleDateformat.format(reportingCriteriaFiltering.getFromDate().getTime()));

				if (reportingCriteriaFiltering.getToDate() != null)
					filter.setInputToValue(postSimpleDateformat.format(reportingCriteriaFiltering.getToDate().getTime()));

				if (reportingCriteriaFiltering.getFieldValueOperator() != null)
					filter.setFieldValueOperator(RelationalOperator.getRelationalOperator(reportingCriteriaFiltering.getFieldValueOperator(), null));

				if (reportingCriteriaFiltering.getFilteringType() != null)
					filter.setFilteringType(FilteringType.getInput(reportingCriteriaFiltering.getFilteringType()));

				if (reportingCriteriaFiltering.getFromOperator() != null)
					filter.setFromOperator(RelationalOperator.getRelationalOperator(reportingCriteriaFiltering.getFromOperator(), null));

				if (reportingCriteriaFiltering.getToOperator() != null)
					filter.setToOperator(RelationalOperator.getRelationalOperator(reportingCriteriaFiltering.getToOperator(), null));

				filterings.add(filter);
			}

			entityRequestForReport.setReportFilterL(filterings);
		}
		return entityRequestForReport;
	}


	@Override public List<ReportingCriteria> findRecurringReportsByDateTime(DateTime date) {
		Assert.notNull(date);

		List<Long> reportIds = reportRecurrenceDAO.findReportIdsByRecurringDateTime(date);
		return reportingCriteriaDAO.findByReportKeys(reportIds);
	}

	@Override public Set<Email> findRecurringReportRecipientsByReportId(Long reportId) {
		Assert.notNull(reportId);

		return reportRecurrenceDAO.findRecurringReportRecipientsByReportId(reportId);
	}


	private List<ReportRow> getReportRows(EntityResponseForReport entityResponseForReport) {
		List<ReportRow> reportRows = new ArrayList<ReportRow>();

		if (entityResponseForReport.getRows() != null) {
			for (int i = 0; i < entityResponseForReport.getRows().size(); i++) {
				ReportRow reportRow = new ReportRow(i + 1, entityResponseForReport.getRows().get(i));
				reportRows.add(reportRow);
			}
		}
		return reportRows;
	}

	private ReportFilter createReportFilter(
			String columnValuesRequestKey[],
			HtmlTagTypeThrift htmlTagTypeThrift,
			String column,
			Map<String, String> nameValueMap,
			String columnValue,
			Locale locale) throws Exception {

		String parsedColumn;
		int index = column.indexOf('.');
		if (index > -1) {
			parsedColumn = column.substring(column.indexOf('.'));
			parsedColumn = parsedColumn.replace('.', '_');
		} else
			parsedColumn = "_" + column;

		ReportFilter entityRequestForFiltering = new ReportFilter();
		entityRequestForFiltering.setProperty(column);

		switch (htmlTagTypeThrift) {
			case DISPLAY: {
				entityRequestForFiltering.setFilteringType(FilteringType.DISPLAY);
				return entityRequestForFiltering;
			}
			case INPUT_TEXT: {
				if(StringUtils.isEmpty(columnValue)) return null;
				entityRequestForFiltering.setFilteringType(FilteringType.FIELD_VALUE);
				entityRequestForFiltering.setFieldValue(columnValue.trim());
				RelationalOperatorThrift relationalOperatorThrift = RelationalOperatorThrift.WORK_EQUAL_TO;
				entityRequestForFiltering.setFieldValueOperator((RelationalOperator) getRelationalOperatorOptions().get(relationalOperatorThrift));

				return entityRequestForFiltering;
			}
			case SELECT_OPTION: {
				entityRequestForFiltering.setFilteringType(FilteringType.FIELD_VALUE);
				if(columnValue.equals(PLEASE_SELECT)){
					return null;
				}
				entityRequestForFiltering.setFieldValue(columnValue);
				RelationalOperatorThrift relationalOperatorThrift = RelationalOperatorThrift.WORK_EQUAL_TO;
				entityRequestForFiltering.setFieldValueOperator((RelationalOperator) getRelationalOperatorOptions().get(relationalOperatorThrift));
				return entityRequestForFiltering;
			}
			case MULTI_SELECT_OPTION:{

				entityRequestForFiltering.setFilteringType(FilteringType.FIELD_VALUE);
				if(columnValue.equals(PLEASE_SELECT)){
					return null;
				}

				entityRequestForFiltering.setFieldValue(columnValue);
				RelationalOperatorThrift relationalOperatorThrift = RelationalOperatorThrift.WORK_EQUAL_TO;
				entityRequestForFiltering.setFieldValueOperator((RelationalOperator) getRelationalOperatorOptions().get(relationalOperatorThrift));
				return entityRequestForFiltering;

			}
			case DATE: {
				entityRequestForFiltering.setFilteringType(FilteringType.DATE_TIME);

				return entityRequestForFiltering;
			}
			case DATE_TIME: {
				entityRequestForFiltering.setFilteringType(FilteringType.DATE_TIME);

				return entityRequestForFiltering;
			}
			case TO_FROM_DATES: {
				entityRequestForFiltering.setFilteringType(FilteringType.DATE_RANGE);
				String selectFilter = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_from");
				FilteringTypeThrift filteringTypeThrift = FilteringTypeThrift.findByValue(Integer.valueOf(selectFilter));
				FilteringType filteringType = (FilteringType) getWorkReportInputTypes().get(filteringTypeThrift);
				if (filteringType.equals(FilteringType.PLEASE_SELECT))
					return null;
				entityRequestForFiltering.setFilteringType(filteringType);
				String inputFrom = null;
				String inputTo = null;
				if (FilteringTypeThrift.WORK_DATE_RANGE.equals(filteringTypeThrift)) {
					try {
						inputFrom = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + parsedColumn + "_from");
						inputTo = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + parsedColumn + "_to");
					} catch (Exception e) {
						List<String> messagesL = new ArrayList<String>();
						messagesL.add("Must have valid inputs and relational operators.");
						throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, messagesL);
					}
					Calendar fromCalendar;
					Calendar toCalendar;

					try {
						fromCalendar = DateUtilities.getMidnightRelativeToTimezone(DateUtilities.getCalendarFromDateString(inputFrom, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
						toCalendar = DateUtilities.getMidnightNextDayRelativeToTimezone(DateUtilities.getCalendarFromDateString(inputTo, Constants.WM_TIME_ZONE), Constants.EST_TIME_ZONE);
						toCalendar.add(Calendar.MILLISECOND, -1);

						entityRequestForFiltering.setFromDate(fromCalendar);
						entityRequestForFiltering.setInputFromValue(inputFrom);

						entityRequestForFiltering.setToDate(toCalendar);
						entityRequestForFiltering.setInputToValue(inputTo);
					} catch (Exception e) {
						throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid date(s)."));
					}
					entityRequestForFiltering.setFromOperator(RelationalOperator.GREATER_THAN_EQUAL_TO);
					entityRequestForFiltering.setToOperator(RelationalOperator.LESS_THAN_EQUAL_TO);
				}
				return entityRequestForFiltering;
			}
			case NUMERIC: {
				entityRequestForFiltering.setFilteringType(FilteringType.NUMERIC);// TODO verify NUMERIC_RANGE vs. NUMERIC in backend API.
				String inputFilter = null;
				String selectFilter = null;
				try {
					inputFilter = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + parsedColumn + "_filter");
					selectFilter = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_filter");
					logger.debug("inputFilter:" + inputFilter + " selectFilter:" + selectFilter);
				} catch (Exception e) {
					throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid inputs and relational operators."));
				}

				try {
					RelationalOperatorThrift relationalOperatorThriftFrom = RelationalOperatorThrift.findByValue(Integer.parseInt(selectFilter));
					if (relationalOperatorThriftFrom == null)
						throw new Exception("The relationalOperatorThriftFrom must not be null");

					if (relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT))
						return null;

					entityRequestForFiltering.setFromOperator((RelationalOperator) getRelationalOperatorOptions().get(relationalOperatorThriftFrom));
				} catch (Exception e) {
					throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid relational operators."));
				}
				entityRequestForFiltering.setFromValue(verifyBigDecimal("The input", inputFilter));

				return entityRequestForFiltering;
			}
			case NUMERIC_RANGE: {
				entityRequestForFiltering.setFilteringType(FilteringType.NUMERIC);// TODO verify NUMERIC_RANGE vs. NUMERIC in backend API.
				String inputFrom = null;
				String inputTo = null;
				String selectFrom = null;
				String selectTo = null;
				try {
					inputFrom = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + parsedColumn + "_from");
					inputTo = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + parsedColumn + "_to");
					selectFrom = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_from");
					selectTo = nameValueMap.get(columnValuesRequestKey[HTML_TAG_TYPE_THRIFT_INDEX] + "_" + columnValuesRequestKey[WORK_REPORT_COLUMN_TYPE_INDEX] + "_select_to");
					logger.debug("inputFrom:" + inputFrom + " selectFrom:" + selectFrom + " inputTo:" + inputTo + " selectTo:" + selectTo);
				} catch (Exception e) {
					throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid inputs and relational operators."));
				}
				try {
					RelationalOperatorThrift relationalOperatorThriftFrom = RelationalOperatorThrift.findByValue(Integer.parseInt(selectFrom));
					RelationalOperatorThrift relationalOperatorThriftTo = RelationalOperatorThrift.findByValue(Integer.parseInt(selectTo));
					if (relationalOperatorThriftFrom == null || relationalOperatorThriftTo == null)
						throw new WorkDisplayException();
					if (relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT) && relationalOperatorThriftTo.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT))
						return null;
					if(!relationalOperatorThriftFrom.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT)){
						entityRequestForFiltering.setFromOperator((RelationalOperator) relationalOperatorOptions.get(relationalOperatorThriftFrom));
						entityRequestForFiltering.setFromValue(verifyBigDecimal("The input", inputFrom));
					}

					if(!relationalOperatorThriftTo.equals(RelationalOperatorThrift.WORK_PLEASE_SELECT)){
						entityRequestForFiltering.setToOperator((RelationalOperator) relationalOperatorOptions.get(relationalOperatorThriftTo));
						entityRequestForFiltering.setToValue(verifyBigDecimal("The input", inputTo));
					}
				} catch (Exception e) {
					throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList("Must have valid relational operators."));
				}

				return entityRequestForFiltering;
			}
			default:
				return null;
		}
	}

	private String verifyString(String fieldName, String fieldValue) throws WorkDisplayException {
		if (fieldValue == null || fieldValue.trim().length() < 1) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList(fieldName + " must be populated."));
		}

		return fieldValue.trim();
	}

	private BigDecimal verifyBigDecimal(String fieldName, String fieldValue) throws WorkDisplayException {
		fieldValue = verifyString(fieldName, fieldValue);
		try {
			return new BigDecimal(fieldValue);
		} catch (Exception e) {
			throw new WorkDisplayException(WorkDisplayErrorType.VALIDATION_EXCEPTION, Lists.newArrayList(fieldName + " must be numeric."));
		}
	}

	public WorkReportGeneratorServiceImpl getAssignmentReportGeneratorServiceImpl() {
		return workReportGeneratorServiceImpl;
	}

	public void setAssignmentReportGeneratorServiceImpl(WorkReportGeneratorServiceImpl workReportGeneratorServiceImpl) {
		this.workReportGeneratorServiceImpl = workReportGeneratorServiceImpl;
	}

	public BidiMap getWorkReportColumnTypes() {
		return workReportColumnTypes;
	}

	public void setWorkReportColumnTypes(BidiMap workAssignmentEntities) {
		this.workReportColumnTypes = workAssignmentEntities;
	}

	public BidiMap getReportingContext() {
		return reportingContext;
	}

	public void setReportingContext(BidiMap reportingContext) {
		this.reportingContext = reportingContext;
	}

	/**
	 * @return the htmlTagTypeOptions
	 */
	public BidiMap getHtmlTagTypeOptions() {
		return htmlTagTypeOptions;
	}

	/**
	 * @param htmlTagTypeOptions the htmlTagTypeOptions to set
	 */
	public void setHtmlTagTypeOptions(BidiMap htmlTagTypeOptions) {
		this.htmlTagTypeOptions = htmlTagTypeOptions;
	}

	/**
	 * @return the relationalOperatorOptions
	 */
	public BidiMap getRelationalOperatorOptions() {
		return relationalOperatorOptions;
	}

	/**
	 * @param relationalOperatorOptions the relationalOperatorOptions to set
	 */
	public void setRelationalOperatorOptions(BidiMap relationalOperatorOptions) {
		this.relationalOperatorOptions = relationalOperatorOptions;
	}

	/**
	 * @return the workReportInputTypes
	 */
	public BidiMap getWorkReportInputTypes() {
		return workReportInputTypes;
	}

	/**
	 * @param workReportInputTypes the workReportInputTypes to set
	 */
	public void setWorkReportInputTypes(BidiMap workReportInputTypes) {
		this.workReportInputTypes = workReportInputTypes;
	}

	/**
	 * @return the workReportGeneratorServiceImpl
	 */
	public WorkReportGeneratorServiceImpl getWorkReportGeneratorServiceImpl() {
		return workReportGeneratorServiceImpl;
	}

	/**
	 * @param workReportGeneratorServiceImpl the workReportGeneratorServiceImpl to set
	 */
	public void setWorkReportGeneratorServiceImpl(WorkReportGeneratorServiceImpl workReportGeneratorServiceImpl) {
		this.workReportGeneratorServiceImpl = workReportGeneratorServiceImpl;
	}

	/**
	 * @return the userDAO
	 */
	public UserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO the userDAO to set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @return the reportingCriteriaDAO
	 */
	public ReportingCriteriasDAO getReportingCriteriaDAO() {
		return reportingCriteriaDAO;
	}

	/**
	 * @param reportingCriteriaDAO the reportingCriteriaDAO to set
	 */
	public void setReportingCriteriaDAO(ReportingCriteriasDAO reportingCriteriaDAO) {
		this.reportingCriteriaDAO = reportingCriteriaDAO;
	}

	public ReportRecurrenceDAO getReportRecurrenceDAO() {
		return reportRecurrenceDAO;
	}

	public void setReportRecurrenceDAO(ReportRecurrenceDAO reportRecurrenceDAO) {
		this.reportRecurrenceDAO = reportRecurrenceDAO;
	}
}
