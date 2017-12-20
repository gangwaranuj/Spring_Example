package com.workmarket.service.business;

import com.workmarket.dao.requirement.WeekdayDAO;
import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeekdayServiceImpl implements WeekdayService {
	@Autowired private WeekdayDAO dao;

	@Override
	public List<WeekdayRequirable> findAll() {
		return dao.findAll();
	}
}
