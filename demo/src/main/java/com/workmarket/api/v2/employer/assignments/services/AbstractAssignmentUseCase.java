package com.workmarket.api.v2.employer.assignments.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.employer.assignments.models.AssignmentDTO;
import com.workmarket.api.v2.employer.assignments.models.AvailableFundsApiDTO;
import com.workmarket.api.v2.employer.assignments.models.ConfigurationDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverableDTO;
import com.workmarket.api.v2.employer.assignments.models.DeliverablesGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.DocumentDTO;
import com.workmarket.api.v2.employer.assignments.models.PricingDTO;
import com.workmarket.api.v2.employer.assignments.models.RecurrenceDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingCandidates;
import com.workmarket.api.v2.employer.assignments.models.RoutingCandidatesDTO;
import com.workmarket.api.v2.employer.assignments.models.RoutingDTO;
import com.workmarket.api.v2.employer.assignments.models.ScheduleDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentDTO;
import com.workmarket.api.v2.employer.assignments.models.ShipmentGroupDTO;
import com.workmarket.api.v2.employer.assignments.models.TemplateDTO;
import com.workmarket.api.v2.model.ContactDTO;
import com.workmarket.api.v2.model.CustomFieldDTO;
import com.workmarket.api.v2.model.CustomFieldGroupDTO;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.api.v2.model.SurveyDTO;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.option.CompanyOption;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.PostalCode;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.model.part.ShippingDestinationType;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkRoutingService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.EntityToObjectFactory;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.WorkResponsePricingHelper;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Phone;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.Upload;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.CustomField;
import com.workmarket.thrift.work.CustomFieldGroup;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Project;
import com.workmarket.thrift.work.Resource;
import com.workmarket.thrift.work.RoutingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkAuthorizationException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.helpers.WorkAuthorizationFailureHelper;
import com.workmarket.web.helpers.WorkStatusValidationHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.workmarket.utility.CollectionUtilities.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

public abstract class AbstractAssignmentUseCase<T, K> implements UseCase<T, K> {

	private final static int CLIENT_LOCATION_NEW = 0;
	private final static int CLIENT_LOCATION_OFFSITE = -1;
	private final static int CLIENT_LOCATION_COMPANY = -2;
	public final static String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mmaa";

	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileService profileService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private DirectoryService directoryService;
	@Autowired private EntityToObjectFactory objectFactory;
	@Autowired private UserService userService;
	@Autowired private PartService partService;
	@Autowired private VendorService vendorService;
	@Autowired private WorkResponsePricingHelper workResponsePricingHelper;
	@Autowired private WorkAuthorizationFailureHelper workAuthorizationFailureHelper;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private SecurityContextFacade securityContextFacade;
	@Autowired private WorkStatusValidationHelper workStatusValidationHelper;
	@Autowired @Qualifier("accountRegisterServicePrefundImpl") protected AccountRegisterService accountRegisterServicePrefundImpl;
	@Autowired protected PricingService pricingService;
	@Autowired protected CompanyService companyService;
	@Qualifier("companyOptionsService") @Autowired private OptionsService<Company> companyOptionsService;

	protected String id;
	protected boolean readyToSend;
	protected Work work;
	protected User user;
	protected WorkSaveRequest workSaveRequest;
	protected WorkRequest workRequest;
	protected WorkResponse workResponse;
	protected AssignmentDTO assignmentDTO;
	protected AssignmentDTO.Builder assignmentDTOBuilder = new AssignmentDTO.Builder();
	protected Template template;
	protected TemplateDTO templateDTO;
	protected TemplateDTO.Builder templateDTOBuilder = new TemplateDTO.Builder();
	protected Schedule schedule;
	protected ScheduleDTO scheduleDTO;
	protected ScheduleDTO.Builder scheduleDTOBuilder = new ScheduleDTO.Builder();
	protected Address address;
	protected Location location;
	protected LocationDTO locationDTO;
	protected LocationDTO.Builder locationDTOBuilder = new LocationDTO.Builder();
	protected User locationContact;
	protected ContactDTO locationContactDTO;
	protected ContactDTO.Builder locationContactDTOBuilder = new ContactDTO.Builder();
	protected User secondaryLocationContact;
	protected ContactDTO secondaryLocationContactDTO;
	protected ContactDTO.Builder secondaryLocationContactDTOBuilder = new ContactDTO.Builder();
	protected User buyer;
	protected User supportContact;
	protected PricingStrategy pricing;
	protected PricingDTO pricingDTO;
	protected PricingDTO.Builder pricingDTOBuilder = new PricingDTO.Builder();
	protected List<RoutingStrategy> routing;
	protected RoutingDTO routingDTO;
	protected RoutingDTO.Builder routingDTOBuilder = new RoutingDTO.Builder();
	protected RoutingCandidatesDTO.Builder routingNeedToApplyCandidatesDTOBuilder = new RoutingCandidatesDTO.Builder();
	protected RoutingCandidatesDTO.Builder routingFirstToAcceptCandidatesDTOBuilder = new RoutingCandidatesDTO.Builder();
	protected List<CustomFieldGroup> customFieldGroups;
	//changed to LinkedHashSet to avoid random ordering of the CustomFieldGroupDTO and CustomFieldGroupDTO.Builder objects
	protected Set<CustomFieldGroupDTO> customFieldGroupDTOs = Sets.newLinkedHashSet();
	protected Set<CustomFieldGroupDTO.Builder> customFieldGroupDTOBuilders = Sets.newLinkedHashSet();
	protected PartGroupDTO shipments;
	protected ShipmentGroupDTO shipmentGroupDTO;
	protected ShipmentGroupDTO.Builder shipmentGroupDTOBuilder = new ShipmentGroupDTO.Builder();
	protected List<Assessment> surveys;
	protected Set<SurveyDTO> surveyDTOs = Sets.newHashSet();
	protected Set<SurveyDTO.Builder> surveyDTOBuilders = Sets.newHashSet();
	protected TreeSet<Asset> documents;
	protected RecurrenceDTO.Builder recurrenceDTOBuilder = new RecurrenceDTO.Builder();
	protected BigDecimal spendingLimit;
	protected BigDecimal aplLimit;
	protected AvailableFundsApiDTO.Builder availableFundsApiDTOBuilder = new AvailableFundsApiDTO.Builder();
	/*
		We need a separate collection here for new uploaded files when creating/editing an assignment.
		A new uploaded file needs to be associated with an assignment first to become an asset. Associating
		an Upload and an Asset with an assignment are two different flows.
	 */
	protected List<Upload> uploads;
	protected Set<DocumentDTO> documentDTOs;
	protected Set<DocumentDTO.Builder> documentDTOBuilders = Sets.newHashSet();
	protected ManageMyWorkMarket configuration;
	protected ConfigurationDTO configurationDTO;
	protected ConfigurationDTO.Builder configurationDTOBuilder = new ConfigurationDTO.Builder();
	protected Exception exception;
	protected DeliverableRequirementGroupDTO deliverablesGroup;
	protected DeliverablesGroupDTO deliverablesGroupDTO;
	protected DeliverablesGroupDTO.Builder deliverablesGroupDTOBuilder = new DeliverablesGroupDTO.Builder();

	private MessageBundle messages = MessageBundle.newInstance();
	private WorkAuthorizationResponse workAuthorizationResponse;


	protected abstract T me();
	protected abstract T handleExceptions() throws Exception;

	@Override
	public T execute() {
		try {
			failFast();
			init();
			prepare();
			process();
			save();
			finish();
		} catch (ValidationException | WorkActionException | WorkAuthorizationException e) {
			exception = e;
		}
		return me();
	}

	protected void failFast() {
		// no-op default implementation
		// override to add behavior
	}

	protected void init() throws WorkActionException {
		// no-op default implementation
		// override to add behavior
	}

	protected void prepare() {
		// no-op default implementation
		// override to add behavior
	}

	protected void process() {
		// no-op default implementation
		// override to add behavior
	}

	protected void save() throws ValidationException, WorkAuthorizationException {
		// no-op default implementation
		// override to add behavior
	}

	protected void finish() throws WorkAuthorizationException{
		// no-op default implementation
		// override to add behavior
	}

