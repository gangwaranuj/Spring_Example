package com.workmarket.domains.model.tag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name = "companyAdminTag")
@NamedQueries({
		@NamedQuery(name="companyAdminTag.findCompanyAdminTagByName", query="select t from companyAdminTag t where t.name = :name")
})
@DiscriminatorValue("CAT")
@AuditChanges
public class CompanyAdminTag extends Tag
{
	private static final long serialVersionUID = 1L;

    public CompanyAdminTag() { }

    public CompanyAdminTag(String name) {
        super(name);
    }
}
