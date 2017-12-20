package com.workmarket.domains.model.tag;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name="tag")
@Table(name="tag")
@NamedQueries({
        @NamedQuery(name="tag.findTagByName", query="select e from tag e where e.name = :name"),
        @NamedQuery(name="tag.findTagByNameAndTagSpace", query="select e from tag e where e.name = :name and e.tagSpace = :tagSpace")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("T")
@AuditChanges
public class Tag extends ApprovableVerifiableEntity
{
	private static final long serialVersionUID = 1L;

	private String name;

    @Deprecated
    public enum TagSpace {
        GENERIC,
        BRAND,
        INDUSTRY,
        OPERATING_SYSTEM;
    }

    private TagSpace tagSpace = TagSpace.GENERIC;

    public Tag() { }

    public Tag(String name) {
        this.name = name;
    }

    @Column(name = "name", nullable = false, length=200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "tag_space", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Deprecated
    public TagSpace getTagSpace()
    {
        return tagSpace;
    }

    @Deprecated
    public void setTagSpace(TagSpace tagSpace)
    {
        this.tagSpace = tagSpace;
    }
}
