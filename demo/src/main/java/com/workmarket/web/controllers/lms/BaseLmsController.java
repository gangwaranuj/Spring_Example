package com.workmarket.web.controllers.lms;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.dao.assessment.AssessmentGroupAssociationDAO;
import com.workmarket.dao.requirement.TestRequirementDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AssessmentStatusType;
import com.workmarket.domains.model.assessment.ManagedAssessment;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.thrift.assessment.AssessmentRequest;
import com.workmarket.thrift.assessment.AssessmentRequestInfo;
import com.workmarket.thrift.assessment.AssessmentResponse;
import com.workmarket.thrift.assessment.AssessmentServiceFacade;
import com.workmarket.thrift.assessment.AuthorizationContext;
import com.workmarket.thrift.assessment.RequestContext;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.utility.ModelEnumUtilities;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.exceptions.HttpException401;
import com.workmarket.web.exceptions.HttpException404;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

public class BaseLmsController extends BaseController {

	protected static final Integer MYTESTS_PAGINATION_MAX_RESULTS = 20;
	protected static final Integer MANAGE_PAGINATION_MAX_RESULTS = 24;
	protected static final Integer BROWSE_PAGINATION_MAX_RESULTS = 20;

	private static final Logger logger = LoggerFactory.getLogger(BaseLmsController.class);

	@Autowired protected AssessmentService assessmentService;
	@Autowired protected AssessmentServiceFacade thriftAssessmentService;
	@Autowired protected CompanyService companyService;
	@Autowired protected MessageBundleHelper messageHelper;
	@Autowired protected TWorkFacadeService tWorkFacadeService;
	@Autowired protected TWorkService workService;
	@Autowired protected UserService userService;

	protected enum MYTESTS_TYPES {
		INVITATIONS("Invitations"), IN_PROGRESS("In Progress"), PASSED("Passed"), FAILED("Failed"), GRADE_PENDING("Grade Pending");

		private final String type;

		MYTESTS_TYPES(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	protected enum MANAGE_TYPES {
		ALL("All"), TESTS_I_OWN("Tests I Own"), ACTIVE("Active"), INACTIVE("Inactive");

		private final String type;

		MANAGE_TYPES(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	public static final Map<String, MANAGE_TYPES> manageTypes = ImmutableMap.of(
			MANAGE_TYPES.ALL.getType(), MANAGE_TYPES.ALL,
			MANAGE_TYPES.TESTS_I_OWN.getType(), MANAGE_TYPES.TESTS_I_OWN,
			MANAGE_TYPES.ACTIVE.getType(), MANAGE_TYPES.ACTIVE,
			MANAGE_TYPES.INACTIVE.getType(), MANAGE_TYPES.INACTIVE);

	public static final Map<String, MYTESTS_TYPES> myTestTypes = ImmutableMap.of(
			MYTESTS_TYPES.INVITATIONS.getType(), MYTESTS_TYPES.INVITATIONS,
			MYTESTS_TYPES.IN_PROGRESS.getType(), MYTESTS_TYPES.IN_PROGRESS,
			MYTESTS_TYPES.PASSED.getType(), MYTESTS_TYPES.PASSED,
			MYTESTS_TYPES.FAILED.getType(), MYTESTS_TYPES.FAILED,
			MYTESTS_TYPES.GRADE_PENDING.getType(), MYTESTS_TYPES.GRADE_PENDING);


	@ModelAttribute("AssessmentType")
	protected Map<String, Object> getAssessmentTypes() {
		return ModelEnumUtilities.assessmentTypes;
	}

	@ModelAttribute("AssessmentItemType")
	protected Map<String, Object> getAssessmentItemTypes() {
		return ModelEnumUtilities.assessmentItemTypes;
	}


	protected AssessmentResponse getAssessment(Long id, Long workId, User user, AuthorizationContext... contexts) {
		AssessmentRequest assessmentRequest = new AssessmentRequest();
		assessmentRequest.setUserId(user != null ? user.getId() : getCurrentUser().getId());
		assessmentRequest.setAssessmentId(id);
		assessmentRequest.setIncludes(
			Sets.newHashSet(
				AssessmentRequestInfo.CONTEXT_INFO,
				AssessmentRequestInfo.ITEM_INFO,
				AssessmentRequestInfo.STATISTICS_INFO,
				AssessmentRequestInfo.LATEST_ATTEMPT_INFO)
		);

		if (workId != null) {
			assessmentRequest.setWorkId(workId);
		}

		return getAndValidateAssessmentResponse(assessmentRequest, contexts);
	}

	protected void authorizeAssessment(Long id, Long workId, AuthorizationContext... contexts) {
		AssessmentRequest assessmentRequest = new AssessmentRequest();
		assessmentRequest.setUserId(getCurrentUser().getId());
		assessmentRequest.setAssessmentId(id);
		assessmentRequest.setIncludes(
			Sets.newHashSet(AssessmentRequestInfo.CONTEXT_INFO)
		);

		if (workId != null) {
			assessmentRequest.setWorkId(workId);
		}

		getAndValidateAssessmentResponse(assessmentRequest, contexts);
	}

	protected List<ManagedAssessment> setCompanyLogos(List<ManagedAssessment> assessments) {
		for (ManagedAssessment assessment : assessments) {
			CompanyAssetAssociation companyAvatar = companyService.findCompanyAvatars(assessment.getCompanyId());
			assessment.setCompanyLogo((companyAvatar != null ? companyAvatar.getAsset().getCdnUri() : null));
		}
		return assessments;
	}

	protected AssessmentResponse getAndValidateAssessmentResponse(AssessmentRequest request, AuthorizationContext... contexts) {
		AssessmentResponse assessmentResponse = null;
		try {
			assessmentResponse = thriftAssessmentService.findAssessment(request);
		} catch (Exception e) {
			logger.error("error loading assessment for userId={}, assessmentId={} and workId={}",
					new Object[]{request.getUserId(), request.getAssessmentId(), request.getWorkId()}, e);
		}

		if (assessmentResponse == null || AssessmentStatusType.REMOVED.equals(assessmentResponse.getAssessment().getStatus().getCode())) {
			throw new HttpException404().setRedirectUri("redirect:/lms/view");
		}

		if (!assessmentResponse.isAuthorized(contexts)) {
			throw new HttpException401().setRedirectUri("redirect:/lms/view");
		}

		if (assessmentResponse.shouldCheckIfInvited()) {
			// explicit invite
			boolean isUserInvited = CollectionUtils.containsAny(
				assessmentResponse.getRequestContexts(),
				Lists.newArrayList(RequestContext.RESOURCE, RequestContext.INVITED)
			);

			// implicit invite
			if (!isUserInvited) {
				long assessmentId = assessmentResponse.getAssessment().getId();
				Long userId = getCurrentUser().getId();
				isUserInvited = assessmentService.isUserAllowedToTakeAssessment(assessmentId, userId);
			}

			if (!isUserInvited) {
				throw new HttpException401().setRedirectUri("redirect:/lms/view");
			}
		}

		return assessmentResponse;
	}

	public AssessmentServiceFacade getThriftAssessmentService() {
		return thriftAssessmentService;
	}
}