	protected void copyTemplateDTO() {
		templateDTOBuilder = new TemplateDTO.Builder(templateDTO);
	}

	protected void copyAssignmentDTO() {
		assignmentDTOBuilder = new AssignmentDTO.Builder(assignmentDTO);
	}

	protected void copyScheduleDTO() {
		scheduleDTOBuilder = new ScheduleDTO.Builder(scheduleDTO);
	}

	protected void copyLocationDTO() {
		locationDTOBuilder = new LocationDTO.Builder(locationDTO);
	}

	protected void copyLocationContactDTO() {
		if (locationContactDTO != null) {
			locationContactDTOBuilder = new ContactDTO.Builder(locationContactDTO);
		}
	}

	protected void copySecondaryLocationContactDTO() {
		if (secondaryLocationContactDTO != null) {
			secondaryLocationContactDTOBuilder = new ContactDTO.Builder(secondaryLocationContactDTO);
		}
	}

	protected void copyPricingDTO() {
		pricingDTOBuilder = new PricingDTO.Builder(pricingDTO);
	}

	protected void copyRoutingDTO() {
		routingDTOBuilder = new RoutingDTO.Builder(routingDTO);
	}

	protected void copyCustomFieldGroupDTOs() {
		for (CustomFieldGroupDTO customFieldGroupDTO : customFieldGroupDTOs) {
			customFieldGroupDTOBuilders.add(new CustomFieldGroupDTO.Builder(customFieldGroupDTO));
		}
	}

	protected void copyShipmentGroupDTO() {
		if (shipmentGroupDTO != null) {
			shipmentGroupDTOBuilder = new ShipmentGroupDTO.Builder(shipmentGroupDTO);
		}
	}

	protected void copySurveyDTOs() {
		for (SurveyDTO surveyDTO : surveyDTOs) {
			surveyDTOBuilders.add(new SurveyDTO.Builder(surveyDTO));
		}
	}

	protected void copyDocumentDTOs() {
		for (DocumentDTO documentDTO : documentDTOs) {
			documentDTOBuilders.add(new DocumentDTO.Builder(documentDTO));
		}
	}

	protected void copyDeliverablesGroupDTO() {
		deliverablesGroupDTOBuilder = new DeliverablesGroupDTO.Builder(deliverablesGroupDTO);
	}

	protected void copyConfigurationDTO() {
		configurationDTOBuilder = new ConfigurationDTO.Builder(configurationDTO);
	}

	protected void getUser() {
		// current user
		user = new User().setId(authenticationService.getCurrentUser().getId());
	}

	protected Company getCompany() {
		return profileService.findCompany(user.getId());
	}

	private CompanyPreference getCompanyPreference() {
		return getCompany().getCompanyPreference();
	}

	protected void generateWorkRequest() {
		Assert.notNull(user);
		Assert.notNull(id);

		workRequest = new WorkRequest()
			.setUserId(user.getId())
			.setWorkNumber(id)
			.setIncludes(ImmutableSet.<WorkRequestInfo>builder()
				.add(WorkRequestInfo.CONTEXT_INFO)
				.add(WorkRequestInfo.STATUS_INFO)
				.add(WorkRequestInfo.INDUSTRY_INFO)
				.add(WorkRequestInfo.PROJECT_INFO)
				.add(WorkRequestInfo.CLIENT_COMPANY_INFO)
				.add(WorkRequestInfo.BUYER_INFO)
				.add(WorkRequestInfo.LOCATION_CONTACT_INFO)
				.add(WorkRequestInfo.SUPPORT_CONTACT_INFO)
				.add(WorkRequestInfo.LOCATION_INFO)
				.add(WorkRequestInfo.SCHEDULE_INFO)
				.add(WorkRequestInfo.PRICING_INFO)
				.add(WorkRequestInfo.ASSETS_INFO)
				.add(WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO)
				.add(WorkRequestInfo.WORKFLOW_INFO)
				.add(WorkRequestInfo.PARTS_INFO)
				.add(WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO)
				.add(WorkRequestInfo.PAYMENT_INFO)
				.add(WorkRequestInfo.REQUIRED_ASSESSMENTS_INFO)
				.add(WorkRequestInfo.REQUIREMENT_SET_INFO)
				.add(WorkRequestInfo.DELIVERABLES_INFO)
				.add(WorkRequestInfo.RESOURCES_INFO)
				.add(WorkRequestInfo.GROUP_INFO)
				.add(WorkRequestInfo.FOLLOWER_INFO)
				.build()
			);
	}

	protected void getWorkResponse() throws WorkActionException {
		Assert.notNull(workRequest);
		workResponse = tWorkFacadeService.findWork(workRequest);
	}

	protected void getWork() {
		Assert.notNull(workResponse);
		work = workResponse.getWork();
	}

	protected void getTemplate() {
		//template should be defined here
		Assert.notNull(work);
		template = work.getTemplate();
	}

	protected void getSchedule() {
		Assert.notNull(work);
		schedule = work.getSchedule();
	}

	protected void getAddress() {
		if (location != null) {
			address = location.getAddress();
		}
	}

	protected void getLocation() {
		Assert.notNull(work);
		location = work.getLocation();
	}

	protected void getBuyer() {
		Assert.notNull(work);
		if (work.getBuyer() != null) {
			buyer = work.getBuyer();
		} else {
			buyer = new User();
		}
	}

	protected void getSupportContact() {
		Assert.notNull(work);
		if (work.getSupportContact() != null) {
			supportContact = work.getSupportContact();
		} else {
			supportContact = new User();
		}
	}

	protected void getLocationContact() {
		Assert.notNull(work);
		locationContact = work.getLocationContact();
	}

	protected void getSecondaryLocationContact() {
		Assert.notNull(work);
		secondaryLocationContact = work.getSecondaryLocationContact();
	}

	protected void getPricing() {
		Assert.notNull(work);
		pricing = work.getPricing();
	}

	protected void getRouting() {
		Assert.notNull(work);
		if (work.getRoutingStrategies() != null) {
			routing = work.getRoutingStrategies();
		} else {
			routing = Lists.newArrayList();
		}
	}

	protected void getCustomFieldGroups() {
		Assert.notNull(work);
		if (work.getCustomFieldGroups() != null) {
			customFieldGroups = work.getCustomFieldGroups();
		} else {
			customFieldGroups = Lists.newArrayList();
		}
	}

	protected void getShipments() {
		Assert.notNull(work);
		shipments = work.getPartGroup();
	}

	protected void getSurveys() {
		Assert.notNull(work);
		if (work.getAssessments() != null) {
			surveys = work.getAssessments();
		} else {
			surveys = Lists.newArrayList();
		}
	}

	protected void getDocuments() {
		Assert.notNull(work);
		if (work.getAssets() != null) {
			documents = work.getAssets();
		} else {
			documents = Sets.newTreeSet();
		}
	}

	protected void getDeliverablesGroup() {
		Assert.notNull(work);
		if (work.getDeliverableRequirementGroupDTO() != null) {
			deliverablesGroup = work.getDeliverableRequirementGroupDTO();
		} else {
			deliverablesGroup = new DeliverableRequirementGroupDTO();
		}
	}

	protected void getAvailableFunds(){
		Assert.notNull(accountRegisterServicePrefundImpl);
		Assert.notNull(companyService);
		Assert.notNull(pricingService);

		spendingLimit = accountRegisterServicePrefundImpl.calcSufficientBuyerFundsByCompany(getCompany().getId());
		if (companyService.hasPaymentTermsEnabled(getCompany().getId())) {
			aplLimit = pricingService.calculateRemainingAPBalance(getCompany().getId());
		}
	}

	protected void getConfiguration() {
		Assert.notNull(work);
		configuration = work.getConfiguration();
	}

	protected void getAssignmentDTO() {
		Assert.notNull(templateDTO);
		assignmentDTO = templateDTO.getAssignment();
	}

	protected void getScheduleDTO() {
		Assert.notNull(assignmentDTO);
		scheduleDTO = assignmentDTO.getSchedule();
	}

	protected void getLocationDTO() {
		Assert.notNull(assignmentDTO);
		locationDTO = assignmentDTO.getLocation();
	}

