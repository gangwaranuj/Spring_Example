package com.workmarket.utility;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by nick on 12/17/14 2:37 PM
 */
public class SeoUtilities {

	public static String buildSEOFriendlyKeywords(String title, String... address) {
		if (StringUtils.isEmpty(title)) {
			return "";
		}
		String[] titleStr = StringUtils.split(title, " ");
		if (address != null && address.length > 0) {
			for (String addrsItem : address) {
				titleStr = (String[]) ArrayUtils.add(titleStr, addrsItem);
			}
		}
		if (titleStr.length == 0) {
			return "";
		}
		return StringUtils.join(titleStr, ", ");
	}
}
