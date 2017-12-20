package com.workmarket.dao.postalcode;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.utility.StringUtilities;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class PostalCodeDAOImpl extends AbstractDAO<PostalCode> implements
		PostalCodeDAO {
	protected Class<PostalCode> getEntityClass() {
		return PostalCode.class;
	}

	@Override
	public PostalCode findByPostalCode(String postalCode,Country country) {
		return (PostalCode) getFactory().getCurrentSession().createCriteria(getEntityClass())
				.add(Restrictions.eq("postalCode", postalCode))
				.add(Restrictions.eq("country", country))
				.createAlias("country", "country")
				.setFetchMode("country", FetchMode.JOIN)
				.setMaxResults(1)
				.uniqueResult();
	}


	@Override
	public PostalCode findByPostalCodeCountryStateCity(String postalCode, String countryId, State state, String city) {
		Country country = Country.valueOf(countryId);
		if (StringUtilities.isNotEmpty(postalCode)) {
			return (PostalCode) getFactory().getCurrentSession().createCriteria(getEntityClass())
					.add(Restrictions.eq("postalCode", postalCode))
					.add(Restrictions.eq("country", country))
					.createAlias("country", "country")
					.setFetchMode("country", FetchMode.JOIN)
					.setMaxResults(1)
					.uniqueResult();
		} else {
			return (PostalCode) getFactory().getCurrentSession().createCriteria(getEntityClass())
					.add(Restrictions.eq("country", country))
					.add(Restrictions.eq("stateProvince", state))
					.add(Restrictions.eq("city", city))
					.createAlias("country", "country")
					.setFetchMode("country", FetchMode.JOIN)
					.setMaxResults(1)
					.uniqueResult();
		}
	}

}
