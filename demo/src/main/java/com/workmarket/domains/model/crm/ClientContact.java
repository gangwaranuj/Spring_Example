package com.workmarket.domains.model.crm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.collect.Iterables;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Where;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.directory.Email;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.directory.Website;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;

@Entity(name="clientContact")
@Table(name="client_contact")
@AuditChanges
public class ClientContact extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private String jobTitle;
	@Deprecated
	private ClientLocation clientLocation; // see ClientContactLocationAssociation
	private ClientCompany clientCompany;
	private Company company;
	private boolean primary = false;
	private boolean manager = false;

	private Set<ClientContactEmailAssociation> emailAssociations = Sets.newLinkedHashSet();
	private Set<ClientContactWebsiteAssociation> websiteAssociations = Sets.newLinkedHashSet();
	private Set<ClientContactPhoneAssociation> phoneAssociations = Sets.newLinkedHashSet();
	private Set<ClientContactLocationAssociation> locationAssociations = Sets.newLinkedHashSet();

	@Transient
	private Email mostRecentEmail;

	@Transient
	private Website mostRecentWebsite;

	@Transient
	private Phone mostRecentWorkPhone;

	@Transient
	private Phone mostRecentMobilePhone;

	public ClientContact() {}

	public ClientContact(Company company) {
		this.company = company;
	}

	@Column(name = "first_name", nullable = false, length=50)
	public String getFirstName() {
		return firstName;
	}

	@Column(name = "last_name", nullable = false, length=50)
	public String getLastName() {
		return lastName;
	}

	@Column(name = "job_title", nullable = true, length=50)
	public String getJobTitle() {
		return jobTitle;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="client_location_id", referencedColumnName="id")
	@Deprecated
	public ClientLocation getClientLocation() {
		return clientLocation;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="client_company_id", referencedColumnName="id")
	public ClientCompany getClientCompany() {
		return clientCompany;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="company_id", referencedColumnName="id", nullable=false)
	public Company getCompany() {
		return company;
	}

	@Column(name = "primary_flag")
    public boolean isPrimary() {
        return primary;
    }

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public void setClientLocation(ClientLocation clientLocation) {
		this.clientLocation = clientLocation;
	}

	public void setPrimary(boolean primary) {
        this.primary = primary;
    }

	@Transient
	public String getFullName() {
		return StringUtilities.fullName(getFirstName(), getLastName());
	}

	public void setClientCompany(ClientCompany clientCompany) {
		this.clientCompany = clientCompany;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientContactEmailAssociation> getEmailAssociations() {
		return emailAssociations;
	}

	public void setEmailAssociations(Set<ClientContactEmailAssociation> emailAssociations) {
		this.emailAssociations = emailAssociations;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientContactWebsiteAssociation> getWebsiteAssociations() {
		return websiteAssociations;
	}

	public void setWebsiteAssociations(Set<ClientContactWebsiteAssociation> websiteAssociations) {
		this.websiteAssociations = websiteAssociations;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientContactPhoneAssociation> getPhoneAssociations() {
		return phoneAssociations;
	}

	public void setPhoneAssociations(Set<ClientContactPhoneAssociation> phoneAssociations) {
		this.phoneAssociations = phoneAssociations;
	}

	@OneToMany(mappedBy = "clientContact", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientContactLocationAssociation> getLocationAssociations() {
		return locationAssociations;
	}

	public void setLocationAssociations(Set<ClientContactLocationAssociation> locationAssociations) {
		this.locationAssociations = locationAssociations;
	}

	@Column(name = "manager_flag")
	public boolean isManager() {
		return manager;
	}

	public void setManager(boolean manager) {
		this.manager = manager;
	}

	@Transient
	public Website getMostRecentWebsite() {
		return mostRecentWebsite;
	}

	@Transient
	public void setMostRecentWebsite(Website mostRecentWebsite) {
		this.mostRecentWebsite = mostRecentWebsite;
	}

	@Transient
	public Email getMostRecentEmail() {
		ClientContactEmailAssociation association = Iterables.getLast(getEmailAssociations(), null);
		Email email = (null != association) ? association.getEmail() : null;
		if (email == null) {
			email = mostRecentEmail;
		}
		return email;
	}

	@Transient
	public void setMostRecentEmail(Email mostRecentEmail) {
		this.mostRecentEmail = mostRecentEmail;
	}

	@Transient
	public Phone getMostRecentWorkPhone() {
		Phone phone = null;
		for (ClientContactPhoneAssociation association : getPhoneAssociations()) {
			if (isWorkPhone(association.getPhone().getContactContextType())) {
				phone = association.getPhone();
			}
		}
		if (phone == null) {
			phone = mostRecentWorkPhone;
		}
		return phone;
	}

	@Transient
	public void setMostRecentWorkPhone(Phone mostRecentWorkPhone) {
		this.mostRecentWorkPhone = mostRecentWorkPhone;
	}

	@Transient
	public Phone getMostRecentMobilePhone() {
		Phone phone = null;
		for (ClientContactPhoneAssociation association : getPhoneAssociations()) {
			if (isHomePhone(association.getPhone().getContactContextType())) {
				phone = association.getPhone();
			}
		}
		if (phone == null) {
			phone = mostRecentMobilePhone;
		}
		return phone;
	}

	@Transient
	public void setMostRecentMobilePhone(Phone mostRecentMobilePhone) {
		this.mostRecentMobilePhone = mostRecentMobilePhone;
	}

	@Transient
	public Set<Email> getEmails() {
		Set<Email> emails = Sets.newLinkedHashSet();
		for (ClientContactEmailAssociation e : getEmailAssociations()) {
			emails.add(e.getEmail());
		}
		return emails;
	}

	@Transient
	public Set<Phone> getPhoneNumbers() {
		Set<Phone> phones = Sets.newLinkedHashSet();
		for (ClientContactPhoneAssociation p : getPhoneAssociations()) {
			phones.add(p.getPhone());
		}
		return phones;
	}

	@Transient
	public Set<Phone> getPhoneNumbers(ContactContextType contactContextType) {
		Set<Phone> phones = Sets.newLinkedHashSet();
		for (ClientContactPhoneAssociation p : getPhoneAssociations()) {
			if (p.getPhone().getContactContextType().equals(contactContextType)) {
				phones.add(p.getPhone());
			}
		}
		return phones;
	}

	@Transient
	public Set<Website> getWebsites() {
		Set<Website> websites = Sets.newLinkedHashSet();
		for (ClientContactWebsiteAssociation w : getWebsiteAssociations()) {
			websites.add(w.getWebsite());
		}
		return websites;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(getFullName()).toString();
	}

	private boolean isHomePhone(ContactContextType contactContextType) {
		return ContactContextType.HOME.equals(contactContextType);
	}

	private boolean isWorkPhone(ContactContextType contactContextType) {
		return ContactContextType.WORK.equals(contactContextType);
	}

}
