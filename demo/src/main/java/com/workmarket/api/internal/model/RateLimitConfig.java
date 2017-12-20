package com.workmarket.api.internal.model;


import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;

import javax.persistence.*;

/**
 */
@Entity(name = "rateLimitConfig")
@Table(name = "rate_limit_config")
@NamedQueries({
        @NamedQuery(name = "rateLimitConfig.getByNameAndCompanyId", query = "from rateLimitConfig rlc where rlc.name = :name and rlc.company.id = :companyId and rlc.disabled = false"),
})
public class RateLimitConfig extends AbstractEntity {
    private static final long serialVersionUID = -6430696557138473595L;

    private String name; // key for this rate limit configuration
    private long limitWindowInSeconds = -1;
    private long limit = -1; // max number of requests within limit window
    private boolean disabled = false;
    private Company company;

    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public RateLimitConfig setName(String name) {
        this.name = name;
        return this;
    }

    @Column(name = "limit_window_in_seconds", nullable = false)
    public long getLimitWindowInSeconds() {
        return limitWindowInSeconds;
    }

    public RateLimitConfig setLimitWindowInSeconds(long limitWindowInSeconds) {
        this.limitWindowInSeconds = limitWindowInSeconds;
        return this;
    }

    @Column(name = "limit", nullable = false)
    public long getLimit() {
        return limit;
    }

    public RateLimitConfig setLimit(long limit) {
        this.limit = limit;
        return this;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    public Company getCompany() {
        return company;
    }

    public RateLimitConfig setCompany(Company company) {
        this.company = company;
        return this;
    }

    @Column(name = "disabled", nullable = false)
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RateLimitConfig that = (RateLimitConfig) o;

        if (limitWindowInSeconds != that.limitWindowInSeconds) return false;
        if (limit != that.limit) return false;
        if (disabled != that.disabled) return false;
        if (!name.equals(that.name)) return false;

        if (company == null && that.company == null) return true;
        if (company == null || that.company == null) return false;

        return company.equals(that.company);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) (limitWindowInSeconds ^ (limitWindowInSeconds >>> 32));
        result = 31 * result + (int) (limit ^ (limit >>> 32));
        result = 31 * result + (disabled ? 1 : 0);
        if (company != null) result = 31 * result + company.hashCode();
        return result;
    }
}
