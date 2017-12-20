package com.workmarket.web.controllers.onboarding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.api.v2.model.ApiJobTitleDTO;
import com.workmarket.business.recommendation.skill.model.RecommendSkillResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.qualification.SkillRecommenderDTO;
import com.workmarket.domains.model.skill.Skill;
import com.workmarket.domains.model.specialty.Specialty;
import com.workmarket.domains.onboarding.model.Qualification;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.IndustryService;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.service.business.feed.Feed;
import com.workmarket.service.business.feed.FeedService;
import com.workmarket.service.business.onboarding.OnboardMappingService;
import com.workmarket.service.business.onboarding.OnboardMappingServiceImpl;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.service.web.cachebusting.CacheBusterServiceImpl;
import com.workmarket.web.controllers.BaseControllerUnitTest;
import com.workmarket.web.forms.feed.FeedRequestParams;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.View;
import rx.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class WorkerOnboardingControllerTest extends BaseControllerUnitTest {

	private static final Long INDUSTRY_ID = 1L;
	private static final Long ADDRESS_ID = 2L;
	private static final String INDUSTRY_NAME = "Health and Wellness";
	private static final Long PROFILE_ID = 111L;
	private static final String MOBILE_PHONE = "123-1231";
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Smith";
	private static final String JOB_TITLE = "Web developer";
	private static final String EMAIL = "john@smith.com";
	private static final String ADDRESS = "303 east 12th, New York, NYC";
	private static final String OVERVIEW = "I'm a qualified web dev living in New York";
	private static final Boolean INDIVIDUAL = Boolean.TRUE;
	private static final String COMPANY_NAME = "Acme Inc.";
	private static final String COMPANY_WEBSITE = "http://www.acme.com";
	private static final Integer YEAR_FOUNDED = 1980;
	private static final Integer EMPLOYESS = 173;

	private static final com.workmarket.search.qualification.Qualification QUALIFICATION = com.workmarket.search
		.qualification.Qualification.builder()
		.setUuid("QUALIFICATION-uuid")
		.setName("QUALIFICATION-name")
		.setIsApproved(true)
		.build();

	private static final ApiJobTitleDTO JOB_TITLE_DTO =
		new ApiJobTitleDTO("QUALIFICATION-uuid", "QUALIFICATION-name");

	private static final RecommendSkillResponse RECOMMEND_SKILL_RESPONSE =
		new RecommendSkillResponse(
			// skills
			ImmutableList.of(new com.workmarket.business.recommendation.skill.model.Qualification("1000", 1d)),
			// specialties
			ImmutableList.of(new com.workmarket.business.recommendation.skill.model.Qualification("1001", 1.1d)),
			// tools (ignore)
			ImmutableList.of(new com.workmarket.business.recommendation.skill.model.Qualification("1002", 1.2d))
		);

	private static final Skill SKILL = new Skill();
	private static final Specialty SPECIALTY = new Specialty();

	private static final ArrayList<Qualification> QUALIFICATIONS =
		Lists.newArrayList(
			new Qualification(1001L, "specialty1", 1.1d, Qualification.Type.SPECIALTY),
			new Qualification(1000L, "skill1", 1d, Qualification.Type.SKILL)
		);

	private static final String SkillRecommenderDTO_JSON = "{\"jobTitle\":\"job title\",\"industries\":[\"1000\"]}";
	private static final String SkillRecommenderDTO_EMPTY_INDUSTRIES_JSON = "{\"jobTitle\":\"job title\",\"industries\":[]}";

	private static String CACHE_BUSTER_HASH = "hash";

	private static final Feed FEED = new Feed();
	private static final ObjectMapper MAPPER = new ObjectMapper();

	protected static class GetIndustryBuilder {
		public static MockHttpServletRequestBuilder create() {
			return MockMvcRequestBuilders.get("/onboarding/industries");
		}
	}

	protected static class GetAvailableAssignmentsBuilder {
		public static MockHttpServletRequestBuilder create() {
			return MockMvcRequestBuilders.get("/onboarding/available_assignments");
		}
	}

	@Mock private View mockView;
	@Mock private CacheBusterServiceImpl cacheBusterServiceImpl;
	@Mock private ProfileService profileService;
	@Mock private InvariantDataService invariantDataService;
	@Mock private UserService userService;
	@Mock private WorkSearchService workSearchService;
	@Mock private IndustryService industryService;
	@Mock private AddressService addressService;
	@Mock private FeedService feedService;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@Mock private QualificationRecommender qualificationRecommender;
	@Mock private SkillService skillService;
	@Mock private SpecialtyService specialtyService;
	@Mock private OnboardMappingService onboardMappingService;
	@InjectMocks
	WorkerOnboardingController controller;

	private MockMvc mockMvc;
	private Profile profile;
	private User user;
	private Address address;
	private Address companyAddress;
	private Company company;
	private IndustryDTO industryDTO;
	private RequestContext requestContext;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		initController(controller);
		requestContext = new RequestContext(UUID.randomUUID().toString(), "DUMMY_TENANT_ID");
		requestContext.setUserId("workmarket");

		SKILL.setId(1000L);
		SKILL.setName("skill1");

		SPECIALTY.setId(1001L);
		SPECIALTY.setName("specialty1");

		when(cacheBusterServiceImpl.getMediaPrefix()).thenReturn(CACHE_BUSTER_HASH);

		mockMvc = standaloneSetup(controller)
			.setSingleView(mockView)
			.build();

		address = mock(Address.class);
		when(address.getFullAddress()).thenReturn(ADDRESS);

		companyAddress = mock(Address.class);
		when(companyAddress.getFullAddress()).thenReturn(ADDRESS);

		company = mock(Company.class);
		when(company.getOperatingAsIndividualFlag()).thenReturn(INDIVIDUAL);
		when(company.getName()).thenReturn(COMPANY_NAME);
		when(company.getWebsite()).thenReturn(COMPANY_WEBSITE);
		when(company.getAddress()).thenReturn(companyAddress);
		when(company.getOverview()).thenReturn(OVERVIEW);
		when(company.getYearFounded()).thenReturn(YEAR_FOUNDED);
		when(company.getEmployees()).thenReturn(EMPLOYESS);

		user = mock(User.class);
		when(user.getFirstName()).thenReturn(FIRST_NAME);
		when(user.getLastName()).thenReturn(LAST_NAME);
		when(user.getEmail()).thenReturn(EMAIL);

		industryDTO = mock(IndustryDTO.class);
		when(industryDTO.getId()).thenReturn(INDUSTRY_ID);
		when(industryDTO.getName()).thenReturn(INDUSTRY_NAME);

		profile = mock(Profile.class);
		when(profile.getUser()).thenReturn(user);
		when(profile.getAddressId()).thenReturn(ADDRESS_ID);
		when(profile.getId()).thenReturn(PROFILE_ID);
		when(profile.getMobilePhone()).thenReturn(MOBILE_PHONE);
		when(profile.getJobTitle()).thenReturn(JOB_TITLE);
		when(profile.getOverview()).thenReturn(OVERVIEW);
		when(industryService.getIndustryDTOsForProfile(profile.getId())).thenReturn(Sets.newHashSet(industryDTO));

		when(profileService.findProfile(any(Long.class))).thenReturn(profile);
		when(profileService.findCompany(any(Long.class))).thenReturn(company);

		when(addressService.findById(anyLong())).thenReturn(address);
		when(industryService.getAllIndustryDTOs()).thenReturn(Lists.newArrayList(industryDTO));

		when(feedService.getFeed(any(FeedRequestParams.class), any(SolrQuery.class))).thenReturn(FEED);

		when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);
		when(skillService.findSkillsByIds(any(Long[].class))).thenReturn(Lists.newArrayList(SKILL));
		when(specialtyService.findSpecialtiesByIds(any(Long[].class))).thenReturn(Lists.newArrayList(SPECIALTY));
		when(qualificationRecommender.getQualifications(any(QualificationType.class), any(RequestContext.class)))
			.thenReturn(Observable.just(QUALIFICATION));
		when(qualificationRecommender.recommendSkills(any(SkillRecommenderDTO.class), any(RequestContext.class)))
			.thenReturn(Observable.just(RECOMMEND_SKILL_RESPONSE));
	}

	@Test
	public void onboarding_getAllIndustries_return200() throws Exception {
		mockMvc.perform(GetIndustryBuilder.create()).andExpect(status().isOk());
	}

	@Test
	public void onboarding_getAllIndustries_returnIndustriesPayload() throws Exception {
		mockMvc.perform(GetIndustryBuilder.create())
			.andExpect(content().string("{\"industries\":{\"" + INDUSTRY_ID + "\":\"" + INDUSTRY_NAME + "\"}}"));
	}

	@Test
	public void onboarding_getAvailableAssignments_return200() throws Exception {
		mockMvc.perform(GetAvailableAssignmentsBuilder.create())
			.andExpect(status().isOk());
	}

	@Test
	public void onboarding_getAvailableAssignments_returnCount() throws Exception {
		mockMvc.perform(GetAvailableAssignmentsBuilder.create())
			.andExpect(content().string(MAPPER.writeValueAsString(FEED)));
	}

	@Test
	public void onboarding_onlyAllowSameUser_fail() throws Exception {
		final Profile profile = new Profile() {
			public Long getId() {
				return 1234L;
			}

			@Override
			public User getUser() {
				return new User() {
					@Override
					public String getUuid() {
						return "ABC123";
					}
				};
			}
		};

		ExtendedUserDetails eud = mock(ExtendedUserDetails.class);
		when(eud.getUuid()).thenReturn("ABC123");

		when(profileService.findById(eq(1234L))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(eud);

		final Company company = new Company();
		when(profileService.findCompany(any(Long.class))).thenReturn(company);
		when(onboardMappingService.mapProfile(
				any(String.class),
				eq(profile),
				eq(company),
				any(RequestContext.class))).thenReturn(new HashMap<String, Object>());
		mockMvc.perform(MockMvcRequestBuilders.get("/onboarding/profiles/1235")).andExpect(status().isNotFound());
	}

	@Test
	public void onboarding_onlyAllowSameUser_happy() throws Exception {
		final Profile profile = new Profile() {
			public Long getId() {
				return 1234L;
			}

			@Override
			public User getUser() {
				return new User() {
					@Override
					public String getUuid() {
						return "ABC123";
					}
				};
			}
		};

		ExtendedUserDetails eud = mock(ExtendedUserDetails.class);
		when(eud.getUuid()).thenReturn("ABC123");

		when(profileService.findById(eq(1234L))).thenReturn(profile);
		when(securityContextFacade.getCurrentUser()).thenReturn(eud);

		final Company company = new Company();
		when(profileService.findCompany(any(Long.class))).thenReturn(company);
		when(onboardMappingService.mapProfile(
			any(String.class),
			eq(profile),
			eq(company),
			any(RequestContext.class))).thenReturn(new HashMap<String, Object>());
		mockMvc.perform(MockMvcRequestBuilders.get("/onboarding/profiles/1234")).andExpect(status().isOk());
	}
}
