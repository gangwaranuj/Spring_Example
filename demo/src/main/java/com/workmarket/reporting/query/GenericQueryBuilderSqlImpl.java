package com.workmarket.reporting.query;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.dao.customfield.WorkCustomFieldDAO;
import com.workmarket.data.report.work.CustomFieldReportFilters;
import com.workmarket.data.report.work.CustomFieldReportRow;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.reporting.*;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class GenericQueryBuilderSqlImpl extends AbstractQueryBuilder {

	@Autowired
	private WorkCustomFieldDAO workCustomFieldDAO;
	/*
	 * Instance variables and constant
	 */
	public final static String TABLE_NAME = "work";
	public final static String WORK_WORK_ID = "work_work_id";
	private java.util.Locale locale = new java.util.Locale("en");

	private static final Log logger = LogFactory.getLog(GenericQueryBuilderSqlImpl.class);

	@Override public SQLBuilder buildQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception {
		SQLBuilder sqlBuilder = new SQLBuilder();
		sqlBuilder.setDistinct(true);

		if (entityRequestForReport.getPaginationPag() != null) {
			sqlBuilder.setStartRow(entityRequestForReport.getPaginationPag().getStartRow());
			sqlBuilder.setPageSize(entityRequestForReport.getPaginationPag().getPageSize());
		}

		WorkReportingContext workReportingContext = (WorkReportingContext) reportingContext;
		addTable(sqlBuilder, TABLE_NAME);
		addDefaultCriteria(sqlBuilder);
		addWorkCriteria(sqlBuilder);
		sqlBuilder.addWhereClause("work.company_id=" + entityRequestForReport.getCompanyId());

		addWhereClauses(sqlBuilder, entityRequestForReport.getReportFilterL(), workReportingContext);
		addJoins(sqlBuilder, entityRequestForReport.getReportFilterL(), workReportingContext);
		addColumns(sqlBuilder, entityRequestForReport.getReportFilterL(), workReportingContext);
		addResponseFilters(entityRequestForReport.getReportFilterL(), sqlBuilder, workReportingContext);

		sqlBuilder.buildLimitClause();

		return sqlBuilder;
	}

	@Override public SQLBuilder buildSizeQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport)  {
		SQLBuilder sqlBuilder = new SQLBuilder();
		WorkReportingContext workReportingContext = (WorkReportingContext) reportingContext;

		addTable(sqlBuilder, TABLE_NAME);
		addDefaultCriteria(sqlBuilder);
		sqlBuilder.addWhereClause("work.company_id=" + entityRequestForReport.getCompanyId());
		addWhereClauses(sqlBuilder, entityRequestForReport.getReportFilterL(), workReportingContext);
		addJoins(sqlBuilder, entityRequestForReport.getReportFilterL(), workReportingContext);
		sqlBuilder.addColumn("count(DISTINCT work.id)");
		try{
			addResponseFilters(entityRequestForReport.getReportFilterL(), sqlBuilder, workReportingContext);
		}catch (Exception e){
			logger.error("Could not build sql " + e.getMessage());
			return null;
		}

		return sqlBuilder;
	}


	@Override
	public List<Map<String, GenericField>> executeQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception {

		SQLBuilder sqlBuilder = buildQuery(reportingContext, entityRequestForReport);

		String sql = sqlBuilder.build();
		String params = sqlBuilder.getParams().getValues().toString();
		logger.debug("sql::" + sql);
		logger.debug("params:" + params);

		List<Map<String, GenericField>> genericFieldL = getJdbcTemplate().query(sql, sqlBuilder.getParams(), new GenericRowMapper(reportingContext, entityRequestForReport));

		if (entityRequestForReport.getHasWorkCustomFields()) {
			List<Long> workIds = getWorkIds(genericFieldL);
			CustomFieldReportFilters filters = new CustomFieldReportFilters();
			filters.setWorkIds(workIds);
			filters.setVisibleToBuyer(true);
			filters.setWorkCustomFieldIds(entityRequestForReport.getWorkCustomFieldIds());
			Map<Long, List<CustomFieldReportRow>> customFieldsSavedValues = workCustomFieldDAO.getWorkCustomFieldsMap(null, entityRequestForReport.getCompanyId(), filters);
			executeCustomFieldsMap(customFieldsSavedValues, reportingContext, entityRequestForReport);
			augmentCustomFieldsMap(workIds, customFieldsSavedValues, entityRequestForReport.getWorkCustomFieldIds());
			merge(genericFieldL, customFieldsSavedValues);
		} else {
			Map<String, CustomFieldEntity> callM = new LinkedHashMap<String, CustomFieldEntity>();
			entityRequestForReport.setCallM(callM);
		}
		return genericFieldL;
	}

	/**
	 * Fill the custom fields map with missing work custom field data.
	 * @param workIds
	 * @param customFieldsSavedValues
	 * @param workCustomFieldIds
	 */
	private void augmentCustomFieldsMap(List<Long> workIds, Map<Long, List<CustomFieldReportRow>> customFieldsSavedValues, List<Long> workCustomFieldIds) {
		if (CollectionUtils.isEmpty(workCustomFieldIds)) {
			return; // No data to fill so return
		}

		// Need more data like 'name', so fetch it
		List<WorkCustomField> fields = workCustomFieldDAO.get(workCustomFieldIds);

		for (Long workId : workIds) {
			List<CustomFieldReportRow> rows = customFieldsSavedValues.get(workId);

			if (CollectionUtils.isEmpty(rows)) {
				rows = new ArrayList<>();
				customFieldsSavedValues.put(workId, rows);
			}

			for (WorkCustomField workCustomField : fields) {
				final Long fId = workCustomField.getId();
				CustomFieldReportRow existingRow = Iterables.find(rows, new Predicate<CustomFieldReportRow>() {
					@Override
					public boolean apply(CustomFieldReportRow customFieldReportRow) {
						if (customFieldReportRow.getFieldValue() != null &&
							customFieldReportRow.getFieldDefaultValue() != null &&
							customFieldReportRow.getFieldValue().contains(",") &&
							customFieldReportRow.getFieldDefaultValue().contains(",")) {
							customFieldReportRow.setFieldValue("");
						}
						return customFieldReportRow.getFieldId().equals(fId);
					}
				}, null);

				if (existingRow == null) {
					rows.add(createCustomFieldReportRowFromWorkCustomFieldWithEmptyValue(workCustomField));
				}
			}
		}
	}

	private CustomFieldReportRow createCustomFieldReportRowFromWorkCustomFieldWithEmptyValue(WorkCustomField workCustomField) {
		CustomFieldReportRow newRow = new CustomFieldReportRow();
		newRow.setFieldId(workCustomField.getId());
		newRow.setFieldName(workCustomField.getName());
		newRow.setFieldValue(""); // the value wasn't found so mark empty string
		return newRow;
	}

	private List<Long> getWorkIds(List<Map<String, GenericField>> genericFieldL) {
		List<Long> workIds = Lists.newArrayList();
		for(Map<String, GenericField> genericFieldsM : genericFieldL) {
			GenericField workWorkIdGenericField = genericFieldsM.get(WORK_WORK_ID);
			Long workId = (Long) workWorkIdGenericField.getValue();
			workIds.add(workId);
		}
		return  workIds;
	}

	private void merge(List<Map<String, GenericField>> genericFieldL, Map<Long, List<CustomFieldReportRow>> customFieldsMap) {
		for (Map<String, GenericField> genericFieldsM : genericFieldL) {
			GenericField workWorkIdGenericField = genericFieldsM.get(WORK_WORK_ID);
			Long workId = (Long) workWorkIdGenericField.getValue();

			// N number of CustomFieldReportRow's so what we have are n name/value pairs.
			List<CustomFieldReportRow> customFieldReportRowL = customFieldsMap.get(workId);
			if (customFieldReportRowL != null) {
				for(CustomFieldReportRow customFieldReportRow : customFieldReportRowL) {
					String fieldName = customFieldReportRow.getFieldName();
					genericFieldsM.put(fieldName, new GenericField(fieldName, customFieldReportRow.getFieldValue()));
				}
			}
		}
	}

	private void executeCustomFieldsMap(Map<Long, List<CustomFieldReportRow>> customFieldSavedValuesMap,  ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception {
		Map<String, CustomFieldEntity> callM = new LinkedHashMap<>();

		for (Map.Entry<Long, List<CustomFieldReportRow>> entry : customFieldSavedValuesMap.entrySet()) {
			for (CustomFieldReportRow customFieldReportRow : entry.getValue()) {
				if (customFieldReportRow.getFieldName() != null) {
					if (entityRequestForReport.getDisplayKeys().add(customFieldReportRow.getFieldName())) { // Adds the FieldName to the Set, if not already there.
						CustomFieldEntity customFieldEntity = (CustomFieldEntity) reportingContext.getEntities().get(CustomFieldEntity.WORK_CUSTOM_FIELDS);
						CustomFieldEntity customFieldEntity0 = (CustomFieldEntity) customFieldEntity.clone();
						customFieldEntity0.setDisplayNameM(CollectionUtilities.<Locale, String> newTypedObjectMap(locale, customFieldReportRow.getFieldName()));
						customFieldEntity0.setKeyName(customFieldReportRow.getFieldName());
						callM.put(customFieldReportRow.getFieldName(), customFieldEntity0);
					}
				}
			}
		}

		entityRequestForReport.setCallM(callM);
	}

	@Override
	public Integer executeQuerySize(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception {

		SQLBuilder sqlBuilder = buildSizeQuery(reportingContext, entityRequestForReport);
		String sql = sqlBuilder.build();
		return getJdbcTemplate().queryForObject(sql, sqlBuilder.getParams(), Integer.class);
	}

	public void addWorkCriteria(SQLBuilder sqlBuilder) {
		sqlBuilder.getOrderColumns().clear();
		sqlBuilder.addJoin("inner join time_zone on time_zone.id = work.time_zone_id");
		sqlBuilder.addColumn("time_zone.time_zone_id");
		sqlBuilder.addColumn("work.id as " + WORK_WORK_ID);
	}

	public void addDefaultCriteria(SQLBuilder sqlBuilder) {
		sqlBuilder.getOrderColumns().clear();
		sqlBuilder.addWhereClause("work.deleted=false");
		sqlBuilder.addWhereClause("work.type = 'W'");
	}
}
