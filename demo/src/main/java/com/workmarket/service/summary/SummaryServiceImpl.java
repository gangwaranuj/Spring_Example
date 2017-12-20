package com.workmarket.service.summary;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import com.workmarket.dao.InvitationDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.dao.recruiting.RecruitingCampaignDAO;
import com.workmarket.dao.summary.HistorySummaryEntityDAO;
import com.workmarket.dao.summary.TimeDimensionDAO;
import com.workmarket.dao.summary.company.CompanySummaryDAO;
import com.workmarket.dao.summary.group.UserGroupSummaryDAO;
import com.workmarket.dao.summary.user.UserSummaryDAO;
import com.workmarket.dao.summary.work.WorkHistorySummaryDAO;
import com.workmarket.dao.summary.work.WorkStatusTransitionDAO;
import com.workmarket.data.aggregate.CompanyAggregate;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupLastRoutedDTO;
import com.workmarket.domains.groups.model.UserGroupThroughputDTO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import com.workmarket.domains.model.DateFilter;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.InvitationStatusType;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.block.AbstractBlockedAssociation;
import com.workmarket.domains.model.block.BlockedUserCompanyAssociation;
import com.workmarket.domains.model.block.BlockedUserUserAssociation;
import com.workmarket.domains.model.cache.MoneyAggregateSummary;
import com.workmarket.domains.model.cache.PaymentCenterAggregateSummary;
import com.workmarket.domains.model.cache.PeopleAggregateSummary;
import com.workmarket.domains.model.rating.Rating;
import com.workmarket.domains.model.summary.TimeDimension;
import com.workmarket.domains.model.summary.company.CompanySummary;
import com.workmarket.domains.model.summary.group.UserGroupHistorySummary;
import com.workmarket.domains.model.summary.group.UserGroupSummary;
import com.workmarket.domains.model.summary.user.BlockedUserHistorySummary;
import com.workmarket.domains.model.summary.user.UserHistorySummary;
import com.workmarket.domains.model.summary.user.UserRatingChangeLogSummary;
import com.workmarket.domains.model.summary.user.UserRatingHistorySummary;
import com.workmarket.domains.model.summary.user.UserSummary;
import com.workmarket.domains.model.summary.work.WorkHistorySummary;
import com.workmarket.domains.model.summary.work.WorkResourceHistorySummary;
import com.workmarket.domains.model.summary.work.WorkStatusPK;
import com.workmarket.domains.model.summary.work.WorkStatusTransition;
import com.workmarket.domains.model.summary.work.WorkStatusTransitionHistorySummary;
import com.workmarket.domains.payments.dao.AccountStatementDetailDAO;
import com.workmarket.domains.payments.dao.BankAccountDAO;
import com.workmarket.domains.payments.service.AccountRegisterService;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.dto.CompanySummaryDTO;
import com.workmarket.service.business.dto.WorkAggregatesDTO;
import com.workmarket.service.business.event.user.UserAverageRatingEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.google.common.base.MoreObjects.firstNonNull;

@Service
public class SummaryServiceImpl implements SummaryService {

	@Autowired private CompanyDAO companyDAO;
	@Autowired private InvitationDAO invitationDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private RecruitingCampaignDAO recruitingCampaignDAO;
	@Autowired private ProfileDAO profileDAO;
	@Qualifier("accountRegisterServicePrefundImpl")
	@Autowired private AccountRegisterService accountRegisterServiceNetMoneyImpl;
	@Autowired private UserGroupService userGroupService;
	@Autowired private WorkService workService;
	@Autowired private CompanyService companyService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private TimeDimensionDAO timeDimensionDAO;
	@Autowired private HistorySummaryEntityDAO historySummaryEntityDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkStatusTransitionDAO workStatusTransitionDAO;
	@Autowired private PricingService pricingService;
	@Autowired private AccountStatementDetailDAO accountStatementDetailDAO;
	@Autowired private CompanySummaryDAO companySummaryDAO;
	@Autowired private UserGroupSummaryDAO userGroupSummaryDAO;
	@Autowired private UserSummaryDAO userSummaryDAO;
	@Autowired private WorkHistorySummaryDAO workHistorySummaryDAO;
	@Autowired private BankAccountDAO bankAccountDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private IndustryService industryService;
	@Autowired private ProfileService profileService;
	@Autowired private EventRouter eventRouter;
	@Autowired private BillingService billingService;

	private static final Log logger = LogFactory.getLog(SummaryServiceImpl.class);

