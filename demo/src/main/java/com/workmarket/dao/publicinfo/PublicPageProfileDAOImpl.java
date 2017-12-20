package com.workmarket.dao.publicinfo;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.publicinfo.PublicPageProfile;
import com.workmarket.dto.PublicPageProfileDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by rahul on 12/15/13
 */
@Repository
public class PublicPageProfileDAOImpl extends AbstractDAO<PublicPageProfile> implements PublicPageProfileDAO {

	private static final Log logger = LogFactory.getLog(PublicPageProfileDAOImpl.class);

	protected Class<PublicPageProfile> getEntityClass() {

		return PublicPageProfile.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicPageProfile> getAllPublicPageProfilesByIndustry(String industry) {
		Assert.notNull(industry);

		Query query = getFactory().getCurrentSession().createQuery("FROM publicPageProfile WHERE industry_name = :industry_name")
				.setParameter("industry_name", industry);

		return (List<PublicPageProfile>)query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicPageProfileDTO> getAllPublicPageProfilesDataByIndustry(String industry) {
		Assert.notNull(industry);

		Query query = getFactory().getCurrentSession()
				.getNamedQuery("PublicPageProfile.getAllPublicPageProfilesDataByIndustry")
				.setParameter("industry", industry);

		return query.setResultTransformer(Transformers.aliasToBean(PublicPageProfileDTO.class)).list();
	}
}
