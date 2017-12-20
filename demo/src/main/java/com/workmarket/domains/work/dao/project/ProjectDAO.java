package com.workmarket.domains.work.dao.project;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.dto.SuggestionDTO;

import java.util.List;

public interface ProjectDAO extends DAOInterface<Project> {
	ProjectPagination findByCompany(Long companyId, ProjectPagination pagination);

	ProjectPagination findReservedFundsEnabledProjectByCompany(Long companyId, ProjectPagination pagination);

	Project findById(Long projectId);

	Project findByNameAndCompanyId(String name, Long companyId);

	Project findByNameCompanyAndClient(String name, Long companyId, Long clientId);

	Project findByWorkId(Long workId);

	ProjectPagination findAllProjectsForClientCompany(Long companyId, Long clientCompanyId, ProjectPagination pagination);

	boolean doesProjectHaveImmediatePaymentWorkInProgress(Long projectId);

	void resetAllProjectBudget(Long companyId);

	List<SuggestionDTO> suggest(String prefix, Long userId);
}
