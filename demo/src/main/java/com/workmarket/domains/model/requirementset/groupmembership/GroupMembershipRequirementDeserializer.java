package com.workmarket.domains.model.requirementset.groupmembership;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/20/13
 */
public class GroupMembershipRequirementDeserializer implements JsonDeserializer<GroupMembershipRequirement> {
	private static final GroupMembershipRequirementDeserializer INSTANCE = new GroupMembershipRequirementDeserializer();

	private GroupMembershipRequirementDeserializer(){}
	public static GroupMembershipRequirementDeserializer getInstance() { return INSTANCE; }

	@Override
	public GroupMembershipRequirement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		GroupMembershipRequirement requirement = new GroupMembershipRequirement();

		JsonElement id = jsonObject.get("id");
		if (id != null && !id.isJsonNull()) {
			requirement.setId(id.getAsLong());
		}

		JsonElement membership = jsonObject.get("requirable");
		if (membership != null && !membership.isJsonNull()) {
			requirement.setGroupMembershipRequirable((GroupMembershipRequirable) context.deserialize(membership, GroupMembershipRequirable.class));
		}

		JsonElement mandatory = jsonObject.get("mandatory");
		if (mandatory != null && !mandatory.isJsonNull()) {
			requirement.setMandatory(mandatory.getAsBoolean());
		}

		return requirement;
	}
}
