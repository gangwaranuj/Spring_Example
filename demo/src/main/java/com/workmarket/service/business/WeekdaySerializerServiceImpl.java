package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.requirementset.availability.WeekdayRequirable;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class WeekdaySerializerServiceImpl implements WeekdaySerializerService {
	private final Gson gson = new GsonBuilder()
		.registerTypeAdapter(WeekdayRequirable.class, WeekdaySerializer.getInstance())
		.create();

	@Override
	public String toJson(WeekdayRequirable weekdayRequirable) {
		return gson.toJson(weekdayRequirable);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public WeekdayRequirable fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public WeekdayRequirable mergeJson(WeekdayRequirable weekdayRequirable, String json) {
		throw new NotImplementedException();
	}

	private static class WeekdaySerializer implements JsonSerializer<WeekdayRequirable> {
		private static final WeekdaySerializer INSTANCE = new WeekdaySerializer();

		private WeekdaySerializer(){}
		public static WeekdaySerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(WeekdayRequirable weekdayRequirable, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", weekdayRequirable.getClass().getSimpleName());
			jsonObject.addProperty("id", weekdayRequirable.getId());
			jsonObject.addProperty("name", weekdayRequirable.getName());
			return jsonObject;
		}
	}
}
