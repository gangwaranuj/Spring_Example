package com.workmarket.service.business.dto;

import com.workmarket.dto.AddressDTO;
import com.workmarket.utility.StringUtilities;
import org.hibernate.validator.constraints.NotEmpty;

public class PaymentDTO extends AddressDTO {

	private String paymentType;
	private String amount;
	private String firstName;
	private String lastName;
	private String cardType; // Values: Visa, MasterCard, Discover, Amex
	private String cardNumber;
	private String cardExpirationMonth;
	private String cardExpirationYear;
	private String cardSecurityCode;
	private String nameOnCard;

	private String cardExpirationDateString; // Format: MMYYYY

	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(final String paymentType) {
		this.paymentType = paymentType;
	}

	public String getAmount() {
		return amount;
	}
	public void setAmount(final String amount) {
		this.amount = amount;
	}

	@NotEmpty
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	@NotEmpty
	public String getLastName() {
		return lastName;
	}
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@NotEmpty
	public String getCardType() {
		return cardType;
	}
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	@NotEmpty
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	@NotEmpty
	public String getCardExpirationMonth() {
		return cardExpirationMonth;
	}
	public void setCardExpirationMonth(final String cardExpirationMonth) {
		this.cardExpirationMonth = cardExpirationMonth;
	}

	@NotEmpty
	public String getCardExpirationYear() {
		return cardExpirationYear;
	}
	public void setCardExpirationYear(final String cardExpirationYear) {
		this.cardExpirationYear = cardExpirationYear;
	}

	@NotEmpty
	public String getCardSecurityCode() {
		return cardSecurityCode;
	}
	public void setCardSecurityCode(final String cardSecurityCode) {
		this.cardSecurityCode = cardSecurityCode;
	}

	@NotEmpty
	public String getNameOnCard() {

		if (nameOnCard == null) {
			return StringUtilities.fullName(firstName, lastName);
		}
		return nameOnCard;
	}

	public void setNameOnCard(final String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	// NOTE Hack to have both PaymentDTO and ScreeningDTO on same form
	public String getFirstNameOnCard() {
		return firstName;
	}
	public void setFirstNameOnCard(final String firstName) {
		this.firstName = firstName;
	}

	// NOTE Hack to have both PaymentDTO and ScreeningDTO on same form
	public String getLastNameOnCard() {
		return lastName;
	}
	public void setLastNameOnCard(final String lastName) {
		this.lastName = lastName;
	}

	public String getCardExpirationDateString() {
		if (cardExpirationDateString == null) {
			return String.format("%s%s", cardExpirationMonth, cardExpirationYear);
		}
		return cardExpirationDateString;
	}
	public void setCardExpirationDateString(final String cardExpirationDateString) {
		this.cardExpirationDateString = cardExpirationDateString;
	}

	public boolean isAccount() {
        return "account".equals(paymentType);
	}
	public boolean isCreditCard() {
        return "cc".equals(paymentType);
	}
}
