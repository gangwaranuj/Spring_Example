package com.workmarket.web.helpers;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 4/17/12
 * Time: 2:52 PM
 */
public class MobileHelper {

	/**
	 * Creates a jquery Mobile date picker
	 * @param name
	 * @param id
	 * @param style
	 * @param placeholder
	 * @return
	 */
	public static String getMobileDatePicker(String name, String id, String style, String placeholder) {
		return String.format("<input name=\"%s\" id=\"%s\" type=\"date\" placeholder=\"%s\" data-role=\"datebox\" " +
				"data-options='{" +
				"\"mode\":\"%s\"," +
				"\"overrideDateFormat\":\"%%m/%%d/%%Y\"," +
				"\"noButtonFocusMode\":true," +
				"\"disableManualInput\":true," +
				"\"afterToday\":true, " +
				"\"useNewStyle\":true}'/>",
				name, id, placeholder, style);
	}

	public static String getMobileTimePicker(String name, String id, String selected, Integer intervalMin) {

		intervalMin = (intervalMin == null) ? 15 : intervalMin;
		List<Pair<String, String>> times = Lists.newArrayList();

		LocalTime base = new LocalTime(0, 0);
		if (StringUtils.isBlank(selected))
			selected = base.toString("hh:mmaa");

		LocalTime dt;
		for (int i = 0; i < DateTimeConstants.MINUTES_PER_DAY / intervalMin; i++) {
			dt = base.plus(Minutes.minutes(i * intervalMin));
			times.add(new ImmutablePair<String, String>(dt.toString("hh:mmaa"), dt.toString("hh:mm a")));
		}
		StringBuilder result = new StringBuilder();
		result.append(String.format("<select name='%s' id='%s'>\n", name, id));
		for (Pair<String, String> time : times) {
			result.append(String.format("<option value='%s'%s>%s</option>\n",
					time.getLeft(),
					selected.equals(time.getLeft()) ? " selected='selected'" : "",
					time.getRight()));
		}
		result.append("</select>");
		return result.toString();
	}

	public static void main(String[] args) {
		System.out.println(getMobileDatePicker("testName", "testId", "calbox", "MM/DD/YYYY"));
		System.out.println(getMobileTimePicker("testName", "testId", "09:15", 15));
	}
}


