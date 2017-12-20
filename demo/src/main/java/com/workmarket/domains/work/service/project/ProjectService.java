package com.workmarket.domains.work.service.project;

import com.google.common.collect.ImmutableList;
import com.workmarket.api.v2.model.ProjectApiDTO;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.service.business.dto.ProjectDTO;
import com.workmarket.service.infra.security.RequestContext;

import java.util.List;
import java.util.Map;

public interface ProjectService {
	Project findById(Long projectId);

	void saveOrUpdate(Project project);
	void activateProject(Long projectId);
	void deactivateProject(Long projectId);
	void deleteProject(Long projectId);

	/**
	 * Returns all projects that are owned by the company.
	 *
	 * @param companyId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	ProjectPagination findProjectsForCompany(Long companyId, ProjectPagination pagination) ;

	List<Project> findReservedFundsEnabledProjectsForCompany(Long companyId);

	/**
	 * Returns all projects that are related to particular client company.
	 *
	 * @param clientCompanyId
	 * @return project pagination
	 */
	ProjectPagination findAllProjectsForClientCompany(Long companyId, Long clientCompanyId, ProjectPagination pagination);

	/**
	 * Get the current user's authorization context for the requested entity.
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	List<RequestContext> getRequestContext(Long projectId);

	// Projects
	Project saveOrUpdateProject(Long userId, ProjectDTO projectDTO);

	Project saveOrUpdateProject(ProjectApiDTO projectApiDTO);

	void addWorkToProject(Long workId, Long projectId);

	void addWorksToProject(List<Long> workIds, Long projectId);

	boolean doesProjectHaveImmediatePaymentWorkInProgress(Long projectId);

	void resetAllProjectBudget(Long companyId);

	Project findByWorkId(Long workId);

	ImmutableList<Map> getProjectedProjects(String[] fields) throws Exception;

	ImmutableList<Map> getProjectedProjectsByClientCompany(long clientCompanyId, String[] fields) throws Exception;
}
