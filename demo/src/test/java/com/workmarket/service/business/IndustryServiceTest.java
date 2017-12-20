package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.dao.industry.IndustryDAO;
import com.workmarket.dao.industry.ProfileIndustryAssociationDAO;
import com.workmarket.dao.profile.ProfileDAO;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Profile;
import com.workmarket.domains.model.ProfileIndustryAssociation;

import com.workmarket.service.business.dto.IndustryDTO;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class IndustryServiceTest {

	@Mock ProfileDAO profileDAO;
	@Mock ProfileIndustryAssociationDAO profileIndustryAssociationDAO;
	@Mock IndustryDAO industryDAO;
	@InjectMocks IndustryService industryService = spy(new IndustryServiceImpl());

	private static final Long PROFILE_ID = 1L, INDUSTRY_ONE_ID = 2L, INDUSTRY_TWO_ID = 3L;

	Profile profile;
	ProfileIndustryAssociation profileIndustryAssociationOne, profileIndustryAssociationTwo;
	Set<ProfileIndustryAssociation> profileIndustryAssociations;
	IndustryDTO industryDTOOne;
	IndustryDTO industryDTOTwo;
	Industry industryOne;
	Industry industryTwo;
	Set<Industry> industries;
	Set<IndustryDTO> industryDTOs;

	@Before
	public void setUp() {
		profile = mock(Profile.class);
		when(profileDAO.get(PROFILE_ID)).thenReturn(profile);

		industryDTOOne = mock(IndustryDTO.class);
		industryDTOTwo = mock(IndustryDTO.class);
		when(industryDTOOne.getId()).thenReturn(INDUSTRY_ONE_ID);
		when(industryDTOTwo.getId()).thenReturn(INDUSTRY_TWO_ID);

		industryOne = mock(Industry.class);
		industryTwo = mock(Industry.class);
		when(industryOne.getId()).thenReturn(INDUSTRY_ONE_ID);
		when(industryTwo.getId()).thenReturn(INDUSTRY_TWO_ID);

		industries = Sets.newHashSet(industryOne, industryTwo);
		industryDTOs = Sets.newHashSet(industryDTOOne, industryDTOTwo);

		profileIndustryAssociationOne = mock(ProfileIndustryAssociation.class);
		when(profileIndustryAssociationOne.getIndustry()).thenReturn(industryOne);

		profileIndustryAssociationTwo = mock(ProfileIndustryAssociation.class);
		when(profileIndustryAssociationTwo.getIndustry()).thenReturn(industryTwo);

		profileIndustryAssociations = Sets.newHashSet(profileIndustryAssociationOne, profileIndustryAssociationTwo);
		when(profileIndustryAssociationDAO.findAllIndustryProfileAssociationsByProfile(PROFILE_ID)).thenReturn(profileIndustryAssociations);
		when(profileIndustryAssociationDAO.findDefaultIndustryForProfile(PROFILE_ID)).thenReturn(industryOne);

		doReturn(industryDTOs).when(industryService).getIndustryDTOsForProfile(PROFILE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getAllIndustriesForProfile_nullProfileId_exceptionThrown() {
		industryService.getAllIndustryDTOsForProfile(null);
	}

	@Test
	public void getAllIndustriesForProfile_profileId_profileIndustryAssociationDAOCalled() {
		industryService.getAllIndustryDTOsForProfile(PROFILE_ID);

		verify(profileIndustryAssociationDAO).findIndustriesForProfile(PROFILE_ID, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIndustriesForProfile_nullProfileId_exceptionThrown() {
		industryService.getIndustryDTOsForProfile(null);
	}

	@Test
	public void getIndustriesForProfile_profileId_profileIndustryAssociationDAOCalled() {
		reset(industryService);

		industryService.getIndustryDTOsForProfile(PROFILE_ID);

		verify(profileIndustryAssociationDAO).findIndustriesForProfile(PROFILE_ID, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIndustryIdsForProfile_nullProfileId_exceptionThrown() {
		industryService.getIndustryIdsForProfile(null);
	}

	@Test
	public void getIndustryIdsForProfile_profileId_getIndustriesForProfileCalled() {
		industryService.getIndustryIdsForProfile(PROFILE_ID);

		verify(industryService).getIndustryDTOsForProfile(PROFILE_ID);
	}

	@Test
	public void getIndustryIdsForProfile_noIndustries_returnEmptyList() {
		doReturn(Sets.newHashSet()).when(industryService).getIndustryDTOsForProfile(PROFILE_ID);

		assertEquals(0, industryService.getIndustryIdsForProfile(PROFILE_ID).size());
	}

	@Test
	public void getIndustryIdsForProfile_withIndustries_returnIds() {
		List<Long> ids = industryService.getIndustryIdsForProfile(PROFILE_ID);

		assertTrue(CollectionUtilities.containsAll(ids, industryOne.getId(), industryTwo.getId()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getDefaultIndustryForProfile_nullProfileId_exceptionThrown() {
		industryService.getDefaultIndustryForProfile(null);
	}

	@Test
	public void getDefaultIndustryForProfile_profileId_profileIndustryAssociationDAOCalled() {
		industryService.getDefaultIndustryForProfile(PROFILE_ID);

		verify(profileIndustryAssociationDAO).findDefaultIndustryForProfile(PROFILE_ID);
	}

	@Test
	public void getDefaultIndustryForProfile_profileId_defaultIndustryReturned() {
		Industry industry = industryService.getDefaultIndustryForProfile(PROFILE_ID);

		assertEquals(industry, industryOne);
	}

	@Test
	public void getDefaultIndustryForProfile_profileIdNoDefault_NONEIndustryReturned() {
		when(profileIndustryAssociationDAO.findDefaultIndustryForProfile(PROFILE_ID)).thenReturn(null);

		Industry industry = industryService.getDefaultIndustryForProfile(PROFILE_ID);

		assertEquals(industry, Industry.NONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getIndustryById_nullId_exceptionThrown() {
		industryService.getIndustryById(null);
	}

	@Test
	public void getIndustryById_industryId_industryDAOCalled() {
		industryService.getIndustryById(INDUSTRY_ONE_ID);

		verify(industryDAO).get(INDUSTRY_ONE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void doesProfileHaveIndustry_nullProfileIdValidIndustryId_exceptionThrown() {
		industryService.doesProfileHaveIndustry(null, INDUSTRY_ONE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void doesProfileHaveIndustry_profileIdNullIndustryId_exceptionThrown() {
		industryService.doesProfileHaveIndustry(PROFILE_ID, null);
	}

	@Test
	public void doesProfileHaveIndustry_profileIdAndIndustryId_profileIndustryAssociationDAOCalled() {
		industryService.doesProfileHaveIndustry(PROFILE_ID, INDUSTRY_ONE_ID);

		verify(profileIndustryAssociationDAO).doesProfileHaveIndustry(PROFILE_ID, INDUSTRY_ONE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setIndustriesForProfile_nullProfileIdValidIndustries_exceptionThrown() {
		industryService.setIndustriesForProfile(null, industries);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setIndustriesForProfile_profileIdNullIndustries_exceptionThrown() {
		industryService.setIndustriesForProfile(PROFILE_ID, null);
	}

	@Test
	public void setIndustriesForProfile_reAddingOldIndustries_deletedSetToFalseOnPreExistingAssociations() {
		when(profileIndustryAssociationOne.getDeleted()).thenReturn(true);
		when(profileIndustryAssociationTwo.getDeleted()).thenReturn(true);

		industryService.setIndustriesForProfile(PROFILE_ID, industries);

		verify(profileIndustryAssociationOne).setDeleted(false);
		verify(profileIndustryAssociationTwo).setDeleted(false);
	}

	@Test
	public void setIndustriesForProfile_onlySetIndustryOne_industryTwoDeleted() {
		industryService.setIndustriesForProfile(PROFILE_ID, Sets.newHashSet(industryOne));

		verify(profileIndustryAssociationTwo).setDeleted(true);
	}

	@Test
	public void setIndustriesForProfile_oneOldOneNewIndustry_newIndustryAssociationCreated() {
		when(profileIndustryAssociationDAO.findAllIndustryProfileAssociationsByProfile(PROFILE_ID))
			.thenReturn(Sets.newHashSet(profileIndustryAssociationOne));

		industryService.setIndustriesForProfile(PROFILE_ID, industries);

		verify(industryService).makeProfileIndustryAssociation(industryTwo, profile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findProfileIndustryAssociationByProfileIdAndIndustryId_nullProfileIdValidIndustryId_exceptionThrown() {
		industryService.findProfileIndustryAssociationByProfileIdAndIndustryId(null, INDUSTRY_ONE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
		 public void findProfileIndustryAssociationByProfileIdAndIndustryId_profileIdNullIndustryId_exceptionThrown() {
		industryService.findProfileIndustryAssociationByProfileIdAndIndustryId(PROFILE_ID, null);
	}

	@Test
	public void findProfileIndustryAssociationByProfileIdAndIndustryId_profileIdAndIndustryId_profileIndustryAssociationDAOCalled() {
		industryService.findProfileIndustryAssociationByProfileIdAndIndustryId(PROFILE_ID, INDUSTRY_ONE_ID);

		verify(profileIndustryAssociationDAO).findByProfileIdAndIndustryId(PROFILE_ID, INDUSTRY_ONE_ID);
	}
}
