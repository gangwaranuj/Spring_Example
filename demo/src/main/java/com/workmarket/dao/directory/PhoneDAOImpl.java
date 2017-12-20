package com.workmarket.dao.directory;

import java.util.Map;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.directory.Phone;

@Repository
public class PhoneDAOImpl extends PaginationAbstractDAO<Phone> implements PhoneDAO {

	protected Class<Phone> getEntityClass() {
        return Phone.class;
    }

	@Override
	public void applySorts(Pagination<Phone> pagination, Criteria query, Criteria count) {}
	@Override
	public void applyFilters(Pagination<Phone> pagination, Criteria query, Criteria count) {}
	@Override
	public void buildWhereClause(Criteria query, Criteria count, Map<String, Object> params) {}

	@Override
	public Phone findById(Long id) {
		return (Phone)getFactory().getCurrentSession().get(getEntityClass(), id);
	}
   
}
