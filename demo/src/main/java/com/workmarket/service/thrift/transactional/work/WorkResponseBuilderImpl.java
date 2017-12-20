package com.workmarket.service.thrift.transactional.work;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.dao.asset.CompanyAssetAssociationDAO;
import com.workmarket.dao.asset.UserAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.dao.asset.WorkAssetVisibilityDAO;
import com.workmarket.dao.customfield.WorkCustomFieldGroupAssociationDAO;
import com.workmarket.dao.lane.LaneAssociationDAO;
import com.workmarket.dao.rating.RatingDAO;
import com.workmarket.data.solr.model.GeoPoint;
import com.workmarket.domains.groups.dao.WorkGroupAssociationDAO;
import com.workmarket.domains.groups.model.WorkGroupAssociation;
import com.workmarket.domains.model.*;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.*;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.model.changelog.work.*;
import com.workmarket.domains.model.crm.ClientContact;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldType;
import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.domains.model.note.NotePagination;
import com.workmarket.domains.model.note.WorkNote;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.model.pricing.PricingStrategyUtilities;
import com.workmarket.domains.work.dao.WorkNegotiationDAO;
import com.workmarket.domains.work.dao.WorkQuestionAnswerPairDAO;
import com.workmarket.domains.work.dao.WorkResourceLabelDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.WorkResourceLabel;
import com.workmarket.domains.work.model.WorkResourceTimeTracking;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.model.negotiation.*;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeCompanySetting;
import com.workmarket.domains.work.service.*;
import com.workmarket.domains.work.service.follow.WorkFollowService;
import com.workmarket.domains.work.service.part.PartService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.route.RoutingStrategyService;
import com.workmarket.domains.work.service.state.WorkSubStatusService;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.pay.PaymentSummaryService;
import com.workmarket.service.business.template.TemplateService;
import com.workmarket.service.option.OptionsService;
import com.workmarket.service.thrift.work.WorkResponseAuthorization;
import com.workmarket.service.thrift.work.WorkResponseContextBuilder;
import com.workmarket.service.EntityToObjectFactory;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.AssessmentAttemptPair;
import com.workmarket.thrift.assessment.Attempt;
import com.workmarket.thrift.core.Address;
import com.workmarket.thrift.core.Company;
import com.workmarket.thrift.core.*;
import com.workmarket.thrift.core.Industry;
import com.workmarket.thrift.core.Location;
import com.workmarket.thrift.core.Profile;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.*;
import com.workmarket.thrift.work.ManageMyWorkMarket;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Scope(value = "prototype")
public class WorkResponseBuilderImpl implements WorkResponseBuilder {

	private static final Log logger = LogFactory.getLog(WorkResponseBuilderImpl.class);

	@Autowired private TemplateService templateService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private PaymentSummaryService paymentSummaryService;
	@Autowired private WorkService workService;
	@Autowired private WorkFollowService workFollowService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private LaneAssociationDAO laneAssociationDAO;
	@Autowired private WorkNoteService workNoteService;
	@Autowired private RatingDAO ratingDAO;
	@Autowired private WorkGroupAssociationDAO groupDAO;
	@Autowired private WorkChangeLogService workChangeLogService;
	@Autowired private WorkNegotiationDAO workNegotiationDAO;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private WorkQuestionAnswerPairDAO workQuestionAnswerPairDAO;
	@Autowired private WorkCustomFieldGroupAssociationDAO workCustomFieldGroupAssociationDAO;
	@Autowired private WorkMilestonesService workMilestonesService;
	@Autowired private UserAssetAssociationDAO userAssetAssociationDAO;
	@Autowired private CompanyAssetAssociationDAO companyAssetAssociationDAO;
	@Autowired private WorkSubStatusService workSubStatusService;
	@Autowired private WorkResourceLabelDAO workResourceLabelDAO;
	@Autowired private EntityToObjectFactory objectFactory;
	@Autowired private UserService userService;
	@Autowired private RoutingStrategyService routingStrategyService;
	@Autowired private WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired private WorkAssetVisibilityDAO workAssetVisibilityDAO;
	@Autowired private DocumentService documentService;
	@Autowired private PartService partService;
	@Autowired private ProjectService projectService;
	@Autowired private AddressService addressService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkResponseContextBuilder workResponseContextBuilder;
	@Autowired private CRMService crmService;
	@Autowired private UserRoleService userRoleService;
	@Autowired private CompanyService companyService;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;

	@Override
	public WorkResponse buildWorkResponse(AbstractWork work, com.workmarket.domains.model.User currentUser, Set<WorkRequestInfo> includes) throws Exception {
		WorkResponse response = new WorkResponse();
		logger.debug("WorkResponseBuilderImpl.buildWorkResponse -- start");

		buildWork(response, work);
		buildWorkRequestInfoResponse(work, currentUser, response, includes);
		buildWorkMilestones(response, work);

		return response;
	}

	@Override
	public WorkResponse buildWorkDetailResponseLight(long workId, Long userId, Set<WorkRequestInfo> includes) throws Exception {
		Assert.notNull(userId);

		AbstractWork work = workService.findWork(workId, false);
		Assert.notNull(work);
		com.workmarket.domains.model.User currentUser = userService.getUser(userId);
		Assert.notNull(currentUser);

		WorkResponse response = new WorkResponse(new Work());
		buildWorkRequestInfoResponse(work, currentUser, response, WorkRequestInfo.getWorkDetailsEnumSet());
		buildWorkRequestInfoResponse(work, currentUser, response, includes);

		WorkResponseAuthorization workResponseAuthorization =
			new WorkResponseAuthorization(response.getAuthorizationContexts(), currentUser, userRoleService.isInternalUser(currentUser));

		if (workResponseAuthorization.isNonActiveResource()) {
			buildAssets(response, work);
			buildViewingResource(response, currentUser, work);
			buildPendingNegotiation(response, currentUser, work);
		}

		if (workResponseAuthorization.isResourceOrAdmin()) {
			buildCustomFields(response, work, true);
		}

		if (workResponseAuthorization.isActiveResourceOrAdmin()) {

			buildWorkRequestInfoResponse(work, currentUser, response, WorkRequestInfo.getAdminUserInfoEnumSet());
			buildWorkNegotiations(work, response);

			if (!(work.isDraft() || work.isSent() || work.isActive() || work.isDeclined())) {
				buildWorkMilestones(response, work);
			}

			if (work.isActive() || work.isComplete() || work.isPaid() || work.isCancelled() || work.isPaymentPending()) {
				buildRatingsForWork(response, currentUser, work);
			}
		}

		if (workResponseAuthorization.isAdmin()) {
			addDataForAdminToResponse(work, response);
		}

		return response;
	}

	private void buildWorkRequestInfoResponse(AbstractWork work, com.workmarket.domains.model.User currentUser, WorkResponse response, Set<WorkRequestInfo> includes) throws Exception {
		if (includes == null) {
			return;
		}
		for (WorkRequestInfo include : includes) {
			include.buildPartOf(this, response, work, currentUser);
		}
	}

	private void addDataForAdminToResponse(AbstractWork work, WorkResponse response) throws Exception {
		buildRoutingStrategies(response, work);
		buildPricingHistory(response, work);
		buildProject(response, work);

		if (work.isSent()) {
			buildNotification(response, work);
			buildPendingNegotiationsForResources(response, work);
		}

		if (work.isInvoiced()) {
			buildInvoice(response, work);
		}
	}

