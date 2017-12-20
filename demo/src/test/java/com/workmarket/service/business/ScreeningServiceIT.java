package com.workmarket.service.business;

import com.google.common.collect.ImmutableSet;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.ScreenedUser;
import com.workmarket.domains.model.screening.ScreenedUserPagination;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningObjectConverter;
import com.workmarket.domains.model.screening.ScreeningPagination;
import com.workmarket.domains.model.screening.ScreeningStatusType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.helpers.WMCallable;
import com.workmarket.screening.model.ScreeningStatusCode;
import com.workmarket.screening.model.VendorRequestCode;
import com.workmarket.service.business.dto.PaymentDTO;
import com.workmarket.service.business.dto.ScreeningDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.business.screening.ExternalScreeningService;
import com.workmarket.service.business.screening.ScreeningAndUser;
import com.workmarket.test.IntegrationTest;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;

import static com.workmarket.domains.model.screening.ScreeningStatusType.CANCELLED;
import static com.workmarket.domains.model.screening.ScreeningStatusType.FAILED;
import static com.workmarket.domains.model.screening.ScreeningStatusType.PASSED;
import static com.workmarket.domains.model.screening.ScreeningStatusType.REQUESTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class ScreeningServiceIT extends BaseServiceIT {

	@Autowired private ScreeningService screeningService;
	@Autowired private ResourceLoader resourceLoader;
	@Autowired private ExternalScreeningService externalScreeningService;
	@Autowired private LaneService laneService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private IndustryService industryService;

	private String getResourceAsString(String resourcePath) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(resourceLoader.getResource(resourcePath).getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}
			in.close();
		} catch (IOException e) {
			logger.error(e);
		}
		return sb.toString();
	}

	private Boolean screeningTrialWhichReturnExperiment;
	private Boolean screeningTrialWhichReturnControlOnly;

	@Before
	public void setup() throws IOException {
		screeningTrialWhichReturnExperiment = featureEvaluatorConfiguration
			.get("screeningTrialWhichReturnExperiment") == null ?
			Boolean.FALSE : featureEvaluatorConfiguration.get("screeningTrialWhichReturnExperiment").isEnabled();
		screeningTrialWhichReturnControlOnly = featureEvaluatorConfiguration
			.get("screeningTrialWhichReturnControlOnly") == null ?
			Boolean.TRUE : featureEvaluatorConfiguration.get("screeningTrialWhichReturnControlOnly").isEnabled();
		setFeatureToggle("screeningTrialWhichReturnExperiment", Boolean.FALSE);
		setFeatureToggle("screeningTrialWhichReturnControlOnly", Boolean.TRUE);
	}

	@After
	public void after() throws IOException {
		setFeatureToggle("screeningTrialWhichReturnExperiment", screeningTrialWhichReturnExperiment);
		setFeatureToggle("screeningTrialWhichReturnControlOnly", screeningTrialWhichReturnControlOnly);
	}

	// Background check

	@Test
	public void testRequestFreeBackgroundCheck() throws Exception {

		User user = newContractorIndependent();

		ScreeningDTO dto = newScreeningDTO();

		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestFreeBackgroundCheck(user.getId(), dto));

		assertNotNull(backgroundCheck.getRequestDate());
		assertNotNull(backgroundCheck.getScreeningId());
		assertFalse(backgroundCheck.getScreeningId().equals(""));
		assertEquals(REQUESTED, backgroundCheck.getScreeningStatusType().getCode());
	}

	@Test
	public void testRequestBackgroundCheckPaidViaAccountRegister() throws Exception {

		User user = newEmployeeWithCashBalance();

		ScreeningDTO dto = newScreeningDTO();

		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestBackgroundCheck(user.getId(), dto));

		assertNotNull(backgroundCheck.getRequestDate());
		assertNotNull(backgroundCheck.getScreeningId());
		assertFalse(backgroundCheck.getScreeningId().equals(""));
		assertEquals(REQUESTED, backgroundCheck.getScreeningStatusType().getCode());
		assertNotNull(screeningService.findByScreeningId(backgroundCheck.getScreeningId()));
	}

	@Test
	@Ignore
	public void testRequestBackgroundCheckPaidViaCreditCard() throws Exception {

		User user = newEmployeeWithCashBalance();
		ScreeningDTO bgDTO = newScreeningDTO();
		PaymentDTO paymentDTO = newPaymentDTO("120");

		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestBackgroundCheck(user.getId(), bgDTO, paymentDTO));

		assertNotNull(backgroundCheck.getRequestDate());
		assertNotNull(backgroundCheck.getScreeningId());
		assertFalse(backgroundCheck.getScreeningId().equals(""));
		assertEquals(REQUESTED, backgroundCheck.getScreeningStatusType().getCode());
		assertNotNull(screeningService.findByScreeningId(backgroundCheck.getScreeningId()));
	}

	@Test
	public void testFindBackgroundCheck() throws Exception {

		User user = newEmployeeWithCashBalance();

		// Let's create one
		ScreeningDTO bgDTO = newScreeningDTO();

		screeningService.requestBackgroundCheck(user.getId(), bgDTO);

		// Now check
		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentBackgroundCheck(user.getId()));

		assertNotNull(backgroundCheck);
		assertNotNull(backgroundCheck.getScreeningId());
		assertFalse(backgroundCheck.getScreeningId().equals(""));

		Long id = backgroundCheck.getId();

		Screening screening = ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findByScreeningId(backgroundCheck.getScreeningId()));

		assertNotNull(screening);
		assertTrue(screening instanceof BackgroundCheck);
		assertEquals(id, screening.getId());
	}

	@Ignore
	@Test
	public void testFindBackgroundCheckList() throws Exception {
		User user = newEmployeeWithCashBalance();
		ScreeningDTO bgOneDTO = newScreeningDTO();
		ScreeningDTO bgTwoDTO = newScreeningDTO();

		final int oldSize = screeningService.findBackgroundChecksByUser(user.getId()).size();

		screeningService.requestBackgroundCheck(user.getId(), bgOneDTO);
		screeningService.requestBackgroundCheck(user.getId(), bgTwoDTO);

		// Now check the list
		final int newSize = screeningService.findBackgroundChecksByUser(user.getId()).size();
		assertTrue("newsize " + newSize + " - oldsize " + oldSize + " is not >= 2", (newSize - oldSize) >= 2);
	}

	@Test
	public void testFindDrugTestList() throws Exception {
		User user = newEmployeeWithCashBalance();
		ScreeningDTO dtOneDTO = newScreeningDTO();
		ScreeningDTO dtTwoDTO = newScreeningDTO();

		screeningService.requestDrugTest(user.getId(), dtOneDTO);
		screeningService.requestDrugTest(user.getId(), dtTwoDTO);

		// Now check the list
		List<?> drugTestList = screeningService.findDrugTestsByUser(user.getId());
		assertTrue(drugTestList.size() >= 2);
	}

	@Test
	public void testHasPassedBackgroundCheck() throws Exception {
		User user = newEmployeeWithCashBalance();
		ScreeningDTO bgDTO = newScreeningDTO();

		screeningService.requestBackgroundCheck(user.getId(), bgDTO);

		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentBackgroundCheck(user.getId()));
		screeningService.updateScreeningStatus(backgroundCheck.getScreeningId(), ScreeningStatusType.PASSED);

		assertTrue(screeningService.hasPassedBackgroundCheck(user.getId()));
		screeningService.updateScreeningStatus(backgroundCheck.getScreeningId(), ScreeningStatusType.FAILED);
		assertFalse(screeningService.hasPassedBackgroundCheck(user.getId()));
	}

	@Test
	public void testHasPassedDrugTest() throws Exception {
		User user = newEmployeeWithCashBalance();
		ScreeningDTO dtDTO = newScreeningDTO();

		screeningService.requestDrugTest(user.getId(), dtDTO);

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentDrugTest(user.getId()));
		screeningService.updateScreeningStatus(drugTest.getScreeningId(), ScreeningStatusType.PASSED);

		assertTrue(screeningService.hasPassedDrugTest(user.getId()));
		screeningService.updateScreeningStatus(drugTest.getScreeningId(), ScreeningStatusType.FAILED);
		assertFalse(screeningService.hasPassedDrugTest(user.getId()));
	}

	private Callable<Boolean> oldBackgroundCheckIsExpired(final Long userId, final Long bgId) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				List<com.workmarket.screening.model.Screening> updateBgList = screeningService.findBackgroundChecksByUser(userId);
				return (
					ScreeningStatusType.EXPIRED.equals(updateBgList.get(0).getStatus().code()) ||
					ScreeningStatusType.EXPIRED.equals(updateBgList.get(1).getStatus().code())
				);
			}
		};
	}

	protected Callable<Boolean> backgroundChecksReady(final Long userId) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				List<?> bgs = screeningService.findBackgroundChecksByUser(userId);
				return bgs.size() >= 2;
			}
		};
	}

	@Test
	public void testUpdateBackgroundCheckStatus() throws Exception {

		User user = newEmployeeWithCashBalance();

		ScreeningDTO dto = newScreeningDTO();

		BackgroundCheck backgroundCheck = (BackgroundCheck) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestBackgroundCheck(user.getId(), dto));

		assertEquals(REQUESTED, backgroundCheck.getScreeningStatusType().getCode());
		assertNull(backgroundCheck.getResponseDate());

		screeningService.updateScreeningStatus(backgroundCheck.getScreeningId(), PASSED);

		com.workmarket.screening.model.Screening screening =
			screeningService.findMostRecentBackgroundCheck(user.getId());

		assertEquals(PASSED, screening.getStatus().code());
		assertNotNull(screening.getVendorResponseDate());
	}

	// Drug Test

	@Test
	public void testRequestFreeDrugTest() throws Exception {

		User user = newContractorIndependent();

		ScreeningDTO dto = newScreeningDTO();


		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestFreeDrugTest(user.getId(), dto));

		assertNotNull(drugTest.getRequestDate());
		assertNotNull(drugTest.getScreeningId());
		assertFalse(drugTest.getScreeningId().equals(""));
		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
	}

	@Test
	public void testRequestDrugTestPaidViaAccountRegister() throws Exception {

		User user = newEmployeeWithCashBalance();

		ScreeningDTO dto = newScreeningDTO();

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), dto));

		assertNotNull(drugTest.getRequestDate());
		assertNotNull(drugTest.getScreeningId());
		assertFalse(drugTest.getScreeningId().equals(""));
		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
		assertNotNull(screeningService.findByScreeningId(drugTest.getScreeningId()));
	}

	@Test
	@Ignore
	public void testRequestDrugTestPaidViaCreditCard() throws Exception {

		User user = newEmployeeWithCashBalance();
		ScreeningDTO drugDTO = newScreeningDTO();
		PaymentDTO paymentDTO = newPaymentDTO(null);

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), drugDTO, paymentDTO));

		assertNotNull(drugTest.getRequestDate());
		assertNotNull(drugTest.getScreeningId());
		assertFalse(drugTest.getScreeningId().equals(""));
		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
		assertNotNull(screeningService.findByScreeningId(drugTest.getScreeningId()));
	}

	@Test
	public void testFindDrugTest() throws Exception {

		User user = newEmployeeWithCashBalance();

		DrugTest drugTest;

		// Let's create one

		ScreeningDTO drugDTO = newScreeningDTO();

		drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), drugDTO));

		assertNotNull(drugTest);

		// Now check

		drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.findMostRecentDrugTest(user.getId()));

		assertNotNull(drugTest);
		assertNotNull(drugTest.getScreeningId());
		assertFalse(drugTest.getScreeningId().equals(""));

		Long id = drugTest.getId();

		com.workmarket.screening.model.Screening screening = screeningService.findByScreeningId(
			drugTest.getScreeningId());

		assertNotNull(screening);
		assertEquals(screening.getVendorRequestCode().code(), VendorRequestCode.DRUG.code());
		assertEquals(drugTest.getScreeningId(), screening.getUuid());
	}

	@Test
	public void testFindRequestedDrugTests() throws Exception {

		List<ScreeningAndUser> pagination = screeningService.findDrugTestsByStatus(REQUESTED,
			new ScreeningPagination());

		Integer count = pagination.size();

		for (int i = 0; i < 5; i++) {
			User user = newEmployeeWithCashBalance();
			ScreeningDTO dto = newScreeningDTO();
			screeningService.requestDrugTest(user.getId(), dto);
		}

		pagination = screeningService.findDrugTestsByStatus(REQUESTED, new ScreeningPagination());

		assertEquals((long) count, pagination.size());
	}

	@Test
	public void testUpdateDrugTestStatus() throws Exception {

		User user = newEmployeeWithCashBalance();

		ScreeningDTO dto = newScreeningDTO();

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), dto));

		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
		assertNull(drugTest.getResponseDate());

		screeningService.updateScreeningStatus(drugTest.getScreeningId(), PASSED);

		com.workmarket.screening.model.Screening screening = screeningService.findMostRecentDrugTest(user.getId());

		assertEquals(PASSED, screening.getStatus().code());
		assertNotNull(screening.getVendorResponseDate());
	}

	@Test
	public void testUpdateDrugTestStatusToCancelled() throws Exception {

		User user = newEmployeeWithCashBalance();

		ScreeningDTO dto = newScreeningDTO();

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), dto));

		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
		assertNull(drugTest.getResponseDate());

		screeningService.updateScreeningStatus(drugTest.getScreeningId(), CANCELLED);

		com.workmarket.screening.model.Screening screening = screeningService.findMostRecentDrugTest(user.getId());

		assertEquals(CANCELLED, screening.getStatus().code());
		assertNotNull(screening.getVendorResponseDate());
	}

	@Test
	public void testFindVendorId() throws Exception {
		String response = getResourceAsString("classpath:backgroundcheck/acxiom-response-passed.xml");
		String vendorId = externalScreeningService.getVendorId(response);

		assertEquals("854164", vendorId);
	}

	@Test
	public void testVendorResponsePassed() throws Exception {
		String response = getResourceAsString("classpath:backgroundcheck/acxiom-response-passed.xml");
		ScreeningStatusCode status = externalScreeningService.getScreeningStatus(response);

		assertEquals(PASSED, status.code());
	}

	@Test
	public void testVendorResponseFailed() throws Exception {
		String response = getResourceAsString("classpath:backgroundcheck/acxiom-response-failed.xml");
		ScreeningStatusCode status = externalScreeningService.getScreeningStatus(response);

		assertEquals(FAILED, status.code());
	}

	@Test
	public void testVendorResponseCancelled() throws Exception {
		String response = getResourceAsString("classpath:backgroundcheck/acxiom-response-cancelled.xml");
		ScreeningStatusCode status = externalScreeningService.getScreeningStatus(response);

		assertEquals(CANCELLED, status.code());
	}

	@Test
	public void testUpdateDrugTestStatusToFAILED() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User user = newContractorIndependentLane4ReadyWithCashBalance();
		Profile profile = profileService.findProfile(user.getId());
		industryService.setIndustriesForProfile(profile.getId(), ImmutableSet.copyOf(industryService.getAllIndustries()));

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIndustryId(Constants.WM_TIME_INDUSTRY_ID);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2009-06-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);
		laneService.addUserToCompanyLane2(user.getId(), employee.getCompany().getId());

		LaneAssociation lane = laneService
			.findActiveAssociationByUserIdAndCompanyId(
				user.getId(),
				employee.getCompany().getId());

		assertNotNull(lane);

		workRoutingService.addToWorkResources(work.getId(), user.getId());

		work = workService.findWork(work.getId());
		assertNotNull(workResourceService.findAllResourcesForWork(work.getId()));

		workService.acceptWork(user.getId(), work.getId());

		work = workService.findWork(work.getId());

		assertTrue(work.getWorkStatusType().getCode().equals(WorkStatusType.ACTIVE));

		ScreeningDTO dto = newScreeningDTO();

		DrugTest drugTest = (DrugTest) ScreeningObjectConverter.convertScreeningResponseToMonolith(
			screeningService.requestDrugTest(user.getId(), dto));

		assertEquals(REQUESTED, drugTest.getScreeningStatusType().getCode());
		assertNull(drugTest.getResponseDate());

		screeningService.updateScreeningStatus(drugTest.getScreeningId(), FAILED);

		com.workmarket.screening.model.Screening screening = screeningService.findMostRecentDrugTest(user.getId());

		assertEquals(FAILED, screening.getStatus().code());
		assertNotNull(screening.getVendorResponseDate());
	}

	@Test
	public void test_findAllScreeningRequests() throws Exception {
		ScreenedUserPagination pagination = new ScreenedUserPagination(true);
		pagination.setSortColumn(ScreenedUserPagination.SORTS.CREDITCHECK_STATUS.toString());

		pagination = screeningService.findAllScreenedUsers(pagination);

		assertNotNull(pagination);

		for (ScreenedUser dto : pagination.getResults()) {
			assertNotNull(dto.getBackgroundCheckStatus());
			assertNotNull(dto.getDrugTestStatus());
			assertNotNull(dto.getCreditCheckStatus());
		}

		int size = pagination.getResults().size();
		assertTrue((size == pagination.getRowCount()) || (size == Pagination.MAX_ROWS));
	}

}
