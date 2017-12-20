package com.workmarket.dao.directory;

import java.util.Map;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.directory.Website;

@Repository
public class WebsiteDAOImpl extends PaginationAbstractDAO<Website> implements WebsiteDAO {

	protected Class<Website> getEntityClass() {
        return Website.class;
    }

	@Override
	public void applySorts(Pagination<Website> pagination, Criteria query, Criteria count) {}

	@Override
	public void applyFilters(Pagination<Website> pagination, Criteria query, Criteria count) {}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {}
   
	@Override
	public Website findById(Long id) {
		return (Website)getFactory().getCurrentSession().get(getEntityClass(), id);
	}
}
