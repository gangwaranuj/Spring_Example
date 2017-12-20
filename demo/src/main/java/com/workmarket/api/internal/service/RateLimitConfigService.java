package com.workmarket.api.internal.service;

import com.workmarket.api.internal.model.RateLimitConfig;

import java.util.List;

public interface RateLimitConfigService {
    List<RateLimitConfig> findAllByName(String name);

    List<RateLimitConfig> findByCompanyId(long companyId);

    RateLimitConfig findByNameAndCompanyId(String name, long companyId);
}