	protected void getLocationContactDTO() {
		Assert.notNull(locationDTO);
		locationContactDTO = locationDTO.getContact();
	}

	protected void getSecondaryLocationContactDTO() {
		Assert.notNull(locationDTO);
		secondaryLocationContactDTO = locationDTO.getSecondaryContact();
	}

	protected void getPricingDTO() {
		Assert.notNull(assignmentDTO);
		pricingDTO = assignmentDTO.getPricing();
	}

	protected void getRoutingDTO() {
		Assert.notNull(assignmentDTO);
		routingDTO = assignmentDTO.getRouting();
	}

	protected void getCustomFieldGroupDTOs() {
		Assert.notNull(assignmentDTO);
		customFieldGroupDTOs = assignmentDTO.getCustomFieldGroups();
	}

	protected void getShipmentDTOs() {
		Assert.notNull(assignmentDTO);
		shipmentGroupDTO = assignmentDTO.getShipmentGroup();
	}

	protected void getSurveyDTOs() {
		Assert.notNull(assignmentDTO);
		surveyDTOs = assignmentDTO.getSurveys();
	}

	protected void getDocumentDTOs() {
		Assert.notNull(assignmentDTO);
		documentDTOs = assignmentDTO.getDocuments();
	}

	protected void getDeliverablesGroupDTO() {
		Assert.notNull(assignmentDTO);
		deliverablesGroupDTO = assignmentDTO.getDeliverablesGroup();
	}


	protected void getConfigurationDTO() {
		Assert.notNull(assignmentDTO);
		configurationDTO = assignmentDTO.getConfiguration();
	}

	protected void createWork() {
		work = new Work();
	}

	protected void createTemplate() {
		template = new Template();
	}

	protected void createSchedule() {
		schedule = new Schedule();
	}

	protected void createAddress() {
		address = new Address();
	}

	protected void createLocation() {
		location = new Location();
	}

	protected void createBuyer() {
		buyer = new User();
	}

	protected void createSupportContact() {
		supportContact = new User();
	}

	protected void createLocationContact() {
		locationContact = new User();
	}

	protected void createSecondaryLocationContact() {
		secondaryLocationContact = new User();
	}

	protected void createPricing() {
		pricing = new PricingStrategy();
	}

	protected void createRouting() {
		routing = Lists.newArrayList();
	}

	protected void createCustomFieldGroups() {
		customFieldGroups = Lists.newArrayList();
	}

	protected void createShipments() {
		shipments = new PartGroupDTO();
	}

	protected void createSurveys() {
		surveys = Lists.newArrayList();
	}

	protected void createDocuments() {
		documents = Sets.newTreeSet();
	}

	protected void createDocumentUploads() {
		uploads = Lists.newArrayList();
	}

	protected void createDeliverablesGroup() {
		deliverablesGroup = new DeliverableRequirementGroupDTO();
	}

	protected void createConfiguration() {
		configuration = new ManageMyWorkMarket();
	}

	protected void loadWork() {
		Assert.notNull(work);

		if (assignmentDTO != null) {
			work
				.setTitle(assignmentDTO.getTitle())
				.setDescription(assignmentDTO.getDescription())
				.setInstructions(assignmentDTO.getInstructions())
				.setPrivateInstructions(assignmentDTO.isInstructionsPrivate())
				.setDesiredSkills(assignmentDTO.getSkills())
				.setRequirementSetIds(assignmentDTO.getRequirementSetIds())
				.setUniqueExternalIdValue(assignmentDTO.getUniqueExternalId())
				.setDocumentsEnabled(assignmentDTO.getConfiguration().getDocumentsEnabled());

			// TODO[Jim]: not happy with the location of this chunk but haven't decided
			//   on a better place for it yet
			if (!assignmentDTO.getFollowerIds().isEmpty()) {
				work
					.setFollowers(
						ImmutableList.copyOf(
							userService.findAllUserIdsByUserNumbers(assignmentDTO.getFollowerIds())
						)
					);
			}

			if (assignmentDTO.getIndustryId() != null) {
				work.setIndustry(new Industry(assignmentDTO.getIndustryId(), null));
			}

			if (assignmentDTO.getProjectId() != null) {
				work.setProject(new Project(assignmentDTO.getProjectId(), null, null));
			}
		}

		if (schedule != null) {
			work.setSchedule(schedule)
				.setResourceConfirmationRequired(scheduleDTO.isConfirmationRequired())
				.setResourceConfirmationHours(scheduleDTO.getConfirmationLeadTime())
				.setCheckinCallRequired(scheduleDTO.isCheckinCallRequired())
				.setCheckinContactName(scheduleDTO.getCheckinContactName())
				.setCheckinContactPhone(scheduleDTO.getCheckinContactPhone())
				.setShowCheckoutNotesFlag(scheduleDTO.isCheckoutNoteDisplayed())
				.setCheckoutNoteInstructions(scheduleDTO.getCheckoutNote());

			configuration.setCheckinRequiredFlag(scheduleDTO.isCheckinRequired());
		}

		if (location != null) {
			if (location.getId() == CLIENT_LOCATION_OFFSITE) {
				work.setLocation(null)
					.setOffsiteLocation(true)
					.setNewLocation(false);
			} else if (location.isSetId()) {
				work.setLocation(location)
					.setNewLocation(false)
					.setOffsiteLocation(false);
			} else if (location.isSetAddress()) {
				work.setLocation(location)
					.setOffsiteLocation(false)
					.setNewLocation(true);
			} else {
				work.setOffsiteLocation(null)
					.setNewLocation(false);
			}

			work.setClientCompany(location.getCompany());
		}

		if (buyer != null) {
			work.setBuyer(buyer);
		} else if (user != null) {
			// If ownerId is not provided, set owner to current user
			work.setBuyer(user);
		}

		if (supportContact != null) {
			work.setSupportContact(supportContact);
		}

		if (locationContact != null) {
			work.setLocationContact(locationContact);
		}

		if (secondaryLocationContact != null) {
			work.setSecondaryLocationContact(secondaryLocationContact);
		}

		if (pricing != null) {
			work.setPricing(pricing);
		}

		if (routing != null) {
			work.setRoutingStrategies(routing);
			if (routingDTO != null) {
				work.setShowInFeed(routingDTO.isShownInFeed());
				configuration
					.setShowInFeed(routingDTO.isShownInFeed())
					.setSmartRoute(routingDTO.isSmartRoute());
			}
		}

		if (customFieldGroups != null) {
			work.setCustomFieldGroups(customFieldGroups);
		}

		work.setPartGroup(shipments);

		if (surveys != null) {
			work.setAssessments(surveys);
		}

		if (isNotEmpty(documents)) {
			work.setAssets(documents);
		}

		if (isNotEmpty(uploads)) {
			work.setUploads(uploads);
		}

		if (deliverablesGroup != null) {
			work.setDeliverableRequirementGroupDTO(deliverablesGroup);
		}

		if (configuration != null) {
			work.setConfiguration(configuration);
		}

		work.setTemplate(template); // if template is null, it's not a template
	}

	protected void loadTemplate() {
		Assert.notNull(template);
		Assert.notNull(templateDTO);

		template
			.setWorkNumber(templateDTO.getId())
			.setName(templateDTO.getName())
			.setDescription(templateDTO.getDescription());
	}

