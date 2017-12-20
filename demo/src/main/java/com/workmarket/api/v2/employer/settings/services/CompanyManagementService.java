package com.workmarket.api.v2.employer.settings.services;

import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.skill.Skill;

import java.util.List;

public interface CompanyManagementService {
	public abstract Location saveCompanyLocation(Long companyId, LocationDTO locationDTO);

	public abstract void addCompanyLocation(Long companyId, Long locationId);

	public abstract void removeCompanyLocation(Long companyId, Long locationId);

	List<Location> getCompanyLocations(Long companyId);

	void setCompanyLocations(List<Long> locationIds, Long companyId);

	public abstract void addCompanySkill(Long companyId, Long skillId);

	public abstract void removeCompanySkill(Long companyId, Long skillId);

	public abstract List<Skill> getCompanySkills(Long companyId);

	public abstract void setCompanySkills(List<Long> skillIds, Long companyId);

	State findStateWithCountryAndState(String country, String state);
}
