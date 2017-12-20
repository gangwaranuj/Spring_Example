package com.workmarket.web.controllers.projects;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.UserStatusType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.editors.CalendarDateEditor;
import com.workmarket.web.editors.ClientCompanyEditor;
import com.workmarket.web.editors.UserEditor;
import com.workmarket.web.helpers.FormOptionsDataHelper;
import com.workmarket.web.models.MessageBundle;
import com.workmarket.web.validators.ProjectValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller("projectFormController")
@RequestMapping("/projects")
public class ProjectsFormController extends BaseProjectsController {

	private static final Log logger = LogFactory.getLog(ProjectsFormController.class);

	@Autowired private ProfileService profileService;
	@Autowired private CRMService crmService;
	@Autowired private CompanyService companyService;
	@Autowired private ProjectValidator projectValidator;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private FormOptionsDataHelper formOptionsDataHelper;
	@Autowired private UserEditor userEditor;
	@Autowired @Qualifier("clientCompanyEditor") private ClientCompanyEditor clientCompanyEditor;
	@Autowired private JsonSerializationService jsonSerializationService;

	private static final String VIEW_ADD = "web/pages/projects/add";
	private static final String VIEW_EDIT = "web/pages/projects/edit";
	private static final String NO_ACCESS = "redirect:/error/no_access";

	@InitBinder("project")
	public void initProjectBinder(WebDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(ClientCompany.class, clientCompanyEditor);
		binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		binder.registerCustomEditor(Calendar.class, new CalendarDateEditor(getCurrentUser().getTimeZoneId()));
	}

	@ModelAttribute("project")
	public Project populateProject(@RequestParam(value="id", required=false) Long id, HttpServletRequest request) {
		Project project = null;

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			if (id == null) {
				project = new Project();
			}
			else {
				authorize(id);
				project = projectService.findById(id);
			}
		}

