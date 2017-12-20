package com.workmarket.domains.forums.dao;


import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.forums.model.ForumCategory;
import org.springframework.stereotype.Repository;

@Repository
public class ForumCategoryDAOImpl extends AbstractDAO<ForumCategory> implements ForumCategoryDAO {

	@Override
	protected Class<ForumCategory> getEntityClass() {
		return ForumCategory.class;
	}

}
