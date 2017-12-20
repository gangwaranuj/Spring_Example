package com.workmarket.dao.account.pricing;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.CompanyAccountPricingTypeChange;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public class CompanyAccountPricingTypeChangeDAOImpl extends AbstractDAO<CompanyAccountPricingTypeChange> implements CompanyAccountPricingTypeChangeDAO {

	protected Class<CompanyAccountPricingTypeChange> getEntityClass() {
		return CompanyAccountPricingTypeChange.class;
	}

	@Override
	public List<CompanyAccountPricingTypeChange> getCompanyAccountPricingTypeChangeScheduledBeforeDate(Calendar currentDate, boolean executed) {
		Criteria criteria = getFactory().getCurrentSession()
		        .createCriteria(getEntityClass())
		        .add(Restrictions.le("scheduledChangeDate", currentDate))
		        .add(Restrictions.eq("deleted", Boolean.FALSE));
		if (executed) {
			criteria.add(Restrictions.isNotNull("actualChangeDate"));
		} else {
			criteria.add(Restrictions.isNull("actualChangeDate"));
		}
		return criteria.list();
	}
	
	@Override 
	public CompanyAccountPricingTypeChange getCompanyAccountPricingTypeChange(long companyId, Calendar scheduleDate) {
		Criteria criteria = getFactory().getCurrentSession()
		        .createCriteria(getEntityClass())
		        .add(Restrictions.le("scheduledChangeDate", scheduleDate))
		        .add(Restrictions.eq("company.id", companyId))
		        .add(Restrictions.eq("deleted", Boolean.FALSE))
		        .addOrder(Order.desc("scheduledChangeDate"))
		        .setMaxResults(1);
		return (CompanyAccountPricingTypeChange) criteria.uniqueResult();
	}

}
