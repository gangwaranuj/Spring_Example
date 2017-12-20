package com.workmarket.json.workresource;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.dto.AddressDTO;
import com.workmarket.thrift.work.ResourceLabel;
import com.workmarket.thrift.work.ResourceNote;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;


public class WorkResourceDetailDeserializer implements JsonDeserializer<WorkResourceDetail> {

	private static final WorkResourceDetailDeserializer INSTANCE = new WorkResourceDetailDeserializer();

	private WorkResourceDetailDeserializer() {
	}

	public static WorkResourceDetailDeserializer getInstance() {
		return INSTANCE;
	}

	@Override
	public WorkResourceDetail deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		WorkResourceDetail workResourceDetail = new WorkResourceDetail();

		JsonElement field = jsonObject.get("workResourceId");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkResourceId(field.getAsLong());
		}
		field = jsonObject.get("userId");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setUserId(field.getAsLong());
		}
		field = jsonObject.get("workResourceStatusTypeCode");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkResourceStatusTypeCode(field.getAsString());
		}
		field = jsonObject.get("firstName");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setFirstName(field.getAsString());
		}
		field = jsonObject.get("lastName");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLastName(field.getAsString());
		}
		field = jsonObject.get("companyName");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setCompanyName(field.getAsString());
		}
		field = jsonObject.get("isAssignedToWork");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAssignedToWork(field.getAsBoolean());
		}
		field = jsonObject.get("scheduleConflict");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setScheduleConflict(field.getAsBoolean());
		}
		field = jsonObject.get("appointmentFrom");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAppointmentFrom((Calendar)jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("appointmentThrough");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAppointmentThrough((Calendar)jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("workPhoneNumber");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkPhoneNumber(field.getAsString());
		}
		field = jsonObject.get("workPhoneExtension");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkPhoneExtension(field.getAsString());
		}
		field = jsonObject.get("mobilePhoneNumber");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setMobilePhoneNumber(field.getAsString());
		}
		field = jsonObject.get("userNumber");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setUserNumber(field.getAsString());
		}
		field = jsonObject.get("companyId");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setCompanyId(field.getAsLong());
		}
		field = jsonObject.get("questionPending");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setQuestionPending(field.getAsBoolean());
		}
		field = jsonObject.get("latestNegotiationPending");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLatestNegotiationPending(field.getAsBoolean());
		}
		field = jsonObject.get("latestNegotiationExpired");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLatestNegotiationExpired(field.getAsBoolean());
		}
		field = jsonObject.get("latestNegotiationDeclined");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLatestNegotiationDeclined(field.getAsBoolean());
		}
		field = jsonObject.get("email");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setEmail(field.getAsString());
		}
		field = jsonObject.get("bestPrice");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setBestPrice(field.getAsBoolean());
		}
		field = jsonObject.get("targeted");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setTargeted(field.getAsBoolean());
		}
		field = jsonObject.get("blocked");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setBlocked(field.getAsBoolean());
		}
		field = jsonObject.get("assignToFirstToAccept");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAssignToFirstToAccept(field.getAsBoolean());
		}
		field = jsonObject.get("distance");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setDistance(field.getAsDouble());
		}
		field = jsonObject.get("latitude");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLatitude(field.getAsBigDecimal());
		}
		field = jsonObject.get("longitude");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLongitude(field.getAsBigDecimal());
		}
		field = jsonObject.get("companyName");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setCompanyName(field.getAsString());
		}
		field = jsonObject.get("workPhone");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkPhone(field.getAsString());
		}
		field = jsonObject.get("workPhoneExtension");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setWorkPhoneExtension(field.getAsString());
		}
		field = jsonObject.get("mobilePhone");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setMobilePhone(field.getAsString());
		}
		field = jsonObject.get("rating");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setRating(field.getAsInt());
		}
		field = jsonObject.get("numberOfRatings");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setNumberOfRatings(field.getAsInt());
		}
		field = jsonObject.get("onTimePercentage");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setOnTimePercentage(field.getAsDouble());
		}
		field = jsonObject.get("declinedDate");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setDeclinedDate((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("invitedOn");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setInvitedOn((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("modifiedOn");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setModifiedOn((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("joinedOn");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setJoinedOn((Calendar) jsonDeserializationContext.deserialize(field, Calendar.class));
		}
		field = jsonObject.get("avatarUri");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAvatarUri(field.getAsString());
		}
		field = jsonObject.get("avatarCdnUri");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAvatarCdnUri(field.getAsString());
		}
		field = jsonObject.get("avatarAvailabilityType");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAvatarAvailabilityType(field.getAsString());
		}
		field = jsonObject.get("avatarUUID");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAvatarUUID(field.getAsString());
		}
		field = jsonObject.get("applyNegotiationNote");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setApplyNegotiationNote(field.getAsString());
		}
		field = jsonObject.get("applyNegotiationSpendLimit");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setApplyNegotiationSpendLimit(field.getAsBigDecimal());
		}
		field = jsonObject.get("applyNegotiationFee");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setApplyNegotiationFee(field.getAsBigDecimal());
		}
		field = jsonObject.get("applyNegotiationTotalCost");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setApplyNegotiationTotalCost(field.getAsBigDecimal());
		}
		field = jsonObject.get("laneType");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLaneType(LaneType.valueOf(field.getAsString()));
		}
		field = jsonObject.get("labels");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setLabels(Arrays.asList((ResourceLabel[]) jsonDeserializationContext.deserialize(field, ResourceLabel[].class)));
		}
		field = jsonObject.get("address");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setAddress((AddressDTO)jsonDeserializationContext.deserialize(field, AddressDTO.class));
		}
		field = jsonObject.get("notes");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setNotes(Arrays.asList((ResourceNote[]) jsonDeserializationContext.deserialize(field, ResourceNote[].class)));
		}
		field = jsonObject.get("applyNegotiation");
		if (field != null && !field.isJsonNull()) {
			workResourceDetail.setApplyNegotiation((WorkNegotiation) jsonDeserializationContext.deserialize(field, WorkNegotiation.class));
		}
		return workResourceDetail;
	}
}
