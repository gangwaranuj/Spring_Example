package com.workmarket.dao.profile;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ProfilePhoneAssociation;
import com.workmarket.domains.model.directory.Phone;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfilePhoneAssociationDAOImpl extends AbstractDAO<ProfilePhoneAssociation> implements ProfilePhoneAssociationDAO {

	protected Class<ProfilePhoneAssociation> getEntityClass() {
		return ProfilePhoneAssociation.class;
	}

	@Override
	public List<Phone> findPhonesByProfileId(long profileId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("phone", FetchMode.JOIN)
				.add(Restrictions.eq("entity.id", profileId))
				.setProjection(Projections.property("phone"));
		return criteria.list();
	}
}
