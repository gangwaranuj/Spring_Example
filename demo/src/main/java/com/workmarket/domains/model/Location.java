package com.workmarket.domains.model;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="location")
@Table(name="location")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType= DiscriminatorType.INTEGER)
@DiscriminatorValue("1")
@AuditChanges
public class Location extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private Address address;
	private String locationNumber;
	private Company company;
	private String instructions;

	public Location() {}
	public Location(Address address) {
		this.address = address;
	}

	@Column(name="name", length=100)
	public String getName() {
		return name;
	}

	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="address_id", referencedColumnName="id", nullable=false)
	public Address getAddress() {
		return address;
	}

	@Column(name="location_number", length=50)
	public String getLocationNumber() {
		return locationNumber;
	}

	@Column(name="instructions")
	public String getInstructions() {
		return instructions;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="company_id", referencedColumnName = "id")
	public Company getCompany() {
		return company;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
