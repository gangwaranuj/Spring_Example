package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.contract.Contract;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ContractSerializationServiceImpl implements ContractSerializationService {
	private Gson gson = new GsonBuilder()
		.registerTypeAdapter(Contract.class, ContractSerializer.getInstance())
		.create();

	@Override
	public String toJson(Contract contract) {
		return gson.toJson(contract);
	}

	@Override
	public String toJson(List<?> contracts) {
		return gson.toJson(contracts);
	}

	@Override
	public Contract fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public Contract mergeJson(Contract contract, String json) {
		throw new NotImplementedException();
	}

	private static class ContractSerializer implements JsonSerializer<Contract> {
		private static final ContractSerializer INSTANCE = new ContractSerializer();

		private ContractSerializer(){}
		public static ContractSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(Contract contract, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", contract.getClass().getSimpleName());
			jsonObject.addProperty("id", contract.getId());
			jsonObject.addProperty("name", contract.getName());
			return jsonObject;
		}
	}
}
