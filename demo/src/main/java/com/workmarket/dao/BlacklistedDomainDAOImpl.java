package com.workmarket.dao;

import com.workmarket.domains.model.BlacklistedDomain;
import org.springframework.stereotype.Repository;

/**
 * Created by nick on 4/21/14 6:57 PM
 */
@Repository
public class BlacklistedDomainDAOImpl extends AbstractDAO<BlacklistedDomain> implements BlacklistedDomainDAO {

	@Override
	protected Class<?> getEntityClass() {
		return BlacklistedDomain.class;
	}
}
