package com.workmarket.web.editors;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 5/10/12
 * Time: 1:22 AM
 */

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component("dateOrTimeEditor")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateOrTimeEditor extends PropertyEditorSupport{
	private String datePattern = "MM/dd/yyyy";
	private String timePattern = "hh:mmaa";


	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}


	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}


	protected DateFormat getDateFormatter() {
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getInstance();
		sdf.applyPattern(datePattern);
		return sdf;
	}


	protected DateFormat getTimeFormatter() {
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getInstance();
		sdf.applyPattern(timePattern);
		return sdf;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Date value = null;

		if (StringUtils.isNotBlank(text)) {
			try {
				DateFormat dateFormat = getDateFormatter();
				value = dateFormat.parse(text);
			} catch (ParseException ex) {
				DateFormat timeFormat = getTimeFormatter();
				try {
					value = timeFormat.parse(text.toUpperCase());
				} catch (ParseException e) {
					throw new IllegalArgumentException(text, ex);
				}
			}
		}

		setValue(value);
	}


	@Override
	public String getAsText() {
		Date date = (Date) getValue();

		if (date == null) {
			return "";
		} else {
			DateFormat dateFormat = getDateFormatter();
			String format = dateFormat.format(date);
			if (StringUtils.isBlank(format)) {
				DateFormat timeFormat = getTimeFormatter();
				return timeFormat.format(date);
			}
		}
		return "";
	}
}
