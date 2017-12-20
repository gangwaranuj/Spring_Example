package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.Industry;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class IndustrySerializationServiceImpl implements IndustrySerializationService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(Industry.class, IndustrySerializer.getInstance())
		.create();

	@Override
	public String toJson(Industry industry) {
		return gson.toJson(industry);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public Industry fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public Industry mergeJson(Industry industry, String json) {
		throw new NotImplementedException();
	}

	private static class IndustrySerializer implements JsonSerializer<Industry> {
		private static final IndustrySerializer INSTANCE = new IndustrySerializer();

		private IndustrySerializer(){}
		public static IndustrySerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(Industry industry, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", industry.getClass().getSimpleName());
			jsonObject.addProperty("id", industry.getId());
			jsonObject.addProperty("name", industry.getName());
			return jsonObject;
		}
	}
}
