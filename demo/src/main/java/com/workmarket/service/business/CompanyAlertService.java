package com.workmarket.service.business;

/**
 * User: iloveopt
 * Date: 10/3/13
 */

public interface CompanyAlertService {

	boolean isLowBalanceAlertSentToday(Long companyId);

	void setLowBalanceAlertSentToday(Long companyId);

	void resetLowBalanceAlertSentToday(Long companyId);
}
