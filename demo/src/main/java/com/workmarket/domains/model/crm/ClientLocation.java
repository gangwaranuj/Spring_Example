
package com.workmarket.domains.model.crm;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.directory.Phone;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="clientLocation")
@DiscriminatorValue("2")
@AuditChanges
public class ClientLocation extends Location {

	private static final long serialVersionUID = 1L;

	private ClientCompany clientCompany;
	private boolean primary = false;

	private Set<ClientLocationAvailability> availableHours = Sets.newLinkedHashSet();
	private Set<ClientLocationPhoneAssociation> phoneAssociations = Sets.newLinkedHashSet();

	public ClientLocation() {}

	public ClientLocation(boolean primary) {
		this.primary = primary;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="client_company_id", referencedColumnName="id")
	public ClientCompany getClientCompany() {
		return clientCompany;
	}

	@Column(name = "primary_flag")
    public boolean isPrimary() {
        return primary;
    }

	public void setClientCompany(ClientCompany clientCompany) {
		this.clientCompany = clientCompany;
	}

	public void setPrimary(boolean primary) {
        this.primary = primary;
	}

	@OneToMany(mappedBy = "clientLocation", cascade = {}, fetch = FetchType.LAZY)
	public Set<ClientLocationAvailability> getAvailableHours() {
		return availableHours;
	}
	public void setAvailableHours(Set<ClientLocationAvailability> availableHours) {
		this.availableHours = availableHours;
	}

	@OneToMany(mappedBy = "entity", cascade = {}, fetch = FetchType.LAZY)
	@Where(clause = "deleted = 0")
	public Set<ClientLocationPhoneAssociation> getPhoneAssociations() {
		return phoneAssociations;
	}

	public void setPhoneAssociations(Set<ClientLocationPhoneAssociation> phoneAssociations) {
		this.phoneAssociations = phoneAssociations;
	}

	@Transient
	public Set<Phone> getPhoneNumbers() {
		Set<Phone> phones = Sets.newLinkedHashSet();
		for (ClientLocationPhoneAssociation p : getPhoneAssociations()) {
			phones.add(p.getPhone());
		}
		return phones;
	}

}
