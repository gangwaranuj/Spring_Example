package com.workmarket.service.business.onboarding;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.workmarket.common.core.RequestContext;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.CallingCode;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.CompanyEmployeeCountRangeEnum;
import com.workmarket.domains.model.Gender;
import com.workmarket.domains.model.ImageDTO;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.YearsOfExperienceEnum;
import com.workmarket.domains.model.asset.CompanyAssetAssociation;
import com.workmarket.domains.model.asset.UserAssetAssociation;
import com.workmarket.domains.model.postalcode.Country;
import com.workmarket.domains.model.postalcode.State;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.skill.SkillPagination;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.model.specialty.SpecialtyPagination;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.domains.onboarding.model.Qualification;
import com.workmarket.domains.onboarding.model.OnboardingSkillDTO;
import com.workmarket.domains.onboarding.model.WorkerOnboardingDTO;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.search.qualification.SearchRequest;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.infra.business.InvariantDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OnboardMappingServiceImpl implements OnboardMappingService {
	private static final Log logger = LogFactory.getLog(OnboardMappingServiceImpl.class);

	@Autowired InvariantDataService invariantDataService;
	@Autowired ProfileService profileService;
	@Autowired UserService userService;
	@Autowired CompanyService companyService;
	@Autowired IndustryService industryService;
	@Autowired AddressService addressService;
	@Autowired SkillService skillService;
	@Autowired SpecialtyService specialtyService;
	@Autowired QualificationClient qualificationClient;

	private Map<String, Object> createPhoneMap(String type, CallingCode code, String number) {
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("type", type);
		map.put("code", code != null ? code.getId() : null);
		map.put("number", number);
		return map;
	}

	@Override
	public Map<String, Object> mapProfile(final String fieldsAsCommaSeparated, final Profile profile, final Company company, final RequestContext context) {
		List<String> fields;

		if (fieldsAsCommaSeparated == null) {
			fields = WorkerOnboardingDTO.ALL_PROFILE_FIELDS;
		} else {
			fields = new ArrayList<>(Arrays.asList(fieldsAsCommaSeparated.split(",")));
		}

		final Map<String, Object> profileMap = Maps.newLinkedHashMap();
		final List<Map<String, Object>> phones = Lists.newArrayList();
		final List<Map<String, Object>> industries = Lists.newArrayList();
		User user = profile.getUser();
		Address address = addressService.findById(profile.getAddressId());
		Map<String, Object> m;

		for (String fld : fields) {
			switch (fld) {
				case WorkerOnboardingDTO.ID:
					profileMap.put(WorkerOnboardingDTO.ID, profile.getId());
					break;
				case WorkerOnboardingDTO.PHONES:
					if (profile.getMobilePhone() != null) {
						phones.add(createPhoneMap("mobile", profile.getMobilePhoneInternationalCode(), profile.getMobilePhone()));
					}
					if (profile.getWorkPhone() != null) {
						phones.add(createPhoneMap("work", profile.getWorkPhoneInternationalCode(), profile.getWorkPhone()));
					}
					if (profile.getSmsPhone() != null) {
						phones.add(createPhoneMap("sms", profile.getSmsPhoneInternationalCode(), profile.getSmsPhone()));
					}
					break;
				case WorkerOnboardingDTO.FIRST_NAME:
					profileMap.put(WorkerOnboardingDTO.FIRST_NAME, user.getFirstName());
					break;
				case WorkerOnboardingDTO.GENDER:
					Gender gender = profile.getGender();
					profileMap.put(WorkerOnboardingDTO.GENDER, gender != null ? gender.getCode() : null);
					break;
				case WorkerOnboardingDTO.LAST_NAME:
					profileMap.put(WorkerOnboardingDTO.LAST_NAME, user.getLastName());
					break;
				case WorkerOnboardingDTO.EMAIL:
					profileMap.put(WorkerOnboardingDTO.EMAIL, user.getEmail());
					break;
				case WorkerOnboardingDTO.JOB_TITLE:
					final String jobTitle = profile.getJobTitle();
					if (StringUtils.isNotBlank(jobTitle)) {
						final String uuid = findJobTitleFromQualificationService(jobTitle, context);
						profileMap.put(WorkerOnboardingDTO.JOB_TITLE, new ApiJobTitleDTO(uuid, jobTitle));
					}
					break;
				case WorkerOnboardingDTO.ADDRESS:
					profileMap.put(WorkerOnboardingDTO.ADDRESS, address != null ? address.getAddress1() : null);
					break;
				case WorkerOnboardingDTO.ADDRESS1:
					profileMap.put(WorkerOnboardingDTO.ADDRESS1, address != null ? address.getAddress1() : null);
					break;
				case WorkerOnboardingDTO.ADDRESS2:
					profileMap.put(WorkerOnboardingDTO.ADDRESS2, address != null ? address.getAddress2() : null);
					break;
				case WorkerOnboardingDTO.CITY:
					profileMap.put(WorkerOnboardingDTO.CITY, address != null ? address.getCity() : null);
					break;
				case WorkerOnboardingDTO.STATE_SHORT_NAME:
					State state = address != null ? address.getState() : null;
					profileMap.put(WorkerOnboardingDTO.STATE_SHORT_NAME, state != null ? state.getShortName() : null);
					break;
				case WorkerOnboardingDTO.COUNTRY_ISO:
					Country country = address != null ? address.getCountry() : null;
					profileMap.put(WorkerOnboardingDTO.COUNTRY_ISO, country != null ? country.getISO() : null);
					break;
				case WorkerOnboardingDTO.POSTAL_CODE:
					profileMap.put(WorkerOnboardingDTO.POSTAL_CODE, address != null ? address.getPostalCode() : null);
					break;
				case WorkerOnboardingDTO.MAX_TRAVEL_DISTANCE:
					profileMap.put(
							WorkerOnboardingDTO.MAX_TRAVEL_DISTANCE,
							(Constants.MAX_TRAVEL_DISTANCE_IN_MILES.compareTo(profile.getMaxTravelDistance()) < 0) ?
									Constants.MAX_TRAVEL_DISTANCE_IN_MILES : profile.getMaxTravelDistance()
					);
					break;
				case WorkerOnboardingDTO.OVERVIEW:
					profileMap.put(WorkerOnboardingDTO.OVERVIEW, profile.getOverview());
					break;
				case WorkerOnboardingDTO.INDIVIDUAL:
					profileMap.put(WorkerOnboardingDTO.INDIVIDUAL, company.getOperatingAsIndividualFlag());
					break;
				case WorkerOnboardingDTO.COMPANY_NAME:
					profileMap.put(WorkerOnboardingDTO.COMPANY_NAME, company.getName());
					break;
				case WorkerOnboardingDTO.WEBSITE:
					profileMap.put(WorkerOnboardingDTO.WEBSITE, company.getWebsite());
					break;
				case WorkerOnboardingDTO.COMPANY_OVERVIEW:
					profileMap.put(WorkerOnboardingDTO.COMPANY_OVERVIEW, company.getOverview());
					break;
				case WorkerOnboardingDTO.COMPANY_YEAR_FOUNDED:
					profileMap.put(WorkerOnboardingDTO.COMPANY_YEAR_FOUNDED, company.getYearFounded());
					break;
				case WorkerOnboardingDTO.COMPANY_EMPLOYEES:
					List<Map<String, Object>> employees = Lists.newArrayList();
					for (CompanyEmployeeCountRangeEnum e : CompanyEmployeeCountRangeEnum.values()) {
						m = Maps.newLinkedHashMap();
						CompanyEmployeeCountRangeEnum range = company.getCompanyEmployeeCountRangeEnum();
						m.put("value", e.getDescription());
						m.put("checked", range == null ? false : e.getDescription().equals(range.getDescription()));
						employees.add(m);
					}
					profileMap.put(WorkerOnboardingDTO.COMPANY_EMPLOYEES, employees);
					break;
				case WorkerOnboardingDTO.COUNTRY_PHONE_CODES:
					profileMap.put(WorkerOnboardingDTO.COUNTRY_PHONE_CODES, invariantDataService.getAllUniqueActiveCallingCodeIds());
					break;
				case WorkerOnboardingDTO.INDUSTRIES:
					List<IndustryDTO> allIndustries = industryService.getAllIndustryDTOs();
					Set<IndustryDTO> chosenIndustries = industryService.getIndustryDTOsForProfile(profile.getId());

					Collections.sort(allIndustries, new Comparator<IndustryDTO>() {
						@Override
						public int compare(IndustryDTO dto1, IndustryDTO dto2) {
							return dto1.getName().compareTo(dto2.getName());
						}
					});

					for (IndustryDTO industry : allIndustries) {
						Map<String, Object> industryMap = Maps.newLinkedHashMap();
						industryMap.put(WorkerOnboardingDTO.ID, industry.getId());
						industryMap.put("name", industry.getName());
						boolean checked = chosenIndustries.contains(industry);
						industryMap.put("checked", checked);
						if(checked && industry.getId().equals(Industry.GENERAL.getId())){
							//find the otherName
							for(IndustryDTO industryDTO : chosenIndustries){
								if(industryDTO.getId().equals(Industry.GENERAL.getId())){
									industryMap.put("otherName", industryDTO.getOtherName());
									break;
								}
							}
						}

						industries.add(industryMap);
					}

					profileMap.put(WorkerOnboardingDTO.INDUSTRIES, industries);
					break;
				case WorkerOnboardingDTO.YEARS_OF_EXPERIENCE:
					List<Map<String, Object>> yearsOfExperience = Lists.newArrayList();
					for (YearsOfExperienceEnum e : YearsOfExperienceEnum.values()) {
						m = Maps.newLinkedHashMap();
						YearsOfExperienceEnum en = profile.getYearsOfExperienceEnum();
						m.put("value", e.getDescription());
						m.put("checked", en == null ? false : e.getDescription().equals(en.getDescription()));
						yearsOfExperience.add(m);
					}
					profileMap.put(WorkerOnboardingDTO.YEARS_OF_EXPERIENCE, yearsOfExperience);
					break;
				case WorkerOnboardingDTO.VIDEO_WATCHED:
					profileMap.put(WorkerOnboardingDTO.VIDEO_WATCHED, profile.getOnboardVideoWatchedOn() == null ? Boolean.FALSE : Boolean.TRUE);
					break;
				case WorkerOnboardingDTO.PROFILE_IMAGE_URL:
					UserAssetAssociation assetAssociation = userService.findUserAvatars(user.getId());
					if (assetAssociation != null && assetAssociation.getTransformedLargeAsset() != null) {
						profileMap.put(WorkerOnboardingDTO.PROFILE_IMAGE_URL, new ImageDTO(assetAssociation.getTransformedLargeAsset().getCdnUri()));
					} else {
						profileMap.put(WorkerOnboardingDTO.PROFILE_IMAGE_URL, new ImageDTO());
					}
					break;
				case WorkerOnboardingDTO.COMPANY_LOGO_URL:
					CompanyAssetAssociation avatars = companyService.findCompanyAvatars(company.getId());
					if (avatars != null && avatars.getLarge() != null) {
						profileMap.put(WorkerOnboardingDTO.COMPANY_LOGO_URL, new ImageDTO(avatars.getLarge().getCdnUri()));
					} else {
						profileMap.put(WorkerOnboardingDTO.COMPANY_LOGO_URL, new ImageDTO());
					}
					break;
				case "countryCodes":
					List<CallingCode> callingCodes = invariantDataService.findAllActiveCallingCodes();
					profileMap.put("countryCodes", callingCodes);
					break;
				case "skills":
					SkillPagination skillPagination = skillService.findAllActiveSkillsByUser(user.getId(), new SkillPagination(true));
					SpecialtyPagination specialtyPagination = specialtyService.findAllActiveSpecialtiesByUser(user.getId(), new SpecialtyPagination());
					List<OnboardingSkillDTO> skills = new ArrayList<>();

					for (Skill skill : skillPagination.getResults()) {
						skills.add(new OnboardingSkillDTO(skill.getId(), skill.getName(), Qualification.Type.SKILL));
					}

					for (Specialty specialty : specialtyPagination.getResults()) {
						skills.add(new OnboardingSkillDTO(specialty.getId(), specialty.getName(), Qualification.Type.SPECIALTY));
					}

					profileMap.put(WorkerOnboardingDTO.SKILLS, skills);
					break;
				default:
					break;
			}
		}

		if (CollectionUtils.isNotEmpty(phones)) {
			profileMap.put("phones", phones);
		}

		return profileMap;
	}

	private String findJobTitleFromQualificationService(final String jobTitle, final RequestContext context) {
		final StringBuilder uuidBuilder = new StringBuilder();
		final SearchRequest request = SearchRequest.builder()
			.setQualificationType(QualificationType.job_title)
			.setNames(Lists.newArrayList(jobTitle))
			.build();
		qualificationClient.searchQualifications(request, context)
			.subscribe(
				new Action1<com.workmarket.search.qualification.Qualification>() {
					@Override
					public void call(com.workmarket.search.qualification.Qualification qualification) {
						// TODO [lu]: once the user_to_qualification table is ready, store successful uuid to the table
						uuidBuilder.append(qualification.getUuid());
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						logger.error("failed to find job title; " + context.toString(), throwable);
					}
				});
		return uuidBuilder.length() == 0 ? null : uuidBuilder.toString();
	}
}