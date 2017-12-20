package com.workmarket.reporting.format;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.workmarket.domains.model.reporting.GenericField;
import com.workmarket.configuration.Constants;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class DateFormat extends Format {
	public DateFormat(){
		simpleDateFormat = new SimpleDateFormat(Constants.CUSTOM_REPORTS_DATE_FORMAT, getLocale());
	}

	public DateFormat(String formatPattern){
		super(formatPattern);

		Assert.notNull(formatPattern, "formatPattern object can't be null");
		simpleDateFormat = new SimpleDateFormat(getFormatPattern(), getLocale());
	}

	public DateFormat(String formatPattern, Locale locale){
		super(formatPattern, locale);
		simpleDateFormat = new SimpleDateFormat(getFormatPattern(), getLocale());
	}

	SimpleDateFormat simpleDateFormat;

	private static final long serialVersionUID = 7850329438749984187L;

	/* (non-Javadoc)
	 * @see com.workmarket.reporting.format.Format#format(java.lang.Object, java.util.Locale)
	 */
	public String format(GenericField genericField, String timeZone) {
		Assert.notNull(genericField, "genericField object can't be null");
		String timeZoneId = StringUtils.isNotEmpty(timeZone) ? timeZone : Constants.WM_TIME_ZONE;
		Long millis = ((java.util.Calendar)genericField.getValue()).getTimeInMillis();
		return DateUtilities.formatMillis(Constants.CUSTOM_REPORTS_DATE_FORMAT, millis, timeZoneId);
	}

	public String format(GenericField genericField){
		Assert.notNull(genericField, "genericField object can't be null");
		return DateUtilities.formatMillis(Constants.CUSTOM_REPORTS_DATE_FORMAT, ((java.util.Calendar)genericField.getValue()).getTimeInMillis(), Constants.WM_TIME_ZONE);
	}

	/**
	 * @param <T>
	 * @param calendar
	 * @return
	 */
	public <T extends java.util.Calendar> String formatToString(T calendar){
		return simpleDateFormat.format(calendar.getTime());
	}

	/**
	 * @param calendar
	 * @return
	 */
	public String formatToString(java.util.GregorianCalendar calendar){
		return simpleDateFormat.format(calendar.getTime());
	}

}
