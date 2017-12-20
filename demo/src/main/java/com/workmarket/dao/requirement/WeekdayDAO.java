package com.workmarket.dao.requirement;

import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;

import java.util.List;

public interface WeekdayDAO {
	List<WeekdayRequirable> findAll();
}
