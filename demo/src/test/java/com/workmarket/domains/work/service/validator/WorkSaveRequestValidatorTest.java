package com.workmarket.domains.work.service.validator;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyPreference;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroup;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.CustomFieldService;
import com.workmarket.service.business.DirectoryService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ProfileDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.thrift.work.PricingStrategy;
import com.workmarket.thrift.work.Schedule;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkSaveRequestValidatorTest {

	@Mock private UserService userService;
	@Mock private DirectoryService directoryService;
	@Mock private CustomFieldService customFieldService;
	@Mock private InvariantDataService invariantDataService;
	@Mock private CompanyService companyService;
	@Mock private PricingService pricingService;
	@Mock private ProfileService profileService;
	@Mock private WorkService workService;
	@Mock private WorkBundleService workBundleService;
	@Mock private AuthenticationService authenticationService;
	@Mock private MessageBundleHelper messageHelper;
	@Mock private WorkSubStatusService workSubStatusService;
	@Mock private IndustryService industryService;
	@InjectMocks WorkSaveRequestValidator workSaveRequestValidator;

	private WorkSaveRequest workSaveRequest;
	private Work work;
	private ManageMyWorkMarket manageMyWorkMarket;
	private User currentUser;
	private Company currentCompany;
	private CompanyPreference currentCompanyPreference;
	private ProfileDTO resourceProfileDTO;
	private com.workmarket.thrift.core.Industry workIndustry;
	private AbstractWork workModel;
	private com.workmarket.domains.model.pricing.InternalPricingStrategy strategy;
	private com.workmarket.thrift.core.User buyer;
	private com.workmarket.thrift.work.ManageMyWorkMarket tManageMyWorkMarket;

	@Before
	public void setUp() throws Exception {
		workIndustry = new com.workmarket.thrift.core.Industry();
		workIndustry.setId(1L);
		work = new Work();
		work.setIndustry(workIndustry);
		work.setId(1L);
		buyer = new com.workmarket.thrift.core.User();
		buyer.setId(1L);
		work.setBuyer(buyer);
		workSaveRequest = new WorkSaveRequest(1L, work);
		workModel = mock(AbstractWork.class);
		strategy = mock(com.workmarket.domains.model.pricing.InternalPricingStrategy.class);

		manageMyWorkMarket = new ManageMyWorkMarket();
		currentCompany = mock(Company.class);
		currentCompanyPreference = mock(CompanyPreference.class);
		currentUser = mock(User.class);
		resourceProfileDTO = mock(ProfileDTO.class);
		tManageMyWorkMarket = mock(com.workmarket.thrift.work.ManageMyWorkMarket.class);
		work.setConfiguration(tManageMyWorkMarket);
		when(userService.getUser(anyLong())).thenReturn(currentUser);
		when(currentUser.getCompany()).thenReturn(currentCompany);
		when(companyService.getManageMyWorkMarket(anyLong())).thenReturn(manageMyWorkMarket);
		when(companyService.getCompanyPreference(anyLong())).thenReturn(currentCompanyPreference);
		when(authenticationService.getCurrentUser()).thenReturn(currentUser);
		when(currentUser.getId()).thenReturn(1L);
		when(profileService.findProfileDTO(anyLong())).thenReturn(resourceProfileDTO);
		when(resourceProfileDTO.getFirstName()).thenReturn("John");
		when(resourceProfileDTO.getLastName()).thenReturn("Smith");
		Industry industry = new Industry();
		industry.setName("Techonology");
		when(invariantDataService.findIndustry(anyLong())).thenReturn(industry);
		when(workService.findWork(1L)).thenReturn(workModel);
		when(workModel.getPricingStrategyType()).thenReturn(PricingStrategyType.INTERNAL);
		when(workModel.isDraft()).thenReturn(false);
		when(workModel.isPricingEditable()).thenReturn(true);
		when(tManageMyWorkMarket.getPaymentTermsDays()).thenReturn(0);
	}

	@Test
	public void validateUser() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateUser(new com.workmarket.thrift.core.User(), violationList);
		assertFoundViolation(violationList, "Contact name");
	}

	@Test
	public void validateUser_withEmptyName() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateUser(new com.workmarket.thrift.core.User().setName(new Name()), violationList);
		assertFoundViolation(violationList, "Contact first name");
		assertFoundViolation(violationList, "Contact last name");
	}

	@Test
	public void validateUser_firstNameTooLong() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		Name name = new Name();
		name.setFirstName(RandomUtilities.generateAlphaNumericString(51));
		workSaveRequestValidator.validateUser(new com.workmarket.thrift.core.User().setName(name), violationList);
		assertFoundViolationError(violationList, MessageKeys.Contact.FIRST_NAME_TOO_LONG);
	}

	@Test
	public void validateUser_lastNameTooLong() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		Name name = new Name();
		name.setLastName(RandomUtilities.generateAlphaNumericString(51));
		workSaveRequestValidator.validateUser(new com.workmarket.thrift.core.User().setName(name), violationList);
		assertFoundViolationError(violationList, MessageKeys.Contact.LAST_NAME_TOO_LONG);
	}

	@Test
	public void validatePricing_withEmptyPrice() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setPricing(new PricingStrategy().setId(PricingStrategyType.PER_HOUR.ordinal()));
		workSaveRequestValidator.validatePricing(workSaveRequest, violationList);
		assertFoundViolation(violationList, "pricing");
	}

	@Test
	public void validatePricing_withInvalidToInternalPriceType() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setPricing(new PricingStrategy().setId(PricingStrategyType.PER_HOUR.ordinal()));
		when(pricingService.findPricingStrategyById(anyLong())).thenReturn(strategy);

		workSaveRequestValidator.validatePricing(workSaveRequest, violationList);
		assertFoundViolation(violationList, "internalPrice");
	}

	@Test
	public void validatePricing_withInvalidFromInternalPriceType() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setPricing(new PricingStrategy().setId(PricingStrategyType.INTERNAL.ordinal()));
		when(pricingService.findPricingStrategyById(anyLong())).thenReturn(strategy);
		when(workModel.getPricingStrategyType()).thenReturn(PricingStrategyType.FLAT);

		workSaveRequestValidator.validatePricing(workSaveRequest, violationList);
		assertFoundViolation(violationList, "internalPrice");
	}

	public void getConstraintViolations_withEmptyTitle() throws Exception {
		assertNotNull(workSaveRequestValidator.getConstraintViolations(workSaveRequest));
	}

	@Test
	public void validateWork_withEmptyTitle() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateWork(workSaveRequest, violationList);
		assertFoundViolation(violationList, "title");
	}

	@Test
	public void validateWork_withEmptyDescription() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateWork(workSaveRequest, violationList);
		assertFoundViolation(violationList, "description");
	}

	@Test
	public void validateWork_withTitleTooLong() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setTitle("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		workSaveRequestValidator.validateWork(workSaveRequest, violationList);
		assertFoundViolation(violationList, "title");
	}

	private void assertFoundViolation(List<ConstraintViolation> violationList, String propertyName) throws Exception {
		boolean found = false;
		for (ConstraintViolation constraintViolation : violationList) {
			if (constraintViolation.getProperty().equals(propertyName)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	private void assertFoundViolationError(List<ConstraintViolation> violationList, String error) throws Exception {
		boolean found = false;
		for (ConstraintViolation constraintViolation : violationList) {
			if (constraintViolation.getError().equals(error)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	private void assertNotFoundViolation(List<ConstraintViolation> violationList, String propertyName) throws Exception {
		boolean found = false;
		for (ConstraintViolation constraintViolation : violationList) {
			if(constraintViolation.getProperty().equals(propertyName)) {
				found = true;
				break;
			}
		}
		assertFalse(found);
	}

	@Test
	public void validateResource_withDifferentIndustry() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		com.workmarket.thrift.core.User resource = new com.workmarket.thrift.core.User();
		resource.setId(1L);
		workSaveRequest.setAssignTo(resource);
		when(industryService.doesProfileHaveIndustry(anyLong(), anyLong())).thenReturn(false);
		workSaveRequestValidator.validateResource(workSaveRequest, violationList);
		assertFoundViolation(violationList, "industry");
	}

	@Test
	public void validateCustomFields_withMissingRequiredCustomFieldGroup() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		manageMyWorkMarket.setCustomFieldsEnabledFlag(true);
		WorkCustomFieldGroup requiredWorkCustomFieldGroup = new WorkCustomFieldGroup();
		requiredWorkCustomFieldGroup.setId(1L);
		when(customFieldService.findRequiredWorkCustomFieldGroup(anyLong())).thenReturn(requiredWorkCustomFieldGroup);
		workSaveRequestValidator.validateCustomFields(workSaveRequest, violationList);
		assertFoundViolation(violationList, "customFieldGroup");
	}

	@Test(expected = ValidationException.class)
	public void validateWork_fails() throws Exception {
		workSaveRequestValidator.validateWork(workSaveRequest);
	}

	@Test(expected = ValidationException.class)
	public void validateWorkDraft_fails() throws Exception {
		workSaveRequestValidator.validateWorkDraft(workSaveRequest);
	}

	@Test
	public void validateRequireProject() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		manageMyWorkMarket.setRequireProjectEnabledFlag(true);
		workSaveRequestValidator.validateRequireProject(workSaveRequest, violationList);
		assertFoundViolation(violationList, "project");
	}

	@Test
	public void validateLocation_withMissingOffsiteLocation() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateLocation(workSaveRequest, violationList);
		assertFoundViolation(violationList, "location");
	}

	@Test
	public void validateLocation_withOffsiteLocation() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setOffsiteLocation(true);
		workSaveRequestValidator.validateLocation(workSaveRequest, violationList);
		assertNotFoundViolation(violationList, "location");
	}

	@Test
	public void validateLocation_withOnsiteLocationAndMissingInfo() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setOffsiteLocation(false);
		workSaveRequestValidator.validateLocation(workSaveRequest, violationList);
		assertFoundViolation(violationList, "location");
	}

	@Test
	public void validateLocation_withOnsiteLocationAndInvalidCompanyId() throws Exception {
		long locationCompanyId = 55L;
		long userCompanyId = 56L;

		// set location company id not equal to the work company id, should fail
		Location location = new Location();
		List<ConstraintViolation> violationList = Lists.newArrayList();

		com.workmarket.thrift.core.Company locationComany =
				new com.workmarket.thrift.core.Company();
		locationComany.setId(locationCompanyId);

		location.setCompany(locationComany);

		work.setLocation(location);
		work.setOffsiteLocation(false);
		work.setNewLocation(false);

		com.workmarket.domains.model.Location dbLocation =
				mock(com.workmarket.domains.model.Location.class);
		Company dbCompany = mock(Company.class);

		when(currentCompany.getId()).thenReturn(userCompanyId);
		when(directoryService.findLocationById(anyLong())).thenReturn(dbLocation);
		when(dbLocation.getCompany()).thenReturn(dbCompany);
		when(dbCompany.getId()).thenReturn(locationCompanyId);

		workSaveRequestValidator.validateLocation(workSaveRequest, violationList);
		assertFoundViolation(violationList, "location");
	}

	@Test
	public void validateRequireProject_withWorkNumber() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setWorkNumber("32324243");
		when(workService.findWork(anyLong())).thenReturn(null);
		workSaveRequestValidator.validateRequireProject(workSaveRequest, violationList);
		assertFoundViolation(violationList, "work");
	}

	@Test
	public void validateWorkDraft_withEmptyTitle() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateWorkDraft(workSaveRequest, violationList);
		assertFoundViolation(violationList, "title");
	}

	@Test
	public void validateWorkDraft_withTitleTooLong() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setTitle("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		workSaveRequestValidator.validateWorkDraft(workSaveRequest, violationList);
		assertFoundViolation(violationList, "title");
	}

	private void processScheduleViolation(String violation) throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateSchedule(workSaveRequest, violationList);
		assertFoundViolationError(violationList, violation);
	}

	private void processScheduleNoViolation() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateSchedule(workSaveRequest, violationList);
		assertEquals(0, violationList.size());
	}


	@Test
	public void validateSchedule_NoSchedule_Violation() throws Exception {
		work.setSchedule(null);
		processScheduleViolation(MessageKeys.Work.NOT_NULL);
	}

	@Test
	public void validateSchedule_Range_NoFrom_Violation() throws Exception {
		Schedule schedule = new Schedule(0L, Calendar.getInstance().getTimeInMillis(), true, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_MISSING_VALUES);
	}

	@Test
	public void validateSchedule_Range_NoThrough_Violation() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), 0L, true, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_MISSING_VALUES);
	}

	@Test
	public void validateSchedule_Range_BackDateThresholdExceeded_Violation() throws Exception {
		Schedule schedule = new Schedule(
			DateUtilities.getWorkBackDateThreshold()-100000,
			Calendar.getInstance().getTimeInMillis(),
			true, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_INVALID_DATE);
	}

	@Test
	public void validateSchedule_Range_FromGreaterThanThrough_Violation() throws Exception {
		Schedule schedule = new Schedule(
			Calendar.getInstance().getTimeInMillis()+1,
			Calendar.getInstance().getTimeInMillis(),
			true, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_INVALID_TIMEFRAME_ORDER);
	}

	@Test
	public void validateSchedule_Range_OverBroadRange_Violation() throws Exception {
		Calendar from = Calendar.getInstance();
		Calendar to   = Calendar.getInstance();
		to.set(Calendar.YEAR, to.get(Calendar.YEAR)+100);

		Schedule schedule = new Schedule(from.getTimeInMillis(), to.getTimeInMillis(), true, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_TIMEFRAME_TOO_LONG);
	}

	@Test
	public void validateSchedule_NotRange_NoFrom_Violation() throws Exception {
		Schedule schedule = new Schedule(0L, 0L, false, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_MISSING_VALUES);
	}

	@Test
	public void validateSchedule_BackDateThresholdExceeded_Violation() throws Exception {
		Schedule schedule = new Schedule(DateUtilities.getWorkBackDateThreshold()-100000, 0L, false, 0L);
		work.setSchedule(schedule);
		processScheduleViolation(MessageKeys.Work.SCHEDULING_INVALID_DATE);
	}

	@Test
	public void validateSchedule_CheckInRequired_NoContactName_Violation() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), 0L, false, 0L);
		work.setSchedule(schedule);
		work.setCheckinCallRequired(true);
		processScheduleViolation(MessageKeys.Work.CHECK_IN_CALL_NAME_REQUIRED);
	}

	@Test
	public void validateSchedule_CheckInRequired_NoContactPhone_Violation() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), 0L, false, 0L);
		work.setSchedule(schedule);
		work.setCheckinCallRequired(true);
		work.setCheckinContactName("AName");
		processScheduleViolation(MessageKeys.Work.CHECK_IN_CALL_PHONE_REQUIRED);
	}

	@Test
	public void validateSchedule_Range_NoCheckInRequired_AllGood() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), Calendar.getInstance().getTimeInMillis()+1, true, 0L);
		work.setSchedule(schedule);
		processScheduleNoViolation();
	}

	@Test
	public void validateSchedule_NotRange_NoCheckInRequired_AllGood() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), 0L, false, 0L);
		work.setSchedule(schedule);
		processScheduleNoViolation();
	}

	@Test
	public void validateSchedule_Range_CheckInRequired_AllGood() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), Calendar.getInstance().getTimeInMillis()+1, true, 0L);
		work.setSchedule(schedule);
		work.setCheckinCallRequired(true);
		work.setCheckinContactName("AName");
		work.setCheckinContactPhone("APhone");
		processScheduleNoViolation();
	}

	@Test
	public void validateSchedule_NotRange_CheckInRequired_AllGood() throws Exception {
		Schedule schedule = new Schedule(Calendar.getInstance().getTimeInMillis(), 0L, false, 0L);
		work.setSchedule(schedule);
		work.setCheckinCallRequired(true);
		work.setCheckinContactName("AName");
		work.setCheckinContactPhone("APhone");
		processScheduleNoViolation();
	}

	@Test
	public void validateTemplate_withEmptyTitle() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateTemplate(workSaveRequest, violationList);
		assertNotFoundViolation(violationList, "title");
	}

	@Test
	public void validateTemplate_withTitleTooLong() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		work.setTitle("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		workSaveRequestValidator.validateWorkDraft(workSaveRequest, violationList);
		assertFoundViolation(violationList, "title");
	}

	@Test
	public void validateTemplate_withEmptyDescription() throws Exception {
		List<ConstraintViolation> violationList = Lists.newArrayList();
		workSaveRequestValidator.validateTemplate(workSaveRequest, violationList);
		assertNotFoundViolation(violationList, "description");
	}
}
