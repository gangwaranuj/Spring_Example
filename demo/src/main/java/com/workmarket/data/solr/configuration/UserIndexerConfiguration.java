package com.workmarket.data.solr.configuration;

import com.workmarket.utility.DateUtilities;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;

/**
 * Author: rocio
 */
@Configuration
public class UserIndexerConfiguration {

	private static final int ON_TIME_PERCENTAGE_THRESHOLD_IN_MONTHS = 3;
	private static final int DELIVERABLE_ON_TIME_PERCENTAGE_THRESHOLD_IN_MONTHS = 3;
	private static final int LATE_LABELS_THRESHOLD_IN_MONTHS = 6;
	private static final int ABANDONED_LABELS_THRESHOLD_IN_MONTHS = 6;
	private static final int CANCELLED_LABELS_THRESHOLD_IN_MONTHS = 6;
	private static final int DELAYED_COMPLETION_LABELS_THRESHOLD_IN_MONTHS = 6;
	private static final int DISTINCT_BLOCKS_COUNT_THRESHOLD_IN_MONTHS = 24;
	private static final int REPEATED_CLIENTS_COUNT_THRESHOLD_IN_MONTHS = 6;

	public static final int RATING_AVERAGE_THRESHOLD_IN_MONTHS = 6;

	public static Calendar getOnTimePercentageThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(ON_TIME_PERCENTAGE_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getDeliverableOnTimePercentageThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(DELIVERABLE_ON_TIME_PERCENTAGE_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getWorkResourceLateLabelsThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(LATE_LABELS_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getWorkResourceAbandonedLabelsThresholdDate() {
		return  DateUtilities.getMidnightNMonthsAgo(ABANDONED_LABELS_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getWorkResourceCancelledLabelsThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(CANCELLED_LABELS_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getWorkResourceOnTimeCompletionLabelsThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(DELAYED_COMPLETION_LABELS_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getDistinctBlocksCountThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(DISTINCT_BLOCKS_COUNT_THRESHOLD_IN_MONTHS);
	}
	public static Calendar getRepeatedClientsThresholdDate() {
		return DateUtilities.getMidnightNMonthsAgo(REPEATED_CLIENTS_COUNT_THRESHOLD_IN_MONTHS);
	}
}
