package com.workmarket.service.business;

import com.workmarket.dao.company.CompanyAlertDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * User: iloveopt
 * Date: 10/3/13
 */

@Service
public class CompanyAlertServiceImpl implements CompanyAlertService {

	@Autowired CompanyAlertDAO companyAlertDao;

	@Override
	public boolean isLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId);
		return companyAlertDao.isLowBalanceAlertSentToday(companyId);
	}

	@Override
	public void setLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId);
		companyAlertDao.setLowBalanceAlertSentToday(companyId);
	}

	@Override
	public void resetLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId);
		companyAlertDao.resetLowBalanceAlertSentToday(companyId);
	}

}