	private WorkResponse buildWorkDetailResponse(AbstractWork work, com.workmarket.domains.model.User currentUser) throws Exception {
		WorkResponse response = new WorkResponse();

		buildWork(response, work);
		buildWorkRequestInfoResponse(work, currentUser, response, WorkRequestInfo.getWorkDetailInfoEnumSet());
		buildWorkMilestones(response, work);

		// Resources

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.RESOURCE) && !CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE)) {
			buildAssets(response, work);
			buildViewingResource(response, currentUser, work);
			buildPendingNegotiation(response, currentUser, work);
		}

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN, AuthorizationContext.ACTIVE_RESOURCE, AuthorizationContext.RESOURCE)
			|| userRoleService.isInternalUser(currentUser)) {
			buildCustomFields(response, work, true);
		}

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN, AuthorizationContext.ACTIVE_RESOURCE)
			|| userRoleService.isInternalUser(currentUser)) {
			buildWorkRequestInfoResponse(work, currentUser, response, WorkRequestInfo.getAdminUserInfoEnumSet());
			buildWorkNegotiations(work, response);
		}

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)
			|| userRoleService.isInternalUser(currentUser)) {
			buildRoutingStrategies(response, work);
			buildChangeLog(response, work);
			buildPricingHistory(response, work);

			if (work.isSent()) {
				buildNotification(response, work);
				buildPendingNegotiationsForResources(response, work);
			}

			if (work.isInvoiced()) {
				buildInvoice(response, work);
			}
		}

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN, AuthorizationContext.ACTIVE_RESOURCE)
			&& (work.isActive() || work.isComplete() || work.isPaid() || work.isCancelled() || work.isPaymentPending())) {
			buildRatingsForWork(response, currentUser, work);
		}
		return response;
	}

	private void buildWorkNegotiations(AbstractWork work, WorkResponse response) throws Exception {
		if (work.isActive()) {
			buildActiveWorkNegotiations(work, response);
		} else if (work.isComplete()) {
			buildCompletedWorkNegotiations(work, response);
		}
	}

	private void buildActiveWorkNegotiations(AbstractWork work, WorkResponse response) throws Exception {
		buildPendingRescheduleNegotiationForBuyer(response, work);
		buildPendingRescheduleNegotiationForActiveResource(response, work);
		buildExpenseNegotiationForActiveResource(response, work);
		buildBudgetNegotiationForActiveResource(response, work);
		buildBonusForActiveResource(response, work);
	}

	private void buildCompletedWorkNegotiations(AbstractWork work, WorkResponse response) throws Exception {
		buildExpenseNegotiationForActiveResource(response, work);
		buildBudgetNegotiationForActiveResource(response, work);
		buildBonusForActiveResource(response, work);
	}

	@Override
	public WorkResponse buildWorkDetailResponse(long workId, long userId) throws Exception {
		AbstractWork work = workService.findWork(workId, false);
		Assert.notNull(work);
		com.workmarket.domains.model.User user = userService.getUser(userId);
		Assert.notNull(user);
		return buildWorkDetailResponse(work, user);
	}

	@Override
	public WorkResponse buildWorkResponse(long workId, long userId, Set<WorkRequestInfo> includes) throws Exception {
		AbstractWork work = workService.findWork(workId, false);
		Assert.notNull(work);
		com.workmarket.domains.model.User user = userService.getUser(userId);
		Assert.notNull(user);
		return buildWorkResponse(work, user, includes);
	}

	void buildContext(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) {
		workResponseContextBuilder.buildContext(response, currentUser, work);
	}

	void buildWorkMilestones(WorkResponse response, AbstractWork abstractWork) {
		if (!(abstractWork instanceof com.workmarket.domains.work.model.Work)) {
			return;
		}
		com.workmarket.domains.work.model.Work work = (com.workmarket.domains.work.model.Work) abstractWork;
		com.workmarket.domains.model.summary.work.WorkMilestones wm = workMilestonesService.findWorkMilestonesByWorkId(work.getId());
		com.workmarket.thrift.work.WorkMilestones workMilestones = new com.workmarket.thrift.work.WorkMilestones();
		if (wm != null) {
			if (wm.getAcceptedOn() != null) {
				workMilestones.setAcceptedOn(wm.getAcceptedOn().getTimeInMillis());
			}
			if (wm.getActiveOn() != null) {
				workMilestones.setActiveOn(wm.getActiveOn().getTimeInMillis());
			}
			if (wm.getCancelledOn() != null) {
				workMilestones.setCancelledOn(wm.getCancelledOn().getTimeInMillis());
			}
			if (wm.getClosedOn() != null) {
				workMilestones.setClosedOn(wm.getClosedOn().getTimeInMillis());
			}
			if (wm.getCompleteOn() != null) {
				workMilestones.setCompleteOn(wm.getCompleteOn().getTimeInMillis());
			}
			if (wm.getCreatedOn() != null) {
				workMilestones.setCreatedOn(wm.getCreatedOn().getTimeInMillis());
			}
			if (wm.getDeclinedOn() != null) {
				workMilestones.setDeclinedOn(wm.getDeclinedOn().getTimeInMillis());
			}
			if (wm.getDraftOn() != null) {
				workMilestones.setDraftOn(wm.getDraftOn().getTimeInMillis());
			}
			if (wm.getPaidOn() != null) {
				workMilestones.setPaidOn(wm.getPaidOn().getTimeInMillis());
			}
			if (wm.getRefundedOn() != null) {
				workMilestones.setRefundedOn(wm.getRefundedOn().getTimeInMillis());
			}
			if (wm.getSentOn() != null) {
				workMilestones.setSentOn(wm.getSentOn().getTimeInMillis());
			}
			if (wm.getVoidOn() != null) {
				workMilestones.setVoidOn(wm.getVoidOn().getTimeInMillis());
			}
			if (work.getDueOn() != null) {
				workMilestones.setDueOn(work.getDueOn().getTimeInMillis());
			}
		}

		response.setWorkMilestones(workMilestones);
	}

	@SuppressWarnings("unchecked")
	void buildWork(WorkResponse response, AbstractWork work) {

		response.setWork(new Work());
		buildBaseWork(response, work);
		response.getWork().setPricingEditable(work.isPricingEditable());

		if (work.getRequirementSets() != null) {
			response.getWork().setRequirementSetIds(
				(List<Long>) CollectionUtils.collect(
					work.getRequirementSets(),
					new BeanToPropertyValueTransformer("id")));
		}

		// Avoid errors when trying to serialize proxy objects
		if (work.getAccountPricingType() != null) {
			AccountPricingType accountPricingType = new AccountPricingType(work.getAccountPricingType().getCode());
			accountPricingType.setDescription(work.getAccountPricingType().getDescription());
			response.getWork().setAccountPricingType(accountPricingType);
		}

		if (work instanceof com.workmarket.domains.work.model.WorkTemplate) {
			response.getWork().setTemplate(
				new Template()
					.setName(((com.workmarket.domains.work.model.WorkTemplate) work).getTemplateName())
					.setDescription(((com.workmarket.domains.work.model.WorkTemplate) work).getTemplateDescription())
			);
		} else if (work.isTemplateSet()) {
			response.getWork().setTemplate(new Template().setId(work.getTemplate().getId()));
		}
	}

	void buildBaseWork(WorkResponse response, AbstractWork work) {
		response.getWork()
			.setId(work.getId())
			.setWorkNumber(work.getWorkNumber())
			.setTitle(work.getTitle())
			.setDescription(work.getDescription())
			.setInstructions(work.getInstructions())
			.setDesiredSkills(work.getDesiredSkills())
			.setShortUrl(work.getShortUrl())
			.setTimeZone(work.getTimeZone().getTimeZoneId())
			.setTimeZoneId(work.getTimeZone().getId())
			.setCheckinCallRequired(work.isCheckinCallRequired())
			.setCheckinContactName(work.getCheckinContactName())
			.setCheckinContactPhone(work.getCheckinContactPhone())
			.setShowCheckoutNotesFlag(work.isShowCheckoutNote())
			.setCheckoutNoteRequiredFlag(work.isCheckoutNoteRequired())
			.setCheckoutNoteInstructions(work.getCheckoutNoteInstructions())
			.setAccountPricingType(work.getAccountPricingType())
			.setShowInFeed(work.isShownInFeed())
			.setPrivateInstructions(work.isPrivateInstructions());

		if (work instanceof WorkBundle) {
			response.setWorkBundle(true);
			response.setWorkBundleParent(WorkBundleDTO.fromWorkBundle((WorkBundle) work));
		} else if (work instanceof com.workmarket.domains.work.model.Work) {
			com.workmarket.domains.work.model.Work workModel = (com.workmarket.domains.work.model.Work) work;
			if (workModel.getWorkUniqueId() != null) {
				response.getWork().setUniqueExternalIdValue(workModel.getWorkUniqueId().getIdValue());
				response.getWork().setUniqueExternalIdDisplayName(workModel.getWorkUniqueId().getDisplayName());
			}
			if (((com.workmarket.domains.work.model.Work) work).getParent() != null) {
				response.setInWorkBundle(true);
				response.setWorkBundleParent(workBundleService.findByChild(work.getId()));
			}
		}
		response.getWork().setDocumentsEnabled(workOptionsService.hasOption(work, WorkOption.DOCUMENTS_ENABLED, "true"));
	}

	void buildStatus(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) {
		boolean isPendingFulfillment = false;
		boolean isAdmin = true;
		if (response.isSetAuthorizationContexts()) {
			isAdmin = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)
				|| userRoleService.isInternalUser(currentUser);
		}
		response.getWork().setStatus(objectFactory.newStatus(work.getWorkStatusType()));

		if (work.isPaymentPending()) {
			isPendingFulfillment = workService.isWorkPendingFulfillment(work.getId());
		}
		response.getWork().setPendingPaymentFulfillment(isPendingFulfillment);

		//Map of the transitionNotes
		Map<String, WorkNote> transitionNotes = Maps.newHashMapWithExpectedSize(work.getUnResolvedWorkSubStatusTypeAssociations().size());
		for (WorkSubStatusTypeAssociation a : work.getUnResolvedWorkSubStatusTypeAssociations()) {
			if (a.getTransitionNote() != null) {
				transitionNotes.put(a.getWorkSubStatusType().getCode(), a.getTransitionNote());
			}
		}

		if (isNotEmpty(work.getUnResolvedWorkSubStatusTypeAssociations())) {
			List<WorkSubStatusType> unresolvedSubStatus = workSubStatusService.findAllUnresolvedSubStatusWithColor(work.getId());

			for (WorkSubStatusType w : unresolvedSubStatus) {
				// Resources should only see sub-statuses that are visible to them
				if (!w.getResourceVisible() && !isAdmin) {
					continue;
				}

				SubStatus substatus = objectFactory.newSubStatus(w);
				if (transitionNotes.containsKey(w.getCode())) {
					substatus.setNote(transitionNotes.get(w.getCode()).getContent());
				}
				response.getWork().addToSubStatuses(substatus);
			}
		}

		response.getWork().setInProgress(work.isActive() && workService.isWorkInProgress(work.getId()));
	}

	void buildIndustry(WorkResponse response, AbstractWork work) {
		if (work.getIndustry() == null) {
			return;
		}

		response.getWork().setIndustry(
			new Industry()
				.setId(work.getIndustry().getId())
				.setName(work.getIndustry().getName())
		);
	}

	void buildCompany(WorkResponse response, AbstractWork work) {
		Company tcompany = new Company();
		tcompany.setId(work.getCompany().getId());
		List<String> companyNumbers = companyService.findCompanyNumbersFromCompanyIds(Lists.newArrayList(work.getCompany().getId()));
		if (companyNumbers.size() > 0) {
			tcompany.setCompanyNumber(companyNumbers.get(0));
		}
		tcompany.setCompanyUuid(work.getCompany().getUuid());
		tcompany.setName(work.getCompany().getEffectiveName());
		tcompany.setCustomSignatureLine(work.getCompany().getCustomSignatureLine());
		CompanyAssetAssociation companyAvatars = companyAssetAssociationDAO.findCompanyAvatars(work.getCompany().getId());
		if (companyAvatars != null) {
			Asset avatarLarge = companyAvatars.getTransformedLargeAsset();

			if (avatarLarge != null) {
				tcompany.setAvatarLarge(objectFactory.newAsset(avatarLarge));
			}
		}

		if (work.getCompany().getManageMyWorkMarket().getBusinessYears() != null) {
			tcompany.setYearsInBusiness(work.getCompany().getManageMyWorkMarket().getBusinessYears());
		}
		if (work.getCompany().getEmployees() != null) {
			tcompany.setNumberOfEmployees(work.getCompany().getEmployees());
		}
		tcompany.setCreatedOn(work.getCompany().getCreatedOn().getTimeInMillis());

		response.getWork().setCompany(tcompany);
	}

	void buildProject(WorkResponse response, AbstractWork work) {
		com.workmarket.domains.work.model.project.Project project = projectService.findByWorkId(work.getId());
		if (project != null) {
			response.getWork().setProject(
				new Project(project.getId(), project.getName(), project.getDescription())
			);
		}
	}

	void buildBuyer(WorkResponse response, AbstractWork work) {
		User tbuyer = objectFactory.newUser(work.getBuyer());

		if (work.getBuyer().getCompany() != null) {
			Company buyerCompany = new Company();
			buyerCompany.setId(work.getBuyer().getCompany().getId());
			tbuyer.setCompany(buyerCompany);
		}

		tbuyer.setRatingSummary(
			new RatingSummary()
				.setRating(ratingDAO.findSatisfactionRateForUser(work.getBuyer().getId()).shortValue())
				.setNumberOfRatings(ratingDAO.countAllUserRatings(work.getBuyer().getId()))
		);

		Profile tprofile = new Profile();

		if (work.getBuyer().getProfile().getWorkPhone() != null) {
			tprofile.addToPhoneNumbers(
				new Phone()
					.setPhone(work.getBuyer().getProfile().getWorkPhone())
					.setExtension(work.getBuyer().getProfile().getWorkPhoneExtension())
					.setType(ContactContextType.WORK.name())
			);
		}

		if (work.getBuyer().getProfile().getMobilePhone() != null) {
			tprofile.addToPhoneNumbers(
				new Phone()
					.setPhone(work.getBuyer().getProfile().getMobilePhone())
					.setType(ContactContextType.OTHER.name())
			);
		}

		tbuyer.setProfile(tprofile);
		response.getWork().setBuyer(tbuyer);
	}

	void buildLocationContact(WorkResponse response, AbstractWork work) {
		if (!work.hasServiceContact()) {
			return;
		}

		if (work.getServiceClientContactId() != null) {
			ClientContact contact = crmService.findClientContactById(work.getServiceClientContactId());
			Profile tprofile = new Profile();
			if (contact.getPhoneAssociations() != null) {
				addPhoneNumbersToProfile(contact, tprofile, ContactContextType.WORK);
				addPhoneNumbersToProfile(contact, tprofile, ContactContextType.OTHER);
			}

			User tuser = new User()
				.setId(contact.getId())
				.setName(new Name(contact.getFirstName(), contact.getLastName()))
				.setProfile(tprofile);

			if (!contact.getEmails().isEmpty()) {
				tuser.setEmail(contact.getMostRecentEmail().getEmail());
			}

			response.getWork().setLocationContact(tuser);

		}

		buildSecondaryLocationContact(response, work);
	}

	void buildSecondaryLocationContact(WorkResponse response, AbstractWork work) {
		if (work.getSecondaryServiceClientContactId() == null) {
			return;
		}
		ClientContact contact = crmService.findClientContactById(work.getSecondaryServiceClientContactId());

		Profile tprofile = new Profile();
		if (contact.getPhoneAssociations() != null) {
			addPhoneNumbersToProfile(contact, tprofile, ContactContextType.WORK);
			addPhoneNumbersToProfile(contact, tprofile, ContactContextType.OTHER);
		}

		User tuser = new User()
			.setId(contact.getId())
			.setName(new Name(contact.getFirstName(), contact.getLastName()))
			.setProfile(tprofile);

		if (!contact.getEmails().isEmpty()) {
			tuser.setEmail(contact.getMostRecentEmail().getEmail());
		}

		response.getWork().setSecondaryLocationContact(tuser);
	}

	private void addPhoneNumbersToProfile(ClientContact contact, Profile tprofile, ContactContextType type) {
		for (com.workmarket.domains.model.directory.Phone p : contact.getPhoneNumbers(type)) {
			tprofile.addToPhoneNumbers(
				new Phone()
					.setPhone(p.getPhone())
					.setExtension(p.getExtension())
					.setType(type.name())
			);
		}
	}

	void buildSupportContact(WorkResponse response, AbstractWork work) {
		if (work.getBuyerSupportUser() == null)
			return;

		Profile tprofile = new Profile();
		if (work.getBuyerSupportUser().getProfile().getWorkPhone() != null) {
			tprofile.addToPhoneNumbers(
				new Phone()
					.setPhone(work.getBuyerSupportUser().getProfile().getWorkPhone())
					.setExtension(work.getBuyerSupportUser().getProfile().getWorkPhoneExtension())
					.setType(ContactContextType.WORK.name())
			);
		}
		if (work.getBuyerSupportUser().getProfile().getMobilePhone() != null) {
			tprofile.addToPhoneNumbers(
				new Phone()
					.setPhone(work.getBuyerSupportUser().getProfile().getMobilePhone())
					.setType(ContactContextType.OTHER.name())
			);
		}

		response.getWork().setSupportContact(
			new User()
				.setId(work.getBuyerSupportUser().getId())
				.setUserNumber(work.getBuyerSupportUser().getUserNumber())
				.setName(new Name(work.getBuyerSupportUser().getFirstName(), work.getBuyerSupportUser().getLastName()))
				.setEmail(work.getBuyerSupportUser().getEmail())
				.setProfile(tprofile)
		);
	}

	void buildViewingResource(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) {
		com.workmarket.domains.model.WorkResource resource = workService.findWorkResource(currentUser.getId(), work.getId());

		if (resource == null) {
			return;
		}

		Profile tprofile = new Profile();
		tprofile.addToPhoneNumbers(
			new Phone()
				.setPhone(resource.getUser().getProfile().getWorkPhone())
				.setExtension(resource.getUser().getProfile().getWorkPhoneExtension())
		);

		// Fallback to company address if a personal address is not set
		Optional<Address> address = getAddress(resource);
		if (address.isPresent()) {
			tprofile.setAddress(address.get());
		}

		User tuser = objectFactory.newUser(resource.getUser());
		tuser.setProfile(tprofile);

		com.workmarket.domains.model.lane.LaneAssociation lane = laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(resource.getUser().getId(), work.getCompany().getId());
		if (lane != null) {
			tuser.setLaneType(lane.getLaneType());
		}

		Resource tresource = new Resource()
			.setId(resource.getId())
			.setStatus(objectFactory.newStatus(resource.getWorkResourceStatusType()))
			.setUser(tuser);

		Double distance = workService.calculateDistanceToWork(resource.getUser().getId(), work);
		if (distance != null) {
			tresource.setDistanceToAssignment(distance);
		}

		response.setViewingResource(tresource);
	}

	void buildActiveResource(WorkResponse response, AbstractWork work) {
		com.workmarket.domains.model.WorkResource resource = workService.findActiveWorkResource(work.getId());

		if (resource == null) {
			return;
		}

		User tUser = objectFactory.newUser(resource.getUser());
		tUser.setCompany(
			new Company()
				.setId(resource.getUser().getCompany().getId())
				.setName(resource.getUser().getCompany().getEffectiveName())
		);

		long userId = resource.getUser().getId();
		UserAssetAssociation avatars = userAssetAssociationDAO.findUserAvatars(userId);
		if (avatars != null) {
			Asset avatarSmall = avatars.getTransformedSmallAsset();

			if (avatarSmall != null) {
				tUser.setAvatarSmall(objectFactory.newAsset(avatarSmall));
			}

		}
		Profile tProfile = new Profile();
		tProfile.addToPhoneNumbers(
			new Phone()
				.setPhone(resource.getUser().getProfile()
					.getWorkPhone()).setExtension(resource.getUser().getProfile().getWorkPhoneExtension())
		);

		Optional<Address> address = getAddress(resource);
		if (address.isPresent()) {
			tProfile.setAddress(address.get());
		}
		tUser.setProfile(tProfile);

		com.workmarket.domains.model.lane.LaneAssociation lane = laneAssociationDAO.findActiveAssociationByUserIdAndCompanyId(userId, work.getCompany().getId());
		if (lane != null) {
			tUser.setLaneType(lane.getLaneType());
		}

		if (work.isComplete() || work.isPaid()) {
			tUser.setRatingSummary(
				new RatingSummary()
					.setRating(ratingDAO.findSatisfactionRateForUser(resource.getUser().getId()).shortValue())
					.setNumberOfRatings(ratingDAO.countAllUserRatings(resource.getUser().getId()))
			);
		}

		Resource tResource = new Resource()
			.setId(resource.getId())
			.setStatus(objectFactory.newStatus(resource.getWorkResourceStatusType()))
			.setUser(tUser);

		Double distance = workService.calculateDistanceToWork(resource.getUser().getId(), work);
		if (distance != null) {
			tResource.setDistanceToAssignment(distance);
		}

		// Build progress and time tracking info

		response.getWork().setResolution(work.getResolution());

		if (resource.getHoursWorked() != null) {
			tResource.setHoursWorked(resource.getHoursWorked().doubleValue());
		}
		if (resource.getUnitsProcessed() != null) {
			tResource.setUnitsProcessed(resource.getUnitsProcessed().doubleValue());
		}

		if (resource.getAdditionalExpenses() != null) {
			tResource.setAdditionalExpenses(resource.getAdditionalExpenses().doubleValue());
		}
		if (resource.getBonus() != null) {
			tResource.setBonus(resource.getBonus().doubleValue());
		}

		tResource.setConfirmed(work.isConfirmed());
		if (work.isConfirmed() && resource.getConfirmedOn() != null) {
			tResource.setConfirmedOn(resource.getConfirmedOn().getTimeInMillis());
		} else if (work.isResourceConfirmationRequired()) {
			boolean isConfirmableNow = workService.isConfirmableNow(work);
			response.getWork().setIsConfirmable(isConfirmableNow);
			if (isConfirmableNow) {
				response.getWork().setConfirmByDate(workService.calculateRequiredConfirmationDate(work));
			} else {
				response.getWork().setConfirmableDate(workService.calculateRequiredConfirmationNotificationDate(work));
			}
		}

		long timeTrackingDuration = 0;
		WorkRescheduleNegotiation workRescheduleNegotiation = workNegotiationService.findLatestApprovedRescheduleRequestForWork(work.getId());
		for (WorkResourceTimeTracking tt : resource.getTimeTracking()) {
			// any check in/check outs that occurred before a re-schedule
			// are for display purposes only
			// they should not affect functionality (e.g. work status, complete button)
			boolean isAfterLastReschedule = workRescheduleNegotiation == null || tt.getCheckedInOn().after(workRescheduleNegotiation.getApprovedOn());

			TimeTrackingEntry tEntry = new TimeTrackingEntry();
			tEntry.setId(tt.getId());
			tEntry.setCreatedOn(tt.getCreatedOn().getTimeInMillis());
			tEntry.setModifiedOn(tt.getModifiedOn().getTimeInMillis());

			if (tt.getNote() != null) {
				tEntry.setNote(objectFactory.newNote(tt.getNote()));
			}

			if (tt.getCheckedInOn() != null) {
				tEntry.setCheckedInOn(tt.getCheckedInOn().getTimeInMillis());
				tEntry.setCheckedInBy(objectFactory.newUser(tt.getCheckedInBy()));
				tResource.setCheckedIn(isAfterLastReschedule);
			}

			if (tt.getCheckedOutOn() != null) {
				tEntry.setCheckedOutOn(tt.getCheckedOutOn().getTimeInMillis());
				tEntry.setCheckedOutBy(objectFactory.newUser(tt.getCheckedOutBy()));
				tResource.setCheckedOut(isAfterLastReschedule);
			} else {
				tResource.setCheckedOut(false);
			}
			if (tt.getDistanceIn() != null) {
				tEntry.setDistanceIn(tt.getDistanceIn());
			}
			if (tt.getDistanceOut() != null) {
				tEntry.setDistanceOut(tt.getDistanceOut());
			}

			if (!tt.getDeleted()) {
				tResource.addToTimeTrackingLog(tEntry);

				// Only include time tracking for which there is an in AND out
				if (tt.getCheckedInOn() != null && tt.getCheckedOutOn() != null) {
					timeTrackingDuration += DateUtilities.getDuration(tt.getCheckedInOn(), tt.getCheckedOutOn());
				}
			}
		}
		tResource.setTimeTrackingDuration(timeTrackingDuration);

		if (resource.getAppointment() != null) {
			tResource.setAppointment(objectFactory.newSchedule(resource.getAppointment()));
		}

		// Get attempts for any required assessments

		List<WorkAssessmentAssociation> assessmentAssociations = assessmentService.findAllWorkAssessmentAssociationByWork(work.getId());
		if (isNotEmpty(assessmentAssociations)) {
			for (com.workmarket.domains.model.assessment.Attempt a : assessmentService.findLatestAttemptByUserAndWork(resource.getUser().getId(), work.getId())) {
				AssessmentAttemptPair p = new AssessmentAttemptPair()
					.setAssessment(
						new Assessment()
							.setId(a.getAssessment().getId())
							.setName(a.getAssessment().getName())
							.setHasAssetItems(a.getAssessment().hasAssetItems())
					)
					.setLatestAttempt(
						new Attempt()
							.setId(a.getId())
							.setStatus(objectFactory.newStatus(a.getStatus()))
							.setRespondedToAllItems(a.getAllQuestionsRespondedTo())
					);
				tResource.addToAssessmentAttempts(p);
			}
		}

		if (resource.getLastRemindedToCompleteOn() != null) {
			tResource.setLastRemindedToCompleteOn(resource.getLastRemindedToCompleteOn().getTimeInMillis());
		}

		for (WorkResourceLabel label : workResourceLabelDAO.findByWorkResource(resource.getId())) {
			tResource.addToLabels(new ResourceLabel()
				.setId(label.getId())
				.setCode(label.getWorkResourceLabelType().getCode())
				.setDescription(label.getWorkResourceLabelType().getDescription())
				.setIgnored(label.isIgnored())
				.setConfirmed(label.isConfirmed())
			);
		}
		response.getWork().setActiveResource(tResource);

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE)) {
			response.setViewingResource(tResource);
		}
	}

	private Optional<Address> getAddress(WorkResource resource) {
		Long addressId = resource.getUser().getProfile().getAddressId();
		if (addressId != null) {
			com.workmarket.domains.model.Address address = addressService.findById(addressId);
			if (address != null) {
				return Optional.of(objectFactory.newAddress(address));
			}
		} else if (resource.getUser().getCompany().getAddress() != null) {
			return Optional.of(objectFactory.newAddress(resource.getUser().getCompany().getAddress()));
		}
		return Optional.absent();
	}

	/**
	 * An alternate method for loading pending negotiations (rather than attached to resources in <code>buildResources</code>)
	 *
	 * @param response
	 * @param work
	 * @throws Exception
	 */
	void buildPendingNegotiationsForResources(WorkResponse response, AbstractWork work) throws Exception {
		Assert.notNull(work.getId());

		WorkNegotiationPagination negotiationPagination = new WorkNegotiationPagination(true);
		negotiationPagination.getFilters().put(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS.toString(), ApprovalStatus.PENDING.toString());
		negotiationPagination = workNegotiationDAO.findByWork(work.getId(), negotiationPagination);

		for (AbstractWorkNegotiation n : negotiationPagination.getResults()) {
			if (!(n instanceof WorkNegotiation)) {
				continue;
			}

			WorkNegotiation wn = (WorkNegotiation) n;
			Negotiation tnegotiation = buildNegotiationFromNegotiation(n);
			tnegotiation
				.setIsPriceNegotiation(wn.isPriceNegotiation())
				.setIsScheduleNegotiation(wn.isScheduleNegotiation());

			if (wn.isPriceNegotiation()) {
				tnegotiation.setPricing(objectFactory.newPricing(wn.getPricingStrategy()));

				com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(wn.getId());
				PaymentSummary paymentSummary = buildPaymentFromPaymentDTO(payment);
				tnegotiation.setPayment(paymentSummary);
			}

			if (wn.isScheduleNegotiation()) {
				Schedule tschedule = new Schedule()
					.setRange(wn.getScheduleRangeFlag())
					.setFrom(wn.getScheduleFrom().getTimeInMillis());
				if (wn.getScheduleRangeFlag()) {
					tschedule.setThrough(wn.getScheduleThrough().getTimeInMillis());
				}
				tnegotiation.setSchedule(tschedule);
			}

			if (wn.hasExpirationDate()) {
				tnegotiation.setExpiresOn(wn.getExpiresOn().getTimeInMillis());
				tnegotiation.setIsExpired(DateUtilities.isInPast(wn.getExpiresOn()));
			} else {
				tnegotiation.setIsExpired(false);
			}

			double distance = workService.calculateDistanceToWork(tnegotiation.getRequestedBy().getId(), work);
			tnegotiation.setDistanceToAssignment(distance);
			response.getWork().addToPendingNegotiations(tnegotiation);
		}
	}

	void buildBudgetNegotiationForActiveResource(WorkResponse response, AbstractWork work) throws Exception {
		if (!response.getWork().isSetActiveResource()) return;

		Resource activeResource = response.getWork().getActiveResource();

		Optional<WorkBudgetNegotiation> negotiationOpt = workNegotiationDAO.findLatestActiveBudgetNegotiationByCompanyForWork(
			activeResource.getUser().getCompany().getId(),
			work.getId()
		);

		if (!negotiationOpt.isPresent()) {
			return;
		}

		WorkBudgetNegotiation negotiation = negotiationOpt.get();

		// need to make sure the value of the negotiation includes what is already set on work.pricing.
		// this is because there can now be more than one type of price negotiation existing at a time
		PricingStrategyUtilities.copyHigherPriceValues(
			work.getPricingStrategy().getFullPricingStrategy(),
			negotiation.getPricingStrategy().getFullPricingStrategy());

		Negotiation tNegotiation = buildNegotiationFromNegotiation(negotiation);
		tNegotiation
			.setIsPriceNegotiation(negotiation.isPriceNegotiation())
			.setPricing(objectFactory.newPricing(negotiation.getPricingStrategy()));

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
			com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(negotiation.getId());
			PaymentSummary tPayment = buildPaymentFromPaymentDTO(payment);
			tNegotiation.setPayment(tPayment);
		}

		activeResource.setBudgetNegotiation(tNegotiation);
	}

	void buildExpenseNegotiationForActiveResource(WorkResponse response, AbstractWork work) throws Exception {
		if (!response.getWork().isSetActiveResource()) {
			return;
		}

		Resource activeResource = response.getWork().getActiveResource();

		Optional<WorkExpenseNegotiation> negotiationOpt = workNegotiationService.findLatestActiveExpenseNegotiationByCompanyForWork(
			activeResource.getUser().getCompany().getId(),
			work.getId()
		);

		if (!negotiationOpt.isPresent()) {
			return;
		}
		WorkExpenseNegotiation negotiation = negotiationOpt.get();

		Negotiation tNegotiation = buildNegotiationFromNegotiation(negotiation);
		tNegotiation
			.setIsPriceNegotiation(negotiation.isPriceNegotiation())
			.setPricing(objectFactory.newPricing(negotiation.getPricingStrategy()));

		// need to do this so that the front end can display the active one. Doesn't affect max spend.
		if (negotiation.getStandaloneAdditionalExpenses() != null)
			tNegotiation.getPricing().setAdditionalExpenses(negotiation.getStandaloneAdditionalExpenses().doubleValue());

		if (negotiation.getSpendLimitNegotiationType() != null) {
			tNegotiation.setType(objectFactory.newStatus(negotiation.getSpendLimitNegotiationType()));
		}

		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
			com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(negotiation.getId());
			PaymentSummary tpayment = buildPaymentFromPaymentDTO(payment);
			tNegotiation.setPayment(tpayment);
		}

		activeResource.setExpenseNegotiation(tNegotiation);
	}

	void buildBonusForActiveResource(WorkResponse response, AbstractWork work) throws Exception {
		if (!response.getWork().isSetActiveResource()) {
			return;
		}

		Resource activeResource = response.getWork().getActiveResource();

		Optional<WorkBonusNegotiation> negotiationOpt = workNegotiationService.findLatestActiveBonusNegotiationByCompanyForWork(
			activeResource.getUser().getCompany().getId(),
			work.getId()
		);

		if (!negotiationOpt.isPresent()) {
			return;
		}
		WorkBonusNegotiation negotiation = negotiationOpt.get();

		Negotiation tNegotiation = buildNegotiationFromNegotiation(negotiation);
		tNegotiation
			.setIsPriceNegotiation(negotiation.isPriceNegotiation())
			.setPricing(objectFactory.newPricing(negotiation.getPricingStrategy()));

		// need to do this so that the front end can display the active one. Doesn't affect max spend.
		if (negotiation.getStandaloneBonus() != null) {
			tNegotiation.getPricing().setBonus(negotiation.getStandaloneBonus().doubleValue());
		}
		if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
			com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(negotiation.getId());
			PaymentSummary tpayment = buildPaymentFromPaymentDTO(payment);
			tNegotiation.setPayment(tpayment);
		}

		activeResource.setBonusNegotiation(tNegotiation);
	}

	void buildPendingRescheduleNegotiationForBuyer(WorkResponse response, AbstractWork work) {

		WorkRescheduleNegotiation n = workNegotiationDAO.findLatestActiveRescheduleRequestByCompanyForWork(
			false,
			work.getCompany().getId(),
			work.getId()
		);

		if (n == null) {
			return;
		}

		Negotiation tNegotiation = buildNegotiationFromNegotiation(n);

		Schedule tSchedule = new Schedule()
			.setRange(n.getScheduleRangeFlag())
			.setFrom(n.getScheduleFrom().getTimeInMillis());
		if (n.getScheduleRangeFlag()) {
			tSchedule.setThrough(n.getScheduleThrough().getTimeInMillis());
		}

		tNegotiation
			.setIsScheduleNegotiation(n.isScheduleNegotiation())
			.setSchedule(tSchedule);

		response.setBuyerRescheduleNegotiation(tNegotiation);
	}

	void buildPendingRescheduleNegotiationForActiveResource(WorkResponse response, AbstractWork work) {
		if (!response.getWork().isSetActiveResource()) {
			return;
		}

		WorkRescheduleNegotiation n = workNegotiationDAO.findLatestActiveRescheduleRequestByCompanyForWork(
			true,
			response.getWork().getActiveResource().getUser().getCompany().getId(),
			work.getId()
		);

		if (n == null) {
			return;
		}

		Negotiation tNegotiation = buildNegotiationFromNegotiation(n);

		Schedule tSchedule = new Schedule()
			.setRange(n.getScheduleRangeFlag())
			.setFrom(n.getScheduleFrom().getTimeInMillis());
		if (n.getScheduleRangeFlag()) {
			tSchedule.setThrough(n.getScheduleThrough().getTimeInMillis());
		}

		tNegotiation
			.setIsScheduleNegotiation(n.isScheduleNegotiation())
			.setSchedule(tSchedule);

		response.getWork().getActiveResource().setRescheduleNegotiation(tNegotiation);
	}

	void buildPendingNegotiation(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) throws Exception {
		if (!response.isSetViewingResource()) {
			return;
		}

		WorkNegotiation n = workNegotiationDAO.findLatestByUserForWork(currentUser.getId(), work.getId());
		if (n == null) {
			return;
		}

		Negotiation tNegotiation = buildNegotiationFromNegotiation(n);

		tNegotiation
			.setIsPriceNegotiation(n.isPriceNegotiation())
			.setIsScheduleNegotiation(n.isScheduleNegotiation());

		if (n.isPriceNegotiation()) {
			tNegotiation.setPricing(objectFactory.newPricing(n.getPricingStrategy()));

			if (CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)) {
				com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(n.getId());
				PaymentSummary tpayment = buildPaymentFromPaymentDTO(payment);
				tNegotiation.setPayment(tpayment);
			}
		}

		if (n.isScheduleNegotiation() && n.getScheduleFrom() != null) {
			Schedule tschedule = new Schedule()
				.setRange(n.getScheduleRangeFlag())
				.setFrom(n.getScheduleFrom().getTimeInMillis());
			if (n.getScheduleRangeFlag()) {
				tschedule.setThrough(n.getScheduleThrough().getTimeInMillis());
			}
			tNegotiation.setSchedule(tschedule);
		}

		boolean isExpired = false;
		if (n.hasExpirationDate()) {
			tNegotiation.setExpiresOn(n.getExpiresOn().getTimeInMillis());
			isExpired = DateUtilities.isInPast(n.getExpiresOn());
		}
		tNegotiation.setIsExpired(isExpired);
		response.getViewingResource().setPendingNegotiation(tNegotiation);
	}

	Negotiation buildNegotiationFromNegotiation(AbstractWorkNegotiation n) {
		Negotiation tNegotiation = new Negotiation()
			.setId(n.getId())
			.setEncryptedId(n.getEncryptedId())
			.setRequestedBy(objectFactory.newUser(n.getRequestedBy()))
			.setRequestedOn(n.getRequestedOn().getTimeInMillis())
			.setInitiatedByResource(n.isInitiatedByResource())
			.setApprovalStatus(objectFactory.newStatus(n.getApprovalStatus()));

		if (n.getApprovedBy() != null && n.getApprovedOn() != null) {
			tNegotiation
				.setApprovedBy(objectFactory.newUser(n.getApprovedBy()))
				.setApprovedOn(n.getApprovedOn().getTimeInMillis());
		}

		if (n.getNote() != null) {
			tNegotiation.setNote(
				new Note()
					.setText(n.getNote().getContent())
					.setCreator(objectFactory.newUser(n.getRequestedBy()))
			);
		}

		return tNegotiation;
	}

	void buildResources(WorkResponse response, AbstractWork work) throws Exception {
		Assert.notNull(work.getId());

		if (CollectionUtils.isEmpty(work.getWorkResources())) {
			return;
		}

		// Build negotiation lookup table: user => negotiation
		Map<Long, WorkNegotiation> negotiationLookup = Maps.newHashMap();
		WorkNegotiationPagination negotiationPagination = new WorkNegotiationPagination(true);
		negotiationPagination.getFilters().put(WorkNegotiationPagination.FILTER_KEYS.APPROVAL_STATUS.toString(), ApprovalStatus.PENDING.toString());
		negotiationPagination = workNegotiationDAO.findByWork(work.getId(), negotiationPagination);
		for (AbstractWorkNegotiation n : negotiationPagination.getResults()) {
			if (n instanceof WorkNegotiation) {
				negotiationLookup.put(n.getRequestedBy().getId(), (WorkNegotiation) n);
			}
		}

		for (com.workmarket.domains.model.WorkResource r : workResourceService.findAllResourcesForWork(work.getId())) {
			User tUser = objectFactory.newUser(r.getUser());
			tUser.setCompany(
				new Company()
					.setId(r.getUser().getCompany().getId())
					.setName(r.getUser().getCompany().getEffectiveName())
			);

			Profile tProfile = new Profile();
			tProfile.addToPhoneNumbers(
				new Phone()
					.setPhone(r.getUser().getProfile().getWorkPhone())
					.setExtension(r.getUser().getProfile().getWorkPhoneExtension())
			);

			tUser.setProfile(tProfile);

			Resource tResource = new Resource().setId(r.getId()).setUser(tUser).setStatus(objectFactory.newStatus(r.getWorkResourceStatusType()));

			if (r.isDeclined()) {
				tResource.setDeclinedOn(r.getModifiedOn().getTimeInMillis());
			} else {
				tResource.setInvitedOn(r.getCreatedOn().getTimeInMillis());
			}

			if (negotiationLookup.containsKey(r.getUser().getId())) {
				WorkNegotiation n = negotiationLookup.get(r.getUser().getId());
				Negotiation tNegotiation = buildNegotiationFromNegotiation(n);
				tNegotiation
					.setIsPriceNegotiation(n.isPriceNegotiation())
					.setIsScheduleNegotiation(n.isScheduleNegotiation());

				if (n.isPriceNegotiation()) {
					tNegotiation.setPricing(objectFactory.newPricing(n.getPricingStrategy()));

					com.workmarket.service.business.dto.PaymentSummaryDTO payment = paymentSummaryService.generatePaymentSummaryForNegotiation(n.getId());
					PaymentSummary tpayment = buildPaymentFromPaymentDTO(payment);
					tNegotiation.setPayment(tpayment);
				}

				if (n.isScheduleNegotiation()) {
					Schedule tschedule = new Schedule()
						.setRange(n.getScheduleRangeFlag())
						.setFrom(n.getScheduleFrom().getTimeInMillis());
					if (n.getScheduleRangeFlag()) {
						tschedule.setThrough(n.getScheduleThrough().getTimeInMillis());
					}
					tNegotiation.setSchedule(tschedule);
				}

				if (n.hasExpirationDate()) {
					tNegotiation.setExpiresOn(n.getExpiresOn().getTimeInMillis());
					tNegotiation.setIsExpired(DateUtilities.isInPast(n.getExpiresOn()));
				} else {
					tNegotiation.setIsExpired(false);
				}

				tResource.setPendingNegotiation(tNegotiation);
			}

			response.getWork().addToResources(tResource);
		}

	}

	void buildNotification(WorkResponse response, AbstractWork work) {
		response.getWork().setWorkNotifyAllowed(workService.isWorkNotifyAllowed(work.getId()));
		response.getWork().setWorkNotifyAvailable(workService.isWorkNotifyAvailable(work.getId()));
	}

	void buildLocation(WorkResponse response, AbstractWork work) {
		if (work.getAddressOnsiteFlag() == null) return;

		if (!work.getAddressOnsiteFlag()) {
			response.getWork().setOffsiteLocation(true);
			return;
		}

		response.getWork().setOffsiteLocation(false);

		boolean hasLocation = (work.getLocation() != null);
		boolean hasAddress = (work.getAddress() != null);

		if (hasLocation || hasAddress) {
			Location tlocation = new Location();

			if (work.getLocation() != null) {
				tlocation.setId(work.getLocation().getId());
				tlocation.setName(work.getLocation().getName());
				tlocation.setNumber(work.getLocation().getLocationNumber());
				tlocation.setInstructions(work.getLocation().getInstructions());
			}

			if (work.getAddress() != null) {
				response.getWork().setNewLocation(true);
				Address taddress = objectFactory.newAddress(work.getAddress());
				if (work.getAddress().getDressCode() != null)
					taddress.setDressCode(work.getAddress().getDressCode().getDescription());

				if (work.getAddress().getLatitude() != null && work.getAddress().getLongitude() != null) {
					taddress.setPoint(
						new GeoPoint(work.getAddress().getLatitude().doubleValue(), work.getAddress().getLongitude().doubleValue())
					);
				}
				tlocation.setAddress(taddress);
			}

			response.getWork().setLocation(tlocation);
		}
	}

	void buildClientCompany(WorkResponse response, AbstractWork work) {
		if (work.getClientCompany() == null)
			return;

		response.getWork().setClientCompany(
			new Company()
				.setId(work.getClientCompany().getId())
				.setName(work.getClientCompany().getName())
		);
	}

	void buildSchedule(WorkResponse response, AbstractWork work) {
		if (work.getSchedule() != null) {
			response.getWork().setSchedule(objectFactory.newSchedule(work.getSchedule()));
		}
	}

	void buildPayment(WorkResponse response, AbstractWork work) {
		PaymentSummary tpayment = buildPaymentFromPaymentDTO(
			paymentSummaryService.generatePaymentSummaryForWork(work.getId())
		);
		response.getWork().setPayment(tpayment);
	}

	PaymentSummary buildPaymentFromPaymentDTO(com.workmarket.service.business.dto.PaymentSummaryDTO payment) {

		PaymentSummary tpayment = new PaymentSummary();

		if (payment.getMaxSpendLimit() != null)
			tpayment.setMaxSpendLimit(payment.getMaxSpendLimit().doubleValue());
		if (payment.getActualSpendLimit() != null)
			tpayment.setActualSpendLimit(payment.getActualSpendLimit().doubleValue());
		if (payment.getBuyerFee() != null)
			tpayment.setBuyerFee(payment.getBuyerFee().doubleValue());
		if (payment.getBuyerFeePercentage() != null)
			tpayment.setBuyerFeePercentage(payment.getBuyerFeePercentage().doubleValue());
		if (payment.getBuyerFeeBand() != null)
			tpayment.setBuyerFeeBand(payment.getBuyerFeeBand());
		if (payment.getTotalCost() != null)
			tpayment.setTotalCost(payment.getTotalCost().doubleValue());

		if (payment.getLegacyBuyerFee() != null)
			tpayment.setLegacyBuyerFee(payment.getLegacyBuyerFee());

		if (payment.getHoursWorked() != null)
			tpayment.setHoursWorked(payment.getHoursWorked().doubleValue());
		if (payment.getUnitsProcessed() != null)
			tpayment.setUnitsProcessed(payment.getUnitsProcessed().doubleValue());
		if (payment.getAdditionalExpenses() != null)
			tpayment.setAdditionalExpenses(payment.getAdditionalExpenses().doubleValue());
		if (payment.getAdditionalExpensesWithFee() != null)
			tpayment.setAdditionalExpensesWithFee(payment.getAdditionalExpensesWithFee().doubleValue());
		if (payment.getBonus() != null)
			tpayment.setBonus(payment.getBonus().doubleValue());
		if (payment.getBonusWithFee() != null)
			tpayment.setBonusWithFee(payment.getBonusWithFee().doubleValue());

		if (payment.getSalesTaxCollectedFlag() != null)
			tpayment.setSalesTaxCollectedFlag(payment.getSalesTaxCollectedFlag());
		if (payment.getSalesTaxCollected() != null)
			tpayment.setSalesTaxCollected(payment.getSalesTaxCollected().doubleValue());
		if (payment.getSalesTaxRate() != null)
			tpayment.setSalesTaxRate(payment.getSalesTaxRate().doubleValue());

		if (payment.getPaidOn() != null)
			tpayment.setPaidOn(payment.getPaidOn().getTimeInMillis());
		if (payment.getPaymentDueOn() != null)
			tpayment.setPaymentDueOn(payment.getPaymentDueOn().getTimeInMillis());

		if (payment.getPerHourPriceWithFee() != null) {
			tpayment.setPerHourPriceWithFee(payment.getPerHourPriceWithFee().doubleValue());
		}
		if (payment.getPerUnitPriceWithFee() != null) {
			tpayment.setPerUnitPriceWithFee(payment.getPerUnitPriceWithFee().doubleValue());
		}
		if (payment.getInitialPerHourPriceWithFee() != null) {
			tpayment.setInitialPerHourPriceWithFee(payment.getInitialPerHourPriceWithFee().doubleValue());
		}
		if (payment.getAdditionalPerHourPriceWithFee() != null) {
			tpayment.setAdditionalPerHourPriceWithFee(payment.getAdditionalPerHourPriceWithFee().doubleValue());
		}
		return tpayment;
	}

	void buildInvoice(WorkResponse response, AbstractWork work) {
		if (!(work instanceof com.workmarket.domains.work.model.Work)) {
			return;
		}
		if (!work.isInvoiced()) {
			return;
		}

		com.workmarket.domains.model.invoice.Invoice invoice = work.getInvoice();
		response.getWork().setInvoice(
			new Invoice()
				.setId(invoice.getId())
				.setNumber(invoice.getInvoiceNumber())
				.setStatus(objectFactory.newStatus(invoice.getInvoiceStatusType()))
				.setBundled(invoice.isBundled())
				.setEditable(invoice.isEditable())
		);
	}

	void buildPricing(WorkResponse response, AbstractWork work) {

		if (work.getPricingStrategy() == null) {
			return;
		}

		PricingStrategy tpricing = objectFactory.newPricing(work.getPricingStrategy());
		tpricing.setOfflinePayment(workService.isOfflinePayment(work));
		response.getWork().setPricing(tpricing);
		/*
		 * This may be redundant but I don't want to move logic around on the front end.
		 */
		response.getWork().setApprovedAdditionalExpenses(tpricing.getAdditionalExpenses() > 0d);
		response.getWork().setApprovedBonus(tpricing.getBonus() > 0d);
		response.getWork().setPricingEditable(work.isPricingEditable());
	}

	void buildPricingHistory(WorkResponse response, AbstractWork work) {
		for (com.workmarket.domains.model.WorkPrice reprice : work.getPriceHistory()) {
			response.getWork().addToPricingHistory(
				new PricingLogEntry()
					.setTimestamp(reprice.getCreatedOn().getTimeInMillis())
					.setPricing(objectFactory.newPricing(reprice.getPricingStrategy()))
			);
		}
	}

	void buildAssets(WorkResponse response, AbstractWork work) {
		List<WorkAssetAssociation> assetAssociations = workAssetAssociationDAO.findByWork(work.getId());
		for (WorkAssetAssociation assetAssociation : assetAssociations) {

			if (assetAssociation.isDeliverable()) {
				DeliverableAsset deliverableAsset = new DeliverableAsset(assetAssociation);
				deliverableAsset.setUploadedBy(userService.getFullName(assetAssociation.getAsset().getCreatorId()));
				response.getWork().addToDeliverableAssets(deliverableAsset);
			} else {
				com.workmarket.thrift.core.Asset asset = objectFactory.newAsset(assetAssociation.getAsset());
				asset.setType(assetAssociation.getAssetType().getCode());

				if (WorkAssetAssociationType.ATTACHMENT.equals(asset.getType())) {
					WorkAssetVisibility workAssetVisibility = workAssetVisibilityDAO.findByWorkAssetAssociationId(assetAssociation.getId());
					if (workAssetVisibility != null) {
						if (!documentService.isDocumentVisible(workAssetVisibility, work)) {
							continue;
						}
						asset.setVisibilityCode(workAssetVisibility.getVisibilityType().getCode());
					} else {
						if (!documentService.isDocumentVisible(VisibilityType.createDefaultVisibility(), work)) {
							continue;
						}
						asset.setVisibilityCode(VisibilityType.DEFAULT_VISIBILITY);
					}
				}

				response.getWork().addToAssets(asset);
			}
		}
	}

	@SuppressWarnings("unchecked")
	void buildNotes(WorkResponse response, final com.workmarket.domains.model.User currentUser, AbstractWork work) {
		boolean privileged = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN)
			|| userRoleService.isInternalUser(currentUser);
		boolean isResource = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.RESOURCE);

		NotePagination notePagination = new NotePagination(true);
		notePagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);
		notePagination.setIncludePrivileged(privileged || isResource);
		notePagination = workNoteService.findAllNotesByWorkForCompany(work.getId(), currentUser.getCompany().getId(), notePagination);
		List<Long> replyToIds = CollectionUtilities.newListPropertyProjection(notePagination.getResults(), "replyToId");
		Map<Long, Map<String, Object>> props = userService.getProjectionMapByIds(replyToIds, "firstName", "lastName");
		final List<Long> noteIds = Lists.newArrayList();

		for (com.workmarket.domains.model.note.Note n : notePagination.getResults()) {
			// hide counter off notes for other resource
			// Note is private, and the user is not privileged or the user is not the note creator
			if (n.getIsPrivate() && !privileged && !currentUser.getId().equals(n.getCreatorId())) {
				continue;
			}
			//Note is privileged, and the user is not privileged, and the user is not the note creator
			else if (n.getIsPrivileged() && !privileged && n.getReplyToId() == null && !currentUser.getId().equals(n.getCreatorId())) {
				continue;
			}
			//Note is privileged, and the user is a resource, and the user is not assigned to the note
			else if (n.getIsPrivileged() && isResource && n.getReplyToId() != null && !(currentUser.getId().equals(n.getReplyToId()))) {
				continue;
			}
			//
			else if (!isResource && !privileged && n.getIsPrivate()) {
				// hide counter off notes for other resource
				continue;
			}
			String replyToName = props.containsKey(n.getReplyToId())
				? StringUtilities.fullName((String) props.get(n.getReplyToId()).get("firstName"), (String) props.get(n.getReplyToId()).get("lastName"))
				: "";
			com.workmarket.domains.model.User creator = userService.findUserById(n.getCreatorId());
			noteIds.add(n.getId());
			User onBehalf = null;

			if (isNotBlank(n.getOnBehalfUserNumber())) {
				onBehalf = new User()
					.setUserNumber(n.getOnBehalfUserNumber())
					.setName(
						new Name()
							.setFirstName(n.getOnBehalfFirstName())
							.setLastName(n.getOnBehalfLastName())
					);
			}

			response.getWork().addToNotes(
				new Note()
					.setId(n.getId())
					.setText(n.getContent())
					.setIsPrivate(n.getIsPrivate())
					.setIsPrivileged(n.getIsPrivileged())
					.setCreatedOn(n.getCreatedOn().getTimeInMillis())
					.setCreator(objectFactory.newUser(creator))
					.setOnBehalfOf(onBehalf)
					.setReplyToName(replyToName)
			);
		}
	}

	void buildChangeLog(WorkResponse response, AbstractWork work) {
		WorkChangeLogPagination changeLogPagination = new WorkChangeLogPagination(true);
		changeLogPagination.setSortDirection(Pagination.SORT_DIRECTION.DESC);

		try {
			List<WorkChangeLog> changeLogs = workChangeLogService.findAllChangeLogsByWorkId(work.getId(), changeLogPagination).getResults();
			List<LogEntry> entries = Lists.newArrayListWithExpectedSize(changeLogs.size());

			for (WorkChangeLog changeLog : changeLogs) {
				WorkChangeLog workChangeLog = workChangeLogService.findWorkChangeLog(changeLog.getId());
				if (workChangeLog != null) {
					if (shouldNotDisplayNegotiationExpiry(work, workChangeLog)) {
						continue;
					}
					workChangeLog.setActorFirstName(changeLog.getActorFirstName());
					workChangeLog.setActorLastName(changeLog.getActorLastName());
					workChangeLog.setWorkTitle(changeLog.getWorkTitle());
					workChangeLog.setOnBehalfOfActorFullName(changeLog.getOnBehalfOfActorFullName());

					String text = templateService.renderChangeLogTemplate(workChangeLog);
					if (isNotBlank(text)) {
						LogEntry entry = new LogEntry()
							.setTimestamp(workChangeLog.getCreatedOn().getTimeInMillis())
							.setText(text)
							.setActor(objectFactory.newWorkChangeLogActor(workChangeLog))
							.setType(objectFactory.newLogEntryType(workChangeLog));

						if (isNotBlank(workChangeLog.getOnBehalfOfActorFullName())) {
							entry.setOnBehalfOfUser(workChangeLog.getOnBehalfOfActorFullName());
						}
						if (entry.getType().equals(LogEntryType.WORK_SUB_STATUS_CHANGE)) {
							WorkSubStatusChangeChangeLog change = (WorkSubStatusChangeChangeLog) workChangeLog;
							SubStatusActionType type = (change.getNewValue() != null) ? SubStatusActionType.ADDED : SubStatusActionType.RESOLVED;
							WorkSubStatusType value = SubStatusActionType.ADDED.equals(type) ? change.getNewValue() : change.getOldValue();

							WorkSubStatusTypeCompanySetting setting = workSubStatusService.findColorByIdAndCompany(value.getId(), work.getCompany().getId());
							value.setCustomColorRgb(setting == null ? "" : setting.getColorRgb());
							entry.setSubStatus(objectFactory.newSubStatus(value));
							entry.setSubStatusActionType(type);
						}
						if (LogEntryType.WORK_STATUS_CHANGE.equals(entry.getType())) {
							WorkStatusChangeChangeLog change = (WorkStatusChangeChangeLog) workChangeLog;
							entry.setStatus(change.getNewStatus().getDescription());
						}

						if (LogEntryType.WORK_RESOURCE_STATUS_CHANGE.equals(entry.getType())) {
							WorkResourceStatusChangeChangeLog change = (WorkResourceStatusChangeChangeLog) workChangeLog;
							entry.setStatus(change.getNewStatus().getDescription());
						}

						if (LogEntryType.WORK_NEGOTIATION_STATUS_CHANGE.equals(entry.getType())) {
							WorkNegotiationStatusChangeChangeLog change = (WorkNegotiationStatusChangeChangeLog) workChangeLog;
							if (ApprovalStatus.DECLINED.equals(change.getNewApprovalStatus())) {
								entry.setRejectAction(true);
							}

							// If the negotiation is a schedule only negotiation, set it true
							if (change != null) {
								if (change.getNegotiation() instanceof WorkRescheduleNegotiation ||
									(change.getCounterofferNegotiation() != null && change.getCounterofferNegotiation().isScheduleNegotiation()
										&& !change.getCounterofferNegotiation().isPriceNegotiation())) {
									entry.setScheduleNegotiationOnly(true);
								}
							}

						}


						entries.add(entry);
					}
				}
			}
			response.getWork().setChangelog(entries);
		} catch (Exception e) {
			logger.error("Error building changelog", e);
		}
	}

	boolean shouldNotDisplayNegotiationExpiry(AbstractWork work, WorkChangeLog workChangeLog) {
		String workStatusCode = work.getWorkStatusType().getCode();
		Long activeWorkerId = workService.findActiveWorkerId(work.getId());

		return activeWorkerId != null &&
			WorkStatusType.ACTIVE.equals(workStatusCode) &&
			workChangeLog instanceof WorkNegotiationExpiredChangeLog &&
			!activeWorkerId.equals(workChangeLog.getActorId());
	}

	void buildQuestionAnswerPairs(WorkResponse response, AbstractWork work) {
		for (com.workmarket.domains.model.WorkQuestionAnswerPair qa : workQuestionAnswerPairDAO.findByWork(work.getId())) {
			QuestionAnswerPair tqa = new QuestionAnswerPair()
				.setId(qa.getId())
				.setQuestion(qa.getQuestion())
				.setQuestionedOn(qa.getCreatedOn().getTimeInMillis())
				.setQuestioner(Long.parseLong(userService.findUserNumber(qa.getQuestionerId())));

			if (qa.isAnswered()) {
				tqa
					.setAnswer(qa.getAnswer())
					.setAnsweredOn(qa.getAnsweredOn().getTimeInMillis())
					.setAnswerer(Long.parseLong(userService.findUserNumber(qa.getAnswererId())));
			}

			response.getWork().addToQuestionAnswerPairs(tqa);
		}
	}

	void buildCustomFields(WorkResponse response, AbstractWork work, boolean includeSavedData) {

		// Default to true if the authorization context hasn't been setup.
		// Otherwise consult context to return only the authorized custom fields.
		boolean isAdmin = true;
		boolean isBuyer = true;
		boolean isActiveResource = true;
		boolean isResource = true;

		if (response.isSetAuthorizationContexts()) {
			isAdmin = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ADMIN);
			isBuyer = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.BUYER);
			isResource = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.RESOURCE);
			isActiveResource = CollectionUtilities.contains(response.getAuthorizationContexts(), AuthorizationContext.ACTIVE_RESOURCE);
		}

		Map<Long, String> savedValues = new HashMap<>();
		for (com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation a : workCustomFieldGroupAssociationDAO.findAllActiveByWork(work.getId())) {

			if (includeSavedData) {
				for (SavedWorkCustomField saved : a.getSavedWorkCustomFields()) {
					savedValues.put(saved.getWorkCustomField().getId(), saved.getValue());
				}
			}

			CustomFieldGroup tGroup = new CustomFieldGroup()
				.setId(a.getWorkCustomFieldGroup().getId())
				.setName(a.getWorkCustomFieldGroup().getName())
				.setIsRequired(a.getWorkCustomFieldGroup().isRequired())
				.setPosition(a.getPosition());

			for (com.workmarket.domains.model.customfield.WorkCustomField field : a.getWorkCustomFieldGroup().getWorkCustomFields()) {
				boolean visibleToBuyer = field.getVisibleToOwnerFlag() && (isAdmin || isBuyer);
				boolean visibleToActiveWorker = field.getVisibleToResourceFlag() && isActiveResource;
				boolean visibleToSentWorkers = work.isSent() && field.getShowOnSentStatus() && isResource;

				// TODO: this shouldn't respect the deleted flag for "legally binding" fields (Assigned or later + resource visible)
				if (!field.getDeleted() && (visibleToBuyer || visibleToActiveWorker || visibleToSentWorkers)) {

					CustomField tField = new CustomField()
						.setId(field.getId())
						.setName(field.getName())
						.setDefaultValue(StringUtils.defaultIfEmpty(field.getDefaultValue(), ""))
						.setIsRequired(field.getRequiredFlag())
						.setType(field.getWorkCustomFieldType().getCode())
						.setVisibleToOwner(field.getVisibleToOwnerFlag())
						.setVisibleToResource(field.getVisibleToResourceFlag())
						.setShowOnPrintout(field.getShowOnPrintout())
						.setShowOnSentStatus(field.getShowOnSentStatus())
						.setShowInAssignmentHeader(field.getShowInAssignmentHeader());

					if (includeSavedData) {
						if (savedValues.containsKey(field.getId())) {
							tField.setValue(StringUtils.defaultIfEmpty(savedValues.get(field.getId()), ""));
						} else {
							tField.setValue("");
						}

						if (isActiveResource && !isAdmin && field.getWorkCustomFieldType().getCode().equals(WorkCustomFieldType.OWNER)) {
							tField.setReadOnly(true);
						}
						if (isResource && !isActiveResource && !isAdmin) {
							tField.setReadOnly(true);
						}
					}
					tGroup.addToFields(tField);
				}
			}
			if (CollectionUtils.isNotEmpty(tGroup.getFields())) {
				response.getWork().addToCustomFieldGroups(tGroup);
			}
		}
	}

	void buildParts(WorkResponse response, AbstractWork work) {
		response.getWork().setPartGroup(partService.getPartGroupByWorkId(work.getId()));
	}

	void buildRequiredAssessments(WorkResponse response, AbstractWork work) {
		List<WorkAssessmentAssociation> assessmentAssociations = assessmentService.findAllWorkAssessmentAssociationByWork(work.getId());

		for (WorkAssessmentAssociation a : assessmentAssociations) {
			response.getWork().addToAssessments(
				new Assessment()
					.setId(a.getAssessment().getId())
					.setName(a.getAssessment().getName())
					.setIsRequired(a.isRequired())
			);
		}
	}

	void buildManageMyWorkMarket(WorkResponse response, AbstractWork work, com.workmarket.domains.model.User currentUser) {
		com.workmarket.domains.model.ManageMyWorkMarket mmw = work.getManageMyWorkMarket();
		com.workmarket.domains.model.WorkResource resource = workService.findWorkResource(currentUser.getId(), work.getId());
		boolean resourceAssignToFirstToAccept = resource != null ? resource.isAssignToFirstToAccept() : false;
		response.getWork().setConfiguration(
			new ManageMyWorkMarket()
				.setCustomFieldsEnabledFlag(mmw.getCustomFieldsEnabledFlag())
				.setEnableAssignmentPrintout(mmw.isEnableAssignmentPrintout())
				.setStandardTermsEndUserFlag(mmw.isStandardTermsEndUserFlag())
				.setEnablePrintoutSignature(mmw.isEnablePrintoutSignature())
				.setBadgeIncludedOnPrintout(mmw.isBadgeIncludedOnPrintout())
				.setCustomFormsEnabledFlag(mmw.getCustomFormsEnabledFlag())
				.setCustomCloseOutEnabledFlag(mmw.getCustomCloseOutEnabledFlag())
				.setAutocloseEnabledFlag(mmw.getAutocloseEnabledFlag())
				.setAutocloseDelayInHours(mmw.getAutocloseDelayInHours())
				.setHideWorkMarketLogoFlag(mmw.getHideWorkMarketLogoFlag())
				.setUseCompanyLogoFlag(mmw.getUseCompanyLogoFlag())
				.setAutoRateEnabledFlag(mmw.getAutoRateEnabledFlag())
				.setPartsLogisticsEnabledFlag(mmw.getPartsLogisticsEnabledFlag())
				.setStandardTermsFlag(mmw.getStandardTermsFlag())
				.setStandardInstructionsFlag(mmw.getStandardInstructionsFlag())
				.setStandardTerms(StringEscapeUtils.unescapeHtml4(mmw.getStandardTerms()))
				.setStandardTermsEndUser(StringEscapeUtils.unescapeHtml4(mmw.getStandardTermsEndUser()))
				.setStandardInstructions(StringEscapeUtils.unescapeHtml4(mmw.getStandardInstructions()))
				.setCheckinRequiredFlag(mmw.getCheckinRequiredFlag())
				.setShowCheckoutNotesFlag(mmw.getShowCheckoutNotesFlag())
				.setCheckoutNoteRequiredFlag(mmw.getCheckoutNoteRequiredFlag())
				.setCheckoutNoteInstructions(mmw.getCheckoutNoteInstructions())
				.setIvrEnabledFlag(mmw.getIvrEnabledFlag())
				.setPaymentTermsDays(work.hasPaymentTerms() ? mmw.getPaymentTermsDays() : 0)
				.setUseMaxSpendPricingDisplayModeFlag(mmw.getUseMaxSpendPricingDisplayModeFlag())
				.setAssessmentsEnabled(mmw.getAssessmentsEnabled())
				.setAutoPayEnabled(mmw.getAutoPayEnabled())
				.setAutoSendInvoiceEmail(mmw.getAutoSendInvoiceEmail())
				.setBadgeShowClientName(mmw.isBadgeShowClientName())
				.setDisablePriceNegotiation(mmw.getDisablePriceNegotiation())
				.setCheckinContactName(mmw.getCheckinContactName())
				.setCheckinContactPhone(mmw.getCheckinContactPhone())
				.setAssignToFirstResource(mmw.getAssignToFirstResource() || resourceAssignToFirstToAccept)
				.setShowInFeed(mmw.getShowInFeed())
				.setUseRequirementSets(mmw.getUseRequirementSets())
		);
		if (!work.isTemplateSet()) {
			response.getWork().setCheckinCallRequired(StringUtilities.all(mmw.getCheckinContactName(), mmw.getCheckinContactPhone()));
		}
		response.getWork().setResourceConfirmationRequired(work.getResourceConfirmation());
		if (work.getResourceConfirmationHours() != null) {
			response.getWork().setResourceConfirmationHours(work.getResourceConfirmationHours());
		}
		response.getWork().setTimetrackingRequired(work.isRequireTimetracking());
	}

	void buildRatingsForWork(WorkResponse response, com.workmarket.domains.model.User currentUser, AbstractWork work) {

		com.workmarket.domains.model.WorkResource resource = workResourceService.findActiveWorkResource(work.getId());
		if (resource == null) return;

		com.workmarket.domains.model.rating.Rating rating;

		rating = ratingDAO.findLatestForUserForWork(work.getBuyer().getId(), work.getId());

		if (rating != null && (rating.isRatingSharedFlag() || rating.getRatingUser().equals(currentUser))) {
			response.setBuyerRatingForWork(rating);
		}

		rating = ratingDAO.findLatestForUserForWork(resource.getUser().getId(), work.getId());

		if (rating != null && (rating.isRatingSharedFlag() || (userRoleService.isAdminOrManager(currentUser) && rating.getRatingCompany().equals(currentUser.getCompany())))) {
			response.setResourceRatingForWork(rating);
			response.setLastRatingBuyerFullName(rating.getRatingUser().getFullName());
		}
	}

	void buildRoutingStrategies(WorkResponse response, AbstractWork work) {
		for (AbstractRoutingStrategy s : routingStrategyService.findAllRoutingStrategiesByWork(work.getId())) {
			RoutingStrategy routingStrategy = new RoutingStrategy()
				.setId(s.getId())
				.setDelayMinutes(s.getDelayMinutes())
				.setStatus(objectFactory.newStatus(s.getDeliveryStatus()));

			if (s.getRoutedOn() != null) {
				routingStrategy
					.setRoutedOn(s.getRoutedOn().getTimeInMillis())
					.setSummary(
						new RoutingStrategySummary()
							.setSent(s.getSummary().getSent())
							.setFailed(s.getSummary().getFailed())
							.setFailedFunds(s.getSummary().getFailedInsufficientFunds())
							.setFailedSpendLimit(s.getSummary().getFailedInsufficientSpendLimit())
							.setFailedCredit(s.getSummary().getFailedPaymentTermsCreditLimit())
							.setFailedValidation(s.getSummary().getFailedIllegalState())
					);
			}

			response.getWork().addToRoutingStrategies(routingStrategy);
		}
	}

	@SuppressWarnings("unchecked")
	public void buildRequirementSetIds(WorkResponse response, AbstractWork work) {
		response.getWork().setRequirementSetIds((List<Long>) CollectionUtils.collect(work.getRequirementSets(), new BeanToPropertyValueTransformer("id")));
	}

	void buildFollowers(WorkResponse response, AbstractWork work) {
		for (WorkFollow workFollow : workFollowService.getWorkFollowers(work.getId())) {
			response.getWork().addFollower(workFollow.getUser().getId());
		}
	}

	void buildGroups(WorkResponse response, AbstractWork work) {
		for (WorkGroupAssociation association : groupDAO.findAllByWork(work.getId())) {
			if (association.isAssignToFirstToAccept()) {
				response.getWork().addFirstToAcceptGroup(association.getGroup().getId());
			} else {
				response.getWork().addNeedToApplyGroup(association.getGroup().getId());
			}
		}
	}

	void buildDeliverables(WorkResponse response, AbstractWork work) {
		Work tWork = response.getWork();
		if (tWork == null) {
			return;
		}

		DeliverableRequirementGroup deliverableRequirementGroup = work.getDeliverableRequirementGroup();
		if (deliverableRequirementGroup == null) {
			tWork.setDeliverableRequirementGroupDTO(null);
			return;
		}

		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = new DeliverableRequirementGroupDTO(deliverableRequirementGroup);
		tWork.setDeliverableRequirementGroupDTO(deliverableRequirementGroupDTO);
		response.setWork(tWork);
	}
}
