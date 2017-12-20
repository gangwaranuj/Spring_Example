package com.workmarket.service;

import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.changelog.work.*;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.assessment.Link;
import com.workmarket.thrift.core.*;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.LogEntryType;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.SubStatus;
import com.workmarket.utility.StringUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityToObjectFactory {

	@Autowired private PricingService pricingService;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private UserService userService;

	public User newUser(com.workmarket.domains.model.User e) {
		return new User()
			.setId(e.getId())
			.setUserNumber(e.getUserNumber())
			.setName(new Name(e.getFirstName(), e.getLastName()))
			.setEmail(e.getEmail());
	}

	public User newWorkChangeLogActor(WorkChangeLog e) {
		return new User()
				.setId(e.getActorId())
				.setUserNumber(e.getActorUserNumber())
				.setName(new Name(e.getActorFirstName(), e.getActorLastName()))
				.setEmail(e.getActorEmail());
	}

	public Status newStatus(com.workmarket.domains.model.LookupEntity e) {
		return new Status()
			.setCode(e.getCode())
			.setDescription(e.getDescription());
	}

	public SubStatus newSubStatus(WorkSubStatusType e) {
		return new SubStatus()
			.setId(e.getId())
			.setCode(e.getCode())
			.setDescription(e.getDescription())
			.setUserResolvable(e.isUserResolvable())
			.setColorRgb(e.getCustomColorRgb());
	}

	public Status newStatus(com.workmarket.domains.model.ApprovalStatus e) {
		return new Status().setCode(e.name().toLowerCase());
	}

	public Address newAddress(com.workmarket.domains.model.Address e) {
		Address address = new Address();
		address
			.setAddressLine1(e.getAddress1())
			.setAddressLine2(e.getAddress2())
			.setCity(e.getCity())
			.setState(e.getState().getShortName())
			.setZip(e.getPostalCode())
			.setCountry(e.getCountry().getId())
			.setType(e.getAddressType().getCode());
		if (e.getLocationType() != null) {
			address.setLocationType(e.getLocationType().getId());
		}
		if (e.getLatitude() != null && e.getLongitude() != null) {
			address.setPoint(new GeoPoint(e.getLatitude().doubleValue(), e.getLongitude().doubleValue()));
		}
		return address;
	}

	public Location newLocation(com.workmarket.domains.model.Location e) {
		return new Location()
			.setId(e.getId())
			.setName(e.getName())
			.setNumber(e.getLocationNumber())
			.setInstructions(e.getInstructions())
			.setAddress(e.getAddress() == null ? null : newAddress(e.getAddress()));
	}

	public Schedule newSchedule(com.workmarket.domains.model.DateRange r) {
		Schedule s = new Schedule();
		s.setRange(r.isRange());
		if (r.getFrom() != null)
			s.setFrom(r.getFrom().getTimeInMillis());
		if (r.getThrough() != null)
			s.setThrough(r.getThrough().getTimeInMillis());
		return s;
	}

	public Asset newAsset(com.workmarket.domains.model.asset.Asset a) {
		return new Asset()
			.setId(a.getId())
			.setUuid(a.getUUID())
			.setName(StringUtilities.urlToFilenameValidateAndDecode(a.getName())) // NOTE Some of these were incorrectly saved as URL encoded string; URL decode required
			.setDescription(a.getDescription())
			.setMimeType(a.getMimeType())
			.setCreatedOn(a.getCreatedOn().getTimeInMillis())
			.setUri(a.getUri());
	}

	public PricingStrategy newPricing(com.workmarket.domains.model.pricing.PricingStrategy pricing) {
		PricingStrategy tpricing = new PricingStrategy()
				.setId(pricing.getId())
				.setType(pricing.getFullPricingStrategy().getPricingStrategyType())
				.setMaxSpendLimit(pricingService.calculateMaximumResourceCost(pricing).doubleValue());

		switch (pricing.getFullPricingStrategy().getPricingStrategyType()) {
		case FLAT:
			if (pricing.getFullPricingStrategy().getFlatPrice() != null)
				tpricing.setFlatPrice(pricing.getFullPricingStrategy().getFlatPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getMaxFlatPrice() != null)
				tpricing.setMaxFlatPrice(pricing.getFullPricingStrategy().getMaxFlatPrice().doubleValue());
			break;
		case PER_HOUR:
			if (pricing.getFullPricingStrategy().getPerHourPrice() != null)
				tpricing.setPerHourPrice(pricing.getFullPricingStrategy().getPerHourPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getMaxNumberOfHours() != null)
				tpricing.setMaxNumberOfHours(pricing.getFullPricingStrategy().getMaxNumberOfHours().doubleValue());
			break;
		case PER_UNIT:
			if (pricing.getFullPricingStrategy().getPerUnitPrice() != null)
				tpricing.setPerUnitPrice(pricing.getFullPricingStrategy().getPerUnitPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getMaxNumberOfUnits() != null)
				tpricing.setMaxNumberOfUnits(pricing.getFullPricingStrategy().getMaxNumberOfUnits().doubleValue());
			break;
		case BLENDED_PER_HOUR:
			if (pricing.getFullPricingStrategy().getInitialPerHourPrice() != null)
				tpricing.setInitialPerHourPrice(pricing.getFullPricingStrategy().getInitialPerHourPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getInitialNumberOfHours() != null)
				tpricing.setInitialNumberOfHours(pricing.getFullPricingStrategy().getInitialNumberOfHours().doubleValue());
			if (pricing.getFullPricingStrategy().getAdditionalPerHourPrice() != null)
				tpricing.setAdditionalPerHourPrice(pricing.getFullPricingStrategy().getAdditionalPerHourPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getMaxBlendedNumberOfHours() != null)
				tpricing.setMaxBlendedNumberOfHours(pricing.getFullPricingStrategy().getMaxBlendedNumberOfHours().doubleValue());
			break;
		case BLENDED_PER_UNIT:
			if (pricing.getFullPricingStrategy().getInitialPerUnitPrice() != null)
				tpricing.setInitialPerUnitPrice(pricing.getFullPricingStrategy().getInitialPerUnitPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getInitialNumberOfUnits() != null)
				tpricing.setInitialNumberOfUnits(pricing.getFullPricingStrategy().getInitialNumberOfUnits().doubleValue());
			if (pricing.getFullPricingStrategy().getAdditionalPerUnitPrice() != null)
				tpricing.setAdditionalPerUnitPrice(pricing.getFullPricingStrategy().getAdditionalPerUnitPrice().doubleValue());
			if (pricing.getFullPricingStrategy().getMaxBlendedNumberOfUnits() != null)
				tpricing.setMaxBlendedNumberOfUnits(pricing.getFullPricingStrategy().getMaxBlendedNumberOfUnits().doubleValue());
			break;
		case INTERNAL:
		default:
		}

		if (pricing.getFullPricingStrategy().getAdditionalExpenses() != null)
			tpricing.setAdditionalExpenses(pricing.getFullPricingStrategy().getAdditionalExpenses().doubleValue());
		if (pricing.getFullPricingStrategy().getBonus() != null)
			tpricing.setBonus(pricing.getFullPricingStrategy().getBonus().doubleValue());
		if (pricing.getFullPricingStrategy().getOverridePrice() != null)
			tpricing.setOverridePrice(pricing.getFullPricingStrategy().getOverridePrice().doubleValue());

		return tpricing;
	}

	public Company newCompany(com.workmarket.domains.model.Company company) {
		Company tcompany = new Company()
			.setId(company.getId())
			.setName(company.getEffectiveName());

		CompanyAssetAssociation companyAvatars = companyAssetAssociationDAO.findCompanyAvatars(company.getId());
		if (companyAvatars != null) {
			com.workmarket.domains.model.asset.Asset avatarOriginal = companyAvatars.getAsset();
			com.workmarket.domains.model.asset.Asset avatarLarge = companyAvatars.getTransformedLargeAsset();
			com.workmarket.domains.model.asset.Asset avatarSmall = companyAvatars.getTransformedSmallAsset();

			if (avatarOriginal != null) {
				tcompany.setAvatarOriginal(newAsset(avatarOriginal));
			}
			if (avatarLarge != null) {
				tcompany.setAvatarLarge(newAsset(avatarLarge));
			}
			if (avatarSmall != null) {
				tcompany.setAvatarSmall(newAsset(avatarSmall));
			}
		}

		return tcompany;
	}

	public LogEntryType newLogEntryType(WorkChangeLog log) {
		if (log instanceof WorkCreatedChangeLog)
			return LogEntryType.WORK_CREATED;
		if (log instanceof WorkUpdatedChangeLog)
			return LogEntryType.WORK_UPDATED;
		if (log instanceof WorkQuestionAskedChangeLog)
			return LogEntryType.WORK_QUESTION_ASKED;
		if (log instanceof WorkQuestionAnsweredChangeLog)
			return LogEntryType.WORK_QUESTION_ANSWERED;
		if (log instanceof WorkStatusChangeChangeLog)
			return LogEntryType.WORK_STATUS_CHANGE;
		if (log instanceof WorkResourceStatusChangeChangeLog)
			return LogEntryType.WORK_RESOURCE_STATUS_CHANGE;
		if (log instanceof WorkNoteCreatedChangeLog)
			return LogEntryType.WORK_NOTE_CREATED;
		if (log instanceof WorkPropertyChangeLog)
			return LogEntryType.WORK_PROPERTY;
		if (log instanceof WorkNegotiationStatusChangeChangeLog)
			return LogEntryType.WORK_NEGOTIATION_STATUS_CHANGE;
		if (log instanceof WorkNegotiationRequestedChangeLog)
			return LogEntryType.WORK_NEGOTIATION_REQUESTED;
		if (log instanceof WorkNegotiationExpiredChangeLog)
			return LogEntryType.WORK_NEGOTIATION_EXPIRED;
		if (log instanceof WorkRescheduleRequestedChangeLog)
			return LogEntryType.WORK_RESCHEDULE_REQUESTED;
		if (log instanceof WorkRescheduleAutoApprovedChangeLog)
			return LogEntryType.WORK_RESCHEDULE_AUTO_APPROVED;
		if (log instanceof WorkRescheduleStatusChangeChangeLog)
			return LogEntryType.WORK_RESCHEDULE_STATUS_CHANGE;
		if (log instanceof WorkSubStatusChangeChangeLog)
			return LogEntryType.WORK_SUB_STATUS_CHANGE;
		if (log instanceof WorkCloseoutAttachmentUploadedChangeLog)
			return LogEntryType.WORK_CLOSEOUT_ATTACHMENT_ADD;
		return LogEntryType.WORK;
	}

	public Note newNote(com.workmarket.domains.model.note.Note note) {
		com.workmarket.domains.model.User creator = userService.findUserById(note.getCreatorId());
		return new Note()
				.setCreatedOn(note.getCreatedOn().getTimeInMillis())
				.setCreator(this.newUser(creator))
				.setId(note.getId())
				.setText(note.getContent())
				.setIsPrivate(note.getIsPrivate())
				.setIsPrivileged(note.getIsPrivileged());
	}

	public Link newLink(com.workmarket.domains.model.asset.Link link) {
		Link newLink = new Link();
		newLink.setName(link.getName());
		newLink.setRemoteUri(link.getRemoteUri());
		newLink.setAvailabilityTypeCode(link.getAvailability().getCode());
		return newLink;
	}
}
