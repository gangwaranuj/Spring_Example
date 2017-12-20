package com.workmarket.domains.model.tag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name = "productTag")
@NamedQueries({
        @NamedQuery(name="productTag.findProductTagByNameAndIndustryId", query="select t from productTag t left join t.industries i where t.name = :name and i.id = :industryId")
})
@DiscriminatorValue("PT")
@AuditChanges
public class ProductTag extends IndustryTag
{
	private static final long serialVersionUID = 1L;

    public ProductTag() { }

    public ProductTag(String name) {
        super(name);
    }
}
