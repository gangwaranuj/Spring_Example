package com.workmarket.dao.requirement;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.requirementset.availability.Weekday;
import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WeekdayDAOImpl implements WeekdayDAO {
	private final List<WeekdayRequirable> types = Lists.newArrayList();
	private boolean loaded = false;

	@Override
	public List<WeekdayRequirable> findAll() {
		this.loadTypes();
		return this.types;
	}

	private void loadTypes() {
		if (this.loaded) {return;} // Should only load once
		for (Weekday type : Weekday.values()) {
			this.types.add(new WeekdayRequirable(type));
		}
		this.loaded = true;
	}
}
