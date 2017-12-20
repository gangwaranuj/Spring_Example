package com.workmarket.api.v2.employer.uploads.services;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.model.ContactDTO;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.api.v2.employer.assignments.services.AbstractAssignmentUseCase;
import com.workmarket.api.v2.employer.uploads.models.CellDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.model.part.PartDistributionMethodType;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.workmarket.domains.work.service.upload.WorkUploadColumn.*;

@Service
public class CsvConversionServiceImpl implements CsvConversionService {

	@Override
	public AssignmentDTO convert(Map<String, CellDTO> data) {
		AssignmentDTO.Builder assignment = new AssignmentDTO.Builder();
		return parseAssignment(data, assignment).build();
	}

	@Override
	public AssignmentDTO convert(Map<String, CellDTO> data, AssignmentDTO assignment) {
		return parseAssignment(data, new AssignmentDTO.Builder(assignment)).build();
	}

	private AssignmentDTO.Builder parseAssignment(Map<String, CellDTO> rowData, AssignmentDTO.Builder assignmentBuilder) {

		// If a template was chosen, this assignment builder will have template values, otherwise it will be empty
		// Therefore we want to append to this assignment, only overwriting if the CSV has a value
		AssignmentDTO assignment = assignmentBuilder.build();

		return parseGeneralAssignment(rowData, assignmentBuilder)
			.setLocation(parseLocation(rowData, assignment))
			.setPricing(parsePricing(rowData, assignment))
			.setRouting(parseRouting(rowData, assignment))
			.setShipmentGroup(parseShipments(rowData, assignment))
			.setSchedule(parseSchedule(rowData, assignment))
			.setCustomFieldGroups(parseCustomFieldGroups(rowData, assignment));
	}

	private AssignmentDTO.Builder parseGeneralAssignment(Map<String, CellDTO> rowData, AssignmentDTO.Builder assignment) {
		String title = getValue(rowData, TITLE.getUploadColumnName());
		String description = getValue(rowData, DESCRIPTION.getUploadColumnName());
		String instructions = getValue(rowData, INSTRUCTIONS.getUploadColumnName());
		String skills = getValue(rowData, DESIRED_SKILLS.getUploadColumnName());
		String uniqueExternalId = getValue(rowData, UNIQUE_EXTERNAL_ID.getUploadColumnName());
		String supportContactUserNumber = getValue(rowData, SUPPORT_CONTACT_USER_NUMBER.getUploadColumnName());
		String ownerUserNumber = getValue(rowData, OWNER_USER_NUMBER.getUploadColumnName());
		String industryId = getValue(rowData, INDUSTRY_ID.getUploadColumnName());

		if (StringUtils.isNotBlank(title)) {
			assignment.setTitle(title);
		}
		if (StringUtils.isNotBlank(description)) {
			assignment.setDescription(description);
		}
		if (StringUtils.isNotBlank(instructions)) {
			assignment.setInstructions(instructions);
		}
		if (StringUtils.isNotBlank(skills)) {
			assignment.setSkills(skills);
		}
		if (StringUtils.isNotBlank(uniqueExternalId)) {
			assignment.setUniqueExternalId(uniqueExternalId);
		}
		if (StringUtils.isNotBlank(supportContactUserNumber)) {
			assignment.setSupportContactId(supportContactUserNumber);
		}
		if (StringUtils.isNotBlank(ownerUserNumber)) {
			assignment.setOwnerId(ownerUserNumber);
		}
		if (StringUtils.isNotBlank(industryId)) {
			assignment.setIndustryId(Long.parseLong(industryId));
		}

		return assignment;
	}

