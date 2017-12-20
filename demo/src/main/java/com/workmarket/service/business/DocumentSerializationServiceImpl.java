package com.workmarket.service.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.domains.model.asset.CompanyAsset;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class DocumentSerializationServiceImpl implements DocumentSerializationService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(CompanyAsset.class, DocumentSerializer.getInstance())
		.create();

	@Override
	public String toJson(CompanyAsset asset) {
		return gson.toJson(asset);
	}

	@Override
	public String toJson(List<?> assets) {
		return gson.toJson(assets);
	}

	@Override
	public CompanyAsset fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public CompanyAsset mergeJson(CompanyAsset asset, String json) {
		throw new NotImplementedException();
	}

	private static class DocumentSerializer implements JsonSerializer<CompanyAsset> {
		private static final DocumentSerializer INSTANCE = new DocumentSerializer();

		private DocumentSerializer(){}
		public static DocumentSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(CompanyAsset asset, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", asset.getClass().getSimpleName());
			jsonObject.addProperty("id", asset.getAssetId());
			jsonObject.addProperty("name", asset.getName());
			return jsonObject;
		}
	}
}
