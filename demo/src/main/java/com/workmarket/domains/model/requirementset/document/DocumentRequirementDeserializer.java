package com.workmarket.domains.model.requirementset.document;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class DocumentRequirementDeserializer implements JsonDeserializer<DocumentRequirement> {
	private static final DocumentRequirementDeserializer INSTANCE = new DocumentRequirementDeserializer();

	private DocumentRequirementDeserializer(){}
	public static DocumentRequirementDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public DocumentRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		DocumentRequirement requirement = new DocumentRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement document = jsonObject.get("requirable");
		if (document != null && !document.isJsonNull()) {
			requirement.setDocumentRequirable((DocumentRequirable) context.deserialize(document, DocumentRequirable.class));
		}

		JsonElement requiresExpirationDate = jsonObject.get("requiresExpirationDate");
		if (requiresExpirationDate != null && !requiresExpirationDate.isJsonNull()) {
			requirement.setRequiresExpirationDate(requiresExpirationDate.getAsBoolean());
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}