	private ShipmentGroupDTO.Builder parseShipments(Map<String, CellDTO> rowData, AssignmentDTO assignmentDTO) {
		List<ShipmentDTO.Builder> shipments = Lists.newArrayList();
		ShipmentGroupDTO.Builder shipmentGroupBuilder = new ShipmentGroupDTO.Builder(assignmentDTO.getShipmentGroup());
		ShipmentDTO.Builder returnShipment = parseReturnShipment(rowData, shipmentGroupBuilder);
		if (returnShipment != null) {
			shipments.add(returnShipment);
			shipmentGroupBuilder.setReturnShipment(true);
		}
		ShipmentDTO.Builder pickupShipment = parsePickupShipment(rowData, shipmentGroupBuilder);
		if (pickupShipment != null) {
			shipments.add(pickupShipment);
		}
		if(shipments.isEmpty()) {
			for(ShipmentDTO shipment : assignmentDTO.getShipmentGroup().getShipments()) {
				shipments.add(new ShipmentDTO.Builder(shipment));
			}
		}

		shipmentGroupBuilder.setShipments(shipments);
		return shipmentGroupBuilder;
	}

	private ShipmentDTO.Builder parseReturnShipment(Map<String, CellDTO> rowData, ShipmentGroupDTO.Builder shipmentGroupBuilder) {
		ShipmentDTO.Builder shipment = null;

		String returnLocationName = getValue(rowData, RETURN_LOCATION_NAME.getUploadColumnName());
		String returnLocationNumber = getValue(rowData, RETURN_LOCATION_NUMBER.getUploadColumnName());
		String returnLocationAddress1 = getValue(rowData, RETURN_LOCATION_ADDRESS_1.getUploadColumnName());
		String returnLocationAddress2 = getValue(rowData, RETURN_LOCATION_ADDRESS_2.getUploadColumnName());
		String returnLocationCity = getValue(rowData, RETURN_LOCATION_CITY.getUploadColumnName());
		String returnLocationState = getValue(rowData, RETURN_LOCATION_STATE.getUploadColumnName());
		String returnLocationZip = getValue(rowData, RETURN_LOCATION_POSTAL_CODE.getUploadColumnName());
		String returnLocationCountry = getValue(rowData, RETURN_LOCATION_COUNTRY.getUploadColumnName());
		String returnLocationType = getValue(rowData, RETURN_LOCATION_TYPE.getUploadColumnName());
		String returnTrackingNumber = getValue(rowData, RETURN_TRACKING_NUMBER.getUploadColumnName());
		String returnPartValue = getValue(rowData, RETURN_PART_VALUE.getUploadColumnName());

		if (!StringUtils.isEmpty(returnLocationName) || !StringUtils.isEmpty(returnLocationNumber) ||
			!StringUtils.isEmpty(returnLocationAddress1) || !StringUtils.isEmpty(returnLocationAddress2) ||
			!StringUtils.isEmpty(returnLocationCity) || !StringUtils.isEmpty(returnLocationState) ||
			!StringUtils.isEmpty(returnLocationZip) || !StringUtils.isEmpty(returnLocationCountry) ||
			!StringUtils.isEmpty(returnLocationType) || !StringUtils.isEmpty(returnTrackingNumber) ||
			!StringUtils.isEmpty(returnPartValue)) {

			shipmentGroupBuilder.setReturnAddress(
				new LocationDTO.Builder()
					.setName(returnLocationName)
					.setNumber(returnLocationNumber)
					.setAddressLine1(returnLocationAddress1)
					.setAddressLine2(returnLocationAddress2)
					.setCity(returnLocationCity)
					.setState(returnLocationState)
					.setZip(returnLocationZip)
					.setCountry(returnLocationCountry))
				.setReturnShipment(true);

			shipment = new ShipmentDTO.Builder()
				.setName("Return part") // TODO: Tim - remove this when we add a return part name field in bulk upload
				.setTrackingNumber(returnTrackingNumber);

			if (!StringUtils.isEmpty(returnPartValue)) {
				shipment.setValue(new BigDecimal(returnPartValue.replaceAll(",", "")));
			}

			String shippingProvider = getValue(rowData, RETURN_SHIPPING_PROVIDER.getUploadColumnName());
			if (!StringUtils.isEmpty(shippingProvider)) {
				shipment.setShippingProvider(ShippingProvider.getShippingProvider(shippingProvider));
			}
		}
		return shipment;
	}

