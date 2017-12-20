package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name = "profileIndustryAssociation")
@Table(name = "profile_industry_association")
@NamedQueries({
	@NamedQuery(name="profileIndustryAssociation.findDefaultIndustryForProfile", query="select pia.industry " +
		"from profileIndustryAssociation pia where pia.profile.id = :profileId and pia.deleted = false order by pia.id desc"),
	@NamedQuery(name="profileIndustryAssociation.findIndustriesForProfile", query="select pia.industry " +
		"from profileIndustryAssociation pia where pia.profile.id = :profileId and pia.deleted = false"),
	@NamedQuery(name="profileIndustryAssociation.findAllIndustriesForProfile", query="select pia.industry from profileIndustryAssociation pia where pia.profile.id = :profileId"),
	@NamedQuery(name="profileIndustryAssociation.findAllIndustriesAssociationsForProfile", query="select pia from profileIndustryAssociation pia where pia.profile.id = :profileId"),
	@NamedQuery(name = "profileIndustryAssociation.doesProfileHaveIndustry", query = "select count(pia.id) " +
		"FROM profileIndustryAssociation pia WHERE pia.profile.id = :profileId AND pia.industry.id = :industryId AND pia.deleted = false"),
	@NamedQuery(name = "profileIndustryAssociation.findByIndustryAndProfile", query = "select pia " +
		"FROM profileIndustryAssociation pia WHERE pia.industry.id = :industryId AND pia.profile.id = :profileId"),
})
@AuditChanges
public class ProfileIndustryAssociation extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private Industry industry;
	private Profile profile;

	public ProfileIndustryAssociation() {}

	public ProfileIndustryAssociation(Industry industry, Profile profile) {
		this.industry = industry;
		this.profile = profile;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "industry_id", updatable = false)
	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id", updatable = false)
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

}
