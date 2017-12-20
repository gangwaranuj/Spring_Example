package com.workmarket.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.workmarket.domains.model.Language;


@Repository
public class LanguageDAOImpl extends AbstractDAO<Language> implements
		LanguageDAO {

	protected Class<Language> getEntityClass() {
		return Language.class;
	}

	@SuppressWarnings("unchecked")
	public List<Language> findLanguages() {
		Query query =  getFactory().getCurrentSession().createQuery("from language order by description asc");
		return query.list();
	}

}
