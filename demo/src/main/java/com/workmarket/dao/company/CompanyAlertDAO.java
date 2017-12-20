package com.workmarket.dao.company;

/**
 * User: iloveopt
 * Date: 10/3/13
 */
public interface CompanyAlertDAO {

	boolean isLowBalanceAlertSentToday(Long companyId);

	void setLowBalanceAlertSentToday(Long companyId);

	void resetLowBalanceAlertSentToday(Long companyId);
}
