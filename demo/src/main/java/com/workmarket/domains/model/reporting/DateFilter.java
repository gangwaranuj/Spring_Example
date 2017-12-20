/**
 * 
 */
package com.workmarket.domains.model.reporting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFilter extends Filter {

	/**
	 * Instance variables and constants
	 */
	private Calendar fromDateC;
	private Calendar toDateC;
	private String fromDate;
	private String toDate;
	private String dateFormat;

	public static final String DATE_FORMAT_MMddyyyy = "MM/dd/yyyy";
	private static final long serialVersionUID = -6119701761363382238L;


	
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Calendar getFromDateC()throws Exception{
		if(fromDateC == null){
			SimpleDateFormat simpleDateFormat = initDateFormat();
			Date fromDate = simpleDateFormat.parse(getFromDate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fromDate);
			fromDateC = calendar;
		}
		return fromDateC;
	}

	public void setFromDateC(Calendar fromDateC) {
		this.fromDateC = fromDateC;
	}

	public Calendar getToDateC() throws Exception{
		if(toDateC == null){
			SimpleDateFormat simpleDateFormat = initDateFormat();
			Date toDate = simpleDateFormat.parse(getToDate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(toDate);
			toDateC = calendar;
		}
		return toDateC;
	}

	public void setToDateC(Calendar toDateC) {
		this.toDateC = toDateC;
	}

	private SimpleDateFormat initDateFormat(){
		if(getDateFormat() == null || getDateFormat().length() < 1)
			setDateFormat(DATE_FORMAT_MMddyyyy);
		
		return new SimpleDateFormat(getDateFormat());
	}

	public String getRootProperty(){
		int index = getProperty().indexOf('.');
		if(index > 0)
			return getProperty().substring(index + 1, getProperty().length());
		else
			return getProperty();
	}

}
