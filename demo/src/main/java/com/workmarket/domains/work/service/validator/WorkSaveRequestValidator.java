package com.workmarket.domains.work.service.validator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.AbstractEntityUtilities;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.customfield.WorkCustomField;
import com.workmarket.domains.model.pricing.BlendedPerHourPricingStrategy;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PerHourPricingStrategy;
import com.workmarket.domains.model.pricing.PerUnitPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.work.dao.DeliverableRequirementDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkUniqueId;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeWorkStatusScope;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.LocationDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.validators.AddressWorkValidator;
import com.workmarket.web.validators.LocationValidator;
import com.workmarket.web.validators.PartGroupValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@Component
public class WorkSaveRequestValidator {

	protected static final Log logger = LogFactory.getLog(WorkSaveRequestValidator.class);

	@Autowired private UserService userService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private CompanyService companyService;
	@Autowired private DirectoryService directoryService;
	@Autowired private PricingService pricingService;
	@Autowired private ProfileService profileService;
	@Autowired private WorkService workService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private DeliverableRequirementDAO deliverableRequirementDAO;
	@Autowired private @Qualifier("addressWorkValidator") AddressWorkValidator addressValidator;
	@Autowired private PartGroupValidator partGroupValidator;
	@Autowired private IndustryService industryService;
	@Autowired private LocationValidator locationValidator;

	private static final int TEMPLATE_NAME_MIN_LENGTH = Constants.NAME_MIN_LENGTH;
	private static final int TEMPLATE_NAME_MAX_LENGTH = Constants.NAME_MAX_LENGTH;

	private static final int TITLE_MAX_LENGTH = 255;
	private static final int DESCRIPTION_MAX_LENGTH = Constants.TEXT_MAX_LENGTH;
	private static final int UNIQUE_EXTERNAL_ID_MAX_LENGTH = 50;
	private static final int MAX_CUSTOM_FIELDS_LENGTH = 1001;
	private static final int MIN_DELIVERABLE_FILE_REQUIREMENT = 1;
	private static final int MAX_DELIVERABLE_FILE_REQUIREMENT = 99;

	private static final int SECONDS_IN_AN_ORDINARY_YEAR = 31536000;
	private static final boolean NOT_READY_TO_SEND = false;
	private static final boolean READY_TO_SEND = true;

	public static final Map<Integer, String> VALID_DELIVERABLE_DEADLINE_HOURS = new ImmutableMap.Builder<Integer,String>()
		.put(0, "None")
		.put(24, "1 day")
		.put(48, "2 days")
		.put(72, "3 days")
		.put(96, "4 days")
		.put(120, "5 days")
		.put(144, "6 days")
		.put(168, "7 days")
		.build();

	public void validateWork(WorkSaveRequest request) throws ValidationException {
		List<ConstraintViolation> errors = getConstraintViolations(request);

		if (CollectionUtils.isNotEmpty(errors)) {
			logErrors(errors);
			throw new ValidationException("Unable to save assignment", errors);
		}
	}


	public List<ConstraintViolation> getConstraintViolations(WorkSaveRequest request) {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateWork(request, errors);
		validateLocation(request, errors);
		validateLocationContacts(request, errors);
		validateSupportContact(request, errors);
		validateSchedule(request, errors);
		validatePricing(request, errors);
		validateParts(request, errors);
		validateCustomFields(request, errors);
		validateResource(request, errors);
		validateRequireProject(request, errors);
		validateLabels(request, errors);
		validateDeliverables(request, errors);

		return errors;
	}


	public void validateWorkDraft(WorkSaveRequest request) throws ValidationException {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateWorkDraft(request, errors);
		validateLocationAndAddress(request, errors, "location", NOT_READY_TO_SEND);
		validateLocationContacts(request, errors);
		validateSupportContact(request, errors);
		validateParts(request, errors);
		validateDeliverables(request, errors, true);

		if (errors.size() > 0) {
			logErrors(errors);
			throw new ValidationException("Unable to save assignment", errors);
		}
	}


	public void validateTemplate(WorkSaveRequest request) throws ValidationException {
		List<ConstraintViolation> errors = Lists.newLinkedList();
		validateTemplate(request, errors);
		validateLocationAndAddress(request, errors, "location", NOT_READY_TO_SEND);
		validateLocationContacts(request, errors);
		validateSupportContact(request, errors);
		validateParts(request, errors);
		validateDeliverables(request, errors, true);

		if (errors.size() > 0) {
			logErrors(errors);
			throw new ValidationException("Unable to save template", errors);
		}
	}


