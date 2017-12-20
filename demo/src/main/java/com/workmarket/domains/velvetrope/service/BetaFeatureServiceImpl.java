package com.workmarket.domains.velvetrope.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.velvetrope.dao.AdmissionDAO;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.velvetrope.model.BetaFeatureAdmissionRequest;
import com.workmarket.domains.velvetrope.model.CompanyAdmissionDTO;
import com.workmarket.domains.velvetrope.model.SoleProprietorAdmissionDTO;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.velvetrope.Venue;
import com.workmarket.velvetrope.Venue.VenueType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

@Service
public class BetaFeatureServiceImpl implements BetaFeatureService {

	private static final String COMPANY_ID_KEY = "companyId";

	@Resource private AdmissionDAO admissionDao;
	@Resource private AdmissionService admissionService;
	@Resource private UserService userService;
	@Resource private CompanyService companyService;
	@Resource private UserRoleService userRoleService;

	@Override
	public List<Admission> getAllCompanyBetaFeatureAdmissions() {
		return admissionDao.findAllAdmissionsByKeyNameForVenue(COMPANY_ID_KEY, Venue.getBetaFeatureVenueArray());
	}

	@Override
	public Map<Venue, List<Admission>> getVenuesAndAdmissionsForBetaFeatures() {
		Set<Venue> betaFeatureVenues = Venue.getBetaFeatureVenueSet();
		List<Admission> admissions = getAllCompanyBetaFeatureAdmissions();
		Map<Venue, List<Admission>> betaFeaturesAndAdmissions = new HashMap<>();
		for (Venue venue : betaFeatureVenues) {
			List<Admission> venueAdmissions = filter(having(on(Admission.class).getVenue(), equalTo(venue)), admissions);
			betaFeaturesAndAdmissions.put(venue, venueAdmissions);
		}
		return betaFeaturesAndAdmissions;
	}

	@Override
	public ServiceResponseBuilder getBetaFeaturesResponseBuilder() {
		Map<String, Object> data = getBetaFeaturesResponseData();
		return getSuccessServiceResponseBuilder(data);
	}

	@Override
	public Map<String, Object> getBetaFeaturesResponseData() {
		List<Admission> admissions = getAllCompanyBetaFeatureAdmissions();
		Set<Long> allCompanyIds = ImmutableSet.copyOf(extract(admissions, on(Admission.class).getLongValue()));
		Map<Long, User> soleProprietorsByCompanyId = userService.getSoleProprietorsByCompanyId(allCompanyIds);
		Set<Long> soleProprietorsCompanyIds = soleProprietorsByCompanyId.keySet();
		Set<Long> nonSoleProprietorCompanyIds = Sets.newHashSet(allCompanyIds);
		nonSoleProprietorCompanyIds.removeAll(soleProprietorsCompanyIds);

		Map<Venue, List<Admission>> betaFeaturesAndAdmissions = getVenuesAndAdmissionsForBetaFeatures();
		Map<Long, Company> companiesById = companyService.getCompaniesByIds(nonSoleProprietorCompanyIds);

		Map<String, Object> data = new HashMap<>();
		for (Entry<Venue, List<Admission>> venueAndAdmissions : betaFeaturesAndAdmissions.entrySet()) {
			Map<String, Object> venueAndAdmissionsData = getVenueAndAdmissionsData(venueAndAdmissions, companiesById, soleProprietorsByCompanyId);
			data.put(venueAndAdmissions.getKey().getDisplayName(), venueAndAdmissionsData);
		}
		return data;
	}

	@Override
	public void updateFeatureParticipation(BetaFeatureAdmissionRequest betaFeatureAdmissionRequest) {
		Venue betaFeature = betaFeatureAdmissionRequest.getBetaFeature();
		Set<Long> participatingCompanyIds = getParticipatingCompanyIds(betaFeature);
		Set<Long> updatedCompanyIds = ImmutableSet.copyOf(betaFeatureAdmissionRequest.getCompanyIds());
		Set<Long> companyIdsToRemove = new HashSet(participatingCompanyIds);
		companyIdsToRemove.removeAll(updatedCompanyIds);
		Set<Long> companyIdsToAdd = new HashSet(updatedCompanyIds);
		companyIdsToAdd.removeAll(participatingCompanyIds);
		turnFeatureOff(betaFeature, companyIdsToRemove);
		turnFeatureOn(betaFeature, companyIdsToAdd);
	}

