package com.workmarket.domains.model.tag;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name = "industryTag")
@NamedQueries({
})
@DiscriminatorValue("IT")
@AuditChanges
public class IndustryTag extends Tag
{
	private static final long serialVersionUID = 1L;

    @NotNull
    private Set<Industry> industries = Sets.newHashSet();

    public IndustryTag() { }

    public IndustryTag(String name) {
        super(name);
    }

	@ManyToMany
	@JoinTable(name = "tag_industry",
			joinColumns = { @JoinColumn(name = "tag_id") },
			inverseJoinColumns = { @JoinColumn(name = "industry_id") })
	public Set<Industry> getIndustries()
	{
		return industries;
	}

	public void setIndustries(Set<Industry> industries)
	{
		this.industries = industries;
	}
}
