package com.workmarket.service.business;

import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;

import java.util.List;

public interface WeekdayService {
	List<WeekdayRequirable> findAll();
}
