package com.workmarket.json.workresource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.dto.AddressDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class WorkResourceDetailSerializer implements JsonSerializer<WorkResourceDetail> {

	private static final WorkResourceDetailSerializer INSTANCE = new WorkResourceDetailSerializer();

	private WorkResourceDetailSerializer() {}

	public static WorkResourceDetailSerializer getInstance() {
		return INSTANCE;
	}

	@Override
	public JsonElement serialize(WorkResourceDetail workResourceDetail, Type type, JsonSerializationContext jsonSerializationContext) {
		final JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("workResourceId", workResourceDetail.getWorkResourceId());
		jsonObject.addProperty("userId", workResourceDetail.getUserId());
		jsonObject.addProperty("workResourceStatusTypeCode", workResourceDetail.getWorkResourceStatusTypeCode());
		jsonObject.addProperty("firstName", workResourceDetail.getFirstName());
		jsonObject.addProperty("lastName", workResourceDetail.getLastName());
		jsonObject.addProperty("companyName", workResourceDetail.getCompanyName());
		jsonObject.addProperty("isAssignedToWork", workResourceDetail.isAssignedToWork());
		jsonObject.addProperty("scheduleConflict", workResourceDetail.hasScheduleConflict());

		if (workResourceDetail.getAppointmentFrom() != null) {
			jsonObject.add("appointmentFrom", jsonSerializationContext.serialize(workResourceDetail.getAppointmentFrom(), Calendar.class));
		}
		if (workResourceDetail.getAppointmentThrough() != null) {
			jsonObject.add("appointmentThrough", jsonSerializationContext.serialize(workResourceDetail.getAppointmentThrough(), Calendar.class));
		}

		jsonObject.addProperty("workPhoneNumber", workResourceDetail.getWorkPhoneNumber());
		jsonObject.addProperty("workPhoneExtension", workResourceDetail.getWorkPhoneExtension());
		jsonObject.addProperty("mobilePhoneNumber", workResourceDetail.getMobilePhoneNumber());

		jsonObject.addProperty("userNumber", workResourceDetail.getUserNumber());
		jsonObject.addProperty("companyId", workResourceDetail.getCompanyId());
		jsonObject.addProperty("questionPending", workResourceDetail.isQuestionPending());
		jsonObject.addProperty("latestNegotiationPending", workResourceDetail.isLatestNegotiationPending());
		jsonObject.addProperty("latestNegotiationExpired", workResourceDetail.isLatestNegotiationExpired());
		jsonObject.addProperty("latestNegotiationDeclined", workResourceDetail.isLatestNegotiationDeclined());
		jsonObject.addProperty("email", workResourceDetail.getEmail());
		jsonObject.addProperty("bestPrice", workResourceDetail.isBestPrice());
		jsonObject.addProperty("targeted", workResourceDetail.isTargeted());
		jsonObject.addProperty("blocked", workResourceDetail.isBlocked());
		jsonObject.addProperty("assignToFirstToAccept", workResourceDetail.isAssignToFirstToAccept());

		if (workResourceDetail.getDistance() != null) {
			jsonObject.addProperty("distance", workResourceDetail.getDistance());
		}
		if (workResourceDetail.getLatitude() != null) {
			jsonObject.addProperty("latitude", workResourceDetail.getLatitude());
		}
		if (workResourceDetail.getLongitude() != null) {
			jsonObject.addProperty("longitude", workResourceDetail.getLongitude());
		}
		if (workResourceDetail.getCompanyName() != null) {
			jsonObject.addProperty("companyName", workResourceDetail.getCompanyName());
		}
		if (workResourceDetail.getWorkPhone() != null) {
			jsonObject.addProperty("workPhone", workResourceDetail.getWorkPhone());
		}
		if (workResourceDetail.getWorkPhoneExtension() != null) {
			jsonObject.addProperty("workPhoneExtension", workResourceDetail.getWorkPhoneExtension());
		}
		if (workResourceDetail.getMobilePhone() != null) {
			jsonObject.addProperty("mobilePhone", workResourceDetail.getMobilePhone());
		}
		if (workResourceDetail.getRating() != null) {
			jsonObject.addProperty("rating", workResourceDetail.getRating());
		}
		if (workResourceDetail.getNumberOfRatings() != null) {
			jsonObject.addProperty("numberOfRatings", workResourceDetail.getNumberOfRatings());
		}
		if (workResourceDetail.getOnTimePercentage() != null) {
			jsonObject.addProperty("onTimePercentage", workResourceDetail.getOnTimePercentage());
		}
		if (workResourceDetail.getDeclinedDate() != null) {
			jsonObject.add("declinedDate", jsonSerializationContext.serialize(workResourceDetail.getDeclinedDate(), Calendar.class));
		}
		if (workResourceDetail.getInvitedOn() != null) {
			jsonObject.add("invitedOn", jsonSerializationContext.serialize(workResourceDetail.getInvitedOn(), Calendar.class));
		}
		if (workResourceDetail.getModifiedOn() != null) {
			jsonObject.add("modifiedOn", jsonSerializationContext.serialize(workResourceDetail.getModifiedOn(), Calendar.class));
		}
		if (workResourceDetail.getJoinedOn() != null) {
			jsonObject.add("joinedOn", jsonSerializationContext.serialize(workResourceDetail.getJoinedOn(), Calendar.class));
		}
		if (workResourceDetail.getAvatarUri() != null) {
			jsonObject.addProperty("avatarUri", workResourceDetail.getAvatarUri());
		}
		if (workResourceDetail.getAvatarUri() != null) {
			jsonObject.addProperty("avatarCdnUri", workResourceDetail.getAvatarCdnUri());
		}
		if (workResourceDetail.getAvatarUri() != null) {
			jsonObject.addProperty("avatarAvailabilityType", workResourceDetail.getAvatarAvailabilityType());
		}
		if (workResourceDetail.getAvatarUri() != null) {
			jsonObject.addProperty("avatarUUID", workResourceDetail.getAvatarUUID());
		}
		if (workResourceDetail.getApplyNegotiationNote() != null) {
			jsonObject.addProperty("applyNegotiationNote", workResourceDetail.getApplyNegotiationNote());
		}
		if (workResourceDetail.getApplyNegotiationSpendLimit() != null) {
			jsonObject.addProperty("applyNegotiationSpendLimit", workResourceDetail.getApplyNegotiationSpendLimit());
		}
		if (workResourceDetail.getApplyNegotiationFee() != null) {
			jsonObject.addProperty("applyNegotiationFee", workResourceDetail.getApplyNegotiationFee());
		}
		if (workResourceDetail.getApplyNegotiationTotalCost() != null) {
			jsonObject.addProperty("applyNegotiationTotalCost", workResourceDetail.getApplyNegotiationTotalCost());
		}
		if (workResourceDetail.getLaneType() != null) {
			jsonObject.addProperty("laneType", workResourceDetail.getLaneType().toString());
		}
		if (isNotEmpty(workResourceDetail.getLabels())) {
			jsonObject.add("labels", jsonSerializationContext.serialize(workResourceDetail.getLabels(), ArrayList.class));
		}
		if (workResourceDetail.getAddress() != null) {
			jsonObject.add("address", jsonSerializationContext.serialize(workResourceDetail.getAddress(), AddressDTO.class));
		}
		if (isNotEmpty(workResourceDetail.getNotes())) {
			jsonObject.add("notes", jsonSerializationContext.serialize(workResourceDetail.getNotes(), ArrayList.class));
		}
		if (workResourceDetail.getApplyNegotiation() != null) {
			jsonObject.add("applyNegotiation", jsonSerializationContext.serialize(workResourceDetail.getApplyNegotiation(), WorkNegotiation.class));
		}
		return jsonObject;
	}
}