	private ShipmentDTO.Builder parsePickupShipment(Map<String, CellDTO> rowData, ShipmentGroupDTO.Builder shipmentGroupBuilder) {
		ShipmentDTO.Builder shipment = new ShipmentDTO.Builder();

		String suppliedByWorker = getValue(rowData, SUPPLIED_BY_RESOURCE.getUploadColumnName());
		String distributionMethod = getValue(rowData, DISTRIBUTION_METHOD.getUploadColumnName());

		String pickupLocationName = getValue(rowData, PICKUP_LOCATION_NAME.getUploadColumnName());
		String pickupLocationNumber = getValue(rowData, PICKUP_LOCATION_NUMBER.getUploadColumnName());
		String pickupLocationAddress1 = getValue(rowData, PICKUP_LOCATION_ADDRESS_1.getUploadColumnName());
		String pickupLocationAddress2 = getValue(rowData, PICKUP_LOCATION_ADDRESS_2.getUploadColumnName());
		String pickupLocationCity = getValue(rowData, PICKUP_LOCATION_CITY.getUploadColumnName());
		String pickupLocationState = getValue(rowData, PICKUP_LOCATION_STATE.getUploadColumnName());
		String pickupLocationZip = getValue(rowData, PICKUP_LOCATION_POSTAL_CODE.getUploadColumnName());
		String pickupLocationCountry = getValue(rowData, PICKUP_LOCATION_COUNTRY.getUploadColumnName());
		String pickupLocationType = getValue(rowData, PICKUP_LOCATION_TYPE.getUploadColumnName());
		String pickupTrackingNumber = getValue(rowData, PICKUP_TRACKING_NUMBER.getUploadColumnName());
		String pickupPartValue = getValue(rowData, PICKUP_PART_VALUE.getUploadColumnName());
		String shippingProvider = getValue(rowData, PICKUP_SHIPPING_PROVIDER.getUploadColumnName());

		shipment
			.setTrackingNumber(pickupTrackingNumber)
			.setName("Part"); // TODO: Tim - remove this when we add a return part name field in bulk upload

		if (!StringUtils.isEmpty(pickupPartValue)) {
			shipment.setValue(new BigDecimal(pickupPartValue.replaceAll(",", "")));
		}
		if (!StringUtils.isEmpty(shippingProvider)) {
			shipment.setShippingProvider(ShippingProvider.getShippingProvider(shippingProvider));
		}
		if (!StringUtils.isEmpty(pickupLocationName) || !StringUtils.isEmpty(pickupLocationNumber) ||
			!StringUtils.isEmpty(pickupLocationAddress1) || !StringUtils.isEmpty(pickupLocationAddress2) ||
			!StringUtils.isEmpty(pickupLocationCity) || !StringUtils.isEmpty(pickupLocationState) ||
			!StringUtils.isEmpty(pickupLocationZip) || !StringUtils.isEmpty(pickupLocationCountry)) {

			shipmentGroupBuilder.setShipToAddress(new LocationDTO.Builder()
				.setName(pickupLocationName)
				.setNumber(pickupLocationNumber)
				.setAddressLine1(pickupLocationAddress1)
				.setAddressLine2(pickupLocationAddress2)
				.setCity(pickupLocationCity)
				.setState(pickupLocationState)
				.setZip(pickupLocationZip)
				.setCountry(pickupLocationCountry))
				.setSuppliedByWorker(false)
				.setShippingDestinationType(ShippingDestinationType.PICKUP);
		} else if (!StringUtils.isEmpty(distributionMethod)) {
			ShippingDestinationType type = ShippingDestinationType.convertFromDistributionMethod(
				PartDistributionMethodType.valueOf(distributionMethod.toUpperCase())
			);
			shipmentGroupBuilder.setShippingDestinationType(type);
			shipmentGroupBuilder.setSuppliedByWorker(false);
		} else if (!StringUtils.isEmpty(suppliedByWorker)) {
			shipmentGroupBuilder.setSuppliedByWorker(true);
		} else {
			shipment = null;
		}

		return shipment;
	}

