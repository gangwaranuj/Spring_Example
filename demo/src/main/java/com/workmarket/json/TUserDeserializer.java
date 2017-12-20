package com.workmarket.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.User;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class TUserDeserializer implements JsonDeserializer<User> {

	private static final TUserDeserializer INSTANCE = new TUserDeserializer();

	private TUserDeserializer() {
	}

	public static TUserDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		User user = new User();

		JsonElement field = jsonObject.get("id");
		if (field != null && !field.isJsonNull()) {
			user.setId(field.getAsLong());
		}
		field = jsonObject.get("userNumber");
		if (field != null && !field.isJsonNull()) {
			user.setUserNumber(field.getAsString());
		}
		field = jsonObject.get("email");
		if (field != null && !field.isJsonNull()) {
			user.setEmail(field.getAsString());
		}
		field = jsonObject.get("isWorkMarketEmployee");
		if (field != null && !field.isJsonNull()) {
			user.setIsWorkMarketEmployee(field.getAsBoolean());
		}
		field = jsonObject.get("laneType");
		if (field != null && !field.isJsonNull()) {
			user.setLaneType(LaneType.valueOf(field.getAsString()));
		}
		field = jsonObject.get("name");
		if (field != null && !field.isJsonNull()) {
			user.setName((Name)jsonDeserializationContext.deserialize(field, Name.class));
		}
		return user;
	}

}
