package com.workmarket.dao.banking;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.banking.BankRouting;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.JoinType;
import java.util.List;

@Repository
public class BankRoutingDAOImpl extends AbstractDAO<BankRouting> implements BankRoutingDAO {
	@Override
	protected Class<BankRouting> getEntityClass() {
		return BankRouting.class;
	}

	@Override
	public BankRouting get(String primaryKey) {
		return (BankRouting)getFactory().getCurrentSession().get(getEntityClass(), primaryKey);
	}

	@Override
	public BankRouting get(String routingNumber, String countryId) {
		Criteria criteria = getFactory().getCurrentSession()
			.createCriteria(getEntityClass())
			.add(Restrictions.eq("routingNumber", routingNumber));

		// if given a country then add that to our criteria, this really is required
		// as we now have both US and Canadian bank accounts and there is no guarantee the
		// routing numbers will be unique across countries
		// @TODO CONVERT THIS TO AN ASSERT (left this way for backwards compatibility)
		if (StringUtils.isNotEmpty(countryId)) {
			criteria = criteria.createCriteria("country")
				.add(Restrictions.eq("id", countryId));
		}
		criteria.setMaxResults(1);

		return (BankRouting) criteria.setMaxResults(1).uniqueResult();
	}

	@Override
	@Deprecated
	public List<BankRouting> suggest(String prefix) {
		return getFactory().getCurrentSession()
			.createCriteria(getEntityClass())
			.add(Restrictions.ilike("routingNumber", prefix, MatchMode.START))
			.setMaxResults(10)
			.list();
	}

	@Override
	public List<BankRouting> suggestInCountry(String text, String countryId) {
		Criteria criteria = getFactory().getCurrentSession()
			.createCriteria(getEntityClass())
			.add(Restrictions.ilike("routingNumber", text, MatchMode.ANYWHERE));

		// if given a country then add that to our criteria, this really is required
		// as we now have both US and Canadian bank accounts and there is no guarantee the
		// routing numbers will be unique across countries
		// @TODO CONVERT THIS TO AN ASSERT (left this way for backwards compatibility)
		if (StringUtils.isNotEmpty(countryId)) {
			criteria = criteria.createCriteria("country")
				.add(Restrictions.eq("id", countryId));
		}

		return criteria.setMaxResults(10).list();
	}

}
