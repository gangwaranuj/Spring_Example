package com.workmarket.reporting.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.query.AbstractQueryBuilder;
import com.workmarket.reporting.query.AbstractSQLExecutor;
import com.workmarket.reporting.query.CSVRowBasedSQLExecutor;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.reporting.WorkReportGenerateEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Component
public class WorkReportGeneratorServiceImpl extends AbstractReportGeneratorService {

	@Autowired EventRouter eventRouter;
	@Autowired UserService userService;
	@Autowired CustomFieldService customFieldService;

	private static final Log logger = LogFactory.getLog(WorkReportGeneratorServiceImpl.class);

	public static final Integer MAX_REPORT_ROWS = 25000;

	private static Map<String, String> replacementMap = Maps.newHashMap();
	static {
		replacementMap.put("'", "''");
		replacementMap.put("\"", "\\\"");
	}

	@Value("${reporting.csv.file.location}")
	String fileLocation;

	AbstractSQLExecutor sqlExecutor = new CSVRowBasedSQLExecutor();

	public Optional<EntityResponseForReport> generate(ReportRequestData reportRequestData)  {

		final AbstractQueryBuilder queryBuilder = getAbstractQueryBuilder();

		List<ReportFilter> unescapedList = reportRequestData.getReportFilterL();
		List<ReportFilter> correctedList = escapeInvalidProperties(unescapedList);
		reportRequestData.setReportFilterL(correctedList);

		SQLBuilder countBuilder = queryBuilder.buildSizeQuery(getReportingContext(), reportRequestData);

		sqlExecutor.setSqlBuilder(countBuilder);
		sqlExecutor.setJdbcTemplate(getAbstractQueryBuilder().getJdbcTemplate());
		Optional<Integer> result = sqlExecutor.count();
		if (result == null || !result.isPresent()) {
			return Optional.absent();
		}

		Integer rows = result.get();
		EntityResponseForReport response;
		try{
			List<GenericField> genericFields = (List<GenericField>) getAbstractQueryBuilder().executeQuery(getReportingContext(), reportRequestData);
			List<WorkCustomField> workCustomFields = customFieldService.findWorkCustomFieldByIds(reportRequestData.getWorkCustomFieldIds());
			response = getReportHandler().generateReport(getReportingContext(), reportRequestData, genericFields, workCustomFields);
		} catch (Exception e) {
			logger.error("Could not generate response for custom reports due to exception " + e.getMessage());
			return Optional.absent();
		}

		reportRequestData.setReportFilterL(unescapedList);
		if (reportRequestData.getPaginationPag() == null){
			PaginationPag paginationPag = new PaginationPag();
			paginationPag.setStartRow(0);
			paginationPag.setPageSize(MAX_REPORT_ROWS);
			reportRequestData.setPaginationPag(paginationPag);
		} else if (reportRequestData.getPaginationPag().getStartRow() > rows) {
			int newStartRow = rows - reportRequestData.getPaginationPag().getPageSize();
			reportRequestData.getPaginationPag().setStartRow(newStartRow > 0 ? newStartRow : 0);
		}

		Pagination pagination = constructPagination(rows, reportRequestData.getPaginationPag().getPageSize());

		response.setPagination(pagination);
		response.setFileName("");
		return Optional.of(response);
	}

	public List<ReportFilter> escapeInvalidProperties(List<ReportFilter> list) {
		List<ReportFilter> newList = Lists.newArrayList();

		for(ReportFilter filter : list) {
			if (filter.getFilteringType().getType().equals("field_value")) {
				ReportFilter newFilter = new ReportFilter();
				BeanUtilities.copyProperties(newFilter, filter);
				String escapedField = escapeField(newFilter.getFieldValue());

				newFilter.setFieldValue(escapedField);
				newList.add(newFilter);
			} else {
				newList.add(filter);
			}
		}
		return newList;
	}

	private String escapeField(String field) {
		for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			field = field.replaceAll(key, value);
		}
		field = StringUtilities.stripXSSAndEscapeHtml(field);
		return field;
	}

	public void generateAsyncAdhocCustomReport(ReportRequestData reportRequestData){
		Assert.isTrue(StringUtils.isNotBlank(reportRequestData.getUserNumber()), "userNumber must be set");
		User user;
		if (reportRequestData.hasMasqueradeUserId()) {
			user = userService.findUserById(reportRequestData.getMasqueradeUserId());
		} else {
			user = userService.findUserByUserNumber(reportRequestData.getUserNumber());
		}
		Assert.notNull(user, "user must be valid");
		Assert.notNull(user.getEmail(), "user must have an email address to receive the report");

		// run the report asynchronously
		WorkReportGenerateEvent event = new WorkReportGenerateEvent(
				reportRequestData, fileLocation, Sets.newHashSet(user.getEmail()),null);
		event.setUser(userService.findUserByUserNumber(reportRequestData.getUserNumber()));
		eventRouter.sendEvent(event);


	}


	public void generateAsyncCustomReport(ReportRequestData reportRequestData,Long reportId){

		Assert.isTrue(StringUtils.isNotBlank(reportRequestData.getUserNumber()), "userNumber must be set");
		User user;
		if (reportRequestData.hasMasqueradeUserId()) {
			user = userService.findUserById(reportRequestData.getMasqueradeUserId());
		} else {
			user = userService.findUserByUserNumber(reportRequestData.getUserNumber());
		}
		Assert.notNull(user, "user must be valid");
		Assert.notNull(user.getEmail(), "user must have an email address to receive the report");

		PaginationPag paginationPag = new PaginationPag();
		paginationPag.setStartRow(0);
		paginationPag.setPageSize(MAX_REPORT_ROWS);
		reportRequestData.setPaginationPag(paginationPag);

		// run the report asynchronously
		WorkReportGenerateEvent event = new WorkReportGenerateEvent(
				reportRequestData, fileLocation, Sets.newHashSet(user.getEmail()),reportId);
		event.setUser(userService.findUserByUserNumber(reportRequestData.getUserNumber()));

		eventRouter.sendEvent(event);
	}

	public String getReportContextName() {
		return getClass().getName();
	}

	public void setSqlExecutor(AbstractSQLExecutor sqlExecutor) {
		this.sqlExecutor = sqlExecutor;
	}
}
