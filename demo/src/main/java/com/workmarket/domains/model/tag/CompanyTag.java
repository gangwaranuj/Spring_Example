package com.workmarket.domains.model.tag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name = "companyTag")
@NamedQueries({
		@NamedQuery(name="companyTag.findCompanyTagByName", query="select t from companyTag t where t.name = :name")
})
@DiscriminatorValue("CT")
@AuditChanges
public class CompanyTag extends Tag
{
	private static final long serialVersionUID = 1L;

    public CompanyTag() { }

    public CompanyTag(String name) {
        super(name);
    }
}