	private String getTimeZone() {
		String timeZone = profileService.getTimeZoneByUserId(user.getId()).getTimeZoneId();

		if (address != null && address.getZip() != null && isNotBlank(address.getCountry())) {
			AddressDTO addressDTO = new AddressDTO();
			BeanUtilities.copyProperties(addressDTO, address);
			addressDTO.setAddress1(address.getAddressLine1());
			addressDTO.setAddress2(address.getAddressLine2());
			addressDTO.setPostalCode(address.getZip());
			if (address.getPoint() != null &&
				address.getPoint().isSetLatitude() &&
				address.getPoint().isSetLongitude()) {
				addressDTO.setLatitude(BigDecimal.valueOf(address.getPoint().getLatitude()));
				addressDTO.setLongitude(BigDecimal.valueOf(address.getPoint().getLongitude()));
			}

			PostalCode postalCode = invariantDataService.findOrCreatePostalCode(addressDTO);
			if (postalCode != null) {
				return postalCode.getTimeZoneName();
			}
		}

		if (address != null) {
			String zip = address.getZip();
			String countryId = null;
			if (StringUtils.isNotEmpty(zip)) {
				if (StringUtils.isEmpty(countryId)) {
					if (PostalCode.canadaPattern.matcher(zip).matches()) {
						countryId = Country.CANADA;
					} else if (PostalCode.usaPattern.matcher(zip).matches()) {
						countryId = Country.USA;
					}
				}
				PostalCode postalCode = StringUtils.isEmpty(countryId) ?
					invariantDataService.getPostalCodeByCode(zip) : invariantDataService.getPostalCodeByCodeAndCountryId(zip, countryId);
				if (postalCode != null) {
					// set timeZone using assignment location timezone if available
					timeZone = postalCode.getTimeZoneName();
				}
			}
		}
		return timeZone;
	}

	protected void loadSchedule() {
		Assert.notNull(schedule);
		Assert.notNull(scheduleDTO);
		String timeZone = getTimeZone();

		if (scheduleDTO.isRange()) {
			schedule.setRange(true);
			if (StringUtils.isNotBlank(scheduleDTO.getFrom())) {
				long from = applyTimeZoneAndGetDateTimeInMillis(scheduleDTO.getFrom(), timeZone);
				schedule.setFrom(from);
			}

			if (StringUtils.isNotBlank(scheduleDTO.getThrough())) {
				long through = applyTimeZoneAndGetDateTimeInMillis(scheduleDTO.getThrough(), timeZone);
				schedule.setThrough(through);
			}
		} else {
			schedule.setRange(false);
			if (StringUtils.isNotBlank(scheduleDTO.getFrom())) {
				long start = applyTimeZoneAndGetDateTimeInMillis(scheduleDTO.getFrom(), timeZone);
				schedule.setFrom(start);
			}
		}
	}

	protected void loadAddress() {
		Assert.notNull(address);
		Assert.notNull(locationDTO);

		if (locationDTO.getLocationMode() == CLIENT_LOCATION_OFFSITE) {
			return;
		}

		com.workmarket.domains.model.Location clientLocation =
			locationDTO.getId() > CLIENT_LOCATION_NEW ? directoryService.findLocationById(locationDTO.getId()) : null;

		if (locationDTO.getClientCompanyId() != null && locationDTO.getId() != CLIENT_LOCATION_NEW) {
			address = objectFactory.newAddress(clientLocation.getAddress()).setAddressLine2(locationDTO.getAddressLine2());
		} else {
			address
				.setAddressLine1(locationDTO.getAddressLine1())
				.setAddressLine2(locationDTO.getAddressLine2())
				.setLocationType(locationDTO.getLocationType())
				.setCity(locationDTO.getCity())
				.setState(locationDTO.getState())
				.setZip(locationDTO.getZip())
				.setCountry(locationDTO.getCountry());

			if (locationDTO.getLatitude() != null && locationDTO.getLongitude() != null) {
				address.setPoint(new GeoPoint(locationDTO.getLatitude(), locationDTO.getLongitude()));
			}
		}
	}

	protected void loadLocation() {
		Assert.notNull(address);
		Assert.notNull(locationDTO);

		if (location == null) {
			createLocation();
		}

		location
			.setId(locationDTO.getId())
			.setName(locationDTO.getName())
			.setNumber(locationDTO.getNumber())
			.setInstructions(locationDTO.getInstructions());

		if (locationDTO.getClientCompanyId() != null) {
			location.setCompany(new com.workmarket.thrift.core.Company().setId(locationDTO.getClientCompanyId()));
		}

		if (locationDTO.getId() == CLIENT_LOCATION_OFFSITE) {
			location.setAddress(null);
		} else {
			if (address.getAddressLine1() != null) {
				location.setAddress(address);
			} else {
				location.setAddress(null);
			}
		}
	}

	protected void loadBuyer() {
		Long ownerId = userService.findUserId(assignmentDTO.getOwnerId());
		if (ownerId != null) {
			buyer = buyer
				.setId(ownerId)
				.setUserNumber(assignmentDTO.getOwnerId());
		} else {
			buyer = null;
		}
	}

	protected void loadSupportContact() {
		Long supportContactId = userService.findUserId(assignmentDTO.getSupportContactId());
		if (supportContactId != null) {
			supportContact = supportContact
				.setId(supportContactId)
				.setUserNumber(assignmentDTO.getSupportContactId());
		} else {
			supportContact = null;
		}
	}

	protected void loadLocationContact() {
		locationContact = loadContact(locationContact, locationContactDTO);
	}

	protected void loadSecondaryLocationContact() {
		secondaryLocationContact = loadContact(secondaryLocationContact, secondaryLocationContactDTO);
	}

	private User loadContact(User contact, ContactDTO contactDTO) {
		if (contactDTO == null) {
			return null;
		}

		if (contact == null) {
			contact = new User();
		}

		Profile profile = new Profile();
		profile.addToPhoneNumbers(
			new Phone()
				.setPhone(contactDTO.getWorkPhone())
				.setExtension(contactDTO.getWorkPhoneExtension())
				.setType(ContactContextType.WORK.name())
		);

		profile.addToPhoneNumbers(
			new Phone()
				.setPhone(contactDTO.getMobilePhone())
				.setType(ContactContextType.HOME.name())
		);

		return contact
			.setId(contactDTO.getId() == null ? 0L : contactDTO.getId())
			.setName(
				new Name(
					isNotBlank(contactDTO.getFirstName()) ? contactDTO.getFirstName() : "",
					isNotBlank(contactDTO.getLastName()) ? contactDTO.getLastName() : ""))
			.setProfile(profile)
			.setEmail(contactDTO.getEmail());
	}

	protected void loadPricing() {
		Assert.notNull(pricing);
		Assert.notNull(pricingDTO);

		if (pricingDTO.getType() != null) {
			pricing.setId(PricingStrategyType.valueOf(pricingDTO.getType().toUpperCase()).ordinal() + 1);
			pricing.setType(Enum.valueOf(PricingStrategyType.class, pricingDTO.getType()));
		}

		if (pricingDTO.getFlatPrice() != null) {
			pricing.setFlatPrice(pricingDTO.getFlatPrice());
		}

		if (pricingDTO.getPerHourPrice() != null) {
			pricing.setPerHourPrice(pricingDTO.getPerHourPrice());
		}

		if (pricingDTO.getMaxNumberOfHours() != null) {
			pricing.setMaxNumberOfHours(pricingDTO.getMaxNumberOfHours());
		}

		if (pricingDTO.getPerUnitPrice() != null) {
			pricing.setPerUnitPrice(pricingDTO.getPerUnitPrice());
		}

		if (pricingDTO.getMaxNumberOfUnits() != null) {
			pricing.setMaxNumberOfUnits(pricingDTO.getMaxNumberOfUnits());
		}

		if (pricingDTO.getInitialPerHourPrice() != null) {
			pricing.setInitialPerHourPrice(pricingDTO.getInitialPerHourPrice());
		}

		if (pricingDTO.getInitialNumberOfHours() != null) {
			pricing.setInitialNumberOfHours(pricingDTO.getInitialNumberOfHours());
		}

		if (pricingDTO.getAdditionalPerHourPrice() != null) {
			pricing.setAdditionalPerHourPrice(pricingDTO.getAdditionalPerHourPrice());
		}

		if (pricingDTO.getMaxBlendedNumberOfHours() != null) {
			pricing.setMaxBlendedNumberOfHours(pricingDTO.getMaxBlendedNumberOfHours());
		}

		pricing.setOfflinePayment(pricingDTO.isOfflinePayment());
	}

	protected void loadRouting() {
		Assert.notNull(user);
		Assert.notNull(routing);
		Assert.notNull(routingDTO);

		loadRouting(routingDTO, routingDTO.isAssignToFirstToAccept());
		loadRouting(routingDTO.getFirstToAcceptCandidates(), true);
		loadRouting(routingDTO.getNeedToApplyCandidates(), false);
	}

