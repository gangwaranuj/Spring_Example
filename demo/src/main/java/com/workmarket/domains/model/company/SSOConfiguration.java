package com.workmarket.domains.model.company;


import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="sso_configuration")
@Table(name="sso_configuration")
@AuditChanges
public class SSOConfiguration  extends AbstractEntity {

    private AclRole defaultRole;
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="default_acl_role_id", referencedColumnName="id")
    public AclRole getDefaultRole() {
        return defaultRole;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
    public Company getCompany() {
        return company;
    }

    public void setDefaultRole(AclRole defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
