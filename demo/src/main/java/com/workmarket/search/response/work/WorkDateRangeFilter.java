package com.workmarket.search.response.work;

import com.google.common.collect.Maps;
import com.workmarket.domains.model.DateRange;
import com.workmarket.thrift.EnumValue;
import com.workmarket.utility.DateUtilities;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import static com.workmarket.utility.StringUtilities.toPrettyName;


public enum WorkDateRangeFilter implements EnumValue {
	CUSTOM_RANGE(7) {
		@Override
		public DateRange getFilteredDateRange(TimeZone timeZone) {
			throw new UnsupportedOperationException();
		}
	},
	TODAY(8) {
		@Override
		public DateRange getFilteredDateRange(TimeZone timeZone) {
			Calendar midnight = DateUtilities.getMidnightTodayRelativeToTimezone(timeZone);
			Calendar midnightTomorrow = DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone);
			return range(midnight, midnightTomorrow);
		}
	},
	LAST_7_DAYS(9) {
		@Override
		public DateRange getFilteredDateRange(TimeZone timeZone) {
			Calendar weekAgo = DateUtilities.getMidnightWeekAgo();
			Calendar midnightTomorrow = DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone);
			return range(weekAgo, midnightTomorrow);
		}
	},
	LAST_30_DAYS(10) {
		@Override
		public DateRange getFilteredDateRange(TimeZone timeZone) {
			Calendar monthAgo = DateUtilities.getMidnightMonthAgo();
			Calendar midnightTomorrow = DateUtilities.getMidnightTomorrowRelativeToTimezone(timeZone);
			return range(monthAgo, midnightTomorrow);
		}
	};
	
	private final int val;
	
	private static final Map<Integer, String> filterMap = Maps.newHashMap();
	
	static {
		for(WorkDateRangeFilter dsft : values()) {
			filterMap.put(dsft.val, toPrettyName(dsft.name()));
		}
	}
	
	private WorkDateRangeFilter(int val) {
		this.val = val;
	}
	
	public static WorkDateRangeFilter valueOf(final int val) {
		for(WorkDateRangeFilter dsft : values()) {
			if(val == dsft.val) return dsft;
		}
		return null;
	}
	
	@Override
	public int getValue() {
		return val;
	}
	
	public static final Map<Integer, String> getFilterMap() {
		return Collections.unmodifiableMap(filterMap);
	}
	
	public abstract DateRange getFilteredDateRange(TimeZone timeZone);
	
	protected DateRange range(Calendar from, Calendar to) {
		return new DateRange(from, to);
	}
}
