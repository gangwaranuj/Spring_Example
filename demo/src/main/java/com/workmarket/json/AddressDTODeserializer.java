package com.workmarket.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.dto.AddressDTO;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class AddressDTODeserializer implements JsonDeserializer<AddressDTO> {

	private static final AddressDTODeserializer INSTANCE = new AddressDTODeserializer();

	private AddressDTODeserializer() {
	}

	public static AddressDTODeserializer getInstance() {
		return INSTANCE;
	}

	@Override public AddressDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		AddressDTO addressDTO = new AddressDTO();

		JsonElement field = jsonObject.get("addressId");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setAddressId(field.getAsLong());
		}
		field = jsonObject.get("address1");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setAddress1(field.getAsString());
		}
		field = jsonObject.get("address2");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setAddress2(field.getAsString());
		}
		field = jsonObject.get("city");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setCity(field.getAsString());
		}
		field = jsonObject.get("state");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setState(field.getAsString());
		}
		field = jsonObject.get("postalCode");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setPostalCode(field.getAsString());
		}
		field = jsonObject.get("country");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setCountry(field.getAsString());
		}
		field = jsonObject.get("addressTypeCode");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setAddressTypeCode(field.getAsString());
		}
		field = jsonObject.get("locationTypeId");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setLocationTypeId(field.getAsLong());
		}
		field = jsonObject.get("dressCodeId");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setDressCodeId(field.getAsLong());
		}
		field = jsonObject.get("latitude");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setLatitude(field.getAsBigDecimal());
		}
		field = jsonObject.get("longitude");
		if (field != null && !field.isJsonNull()) {
			addressDTO.setLongitude(field.getAsBigDecimal());
		}
		return addressDTO;
	}
}
