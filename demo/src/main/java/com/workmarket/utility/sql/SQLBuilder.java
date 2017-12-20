package com.workmarket.utility.sql;

import com.google.common.collect.Lists;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Arrays;
import java.util.List;

public class SQLBuilder {
	private List<String> columns = Lists.newArrayList();
	private List<String> tables = Lists.newArrayList();
	private List<String> joins = Lists.newArrayList();
	private List<String> joinsForCount = Lists.newArrayList();
	private List<String> whereClauses = Lists.newArrayList();
	private List<String> groupColumns = Lists.newArrayList();
	private List<String> orderColumns = Lists.newArrayList();
	private Integer startRow;
	private Integer pageSize;
	private MapSqlParameterSource params = new MapSqlParameterSource();
	private List<String> having = Lists.newArrayList();
	private boolean distinct = false;

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public List<String> getJoins() {
		return joins;
	}

	public void setJoins(List<String> joins) {
		this.joins = joins;
	}

	public List<String> getWhereClauses() {
		return whereClauses;
	}

	public void setWhereClauses(List<String> whereClauses) {
		this.whereClauses = whereClauses;
	}

	public List<String> getGroupColumns() {
		return groupColumns;
	}

	public void setGroupColumns(List<String> groupColumns) {
		this.groupColumns = groupColumns;
	}

	public List<String> getOrderColumns() {
		return orderColumns;
	}

	public void setOrderColumns(List<String> orderColumns) {
		this.orderColumns = orderColumns;
	}

	public String buildProjectionClause() {
		if (distinct) {
			return " SELECT DISTINCT " + StringUtils.join(getColumns(), ", ") + " \n";
		}
		return " SELECT " + StringUtils.join(getColumns(), ", ") + " \n";
	}

