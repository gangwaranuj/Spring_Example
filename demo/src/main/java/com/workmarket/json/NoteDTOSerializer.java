package com.workmarket.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.service.business.dto.NoteDTO;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class NoteDTOSerializer implements JsonSerializer<NoteDTO> {

	private static final NoteDTOSerializer INSTANCE = new NoteDTOSerializer();

	private NoteDTOSerializer() {}

	public static NoteDTOSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(NoteDTO noteDTO, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("content", noteDTO.getContent());
		if (noteDTO.getIsPrivate() != null) {
			jsonObject.addProperty("isPrivate", noteDTO.getIsPrivate());
		}
		if (noteDTO.getPrivileged() != null) {
			jsonObject.addProperty("privileged", noteDTO.getPrivileged());
		}
		return jsonObject;
	}
}
