package com.workmarket.web.helpers;

import com.google.common.collect.Maps;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * User: micah
 * Date: 9/21/13
 * Time: 12:05 AM
 */
public class AutotaskWorkFormHelper {
	private static final Log logger = LogFactory.getLog(AutotaskWorkFormHelper.class);

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm aa");

	public static Map<String, Date> getFullDateParts(String full) {
		Map<String, Date> retMap = Maps.newHashMap();
		try {
			int spaceIdx = full.indexOf(" ");
			String datePart = full.substring(0, spaceIdx);
			String timePart = full.substring(spaceIdx+1);
			retMap.put("date", DATE_FORMAT.parse(datePart));
			retMap.put("time", TIME_FORMAT.parse(timePart));
		} catch (Exception e) {
			logger.error(String.format("Problem with passed in date: %s", full), e);
			Date now = new Date();
			retMap.put("date", now);
			retMap.put("time", now);
		}
		return retMap;
	}
}
