package com.workmarket.domains.work.service;

import ch.lambdaj.group.Group;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.configuration.Constants;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.test.IntegrationTest;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.WorkBundleValidationHelper;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkBundleServiceIT extends BaseServiceIT {

	@Autowired private BaseWorkDAO abstractWorkDAO;
	@Autowired private MessageBundleHelper messageBundleHelper;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private WorkBundleValidationHelper workBundleValidationHelper;
	@Autowired private WorkBundleRouting workBundleRouting;

	WorkBundle parent;
	Work bundleMemberOne, bundleMemberTwo;
	User employee, resource;

	@Before
	public void setup() throws Exception {
		employee = newFirstEmployeeWithCashBalance();
		resource = newContractorIndependentlane4Ready();
		parent = newWorkBundle(employee.getId());
		bundleMemberOne = newWorkOnSiteWithLocation(employee.getId());
		bundleMemberTwo = newWorkOnSiteWithLocation(employee.getId());
	}

	@Test
	@Transactional
	public void isBundle_Natural_True() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);

		assertEquals(true, workBundleService.isAssignmentBundle(parent));
	}

	@Test
	@Transactional
	public void isBundle_Natural_False() throws Exception {
		assertEquals(false, workBundleService.isAssignmentBundle(bundleMemberOne));
	}

	@Test
	@Transactional
	public void isBundle_AbstractWork_True() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);

		AbstractWork work = abstractWorkDAO.findById(parent.getId());
		assertEquals(true, workBundleService.isAssignmentBundle(work));
	}

	@Test
	@Transactional
	public void isBundle_AbstractWork_False() throws Exception {
		AbstractWork work = abstractWorkDAO.findById(bundleMemberOne.getId());
		assertEquals(false, workBundleService.isAssignmentBundle(work));
	}

	@Test
	@Transactional
	public void addToBundle_ByWork_ConfirmAdded() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addToBundle_ByIds_ConfirmAdded() throws Exception {
		workBundleService.addToBundle(parent.getId(), bundleMemberOne.getId());
		workBundleService.addToBundle(parent.getId(), bundleMemberTwo.getId());

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addToBundle_ByWorkNumbers_ConfirmAdded() throws Exception {
		workBundleService.addToBundle(parent.getWorkNumber(), bundleMemberOne.getWorkNumber());
		workBundleService.addToBundle(parent.getWorkNumber(), bundleMemberTwo.getWorkNumber());

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addToBundle_ByMixOfIdsAndWorkNumbers_ConfirmAdded() throws Exception {
		workBundleService.addToBundle(parent.getId(), bundleMemberOne.getWorkNumber());
		workBundleService.addToBundle(parent.getId(), bundleMemberTwo.getWorkNumber());

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addToBundle_ByMixOfWorkNumbersAndIds_ConfirmAdded() throws Exception {
		workBundleService.addToBundle(parent.getWorkNumber(), bundleMemberOne.getId());
		workBundleService.addToBundle(parent.getWorkNumber(), bundleMemberTwo.getId());

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addToBundle_SameAssignmentTwice_ConfirmUnique() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberOne);

		verifyOneInABundle(bundleMemberOne);
	}

	@Test
	@Transactional
	public void removeFromBundle_ByWork_ConfirmRemoved() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		workBundleService.removeFromBundle(parent, bundleMemberOne);

		verifyOneInABundle(bundleMemberTwo);
	}

	@Test
	@Transactional
	public void removeFromBundle_ByIds_ConfirmRemoved() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		workBundleService.removeFromBundle(parent.getId(), bundleMemberOne.getId());

		verifyOneInABundle(bundleMemberTwo);
	}

	@Test
	@Transactional
	public void removeFromBundle_ByWorkNumbers_ConfirmRemoved() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		workBundleService.removeFromBundle(parent.getWorkNumber(), bundleMemberOne.getWorkNumber());

		verifyOneInABundle(bundleMemberTwo);
	}

	@Test
	@Transactional
	public void removeFromBundle_ByMixOfIdAndWorkNumber_ConfirmRemoved() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		workBundleService.removeFromBundle(parent.getId(), bundleMemberOne.getWorkNumber());

		verifyOneInABundle(bundleMemberTwo);
	}

	@Test
	@Transactional
	public void removeFromBundle_ByMixOfWorkNumberAndId_ConfirmRemoved() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		workBundleService.removeFromBundle(parent.getWorkNumber(), bundleMemberOne.getId());

		verifyOneInABundle(bundleMemberTwo);
	}

	@Test
	@Transactional
	public void addAllToBundleByWorkNumbers_WorkAndListOfWorkNumbers_ConfirmAdded() throws Exception {
		List<String> workNumbers = new ArrayList<>();
		workNumbers.add(bundleMemberOne.getWorkNumber());
		workNumbers.add(bundleMemberTwo.getWorkNumber());

		List<ValidateWorkResponse> response = workBundleService.addAllToBundleByWorkNumbers(parent, workNumbers);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addAllToBundleByIds_WorkAndListOfIds_ConfirmAdded() throws Exception {
		List<Long> workIds = new ArrayList<>();
		workIds.add(bundleMemberOne.getId());
		workIds.add(bundleMemberTwo.getId());

		workBundleService.addAllToBundleByIds(parent, workIds);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addAllToBundleByIds_IdAndListOfIds_ConfirmAdded() throws Exception {
		List<Long> workIds = new ArrayList<>();
		workIds.add(bundleMemberOne.getId());
		workIds.add(bundleMemberTwo.getId());

		workBundleService.addAllToBundleByIds(parent.getId(), workIds);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addAllToBundleByIds_WorkNumberAndListOfIds_ConfirmAdded() throws Exception {
		List<Long> workIds = new ArrayList<>();
		workIds.add(bundleMemberOne.getId());
		workIds.add(bundleMemberTwo.getId());

		workBundleService.addAllToBundleByIds(parent.getWorkNumber(), workIds);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addAllToBundleByWorkNumbers_IdAndListOfWorkNumbers_ConfirmAdded() throws Exception {
		List<String> workNumbers = new ArrayList<>();
		workNumbers.add(bundleMemberOne.getWorkNumber());
		workNumbers.add(bundleMemberTwo.getWorkNumber());

		workBundleService.addAllToBundleByWorkNumbers(parent.getId(), workNumbers);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void addAllToBundleByWorkNumbers_WorkNumberAndListOfWorkNumbers_ConfirmAdded() throws Exception {
		List<String> workNumbers = new ArrayList<>();
		workNumbers.add(bundleMemberOne.getWorkNumber());
		workNumbers.add(bundleMemberTwo.getWorkNumber());

		workBundleService.addAllToBundleByWorkNumbers(parent.getWorkNumber(), workNumbers);

		verifyTwoInABundle();
	}

	@Test
	@Transactional
	public void getAllWorkInBundle_ById_ConfirmGet() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);

		Long parentId = parent.getId();
		Set<Work> bundle = workBundleService.getAllWorkInBundle(parentId);

		assertEquals(bundle.size(), 1);
		assertTrue(bundle.contains(bundleMemberOne));
	}

	@Test
	@Transactional
	public void getAllWorkInBundle_ByWorkNumber_ConfirmGet() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberTwo);

		String parentWorkNumber = parent.getWorkNumber();
		Set<Work> bundle = workBundleService.getAllWorkInBundle(parentWorkNumber);

		assertEquals(bundle.size(), 1);
		assertTrue(bundle.contains(bundleMemberTwo));
	}

	@Test
	@Transactional
	public void getAllInWorkBundle_NoChildren_ConfirmNull() throws Exception {
		assertEquals(parent.getBundle(), null);
		assertEquals(workBundleService.getAllWorkInBundle(parent), null);
	}

	@Test
	@Transactional
	public void getParent_NoParent_ConfirmNull() throws Exception {
		assertEquals(bundleMemberOne.getParent(), null);
	}

	@Test
	@Transactional
	public void getBundleData_BuyerAssignmentsDateRangeAndBudget_ConfirmBundleDateRangeAndTotalBudget() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone(Constants.WM_TIME_ZONE));
		c.set(today().getYear(), Calendar.DECEMBER, 31, 23, 59);
		bundleMemberTwo.setScheduleThrough(c);

		// Make this thing Joda, yo!
		String newYearsEve = new DateMidnight(c).toString("MM/dd/yyyy");

		ServiceResponseBuilder response = workBundleService.getBundleData(employee, parent);

		Map<String, Object> overview = (Map<String, Object>) response.getData().get("overview");

		assertEquals(2, overview.get("assignments"));
		assertEquals("$200.00", overview.get("budget"));
		assertEquals(today(new DateMidnight(c).getZone()).toString("MM/dd/yyyy"), ((Map<String,String>)overview.get("dates")).get("from"));
		assertEquals(newYearsEve, ((Map<String,String>)overview.get("dates")).get("to"));
		assertEquals(2, ((List<Object>)response.getData().get("assignments")).size());
	}

	@Test
	@Transactional
	public void getBundleData_InvitedResourceAndAssignmentsBudget_ConfirmTotalBudget() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		User contractor = newContractorIndependent();
		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());
		workRoutingService.addToWorkResources(parent.getWorkNumber(), Sets.newHashSet(contractor.getUserNumber()));

		ServiceResponseBuilder response = workBundleService.getBundleData(contractor, parent);
		Map<String, Object> overview = (Map<String, Object>) response.getData().get("overview");

		assertEquals("$181.82", overview.get("budget"));
	}

	@Test
	@Transactional
	public void getBundleData_UninvitedResource_ConfirmNullData() throws Exception {
		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);

		User contractor = newContractorIndependent();
		ServiceResponseBuilder response = workBundleService.getBundleData(contractor, parent);
		assertEquals(null, response.getData());
	}

	@Test
	public void authorizeAccountRegister_PaymentTerms_Success() throws Exception {
		User employeeWithPaymentTerms = newFirstEmployeeWithNOCashBalanceAndPaymentTerms(30);
		User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

		authorizeAccountRegister_PaymentTerms_Success_testBody(contractor, employeeWithPaymentTerms);
	}

	// This was put into a transactional context because certain DB operations within the service methods could not see the committed test fixture data
	@Transactional
	public void authorizeAccountRegister_PaymentTerms_Success_testBody(User contractor, User employeeWithPaymentTerms) throws Exception {
		laneService.addUserToCompanyLane2(contractor.getId(), employeeWithPaymentTerms.getCompany().getId());

		parent = newWorkBundle(employeeWithPaymentTerms.getId());
		bundleMemberOne = newWorkWithPaymentTerms(employeeWithPaymentTerms.getId(), 30);
		bundleMemberTwo = newWorkWithPaymentTerms(employeeWithPaymentTerms.getId(), 30);

		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);
		parent.setManageMyWorkMarket(bundleMemberOne.getManageMyWorkMarket());

		WorkAuthorizationResponse response = workBundleService.verifyBundleFunds(employeeWithPaymentTerms.getId(), parent.getId());
		assertEquals(WorkAuthorizationResponse.SUCCEEDED, response);
	}

	@Test
	@Transactional
	public void authorizeAccountRegister_PaymentTerms_Fail() throws Exception {
		User employeeWithPaymentTerms = newFirstEmployeeWithNOCashBalanceAndPaymentTerms();
		// adjust AP limit artificially
		pricingService.updateAPLimit(employeeWithPaymentTerms.getCompany().getId(), "100.00");

		User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

		laneService.addUserToCompanyLane2(contractor.getId(), employeeWithPaymentTerms.getCompany().getId());

		parent = newWorkBundle(employeeWithPaymentTerms.getId());
		bundleMemberOne = newWorkWithPaymentTerms(employeeWithPaymentTerms.getId(), 30);
		bundleMemberTwo = newWorkWithPaymentTerms(employeeWithPaymentTerms.getId(), 30);

		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);
		parent.setManageMyWorkMarket(bundleMemberOne.getManageMyWorkMarket());
		// bundle is set with payment terms and total budget of $200

		WorkAuthorizationResponse response = workBundleService.verifyBundleFunds(employeeWithPaymentTerms.getId(), parent.getId());
		assertEquals(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, response);
	}

	@Test
	@Transactional
	public void authorizeAccountRegister_Prefund_Success() throws Exception {
		User employee = newFirstEmployeeWithCashBalance("200.00");
		User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		parent = newWorkBundle(employee.getId());
		bundleMemberOne = newWork(employee.getId());
		bundleMemberTwo = newWork(employee.getId());

		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);
		parent.setManageMyWorkMarket(bundleMemberOne.getManageMyWorkMarket());
		// bundle is set with payment terms and total budget of $200

		WorkAuthorizationResponse response = workBundleService.verifyBundleFunds(employee.getId(), parent.getId());
		assertEquals(WorkAuthorizationResponse.SUCCEEDED, response);
	}

	@Test
	@Transactional
	public void authorizeAccountRegister_Prefund_Fail() throws Exception {
		User employee = newFirstEmployeeWithCashBalance("100.00");
		User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

		laneService.addUserToCompanyLane2(contractor.getId(), employee.getCompany().getId());

		parent = newWorkBundle(employee.getId());
		bundleMemberOne = newWork(employee.getId());
		bundleMemberTwo = newWork(employee.getId());

		workBundleService.addToBundle(parent, bundleMemberOne);
		workBundleService.addToBundle(parent, bundleMemberTwo);
		parent.setManageMyWorkMarket(bundleMemberOne.getManageMyWorkMarket());
		// bundle is set with payment terms and total budget of $200

		WorkAuthorizationResponse response = workBundleService.verifyBundleFunds(employee.getId(), parent.getId());
		assertEquals(WorkAuthorizationResponse.INSUFFICIENT_FUNDS, response);
	}

	private String dateStringFromToday() {
		return dateStringFromToday(0);
	}

	private String dateStringFromToday(int daysAhead) {
		Calendar today = Calendar.getInstance();
		today.setTimeZone(TimeZone.getTimeZone(Constants.WM_TIME_ZONE));
		today.add(Calendar.DATE, daysAhead);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return sdf.format(today.getTime());
	}

	@Test
	@Transactional
	public void saveOrUpdateWorkBundle_AssignmentsDateRange_ConfirmBundleDefaultsAndDateRange() {
		String fiveDaysAhead = dateStringFromToday(5);
		String oneDayBehind = dateStringFromToday(-1);
		String today = dateStringFromToday();

		Work child1 = newWorkOnSiteWithLocationAndDate(employee.getId(), fiveDaysAhead, null);
		Work child2 = newWorkOnSiteWithLocationAndDate(employee.getId(), oneDayBehind, null);
		Work child3 = newWorkOnSiteWithLocationAndDate(employee.getId(), today, fiveDaysAhead);

		WorkBundleDTO dto = new WorkBundleDTO();
		dto.setTitle("A Thing");
		dto.setDescription("The Thing");
		dto.setWorkNumbers(ImmutableList.of(child1.getWorkNumber(), child2.getWorkNumber(), child3.getWorkNumber()));

		WorkBundle bundle = workBundleService.saveOrUpdateWorkBundle(employee.getId(), dto);

		// confirm pricing
		assertEquals(bundle.getPricingStrategy().getId(), (new FlatPricePricingStrategy()).getId());
		// weird - somehow .001 gets stored as .000909 - maybe 10%? but the price is set directly
		assertEquals(bundle.getPricingStrategy().getFullPricingStrategy().getFlatPrice().doubleValue(), 0.00090909, 0);

		// confirm location
		assertEquals(bundle.isOffsite(), true);

		// confirm dates
		assertEquals(Boolean.TRUE, bundle.getScheduleRangeFlag());
		// yuck - not sure why the time is coming out differently
		assertEquals(DateUtilities.getCalendarFromISO8601(oneDayBehind), bundle.getScheduleFrom());
		assertEquals(DateUtilities.getCalendarFromISO8601(fiveDaysAhead), bundle.getScheduleThrough());
	}

	@Test
	public void validateBundledWorkForAdd_AllDrafts_AllNotInBundle_AllSuccess() {
		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(
			ImmutableList.of(bundleMemberOne.getWorkNumber(), bundleMemberTwo.getWorkNumber()), null
		);

		for (ValidateWorkResponse validateWorkResponse : results) {
			assertTrue(validateWorkResponse.isSuccessful());
		}
	}

	@Test
	public void validateBundledWorkForAdd_NotAllDrafts_AllNotInBundle_MixedSuccess() throws Exception {
		User contractor = newContractor();
		Work routedWork = createWorkAndSendToResourceNoPaymentTerms(employee, contractor);

		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(
			ImmutableList.of(routedWork.getWorkNumber(), bundleMemberTwo.getWorkNumber()), null
		);

		Group<ValidateWorkResponse> validationStatusGroup = group(results, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);

		String expected = routedWork.getTitle() + ": " + messageBundleHelper.getMessage(MessageKeys.Work.INVALID_BUNDLE_WORK_STATE, "", "sent");

		assertEquals(expected, validationErrors.iterator().next().getMessages().get(0));
	}

	@Test
	@Transactional
	public void validateBundledWorkForAdd_AllDrafts_NotAllNotInBundle_MixedSuccess() throws Exception {
		WorkBundle otherBundle = newWorkBundle(employee.getId());
		Work otherBundleMemberOne = newWorkOnSiteWithLocation(employee.getId());
		workBundleService.addToBundle(otherBundle, otherBundleMemberOne);

		List<ValidateWorkResponse> results = workBundleService.validateAllBundledWorkForAdd(
			ImmutableList.of(otherBundleMemberOne.getWorkNumber(), bundleMemberTwo.getWorkNumber()), null
		);

		Group<ValidateWorkResponse> validationStatusGroup = group(results, by(on(ValidateWorkResponse.class).isSuccessful()));
		Collection<ValidateWorkResponse> validationErrors = validationStatusGroup.find(false);

		String expected = otherBundleMemberOne.getTitle() + ": " + messageBundleHelper.getMessage(MessageKeys.Work.INVALID_BUNDLE_WORK_INBUNDLE, "", otherBundle.getTitle());
		assertEquals(expected, validationErrors.iterator().next().getMessages().get(0));
	}

	@Test
	@Transactional
	public void sendBundle_confirm_SENT() throws Exception {
		sendBundle();

		assertEquals(WorkStatusType.SENT, parent.getWorkStatusType().getCode());
		assertEquals(WorkStatusType.SENT, bundleMemberOne.getWorkStatusType().getCode());
		assertEquals(WorkStatusType.SENT, bundleMemberTwo.getWorkStatusType().getCode());
	}

	@Test
	@Transactional
	public void acceptBundle_confirm_ACTIVE() throws Exception {
		sendBundle();
		acceptBundle();

		assertEquals(WorkStatusType.ACTIVE, parent.getWorkStatusType().getCode());
		assertEquals(WorkStatusType.ACTIVE, bundleMemberOne.getWorkStatusType().getCode());
		assertEquals(WorkStatusType.ACTIVE, bundleMemberTwo.getWorkStatusType().getCode());

		assertEquals(resource.getId(), workService.findActiveWorkResource(parent.getId()).getUser().getId());
		assertEquals(resource.getId(), workService.findActiveWorkResource(bundleMemberOne.getId()).getUser().getId());
		assertEquals(resource.getId(), workService.findActiveWorkResource(bundleMemberTwo.getId()).getUser().getId());
	}

	@Test
	@Transactional
	public void updateBundleComplete_All_PAID() throws Exception {
		sendBundle();
		acceptBundle();
		bundlePaidSetup();

		assertTrue(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	@Transactional
	public void isBundleComplete_All_PAID() throws Exception {
		sendBundle();
		acceptBundle();
		bundlePaidSetup();

		assertFalse(workBundleService.isBundleComplete(parent.getId()));
		workBundleService.updateBundleComplete(parent.getId());
		assertTrue(workBundleService.isBundleComplete(parent.getId()));
	}

	@Test
	@Transactional
	public void updateBundleComplete_Not_All_PAID() throws Exception {
		sendBundle();
		acceptBundle();

		authenticationService.setCurrentUser(resource.getId());
		Map<String, String> resolution = CollectionUtilities.newStringMap("resolution", "Complete the assignment");

		workService.updateWorkProperties(bundleMemberOne.getId(), resolution);
		workService.completeWork(bundleMemberOne.getId(), new CompleteWorkDTO());

		// pay work
		authenticationService.setCurrentUser(employee.getId());
		workService.closeWork(bundleMemberOne.getId());

		assertFalse(workBundleService.updateBundleComplete(parent.getId()));
	}

	@Test
	@Transactional
	public void updateBundleVoid_Empty_Draft_True() throws Exception {
		assertTrue(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	@Transactional
	public void updateBundleVoid_Empty_Sent_True() throws Exception {
		sendBundle();

		workBundleService.removeFromBundle(parent, bundleMemberOne);
		workBundleService.removeFromBundle(parent, bundleMemberTwo);

		assertTrue(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	@Transactional
	public void updateBundleVoid_Not_Empty_Sent_False() throws Exception {
		sendBundle();

		assertFalse(workBundleService.updateBundleVoid(parent.getId()));
	}

	@Test
	public void findByChild_returnParent() {
		workBundleService.addToBundle(parent.getId(), bundleMemberOne.getId());
		WorkBundleDTO parentDTO = workBundleService.findByChild(bundleMemberOne.getId());

		assertEquals(parent.getId(), parentDTO.getId());
		assertEquals(parent.getTitle(), parentDTO.getTitle());
	}

	private void bundlePaidSetup() throws Exception {
		// complete work
		authenticationService.setCurrentUser(resource.getId());
		Map<String, String> resolution = CollectionUtilities.newStringMap("resolution", "Complete the assignment");

		workService.updateWorkProperties(bundleMemberOne.getId(), resolution);
		workService.completeWork(bundleMemberOne.getId(), new CompleteWorkDTO());

		workService.updateWorkProperties(bundleMemberTwo.getId(), resolution);
		workService.completeWork(bundleMemberTwo.getId(), new CompleteWorkDTO());

		// pay work
		authenticationService.setCurrentUser(employee.getId());
		workService.closeWork(bundleMemberOne.getId());
		workService.closeWork(bundleMemberTwo.getId());
	}

	private void acceptBundle() throws Exception {
		tWorkFacadeService.acceptWork(resource.getId(), parent.getId());
		tWorkFacadeService.acceptWorkBundle(resource.getId(), parent.getId());
	}

	private void sendBundle() throws Exception {
		workBundleService.addAllToBundleByWork(parent, ImmutableList.of(bundleMemberOne, bundleMemberTwo));
		workBundleValidationHelper.readyToSend(parent.getWorkNumber(), employee.getId(), messageBundleHelper.newBundle());
		laneService.addUserToCompanyLane2(resource.getId(), employee.getCompany().getId());
		Set<String> selectedResourcesUserNumbers = Sets.newHashSet(resource.getUserNumber());

		workRoutingService.addToWorkResources(parent.getWorkNumber(), selectedResourcesUserNumbers).getResponse();
		workBundleRouting.routeWorkBundle(parent.getId());
	}

	private void verifyTwoInABundle() {
		Set<Work> bundleMembers = parent.getBundle();

		assertNotNull(bundleMembers);
		assertEquals(bundleMembers.size(), 2);
		assertTrue(bundleMembers.contains(bundleMemberOne));
		assertTrue(bundleMembers.contains(bundleMemberTwo));
	}

	private void verifyOneInABundle(Work workThatShouldBeThere) {
		Set<Work> bundleMembers = parent.getBundle();

		assertNotNull(bundleMembers);
		assertEquals(bundleMembers.size(), 1);
		assertTrue(bundleMembers.contains(workThatShouldBeThere));
	}
}
