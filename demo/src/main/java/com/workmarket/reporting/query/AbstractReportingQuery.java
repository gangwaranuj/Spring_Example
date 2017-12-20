/**
 * 
 */
package com.workmarket.reporting.query;

import com.workmarket.domains.model.reporting.ReportRequestData;
import com.workmarket.domains.model.reporting.GenericField;
import com.workmarket.domains.model.reporting.ReportingContext;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.sql.ResultSet;

/**
 * @since 7/13/2011
 *
 */
public abstract class AbstractReportingQuery {
	
	public static final String TIME_ZONE_ID = "time_zone.time_zone_id";
	
	AbstractReportingQuery(){
	}

	/**
	 * @param reportingContext
	 * @param entityRequestForReport
	 */
	AbstractReportingQuery(ReportingContext reportingContext, ReportRequestData entityRequestForReport){
		this.reportingContext = reportingContext;
		this.entityRequestForReport = entityRequestForReport;		
	}

	/*
	 * Instance variables and constants
	 */
	private ReportingContext reportingContext;
	private ReportRequestData entityRequestForReport;
	private PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
	
	
	/**
	 * @return the reportingContext
	 */
	protected ReportingContext getReportingContext() {
		return reportingContext;
	}
	
	/**
	 * @return the entityRequestForReport
	 */
	protected ReportRequestData getReportRequestData() {
		return entityRequestForReport;
	}
	
	/**
	 * @return the propertyUtilsBean
	 */
	protected PropertyUtilsBean getPropertyUtilsBean() {
		return propertyUtilsBean;
	}

	/**
	 * @param fieldType
	 * @param dbFieldName
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	protected Object getValue(String fieldType, String dbFieldName, ResultSet rs)throws Exception{
//When there is time, make an enum
		if(fieldType.startsWith("int"))
			return rs.getLong(dbFieldName);
		else if(fieldType.equals("text") || fieldType.startsWith("varchar"))
			return rs.getString(dbFieldName);
		else if(fieldType.equals("datetime"))
			return DateUtilities.getCalendarFromDate(rs.getTimestamp(dbFieldName));
		else if(fieldType.startsWith("decimal"))
			return rs.getBigDecimal(dbFieldName);
		else if(fieldType.startsWith("tinyint"))
			return rs.getBoolean(dbFieldName);

		return null;
	}
	
	protected GenericField populateTimeZone(ResultSet resultSet)throws Exception{
		return new GenericField(TIME_ZONE_ID, getValue("varchar(100)", TIME_ZONE_ID, resultSet));
	}

}
