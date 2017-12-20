package com.workmarket.service.business.upload;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkUploadColumnType;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadValue;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class WorkUploadTemplateFactoryImpl implements WorkUploadTemplateFactory {

	@Autowired private PricingService pricingService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private TWorkFacadeService tWorkFacadeService;

	private static final WorkRequestInfo[] requestTypes = {
		WorkRequestInfo.CONTEXT_INFO,
		WorkRequestInfo.STATUS_INFO,
		WorkRequestInfo.INDUSTRY_INFO,
		WorkRequestInfo.PROJECT_INFO,
		WorkRequestInfo.CLIENT_COMPANY_INFO,
		WorkRequestInfo.BUYER_INFO,
		WorkRequestInfo.LOCATION_CONTACT_INFO,
		WorkRequestInfo.SUPPORT_CONTACT_INFO,
		WorkRequestInfo.LOCATION_INFO,
		WorkRequestInfo.SCHEDULE_INFO,
		WorkRequestInfo.PRICING_INFO,
		WorkRequestInfo.ASSETS_INFO,
		WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO,
		WorkRequestInfo.PARTS_INFO,
		WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO,
		WorkRequestInfo.PAYMENT_INFO,
		WorkRequestInfo.REQUIRED_ASSESSMENTS_INFO,
		WorkRequestInfo.FOLLOWER_INFO,
		WorkRequestInfo.DELIVERABLES_INFO
	};
	public static final String WORK_TEMPLATE = RedisConfig.WORK_TEMPLATE;

	@Override
	@Cacheable(
		value = WORK_TEMPLATE,
		key = "#root.target.WORK_TEMPLATE + #templateId + ':' + #userId"
	)
	public Work getTemplate(Long templateId, Long userId) throws WorkUploadException {

		Set<WorkRequestInfo> includes = Sets.newHashSet(requestTypes);

		WorkRequest req = new WorkRequest()
			.setUserId(userId)
			.setWorkId(templateId)
			.setIncludes(includes);
		WorkResponse resp;
		try {
			resp = tWorkFacadeService.findWork(req);
		} catch (Exception e) {
			throw new WorkUploadException(String.format("Issue with the work request when getting the template: templateId: %s, userId: %s", templateId, userId), e);
		}

		Work work = resp.getWork();

		if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
			adjustPricingUpwardByFeePercentage(work, authenticationService.getCurrentUser().getCompany().getId());
		}

		return work;
	}

	@Override
	public List<WorkUploadValue> extractValues(WorkUploaderBuildResponse buildResponse) {
		Work work = buildResponse.getWork();

		List<WorkUploadValue> template = Lists.newArrayListWithExpectedSize(60);
		addBundleInfo(template, buildResponse.getWorkBundle());
		addGeneralInfo(template, work);
		addLocationInfo(template, work);
		addLocationContactInfo(template, work);
		addScheduleInfo(template, work);
		addPricingInfo(template, work);
		addPartsWithTrackingInfo(template, work);
		addCustomFieldsInfo(template, work);

		return template;
	}

	private static final int MAX_LENGTH = 500;
	private static final int MAX_LENGTH_ELIPSIS = MAX_LENGTH - 4;

	private void addBundleInfo(List<WorkUploadValue> template, WorkBundle workBundle) {
		if (workBundle == null) { return; }
		if (StringUtils.isNotEmpty(workBundle.getTitle())) {
			WorkUploadValue value = createStringUploadValue(workBundle.getTitle(), WorkUploadColumn.NEW_BUNDLE_NAME);
			template.add(value);
		}

		if (StringUtils.isNotEmpty(workBundle.getDescription())) {
			String desc = workBundle.getDescription();
			if (desc.length() > MAX_LENGTH) {
				desc = StringUtilities.stripTags(desc);
				desc = StringUtilities.truncate(desc, MAX_LENGTH_ELIPSIS).concat("...");
			}
			WorkUploadValue value = createStringUploadValue(desc, WorkUploadColumn.NEW_BUNDLE_DESCRIPTION);
			template.add(value);
		}
	}

	private void addGeneralInfo(List<WorkUploadValue> template, Work work) {
		if (work.isSetTitle()) {
			WorkUploadValue value = createStringUploadValue(work.getTitle(), WorkUploadColumn.TITLE);
			template.add(value);
		}
		if (work.isSetDescription()) {
			String desc = work.getDescription();
			if (desc.length() > MAX_LENGTH) {
				desc = StringUtilities.stripTags(desc);
				desc = StringUtilities.truncate(desc, MAX_LENGTH_ELIPSIS).concat("...");
			}
			WorkUploadValue value = createStringUploadValue(desc, WorkUploadColumn.DESCRIPTION);
			template.add(value);
		}
		if (work.isSetInstructions()) {
			String inst = work.getInstructions();
			if (inst.length() > MAX_LENGTH) {
				inst = StringUtilities.stripTags(inst);
				inst = StringUtilities.truncate(inst, MAX_LENGTH_ELIPSIS).concat("...");
			}
			WorkUploadValue value = createStringUploadValue(inst, WorkUploadColumn.INSTRUCTIONS);
			template.add(value);
		}
		if (work.isSetDesiredSkills()) {
			WorkUploadValue value = createStringUploadValue(work.getDesiredSkills(), WorkUploadColumn.DESIRED_SKILLS);
			template.add(value);
		}
		if (work.isSetIndustry()) {
			WorkUploadValue value = createStringUploadValue(work.getIndustry().getName(), WorkUploadColumn.INDUSTRY_NAME);
			template.add(value);
		}
		if (work.isSetBuyer()) {
			if (work.getBuyer().isSetEmail()) {
				WorkUploadValue buyer = createStringUploadValue(work.getBuyer().getEmail(), WorkUploadColumn.OWNER_EMAIL);
				template.add(buyer);
			}
			if (work.getBuyer().isSetUserNumber()) {
				WorkUploadValue buyer = createStringUploadValue(work.getBuyer().getUserNumber(), WorkUploadColumn.OWNER_USER_NUMBER);
				template.add(buyer);
			}
		}
		if (work.isSetSupportContact()) {
			if (work.getSupportContact().isSetEmail()) {
				WorkUploadValue supportEmail = createStringUploadValue(work.getSupportContact().getEmail(), WorkUploadColumn.SUPPORT_CONTACT_EMAIL);
				template.add(supportEmail);
			}
			if (work.getSupportContact().isSetUserNumber()) {
				WorkUploadValue support = createStringUploadValue(work.getSupportContact().getUserNumber(), WorkUploadColumn.SUPPORT_CONTACT_USER_NUMBER);
				template.add(support);
			}
		}
		if (work.isSetClientCompany()) {
			String companyName = work.getClientCompany().getName();
			WorkUploadValue companyNameValue = createStringUploadValue(companyName, WorkUploadColumn.CLIENT_NAME);
			template.add(companyNameValue);
		}
		if (work.isSetProject()) {
			WorkUploadValue value = createStringUploadValue(work.getProject().getName(), WorkUploadColumn.PROJECT_NAME);
			template.add(value);
		}
		if (work.isSetTemplate()) {
			template.add(createStringUploadValue(String.valueOf(work.getTemplate().getId()), WorkUploadColumn.TEMPLATE_ID));
			template.add(createStringUploadValue(work.getTemplate().getWorkNumber(), WorkUploadColumn.TEMPLATE_NUMBER));
		}
	}

	private void addLocationInfo(List<WorkUploadValue> template, Work work) {

		if (work.getOffsiteLocation() != null) {
			WorkUploadValue value = createBooleanUploadValue(work.getOffsiteLocation(), WorkUploadColumn.LOCATION_OFFSITE);
			template.add(value);
		}

		if (work.isSetLocation()) {
			Location location = work.getLocation();

			if (location.isSetName()) {
				WorkUploadValue value = createStringUploadValue(location.getName(), WorkUploadColumn.LOCATION_NAME);
				template.add(value);
			}
			if (location.isSetNumber()) {
				WorkUploadValue value = createStringUploadValue(location.getNumber(), WorkUploadColumn.LOCATION_NUMBER);
				template.add(value);
			}
			if (location.isSetInstructions()) {
				WorkUploadValue value = createStringUploadValue(location.getInstructions(), WorkUploadColumn.LOCATION_INSTRUCTIONS);
				template.add(value);
			}
			if (location.isSetAddress()) {
				Address address = location.getAddress();

				if (address.isSetAddressLine1()) {
					WorkUploadValue value = createStringUploadValue(address.getAddressLine1(), WorkUploadColumn.LOCATION_ADDRESS_1);
					template.add(value);
				}
				if (address.isSetAddressLine2()) {
					WorkUploadValue value = createStringUploadValue(address.getAddressLine2(), WorkUploadColumn.LOCATION_ADDRESS_2);
					template.add(value);
				}
				if (address.isSetCity()) {
					WorkUploadValue value = createStringUploadValue(address.getCity(), WorkUploadColumn.LOCATION_CITY);
					template.add(value);
				}
				if (address.isSetState()) {
					WorkUploadValue value = createStringUploadValue(address.getState(), WorkUploadColumn.LOCATION_STATE);
					template.add(value);
				}
				if (address.isSetZip()) {
					WorkUploadValue value = createStringUploadValue(address.getZip(), WorkUploadColumn.LOCATION_POSTAL_CODE);
					template.add(value);
				}
				if (address.isSetCountry()) {
					WorkUploadValue value = createStringUploadValue(address.getCountry(), WorkUploadColumn.LOCATION_COUNTRY);
					template.add(value);
				}
				if (address.isSetDressCode()) {
					WorkUploadValue value = createStringUploadValue(address.getDressCode(), WorkUploadColumn.LOCATION_DRESS_CODE);
					template.add(value);
				}
				if (address.isSetType()) {
					WorkUploadValue value = createStringUploadValue(address.getType(), WorkUploadColumn.LOCATION_TYPE);
					template.add(value);
				}
			}
		}
	}

	private static void addLocationContactInfo(List<WorkUploadValue> template, Work work) {

		if (work.isSetLocationContact()) {
			User locationContact = work.getLocationContact();
			String contactEmail = locationContact.getEmail();
			WorkUploadValue contactEmailValue = createStringUploadValue(contactEmail, WorkUploadColumn.CONTACT_EMAIL);
			template.add(contactEmailValue);

			String contactFirstName = locationContact.getName().getFirstName();
			WorkUploadValue contractFirstNameValue = createStringUploadValue(contactFirstName, WorkUploadColumn.CONTACT_FIRST_NAME);
			template.add(contractFirstNameValue);

			String contactLastName = locationContact.getName().getLastName();
			WorkUploadValue contractLastNameValue = createStringUploadValue(contactLastName, WorkUploadColumn.CONTACT_LAST_NAME);
			template.add(contractLastNameValue);

			if (!isEmpty(locationContact.getProfile().getPhoneNumbers())) {
				Phone firstPhone = locationContact.getProfile().getPhoneNumbers().get(0);
				String contactPhone = firstPhone.getPhone();
				WorkUploadValue contactPhoneValue = createStringUploadValue(contactPhone, WorkUploadColumn.CONTACT_PHONE);
				template.add(contactPhoneValue);
				if (firstPhone.isSetExtension()) {
					String contactExtension = firstPhone.getPhone();
					WorkUploadValue contactExtensionValue = createStringUploadValue(contactExtension, WorkUploadColumn.CONTACT_PHONE_EXTENSION);
					template.add(contactExtensionValue);
				}

			}
		}
		if (work.isSetSecondaryLocationContact()) {
			User secondaryContact = work.getSecondaryLocationContact();
			String contactEmail = secondaryContact.getEmail();
			WorkUploadValue contactEmailValue = createStringUploadValue(contactEmail, WorkUploadColumn.SECONDARY_CONTACT_EMAIL);
			template.add(contactEmailValue);

			String contactFirstName = secondaryContact.getName().getFirstName();
			WorkUploadValue contractFirstNameValue = createStringUploadValue(contactFirstName, WorkUploadColumn.SECONDARY_CONTACT_FIRST_NAME);
			template.add(contractFirstNameValue);

			String contactLastName = secondaryContact.getName().getLastName();
			WorkUploadValue contractLastNameValue = createStringUploadValue(contactLastName, WorkUploadColumn.SECONDARY_CONTACT_LAST_NAME);
			template.add(contractLastNameValue);

			if (!isEmpty(secondaryContact.getProfile().getPhoneNumbers())) {
				Phone firstPhone = secondaryContact.getProfile().getPhoneNumbers().get(0);
				String contactPhone = firstPhone.getPhone();
				WorkUploadValue contactPhoneValue = createStringUploadValue(contactPhone, WorkUploadColumn.SECONDARY_CONTACT_PHONE);
				template.add(contactPhoneValue);
				if (firstPhone.isSetExtension()) {
					String contactExtension = firstPhone.getPhone();
					WorkUploadValue contactExtensionValue = createStringUploadValue(contactExtension, WorkUploadColumn.SECONDARY_CONTACT_PHONE_EXTENSION);
					template.add(contactExtensionValue);
				}
			}
		}
	}

	private static void addScheduleInfo(List<WorkUploadValue> template, Work work) {
		if (!work.isSetSchedule()) {
			return;
		}
		Schedule schedule = work.getSchedule();
		if (schedule.isSetFrom()) {
			long fromTime = schedule.getFrom();
			WorkUploadColumn column = WorkUploadColumn.START_DATE_TIME;
			WorkUploadValue fromTimeValue = createDateTimeUploadValue(fromTime, work.getTimeZone(), column);
			template.add(fromTimeValue);
		}

		if (schedule.isSetThrough()) {
			long toTime = schedule.getThrough();
			WorkUploadColumn column = WorkUploadColumn.END_DATE_TIME;
			WorkUploadValue toTimeValue = createDateTimeUploadValue(toTime, work.getTimeZone(), column);
			template.add(toTimeValue);
		}

	}

	/**
	 * This one is mad crazy.  There's about 10 fields that affect this one with all sorts of crazy business rules around it.
	 *
	 * The documentation for the business rules is on http://workmarket.jira.com/wiki/display/WORK/Interpreting+Pricing+Fields+in+Upload+Tool
	 * @param template
	 * @param work
	 */
	private static void addPricingInfo(List<WorkUploadValue> template, Work work) {
		if (!work.isSetPricing())
			return;

		PricingStrategy pricingStrategy = work.getPricing();

		if (pricingStrategy.isSetFlatPrice()) {
			if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
				WorkUploadValue perHourPriceClientFee = createDoublePriceUploadValue(pricingStrategy.getFlatPrice(), WorkUploadColumn.FLAT_PRICE_CLIENT_FEE);
				template.add(perHourPriceClientFee);
			} else {
				WorkUploadValue perHourPriceClientFee = createDoublePriceUploadValue(pricingStrategy.getFlatPrice(), WorkUploadColumn.FLAT_PRICE_RESOURCE_FEE);
				template.add(perHourPriceClientFee);
			}
		}

		if (pricingStrategy.isSetPerHourPrice()) {
			if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
				WorkUploadValue perHourPriceClientFee = createDoublePriceUploadValue(pricingStrategy.getPerHourPrice(), WorkUploadColumn.PER_HOUR_PRICE_CLIENT_FEE);
				template.add(perHourPriceClientFee);
			} else {
				WorkUploadValue perHourPriceClientFee = createDoublePriceUploadValue(pricingStrategy.getPerHourPrice(), WorkUploadColumn.PER_HOUR_PRICE_RESOURCE_FEE);
				template.add(perHourPriceClientFee);
			}
		}
		if (pricingStrategy.isSetMaxNumberOfHours()) {
			WorkUploadValue maxHours = createDoubleUploadValue(pricingStrategy.getMaxNumberOfHours(), WorkUploadColumn.MAX_NUMBER_OF_HOURS);
			template.add(maxHours);
		}

		if (pricingStrategy.isSetPerUnitPrice()) {
			if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
				WorkUploadValue perUnitPrice = createDoublePriceUploadValue(pricingStrategy.getPerUnitPrice(), WorkUploadColumn.PER_UNIT_PRICE_CLIENT_FEE);
				template.add(perUnitPrice);
			} else {
				WorkUploadValue perUnitPrice = createDoublePriceUploadValue(pricingStrategy.getPerUnitPrice(), WorkUploadColumn.PER_UNIT_PRICE_RESOURCE_FEE);
				template.add(perUnitPrice);
			}
		}
		if (pricingStrategy.isSetMaxNumberOfUnits()) {
			WorkUploadValue maxNumberOfUnits = createDoubleUploadValue(pricingStrategy.getMaxNumberOfUnits(), WorkUploadColumn.MAX_NUMBER_OF_UNITS);
			template.add(maxNumberOfUnits);
		}

		if (pricingStrategy.isSetInitialPerHourPrice()) {
			double perHourPrice = pricingStrategy.getInitialPerHourPrice();
			if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
				//client
				WorkUploadColumn column = WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE;
				WorkUploadValue value = createDoublePriceUploadValue(perHourPrice, column);
				template.add(value);
			} else {
				//resource
				WorkUploadColumn column = WorkUploadColumn.INITIAL_PER_HOUR_PRICE_RESOURCE_FEE;
				WorkUploadValue value = createDoublePriceUploadValue(perHourPrice, column);
				template.add(value);
			}
		}
		if (pricingStrategy.isSetAdditionalPerHourPrice()) {
			double perHourPrice = pricingStrategy.getAdditionalPerHourPrice();
			if (work.getConfiguration().isUseMaxSpendPricingDisplayModeFlag()) {
				//client
				WorkUploadColumn column = WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE;
				WorkUploadValue value = createDoublePriceUploadValue(perHourPrice, column);
				template.add(value);
			} else {
				//resource
				WorkUploadColumn column = WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_RESOURCE_FEE;
				WorkUploadValue value = createDoublePriceUploadValue(perHourPrice, column);
				template.add(value);
			}
		}
		if (pricingStrategy.isSetMaxNumberOfHours()) {
			double maxNumberOfHours = pricingStrategy.getMaxNumberOfHours();
			WorkUploadValue value = createDoubleUploadValue(maxNumberOfHours, WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE);
			template.add(value);
		}
		if (pricingStrategy.isSetMaxBlendedNumberOfHours()) {
			double maxNumberOfHours = pricingStrategy.getMaxBlendedNumberOfHours();
			WorkUploadValue value = createDoubleUploadValue(maxNumberOfHours, WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE);
			template.add(value);
		}
	}

	private static void addPartsWithTrackingInfo(List<WorkUploadValue> template, Work work) {
		if (!work.isSetPartGroup()) {
			return;
		}
		
		PartGroupDTO partGroup = work.getPartGroup();
		if (partGroup.isSetShippingDestinationType()) {
			template.add(createStringUploadValue(partGroup.getShippingDestinationType().name(), WorkUploadColumn.DISTRIBUTION_METHOD));
		}
		if (partGroup.isSuppliedByWorker()) {
			template.add(createBooleanUploadValue(partGroup.isSuppliedByWorker(), WorkUploadColumn.SUPPLIED_BY_RESOURCE));
		}
		if (partGroup.isReturnRequired()) {
			template.add(createStringUploadValue(String.valueOf(partGroup.isReturnRequired()), WorkUploadColumn.RETURN_REQUIRED));
		}

		if (partGroup.hasShipToLocation()) {
			LocationDTO pickupLocation = partGroup.getShipToLocation();
			
			if (pickupLocation.isSetName()) {
				template.add(createStringUploadValue(pickupLocation.getName(), WorkUploadColumn.PICKUP_LOCATION_NAME));
			}
			if (pickupLocation.isSetLocationNumber()) {
				template.add(createStringUploadValue(pickupLocation.getLocationNumber(), WorkUploadColumn.PICKUP_LOCATION_NUMBER));
			}
			if (pickupLocation.isSetAddress1()) {
				template.add(createStringUploadValue(pickupLocation.getAddress1(), WorkUploadColumn.PICKUP_LOCATION_ADDRESS_1));
			}
			if (pickupLocation.isSetAddress2()) {
				template.add(createStringUploadValue(pickupLocation.getAddress2(), WorkUploadColumn.PICKUP_LOCATION_ADDRESS_2));
			}
			if (pickupLocation.isSetCity()) {
				template.add(createStringUploadValue(pickupLocation.getCity(), WorkUploadColumn.PICKUP_LOCATION_CITY));
			}
			if (pickupLocation.isSetCountry()) {
				template.add(createStringUploadValue(pickupLocation.getCountry(), WorkUploadColumn.PICKUP_LOCATION_COUNTRY));
			}
			if (pickupLocation.isSetState()) {
				template.add(createStringUploadValue(pickupLocation.getState(), WorkUploadColumn.PICKUP_LOCATION_STATE));
			}
			if (pickupLocation.isSetLocationTypeId()) {
				template.add(createStringUploadValue(LocationType.getName(pickupLocation.getLocationTypeId()), WorkUploadColumn.PICKUP_LOCATION_TYPE));
			}
			if (pickupLocation.isSetPostalCode()) {
				template.add(createStringUploadValue(pickupLocation.getPostalCode(), WorkUploadColumn.PICKUP_LOCATION_POSTAL_CODE));
			}
		}
		
		if (partGroup.hasReturnToLocation()) {
			LocationDTO returnLocation = partGroup.getReturnToLocation();
			
			if (returnLocation.isSetName()) {
				template.add(createStringUploadValue(returnLocation.getName(), WorkUploadColumn.RETURN_LOCATION_NAME));
			}
			if (returnLocation.isSetLocationNumber()) {
				template.add(createStringUploadValue(returnLocation.getLocationNumber(), WorkUploadColumn.RETURN_LOCATION_NUMBER));
			}
			if (returnLocation.isSetAddress1()) {
				template.add(createStringUploadValue(returnLocation.getAddress1(), WorkUploadColumn.RETURN_LOCATION_ADDRESS_1));
			}
			if (returnLocation.isSetAddress2()) {
				template.add(createStringUploadValue(returnLocation.getAddress2(), WorkUploadColumn.RETURN_LOCATION_ADDRESS_2));
			}
			if (returnLocation.isSetCity()) {
				template.add(createStringUploadValue(returnLocation.getCity(), WorkUploadColumn.RETURN_LOCATION_CITY));
			}
			if (returnLocation.isSetCountry()) {
				template.add(createStringUploadValue(returnLocation.getCountry(), WorkUploadColumn.RETURN_LOCATION_COUNTRY));
			}
			if (returnLocation.isSetState()) {
				template.add(createStringUploadValue(returnLocation.getState(), WorkUploadColumn.RETURN_LOCATION_STATE));
			}
			if (returnLocation.isSetLocationTypeId()) {
				template.add(createStringUploadValue(LocationType.getName(returnLocation.getLocationTypeId()), WorkUploadColumn.RETURN_LOCATION_TYPE));
			}
			if (returnLocation.isSetPostalCode()) {
				template.add(createStringUploadValue(returnLocation.getPostalCode(), WorkUploadColumn.RETURN_LOCATION_POSTAL_CODE));
			}
		}

		for (PartDTO part : partGroup.getParts()) {
			if (part.isReturn()) {
				if (part.isSetPartValue()) {
					template.add(createBigDecimalUploadValue(part.getPartValue(), WorkUploadColumn.RETURN_PART_VALUE));
				}
				if (part.isSetShippingProvider()) {
					template.add(createStringUploadValue(part.getShippingProvider().name(), WorkUploadColumn.RETURN_SHIPPING_PROVIDER));
				}
				if (part.isSetTrackingNumber()) {
					template.add(createStringUploadValue(part.getTrackingNumber(), WorkUploadColumn.RETURN_TRACKING_NUMBER));
				}
			} else {
				if (part.isSetPartValue()) {
					template.add(createBigDecimalUploadValue(part.getPartValue(), WorkUploadColumn.PICKUP_PART_VALUE));
				}
				if (part.isSetShippingProvider()) {
					template.add(createStringUploadValue(part.getShippingProvider().name(), WorkUploadColumn.PICKUP_SHIPPING_PROVIDER));
				}
				if (part.isSetTrackingNumber()) {
					template.add(createStringUploadValue(part.getTrackingNumber(), WorkUploadColumn.PICKUP_TRACKING_NUMBER));
				}
			}
		}
	}

	private static void addCustomFieldsInfo(List<WorkUploadValue> template, Work work) {
		if (work.getCustomFieldGroupsSize() == 0)
			return;

		for (CustomFieldGroup g : work.getCustomFieldGroups()) {
			for (CustomField f : g.getFields()) {
				FieldType fieldType = new FieldType()
					.setCode(WorkUploadColumnType.newInstance(WorkUploadColumnType.CUSTOM_FIELD_TYPE).getDerivedCode(f.getId()))
					.setDescription(f.getName());
				WorkUploadValue value = new WorkUploadValue()
					.setType(fieldType)
					.setFromTemplate(true)
					.setValue(f.getValue());
				template.add(value);
			}
		}
	}

	private static WorkUploadValue createDateTimeUploadValue(long fromTime, String timeZoneId,
			WorkUploadColumn column) {
		WorkUploadValue value = new WorkUploadValue();
		value.setValue(DateUtilities.formatDateForEmail(DateUtilities.getCalendarFromMillis(fromTime), timeZoneId));
		value.setType(column.createFieldType());
		value.setFromTemplate(true);
		return value;
	}

	private static WorkUploadValue createDoublePriceUploadValue(double perHourPrice, WorkUploadColumn column) {
		return createDoubleUploadValue(perHourPrice, column, true);
	}

	private static WorkUploadValue createDoubleUploadValue(double perHourPrice, WorkUploadColumn column) {
		return createDoubleUploadValue(perHourPrice, column, false);
	}

	private static WorkUploadValue createDoubleUploadValue(double perHourPrice, WorkUploadColumn column, boolean isPrice) {
		WorkUploadValue value = new WorkUploadValue();
		DecimalFormat format = new DecimalFormat(isPrice ? "$0.00" : "0.00");
		value.setValue(format.format(perHourPrice));
		value.setType(column.createFieldType());
		value.setFromTemplate(true);
		return value;
	}

	private static WorkUploadValue createBigDecimalUploadValue(BigDecimal perHourPrice, WorkUploadColumn column) {
		WorkUploadValue value = new WorkUploadValue();
		DecimalFormat format = new DecimalFormat("$0.00");
		value.setValue(format.format(perHourPrice));
		value.setType(column.createFieldType());
		value.setFromTemplate(true);
		return value;
	}

	private static WorkUploadValue createStringUploadValue(String value, WorkUploadColumn column) {
		return new WorkUploadValue()
			.setFromTemplate(true)
			.setValue(value)
			.setType(column.createFieldType());
	}

	private static WorkUploadValue createBooleanUploadValue(boolean value, WorkUploadColumn column) {
		return new WorkUploadValue()
			.setFromTemplate(true)
			.setValue(value ? "Yes" : "No")
			.setType(column.createFieldType());
	}

	private void adjustPricingUpwardByFeePercentage(Work work, Long id) {
		if (!work.isSetPricing())
			return;

		PricingStrategy pricingStrategy = work.getPricing();
		AccountRegister register = pricingService.findDefaultRegisterForCompany(id);
		BigDecimal feePercentage = register.getCurrentWorkFeePercentage().movePointLeft(2).add(BigDecimal.ONE);

		if (pricingStrategy.isSetFlatPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getFlatPrice());
			pricingStrategy.setFlatPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetPerHourPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getPerHourPrice());
			pricingStrategy.setPerHourPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetPerUnitPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getPerUnitPrice());
			pricingStrategy.setPerUnitPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetInitialPerHourPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getInitialPerHourPrice());
			pricingStrategy.setInitialPerHourPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetAdditionalPerHourPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getAdditionalPerHourPrice());
			pricingStrategy.setAdditionalPerHourPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetInitialPerUnitPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getInitialPerUnitPrice());
			pricingStrategy.setInitialPerUnitPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
		if (pricingStrategy.isSetAdditionalPerUnitPrice()) {
			BigDecimal price = BigDecimal.valueOf(pricingStrategy.getAdditionalPerUnitPrice());
			pricingStrategy.setAdditionalPerUnitPrice(price.multiply(feePercentage).setScale(Constants.PRICING_STRATEGY_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue());
		}
	}

}
