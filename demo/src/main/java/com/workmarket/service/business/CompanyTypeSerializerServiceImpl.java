package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.requirementset.companytype.CompanyTypeRequirable;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CompanyTypeSerializerServiceImpl implements CompanyTypeSerializerService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(CompanyTypeRequirable.class, CompanyTypeSerializer.getInstance())
		.create();

	@Override
	public String toJson(CompanyTypeRequirable companyType) {
		return gson.toJson(companyType);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public CompanyTypeRequirable fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public CompanyTypeRequirable mergeJson(CompanyTypeRequirable companyType, String json) {
		throw new NotImplementedException();
	}

	private static class CompanyTypeSerializer implements JsonSerializer<CompanyTypeRequirable> {
		private static final CompanyTypeSerializer INSTANCE = new CompanyTypeSerializer();

		private CompanyTypeSerializer(){}
		public static CompanyTypeSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(CompanyTypeRequirable companyType, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", companyType.getClass().getSimpleName());
			jsonObject.addProperty("id", companyType.getId());
			jsonObject.addProperty("name", companyType.getName());
			return jsonObject;
		}
	}
}
