package com.workmarket.api.v2.employer.settings.services;

import com.google.common.collect.Lists;
import com.workmarket.api.v2.model.LocationDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.LocationDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.company.CompanyLocationAssociationDAO;
import com.workmarket.dao.postalcode.StateDAO;
import com.workmarket.dao.skill.CompanySkillAssociationDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.skill.CompanyLocationAssociation;
import com.workmarket.domains.model.skill.CompanySkillAssociation;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CompanyManagementServiceImpl implements CompanyManagementService {
	private static final Log logger = LogFactory.getLog(CompanyManagementServiceImpl.class);

	@Autowired private CompanyService companyService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private AddressService addressService;

	@Autowired private CompanyDAO companyDAO;
	@Autowired private CompanyLocationAssociationDAO companyLocationAssociationDAO;
	@Autowired private CompanySkillAssociationDAO companySkillAssociationDAO;
	@Autowired private LocationDAO locationDAO;
	@Autowired private SkillDAO skillDAO;
	@Autowired private StateDAO stateDAO;

	@Override
	public Location saveCompanyLocation(Long companyId, LocationDTO locationDTO) {
		Assert.notNull(companyId);
		Assert.notNull(locationDTO);

		Address address = new Address();

		State state = invariantDataService.findStateWithCountryAndState(
			Country.valueOf(locationDTO.getCountry()).getId(), StringUtils.isEmpty(locationDTO.getState()) ? Constants.NO_STATE : locationDTO.getState()
		);
		if (state != null) {
			address.setState(state);
			address.setCountry(state.getCountry());
		} else {
			addressService.addNewStateToAddress(address, locationDTO.getCountry(), locationDTO.getState());
			address.setCountry(Country.valueOf(locationDTO.getCountry()));
		}

		address.setState(state);
		address.setAddressType(new AddressType(AddressType.SERVICE_AREA));
		address.setAddress1(locationDTO.getAddressLine1());
		address.setAddress2(locationDTO.getAddressLine2());
		address.setCity(locationDTO.getCity());
		address.setPostalCode(locationDTO.getZip());
		address.setCountry(Country.newInstance(locationDTO.getCountry()));
		if (locationDTO.getLatitude() != null && locationDTO.getLongitude() != null) {
			address.setLatitude(BigDecimal.valueOf(locationDTO.getLatitude()));
			address.setLongitude(BigDecimal.valueOf(locationDTO.getLongitude()));
		}


		Location location = new Location();
		location.setAddress(address);
		location.setCompany(companyService.findById(companyId));
		locationDAO.saveOrUpdate(location);
		return location;
	}

	@Override
	public void addCompanyLocation(Long companyId, Long locationId) {
		Assert.notNull(companyId);
		Assert.notNull(locationId);

		Company company = companyDAO.findCompanyById(companyId);
		Location location = locationDAO.findLocationById(locationId);
		companyLocationAssociationDAO.addCompanyLocation(location, company);
	}

	@Override
	public void removeCompanyLocation(Long companyId, Long locationId) {
		Assert.notNull(companyId);
		Assert.notNull(locationId);

		Company company = companyDAO.findCompanyById(companyId);
		Location location = locationDAO.findLocationById(locationId);
		companyLocationAssociationDAO.removeCompanyLocation(location, company);
	}

	@Override
	public List<Location> getCompanyLocations(Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		return companyLocationAssociationDAO.findCompanyLocations(company);
	}

	@Override
	public void setCompanyLocations(List<Long> locationIds, Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		List<CompanyLocationAssociation> locationsServiced = companyLocationAssociationDAO.findCompanyLocationAssociations(company);

		List<Long> newLocationIds = Lists.newArrayList(locationIds);

		for (CompanyLocationAssociation locationServiced : locationsServiced) {
			if (newLocationIds.contains(locationServiced.getLocation().getId())) {
				locationServiced.setDeleted(false);
				newLocationIds.remove(Long.valueOf(locationServiced.getLocation().getId()));
				if (newLocationIds.contains(locationServiced.getLocation().getId().intValue())) {
					locationServiced.setDeleted(false);
					newLocationIds.remove(Integer.valueOf(locationServiced.getLocation().getId().intValue()));
				} else {
					locationServiced.setDeleted(true);
				}
			}

			for (Long newLocationId : newLocationIds) {
				addCompanyLocation(companyId, newLocationId);
			}
		}
	}

	@Override
	public void addCompanySkill(Long companyId, Long skillId) {
		Assert.notNull(companyId);
		Assert.notNull(skillId);

		Company company = companyDAO.findCompanyById(companyId);
		Skill skill = skillDAO.findSkillById(skillId);
		companySkillAssociationDAO.addCompanySkill(skill, company);
	}

	@Override
	public void removeCompanySkill(Long companyId, Long skillId) {
		Assert.notNull(companyId);
		Assert.notNull(skillId);

		Company company = companyDAO.findCompanyById(companyId);
		Skill skill = skillDAO.findSkillById(skillId);
		companySkillAssociationDAO.removeCompanySkill(skill, company);
	}

	@Override
	public List<Skill> getCompanySkills(Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);
		return companySkillAssociationDAO.findCompanySkills(company);
	}

	@Override
	public void setCompanySkills(List<Long> skillIds, Long companyId) {
		Assert.notNull(companyId);
		Company company = companyDAO.findCompanyById(companyId);

		List<CompanySkillAssociation> skillAssociations = companySkillAssociationDAO.findCompanySkillAssociations(company);

		List<Long> newSkillIds = Lists.newArrayList(skillIds);

		for(CompanySkillAssociation skillAssociation : skillAssociations) {
			if(newSkillIds.contains(skillAssociation.getSkill().getId().intValue())) {
				skillAssociation.setDeleted(false);
				newSkillIds.remove(Integer.valueOf(skillAssociation.getSkill().getId().intValue()));
			} else {
				skillAssociation.setDeleted(true);
			}
		}

		for(Long newSkillId : newSkillIds) {
			addCompanySkill(companyId, newSkillId);
		}
	}

	@Override
	public State findStateWithCountryAndState(String country, String state) {
		if (StringUtils.isNotBlank(country) && StringUtils.isNotBlank(state)) {
			String countryId = Country.newInstance(country).getId();
			State provinceOrState = stateDAO.findStateWithCountryAndStateCode(countryId, state);
			if (provinceOrState == null) {
				return stateDAO.findStateWithCountryAndStateName(countryId, state);
			} else {
				return provinceOrState;
			}
		}
		return null;
	}
}
