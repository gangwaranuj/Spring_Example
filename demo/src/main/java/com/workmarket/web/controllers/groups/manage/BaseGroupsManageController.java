package com.workmarket.web.controllers.groups.manage;

import com.workmarket.service.business.AssessmentService;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.ContractService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.MessagingService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.RequestService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.search.group.GroupSearchService;
import com.workmarket.service.search.SearchService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseGroupsManageController extends BaseController {

	@Autowired private UserGroupService groupService;
	@Autowired private GroupSearchService groupSearchService;
	@Autowired private CompanyService companyService;
	@Autowired private ProfileService profileService;
	@Autowired private InvariantDataService invariantService;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private FormOptionsDataHelper formDataHelper;
	@Autowired private MessagingService messagingService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private ContractService contractService;
	@Autowired private AssetManagementService assetService;
	@Autowired private CRMService crmService;
	@Autowired private ProjectService projectService;
	@Autowired protected RequestService requestService;
	@Autowired private SearchService peopleSearchService;
	@Autowired protected EventRouter eventRouter;
	@Autowired protected EventFactory eventFactory;
	@Autowired protected IndustryService industryService;

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public FormOptionsDataHelper getFormDataHelper() {
		return formDataHelper;
	}

	public void setFormDataHelper(FormOptionsDataHelper formDataHelper) {
		this.formDataHelper = formDataHelper;
	}

	public UserGroupService getGroupService() {
		return groupService;
	}

	public GroupSearchService getGroupSearchService() {
		return groupSearchService;
	}

	public void setGroupService(UserGroupService groupService) {
		this.groupService = groupService;
	}

	public InvariantDataService getInvariantService() {
		return invariantService;
	}

	public void setInvariantService(InvariantDataService invariantService) {
		this.invariantService = invariantService;
	}

	public MessageBundleHelper getMessageHelper() {
		return messageHelper;
	}

	public void setMessageHelper(MessageBundleHelper messageHelper) {
		this.messageHelper = messageHelper;
	}

	public MessagingService getMessagingService() {
		return messagingService;
	}

	public void setMessagingService(MessagingService messagingService) {
		this.messagingService = messagingService;
	}

	public AssessmentService getAssessmentService() {
		return assessmentService;
	}

	public void setAssessmentService(AssessmentService assessmentService) {
		this.assessmentService = assessmentService;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public AssetManagementService getAssetService() {
		return assetService;
	}

	public CRMService getCrmService() {
		return crmService;
	}

	public void setCrmService(CRMService crmService) {
		this.crmService = crmService;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public SearchService getPeopleSearchService() {
		return peopleSearchService;
	}

	public void setPeopleSearchService(SearchService peopleSearchService) {
		this.peopleSearchService = peopleSearchService;
	}

	public RequestService getRequestService() {
		return requestService;
	}

	public void setRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	public IndustryService getIndustryService() {
		return industryService;
	}

	public void setIndustryService(IndustryService industryService) {
		this.industryService = industryService;
	}
}
