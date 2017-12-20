package com.workmarket.domains.model.skill;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name="companyLocationServiced")
@Table(name="company_to_location_association")
@AuditChanges
@Access(AccessType.PROPERTY)
public class CompanyLocationAssociation implements Serializable {

    private static final long serialVersionUID = 3595729108908980308L;

    private CompanyLocationPK companyLocation;
    private Company company;
    private Location location;
    private Boolean deleted = Boolean.FALSE;
    private Calendar createdOn;
    private Calendar modifiedOn;
    private Long modifierId;
    private Long creatorId;

    public CompanyLocationAssociation() { }

    public CompanyLocationAssociation(Company company, Location location) {
        this.companyLocation = new CompanyLocationPK(company.getId(), location.getId());
    }

    @EmbeddedId
    public CompanyLocationPK getCompanyLocation() {
        return companyLocation;
    }

    public void setCompanyLocation(CompanyLocationPK companyLocation) {
        this.companyLocation = companyLocation;
    }

    @Column(name = "deleted", nullable = false)
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @MapsId("companyLocation")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @MapsId("companyLocation")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
