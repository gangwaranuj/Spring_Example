package com.workmarket.dao.account;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkFeeConfigurationDAOImpl extends AbstractDAO<WorkFeeConfiguration> implements WorkFeeConfigurationDAO  {

	protected Class<WorkFeeConfiguration> getEntityClass() {
        return WorkFeeConfiguration.class;
    }
	
	
	public WorkFeeConfiguration findWithWorkFeeBands(Long companyId) {
		
		String queryString = " from workFeeConfiguration wfc join fetch wfc.workFeeBands where wfc.active = true and wfc.accountRegister.company.id = :companyId";
		
		Query query = getFactory().getCurrentSession().createQuery(queryString);
		query.setLong("companyId", companyId);
		List<WorkFeeConfiguration> results = (List<WorkFeeConfiguration>) query.list();

		return CollectionUtilities.first(results);
	}
	
}
