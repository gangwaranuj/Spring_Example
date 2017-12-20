package com.workmarket.json;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.workmarket.service.business.dto.NoteDTO;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class NoteDTODeserializer implements JsonDeserializer<NoteDTO> {

	private static final NoteDTODeserializer INSTANCE = new NoteDTODeserializer();

	private NoteDTODeserializer() {
	}

	public static NoteDTODeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public NoteDTO deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		NoteDTO noteDTO = new NoteDTO();

		JsonElement field = jsonObject.get("content");
		if (field != null && !field.isJsonNull()) {
			noteDTO.setContent(field.getAsString());
		}
		field = MoreObjects.firstNonNull(jsonObject.get("isPrivate"), jsonObject.get("is_private"));
		if (field != null && !field.isJsonNull()) {
			String value = field.getAsString();
			noteDTO.setIsPrivate("1".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value));
		}
		field = jsonObject.get("privileged");
		if (field != null && !field.isJsonNull()) {
			noteDTO.setPrivileged(field.getAsBoolean());
		}
		return noteDTO;
	}


}
