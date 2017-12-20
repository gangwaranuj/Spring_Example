package com.workmarket.domains.model.tag;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.workmarket.domains.model.audit.AuditChanges;


@Entity(name = "userTag")
@NamedQueries({
        @NamedQuery(name="userTag.findUserTagByName", query="select t from userTag t where t.name = :name")
})
@DiscriminatorValue("UT")
@AuditChanges
public class UserTag extends Tag
{
	private static final long serialVersionUID = 1L;

    public UserTag() { }

    public UserTag(String name) {
        super(name);
    }
}
