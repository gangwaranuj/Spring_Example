package com.workmarket.domains.model.requirementset.availability;

import com.google.common.collect.Maps;
import org.apache.commons.lang.WordUtils;

import java.util.Map;

public enum Weekday {
	SUNDAY(0),
	MONDAY(1),
	TUESDAY(2),
	WEDNESDAY(3),
	THURSDAY(4),
	FRIDAY(5),
	SATURDAY(6);

	private final int id;

	private static final Map<Integer, Weekday> lookup = Maps.newHashMap();
	static {
		for (Weekday d : Weekday.values())
			lookup.put(d.getId(), d);
	}

	private Weekday(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getDescription() {
		return WordUtils.capitalizeFully(this.name());
	}

	public static Weekday getById(Integer dayOfWeek) {
		return lookup.get(dayOfWeek);
	}
}
