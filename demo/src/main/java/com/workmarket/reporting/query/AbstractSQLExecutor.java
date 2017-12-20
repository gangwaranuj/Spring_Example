package com.workmarket.reporting.query;

import com.google.common.base.Optional;
import com.workmarket.reporting.exception.ReportingFormatException;
import com.workmarket.utility.sql.SQLBuilder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by nick on 6/25/12 5:36 PM
 */
public abstract class AbstractSQLExecutor implements Serializable {

	protected NamedParameterJdbcTemplate jdbcTemplate;

	protected SQLBuilder sqlBuilder;
	protected String builtSql;
	protected RowMapper rowMapper;

	public AbstractSQLExecutor() {
	}


	public <T> Optional<List<T>> query() throws IOException, ReportingFormatException {
		return !isInitialized() ?
				Optional.<List<T>>absent() :
				Optional.of(jdbcTemplate.query(builtSql, sqlBuilder.getParams(), new BeanPropertyRowMapper<T>()));
	}

	public Optional<Integer> count() {
		return !isInitialized() ?
				Optional.<Integer>absent() :
				Optional.of(jdbcTemplate.queryForObject(builtSql, sqlBuilder.getParams(), Integer.class));
	}

	public void setSqlBuilder(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
		builtSql = sqlBuilder.build();
	}

	public void setRowMapper(RowMapper rowMapper) {
		this.rowMapper = rowMapper;
	}

	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isInitialized() {
		return !(sqlBuilder == null || jdbcTemplate == null);
	}
}
