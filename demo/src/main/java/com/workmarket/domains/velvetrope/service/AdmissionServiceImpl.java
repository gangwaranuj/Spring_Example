package com.workmarket.domains.velvetrope.service;

import com.workmarket.dao.changelog.company.CompanyChangeLogDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.model.changelog.company.CompanyAddFeatureChangeLog;
import com.workmarket.domains.model.changelog.company.CompanyRemoveFeatureChangeLog;
import com.workmarket.domains.velvetrope.dao.AdmissionDAO;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PlanService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdmissionServiceImpl implements AdmissionService {

	private static final String COMPANY_ID_KEY = "companyId";
	private static final String PLAN_ID_KEY = "planId";

	@Autowired private AdmissionDAO dao;
	@Autowired private TokenService tokenService;
	@Autowired private PlanService planService;
	@Autowired private CompanyChangeLogDAO companyChangeLogDAO;
	@Autowired private CompanyService companyService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	public List<Admission> findAllAdmissionsForCompanyId(Long companyId) {
		return dao.findAllBy(
			"value", String.valueOf(companyId),
			"keyName", COMPANY_ID_KEY,
			"deleted", false
		);
	}

	@Override
	public List<Admission> findAllAdmissionsForPlanId(Long planId) {
		return dao.findAllBy(
			"value", String.valueOf(planId),
			"keyName", PLAN_ID_KEY,
			"deleted", false
		);
	}

	@Override
	public List<Admission> findAllAdmissionsByCompanyIdForVenue(Long companyId, Venue... venues) {
		return dao.findAllAdmissionsByCompanyIdForVenue(companyId, COMPANY_ID_KEY, venues);
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameForVenue(String keyName, Venue... venues) {
		return dao.findAllAdmissionsByKeyNameForVenue(keyName, venues);
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameForVenueExcludingVenue(String keyName, Venue includedVenue, Venue excludedVenue) {
		return dao.findAllAdmissionsByKeyNameForVenueExcludingVenue(keyName, includedVenue, excludedVenue);
	}

	@Override
	public List<Admission> findAllAdmissionsByKeyNameExcludingVenueForVenues(String keyName, Venue excludedVenue, Venue... includedVenues) {
		return dao.findAllAdmissionsByKeyNameExcludingVenueForVenues(keyName, excludedVenue, includedVenues);
	}

	@Override
	@Transactional
	public void saveAdmissionForCompanyIdAndVenue(Long companyId, Venue venue) {
		if (venue.isSystemVenue()) {
			return;
		}

		Admission admission = getOrInitializeBy(companyId, COMPANY_ID_KEY, venue);
		admission.setDeleted(false);
		dao.saveOrUpdate(admission);
		tokenService.deleteTokenFor(companyId);

		logAddFeature(companyId, venue);
	}

	@Override
	public void saveAdmissionsForPlanId(Long planId, List<Admission> admissions) {
		for (Admission admission : admissions) {
			admission = getOrInitializeBy(planId, PLAN_ID_KEY, admission.getVenue());
			admission.setDeleted(false);
			dao.saveOrUpdate(admission);

			if (admission.getKeyName() == COMPANY_ID_KEY) {
				logAddFeature(admission.getLongValue(), admission.getVenue());
			}
		}
	}

	@Override
	@Transactional
	public void destroyAdmissionForCompanyIdAndVenue(Long companyId, Venue venue) {
		Admission admission = getOrInitializeBy(companyId, COMPANY_ID_KEY, venue);
		admission.setDeleted(true);
		dao.saveOrUpdate(admission);

		logRemoveFeature(companyId, venue);
		tokenService.deleteTokenFor(companyId);
	}

	@Override
	public void destroyAdmissionsForCompanyId(long companyId) {
		List<Admission> company_admissions = findAllAdmissionsForCompanyId(companyId);

		for (Admission admission : company_admissions) {
			admission.setDeleted(true);
			dao.saveOrUpdate(admission);

			logRemoveFeature(companyId, admission.getVenue());
		}

		tokenService.deleteTokenFor(companyId);
	}

	@Override
	public void destroyAdmissionsForPlanId(Long planId) {
		List<Admission> plan_admissions = findAllAdmissionsForPlanId(planId);

		for (Admission admission : plan_admissions) {
			admission.setDeleted(true);
			dao.saveOrUpdate(admission);

			if (admission.getKeyName() == COMPANY_ID_KEY) {
				logRemoveFeature(admission.getLongValue(), admission.getVenue());
			}
		}
	}

	@Override
	public void grantAdmissionsForCompanyIdByPlanCode(Long companyId, String planCode) {
		if (StringUtils.isBlank(planCode)) { return; }

		Plan plan = planService.find(planCode);
		if (plan == null) { return; }

		List<Admission> admissions = findAllAdmissionsForPlanId(plan.getId());
		for (Admission admission : admissions) {
			for(Venue venue : admission.getProvidedVenues()) {
				saveAdmissionForCompanyIdAndVenue(companyId, venue);
			}
		}
	}

	private void logAddFeature(Long companyId, Venue venue) {
		Company company = companyService.findCompanyById(companyId);
		companyChangeLogDAO.saveOrUpdate(
			new CompanyAddFeatureChangeLog(
				company,
				authenticationService.getCurrentUser(),
				authenticationService.getMasqueradeUser(),
				venue.getDisplayName()
			)
		);
	}

	private void logRemoveFeature(long companyId, Venue venue) {
		Company company = companyService.findCompanyById(companyId);
		companyChangeLogDAO.saveOrUpdate(
			new CompanyRemoveFeatureChangeLog(
				company,
				authenticationService.getCurrentUser(),
				authenticationService.getMasqueradeUser(),
				venue.getDisplayName()
			)
		);
	}

	private Admission getOrInitializeBy(Long value, String keyName, Venue venue) {
		return dao.getOrInitializeBy(
			"value", String.valueOf(value),
			"keyName", keyName,
			"venue", venue
		);
	}
}
