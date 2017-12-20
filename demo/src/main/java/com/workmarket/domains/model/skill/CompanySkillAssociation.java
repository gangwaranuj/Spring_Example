package com.workmarket.domains.model.skill;

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

@Entity(name="companySkillAssociation")
@Table(name="company_to_skill_association")
@AuditChanges
@Access(AccessType.PROPERTY)
public class CompanySkillAssociation implements Serializable {

    private static final long serialVersionUID = 2449030598895403764L;

    private Boolean deleted = Boolean.FALSE;
    private Calendar createdOn;
    private Calendar modifiedOn;
    private Long modifierId;
    private Long creatorId;
    private CompanySkillPK companySkill;
    private Company company;
    private Skill skill;

    public CompanySkillAssociation() { }

    public CompanySkillAssociation(Company company, Skill skill) {
        this.companySkill = new CompanySkillPK(company.getId(), skill.getId());
    }

    @Column(name = "deleted", nullable = false)
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @EmbeddedId
    public CompanySkillPK getCompanySkill() {
        return companySkill;
    }

    public void setCompanySkill(CompanySkillPK companySkill) {
        this.companySkill = companySkill;
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

    public void setCompany(Company company) {
        this.company = company;
    }

    @ManyToOne
    @JoinColumn(name = "company_id")
    @MapsId("companySkill")
    public Company getCompany() {
        return this.company;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @ManyToOne
    @JoinColumn(name = "skill_id")
    @MapsId("companySkill")
    public Skill getSkill() {
        return this.skill;
    }
}
