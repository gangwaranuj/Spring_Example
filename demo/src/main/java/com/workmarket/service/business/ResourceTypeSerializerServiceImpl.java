package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.requirementset.resourcetype.ResourceTypeRequirable;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ResourceTypeSerializerServiceImpl implements ResourceTypeSerializerService {
	private final Gson gson = new GsonBuilder()
		.registerTypeAdapter(ResourceTypeRequirable.class, ResourceTypeSerializer.getInstance())
		.create();

	@Override
	public String toJson(ResourceTypeRequirable resourceType) {
		return gson.toJson(resourceType);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public ResourceTypeRequirable fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public ResourceTypeRequirable mergeJson(ResourceTypeRequirable resourceType, String json) {
		throw new NotImplementedException();
	}

	private static class ResourceTypeSerializer implements JsonSerializer<ResourceTypeRequirable> {
		private static final ResourceTypeSerializer INSTANCE = new ResourceTypeSerializer();

		private ResourceTypeSerializer(){}
		public static ResourceTypeSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(ResourceTypeRequirable resourceType, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", resourceType.getClass().getSimpleName());
			jsonObject.addProperty("id", resourceType.getId());
			jsonObject.addProperty("name", resourceType.getName());
			return jsonObject;
		}
	}
}
