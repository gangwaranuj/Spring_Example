package com.workmarket.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.User;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class TUserSerializer implements JsonSerializer<User> {

	private static final TUserSerializer INSTANCE = new TUserSerializer();

	private TUserSerializer() {}

	public static TUserSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", user.getId());
		jsonObject.addProperty("userNumber", user.getUserNumber());
		jsonObject.addProperty("email", user.getEmail());
		jsonObject.addProperty("isWorkMarketEmployee", user.isIsWorkMarketEmployee());
		if (user.getLaneType() != null) {
			jsonObject.addProperty("laneType", user.getLaneType().toString());
		}
		if (user.getName() != null) {
			jsonObject.add("name", jsonSerializationContext.serialize(user.getName(), Name.class));
		}
		return jsonObject;
	}
}
