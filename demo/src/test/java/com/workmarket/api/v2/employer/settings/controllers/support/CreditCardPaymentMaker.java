package com.workmarket.api.v2.employer.settings.controllers.support;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.employer.settings.models.CreditCardPaymentDTO;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.withNull;
import static com.natpryce.makeiteasy.Property.newProperty;

public class CreditCardPaymentMaker {

	public static final Property<CreditCardPaymentDTO, String> amount = newProperty();
	public static final Property<CreditCardPaymentDTO, String> cardType = newProperty();
	public static final Property<CreditCardPaymentDTO, String> cardNumber = newProperty();
	public static final Property<CreditCardPaymentDTO, String> cardExpirationMonth = newProperty();
	public static final Property<CreditCardPaymentDTO, String> cardExpirationYear = newProperty();
	public static final Property<CreditCardPaymentDTO, String> cardSecurityCode = newProperty();
	public static final Property<CreditCardPaymentDTO, String> nameOnCard = newProperty();
	public static final Property<CreditCardPaymentDTO, LocationDTO.Builder> billingAddress = newProperty();

	public static final Instantiator<CreditCardPaymentDTO> DEFAULT_CREDIT_CARD = new Instantiator<CreditCardPaymentDTO>() {
		@Override
		public CreditCardPaymentDTO instantiate(PropertyLookup<CreditCardPaymentDTO> lookup) {
			return new CreditCardPaymentDTO.Builder()
				.setAmount(lookup.valueOf(amount, "100.00"))
				.setCardType(lookup.valueOf(cardType, "visa"))
				.setNameOnCard(lookup.valueOf(nameOnCard, "Work Market"))
				.setCardNumber(lookup.valueOf(cardNumber, "4856543113776088"))
				.setCardExpirationMonth(lookup.valueOf(cardExpirationMonth, "01"))
				.setCardExpirationYear(lookup.valueOf(cardExpirationYear, "2050"))
				.setCardSecurityCode(lookup.valueOf(cardSecurityCode, "334"))
				.setLocation(lookup.valueOf(billingAddress, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO)))))
				.build();
		}
	};

	public static final Instantiator<CreditCardPaymentDTO> CREDIT_CARD_WITH_EMPTY_ADDRESS1 = new Instantiator<CreditCardPaymentDTO>() {
		@Override
		public CreditCardPaymentDTO instantiate(PropertyLookup<CreditCardPaymentDTO> lookup) {
			return new CreditCardPaymentDTO.Builder()
				.setAmount(lookup.valueOf(amount, "100.00"))
				.setCardType(lookup.valueOf(cardType, "visa"))
				.setNameOnCard(lookup.valueOf(nameOnCard, "Work Market"))
				.setCardNumber(lookup.valueOf(cardNumber, "4856543113776088"))
				.setCardExpirationMonth(lookup.valueOf(cardExpirationMonth, "01"))
				.setCardExpirationYear(lookup.valueOf(cardExpirationYear, "2050"))
				.setCardSecurityCode(lookup.valueOf(cardSecurityCode, "334"))
				.setLocation(lookup.valueOf(billingAddress, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.addressLine1))))))
				.build();
		}
	};

	public static final Instantiator<CreditCardPaymentDTO> CREDIT_CARD_WITH_EMPTY_CITY = new Instantiator<CreditCardPaymentDTO>() {
		@Override
		public CreditCardPaymentDTO instantiate(PropertyLookup<CreditCardPaymentDTO> lookup) {
			return new CreditCardPaymentDTO.Builder()
				.setAmount(lookup.valueOf(amount, "100.00"))
				.setCardType(lookup.valueOf(cardType, "visa"))
				.setNameOnCard(lookup.valueOf(nameOnCard, "Work Market"))
				.setCardNumber(lookup.valueOf(cardNumber, "4856543113776088"))
				.setCardExpirationMonth(lookup.valueOf(cardExpirationMonth, "01"))
				.setCardExpirationYear(lookup.valueOf(cardExpirationYear, "2050"))
				.setCardSecurityCode(lookup.valueOf(cardSecurityCode, "334"))
				.setLocation(lookup.valueOf(billingAddress, new LocationDTO.Builder(make(a(LocationMaker.LocationDTO, withNull(LocationMaker.city))))))
				.build();
		}
	};
}
