package com.workmarket.dao.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.notification.CompanyAlert;
import com.workmarket.domains.model.notification.NotificationType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * User: iloveopt
 * Date: 10/3/13
 */

@Repository
public class CompanyAlertDAOImpl extends AbstractDAO<CompanyAlert> implements CompanyAlertDAO {

	protected Class<CompanyAlert> getEntityClass() {
		return CompanyAlert.class;
	}


	private CompanyAlert findCompanyAlertByCompanyId(Long companyId) {
		Assert.notNull(companyId);
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("companyId", companyId)).setMaxResults(1);

		return (CompanyAlert)criteria.uniqueResult();
	}


	@Override
	public boolean isLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId);
		CompanyAlert companyAlert = findCompanyAlertByCompanyId(companyId);
		return companyAlert != null && companyAlert.isSentToday();
	}

	@Override
	public void setLowBalanceAlertSentToday(Long companyId) {

		Assert.notNull(companyId);
		CompanyAlert companyAlert = findCompanyAlertByCompanyId(companyId);

		if(companyAlert == null) {
			companyAlert = new CompanyAlert();
			companyAlert.setCompanyId(companyId);
			companyAlert.setNotificationType(NotificationType.newNotificationType(NotificationType.MONEY_LOW_BALANCE));
		}

		companyAlert.setSentToday(true);
		saveOrUpdate(companyAlert);
	}

	@Override
	public void resetLowBalanceAlertSentToday(Long companyId) {
		Assert.notNull(companyId);
		CompanyAlert companyAlert = findCompanyAlertByCompanyId(companyId);

		if(companyAlert == null) {
			return;
		}
		companyAlert.setSentToday(false);
		saveOrUpdate(companyAlert);
	}

}