	private static final List<String> WORK_STATUS_TYPE_PRIOR_COMPLETE = Lists.newArrayList();
	private static final Calendar GCC_RELEASE_DATE;
	static {
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.DRAFT);
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.SENT);
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.DECLINED);
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.ACTIVE);
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.VOID);
		WORK_STATUS_TYPE_PRIOR_COMPLETE.add(WorkStatusType.ABANDONED);

		GCC_RELEASE_DATE = DateUtilities.newCalendar(2013, Calendar.NOVEMBER, 5, 0, 0, 0);
	}

	@Override
	public PeopleAggregateSummary findPeopleAggregateSummaryByCompany(Long companyId) {
		Assert.notNull(companyId);

		PeopleAggregateSummary summary = new PeopleAggregateSummary();

		CompanyAggregate companyAggregate = companyService.findCompanyAggregate(companyId);
		if (companyAggregate != null) {
			summary.setLane0(companyAggregate.getLane0Users());
			summary.setLane1(companyAggregate.getLane1Users());
			summary.setLane2(companyAggregate.getLane2Users());
			summary.setLane3(companyAggregate.getLane3Users());
		}

		summary.setLane3WithEINTaxEntity(companyDAO.countAllLane3UsersWithEINsByCompany(companyId));

		// groups
		summary.setGroups(userGroupService.countAllGroupsByCompany(companyId));
		summary.setInvitations(invitationDAO.countInvitationsByCompanyAndStatus(companyId, new InvitationStatusType(InvitationStatusType.SENT)));
		summary.setCampaigns(recruitingCampaignDAO.countCampaignsForCompany(companyId));
		summary.setGroupMembers(userGroupService.countAllActiveGroupMembersByCompany(companyId));

		return summary;
	}

	@Override
	public MoneyAggregateSummary findMoneyAggregateSummaryByCompany(Long companyId) {
		Assert.notNull(companyId);

		MoneyAggregateSummary summary = new MoneyAggregateSummary();

		summary.setTotal(accountRegisterServiceNetMoneyImpl.calcAvailableCashByCompany(companyId));

		summary.setInProgress(accountRegisterServiceNetMoneyImpl.calcPendingCashByCompany(companyId));
		summary.setAvailable(accountRegisterServiceNetMoneyImpl.calcSufficientBuyerFundsByCompany(companyId));

		summary.setEarnedInProgress(accountRegisterServiceNetMoneyImpl.calcEarnedInProgressByCompany(companyId));
		summary.setEarnedPending(accountRegisterServiceNetMoneyImpl.calcEarnedPendingByCompany(companyId));
		summary.setEarnedAvailable(accountRegisterServiceNetMoneyImpl.calculateWithdrawableCashByCompany(companyId));

		summary.setDue7Days(BigDecimal.valueOf(0));

		return summary;
	}

	@Override
	public BigDecimal getTotalUpcomingDueIn24Hours(Long userId) {
		User user = userDAO.get(userId);
		boolean isAdminOrController = authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user);
		return accountStatementDetailDAO.sumTotalUpcomingDueIn24Hours(userId, user.getCompany().getId(), isAdminOrController, true);
	}

	@Override
	public PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForBuyer(Long userId) {
		User user = userDAO.get(userId);
		boolean isAdminOrController = authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user);

		BigDecimal pastDue = accountStatementDetailDAO.sumTotalPastDue(userId, user.getCompany().getId(), isAdminOrController, true);
		BigDecimal upcomingDue = accountStatementDetailDAO.sumTotalUpcomingDue(userId, user.getCompany().getId(), isAdminOrController, true);
		BigDecimal paidYtd = BigDecimal.ZERO;

		PaymentCenterAggregateSummary summary = new PaymentCenterAggregateSummary();
		summary.setPastDue(pastDue);
		summary.setUpcomingDue(upcomingDue);
		summary.setPaidYtd(paidYtd);

		return summary;
	}

	@Override
	public PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForSeller(Long userId) {
		return getPaymentCenterAggregateSummaryForSeller(userId, true);
	}

	@Override
	public PaymentCenterAggregateSummary getPaymentCenterAggregateSummaryForSeller(Long userId, boolean calculateFastFundableAmount) {
		User user = userDAO.get(userId);
		boolean isAdminOrController = authenticationService.hasAccessToAllInvoicesAndStatementsAtCompany(user);

		BigDecimal pastDue = accountStatementDetailDAO.sumTotalPastDue(userId, user.getCompany().getId(), isAdminOrController, false);
		BigDecimal upcomingDue = accountStatementDetailDAO.sumTotalUpcomingDue(userId, user.getCompany().getId(), isAdminOrController, false);
		BigDecimal paidYtd = accountStatementDetailDAO.sumTotalPaid(userId, user.getCompany().getId(), isAdminOrController, false, new DateFilter(DateUtilities.getMidnightYTD(), DateUtilities.getMidnightYTDNextYear()));

		PaymentCenterAggregateSummary summary = new PaymentCenterAggregateSummary();
		summary.setPastDue(pastDue);
		summary.setUpcomingDue(upcomingDue);
		summary.setPaidYtd(paidYtd);
		if(calculateFastFundableAmount) {
			final BigDecimal totalFastFundableAmount = billingService.calculateTotalFastFundableResourceCostForWorker(userId);
			summary.setTotalFastFundableAmount(totalFastFundableAmount);
		}

		return summary;
	}

	@Override
	public WorkAggregatesDTO countWorkByCompany(Long companyId)  {
		return workService.countWorkByCompany(companyId);
	}

	@Override
	public Integer countAssessmentsByCompany(Long companyId) {
		return assessmentService.countAssessmentsByCompany(companyId);
	}

	@Override
	public Long findTimeDimensionId(Calendar calendar) {
		return timeDimensionDAO.findTimeDimensionId(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.HOUR_OF_DAY));
	}

	@Override
	public TimeDimension findTimeDimension(Calendar calendar) {
		return timeDimensionDAO.findTimeDimension(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.HOUR_OF_DAY));
	}

	@Override
	public WorkHistorySummary saveWorkHistorySummary(AbstractWork work) {
		WorkResource workResource = workService.findActiveWorkResource(work.getId());
		return saveWorkHistorySummary(work, work.getWorkStatusType().getCode(), workResource, Calendar.getInstance());
	}

	@Override
	public WorkHistorySummary saveWorkHistorySummary(AbstractWork work, WorkResource workResource) {
	 	return saveWorkHistorySummary(work, work.getWorkStatusType().getCode(), workResource, Calendar.getInstance());
	}

	@Override
	public WorkHistorySummary saveWorkHistorySummary(AbstractWork work, WorkResource workResource, String workStatusTypeCode) {
		return saveWorkHistorySummary(work, workStatusTypeCode, workResource, GregorianCalendar.getInstance());
	}

	@Override
	public WorkHistorySummary saveWorkHistorySummary(Long workId, String workStatusTypeCode, WorkResource workResource, Calendar date) {
		AbstractWork work = workDAO.findWorkById(workId);
		return saveWorkHistorySummary(work, workStatusTypeCode, workResource, date);
	}

	private WorkHistorySummary saveWorkHistorySummary(AbstractWork work, String workStatusTypeCode, WorkResource workResource, Calendar date) {
		WorkHistorySummary summary = new WorkHistorySummary();

		if (work == null || StringUtils.isBlank(workStatusTypeCode)) {
			return null;
		}

		// need to load time dimension and build summary - this won't pass a test yet
		Long dateId = findTimeDimensionId(date);

		if (dateId == null) {
			logger.error("Date Id not found - skipping work summary history for work: " + work.getId());
			return null;
		}

		summary.setDateId(dateId);
		summary.setWorkId(work.getId());
		summary.setCompanyId(work.getCompany().getId());
		summary.setBuyerUserId(work.getBuyer().getId());

		if (work.getIndustry() != null) {
			summary.setIndustryId(work.getIndustry().getId());
		}

		BigDecimal workPrice = getWorkPrice(work, workStatusTypeCode);
		BigDecimal buyerFee = BigDecimal.ZERO;
		BigDecimal buyerTotalCost = BigDecimal.ZERO;

		if (work.getFulfillmentStrategy() != null) {
			if (work.getFulfillmentStrategy().getBuyerFee() != null) {
				buyerFee = work.getFulfillmentStrategy().getBuyerFee().abs();
			}
			if (work.getFulfillmentStrategy().getBuyerTotalCost() != null) {
				buyerTotalCost = work.getFulfillmentStrategy().getBuyerTotalCost().abs();
			}
		}
		summary.setWorkPrice(workPrice);
		summary.setBuyerFee(buyerFee);
		summary.setBuyerTotalCost(buyerTotalCost);

		summary.setWorkStatusTypeCode(workStatusTypeCode);
		summary.setPaymentTermsEnabled(work.hasPaymentTerms());

		if (workResource != null && workResource.getUser() != null) {
			summary.setActiveResourceUserId(workResource.getUser().getId());
			summary.setActiveResourceCompanyId(workResource.getUser().getCompany().getId());
		}

		summary.setAccountPricingType(work.getAccountPricingType().getCode());
		summary.setAccountServiceType(work.getAccountServiceType().getCode());
		historySummaryEntityDAO.saveOrUpdate(summary);

		return summary;
	}

	@Override
	public UserHistorySummary saveUserHistorySummary(User user) {
		UserHistorySummary summary = new UserHistorySummary();

		if (user == null) {
			return null;
		}

		Long dateId = findTimeDimensionId(GregorianCalendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping user summary history for user: " + user.getId());
			return null;
		}

		if (user.getCompany() == null) {
			logger.error("User with no company - skipping user summary history for user: " + user.getId());
			return null;
		}

		Profile profile = profileDAO.findByUser(user.getId());
		if (profile == null) {
			logger.error("User with no profile - skipping user summary history for user: " + user.getId());
			return null;
		}

		summary.setDateId(dateId);
		summary.setUserId(user.getId());
		summary.setUserStatusTypeCode(authenticationService.getUserStatus(user).getCode());
		summary.setCompanyId(user.getCompany().getId());
		Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profile.getId());

		if (defaultIndustry != null) {
			summary.setIndustryId(defaultIndustry.getId());
		} else {
			logger.info("User with no profile industry, defaulting to NONE. userId:" + user.getId());
			summary.setIndustryId(Industry.NONE.getId());
		}
		if (profile.getFindWork() != null) {
			summary.setFindWork(profile.getFindWork());
		}
		if (profile.getManageWork() != null) {
			summary.setManageWork(profile.getManageWork());
		}
		historySummaryEntityDAO.saveOrUpdate(summary);
		return summary;
	}

	@Override
	public WorkResourceHistorySummary saveWorkResourceHistorySummary(WorkResource workResource) {
		if (workResource == null) {
			return null;
		}

		Long dateId = findTimeDimensionId(GregorianCalendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping workResource summary history for workResource: " + workResource.getId());
			return null;
		}
		WorkResourceHistorySummary summary = buildWorkResourceHistorySummary(workResource, dateId);
		summary.setUserIndustryId(industryService.getDefaultIndustryForProfile(workResource.getUser().getProfile().getId()).getId());
		historySummaryEntityDAO.saveOrUpdate(summary);
		userIndexer.reindexById(summary.getUserId());

		return summary;
	}

	@Override
	public void saveWorkResourceHistorySummary(Set<WorkResource> workResources) {
		Long dateId = findTimeDimensionId(GregorianCalendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping workResource summary history");
			return;
		}
		List<WorkResourceHistorySummary> summaries = Lists.newArrayListWithExpectedSize(workResources.size());
		Map<Long, Long> industryMap =  industryService.getDefaultIndustriesForUsers(extract(workResources, on(WorkResource.class).getUser().getId()));
		for (WorkResource workResource: workResources) {
			WorkResourceHistorySummary workResourceHistorySummary = buildWorkResourceHistorySummary(workResource, dateId);
			workResourceHistorySummary.setUserIndustryId(industryMap.get(workResource.getUser().getId()));
			summaries.add(workResourceHistorySummary);
		}
		historySummaryEntityDAO.saveAll(summaries);
	}

	private WorkResourceHistorySummary buildWorkResourceHistorySummary(WorkResource workResource, Long dateId) {
		WorkResourceHistorySummary summary = new WorkResourceHistorySummary();
		summary.setDateId(dateId);
		summary.setWorkResourceId(workResource.getId());
		summary.setUserId(workResource.getUser().getId());
		summary.setWorkResourceStatusTypeCode(workResource.getWorkResourceStatusType().getCode());
		summary.setWorkId(workResource.getWork().getId());

		if (workResource.getUser().getCompany() != null) {
			summary.setUserCompanyId(workResource.getUser().getCompany().getId());
		}
		return summary;
	}

	@Override
	public UserGroupHistorySummary saveUserGroupAssociationHistorySummary(UserUserGroupAssociation userUserGroupAssociation) {
		UserGroupHistorySummary summary = new UserGroupHistorySummary();

		if (userUserGroupAssociation == null) {
			return null;
		}

		if (userUserGroupAssociation.getUserGroup() == null) {
			logger.error("No group associated with userUserGroupAssociation id: " + userUserGroupAssociation.getId());
			return null;
		}

		Long dateId = findTimeDimensionId(GregorianCalendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping userUserGroupAssociation summary history for id: " + userUserGroupAssociation.getId());
			return null;
		}

		UserGroup group = userGroupService.findGroupById(userUserGroupAssociation.getUserGroup().getId());

		if (group != null) {
			User user = userUserGroupAssociation.getUser();
			summary.setDateId(dateId);
			summary.setGroupId(group.getId());
			summary.setGroupCompanyId(group.getCompany().getId());

			if (group.getIndustry() != null) {
				summary.setGroupIndustryId(group.getIndustry().getId());
			}

			summary.setUserId(user.getId());
			summary.setUserCompanyId(user.getCompany().getId());

			summary.setOverrideMember(userUserGroupAssociation.isOverrideMember());
			summary.setVerificationStatus(userUserGroupAssociation.getVerificationStatus().ordinal());
			summary.setApprovalStatus(userUserGroupAssociation.getApprovalStatus().ordinal());

			if (userUserGroupAssociation.getDeleted()) {
				summary.setUserGroupAssociationStatusTypeCode(UserGroupHistorySummary.DELETED_USER_GROUP_ASSOCIATION_STATUS);
			} else {
				switch (userUserGroupAssociation.getApprovalStatus()) {
				case APPROVED:
					summary.setUserGroupAssociationStatusTypeCode(UserGroupHistorySummary.APPROVED_USER_GROUP_ASSOCIATION_STATUS);
					break;
				case DECLINED:
					summary.setUserGroupAssociationStatusTypeCode(UserGroupHistorySummary.DECLINED_USER_GROUP_ASSOCIATION_STATUS);
					break;
				case PENDING:
					summary.setUserGroupAssociationStatusTypeCode(UserGroupHistorySummary.PENDING_USER_GROUP_ASSOCIATION_STATUS);
					break;
				}
			}
			historySummaryEntityDAO.saveOrUpdate(summary);
		}

		return summary;
	}

	@Override
	public <T extends AbstractBlockedAssociation> BlockedUserHistorySummary saveBlockedUserHistorySummary(T blockedAssociation) {
		BlockedUserHistorySummary summary = new BlockedUserHistorySummary();

		if (blockedAssociation == null) {
			return null;
		}

		Long dateId = findTimeDimensionId(GregorianCalendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping blockedAssociation summary history for id: " + blockedAssociation.getId());
			return null;
		}

		summary.setDateId(dateId);
		summary.setDeleted(blockedAssociation.getDeleted());

		if (blockedAssociation instanceof BlockedUserCompanyAssociation) {
			BlockedUserCompanyAssociation a = (BlockedUserCompanyAssociation) blockedAssociation;
			User user = a.getBlockedUser().getUser();
			summary.setUserId(user.getId());
			summary.setUserCompanyId(user.getCompany().getId());
			Profile profile = user.getProfile();
			if (profile != null) {
				Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profile.getId());
				if (defaultIndustry != null) {
					summary.setUserIndustryId(defaultIndustry.getId());
				}
			}
			summary.setBlockingCompanyId(a.getBlockingCompany().getCompany().getId());

		} else if (blockedAssociation instanceof BlockedUserUserAssociation) {
			BlockedUserUserAssociation a = (BlockedUserUserAssociation) blockedAssociation;
			User user = a.getBlockedUser().getUser();
			summary.setUserId(user.getId());
			summary.setUserCompanyId(user.getCompany().getId());
			Profile profile = user.getProfile();
			if (profile != null) {
				Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profile.getId());
				summary.setUserIndustryId(defaultIndustry.getId());
			}
			summary.setBlockingCompanyId(a.getUser().getCompany().getId());

		} else {
			return summary;
		}

		historySummaryEntityDAO.saveOrUpdate(summary);
		return summary;
	}

	@Override
	public UserRatingHistorySummary saveUserRatingHistorySummary(Rating rating) {
		Assert.notNull(rating);
		Long dateId = findTimeDimensionId(Calendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping UserRatingHistorySummary");
			return null;
		}

		UserRatingHistorySummary userRatingHistorySummary = new UserRatingHistorySummary();
		userRatingHistorySummary.setDateId(dateId);
		User ratedUser = rating.getRatedUser();
		User raterUser = rating.getRatingUser();
		if (ratedUser != null && raterUser != null) {
			userRatingHistorySummary.setRatedUserId(ratedUser.getId());
			userRatingHistorySummary.setRatedCompanyId(ratedUser.getCompany().getId());

			Long profileId;
			if (ratedUser.getProfile() != null) {
				profileId = ratedUser.getProfile().getId();
			} else {
				profileId = profileService.findProfileDTO(ratedUser.getId()).getProfileId();
			}
			Industry defaultIndustry = industryService.getDefaultIndustryForProfile(profileId);
			if (defaultIndustry != null) {
				userRatingHistorySummary.setRatedIndustryId(defaultIndustry.getId());
			}

			userRatingHistorySummary.setRatingId(rating.getId());
			userRatingHistorySummary.setRatingSharedFlag(rating.isRatingSharedFlag());
			userRatingHistorySummary.setReviewSharedFlag(rating.isReviewSharedFlag());
			userRatingHistorySummary.setRatingValue(rating.getValue());
			userRatingHistorySummary.setBuyerRating(rating.isBuyerRating());

			userRatingHistorySummary.setRaterUserId(raterUser.getId());
			userRatingHistorySummary.setRaterCompanyId(raterUser.getCompany().getId());
			historySummaryEntityDAO.saveOrUpdate(userRatingHistorySummary);
			eventRouter.sendEvent(new UserAverageRatingEvent(ratedUser.getId(), raterUser.getCompany().getId()));
		}

		return userRatingHistorySummary;
	}

	@Override
	public UserRatingChangeLogSummary saveUserRatingChangeLogSummary(Rating rating, Work work) {
		Assert.notNull(rating);
		Long dateId = findTimeDimensionId(Calendar.getInstance());
		if (dateId == null) {
			logger.error("Date Id not found - skipping UserRatingChangeLogSummary");
			return null;
		}

		UserRatingChangeLogSummary userRatingChangeLogSummary = new UserRatingChangeLogSummary();
		userRatingChangeLogSummary.setDateId(dateId);
		User ratedUser = rating.getRatedUser();
		User raterUser = rating.getRatingUser();
		if (ratedUser != null && raterUser != null) {
			userRatingChangeLogSummary.setRatedUserId(ratedUser.getId());
			userRatingChangeLogSummary.setRatedCompanyId(ratedUser.getCompany().getId());
			if (ratedUser.getProfile() != null) {
				Industry defaultIndustry = industryService.getDefaultIndustryForProfile(ratedUser.getProfile().getId());
				if (defaultIndustry != null) {
					userRatingChangeLogSummary.setRatedIndustryId(defaultIndustry.getId());
				}
			}
			userRatingChangeLogSummary.setRatingId(rating.getId());
			userRatingChangeLogSummary.setRatingSharedFlag(rating.isRatingSharedFlag());
			userRatingChangeLogSummary.setReviewSharedFlag(rating.isReviewSharedFlag());
			userRatingChangeLogSummary.setRatingValue(NumberUtilities.defaultValue(rating.getValue()));
			userRatingChangeLogSummary.setQualityValue(NumberUtilities.defaultValue(rating.getQuality()));
			userRatingChangeLogSummary.setProfessionalismValue(NumberUtilities.defaultValue(rating.getProfessionalism()));
			userRatingChangeLogSummary.setCommunicationValue(NumberUtilities.defaultValue(rating.getCommunication()));
			userRatingChangeLogSummary.setReview(rating.getReview());
			userRatingChangeLogSummary.setBuyerRating(rating.isBuyerRating());
			userRatingChangeLogSummary.setWorkId(work.getId());
			userRatingChangeLogSummary.setWorkStatusType(work.getWorkStatusType());

			userRatingChangeLogSummary.setRaterUserId(raterUser.getId());
			userRatingChangeLogSummary.setRaterCompanyId(raterUser.getCompany().getId());
			historySummaryEntityDAO.saveOrUpdate(userRatingChangeLogSummary);
		}

		return userRatingChangeLogSummary;

	}

	private void saveWorkStatusTransition(AbstractWork work, String workStatusTypeCode, Calendar date, Long dateId) {
		Assert.notNull(work);
		Assert.hasText(workStatusTypeCode);
		Assert.notNull(date);

		if (dateId == null) {
			dateId = findTimeDimensionId(date);
			if (dateId == null) {
				logger.error("Date Id not found - skipping WorkStatusTransition summary history for workId: " + work.getId());
				return;
			}
		}

		if (workStatusTypeCode.equals(WorkStatusType.CANCELLED_WITH_PAY) || workStatusTypeCode.equals(WorkStatusType.CANCELLED_PAYMENT_PENDING)) {
			workStatusTypeCode = WorkStatusType.CANCELLED;
		}

		WorkStatusTransition transition = workStatusTransitionDAO.findWorkStatusTransition(work.getId(), workStatusTypeCode);
		if (transition != null) {
			return;
		}

		if (workStatusTypeCode.equals(WorkStatusType.DRAFT) && work.getCompany().getFirstCreatedAssignmentOn() == null) {
			work.getCompany().setFirstCreatedAssignmentOn(date);
		}

		BigDecimal workPrice = getWorkPrice(work, workStatusTypeCode);

		WorkStatusPK pk = new WorkStatusPK(work.getId(), workStatusTypeCode);
		transition = new WorkStatusTransition(pk, dateId);
		transition.setCompanyId(work.getCompany().getId());
		transition.setWorkPrice(workPrice);
		workStatusTransitionDAO.saveOrUpdate(transition);
	}

	@Override
	public List<WorkStatusTransition> findAllTransitionsByWork(Long workId) {
		return workStatusTransitionDAO.findAllTransitionsByWork(workId);
	}

	@Override
	public Integer countWorkStatusTransitions(String workStatusTypeCode, Calendar start, Calendar end) {
		Assert.hasText(workStatusTypeCode);
		Assert.notNull(start);
		Assert.notNull(end);
		return workStatusTransitionDAO.countWorkStatusTransitions(workStatusTypeCode, start, end);
	}

	@Override
	public CompanySummary findCompanySummary(long companyId) {
		return companySummaryDAO.findByCompany(companyId);
	}

	@Override
	public CompanySummary updateCompanySummary(long companyId) {
		CompanySummary companySummary = companySummaryDAO.findByCompany(companyId);
		if (companySummary == null) {
			companySummary = new CompanySummary(companyDAO.findById(companyId));
			companySummary.setModifiedOn(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH));
		}

		DateRange dateRange = new DateRange(companySummary.getModifiedOn(), Calendar.getInstance());
		int totalPaidAssignments = companySummary.getTotalPaidAssignments() + workHistorySummaryDAO.countWork(companyId, WorkStatusType.PAID, dateRange);
		int latePaidAssignments = companySummary.getTotalLatePaidAssignments() + workHistorySummaryDAO.countWorkWithLatePayment(companyId, dateRange);
		int cancelledAssignments = companySummary.getTotalCancelledAssignments() + workHistorySummaryDAO.countWork(companyId, WorkStatusType.CANCELLED, dateRange);
		int createdAssignments = companySummary.getTotalCreatedAssignments() + workHistorySummaryDAO.countWork(companyId, WorkStatusType.DRAFT, dateRange);
		int totalCreatedGroups = companySummary.getTotalCreatedGroups() + userGroupService.countGroupsCreatedSince(companyId, dateRange.getFrom());
		int totalCreatedTests = companySummary.getTotalCreatedAssessments() + assessmentService.countAssessmentsByCompanyCreatedSince(companyId, dateRange.getFrom());

		companySummary.setTotalPaidAssignments(totalPaidAssignments);
		companySummary.setTotalLatePaidAssignments(latePaidAssignments);
		companySummary.setTotalCancelledAssignments(cancelledAssignments);
		companySummary.setTotalCreatedAssignments(createdAssignments);
		companySummary.setTotalCreatedGroups(totalCreatedGroups);
		companySummary.setTotalCreatedAssessments(totalCreatedTests);
		companySummary.setBuyer(createdAssignments > 0 || totalCreatedGroups > 0 || totalCreatedTests > 0);

		companySummaryDAO.saveOrUpdate(companySummary);
		return companySummary;
	}

	@Override
	public CompanySummary updateCompanySummary(CompanySummaryDTO companySummaryDTO) {
		Assert.notNull(companySummaryDTO);
		Assert.notNull(companySummaryDTO.getCompanyId());

		CompanySummary companySummary = companySummaryDAO.findByCompany(companySummaryDTO.getCompanyId());
		if (companySummary == null) {
			companySummary = new CompanySummary(companyDAO.findById(companySummaryDTO.getCompanyId()));
			companySummary.setModifiedOn(DateUtilities.getCalendarFromMillis(DateUtilities.WM_EPOCH));
		}

		companySummary.setTotalPaidAssignments(companySummaryDTO.getTotalPaidAssignments());
		companySummary.setTotalLatePaidAssignments(companySummaryDTO.getTotalLatePaidAssignments());
		companySummary.setTotalCancelledAssignments(companySummaryDTO.getTotalCancelledAssignments());
		companySummary.setTotalCreatedAssignments(companySummaryDTO.getTotalCreatedAssignments());
		companySummary.setTotalCreatedGroups(companySummaryDTO.getTotalCreatedGroups());
		companySummary.setTotalCreatedAssessments(companySummaryDTO.getTotalCreatedAssessments());
		companySummary.setBuyer(companySummaryDTO.isBuyer());

		companySummaryDAO.saveOrUpdate(companySummary);
		return companySummary;
	}

	private UserGroupSummary createGroupSummaryWithGroup(Long groupId) {
		Assert.notNull(groupId);
		UserGroupSummary ugs = new UserGroupSummary();
		UserGroup ug = userGroupService.findGroupById(groupId);

		ugs.setUserGroup(ug);

		return ugs;
	}

	void updateUserGroupSummary(UserGroupThroughputDTO userGroupThroughputDTO) {
		Assert.notNull(userGroupThroughputDTO);
		Assert.notNull(userGroupThroughputDTO.getUserGroupId());
		Long groupId = userGroupThroughputDTO.getUserGroupId();

		if (userGroupThroughputDTO.getThroughput() != null && userGroupThroughputDTO.getThroughput().compareTo(BigDecimal.ZERO) != 0) {
			Optional<UserGroupSummary> optionalUserGroupSummary = Optional.fromNullable(
				userGroupService.findUserGroupSummaryByUserGroup(groupId)
			);
			UserGroupSummary userGroupSummary = optionalUserGroupSummary.or(createGroupSummaryWithGroup(groupId));

			userGroupSummary.setTotalThroughput(userGroupSummary.getTotalThroughput().add(userGroupThroughputDTO.getThroughput()));
			userGroupSummary.setLastUpdateOn(Calendar.getInstance());

			userGroupSummaryDAO.saveOrUpdate(userGroupSummary);
		}
	}

	void updateUserGroupSummary(UserGroupLastRoutedDTO userGroupLastRoutedDTO) {
		Assert.notNull(userGroupLastRoutedDTO);

		UserGroupSummary userGroupSummary = userGroupService.findUserGroupSummaryByUserGroup(userGroupLastRoutedDTO.getUserGroupId());

		if (userGroupLastRoutedDTO.getLastRoutedOn() != null) {
			userGroupSummary.setLastRoutedOn(userGroupLastRoutedDTO.getLastRoutedOn());
			userGroupSummary.setLastUpdateOn(Calendar.getInstance());
			userGroupSummaryDAO.saveOrUpdate(userGroupSummary);
		}
	}

	void updateUserGroupSummary(UserGroupThroughputDTO userGroupThroughputDTO, UserGroupLastRoutedDTO userGroupLastRoutedDTO) {
		Assert.notNull(userGroupThroughputDTO);
		Assert.notNull(userGroupLastRoutedDTO);

		UserGroupSummary userGroupSummary = userGroupService.findUserGroupSummaryByUserGroup(userGroupLastRoutedDTO.getUserGroupId());

		if (userGroupLastRoutedDTO.getLastRoutedOn() != null) {
			userGroupSummary.setLastRoutedOn(userGroupLastRoutedDTO.getLastRoutedOn());
			userGroupSummary.setLastUpdateOn(Calendar.getInstance());
		}

		if (userGroupThroughputDTO.getThroughput() != null) {
			userGroupSummary.setTotalThroughput(userGroupSummary.getTotalThroughput().add(userGroupThroughputDTO.getThroughput()));
		}

		userGroupSummaryDAO.saveOrUpdate(userGroupSummary);
	}

	@Override
	public List<Long> updateUserGroupSummary() {
		// Fetch latest work price sums and last routed timestamp
		List<UserGroupThroughputDTO> userGroupThroughputDTOs = userGroupService.calculateThroughputSinceLastUpdate();
		List<UserGroupLastRoutedDTO> userGroupLastRoutedDTOs = userGroupService.findAllWithNewLastRoutedSinceLastUpdate();

		// Update groups with only work prices sums
		List<UserGroupThroughputDTO> userGroupThroughputsOnly = Lists.newArrayList();
		List<UserGroupThroughputDTO> userGroupThroughputWithRouted = Lists.newArrayList();
		List<UserGroupLastRoutedDTO> userGroupLastRoutedWithThroughput = Lists.newArrayList();
		List<UserGroupLastRoutedDTO> userGroupLastRoutedOnly; // Calculated

		for (UserGroupThroughputDTO ugt : userGroupThroughputDTOs) {
			UserGroupLastRoutedDTO elem = CollectionUtilities.findFirst(userGroupLastRoutedDTOs, "userGroupId", ugt.getUserGroupId());

			if (elem == null) {
				userGroupThroughputsOnly.add(ugt);
			} else {
				userGroupThroughputWithRouted.add(ugt);
				userGroupLastRoutedWithThroughput.add(elem);
			}
		}
		userGroupLastRoutedOnly = (List) CollectionUtils.subtract(userGroupLastRoutedDTOs, userGroupLastRoutedWithThroughput);


		// Update groups with only new throughput values
		for (UserGroupThroughputDTO ug : userGroupThroughputsOnly) {
			updateUserGroupSummary(ug);
		}

		// Update groups with only last routed timestamps
		for (UserGroupLastRoutedDTO ug : userGroupLastRoutedOnly) {
			updateUserGroupSummary(ug);
		}

		// Update groups with both new work prices sums and last routed
		for (int i = 0; i < userGroupThroughputWithRouted.size(); i++) {
			updateUserGroupSummary(userGroupThroughputWithRouted.get(i), userGroupLastRoutedWithThroughput.get(i));
		}

		// Find all groups with updated summaries
		return (List) CollectionUtils.union(CollectionUtilities.newListPropertyProjection(userGroupLastRoutedDTOs, "userGroupId"),
				CollectionUtilities.newListPropertyProjection(userGroupThroughputDTOs, "userGroupId"));
	}

	@Override
	public WorkStatusTransitionHistorySummary saveWorkStatusTransitionHistorySummary(Long workId, WorkStatusType fromWorkStatusTypeCode, WorkStatusType toWorkStatusTypeCode, int timeInSeconds, Calendar date) {
		Work work = workDAO.findWorkById(workId);
		return saveWorkStatusTransitionHistorySummary(work, fromWorkStatusTypeCode, toWorkStatusTypeCode, timeInSeconds, date);
	}

	@Override
	public WorkStatusTransitionHistorySummary saveWorkStatusTransitionHistorySummary(Work work, WorkStatusType fromWorkStatusTypeCode, WorkStatusType toWorkStatusTypeCode, int timeInSeconds) {
		return saveWorkStatusTransitionHistorySummary(work, fromWorkStatusTypeCode, toWorkStatusTypeCode, timeInSeconds, Calendar.getInstance());
	}

	@Override
	public List<UserSummary> findAllUsersWithLastAssignedDateBetweenDates(Calendar fromDate, Calendar throughDate) {
		Assert.notNull(fromDate);
		Assert.notNull(throughDate);
		return userSummaryDAO.findAllUsersWithLastAssignedDateBetweenDates(fromDate, throughDate);
	}

	@Override
	public int countGccBankAccountsSinceRelease() {
		return bankAccountDAO.countGccBankAccounts(GCC_RELEASE_DATE);
	}

	WorkStatusTransitionHistorySummary saveWorkStatusTransitionHistorySummary(Work work, WorkStatusType fromWorkStatusTypeCode, WorkStatusType toWorkStatusTypeCode, int timeInSeconds, Calendar date) {
		Assert.notNull(toWorkStatusTypeCode);
		Assert.notNull(work);
		Assert.notNull(timeInSeconds);

		Long dateId = findTimeDimensionId(date);
		if (dateId == null) {
			logger.error("Date Id not found - skipping WorkStatusTransition summary history for workId: " + work.getId());
			return null;
		}

		WorkStatusTransitionHistorySummary workStatusTransition = new WorkStatusTransitionHistorySummary(work, dateId);
		workStatusTransition.setToWorkStatusTypeCode(toWorkStatusTypeCode.getCode());
		workStatusTransition.setTransitionTimeInSeconds(timeInSeconds);
		if (fromWorkStatusTypeCode != null) {
			workStatusTransition.setFromWorkStatusTypeCode(fromWorkStatusTypeCode.getCode());
		}
		historySummaryEntityDAO.saveOrUpdate(workStatusTransition);

		if (toWorkStatusTypeCode.isSent()) {
			//This is to capture time to start time from the date it was sent.
	   		saveSentToStartWorkTransitionHistorySummary(work, dateId, date);
		}
		if (toWorkStatusTypeCode.isComplete()) {
			saveStartToCompleteWorkTransitionHistorySummary(work, dateId, date);
		}
		saveWorkStatusTransition(work, toWorkStatusTypeCode.getCode(), Calendar.getInstance(), dateId);
		return workStatusTransition;

	}

	private void saveSentToStartWorkTransitionHistorySummary(Work work, long dateId, Calendar date) {
		WorkStatusTransitionHistorySummary sentToStartDate = new WorkStatusTransitionHistorySummary(work, dateId);
		sentToStartDate.setFromWorkStatusTypeCode(WorkStatusType.SENT);
		sentToStartDate.setToWorkStatusTypeCode(WorkStatusType.START_TIME);
		Calendar maxStartDate = firstNonNull(work.getScheduleThrough(), work.getScheduleFrom());
		int timeToStartTime = 0;
		if (DateUtilities.isInFuture(maxStartDate)) {
			timeToStartTime = DateUtilities.getSecondsBetween(date, maxStartDate);
		}
		sentToStartDate.setTransitionTimeInSeconds(timeToStartTime);
		sentToStartDate.setShowInWorkFeed(work.isShownInFeed());
		historySummaryEntityDAO.saveOrUpdate(sentToStartDate);
	}

	private void saveStartToCompleteWorkTransitionHistorySummary(Work work, long dateId, Calendar date) {
		WorkStatusTransitionHistorySummary sentToStartDate = new WorkStatusTransitionHistorySummary(work, dateId);
		sentToStartDate.setFromWorkStatusTypeCode(WorkStatusType.START_TIME);
		sentToStartDate.setToWorkStatusTypeCode(WorkStatusType.COMPLETE);
		Calendar maxStartDate = (Calendar)firstNonNull(work.getScheduleThrough(), work.getScheduleFrom()).clone();
		int timeToStartTime = 0;
		if (DateUtilities.isInPast(maxStartDate)) {
			timeToStartTime = DateUtilities.getSecondsBetween(maxStartDate, date);
		}
		sentToStartDate.setTransitionTimeInSeconds(timeToStartTime);
		historySummaryEntityDAO.saveOrUpdate(sentToStartDate);
	}

	private BigDecimal getWorkPrice(AbstractWork work, String workStatusTypeCode) {
		BigDecimal workPrice = BigDecimal.ZERO;
	   	boolean hasWorkStatusPriorComplete = WORK_STATUS_TYPE_PRIOR_COMPLETE.contains(workStatusTypeCode);


		if (hasWorkStatusPriorComplete) {
			if (work.getPricingStrategy() != null) {
				workPrice = pricingService.calculateMaximumResourceCost(work.getPricingStrategy());
				logger.debug("work id: " + work.getId() + " " + work.getPricingStrategy());
			}
		}

		/**
		 * In cases like the ACTIVE status, the pricing strategy may be 0 but the fulfillment strategy
		 * could be populated already. Therefore the double check.
		 *
		 * We want to avoid having ZERO in the summary tables, since this affects the calculations
		 * for the average work price in the daily summary.
		 */

		if (!hasWorkStatusPriorComplete || workPrice.compareTo(BigDecimal.ZERO) == 0) {
			if (work.getFulfillmentStrategy() != null) {
				if (work.getFulfillmentStrategy().getWorkPrice() != null) {
					workPrice = work.getFulfillmentStrategy().getWorkPrice().abs();
				} else {
					workPrice = work.getFulfillmentStrategy().getWorkPricePriorComplete();
				}
			}
		}
		return workPrice;
	}
}
