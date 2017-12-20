package com.workmarket.service.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.Company;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ianha on 1/5/14
 */
@Service
public class CompanySerializationServiceImpl implements CompanySerializationService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(Company.class, CompanySerializer.getInstance())
		.create();

	@Override
	public String toJson(Company company) {
		return gson.toJson(company);
	}

	@Override
	public String toJson(List<?> collection) {
		return gson.toJson(collection);
	}

	@Override
	public Company fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public Company mergeJson(Company object, String json) {
		throw new NotImplementedException();
	}

	private static class CompanySerializer implements JsonSerializer<Company> {
		private static final CompanySerializer INSTANCE = new CompanySerializer();

		private CompanySerializer(){}
		public static CompanySerializer getInstance() { return INSTANCE; }

		@Override
		public JsonElement serialize(Company company, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", company.getClass().getSimpleName());
			jsonObject.addProperty("id", company.getId());
			jsonObject.addProperty("name", company.getName());
			return jsonObject;
		}
	}
}
