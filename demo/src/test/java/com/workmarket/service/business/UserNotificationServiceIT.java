package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.account.BankAccountTransactionStatus;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.banking.AbstractBankAccount;
import com.workmarket.domains.model.banking.BankAccount;
import com.workmarket.domains.model.banking.BankAccountPagination;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.model.notification.NotificationType;
import com.workmarket.domains.model.notification.UserNotification;
import com.workmarket.domains.model.notification.UserNotificationPagination;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.request.UserGroupInvitation;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkQuestionService;
import com.workmarket.dto.UnreadNotificationsDTO;
import com.workmarket.dto.UserGroupDTO;
import com.workmarket.helpers.WMCallable;
import com.workmarket.service.business.dto.CompanyDTO;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.RatingDTO;
import com.workmarket.service.business.dto.RecruitingCampaignDTO;
import com.workmarket.service.business.dto.UserDTO;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.dto.UserNotificationDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserNotificationServiceIT extends BaseServiceIT {

	@Autowired private LaneService laneService;
	@Autowired private RegistrationService registrationService;
	@Autowired private ProfileService profileService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserNotificationService userNotificationService;
	@Autowired private UserGroupService userGroupService;
	@Autowired private RequestService requestService;
	@Autowired private RatingService ratingService;
	@Autowired private RecruitingService recruitingService;
	@Autowired private WorkQuestionService workQuestionService;
	@Autowired private BankAccountDAO bankAccountDAO;

	@Autowired @Qualifier("accountRegisterServicePrefundImpl") private AccountRegisterService accountRegisterService;

	private static final boolean assignToFirstToAccept = false;

	@Test
	@Ignore
	public void archiveUserNotification_archiveAndRefreshCache() throws Exception {
		User user = newEmployeeWithCashBalance();

		UserNotificationDTO userNotificationDTO = new UserNotificationDTO();
		userNotificationDTO.setToUserId(user.getId());
		userNotificationDTO.setFromUserId(user.getId());
		userNotificationDTO.setMsg("test notification");
		userNotificationDTO.setSticky(true);
		userNotificationDTO.setNotificationType(NotificationType.ADD_FUNDS_WIRE);

		userNotificationService.sendUserNotification(userNotificationDTO);

		await().atMost(JMS_DELAY, MILLISECONDS).until(notificationListIsNotEmpty(user.getId()));

		UserNotificationPagination pagination = new UserNotificationPagination();
		pagination.setReturnAllRows();

		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.ARCHIVED, Boolean.FALSE);
		pagination.addFilter(UserNotificationPagination.FILTER_KEYS.USER_FILTERS, Boolean.FALSE);
		pagination = userNotificationService.findAllUserNotifications(user.getId(), pagination);

		for (UserNotification n : pagination.getResults()) {
			userNotificationService.archiveUserNotification(n.getUuid(), user.getId());
		}

		await().atMost(JMS_DELAY, MILLISECONDS).until(notificationListIsEmpty(user.getId()));

		UnreadNotificationsDTO notificationsDTO = userNotificationService.getUnreadNotificationsInfoByUser(user.getId());

		assertEquals(notificationsDTO.getUnreadCount(), 0);
	}

	private Callable<Boolean> notificationListIsNotEmpty(final Long userId) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				UnreadNotificationsDTO notificationsDTO = userNotificationService.getUnreadNotificationsInfoByUser(userId);
				return notificationsDTO != null && notificationsDTO.getUnreadCount() > 0;
			}
		};
	}

	private Callable<Boolean> notificationListIsEmpty(final Long userId) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				UnreadNotificationsDTO notificationsDTO = userNotificationService.getUnreadNotificationsInfoByUser(userId);
				return notificationsDTO == null || notificationsDTO.getUnreadCount() == 0;
			}
		};
	}

	@Test
	@Ignore
	public void lane23CreatedUserNotification() throws Exception {
		User employee = newEmployeeWithCashBalance();
		User user = newContractorIndependentLane4ReadyWithCashBalance();

		laneService.addUserToCompanyLane2(user.getId(), employee.getCompany().getId());

		Assert.assertFalse(authenticationService.getEmailConfirmed(user));

		LaneAssociation lane = laneService
			.findActiveAssociationByUserIdAndCompanyId(
				user.getId(),
				employee.getCompany().getId());

		Assert.assertNotNull(lane);
		Assert.assertTrue(lane.getLaneType().equals(LaneType.LANE_2));

		//User confirms his account
		registrationService.confirmAccount(user.getId());

		CompanyDTO dto = new CompanyDTO();
		dto.setCompanyId(employee.getCompany().getId());
		dto.setCustomLowBalanceFlag(true);
		dto.setLowBalanceAmount(4999999.00);
		dto.setName("company name");
		dto.setOperatingAsIndividualFlag(false);

		profileService.saveOrUpdateCompany(dto);

		//Employee creates work
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(4999009.20);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2009-06-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);
		Assert.assertNotNull(work);

		//Employee invites contractor
		workRoutingService.addToWorkResources(work.getId(), user.getId(), assignToFirstToAccept);

		//Contractor accepts work
		workService.acceptWork(user.getId(), work.getId());

		//Find the User notifications for the contractor
		UserNotificationPagination pagination = new UserNotificationPagination(true);
		pagination = userNotificationService.findAllUserNotifications(user.getId(), pagination);

		Assert.assertNotNull(pagination);
		Assert.assertFalse(pagination.getResults().isEmpty());

		workService.completeWork(work.getId(), new CompleteWorkDTO());

		workService.closeWork(work.getId());
	}

	@Test
	@Ignore
	public void onUserApprovedToGroup() throws Exception {
		User employee = newEmployeeWithCashBalance();

		UserGroupDTO userGroupDTO = new UserGroupDTO();
		userGroupDTO.setCompanyId(employee.getCompany().getId());
		userGroupDTO.setName("group" + RandomUtilities.generateAlphaString(10));
		userGroupDTO.setDescription("description");
		userGroupDTO.setOpenMembership(true);
		userGroupDTO.setRequiresApproval(true);
		UserGroup group1 = userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);

		userGroupService.saveOrUpdateCompanyUserGroup(userGroupDTO);

		User contractor1 = newContractorIndependentlane4Ready();
		User contractor2 = newContractorIndependentlane4Ready();

		userGroupService.addUsersToGroup(Lists.newArrayList(contractor1.getId(), contractor2.getId()), group1.getId(), 1L);

		UserUserGroupAssociation association1 = userGroupService.findAssociationByGroupIdAndUserId(group1.getId(), contractor1.getId());
		UserUserGroupAssociation association2 = userGroupService.findAssociationByGroupIdAndUserId(group1.getId(), contractor2.getId());

		Assert.assertNull(association1);
		Assert.assertNull(association2);

		List<UserGroupInvitation> invitations1 = requestService.findUserGroupInvitationRequestsByInvitedUserAndUserGroup(contractor1.getId(), group1.getId());
		List<UserGroupInvitation> invitations2 = requestService.findUserGroupInvitationRequestsByInvitedUserAndUserGroup(contractor1.getId(), group1.getId());

		Assert.assertEquals(1, invitations1.size());
		Assert.assertEquals(1, invitations2.size());

		userGroupService.applyToGroup(group1.getId(), contractor1.getId());
		userGroupService.applyToGroup(group1.getId(), contractor2.getId());

		association1 = userGroupService.findAssociationByGroupIdAndUserId(group1.getId(), contractor1.getId());
		association2 = userGroupService.findAssociationByGroupIdAndUserId(group1.getId(), contractor2.getId());

		Assert.assertTrue(association1.getVerificationStatus().isVerified());
		Assert.assertTrue(association2.getVerificationStatus().isVerified());

		Assert.assertTrue(association1.getApprovalStatus().isPending());
		Assert.assertTrue(association2.getApprovalStatus().isPending());

		//User 1 gets approved
		userGroupService.approveUser(group1.getId(), contractor1.getId());

		//User 2 gets declined
		userGroupService.declineUser(group1.getId(), contractor2.getId());

		//Find the User notifications for user 1
		UserNotificationPagination pagination = new UserNotificationPagination(true);
		pagination = userNotificationService.findAllUserNotifications(contractor1.getId(), pagination);

		Assert.assertNotNull(pagination);
		Assert.assertFalse(pagination.getResults().isEmpty());
		for (UserNotification notification : pagination.getResults()) {
			Assert.assertTrue(notification.getNotificationType().getCode().equals(NotificationType.GROUP_APPROVED));
		}

		//Find the User notifications for the employee
		pagination = userNotificationService.findAllUserNotifications(contractor2.getId(), pagination);

		Assert.assertNotNull(pagination);
		Assert.assertFalse(pagination.getResults().isEmpty());
		Assert.assertTrue(pagination.getResults().size() == 1);
		for (UserNotification notification : pagination.getResults()) {
			Assert.assertTrue(notification.getNotificationType().getCode().equals(NotificationType.GROUP_DECLINED));
		}

	}

	@Test
	@Ignore
	public void onUserGroupInvitation() throws Exception {
		requestService.inviteUserToGroup(ANONYMOUS_USER_ID, CONTRACTOR_USER_ID, USER_GROUP_ID);
	}

	@Test
	@Ignore
	public void onRating() throws Exception {
		RatingDTO ratingDTO = new RatingDTO(Rating.EXCELLENT, "Awesome work!");
		ratingDTO.setReviewSharedFlag(false);
		ratingDTO.setRatingSharedFlag(true);
		Rating rating = ratingService.createRatingForWork(6458L, 6457L, 1180L, ratingDTO);
		Assert.assertNotNull(rating);
	}

	@Test
	public void onQuestion() throws Exception {
		User employee = newEmployeeWithCashBalance();

		//Employee creates work
		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Do Work!");
		workDTO.setDescription("Description of work.");
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2009-06-02T09:00:00Z");

		Work work = workFacadeService.saveOrUpdateWork(employee.getId(), workDTO);
		Assert.assertNotNull(work);

		WorkQuestionAnswerPair qa = workQuestionService.saveQuestion(work.getId(), ANONYMOUS_USER_ID, "Will this test pass?");
		Assert.assertNotNull(qa);
	}

	@Test
	@Ignore
	public void onconfirmAccount() throws Exception {
		User employee = newWMEmployee();
		UserGroupDTO groupDTO = new UserGroupDTO();
		groupDTO.setCompanyId(employee.getCompany().getId());
		groupDTO.setDescription("test for recruiting campaign");
		groupDTO.setOpenMembership(true);
		groupDTO.setRequiresApproval(false);
		groupDTO.setName("Group name for recruiting campaign" + RandomUtilities.nextLong());

		UserGroup group = userGroupService.saveOrUpdateCompanyUserGroup(groupDTO);

		Assert.assertNotNull(group);

		RecruitingCampaignDTO dto = new RecruitingCampaignDTO();
		dto.setCompanyId(employee.getCompany().getId());
		dto.setCompanyUserGroupId(group.getId());
		dto.setTitle("recruiting" + RandomUtilities.nextLong());
		dto.setDescription("Recruiting description");
		dto.setRecruitingVendorId(RECRUITING_VENDOR_1_CODE);

		RecruitingCampaign campaign = recruitingService.saveOrUpdateRecruitingCampaign(dto);

		Assert.assertNotNull(campaign);

		UserDTO userDTO = new UserDTO();
		String userName = "contractorUser" + RandomUtilities.nextLong();
		userDTO.setEmail(userName + "@workmarket.com");
		userDTO.setFirstName(userName + "@workmarket.com");
		userDTO.setLastName(userName + "@workmarket.com");
		userDTO.setPassword("" + RandomUtilities.nextLong());
		userDTO.setRecruitingCampaignId(campaign.getId());
		User invited = registrationService.registerNew(userDTO, null);

		Assert.assertNotNull(invited);

		registrationService.confirmAccount(invited.getId());
	}

	@Test
	public void workResourceConfirmed() throws Exception {
		User employee = newEmployeeWithCashBalance();

		authenticationService.setCurrentUser(employee);

		Work work = newWorkOnSiteWithLocationWithRequiredConfirmationAndCheckin(employee.getId());

		User contractor = newContractorIndependentlane4Ready();

		laneService.addUserToCompanyLane3(contractor.getId(), employee.getCompany().getId());

		workRoutingService.addToWorkResources(work.getId(), contractor.getId(), assignToFirstToAccept);

		workService.acceptWork(contractor.getId(), work.getId());

		workService.confirmWorkResource(contractor.getId(), work.getId());
	}

	@Test
	@Transactional
	public void addFundPendingAndDeposited() throws Exception {
		User user = newInternalUser();
		User employee = newEmployeeWithCashBalance();

		authenticationService.setCurrentUser(employee);
		BankAccount account = newBankAccount(employee.getCompany());
		bankAccountDAO.saveOrUpdate(account);
		BankAccountPagination pagination = new BankAccountPagination();
		List<AbstractBankAccount> bankAccounts = bankAccountDAO.find(employee.getCompany().getId(), pagination).getResults();
		//deposited
		Long transactionId = accountRegisterService.addFundsToRegisterFromAch(user.getId(),bankAccounts.get(0).getId(), "10");
		//approved
		RegisterTransaction transaction = accountRegisterService.findRegisterTransaction(transactionId);
		BankAccountTransaction bankTransaction = (BankAccountTransaction) accountRegisterService.findRegisterTransaction(transactionId);
		bankTransaction.setBankAccountTransactionStatus(new BankAccountTransactionStatus(BankAccountTransactionStatus.APPROVED));
		userNotificationService.onCreditTransaction(transaction);
	}
}
