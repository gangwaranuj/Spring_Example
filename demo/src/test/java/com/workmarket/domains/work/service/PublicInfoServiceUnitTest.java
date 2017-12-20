package com.workmarket.domains.work.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.common.cache.PublicPageProfileCache;
import com.workmarket.dao.publicinfo.PublicPageProfileDAO;
import com.workmarket.service.infra.business.PublicInfoServiceImpl;
import com.workmarket.dto.PublicPageProfileDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Created by rahul on 12/20/13
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicInfoServiceUnitTest {

	@Mock PublicPageProfileDAO publicPageProfileDAO;
	@Mock PublicPageProfileCache publicPageProfileCache;
	@InjectMocks PublicInfoServiceImpl publicInfoService;

	List<PublicPageProfileDTO> industryOneProfiles;
	List<PublicPageProfileDTO> industryTwoProfiles;

	private static String INDUSTRY_ONE = "IndustryOne";
	private static String INDUSTRY_TWO = "IndustryTwo";
	private static int DISPLAYED_PROFILES = 1;

	List<String> industries;
	Multimap<String, PublicPageProfileDTO> cacheMissResults;
	Multimap<String, PublicPageProfileDTO> cacheHitResults;

	@Before
	public void setUp() {
		industries = Lists.newArrayList(INDUSTRY_ONE, INDUSTRY_TWO);
		cacheMissResults = ArrayListMultimap.create();
		cacheHitResults = ArrayListMultimap.create();

		PublicPageProfileDTO industryOneProfile = new PublicPageProfileDTO();
		industryOneProfile.setFirstName("Bob");
		industryOneProfile.setLastName("Stevens");
		industryOneProfile.setGender("M");
		industryOneProfile.setUserNumber("123");
		industryOneProfile.setProfileImageUri("test.com");
		industryOneProfile.setShortDescription("short description");

		PublicPageProfileDTO industryTwoProfile = new PublicPageProfileDTO();
		industryTwoProfile.setFirstName("Stacy");
		industryTwoProfile.setLastName("Stevens");
		industryTwoProfile.setGender("F");
		industryTwoProfile.setUserNumber("321");
		industryTwoProfile.setProfileImageUri("test.com");
		industryTwoProfile.setShortDescription("short description");

		PublicPageProfileDTO anotherIndustryTwoProfile = new PublicPageProfileDTO();
		anotherIndustryTwoProfile.setFirstName("Britney");
		anotherIndustryTwoProfile.setLastName("Stevens");
		anotherIndustryTwoProfile.setGender("F");
		anotherIndustryTwoProfile.setUserNumber("456");
		anotherIndustryTwoProfile.setProfileImageUri("test.com/britney");
		anotherIndustryTwoProfile.setShortDescription("shorter description");

		industryOneProfiles = Lists.newArrayList(industryOneProfile);
		industryTwoProfiles = Lists.newArrayList(industryTwoProfile);

		cacheMissResults.put(INDUSTRY_ONE, industryOneProfile);

		cacheHitResults.put(INDUSTRY_ONE, industryOneProfile);
		cacheHitResults.put(INDUSTRY_TWO, industryTwoProfile);
		cacheHitResults.put(INDUSTRY_TWO, anotherIndustryTwoProfile);

		when(publicPageProfileCache.getPublicPageProfiles(anyList(), anyInt()))
				.thenReturn(cacheMissResults)
				.thenReturn(cacheHitResults);

		when(publicPageProfileCache.putNewPublicPageProfiles(INDUSTRY_ONE, industryOneProfiles))
				.thenReturn(industryOneProfiles);

		when(publicPageProfileCache.putNewPublicPageProfiles(INDUSTRY_TWO, industryTwoProfiles))
				.thenReturn(industryTwoProfiles);

		when(publicPageProfileDAO.getAllPublicPageProfilesDataByIndustry(INDUSTRY_ONE))
				.thenReturn(industryOneProfiles);

		when(publicPageProfileDAO.getAllPublicPageProfilesDataByIndustry(INDUSTRY_TWO))
				.thenReturn(industryTwoProfiles);
	}

	@Test
	public void getPublicProfiles_CacheIsInvalid_AllProfiles() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(industries,DISPLAYED_PROFILES);

		assertTrue(results.containsKey(INDUSTRY_ONE));
		assertTrue(results.containsKey(INDUSTRY_TWO));

		PublicPageProfileDTO profileForIndustryOneResult = Lists.newArrayList(results.get(INDUSTRY_ONE))
				.get(0);
		PublicPageProfileDTO profileForIndustryTwoResult = Lists.newArrayList(results.get(INDUSTRY_TWO))
				.get(0);

		PublicPageProfileDTO profileForIndustryOneExpected = industryOneProfiles.get(0);
		PublicPageProfileDTO profileForIndustryTwoExpected = industryTwoProfiles.get(0);

		assertEquals(profileForIndustryOneResult.getFirstName(), profileForIndustryOneExpected.getFirstName());
		assertEquals(profileForIndustryTwoResult.getFirstName(), profileForIndustryTwoExpected.getFirstName());
	}

	@Test
	public void getPublicProfiles_CacheIsValid_AllProfiles() {
		publicInfoService.getPublicProfiles(industries,DISPLAYED_PROFILES);
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(industries,DISPLAYED_PROFILES);

		assertTrue(results.containsKey(INDUSTRY_ONE));
		assertTrue(results.containsKey(INDUSTRY_TWO));

		PublicPageProfileDTO profileForIndustryOneResult = Lists.newArrayList(results.get(INDUSTRY_ONE))
				.get(0);
		PublicPageProfileDTO profileForIndustryTwoResult = Lists.newArrayList(results.get(INDUSTRY_TWO))
				.get(0);

		PublicPageProfileDTO profileForIndustryOneExpected = industryOneProfiles.get(0);
		PublicPageProfileDTO profileForIndustryTwoExpected = industryTwoProfiles.get(0);

		assertEquals(profileForIndustryOneResult.getFirstName(), profileForIndustryOneExpected.getFirstName());
		assertEquals(profileForIndustryTwoResult.getFirstName(), profileForIndustryTwoExpected.getFirstName());
	}

	@Test
	public void getPublicProfiles_NoIndustries_EmptyResult() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(Lists.<String>newArrayList(),DISPLAYED_PROFILES);

		assertTrue(results.isEmpty());
	}

	@Test
	public void getPublicProfiles_NegativeDisplayedProfiles_EmptyResult() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(Lists.<String>newArrayList(),-1);

		assertTrue(results.isEmpty());
	}

	@Test
	public void getPublicProfiles_NullIndustries_EmptyResult() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(null,DISPLAYED_PROFILES);

		assertTrue(results.isEmpty());
	}

	@Test
	public void getPublicProfiles_RequestLessProfilesThanAvailable_RequestedNumberOfProfiles() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(industries,DISPLAYED_PROFILES);
		int industryOneResultSize = results.get(INDUSTRY_ONE).size();
		int industryTwoResultSize = results.get(INDUSTRY_TWO).size();

		assertEquals(industryOneResultSize, DISPLAYED_PROFILES);
		assertEquals(industryTwoResultSize, DISPLAYED_PROFILES);
	}

	@Test
	public void getPublicProfiles_RequestMoreProfilesThanAvailable_AllProfiles() {
		Multimap<String, PublicPageProfileDTO> results = publicInfoService.getPublicProfiles(industries,10000);
		int industryOneResultSize = results.get(INDUSTRY_ONE).size();
		int industryTwoResultSize = results.get(INDUSTRY_TWO).size();

		assertEquals(industryOneResultSize, industryOneProfiles.size());
		assertEquals(industryTwoResultSize, industryOneProfiles.size());
	}

	@Test
	public void getPublicProfiles_CacheIsValid_DBNotAccessed() {
		publicInfoService.getPublicProfiles(industries, DISPLAYED_PROFILES);
		publicInfoService.getPublicProfiles(industries, DISPLAYED_PROFILES);

		verify(publicPageProfileDAO,atMost(1)).getAllPublicPageProfilesDataByIndustry(INDUSTRY_TWO);
	}
}
