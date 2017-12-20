package com.workmarket.json.workresource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.thrift.work.ResourceNote;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class ResourceNoteSerializer implements JsonSerializer<ResourceNote> {

	private static final ResourceNoteSerializer INSTANCE = new ResourceNoteSerializer();

	private ResourceNoteSerializer() {}

	public static ResourceNoteSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(ResourceNote resourceNote, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("hoverType", resourceNote.getHoverType().toString());
		jsonObject.addProperty("note", resourceNote.getNote());
		jsonObject.addProperty("actionCodeDescription", resourceNote.getActionCodeDescription());
		jsonObject.addProperty("dateOfNote", resourceNote.getDateOfNote());
		jsonObject.addProperty("resourceId", resourceNote.getResourceId());
		jsonObject.addProperty("onBehalfOfUserName", resourceNote.getOnBehalfOfUserName());
		jsonObject.addProperty("masqueradeUserName", resourceNote.getMasqueradeUserName());
		jsonObject.addProperty("actionCodeId", resourceNote.getActionCodeId());
		if (resourceNote.getMasqueradeUser() != null) {
			jsonObject.add("masqueradeUser", jsonSerializationContext.serialize(resourceNote.getMasqueradeUser()));
		}
		if (resourceNote.getOnBehalfOfUser() != null) {
			jsonObject.add("onBehalfOfUser", jsonSerializationContext.serialize(resourceNote.getOnBehalfOfUser()));
		}
		if (resourceNote.getResourceUser() != null) {
			jsonObject.add("resourceUser", jsonSerializationContext.serialize(resourceNote.getResourceUser()));
		}
		return jsonObject;
	}
}
