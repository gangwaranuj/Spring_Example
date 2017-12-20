package com.workmarket.service.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.groups.model.UserGroup;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ianha on 12/26/13
 */
@Service
public class UserGroupSerializationServiceImpl implements UserGroupSerializationService {
	private final Gson gson = new GsonBuilder()
			.registerTypeAdapter(UserGroup.class, UserGroupSerializer.getInstance())
			.create();

	@Override
	public String toJson(UserGroup userGroup) {
		return gson.toJson(userGroup);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public UserGroup fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public UserGroup mergeJson(UserGroup object, String json) {
		throw new NotImplementedException();
	}

	private static class UserGroupSerializer implements JsonSerializer<UserGroup> {
		private static final UserGroupSerializer INSTANCE = new UserGroupSerializer();

		private UserGroupSerializer(){}
		public static UserGroupSerializer getInstance() { return INSTANCE; }

		@Override
		public JsonElement serialize(UserGroup userGroup, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", userGroup.getClass().getSimpleName());
			jsonObject.addProperty("id", userGroup.getId());
			jsonObject.addProperty("name", userGroup.getName());
			return jsonObject;
		}
	}
}
