package com.workmarket.domains.model.crm;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.domains.model.audit.AuditChanges;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name="clientCompany")
@Table(name="client_company")
@AuditChanges
public class ClientCompany extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private Company company;
	private Set<ClientLocation> locations = Sets.newLinkedHashSet();
	private String customerId; // AKA Client Number
	private String region;
	private String division;
	private Industry industry;
	private String industryName;

	private Set<ClientCompanyEmailAssociation> emailAssociations = Sets.newLinkedHashSet();
	private Set<ClientCompanyWebsiteAssociation> websiteAssociations = Sets.newLinkedHashSet();
	private Set<ClientCompanyPhoneAssociation> phoneAssociations = Sets.newLinkedHashSet();

	private Set<ClientContact> contacts = Sets.newLinkedHashSet();
	public ClientCompany() {}
	public ClientCompany(Company company) {
		this.company = company;
	}

	@Column(name = "name", nullable = false, length=255)
	public String getName() {
		return name;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="company_id", referencedColumnName="id", nullable=false)
	public Company getCompany() {
		return company;
	}

	@OneToMany(mappedBy = "clientCompany", fetch = FetchType.LAZY, cascade = {})
	@Where(clause = "deleted = 0")
    public Set<ClientLocation> getLocations() {
        return locations;
    }

	public void setName(String name) {
		this.name = name;
	}

	public void setCompany(Company company){
		this.company = company;
	}

	public void setLocations(Set<ClientLocation> locations) {
        this.locations = locations;
    }

	@Column(name = "customer_id", length=255)
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Column(name = "region", length=255)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Column(name = "division", length=255)
	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}


	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="industry_id", referencedColumnName="id")
	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientCompanyEmailAssociation> getEmailAssociations() {
		return emailAssociations;
	}

	public void setEmailAssociations(Set<ClientCompanyEmailAssociation> emailAssociations) {
		this.emailAssociations = emailAssociations;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientCompanyWebsiteAssociation> getWebsiteAssociations() {
		return websiteAssociations;
	}

	public void setWebsiteAssociations(Set<ClientCompanyWebsiteAssociation> websiteAssociations) {
		this.websiteAssociations = websiteAssociations;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientCompanyPhoneAssociation> getPhoneAssociations() {
		return phoneAssociations;
	}

	public void setPhoneAssociations(Set<ClientCompanyPhoneAssociation> phoneAssociations) {
		this.phoneAssociations = phoneAssociations;
	}

	@Column(name = "industry", length=200)
	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public void setContacts(Set<ClientContact> contacts) {
		this.contacts = contacts;
	}

	@OneToMany(mappedBy = "clientCompany", fetch = FetchType.LAZY, cascade = {})
	@Where(clause = "deleted = 0")
	public Set<ClientContact> getContacts() {
		return contacts;
	}

	@Transient
	public Set<Email> getEmails() {
		Set<Email> emails = Sets.newLinkedHashSet();
		for (ClientCompanyEmailAssociation e : getEmailAssociations()) {
			emails.add(e.getEmail());
		}
		return emails;
	}

	@Transient
	public Set<Phone> getPhoneNumbers() {
		Set<Phone> phones = Sets.newLinkedHashSet();
		for (ClientCompanyPhoneAssociation p : getPhoneAssociations()) {
			phones.add(p.getPhone());
		}
		return phones;
	}

	@Transient
	public Set<Website> getWebsites() {
		Set<Website> websites = Sets.newLinkedHashSet();
		for (ClientCompanyWebsiteAssociation w : getWebsiteAssociations()) {
			websites.add(w.getWebsite());
		}
		return websites;
	}

	@Transient
	public Phone getLastPhone() {
		ClientCompanyPhoneAssociation association = Iterables.getLast(getPhoneAssociations(), null);
		return (null != association)? association.getPhone() : null;
	}

	@Transient
	public Website getLastWebsite() {
		ClientCompanyWebsiteAssociation association = Iterables.getLast(getWebsiteAssociations(), null);
		return (null != association)? association.getWebsite() : null;
	}

	@Transient
	public boolean isDeletable()  {
		return CollectionUtils.isEmpty(getLocations()) && CollectionUtils.isEmpty(getContacts());
	}

}
