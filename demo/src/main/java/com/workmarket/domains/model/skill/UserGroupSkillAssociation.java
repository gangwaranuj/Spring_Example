package com.workmarket.domains.model.skill;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name="userGroupSkillAssociation")
@Table(name="user_group_to_skill_association")
@AuditChanges
@Access(AccessType.PROPERTY)
public class UserGroupSkillAssociation implements Serializable {
    private Boolean deleted = Boolean.FALSE;
    private Calendar createdOn;
    private Calendar modifiedOn;
    private Long modifierId;
    private Long creatorId;
    private UserGroupSkillPK userGroupSkill;
    private UserGroup userGroup;
    private Skill skill;

    public UserGroupSkillAssociation() { }

    public UserGroupSkillAssociation(UserGroup userGroup, Skill skill) {
        this.userGroupSkill = new UserGroupSkillPK(userGroup.getId(), skill.getId());
    }

    public UserGroupSkillAssociation(Long userGroupId, Long skillId) {
        this.userGroupSkill = new UserGroupSkillPK(userGroupId, skillId);
    }

    @Column(name = "deleted", nullable = false)
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @EmbeddedId
    public UserGroupSkillPK getUserGroupSkill() {
        return userGroupSkill;
    }

    public void setUserGroupSkill(UserGroupSkillPK userGroupSkill) {
        this.userGroupSkill = userGroupSkill;
    }

    @Column(name = "created_on", nullable = false, updatable = false)
    public Calendar getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Calendar createdOn) {
        this.createdOn = createdOn;
    }

    @Column(name = "modified_on", nullable = false)
    public Calendar getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Calendar modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Column(name = "modifier_id")
    public Long getModifierId() { return modifierId; }

    public void setModifierId(Long modifierId) { this.modifierId = modifierId; }

    @Column(name = "creator_id")
    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    @MapsId("userGroupSkill")
    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @ManyToOne
    @JoinColumn(name = "skill_id")
    @MapsId("userGroupSkill")
    public Skill getSkill() {
        return this.skill;
    }
}
