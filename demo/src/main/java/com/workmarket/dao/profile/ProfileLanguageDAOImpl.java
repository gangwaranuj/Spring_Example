package com.workmarket.dao.profile;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.ProfileLanguage;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileLanguageDAOImpl extends AbstractDAO<ProfileLanguage> implements ProfileLanguageDAO  {

	protected Class<ProfileLanguage> getEntityClass() {
        return ProfileLanguage.class;
    }


	@Override
	public List<ProfileLanguage> findAllProfileLanguageByProfileId(long profileId) {
		Criteria criteria = getFactory().getCurrentSession().createCriteria(getEntityClass())
				.setFetchMode("profile", FetchMode.JOIN)
				.add(Restrictions.eq("profile.id", profileId));
		return criteria.list();
	}
}
