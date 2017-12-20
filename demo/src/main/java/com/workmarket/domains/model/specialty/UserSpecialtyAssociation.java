package com.workmarket.domains.model.specialty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.VerifiableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="userSpecialtyAssociation")
@Table(name="user_specialty_association")
@AuditChanges
public class UserSpecialtyAssociation extends VerifiableEntity {

	private static final long serialVersionUID = 1L;

	private User user;
    private Specialty specialty;
    private Integer skillLevel = 0;

    public UserSpecialtyAssociation() { }

    public UserSpecialtyAssociation(User user, Specialty specialty) {
        this.user = user;
        this.specialty = specialty;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", updatable = false)
    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    @Column(name = "skill_level", nullable = false, unique = false)
    public Integer getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Integer skillLevel) {
        this.skillLevel = skillLevel;
    }


}