	private RoutingDTO.Builder parseRouting(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		String userNumberCSV = getValue(rowData, USER_NUMBER.getUploadColumnName());
		if (!StringUtils.isEmpty(userNumberCSV)) {
			return new RoutingDTO.Builder()
				.setResourceNumbers(Sets.newLinkedHashSet(Arrays.asList(userNumberCSV.trim().split("[\\s,]+"))));
		}
		else {
			return new RoutingDTO.Builder(assignment.getRouting());
		}
	}

	private LocationDTO.Builder parseLocation(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		LocationDTO.Builder builder = new LocationDTO.Builder(assignment.getLocation());
		String addressLine1 = getValue(rowData, LOCATION_ADDRESS_1.getUploadColumnName());
		String addressLine2 = getValue(rowData, LOCATION_ADDRESS_2.getUploadColumnName());
		String city = getValue(rowData, LOCATION_CITY.getUploadColumnName());
		String state = getValue(rowData, LOCATION_STATE.getUploadColumnName());
		String zip = getValue(rowData, LOCATION_POSTAL_CODE.getUploadColumnName());
		String country = getValue(rowData, LOCATION_COUNTRY.getUploadColumnName());
		String name = getValue(rowData, LOCATION_NAME.getUploadColumnName());
		String number = getValue(rowData, LOCATION_NUMBER.getUploadColumnName());

		if(StringUtils.isNotBlank(addressLine1)) {
			builder.setAddressLine1(addressLine1);
		}
		if(StringUtils.isNotBlank(addressLine2)) {
			builder.setAddressLine2(addressLine2);
		}
		if(StringUtils.isNotBlank(city)) {
			builder.setCity(city);
		}
		if(StringUtils.isNotBlank(state)) {
			builder.setState(state);
		}
		if(StringUtils.isNotBlank(zip)) {
			builder.setZip(zip);
		}
		if(StringUtils.isNotBlank(country)) {
			builder.setCountry(country);
		}
		if(StringUtils.isNotBlank(name)) {
			builder.setName(name);
		}
		if(StringUtils.isNotBlank(number)) {
			builder.setNumber(number);
		}

		Optional<ContactDTO.Builder> contactBuilder = parseLocationContact(rowData, assignment);
		if(contactBuilder.isPresent()) {
			builder.setContact(contactBuilder.get());
		}
		Optional<ContactDTO.Builder> secondaryContactBuilder = parseLocationSecondaryContact(rowData, assignment);
		if(secondaryContactBuilder.isPresent()) {
			builder.setSecondaryContact(secondaryContactBuilder.get());
		}
		return builder;
	}

	private Optional<ContactDTO.Builder> parseLocationContact(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		String email = getValue(rowData, CONTACT_EMAIL.getUploadColumnName());
		String firstName = getValue(rowData, CONTACT_FIRST_NAME.getUploadColumnName());
		String lastName = getValue(rowData, CONTACT_LAST_NAME.getUploadColumnName());
		String workPhone = getValue(rowData, CONTACT_PHONE.getUploadColumnName());
		String workPhoneExtension = getValue(rowData, CONTACT_PHONE_EXTENSION.getUploadColumnName());

		return parseContactInfo(assignment.getLocation().getContact(), email, firstName, lastName, workPhone, workPhoneExtension);
	}

	private Optional<ContactDTO.Builder> parseLocationSecondaryContact(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		String email = getValue(rowData, SECONDARY_CONTACT_EMAIL.getUploadColumnName());
		String firstName = getValue(rowData, SECONDARY_CONTACT_FIRST_NAME.getUploadColumnName());
		String lastName = getValue(rowData, SECONDARY_CONTACT_LAST_NAME.getUploadColumnName());
		String workPhone = getValue(rowData, SECONDARY_CONTACT_PHONE.getUploadColumnName());
		String workPhoneExtension = getValue(rowData, SECONDARY_CONTACT_PHONE_EXTENSION.getUploadColumnName());
		return parseContactInfo(assignment.getLocation().getSecondaryContact(), email, firstName, lastName, workPhone, workPhoneExtension);
	}