	public String buildLimitClause() {
		if (startRow != null && pageSize != null) {
			return " LIMIT " + startRow + ", " + pageSize;
		}
		return StringUtils.EMPTY;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public SQLBuilder setStartRow(Integer startRow) {
		this.startRow = startRow;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public SQLBuilder setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	private String buildFromClause() {
		return " FROM " + StringUtils.join(getTables(), ", ") + " \n";
	}

	private String buildJoinClause() {
		return StringUtils.join(getJoins(), " \n");
	}

	private String buildJoinClauseForCount() {
		return StringUtils.join(getJoinsForCount(), " \n");
	}

	private String buildWhereClause() {
		if (getWhereClauses().size() > 0)
			return " WHERE 1=1 AND " + StringUtils.join(getWhereClauses(), " AND ") + " \n";
		else
			return " WHERE 1=1 ";
	}

	private String buildSortClause() {
		StringBuilder sb = new StringBuilder();
		if (getOrderColumns().size() > 0) {
			sb.append(" ORDER BY ").append(StringUtils.join(getOrderColumns(), " , "));
		}
		return sb.toString();
	}

	private String buildGroupByClause() {
		if (getGroupColumns().size() > 0)
			return " GROUP BY " + StringUtils.join(getGroupColumns(), ", ");
		else
			return StringUtils.EMPTY;
	}

	private String buildHavingClause() {
		StringBuilder sb = new StringBuilder();
		if (getHaving().size() > 0) {
			sb.append(" HAVING ");
			sb.append(StringUtils.join(getHaving(), " AND "));
		}
		return sb.toString();
	}

	public String build() {
		return buildProjectionClause() + buildFromClause() + buildJoinClause() + buildWhereClause() + buildGroupByClause() + buildHavingClause() + buildSortClause() + buildLimitClause();
	}

	public String buildCount(String countField) {
		StringBuilder count = new StringBuilder()
				.append(" SELECT count( ")
				.append(countField)
				.append(") AS count ")
				.append(buildFromClause())
				.append(buildJoinClauseForCount())
				.append(buildWhereClause())
				.append(buildGroupByClause())
				.append(buildHavingClause());

		if (StringUtils.isNotBlank(buildGroupByClause())) {
			count.append(" ORDER BY NULL \n");
		}

		return count.toString();
	}

	public String buildCount() {
		StringBuilder count = new StringBuilder().append(" SELECT count(*) FROM ( ")
				.append(buildProjectionClause())
				.append(buildFromClause())
				.append(buildJoinClauseForCount())
				.append(buildWhereClause())
				.append(buildGroupByClause())
				.append(buildHavingClause());

		if (StringUtils.isNotBlank(buildGroupByClause())) {
			count.append(" ORDER BY NULL ");
		}

		count.append(" ) AS results \n");

		return count.toString();
	}

	public String buildCountWithColumns(String countField) {
		StringBuilder count = new StringBuilder()
				.append(" SELECT count( ")
				.append(countField)
				.append(") AS count ");

		if (getColumns().size() > 0) {
			count.append(", ").append(StringUtils.join(getColumns(), ", "));
		}

		count
				.append(buildFromClause())
				.append(buildJoinClauseForCount())
				.append(buildWhereClause())
				.append(buildGroupByClause())
				.append(buildHavingClause());

		if (StringUtils.isNotBlank(buildGroupByClause())) {
			count.append(" ORDER BY NULL \n");
		}
		return count.toString();
	}

	public String buildCountFromSubSelectWithColumns(String... columns) {
		StringBuilder count = new StringBuilder().append(" SELECT count(*) AS count ");

		if (columns.length > 0)
			count.append(", ").append(StringUtils.join(columns, ", "));

		count
				.append(" FROM ( ")
				.append(buildProjectionClause())
				.append(buildFromClause())
				.append(buildJoinClauseForCount())
				.append(buildWhereClause())
				.append(buildGroupByClause())
				.append(buildHavingClause());

		if (StringUtils.isNotBlank(buildGroupByClause())) {
			count.append(" ORDER BY NULL ");
		}
		count.append(" ) AS results \n");

		return count.toString();
	}

	public MapSqlParameterSource getParams() {
		return params;
	}

	public void setParams(MapSqlParameterSource params) {
		this.params = params;
	}

	public void setHaving(List<String> having) {
		this.having = having;
	}

	public List<String> getHaving() {
		return having;
	}

	public SQLBuilder addTable(String t) {
		this.getTables().add(t);
		return this;
	}

	public SQLBuilder addParam(String paramName, Object value) {
		this.getParams().addValue(paramName, value);
		return this;
	}

	public SQLBuilder addColumn(String s) {
		this.getColumns().add(s);
		return this;
	}

	public SQLBuilder addColumns(String... s) {
		this.getColumns().addAll(Arrays.asList(s));
		return this;
	}

	public SQLBuilder addJoin(String j) {
		this.getJoins().add(j);
		this.getJoinsForCount().add(j);
		return this;
	}

	public SQLBuilder addJoin(String j, boolean includeInCount) {
		if (includeInCount) {
			return addJoin(j);
		}
		this.getJoins().add(j);
		return this;
	}

	public SQLBuilder addWhereClause(String w) {
		this.getWhereClauses().add(w);
		return this;
	}

	public SQLBuilder addWhereClause(String column, String operator, String paramName, Object value) {
		if (operator.equals(SQLOperator.IN)) {
			return addWhereInClause(column, paramName, value);
		}
		if (operator.equals(SQLOperator.LIKE) && value instanceof String) {
			value = StringUtilities.processForLike((String) value);
		}
		this.getWhereClauses().add(column + " " + operator + ":" + paramName);
		this.getParams().addValue(paramName, value);
		return this;
	}

	public SQLBuilder addWhereInClause(String column, String paramName, Object value) {
		this.getWhereClauses().add(column + " IN (:" + paramName + ")");
		this.getParams().addValue(paramName, value);
		return this;
	}

	public SQLBuilder addHavingClause(String h) {
		this.getHaving().add(h);
		return this;
	}

	public SQLBuilder addHavingClause(String column, String operator, String paramName, Object value) {
		this.getHaving().add(column + " " + operator + " :" + paramName);
		this.getParams().addValue(paramName, value);
		return this;
	}

	public SQLBuilder addGroupColumns(String... columns) {
		this.getGroupColumns().addAll(Lists.newArrayList(columns));
		return this;
	}

	public SQLBuilder addOrderBy(String column, String order) {
		this.getOrderColumns().add(column + " " + order);
		return this;
	}

	public SQLBuilder addDescOrderBy(String column) {
		this.getOrderColumns().add(column + " DESC");
		return this;
	}

	public SQLBuilder addAscOrderBy(String column) {
		this.getOrderColumns().add(column + " ASC");
		return this;
	}

	public SQLBuilder setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setJoinsForCount(List<String> joinsForCount) {
		this.joinsForCount = joinsForCount;
	}

	List<String> getJoinsForCount() {
		return joinsForCount;
	}

	public SQLBuilder addLimitClause(Integer startRow, Integer pageSize, boolean limitMaxRows) {
		this.setStartRow(startRow);
		if (limitMaxRows) {
			this.setPageSize(pageSize);
		}
		return this;
	}
}