	private void loadRouting(RoutingCandidates routingDTO, boolean assignToFirstToAccept) {
		if (!isEmpty(routingDTO.getGroupIds())) {
			PeopleSearchRequest filter = new PeopleSearchRequest()
				.setUserId(user.getId())
				.setGroupFilter(routingDTO.getGroupIds());

			routing.add(
				new RoutingStrategy()
					.setFilter(filter)
					.setDelayMinutes(0)
					.setAssignToFirstToAccept(assignToFirstToAccept)
			);
		}

		if (!isEmpty(routingDTO.getResourceNumbers())) {
			routing.add(
				new RoutingStrategy()
					.setRoutingUserNumbers(routingDTO.getResourceNumbers())
					.setDelayMinutes(0)
					.setAssignToFirstToAccept(assignToFirstToAccept)
			);
		}

		if (!isEmpty(routingDTO.getVendorCompanyNumbers())) {
			routing.add(
				new RoutingStrategy()
					.setVendorCompanyNumbers(routingDTO.getVendorCompanyNumbers())
					.setDelayMinutes(0)
					.setAssignToFirstToAccept(assignToFirstToAccept)
			);
		}
	}

	protected void loadCustomFieldGroups() {
		Assert.notNull(customFieldGroups);
		Assert.notNull(customFieldGroupDTOs);

		customFieldGroups.clear(); // We're replacing whatever's there.
		for (CustomFieldGroupDTO customFieldGroupDTO : customFieldGroupDTOs) {
			CustomFieldGroup customFieldGroup = new CustomFieldGroup()
				.setId(customFieldGroupDTO.getId())
				.setName(customFieldGroupDTO.getName())
				.setIsRequired(customFieldGroupDTO.isRequired())
				.setPosition(customFieldGroupDTO.getPosition())
				.setFields(Lists.<CustomField>newArrayList());

			for (CustomFieldDTO field : customFieldGroupDTO.getFields()) {
				// custom fields with default values that contain commas are dropdown lists
				// we don't allow the custom field value to be saved containing the list of options
				String value =
					field.getValue() != null &&
						field.getDefaultValue() != null &&
						field.getValue().contains(",") &&
						field.getDefaultValue().contains(",") ? "" : field.getValue();
				customFieldGroup.addToFields(
					new CustomField()
						.setId(field.getId())
						.setName(field.getName())
						.setValue(value)
						.setDefaultValue(field.getDefaultValue())
						.setVisibleToResource(field.isVisibleToResource())
						.setVisibleToOwner(field.isVisibleToOwner())
						.setIsRequired(field.isRequired())
						.setType(field.getType())
						.setReadOnly(field.isReadOnly())
						.setShowOnPrintout(field.isShowOnPrintout())
						.setShowInAssignmentHeader(field.isShowInAssignmentHeader())
						.setShowOnSentStatus(field.isShowOnSentStatus())
				);
			}

			customFieldGroups.add(customFieldGroup);
		}
	}

	protected void loadShipments() {
		Assert.notNull(shipmentGroupDTO);
		Assert.notNull(configurationDTO);

		if (configurationDTO.isShipmentsEnabled()) {
			if (shipments == null) {
				createShipments();
			}

			com.workmarket.service.business.dto.LocationDTO shipToLocation;
			ShippingDestinationType shippingDestinationType = shipmentGroupDTO.getShippingDestinationType();
			switch (shippingDestinationType) {
				case PICKUP:
					shipToLocation = loadShippingLocation(shipmentGroupDTO.getShipToAddress());
					break;
				case ONSITE:
					shipToLocation = loadShippingLocation(locationDTO);
					break;
				case WORKER:
				default:
					shipToLocation = null;
			}

			com.workmarket.service.business.dto.LocationDTO returnLocation = null;
			if (shipmentGroupDTO.isReturnShipment()) {
				returnLocation = loadShippingLocation(shipmentGroupDTO.getReturnAddress());
			}

			shipments.setReturnRequired(shipmentGroupDTO.isReturnShipment());
			shipments.setShipToLocation(shipToLocation);
			shipments.setReturnToLocation(returnLocation);
			shipments.setSuppliedByWorker(shipmentGroupDTO.isSuppliedByWorker());
			shipments.setShippingDestinationType(shippingDestinationType);

			shipments.getParts().clear();
			for (ShipmentDTO shipmentDTO : shipmentGroupDTO.getShipments()) {
				PartDTO part = new PartDTO();
				part.setName(shipmentDTO.getName());
				part.setReturn(shipmentDTO.isReturn());
				part.setTrackingNumber(shipmentDTO.getTrackingNumber());
				part.setTrackingStatus(shipmentDTO.getTrackingStatus());
				part.setShippingProvider(shipmentDTO.getShippingProvider());
				part.setPartValue(shipmentDTO.getValue());
				part.setReturn(shipmentDTO.isReturn());
				shipments.addPart(part);
			}
		} else {
			shipments = null;
		}
	}

	private com.workmarket.service.business.dto.LocationDTO loadShippingLocation(LocationDTO shippingAddress) {
		com.workmarket.service.business.dto.LocationDTO shippingLocation = null;

		if (shippingAddress != null && !locationHasEmptyAddress(shippingAddress)) {
			shippingLocation = new com.workmarket.service.business.dto.LocationDTO(
				shippingAddress.getId() > 0 ? shippingAddress.getId() : null,
				shippingAddress.getName(),
				shippingAddress.getNumber(),
				shippingAddress.getAddressLine1(),
				shippingAddress.getAddressLine2(),
				shippingAddress.getCity(),
				shippingAddress.getState(),
				shippingAddress.getZip(),
				null,
				shippingAddress.getCountry()
			);

			if (shippingAddress.getLatitude() != null) {
				shippingLocation.setLatitude(new BigDecimal(shippingAddress.getLatitude()));
			}

			if (shippingAddress.getLongitude() != null) {
				shippingLocation.setLongitude(new BigDecimal(shippingAddress.getLongitude()));
			}
		}

		if (shippingAddress.getId() > CLIENT_LOCATION_NEW && locationHasEmptyAddress(shippingAddress)) {
			com.workmarket.domains.model.Location clientLocation = directoryService.findLocationById(locationDTO.getId());
			if (clientLocation != null) {
				shippingLocation = new com.workmarket.service.business.dto.LocationDTO(
					clientLocation.getId() > 0 ? clientLocation.getId() : null,
					clientLocation.getName(),
					clientLocation.getLocationNumber(),
					clientLocation.getAddress().getAddress1(),
					clientLocation.getAddress().getAddress2(),
					clientLocation.getAddress().getCity(),
					clientLocation.getAddress().getState().getShortName(),
					clientLocation.getAddress().getPostalCode(),
					null,
					clientLocation.getAddress().getCountry().getISO()
				);
			}
			if (clientLocation.getAddress().getLatitude() != null) {
				shippingLocation.setLatitude(clientLocation.getAddress().getLatitude());
			}

			if (clientLocation.getAddress().getLongitude() != null) {
				shippingLocation.setLongitude(clientLocation.getAddress().getLongitude());
			}
		}

		return shippingLocation;
	}

	protected void loadSurveys() {
		Assert.notNull(surveyDTOs);
		surveys.clear();

		for (SurveyDTO surveyDTO : surveyDTOs) {
			surveys.add(
				new Assessment()
					.setId(surveyDTO.getId())
					.setName(surveyDTO.getName())
					.setDescription(surveyDTO.getDescription())
					.setIsRequired(surveyDTO.getRequired())
			);
		}
	}

	protected void loadDocuments() {
		Assert.notNull(documentDTOs);
		documents.clear();

		for (DocumentDTO documentDTO : documentDTOs) {
			if (documentDTO.isUploaded()) {
				uploads.add(
					new Upload()
						.setUuid(documentDTO.getUuid())
						.setName(documentDTO.getName())
						.setDescription(documentDTO.getDescription())
						.setVisibilityCode(documentDTO.getVisibilityType())
				);
			} else {
				documents.add(
					new Asset()
						.setId(documentDTO.getId())
						.setUuid(documentDTO.getUuid())
						.setName(documentDTO.getName())
						.setDescription(documentDTO.getDescription())
						.setVisibilityCode(documentDTO.getVisibilityType())
				);
			}
		}
	}