	private Optional<ContactDTO.Builder> parseContactInfo(ContactDTO contact, String email, String firstName,
		String lastName, String phone, String phoneExt) {

		Optional<ContactDTO.Builder> builder = Optional.absent();
		if(contact != null) {
			builder = Optional.of(new ContactDTO.Builder(contact));
		}
		else if(hasContactInfo(email, firstName, lastName, phone, phoneExt)) {
			builder = Optional.of(new ContactDTO.Builder());
		}

		if(builder.isPresent()) {
			if (StringUtils.isNotBlank(email)) {
				builder.get().setEmail(email);
			}
			if (StringUtils.isNotBlank(firstName)) {
				builder.get().setFirstName(firstName);
			}
			if (StringUtils.isNotBlank(lastName)) {
				builder.get().setLastName(lastName);
			}
			if (StringUtils.isNotBlank(phone)) {
				builder.get().setWorkPhone(phone);
			}
			if (StringUtils.isNotBlank(phoneExt)) {
				builder.get().setWorkPhoneExtension(phoneExt);
			}
		}
		return builder;
	}

	private boolean hasContactInfo(String email, String firstName, String lastName, String phone, String phoneExt) {
		return StringUtils.isNotBlank(email) || StringUtils.isNotBlank(firstName) ||
			StringUtils.isNotBlank(lastName) || StringUtils.isNotBlank(phone) ||
			StringUtils.isNotBlank(phoneExt);
	}

	private ScheduleDTO.Builder parseSchedule(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		ScheduleDTO.Builder schedule = new ScheduleDTO.Builder(assignment.getSchedule());

		String startDateTimeString = getValue(rowData, START_DATE_TIME.getUploadColumnName());
		if (!StringUtils.isEmpty(startDateTimeString)) {
			Calendar startDateTime = DateUtilities.getCalendarFromDateTimeString(startDateTimeString, Constants.DEFAULT_TIMEZONE);
			startDateTimeString = DateUtilities.format(AbstractAssignmentUseCase.DATE_TIME_FORMAT, startDateTime);
			schedule.setFrom(startDateTimeString);
		}

		String endDateTimeString = getValue(rowData, END_DATE_TIME.getUploadColumnName());
		if (!StringUtils.isEmpty(endDateTimeString)) {
			Calendar endDateTime = DateUtilities.getCalendarFromDateTimeString(endDateTimeString, Constants.DEFAULT_TIMEZONE);
			endDateTimeString = DateUtilities.format(AbstractAssignmentUseCase.DATE_TIME_FORMAT, endDateTime);
			schedule.setThrough(endDateTimeString);
		}

		String startDateString = getValue(rowData, START_DATE.getUploadColumnName());
		String startTimeString = getValue(rowData, START_TIME.getUploadColumnName());
		if (!StringUtils.isEmpty(startDateString) && !StringUtils.isEmpty(startTimeString)) {
			Calendar startDateTime = DateUtilities.getCalendarFromDateTimeString(startDateString, startTimeString, Constants.DEFAULT_TIMEZONE);
			startDateTimeString = DateUtilities.format(AbstractAssignmentUseCase.DATE_TIME_FORMAT, startDateTime);
			schedule.setFrom(startDateTimeString);
		}

		String endDateString = getValue(rowData, END_DATE.getUploadColumnName());
		String endTimeString = getValue(rowData, END_TIME.getUploadColumnName());
		if (!StringUtils.isEmpty(endDateString) && !StringUtils.isEmpty(endTimeString)) {
			Calendar endDateTime = DateUtilities.getCalendarFromDateTimeString(endDateString, endTimeString, Constants.DEFAULT_TIMEZONE);
			endDateTimeString = DateUtilities.format(AbstractAssignmentUseCase.DATE_TIME_FORMAT, endDateTime);
			schedule.setThrough(endDateTimeString);
		}
		return schedule;
	}


