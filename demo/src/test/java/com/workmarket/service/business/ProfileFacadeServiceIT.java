package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.acl.AclRole;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkResourceFeedbackPagination;
import com.workmarket.domains.work.model.WorkResourceFeedbackRow;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.business.dto.CompanyDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.NoteDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.WorkCustomFieldDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.domains.work.service.WorkNegotiationService;
import com.workmarket.domains.work.service.WorkNoteService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.service.web.ProfileFacadeService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.test.mock.auth.AuthenticationMock;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.RandomUtilities;
import com.workmarket.web.facade.ProfileFacade;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

// TODO: Alex - audit @Ignore tests with product and remove
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ProfileFacadeServiceIT extends BaseServiceIT {

	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private ProfileFacadeService profileFacadeService;
	@Autowired private WorkService workService;
	@Autowired private LaneService laneService;
	@Autowired private RatingService ratingService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private ProfileService profileService;
	@Autowired private InsuranceService insuranceService;
	@Autowired private AssetManagementService assetManagementService;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private CustomFieldService customFieldService;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private WorkResourceService workResourceService;

 	@Test
	public void findProfileFacadeByUserNumber_success() throws Exception {
		User user = newContractorIndependentlane4Ready();
	    
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());
	    
		assertNotNull(facade);
	}

 	@Test
	@Ignore
	public void findProfileFacadeByUserNumber_asUnauthorized() throws Exception {
		User viewer = newEmployeeWithCashBalance();
		User user = newContractorIndependent();
		authenticationService.setCurrentUser(viewer);

		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

	    assertNull(facade.getLaneType());
	}

	@Test
	@Ignore
	public void findProfileFacadeByUserNumber_asPublic() throws Exception {
		User viewer = newEmployeeWithCashBalance();
		User user = newContractorIndependentLane4ReadyWithCashBalance();
		
		user = userService.findUserById(user.getId());
		
		populateUserWithProfileStuff(user);
		
		authenticationService.setCurrentUser(viewer);
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.PUBLIC.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(4), facade.getLaneType());
		assertTrue(facade.getAssessments().isEmpty());
		assertTrue(facade.getCertifications().isEmpty());
		assertTrue(facade.getInsurance().isEmpty());
		assertTrue(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	@Test
	@Ignore
	public void findProfileFacadeByUserNumber_asOwner() throws Exception {

		User viewer = newEmployeeWithCashBalance();
		User user = newCompanyEmployee(viewer.getCompany().getId());

		laneService.removeUserFromCompanyLane(user.getId(), viewer.getCompany().getId());

		populateUserWithProfileStuff(user);

		authenticationService.setCurrentUser(user);
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.OWNER.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(0), (facade.getLaneType()));
		assertFalse(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	@Test
	@Ignore
	public void findProfileFacadeByUserNumber_asAdmin() throws Exception {
		User viewer = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
		User user = newCompanyEmployee(viewer.getCompany().getId());
		
		laneService.addUserToCompanyLane1(user.getId(), viewer.getCompany().getId());
		
		populateUserWithProfileStuff(user);
		
		authenticationService.setCurrentUser(viewer);
		authenticationService.assignAclRolesToUser(viewer.getId(), new Long[] {AclRole.ACL_ADMIN});
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.ADMIN.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(1), facade.getLaneType());
		assertFalse(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertFalse(facade.getRoleIds().isEmpty());
	}

	@Test
	public void findProfileFacadeByUserNumber_asLane1() throws Exception {
		User admin = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
		User viewer = newCompanyEmployee(admin.getCompany().getId());
		User user = newCompanyEmployee(viewer.getCompany().getId());

		laneService.addUserToCompanyLane1(user.getId(), viewer.getCompany().getId());

		populateUserWithProfileStuff(user);

		authenticationService.setCurrentUser(viewer);
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.PUBLIC.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(1), facade.getLaneType());
		assertTrue(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	@Test
	public void findProfileFacadeByUserNumber_asLane2() throws Exception {
		User viewer = newEmployeeWithCashBalance();
		User user = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);

		laneService.addUserToCompanyLane2(user.getId(), viewer.getCompany().getId());

		populateUserWithProfileStuff(user);

		authenticationService.setCurrentUser(viewer);
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.PUBLIC.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(2), facade.getLaneType());
		assertTrue(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	@Test
	public void findProfileFacadeByUserNumber_asLane3() throws Exception {
		User viewer = newEmployeeWithCashBalance();
		User user = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
		
		userService.updateLane3ApprovalStatus(user.getId(), ApprovalStatus.APPROVED);
		laneService.addUserToCompanyLane3(user.getId(), viewer.getCompany().getId());
		
		populateUserWithProfileStuff(user);
		
		authenticationService.setCurrentUser(viewer);
		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.PUBLIC.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(3), facade.getLaneType());
		assertTrue(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	@Test
	@Ignore
	public void findProfileFacadeByUserNumber_asInternal() throws Exception {
		User viewer = newInternalUser();
		User user = newContractorIndependentLane4ReadyWithCashBalance();

		populateUserWithProfileStuff(user);

		authenticationService.setCurrentUser(viewer);

		ProfileFacade facade = profileFacadeService.findProfileFacadeByUserNumber(user.getUserNumber());

		assertEquals(RequestContext.PUBLIC.getCode(), facade.getRequestContext());
		assertEquals(Integer.valueOf(4), facade.getLaneType());
		assertFalse(facade.getAssessments().isEmpty());
		assertFalse(facade.getCertifications().isEmpty());
		assertFalse(facade.getInsurance().isEmpty());
		assertFalse(facade.getLicenses().isEmpty());
		assertTrue(facade.getRoleIds().isEmpty());
	}

	public void populateUserWithProfileStuff(User user) throws Exception {
		Profile profile = profileService.findProfile(user.getId());

		profileService.updateProfileProperties(user.getId(), CollectionUtilities.newStringMap(
			"overview", RandomUtilities.generateLoremIpsum(10),
			"jobTitle", "Employee #" + RandomUtilities.generateNumericString(2),
			"workPhone", RandomUtilities.generateNumericString(10),
			"mobilePhone", RandomUtilities.generateNumericString(10)
		));

		profileService.updateAddress(profile.getId(), newAddress200FultonStreetNewYork());
		profileService.setBlacklistedZipcodesForUser(user.getId(), new String[] {
			"10010",
			"10001",
			"10001",
			"11238"
		});

		CompanyDTO dto = new CompanyDTO();
		dto.setName(RandomUtilities.generateAlphaString(20));
		dto.setOverview(RandomUtilities.generateLoremIpsum(100));
		dto.setWebsite("http://www." + RandomUtilities.generateAlphaString(20) + ".com");
		dto.setEmployees(RandomUtilities.nextIntInRange(0, 1000));
		dto.setYearFounded(RandomUtilities.nextIntInRange(1900, 2010));
		dto.setIndustryId(INDUSTRY_ID);
		dto.setOperatingAsIndividualFlag(false);

		profileService.saveOrUpdateCompany(user.getId(), dto);

		insuranceService.addInsuranceToUser(user.getId(), newInsuranceDTO(INSURANCE_ID));
		licenseService.addLicenseToUser(LICENSE_ID, user.getId(), LICENSE_NUMBER);
		certificationService.addCertificationToUser(CERTIFICATION_ID, user.getId(), CERTIFICATION_NUMBER);

		authenticationService.approveUser(user.getId());
	}

	public void populateWorkFacadeStuff(Work work, User buyer, User resource1, User resource2) throws Exception {
		String uniqueId = RandomUtilities.generateAlphaNumericString(10);
		AssetDTO assetDTO = new AssetDTO();
		assetDTO.setSourceFilePath(STORAGE_TEST_FILE + uniqueId);
		assetDTO.setName("name");
		assetDTO.setDescription("description");
		assetDTO.setMimeType(MimeType.TEXT_PLAIN.getMimeType());

		assetManagementService.storeAssetForWork(assetDTO, work.getId());

		workNoteService.addNoteToWork(work.getId(), new NoteDTO("Note testing"));

		WorkCustomFieldDTO dto1 = new WorkCustomFieldDTO();
		dto1.setId(WORK_CUSTOM_FIELD_1_ID);
		dto1.setValue("3");

		WorkCustomFieldDTO dto2 = new WorkCustomFieldDTO();
		dto2.setId(WORK_CUSTOM_FIELD_2_ID);
		dto2.setValue("N123456789");

		customFieldService.saveWorkCustomFieldsForWorkAndIndex(new WorkCustomFieldDTO[] {
			dto1,
			dto2
		}, work.getId());

		laneService.addUserToCompanyLane3(resource1.getId(), buyer.getCompany().getId());
		laneService.addUserToCompanyLane3(resource2.getId(), buyer.getCompany().getId());

		ratingService.createRatingForWork(buyer.getId(), resource1.getId(), work.getId(), new RatingDTO(Rating.EXCELLENT, "Awesome work!"));
		ratingService.createRatingForWork(buyer.getId(), resource1.getId(), work.getId(), new RatingDTO(Rating.SATISFIED, "Not bad!"));
		ratingService.createRatingForWork(buyer.getId(), resource2.getId(), work.getId(), new RatingDTO(Rating.EXCELLENT, "Awesome work!"));

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(resource1.getUserNumber(), resource2.getUserNumber()));

		WorkNegotiationDTO dto = new WorkNegotiationDTO();
		dto.setPriceNegotiation(true);
		dto.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		dto.setFlatPrice(500.00);

		authenticationService.setCurrentUser(resource2);
		workNegotiationService.createNegotiation(work.getId(), dto);

		workService.acceptWork(resource1.getId(), work.getId());

		dto = new WorkNegotiationDTO();
		dto.setIsScheduleRange(false);
		dto.setScheduleFromString(DateUtilities.getISO8601(DateUtilities.newCalendar(2011, 8, 2, 9, 0, 0)));

		authenticationService.setCurrentUser(resource1);
		workNegotiationService.createRescheduleNegotiation(work.getId(), dto);
	}

	/**
	 * WorkResourceService#findResourceFeedbackForUser should return ratings sorted by work.schedule_from desc
	 */
	@Test
	public void userProfileRatingsSort() throws Exception {

		User employee = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
		User contractor = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		// Create assignment with schedule_from = today + 2 days
		// and work_resource with appointment_from = today + 3 days
		Calendar workWithAppointmentDate = Calendar.getInstance();
		workWithAppointmentDate.add(Calendar.DAY_OF_MONTH, 2);
		Calendar scheduleNegotiationDate1 = Calendar.getInstance();
		scheduleNegotiationDate1.add(Calendar.DAY_OF_MONTH, 3);
		Work workWithAppointment = createWorkForRating(employee, contractor, workWithAppointmentDate, scheduleNegotiationDate1);
		ratingService.createRatingForWork(employee.getId(), contractor.getId(), workWithAppointment.getId(),
			new RatingDTO(Rating.EXCELLENT, "Awesome work!"));

		// Create assignment with schedule_from = today + 5 days
		Calendar workDate2 = Calendar.getInstance();
		workDate2.add(Calendar.DAY_OF_MONTH, 5);
		Work workNoAppointment = createWorkForRating(employee, contractor, workDate2, null);
		ratingService.createRatingForWork(employee.getId(), contractor.getId(), workNoAppointment.getId(),
			new RatingDTO(Rating.EXCELLENT, "Awesome work!"));

		WorkResourceFeedbackPagination pagination = new WorkResourceFeedbackPagination();
		pagination = workResourceService.findResourceFeedbackForUser(contractor.getId(), pagination);
		List<WorkResourceFeedbackRow> ratings = pagination.getResults();
		assertEquals(2, ratings.size());

		WorkResource resourceNoAppointment = workResourceService.findActiveWorkResource(workNoAppointment.getId());
		WorkResource resourceWithAppointment = workResourceService.findActiveWorkResource(workWithAppointment.getId());
		assertNull(resourceNoAppointment.getAppointment());
		assertNotNull(resourceWithAppointment.getAppointment());
		// workNoAppointment.schedule_from > workNoAppointment.schedule_from
		assertTrue(workNoAppointment.getSchedule().getFrom().after(workWithAppointment.getSchedule().getFrom()));
		// Ratings should be sorted descending by work.schedule_from so workWithoutAppointmentFrom should be first as it has
		// later schedule from
		assertEquals(ratings.get(0).getWorkId(), workNoAppointment.getId());
	}

	private Work createWorkForRating(User employee, User contractor, Calendar workDate, Calendar scheduleNegotiationDate)
		throws Exception {

		setCurrentUser(employee);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!" + RandomUtilities.nextLong());
		workDTO.setDescription("Description of work. " + RandomUtilities.generateAlphaString(10));
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId((new FlatPricePricingStrategy()).getId());
		workDTO.setFlatPrice(DEFAULT_WORK_FLAT_PRICE);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString(DateUtilities.getISO8601(workDate));
		workDTO.setPaymentTermsDays(30);
		workDTO.setPaymentTermsEnabled(true);
		workDTO.setIndustryId(INDUSTRY_ID);
		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);

		workRoutingService.addToWorkResources(work.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));
		workService.acceptWork(contractor.getId(), work.getId());

		if(scheduleNegotiationDate != null) {
			WorkNegotiationDTO workNegotiationDTO = new WorkNegotiationDTO();

			workNegotiationDTO.setScheduleFromString(DateUtilities.getISO8601(scheduleNegotiationDate.getTime()));
			workNegotiationDTO.setScheduleThroughString(DateUtilities.getISO8601(scheduleNegotiationDate.getTime()));
			workNegotiationDTO.setIsScheduleRange(Boolean.TRUE);
			workNegotiationDTO.setScheduleNegotiation(Boolean.TRUE);
			workNegotiationDTO.setTimeZoneId(invariantDataService.findTimeZonesByTimeZoneId("GMT").getId());

			setCurrentUser(contractor);
			WorkRescheduleNegotiation negotiation = workNegotiationService.createRescheduleNegotiation(work.getId(), workNegotiationDTO);

			setCurrentUser(employee);
			workNegotiationService.approveNegotiation(negotiation.getId());
		}
		else {
			setCurrentUser(contractor);
			workService.acceptWork(contractor.getId(), work.getId());
		}

		workService.updateWorkProperties(work.getId(),
			CollectionUtilities.newStringMap("resolution", "Complete the assignment"));
		authenticationService.setCurrentUser(contractor.getId());
		workService.completeWork(work.getId(), new CompleteWorkDTO());

		setCurrentUser(employee);
		workService.closeWork(work.getId());

		work = workService.findWork(work.getId());
		Calendar dueDate = work.getClosedOn();
		dueDate.add(Calendar.DATE, work.getPaymentTermsDays());
		dueDate = DateUtilities.getCalendarWithLastMinuteOfDay(dueDate, Constants.EST_TIME_ZONE);
		assertTrue(work.getInvoice().getDueDate().compareTo(dueDate) == 0);

		billingService.payInvoice(employee.getId(), work.getInvoice().getId());
		work = workService.findWork(work.getId());
		return work;
	}

	private void setCurrentUser(User user) {
		authenticationService.setCurrentUser(user);
		ExtendedUserDetails details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByUsername(user.getEmail());
		SecurityContextHolder.getContext().setAuthentication(new AuthenticationMock(details));
	}
}
