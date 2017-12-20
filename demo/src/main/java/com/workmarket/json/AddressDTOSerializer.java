package com.workmarket.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.dto.AddressDTO;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class AddressDTOSerializer implements JsonSerializer<AddressDTO> {

	private static final AddressDTOSerializer INSTANCE = new AddressDTOSerializer();

	private AddressDTOSerializer() {}

	public static AddressDTOSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(AddressDTO addressDTO, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("addressId", addressDTO.getAddressId());
		jsonObject.addProperty("address1", addressDTO.getAddress1());
		jsonObject.addProperty("address2", addressDTO.getAddress2());
		jsonObject.addProperty("city", addressDTO.getCity());
		jsonObject.addProperty("state", addressDTO.getState());
		jsonObject.addProperty("postalCode", addressDTO.getPostalCode());
		jsonObject.addProperty("country", addressDTO.getCountry());
		jsonObject.addProperty("addressTypeCode", addressDTO.getAddressTypeCode());
		jsonObject.addProperty("locationTypeId", addressDTO.getLocationTypeId());
		jsonObject.addProperty("dressCodeId", addressDTO.getDressCodeId());
		jsonObject.addProperty("latitude", addressDTO.getLatitude());
		jsonObject.addProperty("longitude", addressDTO.getLongitude());
		return jsonObject;
	}
}
