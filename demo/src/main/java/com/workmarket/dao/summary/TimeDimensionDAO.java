package com.workmarket.dao.summary;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.summary.TimeDimension;

import java.util.Calendar;
import java.util.List;

public interface TimeDimensionDAO extends DAOInterface<TimeDimension> {

	Long findTimeDimensionId(Integer month, Integer year, Integer day, Integer hour);

	TimeDimension findTimeDimension(Integer month, Integer year, Integer day, Integer hour);

	List<Calendar> findMonthsBetweenDates(Calendar fromDate, Calendar toDate);

	String getDateInStringById(long dateId);
}
