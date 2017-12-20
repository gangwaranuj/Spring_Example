package com.workmarket.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: iloveopt
 * Date: 5/7/14
 */

public class AnalyticsUtilities {

	private static final Log logger = LogFactory.getLog(AnalyticsUtilities.class);

	public static String scoreCardValueDisplay(Double value) {
		if (value.compareTo(1.0) == 0) {
			return "100" ;
		}

		if (value.compareTo(1.0) == -1 && value.compareTo(0.0) == 1) {
			return String.format("%.1f", value * 100);
		}

		return "-" ;

	}

}