	protected void validateResource(WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (request.isSetAssignTo()) {
			ProfileDTO profile = profileService.findProfileDTO(request.getAssignTo().getId());

			if (profile == null) {
				errors.add(new ConstraintViolation().setWhy(messageHelper.getMessage("work.save.resource.invalid")));
			} else if (!industryService.doesProfileHaveIndustry(profile.getProfileId(), request.getWork().getIndustry().getId())) {
				String industryName = invariantDataService.findIndustry(request.getWork().getIndustry().getId()).getName();

				errors.add(new ConstraintViolation().setWhy(messageHelper.getMessage("work.save.resource.invalid_industry",
					profile.getFirstName(), profile.getLastName(), industryName)).setProperty("industry"));
			}
		}

		// NOTE further validation is actually executed by {@link WorkService.validateSaveWorkResource}
	}

	protected void validateWork(WorkSaveRequest request, List<ConstraintViolation> errors) {
		String title = request.getWork().getTitle();
		if (isEmpty(title)) {
			errors.add(newConstraintViolation("title", MessageKeys.Work.NOT_NULL));
		} else if (length(title) > TITLE_MAX_LENGTH) {
			errors.add(newConstraintViolation("title", MessageKeys.Work.MAX, String.valueOf(TITLE_MAX_LENGTH)));
		}

		String description = StringUtils.defaultString(request.getWork().getDescription());
		String plainTextDescriptionNoSpaces = Jsoup.parse(description).text()
			.replaceAll("\\s+", "")
			.replace("\u00a0", "");
		if (isEmpty(plainTextDescriptionNoSpaces)) {
			errors.add(newConstraintViolation("description", MessageKeys.Work.NOT_NULL));
		} else if (length(description) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("description", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(description) > 0) {
			String cleanedDescription = StringUtilities.newLineToHtmlBreak(StringUtilities.stripXSSAndEscapeHtml(description), false);
			request.getWork().setDescription(cleanedDescription);
		}

		String instructions = request.getWork().getInstructions();
		if (length(instructions) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("instructions", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(instructions) > 0) {
			String cleanedInstructions = StringUtilities.newLineToHtmlBreak(StringUtilities.stripXSSAndEscapeHtml(instructions), false);
			request.getWork().setInstructions(cleanedInstructions);
		}

		if (!request.getWork().isSetIndustry()) {
			errors.add(newConstraintViolation("industry", MessageKeys.Work.NOT_NULL));
		}

		validateUniqueExternalId(request, errors, false);
	}


	protected void validateWorkDraft(WorkSaveRequest request, List<ConstraintViolation> errors) {
		String title = request.getWork().getTitle();
		if (isEmpty(title)) {
			errors.add(newConstraintViolation("title", MessageKeys.Work.NOT_NULL));
		} else if (length(title) > TITLE_MAX_LENGTH) {
			errors.add(newConstraintViolation("title", MessageKeys.Work.MAX, String.valueOf(TITLE_MAX_LENGTH)));
		}

		String description = request.getWork().getDescription();
		if (isEmpty(description)) {
			errors.add(newConstraintViolation("description", MessageKeys.Work.NOT_NULL));
		} else if (length(description) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("description", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(description) > 0) {
			String cleanedDescription = StringUtilities.newLineToHtmlBreak(StringUtilities.stripXSSAndEscapeHtml(description), false);
			request.getWork().setDescription(cleanedDescription);
		}

		String instructions = request.getWork().getInstructions();
		if (length(instructions) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("instructions", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(instructions) > 0) {
			String cleanedInstructions = StringUtilities.newLineToHtmlBreak(StringUtilities.stripXSSAndEscapeHtml(instructions), false);
			request.getWork().setInstructions(cleanedInstructions);
		}

		validateUniqueExternalId(request, errors, true);
	}

	public void validateUniqueExternalId(WorkSaveRequest request, List<ConstraintViolation> errors, boolean allowEmpty) {
		com.workmarket.domains.model.User user = userService.getUser(request.getUserId());
		CompanyPreference companyPreference = companyService.getCompanyPreference(user.getCompany().getId());
		String uniqueIdValue = request.getWork().getUniqueExternalIdValue();

		//second part of the condition is to allow uniqueness check for updating external unique id existing assignments when external id feature is disabled by user
		if (companyPreference.isExternalIdActive() || uniqueIdValue != null) {
			if (isEmpty(uniqueIdValue) && !allowEmpty) {
				errors.add(newConstraintViolation(companyPreference.getExternalIdDisplayName(),
					MessageKeys.Work.NOT_NULL));
			} else if (length(uniqueIdValue) > UNIQUE_EXTERNAL_ID_MAX_LENGTH) {
				errors.add(newConstraintViolation(companyPreference.getExternalIdDisplayName(),
					MessageKeys.Work.MAX, String.valueOf(UNIQUE_EXTERNAL_ID_MAX_LENGTH)));
			} else {
				WorkUniqueId workUniqueId = workService.findUniqueIdByCompanyVersionIdValue(user.getCompany().getId(),
					companyPreference.getExternalIdVersion(), uniqueIdValue);
				if (workUniqueId != null && workUniqueId.getWorkId() != request.getWork().getId()) {
					errors.add(newConstraintViolation(
						workUniqueId.getDisplayName(),
						String.format("%s value %s is already in use",
							workUniqueId.getDisplayName(), uniqueIdValue)));
				} else if (length(uniqueIdValue) > 0) {
					String cleanedUniqueIdValue = StringUtilities.newLineToHtmlBreak(
						StringUtilities.stripXSSAndEscapeHtml(uniqueIdValue), false);
					request.getWork().setUniqueExternalIdValue(cleanedUniqueIdValue);
				}
			}
		}
	}

	protected void validateTemplate(WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (!request.getWork().isSetTemplate()) {
			errors.add(newConstraintViolation("name", MessageKeys.Work.NOT_NULL));
			// errors.add(newConstraintViolation("description", MessageKeys.Work.NOT_NULL));
			return;
		}
		String name = request.getWork().getTemplate().getName();
		if (isBlank(name)) {
			errors.add(newConstraintViolation("name", MessageKeys.Work.NOT_NULL));
		} else if (length(name) < TEMPLATE_NAME_MIN_LENGTH) {
			errors.add(newConstraintViolation("name", MessageKeys.Work.MINLEN, String.valueOf(TEMPLATE_NAME_MIN_LENGTH)));
		} else if (length(name) > TEMPLATE_NAME_MAX_LENGTH) {
			errors.add(newConstraintViolation("name", MessageKeys.Work.MAXLEN, String.valueOf(TEMPLATE_NAME_MAX_LENGTH)));
		}

		String title = request.getWork().getTitle();
		if (length(title) > TITLE_MAX_LENGTH) {
			errors.add(newConstraintViolation("title", MessageKeys.Work.MAX, String.valueOf(TITLE_MAX_LENGTH)));
		}

		String description = request.getWork().getDescription();
		if (length(description) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("description", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(description) > 0) {
			request.getWork().setDescription(StringUtilities.stripXSSAndEscapeHtml(description));
		}

		String instructions = request.getWork().getInstructions();
		if (length(instructions) > DESCRIPTION_MAX_LENGTH) {
			errors.add(newConstraintViolation("instructions", MessageKeys.Work.MAX, String.valueOf(DESCRIPTION_MAX_LENGTH)));
		} else if (length(instructions) > 0) {
			request.getWork().setInstructions(StringUtilities.stripXSSAndEscapeHtml(instructions));
		}
	}

	protected void validateRequireProject(WorkSaveRequest request, List<ConstraintViolation> errors) {
		Work work = request.getWork();
		long companyId;

		if (work.isSetWorkNumber()) {
			// Convert thrift company to company
			AbstractWork savedWork = workService.findWork(work.getId());
			if (savedWork == null) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("assignment.validation.error")).setProperty("work"));
				return;
			}
			companyId = savedWork.getCompany().getId();
		} else {
			com.workmarket.domains.model.User user = userService.getUser(request.getUserId());
			if (user == null) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("assignment.validation.error")));
				return;
			}
			companyId = user.getCompany().getId();
		}

