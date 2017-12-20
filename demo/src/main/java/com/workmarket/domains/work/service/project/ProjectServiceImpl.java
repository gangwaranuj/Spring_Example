package com.workmarket.domains.work.service.project;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.ProjectApiDTO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.domains.work.dao.project.ProjectDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.ProjectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static com.workmarket.domains.model.Pagination.SORT_DIRECTION.ASC;
import static com.workmarket.domains.work.model.project.ProjectPagination.SORTS.NAME;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired private AuthenticationService authenticationService;
	@Autowired private UserService userService;
	@Autowired private EventRouter eventRouter;
	@Autowired private ClientCompanyDAO clientCompanyDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private BaseWorkDAO workDAO;


	@Override
	public Project findById(Long projectId) {
		return projectDAO.findById(projectId);
	}

	@Override
	public void saveOrUpdate(Project project) {
		projectDAO.saveOrUpdate(project);
	}

	@Override
	public Project saveOrUpdateProject(Long userId, ProjectDTO projectDTO) {
		Assert.notNull(userId);
		Assert.notNull(projectDTO);
		Assert.notNull(projectDTO.getClientCompanyId(), "Client company id is required");

		User user = userService.getUser(userId);
		Assert.notNull(user, "Unable to find the user");
		Assert.notNull(user.getCompany(), "User must have a company");
		Project project = (projectDTO.getProjectId() == null) ? new Project() : projectDAO.get(projectDTO.getProjectId());

		project.setOwner(user);
		project.setDueDate(DateUtilities.getCalendarFromISO8601(projectDTO.getDueDateString()));

		if (projectDTO.getStartDateString() != null)
			project.setStartDate(DateUtilities.getCalendarFromISO8601(projectDTO.getStartDateString()));


		project.setClientCompany(clientCompanyDAO.get(projectDTO.getClientCompanyId()));
		project.setCompany(user.getCompany());

		project.setName(projectDTO.getName());
		project.setDescription(projectDTO.getDescription());
		project.setCode(projectDTO.getCode());
		project.setReservedFundsEnabled(projectDTO.isReservedFundsEnabled());

		project.setExpectedRevenue(projectDTO.getExpectedRevenue());
		project.setAnticipatedCost(projectDTO.getAnticipatedCost());
		project.setTargetMargin(projectDTO.getTargetMargin());

		projectDAO.saveOrUpdate(project);

		return project;
	}

	public Project saveOrUpdateProject(ProjectApiDTO projectApiDTO) {
		Assert.notNull(projectApiDTO);
		Assert.notNull(projectApiDTO.getOwnerId(), "Owner id is required");
		Assert.notNull(projectApiDTO.getClientCompanyId(), "Client company id is required");

		User user = userService.getUser(projectApiDTO.getOwnerId());
		Assert.notNull(user, "Unable to find the user");
		Assert.notNull(user.getCompany(), "User must have a company");
		Project project = (projectApiDTO.getProjectId() == null) ? new Project() : projectDAO.get(projectApiDTO.getProjectId());

		project.setOwner(user);
		project.setDueDate(DateUtilities.getCalendarFromISO8601(projectApiDTO.getDueDate()));

		project.setClientCompany(clientCompanyDAO.get(projectApiDTO.getClientCompanyId()));
		project.setCompany(user.getCompany());

		project.setName(projectApiDTO.getName());
		project.setDescription(projectApiDTO.getDescription());
		project.setReservedFundsEnabled(projectApiDTO.isReservedFundsEnabled());

		projectDAO.saveOrUpdate(project);

		return project;
	}

	@Override
	public void activateProject(Long projectId) {
		Assert.notNull(projectId);
		Project project = projectDAO.get(projectId);
		project.setActive(true);
	}

	@Override
	public void deactivateProject(Long projectId) {
		Assert.notNull(projectId);
		Project project = projectDAO.get(projectId);
		project.setActive(false);
	}

	@Override
	public void deleteProject(Long projectId) {
		Assert.notNull(projectId);
		Project project = projectDAO.get(projectId);
		project.setDeleted(true);
	}

	@Override
	public ProjectPagination findProjectsForCompany(Long companyId, ProjectPagination pagination) {
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return projectDAO.findByCompany(companyId, pagination);
	}

	@Override
	public List<Project> findReservedFundsEnabledProjectsForCompany(Long companyId) {
		Assert.notNull(companyId);
		ProjectPagination projectPagination = new ProjectPagination();
		projectPagination.setReturnAllRows();
		projectPagination.addFilter(ProjectPagination.FILTER_KEYS.RESERVED_FUNDS_ENABLED, "true");
		return projectDAO.findReservedFundsEnabledProjectByCompany(companyId, projectPagination).getResults();
	}

	@Override
	public ProjectPagination findAllProjectsForClientCompany(Long companyId, Long clientCompanyId, ProjectPagination pagination) {
		Assert.notNull(clientCompanyId);
		Assert.notNull(companyId);
		Assert.notNull(pagination);
		return projectDAO.findAllProjectsForClientCompany(companyId, clientCompanyId, pagination);
	}

	@Override
	public List<RequestContext> getRequestContext(Long projectId) {
		List<RequestContext> contexts = Lists.newArrayList(RequestContext.PUBLIC);
		User currentUser = authenticationService.getCurrentUser();
		Project project = findById(projectId);

		if (project.getCreatorId().equals(currentUser.getId())) {
			contexts.add(RequestContext.OWNER);
		} else if (project.getCompany().equals(currentUser.getCompany())) {
			contexts.add(RequestContext.COMPANY_OWNED);
		}

		return contexts;
	}

	@Override
	public void addWorkToProject(Long workId, Long projectId) {
		Assert.notNull(workId);
		Assert.notNull(projectId);
		AbstractWork work = workDAO.get(workId);
		Project project = findById(projectId);

		Assert.notNull(work);
		Assert.notNull(project);

		work.setProject(project);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workId));
	}

	@Override
	public void addWorksToProject(List<Long> workIds, Long projectId) {
		Assert.notNull(workIds);
		Assert.notNull(projectId);
		Project project = findById(projectId);
		Assert.notNull(project);
		for (Long workId : workIds) {
			AbstractWork work = workDAO.get(workId);
			Assert.notNull(work);
			work.setProject(project);
		}
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workIds));
	}

	@Override
	public boolean doesProjectHaveImmediatePaymentWorkInProgress(Long projectId) {
		return projectDAO.doesProjectHaveImmediatePaymentWorkInProgress(projectId);
	}

	@Override
	public void resetAllProjectBudget(Long companyId) {
		projectDAO.resetAllProjectBudget(companyId);
	}

	@Override
	public Project findByWorkId(Long workId) {
		Assert.notNull(workId);

		return projectDAO.findByWorkId(workId);
	}

	@Override
	public ImmutableList<Map> getProjectedProjects(String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		ProjectPagination pagination = new ProjectPagination(true);
		pagination.setSortColumn(NAME);
		pagination.setSortDirection(ASC);
		pagination = findProjectsForCompany(companyId, pagination);
		pagination.setProjection(fields);

		pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
		return ImmutableList.copyOf(pagination.getProjectionResults());
	}

	@Override
	public ImmutableList<Map> getProjectedProjectsByClientCompany(long clientCompanyId, String[] fields) throws Exception {
		Long companyId = authenticationService.getCurrentUserCompanyId();
		ProjectPagination pagination = new ProjectPagination(true);
		pagination.setSortColumn(NAME);
		pagination.setSortDirection(ASC);
		pagination = findAllProjectsForClientCompany(companyId, clientCompanyId, pagination);
		pagination.setProjection(fields);

		pagination.setProjectionResults(ProjectionUtilities.projectAsArray(pagination.getProjection(), pagination.getResults()));
		return ImmutableList.copyOf(pagination.getProjectionResults());
	}
}
