package com.workmarket.web.editors;

import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;

import java.beans.PropertyEditorSupport;
import java.util.Calendar;

public class CalendarDateEditor extends PropertyEditorSupport {
    private String timeZoneId;
	private String format = "MM/dd/yyyy";

    public CalendarDateEditor(String timeZoneId) {
		super();
        this.timeZoneId = timeZoneId;
    }
	
	public CalendarDateEditor(String timeZoneId, String format) {
		this(timeZoneId);
		this.format = format;
	}

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtilities.isNotEmpty(text)) {
			try {
				setValue(DateUtilities.getCalendarFromDateString(text, timeZoneId));
			} catch (Exception ex) {
				throw new IllegalArgumentException(ex);
			}
		}
		else {
			setValue(null);
		}
    }

    @Override
    public String getAsText() {
        Calendar calendar = (Calendar)getValue();
        return (calendar == null) ? null : DateUtilities.format(format, calendar, timeZoneId);
    }
}