	private PricingDTO.Builder parsePricing(Map<String, CellDTO> rowData, AssignmentDTO assignment) {
		// Flat Price
		String flatPriceClientFee = getValue(rowData, FLAT_PRICE_CLIENT_FEE.getUploadColumnName());
		String flatPriceResourceFee = getValue(rowData, FLAT_PRICE_RESOURCE_FEE.getUploadColumnName());
		if (!StringUtils.isEmpty(flatPriceClientFee) || !StringUtils.isEmpty(flatPriceResourceFee)) {
			return parsePricingFlat(rowData);
		}

		// Per Hour Price
		String perHourPriceClientFee = getValue(rowData, PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String perHourPriceResourceFee = getValue(rowData, PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());

		if (!StringUtils.isEmpty(perHourPriceClientFee) || !StringUtils.isEmpty(perHourPriceResourceFee)) {
			return parsePricingPerHour(rowData);
		}

		// Blended Per Hour Price
		String initialPerHourPriceClientFee = getValue(rowData, INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String initialPerHourPriceResourceFee = getValue(rowData, INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());

		if (!StringUtils.isEmpty(initialPerHourPriceClientFee) || !StringUtils.isEmpty(initialPerHourPriceResourceFee)) {
			return parsePricingBlended(rowData);
		}

		// Per Unit Price
		String perUnitPriceClientFee = getValue(rowData, PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName());
		String perUnitPriceResourceFee = getValue(rowData, PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName());
		if (!StringUtils.isEmpty(perUnitPriceClientFee) || !StringUtils.isEmpty(perUnitPriceResourceFee)) {
			return parsePricingUnit(rowData);
		}
		return new PricingDTO.Builder(assignment.getPricing());
	}

	private PricingDTO.Builder parsePricingFlat(Map<String, CellDTO> rowData) {
		PricingDTO.Builder pricingDTO = new PricingDTO.Builder()
			.setType(PricingStrategyType.FLAT.name());

		// Flat Price
		String flatPriceClientFee = getValue(rowData, FLAT_PRICE_CLIENT_FEE.getUploadColumnName());
		String flatPriceResourceFee = getValue(rowData, FLAT_PRICE_RESOURCE_FEE.getUploadColumnName());
		if (!StringUtils.isEmpty(flatPriceClientFee)) {
			pricingDTO.setFlatPrice(Double.parseDouble(flatPriceClientFee));
		}
		if (!StringUtils.isEmpty(flatPriceResourceFee)) {
			pricingDTO.setFlatPrice(Double.parseDouble(flatPriceResourceFee));
		}
		return pricingDTO;
	}

	private PricingDTO.Builder parsePricingPerHour(Map<String, CellDTO> rowData) {
		PricingDTO.Builder pricingDTO = new PricingDTO.Builder()
			.setType(PricingStrategyType.PER_HOUR.name());

		// Per Hour Price
		String perHourPriceClientFee = getValue(rowData, PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String perHourPriceResourceFee = getValue(rowData, PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxNumberOfHours = getValue(rowData, MAX_NUMBER_OF_HOURS.getUploadColumnName());

		if (!StringUtils.isEmpty(perHourPriceClientFee)) {
			pricingDTO.setPerHourPrice(Double.parseDouble(perHourPriceClientFee));
		}
		if (!StringUtils.isEmpty(perHourPriceResourceFee)) {
			pricingDTO.setPerHourPrice(Double.parseDouble(perHourPriceResourceFee));
		}
		if (!StringUtils.isEmpty(maxNumberOfHours)) {
			pricingDTO.setMaxNumberOfHours(Double.parseDouble(maxNumberOfHours));
		}
		return pricingDTO;
	}

	private PricingDTO.Builder parsePricingBlended(Map<String, CellDTO> rowData) {
		PricingDTO.Builder pricingDTO = new PricingDTO.Builder()
			.setType(PricingStrategyType.BLENDED_PER_HOUR.name());

		// Blended Per Hour Price
		String initialPerHourPriceClientFee = getValue(rowData, INITIAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String initialPerHourPriceResourceFee = getValue(rowData, INITIAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxNumberOfHoursAtInitialPrice = getValue(rowData, MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE.getUploadColumnName());
		String maxNumberOfHoursAtAdditionalPrice = getValue(rowData, MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE.getUploadColumnName());
		String additionalPerHourPriceClientFee = getValue(rowData, ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE.getUploadColumnName());
		String additionalPerHourPriceResourceFee = getValue(rowData, ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE.getUploadColumnName());

		if (!StringUtils.isEmpty(initialPerHourPriceClientFee)) {
			pricingDTO.setInitialPerHourPrice(Double.parseDouble(initialPerHourPriceClientFee));
		}
		if (!StringUtils.isEmpty(initialPerHourPriceResourceFee)) {
			pricingDTO.setInitialPerHourPrice(Double.parseDouble(initialPerHourPriceResourceFee));
		}
		if (!StringUtils.isEmpty(maxNumberOfHoursAtInitialPrice)) {
			pricingDTO.setInitialNumberOfHours(Double.parseDouble(maxNumberOfHoursAtInitialPrice));
		}
		if (!StringUtils.isEmpty(additionalPerHourPriceClientFee)) {
			pricingDTO.setAdditionalPerHourPrice(Double.parseDouble(additionalPerHourPriceClientFee));
		}
		if (!StringUtils.isEmpty(additionalPerHourPriceResourceFee)) {
			pricingDTO.setAdditionalPerHourPrice(Double.parseDouble(additionalPerHourPriceResourceFee));
		}
		if (!StringUtils.isEmpty(maxNumberOfHoursAtAdditionalPrice)) {
			pricingDTO.setMaxBlendedNumberOfHours(Double.parseDouble(maxNumberOfHoursAtAdditionalPrice));
		}
		return pricingDTO;
	}

	private PricingDTO.Builder parsePricingUnit(Map<String, CellDTO> rowData) {
		PricingDTO.Builder pricingDTO = new PricingDTO.Builder()
			.setType(PricingStrategyType.PER_UNIT.name());

		// Per Unit Price
		String perUnitPriceClientFee = getValue(rowData, PER_UNIT_PRICE_CLIENT_FEE.getUploadColumnName());
		String perUnitPriceResourceFee = getValue(rowData, PER_UNIT_PRICE_RESOURCE_FEE.getUploadColumnName());
		String maxNumberOfUnits = getValue(rowData, MAX_NUMBER_OF_UNITS.getUploadColumnName());
		if (!StringUtils.isEmpty(perUnitPriceClientFee)) {
			pricingDTO.setPerUnitPrice(Double.parseDouble(perUnitPriceClientFee));
		}
		if (!StringUtils.isEmpty(perUnitPriceResourceFee)) {
			pricingDTO.setPerUnitPrice(Double.parseDouble(perUnitPriceResourceFee));
		}
		if (!StringUtils.isEmpty(maxNumberOfUnits)) {
			pricingDTO.setMaxNumberOfUnits(Double.parseDouble(maxNumberOfUnits));
		}
		return pricingDTO;
	}

	private Set<CustomFieldGroupDTO.Builder> parseCustomFieldGroups(
		Map<String, CellDTO> rowData,
		AssignmentDTO assignment
	) {
		Set<CustomFieldGroupDTO.Builder> customFieldGroups = Sets.newHashSet();
		Set<CustomFieldGroupDTO> templateCustomFieldGroups = assignment.getCustomFieldGroups();
		for (CustomFieldGroupDTO customFieldGroup : templateCustomFieldGroups) {

			Set<CustomFieldDTO.Builder> customFields = Sets.newHashSet();

			for (CustomFieldDTO customField : customFieldGroup.getFields()) {
				String field = String.format("%s:%d", WorkUploadColumn.CUSTOM_FIELD.getUploadColumnName(), customField.getId());
				CellDTO cellDTO = rowData.get(field);
				if (cellDTO != null) {
					customFields.add(new CustomFieldDTO.Builder(customField).setValue(cellDTO.getValue()));
				}
			}
			customFieldGroups.add(new CustomFieldGroupDTO.Builder(customFieldGroup).setFields(customFields));
		}

		return customFieldGroups;
	}

	private String getValue(Map<String, CellDTO> rowData, String field) {
		Optional<CellDTO> dto = Optional.fromNullable(rowData.get(field));
		return dto.isPresent() ? dto.get().getValue() : null;
	}
}