		ManageMyWorkMarket manageMyWorkMarket = companyService.getManageMyWorkMarket(companyId);
		if (manageMyWorkMarket != null && manageMyWorkMarket.getRequireProjectEnabledFlag() && !work.isSetProject()) {
			errors.add(newConstraintViolation("project", MessageKeys.Work.NOT_NULL));
		}
	}

	protected void validateLocation(WorkSaveRequest request, List<ConstraintViolation> errors) {
		Boolean isOffsiteLocation = request.getWork().getOffsiteLocation();
		if (isOffsiteLocation == null) {
			errors.add(newConstraintViolation("location", MessageKeys.Work.NOT_NULL));
			return;
		}

		if (isOffsiteLocation) {
			return;
		}

		if (!request.getWork().isSetLocation()) {
			errors.add(newConstraintViolation("location", MessageKeys.Work.NOT_NULL));
			return;
		}

		if (!request.getWork().isNewLocation() && !request.getWork().isOffsiteLocation()) {
			com.workmarket.domains.model.Location location =
					directoryService.findLocationById(request.getWork().getLocation().getId());
			Long userCompanyId = userService.getUser(request.getUserId()).getCompany().getId();

			if (location == null || (userCompanyId != null && !userCompanyId.equals(location.getCompany().getId()))) {
				errors.add(newConstraintViolation("location", messageHelper.getMessage("work.form.location.does_not_exist")));
				return;
			}
		}

		Boolean isSetId = request.getWork().getLocation().isSetId();
		if (!request.getWork().getLocation().isSetAddress() && !isSetId) {
			errors.add(newConstraintViolation("location", MessageKeys.Work.NOT_NULL));
			return;
		}

		if (!isSetId) {
			validateLocationAndAddress(request, errors, "Assignment", READY_TO_SEND);
		}
	}

	protected void validateLocationAndAddress(WorkSaveRequest request, List<ConstraintViolation> errors, String key, boolean readyToSend) {
		if (request == null) {
			return;
		}
		Boolean isOffsiteLocation = request.getWork().getOffsiteLocation();
		if (isOffsiteLocation != null) {
			if (isOffsiteLocation) {
				return;
			}
		}
		Location location = request.getWork().getLocation();
		if (location == null) {
			return;
		}
		if (location.isSetId()) {
			return;
		}

		if (!location.isSetAddress()) {
			if (readyToSend) {
				errors.add(newConstraintViolation("Address", MessageKeys.Work.NOT_NULL, key));
			}
			return;
		}

		LocationDTO locationDTO = new LocationDTO();
		locationDTO.setName(location.getName());
		locationDTO.setLocationNumber(location.getNumber());
		locationDTO.setInstructions(location.getInstructions());
		if (location.getCompany() != null) {
			locationDTO.setClientCompanyId(location.getCompany().getId());
		}
		DataBinder dataBinder = new DataBinder(locationDTO);
		BindingResult binding = dataBinder.getBindingResult();
		locationValidator.validate(locationDTO, binding);

		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				errors.add(new ConstraintViolation().setWhy(
					ArrayUtils.isEmpty(e.getArguments()) ?
						messageHelper.getMessage(StringUtilities.smartJoin(e.getCode(), ".", ((FieldError) e).getField())) :
						messageHelper.getMessage(StringUtilities.smartJoin(e.getCode(), ".", ((FieldError) e).getField()), e.getArguments())));
			}
		}

		// If the work specifies a client, but not a location name, then we throw an error
		if (!location.isSetName() && request.getWork().isSetClientCompany()) {
			errors.add(new ConstraintViolation()
				.setWhy(messageHelper.getMessage("work.form.location.client_but_no_name")));
		}

		AddressDTO addressDTO = locationToAddressDTO(location);
		dataBinder = new DataBinder(addressDTO);
		binding = dataBinder.getBindingResult();
		addressValidator.validate(addressDTO, binding);

		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				errors.add(new ConstraintViolation().setWhy(
					ArrayUtils.isEmpty(e.getArguments()) ?
						messageHelper.getMessage(StringUtilities.smartJoin(e.getCode(), ".", ((FieldError) e).getField())) :
						messageHelper.getMessage(StringUtilities.smartJoin(e.getCode(), ".", ((FieldError) e).getField()), e.getArguments())));
			}
		}
	}

	protected AddressDTO locationToAddressDTO(Location location) {
		AddressDTO dto = new AddressDTO();
		Address address = location.getAddress();
		dto.setAddress1(address.getAddressLine1());
		dto.setAddress2(address.getAddressLine2());
		dto.setCity(address.getCity());
		dto.setCountry(address.getCountry());
		dto.setState(address.getState());
		dto.setPostalCode(address.getZip());
		if (address.getPoint() != null) {
			dto.setLatitude(new BigDecimal(address.getPoint().getLatitude()));
			dto.setLongitude(new BigDecimal(address.getPoint().getLongitude()));
		}
		return dto;
	}

	protected void validateUser(User user, List<ConstraintViolation> errors) {
		if (!user.isSetName()) {
			errors.add(newConstraintViolation("Contact name", MessageKeys.NOT_NULL));
		} else {
			if (!user.getName().isSetFirstName()) {
				errors.add(newConstraintViolation("Contact first name", MessageKeys.NOT_NULL));
			} else if (user.getName().getFirstName().length() > Constants.FIRST_NAME_MAX_LENGTH) {
				errors.add(newConstraintViolation(Integer.toString(Constants.LAST_NAME_MAX_LENGTH), MessageKeys.Contact.FIRST_NAME_TOO_LONG, Integer.toString(Constants.FIRST_NAME_MAX_LENGTH)));
			}
			if (!user.getName().isSetLastName()) {
				errors.add(newConstraintViolation("Contact last name", MessageKeys.NOT_NULL));
			} else if (user.getName().getLastName().length() > Constants.LAST_NAME_MAX_LENGTH) {
				errors.add(newConstraintViolation(Integer.toString(Constants.LAST_NAME_MAX_LENGTH), MessageKeys.Contact.LAST_NAME_TOO_LONG));
			}
		}
	}


	protected void validateLocationContacts(WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (!request.getWork().isSetLocation()) {
			return;
		}

		if (request.getWork().isSetLocationContact()) {
			if (!request.getWork().getLocationContact().isSetId()) {
				validateUser(request.getWork().getLocationContact(), errors);
			}
		}

		if (request.getWork().isSetSecondaryLocationContact()) {
			if (!request.getWork().getSecondaryLocationContact().isSetId()) {
				validateUser(request.getWork().getSecondaryLocationContact(), errors);
			}
		}
	}


	protected void validateSupportContact(WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (!request.getWork().isSetSupportContact()) return;

		if (request.isPartOfBulk() && request.getWork().getSupportContact().getUserNumber() == null) return;

		if (request.getWork().getSupportContact().isSetId()) {
			try {
				com.workmarket.domains.model.User u = userService.getUser(request.getWork().getSupportContact().getId());
				com.workmarket.domains.model.User actor = authenticationService.getCurrentUser();
				if (!u.getCompany().getId().equals(actor.getCompany().getId()))
					errors.add(new ConstraintViolation()
						.setWhy(messageHelper.getMessage("work.save.support_contact.company")));
				return;
			} catch (Exception e) {
				logger.error("error validating support contact", e);
			}
		}
		errors.add(new ConstraintViolation().setWhy(
			messageHelper.getMessage("work.save.support_contact.invalid")));
	}


	protected void validateSchedule(WorkSaveRequest request, List<ConstraintViolation> errors) {
		Work work = request.getWork();
		if (!work.isSetSchedule()) {
			errors.add(newConstraintViolation("schedule", MessageKeys.Work.NOT_NULL));
			return;
		}

		if (work.getSchedule().isRange()) {
			if (!work.getSchedule().isSetFrom() || !work.getSchedule().isSetThrough()) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_MISSING_VALUES));
				return;
			}
			if (work.getSchedule().getFrom() < DateUtilities.getWorkBackDateThreshold()) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_INVALID_DATE));
				return;
			}
			if (work.getSchedule().getFrom() >= work.getSchedule().getThrough()) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_INVALID_TIMEFRAME_ORDER));
				return;
			}
		  if ((work.getSchedule().getThrough() - work.getSchedule().getFrom())/1000 > SECONDS_IN_AN_ORDINARY_YEAR) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_TIMEFRAME_TOO_LONG));
				return;
			}
		} else {
			if (!work.getSchedule().isSetFrom()) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_MISSING_VALUES));
				return;
			}
			if (work.getSchedule().getFrom() < DateUtilities.getWorkBackDateThreshold()) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.SCHEDULING_INVALID_DATE));
				return;
			}
		}
		if (work.isCheckinCallRequired()) {
			if (StringUtils.isBlank(work.getCheckinContactName())) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.CHECK_IN_CALL_NAME_REQUIRED));
			}
			if (StringUtils.isBlank(work.getCheckinContactPhone())) {
				errors.add(newConstraintViolation("scheduling", MessageKeys.Work.CHECK_IN_CALL_PHONE_REQUIRED));
			}
		}
	}


	protected void validatePricing(WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (!request.getWork().isSetPricing()) {
			errors.add(newConstraintViolation("pricing", MessageKeys.Work.NOT_NULL));
			return;
		}

		PricingStrategy strategy;
		try {
			strategy = pricingService.findPricingStrategyById(request.getWork().getPricing().getId());
		} catch (Exception e) {
			errors.add(newConstraintViolation("pricing", MessageKeys.Work.NOT_NULL));
			return;
		}

		boolean isNewObject = !request.getWork().isSetId();

		if (isNewObject) {
			if (strategy == null) {
				errors.add(newConstraintViolation("pricing", MessageKeys.Work.NOT_NULL));
				return;
			}
		}
		else {
			AbstractWork workModel = workService.findWork(request.getWork().getId());

			if (workModel.isPricingEditable()) {
				if (strategy == null) {
					errors.add(newConstraintViolation("pricing", MessageKeys.Work.NOT_NULL));
					return;
				}

				if (!workModel.isDraft() &&
						(workModel.getPricingStrategyType() == PricingStrategyType.INTERNAL ||
						strategy instanceof InternalPricingStrategy)) {
					errors.add(newConstraintViolation("internalPrice", MessageKeys.Work.INVALID_PRICING_TYPE_CHANGE));
				}
			}
			else {
				strategy = workModel.getPricingStrategy();
			}
		}

		if (strategy instanceof FlatPricePricingStrategy) {
			if (!request.getWork().getPricing().isSetFlatPrice()) {
				errors.add(newConstraintViolation("flatPrice", MessageKeys.Work.NOT_NULL));
			}
		} else if (strategy instanceof PerHourPricingStrategy) {
			if (!request.getWork().getPricing().isSetPerHourPrice()) {
				errors.add(newConstraintViolation("perHourPrice", MessageKeys.Work.NOT_NULL));
			}
			if (!request.getWork().getPricing().isSetMaxNumberOfHours()) {
				errors.add(newConstraintViolation("maxNumberOfHours", MessageKeys.Work.NOT_NULL));
			}
		} else if (strategy instanceof PerUnitPricingStrategy) {
			if (!request.getWork().getPricing().isSetPerUnitPrice()) {
				errors.add(newConstraintViolation("perUnitPrice", MessageKeys.Work.NOT_NULL));
			}
			if (!request.getWork().getPricing().isSetMaxNumberOfUnits()) {
				errors.add(newConstraintViolation("maxNumberOfUnits", MessageKeys.Work.NOT_NULL));
			}
		} else if (strategy instanceof BlendedPerHourPricingStrategy) {
			if (!request.getWork().getPricing().isSetInitialPerHourPrice()) {
				errors.add(newConstraintViolation("initialPerHourPrice", MessageKeys.Work.NOT_NULL));
			}
			if (!request.getWork().getPricing().isSetInitialNumberOfHours()) {
				errors.add(newConstraintViolation("initialNumberOfHours", MessageKeys.Work.NOT_NULL));
			}
			if (!request.getWork().getPricing().isSetAdditionalPerHourPrice()) {
				errors.add(newConstraintViolation("additionalPerHourPrice", MessageKeys.Work.NOT_NULL));
			}
			if (!request.getWork().getPricing().isSetMaxBlendedNumberOfHours()) {
				errors.add(newConstraintViolation("maxBlendedNumberOfHours", MessageKeys.Work.NOT_NULL));
			}
		}
		
		if (request.getWork().getConfiguration() != null && request.getWork().getConfiguration().getPaymentTermsDays() > Constants.MAX_PAYMENT_TERMS_DAYS) {
			errors.add(new ConstraintViolation()
				.setProperty("paymentTermsDays")
				.setWhy(messageHelper.getMessage("work.save.invalid_payment_terms", Constants.MAX_PAYMENT_TERMS_DAYS)));
		}

		BeanUtilities.copyProperties(strategy.getFullPricingStrategy(), request.getWork().getPricing());
	}

	protected void validateParts(WorkSaveRequest request, List<ConstraintViolation> errors) {
		Assert.notNull(request);
		Assert.notNull(request.getWork());

		Work work = request.getWork();
		PartGroupDTO partGroup = request.getWork().getPartGroup();
		if (partGroup == null) {
			return;
		}

		BindingResult binding = new DataBinder(partGroup).getBindingResult();
		partGroupValidator.validate(partGroup, binding);

		if (binding.hasErrors()) {
			for (ObjectError e : binding.getAllErrors()) {
				errors.add(new ConstraintViolation().setWhy(messageHelper.getMessage(e.getDefaultMessage())));
			}
		}

		if (ShippingDestinationType.ONSITE.equals(partGroup.getShippingDestinationType()) && work.isOffsiteLocation()) {
			errors.add(newConstraintViolation("shippingDestination", "partsAndLogistics.shippingDestinationType.onsite.no_work_address"));
		}
	}

	protected void validateCustomFields(WorkSaveRequest request, List<ConstraintViolation> errors) {
		// Validate that any required custom field groups are present, and
		// Validate that any required fields in each group have values.
		// Do not validate if custom fields are disabled

		com.workmarket.domains.model.User actor = authenticationService.getCurrentUser();
		// return right away if custom fields aren't enabled or
		// if the assignment is a bundle parent
		ManageMyWorkMarket manageMyWorkMarket = companyService.getManageMyWorkMarket(actor.getCompany().getId());
		if (!manageMyWorkMarket.getCustomFieldsEnabledFlag() ||
			workBundleService.isAssignmentBundle(request.getWork().getId())) {
			return;
		}
		com.workmarket.domains.model.customfield.WorkCustomFieldGroup requiredGroup = customFieldService.findRequiredWorkCustomFieldGroup(actor.getCompany().getId());

		if (!request.getWork().isSetCustomFieldGroups()) {
			if (requiredGroup != null) {
				errors.add(newConstraintViolation("customFieldGroup", MessageKeys.Work.CUSTOM_FIELD_GROUP_REQUIRED, requiredGroup.getId().toString()));
			}
			return;
		}

		// Default to true if nothing is required
		boolean requiredGroupProvided = (requiredGroup == null);

		for (CustomFieldGroup customFieldGroup : request.getWork().getCustomFieldGroups()) {

			if (!requiredGroupProvided && requiredGroup != null && requiredGroup.getId().equals(customFieldGroup.getId())) {
				requiredGroupProvided = true;
			}

			Map<Long, WorkCustomField> requiredFields = AbstractEntityUtilities.newEntityIdMap(customFieldService.findRequiredBuyerFieldsForCustomFieldGroup(customFieldGroup.getId()));

			if (customFieldGroup.hasFields()) {
				for (CustomField field : customFieldGroup.getFields()) {
					if (isNotEmpty(field.getValue())) {
						if (field.getValue().length() > MAX_CUSTOM_FIELDS_LENGTH) {
							errors.add(new ConstraintViolation()
								.setWhy(messageHelper.getMessage("work.save.custom_fields.length", field.getName())));
							field.setValue(null);
						} else {
							requiredFields.remove(field.getId());
						}
					}
				}
			}

			if (MapUtils.isNotEmpty(requiredFields)) {
				for (Long fid : requiredFields.keySet()) {
					errors.add(new ConstraintViolation()
						.setWhy(messageHelper.getMessage("NotNull", requiredFields.get(fid).getName())));
				}
			}
		}

		if (!requiredGroupProvided) {
			errors.add(newConstraintViolation("customFieldGroup", MessageKeys.Work.CUSTOM_FIELD_GROUP_REQUIRED, requiredGroup.getId().toString()));
		}
	}

	protected void validateLabels(final WorkSaveRequest request, List<ConstraintViolation> errors) {
		if (!request.isSetLabelId()) {
			return;
		}

		Set<WorkSubStatusTypeWorkStatusScope> labelScopes = workSubStatusService.findAllScopesForSubStatusId(request.getLabelId());
		// validate label scope
		if (!labelScopes.isEmpty() && !Iterables.tryFind(labelScopes, new Predicate<WorkSubStatusTypeWorkStatusScope>() {
			@Override public boolean apply(WorkSubStatusTypeWorkStatusScope scope) {
				return scope.getWeak().getCode().equals(request.getWork().getStatus().getCode());
			}
		}).isPresent()) {
			errors.add(newConstraintViolation("label", MessageKeys.Work.LABEL_INVALID_SCOPE));
		}
	}

	private void validateDeliverables(final WorkSaveRequest request, List<ConstraintViolation> errors) {
		validateDeliverables(request, errors, false);
	}

	private void validateDeliverables(final WorkSaveRequest request, List<ConstraintViolation> errors, boolean isDraftOrTemplate) {
		Work work = request.getWork();
		if (work == null || work.getDeliverableRequirementGroupDTO() == null) {
			return;
		}

		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = work.getDeliverableRequirementGroupDTO();

		//If editing pre-existing deliverable group, ensure this group belongs to this assignment AND this assignment exists
		if (deliverableRequirementGroupDTO.getId() != null && request.getWork().getId() > 0) {
			AbstractWork workModel = workService.findWork(request.getWork().getId());
			if (workModel == null) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("deliverable.validation.genericError")));
				return;
			}

			DeliverableRequirementGroup deliverableRequirementGroup = workModel.getDeliverableRequirementGroup();
			if (deliverableRequirementGroup == null || !deliverableRequirementGroup.getId().equals(deliverableRequirementGroupDTO.getId())) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("deliverable.validation.genericError")));
				return;
			}
		}

		List<DeliverableRequirementDTO> deliverableRequirementDTOs = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();
		if (deliverableRequirementDTOs == null) {
			deliverableRequirementDTOs = Lists.newArrayList();
		}

		//If editing a pre-existing deliverableRequirement, ensure these requirements belong to this deliverableGroup
		if (CollectionUtils.isNotEmpty(deliverableRequirementDTOs)) {
			List<Long> savedDeliverableRequirementIds = deliverableRequirementDAO.findAllDeliverableRequirementIdsByGroupId(deliverableRequirementGroupDTO.getId());
			for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
				Long deliverableRequirementId = deliverableRequirementDTO.getId();
				if (deliverableRequirementId != null && !savedDeliverableRequirementIds.contains(deliverableRequirementId)) {
					errors.add(new ConstraintViolation()
						.setWhy(messageHelper.getMessage("deliverable.validation.genericError")));
					return;
				}
			}
		}

		int numberOfHours = deliverableRequirementGroupDTO.getHoursToComplete();

		if (!VALID_DELIVERABLE_DEADLINE_HOURS.containsKey(numberOfHours)) {
			errors.add(new ConstraintViolation()
				.setWhy(messageHelper.getMessage("deliverable.validation.invalidNumberOfHours", numberOfHours)));
		}

		for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
			String type = deliverableRequirementDTO.getType();
			int numberOfFiles = deliverableRequirementDTO.getNumberOfFiles();
			if (!WorkAssetAssociationType.DELIVERABLE_TYPES.contains(type)) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("deliverable.validation.invalidType", type)));
			}
			if (numberOfFiles < MIN_DELIVERABLE_FILE_REQUIREMENT || numberOfFiles > MAX_DELIVERABLE_FILE_REQUIREMENT) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("deliverable.validation.invalidNumberOfFiles", WorkAssetAssociationType.DELIVERABLE_TYPE_TO_NAME_MAP.get(type))));
			}
			if (!isDraftOrTemplate && StringUtils.isBlank(deliverableRequirementDTO.getInstructions())) {
				errors.add(new ConstraintViolation()
					.setWhy(messageHelper.getMessage("deliverable.validation.invalidInstructions",
						WorkAssetAssociationType.DELIVERABLE_TYPE_TO_NAME_MAP.get(type))));
			}
		}
	}

	protected ConstraintViolation newConstraintViolation(String property, String error, String... params) {
		ConstraintViolation v = new ConstraintViolation()
			.setProperty(property)
			.setError(error)
			.setWhy(messageHelper.getMessage(error));
		v.addToParams(Lists.newArrayList(params));
		return v;
	}

	private void logErrors(List<ConstraintViolation> errors) {
		for (ConstraintViolation error : errors)
			logger.error(error);
	}
}