	protected void loadDeliverablesGroup() {
		Assert.notNull(deliverablesGroup);
		Assert.notNull(deliverablesGroupDTO);

		Set<DeliverableRequirementDTO> deliverables = Sets.newHashSet();
		for (DeliverableDTO deliverableDTO : deliverablesGroupDTO.getDeliverables()) {
			deliverables.add(
				new DeliverableRequirementDTO()
					.setId(deliverableDTO.getId())
					.setType(deliverableDTO.getType())
					.setInstructions(deliverableDTO.getDescription())
					.setNumberOfFiles(deliverableDTO.getNumberOfFiles())
					.setPriority(deliverableDTO.getPriority())
			);
		}

		deliverablesGroup
			.setId(deliverablesGroupDTO.getId())
			.setInstructions(deliverablesGroupDTO.getInstructions())
			.setHoursToComplete(deliverablesGroupDTO.getHoursToComplete())
			.setDeliverableRequirementDTOs(ImmutableList.copyOf(deliverables));
	}

	protected void loadConfiguration() {
		Assert.notNull(user);
		Assert.notNull(configuration);
		Assert.notNull(configurationDTO);

		com.workmarket.domains.model.ManageMyWorkMarket mmw = getCompany().getManageMyWorkMarket();

		configuration
			.setCustomFieldsEnabledFlag(configurationDTO.isCustomFieldsEnabled())
			.setPartsLogisticsEnabledFlag(configurationDTO.isShipmentsEnabled())
			.setUseRequirementSets(configurationDTO.isRequirementSetsEnabled())
			.setCustomCloseOutEnabledFlag(configurationDTO.isDeliverablesEnabled())
			.setAssessmentsEnabled(configurationDTO.isSurveysEnabled());

		if (template == null && routingDTO != null) {
			configuration.setAssignToFirstResource(routingDTO.isAssignToFirstToAccept());
			configuration
				.setShowInFeed(routingDTO.isShownInFeed())
				.setSmartRoute(routingDTO.isSmartRoute());
		}

		if (pricingDTO != null) {
			configuration
				.setUseMaxSpendPricingDisplayModeFlag("spend".equals(pricingDTO.getMode()))
				.setPaymentTermsDays(
					pricingDTO.getPaymentTermsDays() != null ?
						pricingDTO.getPaymentTermsDays() :
						mmw.getPaymentTermsDays())
				.setDisablePriceNegotiation(
					pricingDTO.getDisablePriceNegotiation() != null ?
						pricingDTO.getDisablePriceNegotiation() :
						mmw.getDisablePriceNegotiation());

		}
	}

	protected void loadAssignmentDTO() {
		Assert.notNull(work);
		configurationDTOBuilder.setDocumentsEnabled(work.getDocumentsEnabled());

		assignmentDTOBuilder
			.setId(work.getWorkNumber())
			.setTitle(work.getTitle())
			.setDescription(work.getDescription())
			.setInstructions(work.getInstructions())
			.setInstructionsPrivate(work.isSetPrivateInstructions() && work.getPrivateInstructions())
			.setSkills(StringUtilities.stripXSSAndEscapeHtml(work.getDesiredSkills()))
			.setIndustryId(work.isSetIndustry() ? work.getIndustry().getId() : null)
			.setProjectId(work.isSetProject() ? work.getProject().getId() : null)
			.setUniqueExternalId(work.getUniqueExternalIdValue())
			.setRequirementSetIds(work.getRequirementSetIds())
			.setLocation(locationDTOBuilder)
			.setSchedule(scheduleDTOBuilder)
			.setPricing(pricingDTOBuilder)
			.setRouting(routingDTOBuilder)
			.setCustomFieldGroups(customFieldGroupDTOBuilders)
			.setShipmentGroup(shipmentGroupDTOBuilder)
			.setSurveys(surveyDTOBuilders)
			.setDocuments(documentDTOBuilders)
			.setDeliverablesGroup(deliverablesGroupDTOBuilder)
			.setConfiguration(configurationDTOBuilder);

		// TODO[Jim]: not happy with the location of this chunk but haven't decided
		//   on a better place for it yet
		if (!work.getFollowers().isEmpty()) {
			assignmentDTOBuilder
				.setFollowerIds(
					ImmutableList.copyOf(
						userService.findAllUserNumbersByUserIds(work.getFollowers())
					)
				);
		}

		if (supportContact != null) {
			assignmentDTOBuilder.setSupportContactId(supportContact.getUserNumber());
		}

		if (buyer != null) {
			assignmentDTOBuilder.setOwnerId(buyer.getUserNumber());
		}
		assignmentDTOBuilder.setRecurrence(recurrenceDTOBuilder);
	}

	protected void loadTemplateDTO() {
		Assert.notNull(work);
		Assert.notNull(template);

		templateDTOBuilder
			.setId(work.getWorkNumber())
			.setName(template.getName())
			.setDescription(template.getDescription())
			.setAssignment(assignmentDTOBuilder);
	}

	protected void loadScheduleDTO() {

		if (schedule != null) {
			if (StringUtils.isNotEmpty(work.getTimeZone())) {
				Date fromDate = DateUtilities.changeTimeZone(schedule.getFrom(), work.getTimeZone());
				String from = DateUtilities.format(DATE_TIME_FORMAT, fromDate);

				if (schedule.isRange()) {
					if (!DateUtilities.isNearEpoch(fromDate.getTime())) {
						scheduleDTOBuilder.setFrom(from);
					}
					Date throughDate = DateUtilities.changeTimeZone(schedule.getThrough(), work.getTimeZone());
					String through = DateUtilities.format(DATE_TIME_FORMAT, throughDate);
					if (!DateUtilities.isNearEpoch(throughDate.getTime())) {
						scheduleDTOBuilder.setThrough(through);
					}
				} else {
					if (!DateUtilities.isNearEpoch(fromDate.getTime())) {
						scheduleDTOBuilder.setFrom(from);
					}
				}
			} else {
				String timeZone = securityContextFacade.getCurrentUser().getTimeZoneId();
				scheduleDTOBuilder
					.setFrom(DateUtilities.formatMillis(DATE_TIME_FORMAT, schedule.getFrom(), timeZone))
					.setThrough(DateUtilities.formatMillis(DATE_TIME_FORMAT, schedule.getThrough(), timeZone));
			}

			scheduleDTOBuilder
				.setRange(schedule.isRange())
				.setConfirmationRequired(work.isResourceConfirmationRequired())
				.setConfirmationLeadTime(work.getResourceConfirmationHours())
				.setCheckinCallRequired(work.isCheckinCallRequired())
				.setCheckinContactName(work.getCheckinContactName())
				.setCheckinContactPhone(work.getCheckinContactPhone())
				.setCheckoutNoteDisplayed(work.isShowCheckoutNotesFlag())
				.setCheckoutNote(work.getCheckoutNoteInstructions());
		}

		if (configuration != null) {
			scheduleDTOBuilder.setCheckinRequired(configuration.isCheckinRequiredFlag());
		}
	}

	protected void loadLocationDTO() {
		//need to set the client company Id if present regardless whether location is virtual or not
		locationDTOBuilder.setClientCompanyId(work.isSetClientCompany() ? work.getClientCompany().getId() : null);

		if (location != null) {
			locationDTOBuilder
				.setId(location.getId())
				.setName(location.getName())
				.setNumber(location.getNumber())
				.setInstructions(location.getInstructions())
				.setContact(locationContactDTOBuilder)
				.setSecondaryContact(secondaryLocationContactDTOBuilder);

			if (work.isNewLocation()) {
				locationDTOBuilder.setLocationMode(CLIENT_LOCATION_NEW);
			} else {
				locationDTOBuilder.setLocationMode(CLIENT_LOCATION_COMPANY);
			}
		} else if (work.isOffsiteLocation()) {
			locationDTOBuilder.setId(CLIENT_LOCATION_OFFSITE)
				.setLocationMode(CLIENT_LOCATION_OFFSITE);
		} else {
			locationDTOBuilder.setLocationMode(CLIENT_LOCATION_NEW);
		}

		if (address != null) {
			locationDTOBuilder
				.setAddressLine1(address.getAddressLine1())
				.setAddressLine2(address.getAddressLine2())
				.setCity(address.getCity())
				.setState(address.getState())
				.setZip(address.getZip())
				.setCountry(address.getCountry())
				.setLongitude(address.getPoint() != null ? address.getPoint().getLongitude() : null)
				.setLatitude(address.getPoint() != null ? address.getPoint().getLatitude() : null)
				.setLocationType(address.getLocationType());
		}
	}

