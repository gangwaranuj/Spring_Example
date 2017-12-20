package com.workmarket.search.response.work;

import com.google.common.collect.Maps;
import com.workmarket.thrift.EnumValue;

import java.util.Collections;
import java.util.Map;

import static com.workmarket.utility.StringUtilities.toPrettyName;

public enum WorkMilestoneFilter implements EnumValue {
	SCHEDULED_DATE(0) ,
	CREATED_DATE(1),
	SENT_DATE(2),
	COMPLETED_DATE(3),
	APPROVED_DATE(4),
	PAID_DATE(5),
	LAST_MODIFIED_DATE(6),
	INDEX_DATE(7);

	private final int val;
	
	private static final Map<Integer, String> filterMap = Maps.newHashMap();
	
	static {
		for(WorkMilestoneFilter dft : values()) {
			filterMap.put(dft.val, toPrettyName(dft.name()));
		}
	}
	
	private WorkMilestoneFilter(int val) {
		this.val = val;
	}
	
	public static WorkMilestoneFilter valueOf(final int val) {
		for(WorkMilestoneFilter dft : values()) {
			if(val == dft.val) {
				return dft;
			}
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
	
}
