package com.workmarket.dao.company;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.CompanyPreference;
import org.springframework.stereotype.Repository;

/**
 * Created by ianha on 2/18/14
 */
@Repository
public class CompanyPreferenceDAOImpl extends AbstractDAO<CompanyPreference> implements CompanyPreferenceDAO {
	protected Class<CompanyPreference> getEntityClass() {
		return CompanyPreference.class;
	}
}
