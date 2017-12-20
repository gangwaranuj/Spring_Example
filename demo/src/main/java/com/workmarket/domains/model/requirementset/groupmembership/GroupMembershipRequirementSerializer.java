package com.workmarket.domains.model.requirementset.groupmembership;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by ianha on 12/20/13
 */
public class GroupMembershipRequirementSerializer implements JsonSerializer<GroupMembershipRequirement> {
	private static final GroupMembershipRequirementSerializer INSTANCE = new GroupMembershipRequirementSerializer();

	private GroupMembershipRequirementSerializer(){}
	public static GroupMembershipRequirementSerializer getInstance() { return INSTANCE; }

	@Override
	public JsonElement serialize(GroupMembershipRequirement requirement, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("$type", requirement.getClass().getSimpleName());
		jsonObject.addProperty("$humanTypeName", requirement.getHumanTypeName());
		jsonObject.addProperty("id", requirement.getId());
		jsonObject.addProperty("mandatory", requirement.isMandatory());

		JsonObject agreementObject = new JsonObject();
		agreementObject.addProperty("name", requirement.getGroupMembershipRequirable().getName());
		agreementObject.addProperty("id", requirement.getGroupMembershipRequirable().getId());
		jsonObject.add("requirable", agreementObject);

		return jsonObject;
	}
}
