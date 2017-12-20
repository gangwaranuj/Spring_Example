package com.workmarket.api.internal.service;

import com.workmarket.api.internal.dao.RateLimitConfigDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.api.internal.model.RateLimitConfig;
import com.workmarket.redis.RedisAdapter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RateLimitConfigServiceImpl implements RateLimitConfigService {
    @Autowired RateLimitConfigDAO rateLimitConfigDAO;
    @Autowired RedisAdapter redisAdapter;

    @Override
    public List<RateLimitConfig> findAllByName(String name) {
        List<RateLimitConfig> configs = new ArrayList<>();
        Map<Object, Object> map = redisAdapter.getAllForHash(name);

        if (MapUtils.isEmpty(map)) {
            configs = rateLimitConfigDAO.findAllBy("name", name, "disabled", false);

            if (CollectionUtils.isEmpty(configs)) {
                return configs;
            }

            Map<String, String> hashFields = new HashMap<>();
            for (RateLimitConfig c : configs) {
                String key = isDefaultConfig(c) ? "null" : c.getCompany().getId().toString();
                String value = c.getLimit() + ":" + c.getLimitWindowInSeconds();
                hashFields.put(key, value);
            }
            redisAdapter.setAll(name, hashFields);
        } else {
            for (Object companyId : map.keySet()) {
                String values = (String) map.get(companyId);
                if (StringUtils.isNotBlank(values)) {
                    String[] fields = values.split(":"); // value is ":" delimited string with format "<limit>:<limitWindowInSeconds>"
                    RateLimitConfig c = new RateLimitConfig().setLimit(Long.parseLong(fields[0]))
                            .setLimitWindowInSeconds(Long.parseLong(fields[1])).setName(name);
                    Company company = new Company();
                    if (!"null".equals(companyId)) {
                        company.setId(Long.parseLong((String)companyId));
                    }
                    c.setCompany(company);
                    configs.add(c);
                }
            }
        }

        return configs;
    }

    @Override
    public List<RateLimitConfig> findByCompanyId(long companyId) {
        return rateLimitConfigDAO.findAllBy("company.id", companyId, "disabled", false);
    }

    @Override
    public RateLimitConfig findByNameAndCompanyId(String name, long companyId) {
        List<RateLimitConfig> configs = findAllByName(name);
        RateLimitConfig defaultConfig = null;

        for (RateLimitConfig c : configs) {
            if (isDefaultConfig(c)) {
                defaultConfig = c;
            } else if (c.getCompany().getId().equals(companyId)) {
                return c;
            }
        }

        return defaultConfig;
    }

    private boolean isDefaultConfig(RateLimitConfig c) {
        return c.getCompany() == null || c.getCompany().getId() == null;
    }
}
