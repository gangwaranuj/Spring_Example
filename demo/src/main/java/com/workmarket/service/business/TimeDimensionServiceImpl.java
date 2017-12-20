package com.workmarket.service.business;

import com.workmarket.dao.summary.TimeDimensionDAO;
import com.workmarket.domains.model.summary.TimeDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;

/**
 * User: alexsilva Date: 3/10/14 Time: 6:31 PM
 */

@Service
public class TimeDimensionServiceImpl implements TimeDimensionService {

	@Autowired TimeDimensionDAO timeDimensionDAO;

	@Override
	public void saveOrUpdate(TimeDimension timeDimension) {
		timeDimensionDAO.saveOrUpdate(timeDimension);
	}

	@Override
	public Long findTimeDimensionId(Calendar calendar) {
		Assert.notNull(calendar);
		Integer correctedMonth = calendar.get(Calendar.MONTH) + 1;
		return timeDimensionDAO.findTimeDimensionId(correctedMonth , calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.HOUR_OF_DAY));

	}

	@Override
	public String getDateInStringById(long dateId) {
		return timeDimensionDAO.getDateInStringById(dateId);
	}
}
