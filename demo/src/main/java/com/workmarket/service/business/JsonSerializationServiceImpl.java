package com.workmarket.service.business;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.json.GsonJsonAdapter;
import com.workmarket.json.JsonAdapter;
import com.workmarket.thrift.EnumValue;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class JsonSerializationServiceImpl implements JsonSerializationService {

	private JsonAdapter jsonAdapter = new GsonJsonAdapter();

	private GsonBuilder getDefaultGsonBuilder() {
		return new GsonBuilder()
			.registerTypeAdapter(GregorianCalendar.class, new JsonSerializer<Calendar>() {
				public JsonElement serialize(Calendar src, Type srcType, JsonSerializationContext context) {
					return new JsonPrimitive(DateUtilities.getISO8601(src));
				}
			})
			.registerTypeAdapter(Calendar.class, new JsonDeserializer<Calendar>() {
				public Calendar deserialize(JsonElement json, Type srcType, JsonDeserializationContext context) {
					return DateUtilities.getCalendarFromISO8601(json.getAsJsonPrimitive().getAsString());
				}
			})
			.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
					return new JsonPrimitive(DateUtilities.getISO8601(src));
				}
			})
			.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
				public Date deserialize(JsonElement json, Type srcType, JsonDeserializationContext context) {
					return DateUtilities.getDateFromISO8601(json.getAsJsonPrimitive().getAsString());
				}
			})
			.registerTypeAdapter(Boolean.class, new JsonSerializer<Boolean>() {
				public JsonElement serialize(Boolean src, Type srcType, JsonSerializationContext context) {
					return new JsonPrimitive((src == null) ? 0 : (src ? 1 : 0));
				}
			})
			.registerTypeAdapter(boolean.class, new JsonSerializer<Boolean>() {
				public JsonElement serialize(Boolean src, Type srcType, JsonSerializationContext context) {
					return new JsonPrimitive(src ? 1 : 0);
				}
			})
			.registerTypeHierarchyAdapter(EnumValue.class, new JsonSerializer<EnumValue>() {
				public JsonElement serialize(EnumValue src, Type srcType, JsonSerializationContext context) {
					return new JsonPrimitive(src.getValue());
				}
			});
	}

	@Override
	public String toJson(Object object, final String... ignore) {
		GsonBuilder builder = getDefaultGsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
			@Override public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return ArrayUtils.contains(ignore, fieldAttributes.getName());
			}

			@Override public boolean shouldSkipClass(Class<?> aClass) {
				return false;
			}
		});

		return builder.create().toJson(object);
	}

	@Override
	public String toJson(Object object) {
		return jsonAdapter.toJson(object);
	}

	@Override
	public String toJsonIdentity(Object object) {
		return getDefaultGsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJson(object);
	}

	@Override
	public <E> E fromJson(String json, Class<E> clazz) {
		return jsonAdapter.fromJson(json, clazz);
	}

	@Override
	public <E> E fromJson(String json, Type typeOfT) {
		return jsonAdapter.fromJson(json, typeOfT);
	}
}
