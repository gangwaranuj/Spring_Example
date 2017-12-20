package com.workmarket.service.business.dto;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.AvailabilityType;
import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.dto.AddressDTO;
import com.workmarket.thrift.work.ResourceLabel;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.utility.FileUtilities;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class WorkResourceDetail extends WorkResourceDTO {

	private AddressDTO address;
	private Double distance;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String companyName;
	private String workPhone;
	private String workPhoneExtension;
	private String mobilePhone;
	private Integer rating;
	private Integer numberOfRatings;
	private Double onTimePercentage;
	private Double deliverableOnTimePercentage;
	private Calendar declinedDate;
	private Calendar invitedOn;
	private Calendar modifiedOn;
	private boolean questionPending;
	private boolean latestNegotiationPending;
	private boolean latestNegotiationExpired;
	private boolean latestNegotiationDeclined;
	private boolean scheduleConflict;
	private LaneType laneType;
	private String email;

	private String avatarCdnUri;
	private String avatarUUID;
	private String avatarUri;
	private String avatarAvailabilityType;

	private List<ResourceNote> notes = Lists.newArrayList();
	private List<ResourceLabel> labels = Lists.newArrayList();

	private WorkNegotiation applyNegotiation;
	private String applyNegotiationNote;
	private BigDecimal applyNegotiationSpendLimit;
	private BigDecimal applyNegotiationFee;
	private BigDecimal applyNegotiationTotalCost;

	private boolean assignToFirstToAccept = false;
	private boolean bestPrice = false;
	private boolean targeted = true;
	private Calendar joinedOn;
	private boolean blocked;
	private ResourceScoreCard resourceScoreCard;
	private ResourceScoreCard resourceCompanyScoreCard;
	private DispatcherDTO dispatcher;

	public AddressDTO getAddress() {
		return address;
	}

	public void setAddress(AddressDTO address) {
		this.address = address;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhoneExtension() {
		return workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(Integer numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public Double getOnTimePercentage() {
		return onTimePercentage;
	}

	public void setOnTimePercentage(Double onTimePercentage) {
		this.onTimePercentage = onTimePercentage;
	}

	public Double getDeliverableOnTimePercentage() {
		return deliverableOnTimePercentage;
	}

	public void setDeliverableOnTimePercentage(Double deliverableOnTimePercentage) {
		this.deliverableOnTimePercentage = deliverableOnTimePercentage;
	}

	public String getAddress1() {
		return address.getAddress1();
	}

	public String getAddress2() {
		return address.getAddress2();
	}

	public String getCity() {
		return address.getCity();
	}

	public String getState() {
		return address.getState();
	}

	public String getPostalCode() {
		return address.getPostalCode();
	}

	public String getCountry() {
		return address.getCountry();
	}

	public String getAddressTypeCode() {
		return address.getAddressTypeCode();
	}

	public Calendar getInvitedOn() {
		return invitedOn;
	}

	public void setInvitedOn(Calendar invitedOn) {
		this.invitedOn = invitedOn;
	}

	public Calendar getDeclinedDate() {
		return declinedDate;
	}

	public void setDeclinedDate(Calendar declinedDate) {
		this.declinedDate = declinedDate;
	}

	public Calendar getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Calendar modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public boolean isQuestionPending() {
		return questionPending;
	}

	public void setQuestionPending(boolean questionPending) {
		this.questionPending = questionPending;
	}

	public boolean isLatestNegotiationPending() {
		return latestNegotiationPending;
	}

	public void setLatestNegotiationPending(boolean latestNegotiationPending) {
		this.latestNegotiationPending = latestNegotiationPending;
	}

	public boolean isLatestNegotiationExpired() {
		return latestNegotiationExpired;
	}

	public void setLatestNegotiationExpired(boolean latestNegotiationExpired) {
		this.latestNegotiationExpired = latestNegotiationExpired;
	}

	public boolean isLatestNegotiationDeclined() {
		return latestNegotiationDeclined;
	}

	public void setLatestNegotiationDeclined(boolean latestNegotiationDeclined) {
		this.latestNegotiationDeclined = latestNegotiationDeclined;
	}

	public LaneType getLaneType() {
		return laneType;
	}

	public void setLaneType(LaneType laneType) {
		this.laneType = laneType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatarCdnUri() {
		return avatarCdnUri;
	}

	public void setAvatarCdnUri(String avatarCdnUri) {
		this.avatarCdnUri = avatarCdnUri;
	}

	public String getAvatarUUID() {
		return avatarUUID;
	}

	public void setAvatarUUID(String avatarUUID) {
		this.avatarUUID = avatarUUID;
	}

	public String getAvatarUri() {
		if (isNotBlank(avatarAvailabilityType) && isNotBlank(avatarCdnUri) && isNotBlank(avatarUUID)) {

			AvailabilityType availabilityType = new AvailabilityType(avatarAvailabilityType);
			return availabilityType.getUri(avatarUUID, FileUtilities.createRemoteFileandDirectoryStructor(avatarCdnUri, avatarUUID));
		}
		return "";
	}

	public void setAvatarUri(String avatarUri) {
		this.avatarUri = avatarUri;
	}

	public String getAvatarAvailabilityType() {
		return avatarAvailabilityType;
	}

	public void setAvatarAvailabilityType(String avatarAvailabilityType) {
		this.avatarAvailabilityType = avatarAvailabilityType;
	}

	public List<ResourceNote> getNotes() {
		return notes;
	}

	public void setNotes(List<ResourceNote> notes) {
		this.notes = notes;
	}

	public List<ResourceLabel> getLabels() {
		return labels;
	}

	public void setLabels(List<ResourceLabel> labels) {
		this.labels = labels;
	}

	public WorkNegotiation getApplyNegotiation() {
		return applyNegotiation;
	}

	public void setApplyNegotiation(WorkNegotiation applyNegotiation) {
		this.applyNegotiation = applyNegotiation;
	}

	public String getApplyNegotiationNote() {
		return applyNegotiationNote;
	}

	public void setApplyNegotiationNote(String applyNegotiationNote) {
		this.applyNegotiationNote = applyNegotiationNote;
	}

	public BigDecimal getApplyNegotiationSpendLimit() {
		return applyNegotiationSpendLimit;
	}

	public void setApplyNegotiationSpendLimit(BigDecimal applyNegotiationSpendLimit) {
		this.applyNegotiationSpendLimit = applyNegotiationSpendLimit;
	}

	public BigDecimal getApplyNegotiationFee() {
		return applyNegotiationFee;
	}

	public void setApplyNegotiationFee(BigDecimal applyNegotiationFee) {
		this.applyNegotiationFee = applyNegotiationFee;
	}

	public BigDecimal getApplyNegotiationTotalCost() {
		return applyNegotiationTotalCost;
	}

	public void setApplyNegotiationTotalCost(BigDecimal applyNegotiationTotalCost) {
		this.applyNegotiationTotalCost = applyNegotiationTotalCost;
	}

	public boolean isAssignToFirstToAccept() {
		return assignToFirstToAccept;
	}

	public void setAssignToFirstToAccept(boolean assignToFirstToAccept) {
		this.assignToFirstToAccept = assignToFirstToAccept;
	}

	public boolean isBestPrice() {
		return bestPrice;
	}

	public void setBestPrice(boolean bestPrice) {
		this.bestPrice = bestPrice;
	}

	public boolean isTargeted() {
		return targeted;
	}

	public void setTargeted(boolean targeted) {
		this.targeted = targeted;
	}

	public void setJoinedOn(Calendar joinedOn) {
		this.joinedOn = joinedOn;
	}

	public Calendar getJoinedOn() {
		return joinedOn;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public ResourceScoreCard getResourceCompanyScoreCard() {
		if (resourceCompanyScoreCard == null) {
			this.resourceCompanyScoreCard = new ResourceScoreCard();
		}
		return resourceCompanyScoreCard;
	}

	public void setResourceCompanyScoreCard(ResourceScoreCard resourceCompanyScoreCard) {
		this.resourceCompanyScoreCard = resourceCompanyScoreCard;
	}

	public ResourceScoreCard getResourceScoreCard() {
		if (resourceScoreCard == null) {
			this.resourceScoreCard = new ResourceScoreCard();
		}
		return resourceScoreCard;
	}

	public void setResourceScoreCard(ResourceScoreCard resourceScoreCard) {
		this.resourceScoreCard = resourceScoreCard;
	}

	public boolean hasScheduleConflict() {
		return scheduleConflict;
	}

	public void setScheduleConflict(boolean scheduleConflict) {
		this.scheduleConflict = scheduleConflict;
	}

	public void setDispatcher(DispatcherDTO dispatcher) {
		this.dispatcher = dispatcher;
	}

	public DispatcherDTO getDispatcher() {
		return dispatcher;
	}
}
