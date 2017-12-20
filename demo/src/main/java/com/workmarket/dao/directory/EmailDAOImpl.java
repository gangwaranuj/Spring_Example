package com.workmarket.dao.directory;

import java.util.Map;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.directory.Email;

@Repository
public class EmailDAOImpl extends PaginationAbstractDAO<Email> implements EmailDAO {

	protected Class<Email> getEntityClass() {
        return Email.class;
    }

	@Override
	public void applySorts(Pagination<Email> pagination, Criteria query, Criteria count) {}

	@Override
	public void applyFilters(Pagination<Email> pagination, Criteria query, Criteria count) {}

	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {}

	@Override
	public Email findById(Long id) {
		return (Email)getFactory().getCurrentSession().get(getEntityClass(), id);
	}
   
}