	protected void loadLocationContactDTO() {
		locationContactDTOBuilder = loadContactDTO(locationContact, locationContactDTOBuilder);
	}

	protected void loadSecondaryLocationContactDTO() {
		secondaryLocationContactDTOBuilder = loadContactDTO(secondaryLocationContact, secondaryLocationContactDTOBuilder);
	}

	private ContactDTO.Builder loadContactDTO(User contact, ContactDTO.Builder builder) {
		if (contact == null) {
			return null;
		}

		builder
			.setId(contact.getId())
			.setFirstName(contact.getName().getFirstName())
			.setLastName(contact.getName().getLastName())
			.setEmail(contact.getEmail());

		if (contact.getProfile().isSetPhoneNumbers()) {
			for (Phone phone : contact.getProfile().getPhoneNumbers()){
				if (ContactContextType.WORK.name().equals(phone.getType())) {
					builder
						.setWorkPhone(phone.getPhone())
						.setWorkPhoneExtension(phone.getExtension());
				} else if (ContactContextType.HOME.name().equals(phone.getType())) {
					builder
						.setMobilePhone(phone.getPhone());
				}
			}
		}

		return builder;
	}

	protected void loadPricingDTO() {
		if (pricing != null && configuration != null){
			pricingDTOBuilder
				.setType(pricing.isSetType() ? pricing.getType().toString() : null)
				.setMode(configuration.isUseMaxSpendPricingDisplayModeFlag() ? "spend" : "pay")
				.setFlatPrice(pricing.getFlatPrice() > 0 ? pricing.getFlatPrice() : null)
				.setPerHourPrice(pricing.getPerHourPrice() > 0 ? pricing.getPerHourPrice() : null)
				.setMaxNumberOfHours(pricing.getMaxNumberOfHours() > 0 ? pricing.getMaxNumberOfHours() : null)
				.setPerUnitPrice(pricing.getPerUnitPrice() > 0 ? pricing.getPerUnitPrice() : null)
				.setMaxNumberOfUnits(pricing.getMaxNumberOfUnits() > 0 ? pricing.getMaxNumberOfUnits() : null)
				.setInitialPerHourPrice(pricing.getInitialPerHourPrice() > 0 ? pricing.getInitialPerHourPrice() : null)
				.setInitialNumberOfHours(pricing.getInitialNumberOfHours() > 0 ? pricing.getInitialNumberOfHours() : null)
				.setAdditionalPerHourPrice(pricing.getAdditionalPerHourPrice() > 0 ? pricing.getAdditionalPerHourPrice() : null)
				.setMaxBlendedNumberOfHours(pricing.getMaxBlendedNumberOfHours() > 0 ? pricing.getMaxBlendedNumberOfHours() : null)
				.setPaymentTermsDays(configuration.isSetPaymentTermsDays() ? configuration.getPaymentTermsDays() : 0)
				.setDisablePriceNegotiation(configuration.isDisablePriceNegotiation())
				.setOfflinePayment(pricing.isOfflinePayment());
		}
	}

	protected void loadRoutingDTO() {
		Assert.notNull(work);

		if (configuration != null) {
			routingDTOBuilder
				.setSmartRoute(configuration.isSmartRoute())
				.setAssignToFirstToAccept(configuration.isAssignToFirstResource())
				.setShownInFeed(work.isShowInFeed());

			if (work.getNeedToApplyGroups() != null) {
				for (Long groupId : work.getNeedToApplyGroups()) {
					routingDTOBuilder.addGroupId(groupId);
				}
			}
			if (work.getFirstToAcceptGroups() != null) {
				for (Long groupId : work.getFirstToAcceptGroups()) {
					routingDTOBuilder.addGroupId(groupId);
				}
			}
			routingDTOBuilder.setNeedToApplyCandidates(
				routingNeedToApplyCandidatesDTOBuilder.setGroupIds(Sets.newHashSet(work.getNeedToApplyGroups())));

			routingDTOBuilder.setFirstToAcceptCandidates(
				routingFirstToAcceptCandidatesDTOBuilder.setGroupIds(Sets.newHashSet(work.getFirstToAcceptGroups())));

			if (work.getResources() != null) {
				for (Resource resource : work.getResources()) {
					routingDTOBuilder.addResourceNumber(resource.getUser().getUserNumber());
				}
			}

			for (String vendorNumber : vendorService.getVendorNumbersByWork(work.getId())) {
				routingDTOBuilder.addVendorCompanyNumber(vendorNumber);
			}
		}
	}

	protected void loadCustomFieldGroupDTOs() {
		if (work.getCustomFieldGroups() != null) {
			customFieldGroupDTOBuilders.clear(); // We're replacing whatever's there.
			for (CustomFieldGroup customFieldGroup : work.getCustomFieldGroups()) {
				CustomFieldGroupDTO.Builder builder = new CustomFieldGroupDTO.Builder()
					.setId(customFieldGroup.getId())
					.setName(customFieldGroup.getName())
					.setRequired(customFieldGroup.isIsRequired())
					.setPosition(customFieldGroup.getPosition());

				for (CustomField customField : customFieldGroup.getFields()) {
					// custom fields with default values that contain commas are dropdown lists
					// we don't allow the custom field value to be saved containing the list of options
					String value =
						customField.getValue() != null &&
							customField.getDefaultValue() != null &&
							customField.getValue().contains(",") &&
							customField.getDefaultValue().contains(",") ? "" : customField.getValue();
					builder.addField(
						new CustomFieldDTO.Builder()
							.setId(customField.getId())
							.setName(customField.getName())
							.setValue(value)
							.setDefaultValue(customField.getDefaultValue())
							.setVisibleToResource(customField.isVisibleToResource())
							.setVisibleToOwner(customField.isVisibleToOwner())
							.setRequired(customField.isIsRequired())
							.setType(customField.getType())
							.setReadOnly(customField.isReadOnly())
							.setShowOnPrintout(customField.isShowOnPrintout())
							.setShowInAssignmentHeader(customField.isShowInAssignmentHeader())
							.setShowOnSentStatus(customField.isShowOnSentStatus())
					);
				}
				customFieldGroupDTOBuilders.add(builder);
			}
		}
	}

	protected void loadShipmentGroupDTO() {
		if (shipments != null) {
			shipmentGroupDTOBuilder.clearShipments(); // We're replacing whatever's there.

			List<PartDTO> parts = Lists.newArrayList();
			if (isNotEmpty(shipments.getParts())) {
				if (shipments.getUuid() != null) {
					parts = partService.getPartsByGroupUuid(shipments.getUuid());
				}
			} else {
				parts.addAll(shipments.getParts());
			}

			shipmentGroupDTOBuilder
				.setUuid(shipments.getUuid())
				.setReturnShipment(shipments.isReturnRequired())
				.setSuppliedByWorker(shipments.isSuppliedByWorker())
				.setShippingDestinationType(shipments.getShippingDestinationType());

			if (shipments.hasReturnToLocation() && shipments.isReturnRequired()) {
				shipmentGroupDTOBuilder.setReturnAddress(loadLocationDTO(shipments.getReturnToLocation()));
			}

			if (shipments.hasShipToLocation()) {
				shipmentGroupDTOBuilder.setShipToAddress(loadLocationDTO(shipments.getShipToLocation()));
			}

			for (PartDTO part : parts) {
				ShipmentDTO.Builder shipmentDTOBuilder = new ShipmentDTO.Builder()
					.setUuid(part.getUuid())
					.setName(part.getName())
					.setTrackingNumber(part.getTrackingNumber())
					.setTrackingStatus(part.getTrackingStatus())
					.setShippingProvider(part.getShippingProvider())
					.setValue(part.getPartValue())
					.setReturn(part.isReturn());
				shipmentGroupDTOBuilder.addShipment(shipmentDTOBuilder);
			}
		} else {
			shipmentGroupDTOBuilder.reset();
		}
	}

