package com.workmarket.domains.model.reporting;

import java.io.Serializable;
import org.hibernate.Criteria;

import com.workmarket.utility.sql.SQLBuilder;

import com.workmarket.reporting.mapping.RelationalOperator;


public class Filter implements Serializable {

	/**
	 * Instance variables and constants
	 */
	private Criteria criteria;
	private SQLBuilder sqlBuilder;
	private String property;
	private String dbFieldName;
	private String dbTable;
	private RelationalOperator equalNotEqualTo;
	private RelationalOperator fromRelationalOperator;
	private RelationalOperator toRelationalOperator;
	public static final String NEST_DELIMITER = ".";

	private static final long serialVersionUID = 2678697209977883919L;

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public RelationalOperator getFromRelationalOperator() {
		return fromRelationalOperator;
	}

	public void setFromRelationalOperator(RelationalOperator fromRelationalOperator) {
		this.fromRelationalOperator = fromRelationalOperator;
	}

	public RelationalOperator getToRelationalOperator() {
		return toRelationalOperator;
	}

	public void setToRelationalOperator(RelationalOperator toRelationalOperator) {
		this.toRelationalOperator = toRelationalOperator;
	}

	public RelationalOperator getEqualNotEqualTo() {
		return equalNotEqualTo;
	}

	public void setEqualNotEqualTo(RelationalOperator equalNotEqualTo) {
		this.equalNotEqualTo = equalNotEqualTo;
	}

	/**
	 * @return the sqlBuilder
	 */
	public SQLBuilder getSqlBuilder() {
		return sqlBuilder;
	}

	/**
	 * @param sqlBuilder the sqlBuilder to set
	 */
	public void setSqlBuilder(SQLBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}

	/**
	 * @return the dbFieldName
	 */
	public String getDbFieldName() {
		return dbFieldName;
	}

	/**
	 * @param dbFieldName the dbFieldName to set
	 */
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}

	/**
	 * @return the dbTable
	 */
	public String getDbTable() {
		return dbTable;
	}

	/**
	 * @param dbTable the dbTable to set
	 */
	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}
	
	//Convience method
	public String getDbTableAndField(){
		if(getDbTable() != null && getDbTable().length() > 0)
			return getDbTable() + "." + getDbFieldName();
		
		return getDbFieldName();
	}

}
