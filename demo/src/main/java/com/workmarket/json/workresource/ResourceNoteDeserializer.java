package com.workmarket.json.workresource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.ResourceNoteType;

import java.lang.reflect.Type;

/**
 * Author: rocio
 */
public class ResourceNoteDeserializer implements JsonDeserializer<ResourceNote> {

	private static final ResourceNoteDeserializer INSTANCE = new ResourceNoteDeserializer();

	private ResourceNoteDeserializer() {
	}

	public static ResourceNoteDeserializer getInstance() {
		return INSTANCE;
	}

	@Override public ResourceNote deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		ResourceNote resourceNote = new ResourceNote();

		JsonElement field = jsonObject.get("hoverType");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setHoverType(ResourceNoteType.valueOf(field.getAsString()));
		}
		field = jsonObject.get("note");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setNote(field.getAsString());
		}
		field = jsonObject.get("actionCodeDescription");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setActionCodeDescription(field.getAsString());
		}
		field = jsonObject.get("dateOfNote");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setDateOfNote(field.getAsLong());
		}
		field = jsonObject.get("resourceId");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setResourceId(field.getAsLong());
		}
		field = jsonObject.get("onBehalfOfUserName");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setOnBehalfOfUserName(field.getAsString());
		}
		field = jsonObject.get("masqueradeUserName");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setMasqueradeUserName(field.getAsString());
		}
		field = jsonObject.get("actionCodeId");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setActionCodeId(field.getAsLong());
		}
		field = jsonObject.get("masqueradeUser");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setMasqueradeUser((User)jsonDeserializationContext.deserialize(field, User.class));
		}
		field = jsonObject.get("onBehalfOfUser");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setOnBehalfOfUser((User)jsonDeserializationContext.deserialize(field, User.class));
		}
		field = jsonObject.get("resourceUser");
		if (field != null && !field.isJsonNull()) {
			resourceNote.setResourceUser((User)jsonDeserializationContext.deserialize(field, User.class));
		}
		return resourceNote;
	}
}
