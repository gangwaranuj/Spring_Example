package com.workmarket.thrift;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.json.JsonAdapter;
import com.workmarket.json.TUserDeserializer;
import com.workmarket.json.TUserSerializer;
import com.workmarket.json.workresource.ResourceNoteDeserializer;
import com.workmarket.json.workresource.ResourceNoteSerializer;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ThriftJsonAdapter implements JsonAdapter {
	private Gson gson;

	public ThriftJsonAdapter() {
		gson = getDefaultGsonBuilder().create();
	}

	@Override
	public String toJson(Object object) {
		return gson.toJson(object);
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
	public <E> E fromJson(String json, Class<E> clazz) {
		return gson.fromJson(json, clazz);
	}

	@Override
	public <E> E fromJson(String json, Type typeOfT) {
		return (E)gson.fromJson(json, typeOfT);
	}

	public Gson getGson() {
		return gson;
	}

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
				})
				.registerTypeAdapter(ResourceNote.class, ResourceNoteSerializer.getInstance())
				.registerTypeAdapter(ResourceNote.class, ResourceNoteDeserializer.getInstance())
				.registerTypeAdapter(User.class, TUserSerializer.getInstance())
				.registerTypeAdapter(User.class, TUserDeserializer.getInstance());
	}
}