		return project;
	}

	@ModelAttribute("users")
	public List<User> populateUsers() {
		return profileService.findAllUsersByCompanyId(getCurrentUser().getCompanyId(), Lists.newArrayList(UserStatusType.APPROVED));
	}

	@ModelAttribute("clientCompanies")
	public List<ClientCompany> populateClientCompanies() {
		try {
			return crmService.findAllClientCompanyByUser(getCurrentUser().getId());
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@RequestMapping(
		value = "/add",
		method = GET)
	public String add(Model model) {

		if (hasFeature("projectPermission")) {
			if (!authenticationService.hasProjectAccess(getCurrentUser().getId())) {
				return NO_ACCESS;
			}
		}

		Project project = new Project();
		Assert.notNull(getCurrentUser().getCompanyId());
		Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
		boolean reserveFundsEnabledFlag = company.getManageMyWorkMarket().getReserveFundsEnabledFlag();
		boolean budgetEnabledFlag = company.getManageMyWorkMarket().getBudgetEnabledFlag();
		model.addAttribute("project", project);
		model.addAttribute("reserveFundsEnabledFlag", reserveFundsEnabledFlag);
		model.addAttribute("budgetEnabledFlag", budgetEnabledFlag);
		model.addAttribute("industries", formOptionsDataHelper.getIndustries());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return VIEW_ADD;
	}

	@RequestMapping(
		value="/edit/{id}",
		method = GET)
	public String edit(@ModelAttribute("id") Long projectId, Model model) {

		if (hasFeature("projectPermission")) {
			if (!authenticationService.hasProjectAccess(getCurrentUser().getId())) {
				return NO_ACCESS;
			}
		}

		authorize(projectId);
		Project project = projectService.findById(projectId);
		boolean reserveFundsEnabledFlag = companyService.findCompanyById(getCurrentUser().getCompanyId()).getManageMyWorkMarket().getReserveFundsEnabledFlag();
		boolean budgetEnabledFlag = companyService.findCompanyById(getCurrentUser().getCompanyId()).getManageMyWorkMarket().getBudgetEnabledFlag();
		boolean canNotEdit = projectService.doesProjectHaveImmediatePaymentWorkInProgress(projectId);
		model.addAttribute("project", project);
		model.addAttribute("canNotEdit", canNotEdit);
		model.addAttribute("reserveFundsEnabledFlag", reserveFundsEnabledFlag);
		model.addAttribute("budgetEnabledFlag", budgetEnabledFlag);
		model.addAttribute("industries", formOptionsDataHelper.getIndustries());

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		return VIEW_EDIT;
	}

	@RequestMapping(
		value = "/add",
		method = POST)
	public String add(
		@RequestParam(value = "add_assignment", required = false) int addAssignmentCode,
		@ModelAttribute("project") Project project,
		BindingResult result,
		Model model,
		RedirectAttributes flash) {

		project.setRemainingBudget(project.getBudget());

		projectValidator.validate(project, result);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		if (result.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, result);
			return VIEW_ADD;
		}

		try {
			Company company = companyService.findCompanyById(getCurrentUser().getCompanyId());
			project.setCompany(company);
			projectService.saveOrUpdate(project);
		} catch (Exception ex) {
			logger.error("Error saving project.", ex);

			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.addError(messages, "projects.save.exception");

			return VIEW_ADD;
		}

		flash.addAttribute("projectId", project.getId());

		if (addAssignmentCode != 0) {
			return "redirect:/assignments/add?project_id={projectId}";
		} else {
			return "redirect:/projects/view/{projectId}";
		}
	}

	@RequestMapping(
		value = "/edit",
		method = POST)
	public String edit(
		@RequestParam(value = "add_assignment", required = false) int addAssignmentCode,
		@ModelAttribute("project") Project project,
		BindingResult result,
		Model model,
		RedirectAttributes flash) {

		Assert.notNull(project);
		authorize(project.getId());
		boolean reserveFundsEnabledFlag = companyService.findCompanyById(getCurrentUser().getCompanyId()).getManageMyWorkMarket().getReserveFundsEnabledFlag();
		boolean budgetEnabledFlag = companyService.findCompanyById(getCurrentUser().getCompanyId()).getManageMyWorkMarket().getBudgetEnabledFlag();
		boolean canNotEdit = projectService.doesProjectHaveImmediatePaymentWorkInProgress(project.getId());
		model.addAttribute("canNotEdit", canNotEdit);
		model.addAttribute("reserveFundsEnabledFlag", reserveFundsEnabledFlag);
		model.addAttribute("budgetEnabledFlag", budgetEnabledFlag);

		BigDecimal totalWorkValue = projectBudgetService.calcTotalWorkValue(project);

		Map<String, Object> context = CollectionUtilities.newObjectMap(
			"name", "projects",
			"data", CollectionUtilities.newObjectMap(),
			"features", CollectionUtilities.newObjectMap()
		);

		model.addAttribute("context", context);
		model.addAttribute("contextJson", jsonSerializationService.toJson(context));

		if (!project.getBudgetEnabledFlag()) {
			project.setBudget(BigDecimal.ZERO);
			project.setRemainingBudget(BigDecimal.ZERO);
		} else if(!projectService.findById(project.getId()).getBudgetEnabledFlag() && project.getBudgetEnabledFlag()) {
			if (project.getBudget().compareTo(totalWorkValue) == -1) {
				result.rejectValue("remainingBudget", "projects.remainingBudget.greaterThanZero");
				MessageBundle messages = messageHelper.newBundle(model);
				messageHelper.setErrors(messages, result);
				return VIEW_EDIT;
			} else {
				project.setRemainingBudget(project.getBudget().subtract(totalWorkValue));
			}
		} else {
			BigDecimal oldBudget = projectService.findById(project.getId()).getBudget();
			BigDecimal budgetChangedAmount = project.getBudget().subtract(oldBudget);
			project.setRemainingBudget(project.getRemainingBudget().add(budgetChangedAmount));
		}

		projectValidator.validate(project, result);

		if (result.hasErrors()) {
			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.setErrors(messages, result);
			return VIEW_EDIT;
		}

		try {
			projectService.saveOrUpdate(project);
		} catch (Exception ex) {
			logger.error("Error saving project.", ex);

			MessageBundle messages = messageHelper.newBundle(model);
			messageHelper.addError(messages, "projects.save.exception");

			return VIEW_EDIT;
		}

		flash.addAttribute("projectId", project.getId());

		if (addAssignmentCode != 0) {
			return "redirect:/assignments/add?project_id={projectId}";
		} else {
			return "redirect:/projects/view/{projectId}";
		}
	}
}
