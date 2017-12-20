package com.workmarket.domains.model.reporting;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Required;

import java.util.List;


public class Column implements Serializable, Comparable<Column>  {

	/**
	 * Instance variables and constants
	 */
	private transient String dbFieldName;
	private String alternateDbFieldName;
	private transient String dbTable;
	private String fieldType;
	private List<? extends SqlJoin> sqlJoin;
	private String dbFieldNameAlias;
	private String joinClass;
	private String whereClause;
	private Integer orderBy = new Integer(60);

	private static final long serialVersionUID = -2114869369447190289L;
	/**
	 * @return the dbFieldName
	 */
	public String getDbFieldName() {
		return dbFieldName;
	}
	/**
	 * @param dbFieldName the dbFieldName to set
	 */
	@Required
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
	@Required
	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}
	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}
	/**
	 * @param fieldType the fieldType to set
	 */
	@Required
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * @return the sqlJoin
	 */
	public List<? extends SqlJoin> getSqlJoin() {
		return sqlJoin;
	}
	/**
	 * @param sqlJoin the sqlJoin to set
	 */
	public void setSqlJoin(List<? extends SqlJoin> sqlJoin) {
		this.sqlJoin = sqlJoin;
	}
	
	public String getDbFieldNameAlias() {
		if (dbFieldNameAlias != null) {
			return dbFieldNameAlias;
		}
		return dbFieldName;
	}
	
	public void setDbFieldNameAlias(String dbFieldNameAlias) {
		this.dbFieldNameAlias = dbFieldNameAlias;
	}
	
	/**
	 * @return the joinClass
	 */
	public String getJoinClass() {
		return joinClass;
	}

	/**
	 * @param joinClass the joinClass to set
	 */
	public void setJoinClass(String joinClass) {
		this.joinClass = joinClass;
	}

	/**
	 * @return the whereClause
	 */
	public String getWhereClause() {
		return whereClause;
	}

	/**
	 * @param whereClause the whereClause to set
	 */
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	/**
	 * @return the orderBy
	 */
	public Integer getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
	
	/**
	 * @return the alternateDbFieldName
	 */
	public String getAlternateDbFieldName() {
		return alternateDbFieldName;
	}
	/**
	 * @param alternateDbFieldName the alternateDbFieldName to set
	 */
	public void setAlternateDbFieldName(String alternateDbFieldName) {
		this.alternateDbFieldName = alternateDbFieldName;
	}
	@Override
	public int compareTo(Column cl) {
		if(cl.getOrderBy() > this.getOrderBy())
			return -1;
		if(cl.getOrderBy() < this.getOrderBy())
			return 1;

		return 0;
	}


}
