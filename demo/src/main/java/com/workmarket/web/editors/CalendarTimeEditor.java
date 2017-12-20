package com.workmarket.web.editors;

import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;

import java.beans.PropertyEditorSupport;
import java.util.Calendar;

public class CalendarTimeEditor extends PropertyEditorSupport {
	private String timeZoneId;
	private String format = "h:mm aa";

	public CalendarTimeEditor(String timeZoneId) {
		super();
		this.timeZoneId = timeZoneId;
	}
	
	public CalendarTimeEditor(String timeZoneId, String format) {
		this(timeZoneId);
		this.format = format;
	}

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtilities.isNotEmpty(text)) {
			try {
				setValue(DateUtilities.getCalendarFromTimeString(text, timeZoneId));
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
