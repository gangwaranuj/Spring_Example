package com.workmarket.domains.model.account;

import javax.persistence.*;

import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity(name = "credit_card_transaction")
@Table(name = "credit_card_transaction")
@AuditChanges
public class CreditCardTransaction extends RegisterTransaction {

	private static final long serialVersionUID = 1L;

	private String cardType;
	private String lastFourDigits;

	private RegisterTransaction feeTransaction;

	private String firstName;
	private String lastName;

	// TODO: Move these to an embedded address model
	// Validation is not required because it has already been validated and processed
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postalCode;
	private Country country;

	@Transient
	public String getType() {
		return "Credit Card";
	}

	@Transient
	public String getCardName() {
		if (StringUtilities.isNotEmpty(cardType) && StringUtilities.isNotEmpty(lastFourDigits)) {
			return cardType.toUpperCase() + " - xxxxxxxxxxxx" + lastFourDigits;
		} else {
			return "N/A";
		}
	}

	@Column(name = "card_type", nullable = true, length = 20)
	public String getCardType() {
		return cardType;
	}

	@Column(name = "last_four_digits", nullable = true, length = 4)
	public String getLastFourDigits() {
		return lastFourDigits;
	}

	@Column(name = "first_name", nullable = false, length = 50)
	public String getFirstName() {
		return firstName;
	}

	@Column(name = "last_name", nullable = false, length = 50)
	public String getLastName() {
		return lastName;
	}

	@Column(name = "line1", nullable = false, length = 100)
	public String getAddress1() {
		return address1;
	}

	@Column(name = "line2", nullable = true, length = 100)
	public String getAddress2() {
		return address2;
	}

	@Column(name = "city", nullable = true, length = 50)
	public String getCity() {
		return city;
	}

	@Column(name = "state", nullable = true, length = 100)
	public String getState() {
		return state;
	}

	@Column(name = "postal_code", nullable = true, length = 9)
	public String getPostalCode() {
		return postalCode;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "country", referencedColumnName = "id", nullable = false)
	public Country getCountry() {
		return country;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "fee_id", referencedColumnName = "id", nullable = false)
	public RegisterTransaction getFeeTransaction() {
		return feeTransaction;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public void setLastFourDigits(String lastFourDigits) {
		this.lastFourDigits = lastFourDigits;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
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

	public void setCountry(Country country) {
		this.country = country;
	}

	public void setFeeTransaction(RegisterTransaction feeTransaction) {
		this.feeTransaction = feeTransaction;
	}
}
