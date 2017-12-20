package com.workmarket.domains.payments.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.pricing.PaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPaymentPeriod;
import com.workmarket.domains.model.account.pricing.subscription.SubscriptionPeriodType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository
public class PaymentPeriodDAOImpl extends AbstractDAO<PaymentPeriod> implements PaymentPeriodDAO {

	protected Class<PaymentPeriod> getEntityClass() {
		return PaymentPeriod.class;
	}

	@Override
	public List<SubscriptionPaymentPeriod> findBySubscriptionConfigurationId(long subscriptionConfigurationId) {
		return getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.asc("periodDateRange.from")).list();
	}

	@Override
	public SubscriptionPaymentPeriod findBySubscriptionConfigurationIdAndDateInRange(long subscriptionConfigurationId, Calendar dateInRange) {
		return (SubscriptionPaymentPeriod)getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.add(Restrictions.le("periodDateRange.from", dateInRange))
				.add(Restrictions.ge("periodDateRange.through", dateInRange))
				.addOrder(Order.asc("periodDateRange.from"))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public SubscriptionPaymentPeriod findNextFromDateBySubscriptionConfigurationId(long subscriptionConfigurationId, Calendar fromDate) {
		return (SubscriptionPaymentPeriod)getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.add(Restrictions.ge("periodDateRange.from", fromDate))
				.addOrder(Order.asc("periodDateRange.from"))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public SubscriptionPaymentPeriod findNextNotInvoicedSubscriptionPaymentPeriod(long subscriptionConfigurationId) {
		return (SubscriptionPaymentPeriod)getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.isNull("subscriptionInvoice"))
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.addOrder(Order.asc("periodDateRange.from"))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public SubscriptionPaymentPeriod findNextInvoiceableSubscriptionPaymentPeriod(long subscriptionConfigurationId, Calendar firstDayOfNextMonth) {
		return (SubscriptionPaymentPeriod)getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.isNull("subscriptionInvoice"))
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.add(Restrictions.le("periodDateRange.from", firstDayOfNextMonth))
				.addOrder(Order.asc("periodDateRange.from"))
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public List<SubscriptionPaymentPeriod> findAllInvoicedFutureSubscriptionPaymentPeriods(long subscriptionConfigurationId) {
		Calendar today = Calendar.getInstance();
		Criteria criteria = getFactory().getCurrentSession().createCriteria(SubscriptionPaymentPeriod.class)
				.add(Restrictions.eq("subscriptionConfiguration.id", subscriptionConfigurationId))
				.add(Restrictions.eq("deleted", false))
				.add(Restrictions.isNotNull("subscriptionInvoice"))
				.add(Restrictions.eq("subscriptionPeriodType.code", SubscriptionPeriodType.AUTO))
				.add(Restrictions.ge("periodDateRange.from", today))
				.addOrder(Order.asc("periodDateRange.from"));
		return criteria.list();
	}
}