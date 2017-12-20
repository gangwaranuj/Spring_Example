package com.workmarket.reporting.query;


import org.hibernate.criterion.Restrictions;

import com.workmarket.domains.model.reporting.Filter;
import com.workmarket.reporting.util.ReportingUtil;

public abstract class AbstractFilter {

	/**
	 * @param <T>
	 * @param filter
	 * @throws Exception
	 */
	public abstract <T extends Filter> void filter(T filter) throws Exception;


	protected <T extends Filter> void filter(T filter, Object fromValue, Object toValue) throws Exception {

		if(filter.getCriteria() != null){
			//FilterCriteria
			filterCriteria(filter, fromValue, toValue);
		}else{
			//FilterSqlBuilder
			filterSqlBuilder(filter, fromValue, toValue);
		}
	}

	protected <T extends Filter> void filterCriteria(T filter, Object fromValue, Object toValue) throws Exception {

		String property = ReportingUtil.getRootProperty(filter.getProperty());
		//From
		if(fromValue != null){
			switch(filter.getFromRelationalOperator()){
				case EQUAL_TO:
					filter.getCriteria().add(Restrictions.eq(property, fromValue));
					break;
				case GREATER_THAN:
					filter.getCriteria().add(Restrictions.gt(property, fromValue));
					break;
				case NOT_EQUAL_TO:
					filter.getCriteria().add(Restrictions.ne(property, fromValue));
					break;
				case GREATER_THAN_EQUAL_TO:
					filter.getCriteria().add(Restrictions.ge(property, fromValue));
					break;
				case LESS_THAN:
					filter.getCriteria().add(Restrictions.lt(property, fromValue));
					break;
				case LESS_THAN_EQUAL_TO:
					filter.getCriteria().add(Restrictions.le(property, fromValue));
					break;
			}
		}

		//To
		if(toValue != null){
			switch(filter.getToRelationalOperator()){
				case EQUAL_TO:
					filter.getCriteria().add(Restrictions.eq(property, toValue));
					break;
				case GREATER_THAN:
					filter.getCriteria().add(Restrictions.gt(property, toValue));
					break;
				case NOT_EQUAL_TO:
					filter.getCriteria().add(Restrictions.ne(property, toValue));
					break;
				case GREATER_THAN_EQUAL_TO:
					filter.getCriteria().add(Restrictions.ge(property, toValue));
					break;
				case LESS_THAN:
					filter.getCriteria().add(Restrictions.lt(property, toValue));
					break;
				case LESS_THAN_EQUAL_TO:
					filter.getCriteria().add(Restrictions.le(property, toValue));
					break;
			}
		}
	}

	protected <T extends Filter> void filterSqlBuilder(T filter, Object fromValue, Object toValue) throws Exception{
		//From
		if(fromValue != null){
			switch(filter.getFromRelationalOperator()){
				case EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "=", filter.getProperty() + ".from", fromValue);
					break;
				case GREATER_THAN:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), ">", filter.getProperty() + ".from", fromValue);
					break;
				case NOT_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<>", filter.getProperty() + ".from", fromValue);
					break;
				case GREATER_THAN_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), ">=", filter.getProperty() + ".from", fromValue);
					break;
				case LESS_THAN:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<", filter.getProperty() + ".from", fromValue);
					break;
				case LESS_THAN_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<=", filter.getProperty() + ".from", fromValue);
					break;
			}
		}

		//To
		if(toValue != null){
			switch(filter.getToRelationalOperator()){
				case EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "=", filter.getProperty() + ".to", toValue);
					break;
				case GREATER_THAN:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), ">", filter.getProperty() + ".to", toValue);
					break;
				case NOT_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<>", filter.getProperty() + ".to", toValue);
					break;
				case GREATER_THAN_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), ">=", filter.getProperty() + ".to", toValue);
					break;
				case LESS_THAN:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<", filter.getProperty() + ".to", toValue);
					break;
				case LESS_THAN_EQUAL_TO:
					filter.getSqlBuilder().addWhereClause(filter.getDbTableAndField(), "<=", filter.getProperty() + ".to", toValue);
					break;
			}
		}
	}
}
