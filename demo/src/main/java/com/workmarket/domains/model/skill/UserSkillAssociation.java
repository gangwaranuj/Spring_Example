package com.workmarket.domains.model.skill;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="userSkillAssociation")
@Table(name="user_skill_association")
@AuditChanges
public class UserSkillAssociation extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
    private Skill skill;
    private Long skillLevel = 0L;

    public UserSkillAssociation() { }

    public UserSkillAssociation(User user, Skill skill) {
        this.user = user;
        this.skill = skill;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @Column(name = "skill_level", nullable = false, unique = false)
    public Long getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Long skillLevel) {
        this.skillLevel = skillLevel;
    }
}
