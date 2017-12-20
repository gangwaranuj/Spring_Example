package com.workmarket.api.internal.dao;

import com.workmarket.dao.DAOInterface;
import com.workmarket.api.internal.model.RateLimitConfig;

public interface RateLimitConfigDAO extends DAOInterface<RateLimitConfig> {
    RateLimitConfig findByNameAndCompanyId(String name, long companyId);
}