	private Set<Long> getParticipatingCompanyIds(Venue betaFeature) {
		List<Admission> admissions = admissionService.findAllAdmissionsByKeyNameForVenue(COMPANY_ID_KEY, betaFeature);
		return ImmutableSet.copyOf(extract(admissions, on(Admission.class).getLongValue()));
	}

	@Override
	public void turnFeatureOn(Venue betaFeature, Set<Long> companyIds) {
		for (Long companyId : companyIds) {
			admissionService.saveAdmissionForCompanyIdAndVenue(companyId, betaFeature);
		}
	}

	@Override
	public void turnFeatureOff(Venue betaFeature, Set<Long> companyIds) {
		for (Long companyId : companyIds) {
			admissionService.destroyAdmissionForCompanyIdAndVenue(companyId, betaFeature);
		}
	}

	@Override
	public boolean canToggleOwnCompanyBetaFeatureParticipation(long userId) {
		User user = userService.getUser(userId);
		return userRoleService.isAdminOrManager(user);
	}

	private ServiceResponseBuilder getSuccessServiceResponseBuilder(Map<String, Object> data) {
		ServiceResponseBuilder serviceResponseBuilder = new ServiceResponseBuilder();
		serviceResponseBuilder.setData(data);
		return serviceResponseBuilder.setSuccessful(true);
	}

	private Map<String, Object> getVenueAndAdmissionsData(Entry<Venue, List<Admission>> venueAndAdmissions,
	                                                      Map<Long, Company> companiesById,
	                                                      Map<Long, User> soleProprietorsByCompanyId) {

		Map<String, Object> venueAndAdmissionsData = new HashMap<>();
		venueAndAdmissionsData.put("venue", venueAndAdmissions.getKey());
		boolean isOpenSignup = (venueAndAdmissions.getKey().getVenueType() == VenueType.OPEN_SIGNUP_BETA_FEATURE);
		venueAndAdmissionsData.put("isOpenSignup", isOpenSignup);
		List<CompanyAdmissionDTO> companyAdmissionDTOs = getCompanyAdmissionDTOs(venueAndAdmissions, companiesById);
		venueAndAdmissionsData.put("companies", companyAdmissionDTOs);
		List<SoleProprietorAdmissionDTO> soleProprietorAdmissionDTOs =
			getSoleProprietorAdmissionDTOs(venueAndAdmissions, soleProprietorsByCompanyId);
		venueAndAdmissionsData.put("soleProprietors", soleProprietorAdmissionDTOs);
		return venueAndAdmissionsData;
	}

	private List<CompanyAdmissionDTO> getCompanyAdmissionDTOs(Entry<Venue, List<Admission>> venueAndAdmissions, Map<Long, Company> companiesById) {
		List<CompanyAdmissionDTO> companyAdmissionDTOs = new ArrayList<>();
		for (Admission admission : venueAndAdmissions.getValue()) {
			Long companyId = Long.valueOf(admission.getValue());
			if (companiesById.containsKey(companyId)) {
				CompanyAdmissionDTO dto = new CompanyAdmissionDTO(companiesById.get(companyId), admission);
				companyAdmissionDTOs.add(dto);
			}
		}
		return companyAdmissionDTOs;
	}

	private List<SoleProprietorAdmissionDTO> getSoleProprietorAdmissionDTOs(Entry<Venue, List<Admission>> venueAndAdmissions, Map<Long, User> usersByCompanyId) {
		List<SoleProprietorAdmissionDTO> soleProprietorAdmissionDTOs = new ArrayList<>();
		for (Admission admission : venueAndAdmissions.getValue()) {
			Long companyId = Long.valueOf(admission.getValue());
			if (usersByCompanyId.containsKey(companyId)) {
				SoleProprietorAdmissionDTO dto = new SoleProprietorAdmissionDTO(usersByCompanyId.get(companyId), companyId, admission);
				soleProprietorAdmissionDTOs.add(dto);
			}
		}
		return soleProprietorAdmissionDTOs;
	}
}
