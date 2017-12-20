package com.workmarket.domains.model.requirementset.document;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class DocumentRequirementSerializer implements JsonSerializer<DocumentRequirement> {
	private static final DocumentRequirementSerializer INSTANCE = new DocumentRequirementSerializer();

	private DocumentRequirementSerializer(){}
	public static DocumentRequirementSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(DocumentRequirement requirement, Type type, JsonSerializationContext jsc) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());
		jsonObject.addProperty("requiresExpirationDate", requirement.isRequiresExpirationDate());
		jsonObject.addProperty("allowMultiple", requirement.allowMultiple());

		JsonObject documentObject = new JsonObject();
		documentObject.addProperty("name", requirement.getDocumentRequirable().getName());
		documentObject.addProperty("id", requirement.getDocumentRequirable().getId());
		jsonObject.add("requirable", documentObject);

		return jsonObject;
	}
}
