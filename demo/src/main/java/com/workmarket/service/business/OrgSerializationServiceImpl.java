package com.workmarket.service.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.ProtocolStringList;
import com.workmarket.business.gen.Messages;
import com.workmarket.business.gen.Messages.OrgUnitPath;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

@Service
public class OrgSerializationServiceImpl implements OrgSerializationService {

	private Gson orgGson = new GsonBuilder()
			.registerTypeAdapter(OrgUnitPath.class, OrgUnitPathSerializer.getInstance())
			.create();

	@Override
	public String toJson(final OrgUnitPath orgUnitPath) {
		return orgGson.toJson(orgUnitPath);
	}

	@Override
	public String toJson(final List<OrgUnitPath> orgUnitPaths) {
		return orgGson.toJson(orgUnitPaths);
	}


	private static class OrgUnitPathSerializer implements JsonSerializer<OrgUnitPath> {

		private static OrgUnitPathSerializer INSTANCE = new OrgUnitPathSerializer();

		public static OrgUnitPathSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(final OrgUnitPath orgUnitPath, final Type type,
				final JsonSerializationContext jsonSerializationContext) {
			final JsonObject json = new JsonObject();
			json.addProperty("uuid", orgUnitPath.getUuid());
			json.addProperty("name", orgUnitPath.getName());

			final ProtocolStringList pathList = orgUnitPath.getPathList();
			final JsonArray pathsArray = (pathList != null)
					? new Gson().toJsonTree(pathList).getAsJsonArray()
					: new JsonArray();
			json.add("paths", pathsArray);
			return json;
		}

	}

}
