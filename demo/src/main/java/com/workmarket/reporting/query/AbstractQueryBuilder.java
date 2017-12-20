/**
 *
 */
package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.*;
import com.workmarket.reporting.mapping.FilteringType;
import com.workmarket.reporting.mapping.RelationalOperator;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

/**
 * @since 7/14/2011
 */
public abstract class AbstractQueryBuilder {

	@Autowired
	@Qualifier("readOnlyJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	private Integer startRow;
	private Integer pageSize;

	public final static Integer MAX_RESULTS = 10000;
	public final static String DESCENDING = "DESC";
	public final static String ASCENDING = "ASC";

	/**
	 * @param reportingContext
	 * @param entityRequestForReport
	 * @return
	 * @throws Exception
	 */

	public abstract SQLBuilder buildQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception;

	public abstract SQLBuilder buildSizeQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport);

	public abstract List<?> executeQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception;

	public abstract void addDefaultCriteria(SQLBuilder sqlBuilder);

	public abstract Integer executeQuerySize(ReportingContext reportingContext, ReportRequestData entityRequestForReport) throws Exception;

	protected void addWhereClauses(SQLBuilder sqlBuilder, List<ReportFilter> entityRequestForFilteringL,
	                               WorkReportingContext workReportingContext) {

		// Note, addWhereClauses are only for display?????
		for (ReportFilter entityRequestForFiltering : entityRequestForFilteringL) {
			Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());

			if (entity != null && entity.hasWhereClause()) {
				sqlBuilder.addWhereClause(entity.getWhereClause());
			}
		}
	}

	/**
	 * @param sortColumns
	 * @param sqlBuilder
	 */
	protected void addResponseSorts(List<OrderBy> sortColumns, SQLBuilder sqlBuilder) {
		for (OrderBy orderBy : sortColumns)
			sqlBuilder.addOrderBy(orderBy.getColumn(), orderBy.getDesc() ? DESCENDING : ASCENDING);
	}

	/**
	 * @param sqlBuilder
	 * @param tableName
	 */
	protected void addTable(SQLBuilder sqlBuilder, String tableName) {
		sqlBuilder.addTable(tableName);
	}

	/**
	 * @param sqlBuilder
	 * @param entityRequestForFilteringL
	 * @param workReportingContext
	 * @return
	 * @throws Exception
	 */
	protected Set<String> addTables(SQLBuilder sqlBuilder, List<ReportFilter> entityRequestForFilteringL,
	                                WorkReportingContext workReportingContext) throws Exception {

		Set<String> uniqueTableNames = new HashSet<String>();
		for (ReportFilter entityRequestForFiltering : entityRequestForFilteringL) {
			Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());
			if (uniqueTableNames.add(entity.getDbTable()) && entity.isSqlJoin()) {
				sqlBuilder.addTable(entity.getDbTable());
			}
		}
		return uniqueTableNames;
	}

	/**
	 * @param sqlBuilder
	 * @param entityRequestForFilteringL
	 * @param workReportingContext
	 * @throws Exception
	 */
	protected void addJoins(SQLBuilder sqlBuilder, List<ReportFilter> entityRequestForFilteringL,
	                        WorkReportingContext workReportingContext) {

		Set<String> dupsS = new HashSet<String>();
		List<SqlJoin> allJoinsL = new ArrayList<SqlJoin>();

		for (ReportFilter entityRequestForFiltering : entityRequestForFilteringL) {
			Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());
			if (entity != null /*&& entityRequestForFiltering.getFilteringType().equals(FilteringType.DISPLAY)*/ && entity.isSqlJoin()) {
				allJoinsL.addAll(entity.getSqlJoin());
			}
		}

		if (allJoinsL.size() > 0) {
			sortSqlJoin(allJoinsL);
			for (SqlJoin sqlJoin : allJoinsL) {
				String join = sqlJoin.getJoin();
				if (dupsS.add(join)) {
					sqlBuilder.addJoin(join);
				}
			}
		}
	}

	private void sortSqlJoin(List<SqlJoin> allJoinsL) {
		Collections.sort(allJoinsL);
	}

	/**
	 * @param sqlBuilder
	 * @param entityRequestForFilteringL
	 * @param workReportingContext
	 * @throws Exception
	 */
	protected void addColumns(SQLBuilder sqlBuilder, List<ReportFilter> entityRequestForFilteringL,
	                          WorkReportingContext workReportingContext) {

		Set<Entity> entities = new HashSet<Entity>();
		if (entityRequestForFilteringL != null) {
			for (ReportFilter entityRequestForFiltering : entityRequestForFilteringL) {
				Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());
				if (entity != null) {
					if (entityRequestForFiltering.getFilteringType().equals(FilteringType.DISPLAY)) {
						entities.add(entity);
					}
					if (entity.getReferencedEntities() != null && entity.getReferencedEntities().size() > 0) {
						entities.addAll(entity.getReferencedEntities());
					}
				}
			}
		}

		for (Entity entity : entities) {
			if (StringUtils.isNotBlank(entity.getFullColumnName())) {
				sqlBuilder.addColumn(entity.getFullColumnName());
			}
		}
	}

	/**
	 * @param entityRequestForFilteringL
	 * @param sqlBuilder
	 * @param workReportingContext
	 * @throws Exception
	 */
	protected void addResponseFilters(List<ReportFilter> entityRequestForFilteringL,
	                                  SQLBuilder sqlBuilder, WorkReportingContext workReportingContext) throws Exception {

		for (ReportFilter entityRequestForFiltering : entityRequestForFilteringL) {
			Entity entity = workReportingContext.getEntities().get(entityRequestForFiltering.getProperty());
			if (entity != null && entity.getAbstractFilter() != null) {
				Filter filter = populateAppropriateFilter(entityRequestForFiltering);
				if (filter != null) {
					filter.setSqlBuilder(sqlBuilder);
					filter.setDbTable(entity.getDbTable());
					filter.setProperty(entityRequestForFiltering.getProperty());
					if (entity.getAlternateDbFieldName() != null)
						filter.setDbFieldName(entity.getAlternateDbFieldName());
					else
						filter.setDbFieldName(entity.getDbFieldName());

					if (entityRequestForFiltering.getFilteringType().equals(FilteringType.FIELD_VALUE) && entity.getCustomSql() != null) {
						AbstractFilter abstractFilter = entity.getCustomSql().getFilter(entityRequestForFiltering.getFieldValue());
						abstractFilter.filter(filter);
					} else {
						if (filter.getEqualNotEqualTo() == null)
							filter.setEqualNotEqualTo(entityRequestForFiltering.getFieldValueOperator());
						if (filter.getFromRelationalOperator() == null)
							filter.setFromRelationalOperator(entityRequestForFiltering.getFromOperator());
						if (filter.getToRelationalOperator() == null)
							filter.setToRelationalOperator(entityRequestForFiltering.getToOperator());

						entity.getAbstractFilter().filter(filter);
					}
				}
			}
		}
	}

	/**
	 * @param <T>
	 * @param entityRequestForFiltering
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Filter> T populateAppropriateFilter(ReportFilter entityRequestForFiltering) {

		switch (entityRequestForFiltering.getFilteringType()) {
			case DISPLAY: {
				// Do nothing...
				return null;
			}
			case DATE_RANGE: {
				DateFilter dateFilter = new DateFilter();
				dateFilter.setFromDateC(entityRequestForFiltering.getFromDate());
				dateFilter.setToDateC(entityRequestForFiltering.getToDate());
				return (T) dateFilter;
			}
			case FIELD_VALUE:
				FieldValueFilter fieldValueFilter = new FieldValueFilter();
				fieldValueFilter.setFieldValue(entityRequestForFiltering.getFieldValue());
				return (T) fieldValueFilter;
			case NEXT_1_DAY: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightToday = DateUtilities.getMidnightToday();
				dateFilter.setFromDateC(midnightToday);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				// To
				Calendar nextNDaysCalendar = DateUtilities.nextNDaysMidnight(1);
				dateFilter.setToDateC(nextNDaysCalendar);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				return (T) dateFilter;
			}
			case NEXT_7_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightToday = DateUtilities.getMidnightToday();
				dateFilter.setFromDateC(midnightToday);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				// To
				Calendar nextNDaysCalendar = DateUtilities.nextNDaysMidnight(7);
				dateFilter.setToDateC(nextNDaysCalendar);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				return (T) dateFilter;
			}
			case NEXT_30_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightToday = DateUtilities.getMidnightToday();
				dateFilter.setFromDateC(midnightToday);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				// To
				Calendar nextNDaysCalendar = DateUtilities.nextNDaysMidnight(30);
				dateFilter.setToDateC(nextNDaysCalendar);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				return (T) dateFilter;
			}
			case NEXT_60_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightToday = DateUtilities.getMidnightToday();
				dateFilter.setFromDateC(midnightToday);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				// To
				Calendar nextNDaysCalendar = DateUtilities.nextNDaysMidnight(60);
				dateFilter.setToDateC(nextNDaysCalendar);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				return (T) dateFilter;
			}
			case NEXT_90_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightToday = DateUtilities.getMidnightToday();
				dateFilter.setFromDateC(midnightToday);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				// To
				Calendar nextNDaysCalendar = DateUtilities.nextNDaysMidnight(90);
				dateFilter.setToDateC(nextNDaysCalendar);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				return (T) dateFilter;
			}
			case LAST_1_DAY: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// To
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(1);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				return (T) dateFilter;
			}
			case LAST_7_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// To
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(7);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				return (T) dateFilter;
			}
			case LAST_30_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// To
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(30);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				return (T) dateFilter;
			}
			case LAST_60_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// To
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(60);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);
				return (T) dateFilter;
			}
			case LAST_90_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// To
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// From
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(90);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);

				return (T) dateFilter;
			}
			case LAST_365_DAYS: {
				DateFilter dateFilter = new DateFilter();
				// To
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);
				// From
				Calendar lastNDaysCalendar = DateUtilities.lastNDaysMidnight(365);
				dateFilter.setFromDateC(lastNDaysCalendar);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);

				return (T) dateFilter;
			}
			case THIS_YEAR_TO_DATE: {
				DateFilter dateFilter = new DateFilter();
				// From
				Calendar midnightYTD = DateUtilities.getMidnightYTD();
				dateFilter.setFromDateC(midnightYTD);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);

				// To
				Calendar now = DateUtilities.getCalendarNow();
				dateFilter.setToDateC(now);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);

				return (T) dateFilter;
			}
			case LAST_YEAR_ONLY: {
				DateFilter dateFilter = new DateFilter();
				// to
				Calendar midnightYTD = DateUtilities.getMidnightYTD();
				dateFilter.setToDateC(midnightYTD);
				dateFilter.setToRelationalOperator(RelationalOperator.LESS_THAN);

				// From
				Calendar midnightYTDAYearAgo = DateUtilities.getMidnightYTDAYearAgo();
				dateFilter.setFromDateC(midnightYTDAYearAgo);
				dateFilter.setFromRelationalOperator(RelationalOperator.GREATER_THAN);

				return (T) dateFilter;
			}
			case DATE_BEFORE: {
				DateFilter dateFilter = new DateFilter();
				// From none
				// To
				dateFilter.setToDateC(entityRequestForFiltering.getToDate());
				return (T) dateFilter;
			}
			case DATE_AFTER: {
				DateFilter dateFilter = new DateFilter();
				// From
				dateFilter.setFromDateC(entityRequestForFiltering.getFromDate());
				// To none
				return (T) dateFilter;
			}
			case NUMERIC:
				NumericFilter numericFilter = new NumericFilter();
				numericFilter.setFromValue(entityRequestForFiltering.getFromValue());
				numericFilter.setToValue(entityRequestForFiltering.getToValue());

				return (T) numericFilter;

			default:
				return null;
		}
	}

	/**
	 * @return the jdbcTemplate
	 */
	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	protected void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @return the startRow
	 */
	public Integer getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
