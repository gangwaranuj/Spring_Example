package com.workmarket.web.helpers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.model.Pagination;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.project.ProjectPagination;
import com.workmarket.service.business.CRMService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.domains.work.service.project.ProjectService;
import com.workmarket.domains.work.service.state.WorkStatusService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.dto.StateDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Helpers for pulling data used to populate common form select options.
 */
@Component
public class FormOptionsDataHelper implements InitializingBean {

	@Autowired private CRMService crmService;
	@Autowired private InvariantDataService invariantService;
	@Autowired private ProjectService projectService;
	@Autowired private ProfileService profileService;
	@Autowired private WorkStatusService workStatusService;
	@Autowired private IndustryService industryService;

	private final static String[] WORK_STATUS_TYPE_CODES_FOR_LIST = new String[] {
		WorkStatusType.ACTIVE,
		WorkStatusType.CANCELLED,
		WorkStatusType.COMPLETE,
		WorkStatusType.DECLINED,
		WorkStatusType.DRAFT,
		WorkStatusType.EXCEPTION,
		WorkStatusType.INPROGRESS,
		WorkStatusType.PAID,
		WorkStatusType.PAYMENT_PENDING,
		WorkStatusType.REFUNDED,
		WorkStatusType.SENT,
		WorkStatusType.VOID,
	};

	private static Map<String,String> WORK_STATUS_TYPES_FOR_LIST = null;
	private static final Map<String,String> LANE_TYPES = Maps.newHashMapWithExpectedSize(2);

	static {
		LANE_TYPES.put("1", "Employees");
		LANE_TYPES.put("23", "Resources");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WORK_STATUS_TYPES_FOR_LIST = Maps.newLinkedHashMap();
		for (String c : WORK_STATUS_TYPE_CODES_FOR_LIST) {
			WorkStatusType t = workStatusService.findWorkStatusTypeByCode(c);
			if (t == null) {
				// TODO Resolve against message.properties
				String description = "";
				if (c.equals(WorkStatusType.INPROGRESS)) {
					description = "In Progress";
				}
				WORK_STATUS_TYPES_FOR_LIST.put(c, description);
			} else {
				WORK_STATUS_TYPES_FOR_LIST.put(t.getCode(), t.getDescription());
			}
		}
	}

	public Map<Long, String> getClients(ExtendedUserDetails currentUser) {
		List<ClientCompany> clients = crmService.findAllClientCompanyByCompany(currentUser.getCompanyId());
		return CollectionUtilities.extractKeyValues(clients, "id", "name");
	}

	public Map<Long, String> getProjects(ExtendedUserDetails currentUser) {
		ProjectPagination projectPagination = new ProjectPagination(true);
		projectPagination.setSortColumn(ProjectPagination.SORTS.NAME);
		projectPagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		projectPagination = projectService.findProjectsForCompany(currentUser.getCompanyId(), projectPagination);
		List<Project> projects = projectPagination.getResults();
		return CollectionUtilities.extractKeyValues(projects, "id", "name");
	}

	public Map<Long, String> getEnabledProjects(ExtendedUserDetails currentUser) {
		ProjectPagination projectPagination = new ProjectPagination(true);
		projectPagination.setSortColumn(ProjectPagination.SORTS.NAME);
		projectPagination.setSortDirection(Pagination.SORT_DIRECTION.ASC);
		projectPagination = projectService.findProjectsForCompany(currentUser.getCompanyId(), projectPagination);
		List<Project> projects = Lists.newArrayList();
		for(Project project: projectPagination.getResults()) {
			if(project.isReservedFundsEnabled())
				projects.add(project);
		}
		return CollectionUtilities.extractKeyValues(projects, "id", "name");
	}

	public Map<Long, String> getUsers(ExtendedUserDetails currentUser) {
		return profileService.findAllUsersByCompanyId(currentUser.getCompanyId());
	}

	public Map<Long, String> getActiveUsers(ExtendedUserDetails currentUser) {
		return profileService.findAllActiveUsersByCompanyId(currentUser.getCompanyId());
	}

	public Map<Long, String> getIndustries() {
		List<IndustryDTO> industries = industryService.getAllIndustryDTOs();
		return CollectionUtilities.extractKeyValues(industries, "id", "name");
	}

	public Map<String, String> getLanes() {
		return LANE_TYPES;
	}

	public Map<String, String> getWorkStatusTypes() {
		return WORK_STATUS_TYPES_FOR_LIST;
	}

	public Map<Long, String> getStates() {
		return CollectionUtilities.extractKeyValues(invariantService.getStateDTOs(), "shortName", "name");
	}

	public Map<Long, String> getStates(String countryId) {
		return CollectionUtilities.extractKeyValues(invariantService.getStates(countryId), "shortName", "name");
	}

	public Map<String, Map<String, String>> getStatesAsOptgroup() {

		List<StateDTO> states = invariantService.getStateDTOs();
		Map<String, Map<String, String>> result = Maps.newHashMap();

		for (StateDTO s : states) {
			if (!result.containsKey(s.getCountry())) {
				result.put(s.getCountry(), Maps.<String, String>newTreeMap());
			}
			result.get(s.getCountry()).put(s.getName(), s.getShortName());
		}

		return result;
	}

	public Map<String, String> getCountries() {
		return CollectionUtilities.extractKeyValues(invariantService.getCountries(), "id", "name");
	}

	public Map<String, String> getAllCountries() {
		return invariantService.getAllCountries();
	}

	public Map<Long, String> getLocationTypes() {
		return CollectionUtilities.extractKeyValues(invariantService.getLocationTypeDTOs(), "id", "description");
	}

	public Map<Long, String> getDressCodes() {
		return CollectionUtilities.extractKeyValues(crmService.findAllDressCodes(), "id", "description");
	}

	public Map<Long, String> getFollowers(Long companyId, Collection<Long> excludeIds) {
		Map<Long, String> users = profileService.findAllActiveUsersByCompanyId(companyId);
		if (MapUtils.isNotEmpty(users)) {
			for (Long id: excludeIds) {
				users.remove(id);
			}
		}
		return users;
	}
}
