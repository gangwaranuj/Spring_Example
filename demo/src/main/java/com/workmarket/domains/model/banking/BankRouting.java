package com.workmarket.domains.model.banking;

import com.workmarket.domains.model.postalcode.Country;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name = "bank_routing")
@Table(name = "bank_routing_number")
public class BankRouting {

	private Long id;
	private String routingNumber;
	private String bankName;
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private Country country;

	@Id
	@Column(name = "id", nullable = false)
	public Long getId() {return id;}

	@Column(name = "routing_number", nullable = false)
	public String getRoutingNumber() {
		return routingNumber;
	}

	@Column(name = "bank_name", nullable = false)
	public String getBankName() {
		return bankName;
	}

	@Column(name = "address", nullable = false)
	public String getAddress() {
		return address;
	}

	@Column(name = "city", nullable = false)
	public String getCity() {
		return city;
	}

	@Column(name = "state", nullable = false)
	public String getState() {
		return state;
	}

	@Column(name = "postal_code", nullable = false)
	public String getPostalCode() {
		return postalCode;
	}

	public void setId(Long id) {this.id = id;}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