	private LocationDTO.Builder loadLocationDTO(com.workmarket.service.business.dto.LocationDTO location) {
		LocationDTO.Builder address = null;
		if (location != null) {
			address = new LocationDTO.Builder()
				.setName(location.getName())
				.setNumber(location.getLocationNumber())
				.setAddressLine1(location.getAddress1())
				.setAddressLine2(location.getAddress2())
				.setCity(location.getCity())
				.setState(location.getState())
				.setZip(location.getPostalCode())
				.setCountry(location.getCountry())
				.setLatitude(location.getLatitude() != null ? location.getLatitude().doubleValue() : null)
				.setLongitude(location.getLongitude() != null ? location.getLongitude().doubleValue() : null);

			if (location.getId() != null) {
				address.setId(location.getId());
			}
		}

		return address;
	}

	protected void loadSurveyDTOs() {
		if (work.getAssessments() != null) {
			surveyDTOBuilders.clear(); // We're replacing whatever's there.
			for (Assessment survey : work.getAssessments()) {
				SurveyDTO.Builder builder = new SurveyDTO.Builder()
					.setId(survey.getId())
					.setRequired(survey.isIsRequired());

				surveyDTOBuilders.add(builder);
			}
		}
	}

	protected void loadDocumentDTOs() {
		if (documents != null) {
			documentDTOBuilders.clear(); // We're replacing whatever's there.
			for (Asset asset : documents) {
				DocumentDTO.Builder builder = new DocumentDTO.Builder()
					.setId(asset.getId())
					.setUuid(asset.getUuid())
					.setName(asset.getName())
					.setDescription(asset.getDescription())
					.setVisibilityType(asset.getVisibilityCode());

				documentDTOBuilders.add(builder);
			}
		}
	}

	protected void loadDeliverablesGroupDTO() {
		Assert.notNull(deliverablesGroupDTOBuilder);

		if (deliverablesGroup != null && deliverablesGroup.getDeliverableRequirementDTOs() != null) {
			Set<DeliverableDTO.Builder> deliverables = Sets.newHashSet();
			for (DeliverableRequirementDTO deliverable : deliverablesGroup.getDeliverableRequirementDTOs()) {
				deliverables.add(
					new DeliverableDTO.Builder()
						.setId(deliverable.getId())
						.setType(deliverable.getType())
						.setDescription(deliverable.getInstructions())
						.setNumberOfFiles(deliverable.getNumberOfFiles())
						.setPriority(deliverable.getPriority())
				);
			}

			deliverablesGroupDTOBuilder
				.setId(deliverablesGroup.getId())
				.setInstructions(deliverablesGroup.getInstructions())
				.setHoursToComplete(deliverablesGroup.getHoursToComplete())
				.setDeliverables(deliverables);
		}
	}

	protected void loadAvailableFundsApiDTO() {
		Assert.notNull(availableFundsApiDTOBuilder);
		availableFundsApiDTOBuilder
				.setSpendingLimit(spendingLimit)
				.setAplLimit(aplLimit);
	}

	protected void loadConfigurationDTO() {
		if (configuration != null) {
			CompanyPreference preference = getCompanyPreference();

			configurationDTOBuilder
				.setCustomFieldsEnabled(configuration.isCustomFieldsEnabledFlag())
				.setShipmentsEnabled(configuration.isPartsLogisticsEnabledFlag())
				.setRequirementSetsEnabled(configuration.isUseRequirementSets())
				.setDeliverablesEnabled(configuration.isCustomCloseOutEnabledFlag())
				.setSurveysEnabled(configuration.isAssessmentsEnabled())
				.setUniqueExternalIdEnabled(preference.isExternalIdActive())
				.setUniqueExternalIdDisplayName(preference.getExternalIdDisplayName());

			configurationDTOBuilder.setDocumentsEnabled(!companyOptionsService.hasOption(getCompany(), CompanyOption.DOCUMENTS_ENABLED, "false"));
			if (isNotEmpty(work.getFollowers())) {
				configurationDTOBuilder.setFollowersEnabled(Boolean.TRUE);
			}
		}
	}

	protected void generateWorkSaveRequest() {
		Assert.notNull(user);
		Assert.notNull(work);

		workSaveRequest = new WorkSaveRequest()
			.setUserId(user.getId())
			.setWork(work);

		if (routingDTO != null) {
			workSaveRequest
				.setGroupIds(Lists.newArrayList(routingDTO.getGroupIds()))
				.setVendorCompanyNumbers(ImmutableList.copyOf(routingDTO.getVendorCompanyNumbers()));
		}

		if (routing != null) {
			for (RoutingStrategy routingStrategy : routing) {
				workSaveRequest.addToRoutingStrategies(routingStrategy);
			}
		}

		if (template != null) {
			workSaveRequest.setTemplateId(work.getId());
		}
	}

	protected void saveWork() throws ValidationException, WorkAuthorizationException {
		Assert.notNull(workSaveRequest);

		if (work.isSetId()) {
			List<ConstraintViolation> errors = workStatusValidationHelper.validateUpdateOnWorkStatus(work.getId());
			if (isNotEmpty(errors)) {
				throw new ValidationException(errors);
			}
		}

		if (readyToSend) {
			workResponse = tWorkFacadeService.saveOrUpdateWork(workSaveRequest, WorkRequestInfo.getInfoEnumSet());
		} else {
			workResponse = tWorkFacadeService.saveOrUpdateWorkDraft(workSaveRequest, WorkRequestInfo.getInfoEnumSet());
		}

		workAuthorizationFailureHelper.handleErrorsFromAuthResponse(workSaveRequest, workResponse, messages);
		if (messages.hasErrors()) {
			throw new WorkAuthorizationException(join(messages.getErrors().toArray(), "\n"));
		}
	}

	protected void sendWork() throws WorkAuthorizationException {
		Assert.notNull(workSaveRequest);
		Assert.notNull(workResponse);
		if (readyToSend && workSaveRequest.isShowInFeed()) {
			workAuthorizationResponse = workRoutingService.openWork(workResponse.getWork().getWorkNumber());
			if (workAuthorizationResponse != null
				&& workAuthorizationResponse.fail()
				&& !messages.hasErrors()) {
				workAuthorizationFailureHelper.handleErrorsFromAuthResponse(workAuthorizationResponse, work, messages);
				if (messages.hasErrors()) {
					throw new WorkAuthorizationException(join(messages.getErrors().toArray(), "\n"));
				}
			}
		}
	}

	protected void saveTemplate() throws ValidationException {
		Assert.notNull(workSaveRequest);
		workResponse = tWorkFacadeService.saveOrUpdateWorkTemplate(workSaveRequest);
	}

	protected void buildConfigurationDTO() {
		Assert.notNull(configurationDTOBuilder);
		configurationDTO = configurationDTOBuilder.build();
	}

	protected void normalizeWorkPricing() {
		Assert.notNull(work);
		workResponsePricingHelper.normalizePricing(work);
	}

	protected void handleValidationException() throws ValidationException {
		if (exception instanceof ValidationException) {
			throw (ValidationException) exception;
		}
	}

	protected void handleWorkActionException() throws WorkActionException {
		if (exception instanceof WorkActionException) {
			throw (WorkActionException) exception;
		}
	}

	protected void handleWorkAuthorizationException() throws WorkAuthorizationException {
		if (exception instanceof WorkAuthorizationException) {
			throw (WorkAuthorizationException) exception;
		}
	}

	private long applyTimeZoneAndGetDateTimeInMillis(String dateTime, String timeZone) {
		Calendar fromTimeCal = DateUtilities.getCalendarFromDateTimeString(dateTime, timeZone);
		return fromTimeCal.getTimeInMillis();
	}

	private boolean locationHasEmptyAddress(LocationDTO locationDTO) {
		return isBlank(locationDTO.getAddressLine1())
			&& isBlank(locationDTO.getCity())
			&& isBlank(locationDTO.getState())
			&& isBlank(locationDTO.getCountry())
			&& isBlank(locationDTO.getZip());
	}
}
