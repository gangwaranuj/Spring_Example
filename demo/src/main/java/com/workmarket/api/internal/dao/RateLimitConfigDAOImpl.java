package com.workmarket.api.internal.dao;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.api.internal.model.RateLimitConfig;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RateLimitConfigDAOImpl extends AbstractDAO<RateLimitConfig> implements RateLimitConfigDAO {
    @Override
    protected Class<?> getEntityClass() {
        return RateLimitConfig.class;
    }

    @Override
    public RateLimitConfig findByNameAndCompanyId(String name, long companyId) {
        Query query = getFactory().getCurrentSession().getNamedQuery("rateLimitConfig.getByNameAndCompanyId");
        query.setParameter("name", name);
        query.setParameter("companyId", companyId);

        return (RateLimitConfig) query.uniqueResult();
    }
}
