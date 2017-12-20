package com.workmarket.api.v2.employer.settings.controllers.support;


import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import com.workmarket.api.v2.employer.settings.models.TaxInfoDTO;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxEntityType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.natpryce.makeiteasy.Property.newProperty;

public class TaxInfoMaker {

	public static final Property<TaxInfoDTO, String> taxCountry = newProperty();
	public static final Property<TaxInfoDTO, String> taxEntityTypeCode = newProperty();
	public static final Property<TaxInfoDTO, String> taxName = newProperty();
	public static final Property<TaxInfoDTO, String> taxNumber = newProperty();
	public static final Property<TaxInfoDTO, String> firstName = newProperty();
	public static final Property<TaxInfoDTO, String> lastName = newProperty();
	public static final Property<TaxInfoDTO, Boolean> businessFlag = newProperty();
	public static final Property<TaxInfoDTO, String> address = newProperty();
	public static final Property<TaxInfoDTO, String> city = newProperty();
	public static final Property<TaxInfoDTO, String> state = newProperty();
	public static final Property<TaxInfoDTO, String> postalCode = newProperty();
	public static final Property<TaxInfoDTO, String> country = newProperty();
	public static final Property<TaxInfoDTO, String> businessName = newProperty();
	public static final Property<TaxInfoDTO, String> signature = newProperty();
	public static final Property<TaxInfoDTO, String> signatureDateString = newProperty();
	public static final Property<TaxInfoDTO, String> countryOfIncorporation = newProperty();
	public static final Property<TaxInfoDTO, Boolean> deliveryPolicyFlag = new Property<>();

	public static final Instantiator<TaxInfoDTO> USBusinessTaxEntityDTO = new Instantiator<TaxInfoDTO>() {
		@Override
		public TaxInfoDTO instantiate(PropertyLookup<TaxInfoDTO> lookup) {

			TaxInfoDTO.Builder builder =  new TaxInfoDTO.Builder();
			// Generate a unique tax number to abide by DB unique constraints
			final String sevenDigits = String.valueOf(Math.round((Math.max(Math.random() * 9999999, 1000000))));
			final String twoDigits = String.valueOf(Math.round(Math.max(Math.random() * 99, 10)));
			final String uniqueTaxNumber = twoDigits + "-" + sevenDigits;

			builder.setTaxCountry(lookup.valueOf(taxCountry, AbstractTaxEntity.COUNTRY_USA))
				.setBusinessFlag(lookup.valueOf(businessFlag, Boolean.TRUE))
				.setTaxName(lookup.valueOf(taxName, "The TEST Company"))
				.setLastName(lookup.valueOf(lastName, "The TEST Company"))
				.setBusinessNameFlag(Boolean.TRUE)
				.setBusinessName(lookup.valueOf(businessName, "The TEST Company Business"))
				.setTaxEntityTypeCode(lookup.valueOf(taxEntityTypeCode, TaxEntityType.C_CORP))
				.setTaxNumber(lookup.valueOf(taxNumber, uniqueTaxNumber))
				.setDeliveryPolicyFlag(lookup.valueOf(deliveryPolicyFlag, Boolean.TRUE))
				.setSignature(lookup.valueOf(signature, "9393"))
				.setSignatureDateString(lookup.valueOf(signatureDateString, getDateString()));
			setUsaDefaultAddress(builder, lookup);
			return builder.build();
		}
	};

	public static final Instantiator<TaxInfoDTO> CANADABusinessTaxEntityDTO = new Instantiator<TaxInfoDTO>() {
		@Override
		public TaxInfoDTO instantiate(PropertyLookup<TaxInfoDTO> lookup) {

			TaxInfoDTO.Builder builder =  new TaxInfoDTO.Builder();
			final String random9DigitNum = String.valueOf(100000000 + new Random().nextInt(900000000));
			builder.setTaxCountry(lookup.valueOf(taxCountry, AbstractTaxEntity.COUNTRY_CANADA))
				.setBusinessFlag(lookup.valueOf(businessFlag, Boolean.TRUE))
				.setTaxName(lookup.valueOf(taxName, "Doug Flutie, Eh"))
				.setLastName(lookup.valueOf(lastName, "Doug Flutie, Eh"))
				.setTaxNumber(random9DigitNum + "-RN-2345") // EIN
				.setDeliveryPolicyFlag(lookup.valueOf(deliveryPolicyFlag, Boolean.TRUE));
			setCanadaDefaultAddress(builder, lookup);
			return builder.build();
		}
	};

	public static final Instantiator<TaxInfoDTO> ForeignBusinessTaxEntityDTO = new Instantiator<TaxInfoDTO>() {
		@Override
		public TaxInfoDTO instantiate(PropertyLookup<TaxInfoDTO> lookup) {

			TaxInfoDTO.Builder builder =  new TaxInfoDTO.Builder();
			builder.setTaxCountry(lookup.valueOf(taxCountry, AbstractTaxEntity.COUNTRY_OTHER))
				.setBusinessFlag(lookup.valueOf(businessFlag, Boolean.TRUE))
				.setTaxName(lookup.valueOf(taxName, "Frédéric"))
				.setLastName(lookup.valueOf(lastName, "Frédéric"))
				.setTaxEntityTypeCode(lookup.valueOf(taxEntityTypeCode, TaxEntityType.LLC_DISREGARDED))
							// TODO API - may need to generate unique tax numbers here
				.setTaxNumber(lookup.valueOf(taxNumber, "123-456-pommes-frites-99")) // EIN
				.setCountryOfIncorporation(lookup.valueOf(countryOfIncorporation, "FRA"))
				.setDeliveryPolicyFlag(lookup.valueOf(deliveryPolicyFlag, Boolean.TRUE));
			setForeignDefaultAddress(builder, lookup);
			return builder.build();
		}
	};

	private static void setUsaDefaultAddress(TaxInfoDTO.Builder builder, PropertyLookup<TaxInfoDTO> lookup) {
		builder.setAddress(lookup.valueOf(address, "20 West 20th Street"))
		.setCity(lookup.valueOf(city, "New York"))
		.setState(lookup.valueOf(state, "NY"))
		.setPostalCode(lookup.valueOf(postalCode, "10010"));
	}

	private static void setCanadaDefaultAddress(TaxInfoDTO.Builder builder, PropertyLookup<TaxInfoDTO> lookup) {
		builder.setAddress(lookup.valueOf(address, "800 Wellington Street"))
			.setCity(lookup.valueOf(city, "Ottawa"))
			.setState(lookup.valueOf(state, "ON"))
			.setPostalCode(lookup.valueOf(postalCode, "K0A1B0"));
	}

	private static void setForeignDefaultAddress(TaxInfoDTO.Builder builder, PropertyLookup<TaxInfoDTO> lookup) {
		builder.setAddress(lookup.valueOf(address, "800 Chemin de la Marde"))
			.setCity(lookup.valueOf(city, "Aix-en-Provence"))
			.setState(lookup.valueOf(state, "CÃ´te-d'Azur"))
			.setPostalCode(lookup.valueOf(postalCode, "13001"));
	}

	private static String getDateString() {
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateString = sdf.format(date);
		return dateString;
	}
}
