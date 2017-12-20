package com.workmarket.service.business;

import com.workmarket.domains.model.summary.TimeDimension;

import java.util.Calendar;

/**
 * User: alexsilva Date: 3/10/14 Time: 6:30 PM
 */
public interface TimeDimensionService {

	void saveOrUpdate(TimeDimension timeDimension);

	Long findTimeDimensionId(Calendar calendar);

	String getDateInStringById(long dateId);
}
