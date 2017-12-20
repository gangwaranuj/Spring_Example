package com.workmarket.service.business.requirementsets;

import com.google.common.collect.ImmutableList;
import com.workmarket.common.metric.NoOpMetricRegistryFacade;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.EligibilityUser;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.model.requirementset.RequirementSetable;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.work.dao.WorkVendorInvitationDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.exception.search.SearchException;
import com.workmarket.service.search.user.PeopleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityServiceTest {
	private static final Long USER_ID = 1L;
	private static final Long COMPANY_ID = 2L;
	private static final Long WORK_ID = 3L;

	@Mock UserDAO userDAO;
	@Mock WorkDAO workDAO;
	@Mock EligibilityVisitor eligibilityVisitor;
	@Mock WorkVendorInvitationDAO workVendorInvitationDAO;
	@Mock UserGroupService userGroupService;
	@Mock PeopleSearchService peopleSearchService;
	@InjectMocks EligibilityServiceImpl eligibilityService = spy(new EligibilityServiceImpl());

	PeopleSearchRequest peopleSearchRequest;
	PeopleSearchResponse peopleSearchResponse;
	Work work;
	com.workmarket.thrift.work.Work tWork;
	Company company, userCompany;
	User user;
	UserGroup userGroup;
	AbstractRequirement requirement;
	RequirementSet requirementSet;
	RequirementSetable requirementSetable;

	@Before
	public void setup() throws SearchException {
		work = mock(Work.class);
		tWork = mock(com.workmarket.thrift.work.Work.class);
		company = mock(Company.class);
		userCompany = mock(Company.class);
		user = mock(User.class);
		userGroup = mock(UserGroup.class);

		requirement = mock(AbstractRequirement.class);
		requirementSet = mock(RequirementSet.class);
		requirementSetable = mock(RequirementSetable.class);

		eligibilityService.setMetricRegistryFacade(new NoOpMetricRegistryFacade());

		when(company.getId()).thenReturn(COMPANY_ID);
		when(work.getCompany()).thenReturn(company);
		when(tWork.getId()).thenReturn(WORK_ID);
		when(workDAO.get(WORK_ID)).thenReturn(work);
		when(userDAO.get(USER_ID)).thenReturn(user);

		when(user.getCompany()).thenReturn(userCompany);
		peopleSearchRequest = spy(new PeopleSearchRequest());
		doReturn(peopleSearchRequest).when(eligibilityService).makePeopleSearchRequest();
		
		peopleSearchResponse = mock(PeopleSearchResponse.class);
		when(peopleSearchResponse.getResults()).thenReturn(null);
		when(peopleSearchService.searchPeople(peopleSearchRequest)).thenReturn(peopleSearchResponse);

		when(userGroupService.findGroupById(anyLong())).thenReturn(userGroup);
	}

	@Test
	public void eligibilityService_getUserFromSolr_setLaneCompanyFilterIdFromWorkCompanyId() {
		eligibilityService.getUserFromSolr(USER_ID, work);

		verify(peopleSearchRequest).setLaneFilterCompanyId(COMPANY_ID);
	}

	@Test
	public void getEligibilityFor_userAndWork_userIsEligible() {
		assertTrue(eligibilityService.getEligibilityFor(USER_ID, tWork).isEligible());
	}

	@Test
	public void getEligibilityFor_userAndWork_andVendorInvited_checksGroup() {
		when(workVendorInvitationDAO.getVendorInvitedByGroupIds(anyLong(), anyLong())).thenReturn(ImmutableList.of(1L, 2L));
		when(work.getRequirementSetCollection()).thenReturn(Arrays.asList(requirementSet));
		when(requirementSet.getRequirements()).thenReturn(Arrays.asList(requirement));

		eligibilityService.getEligibilityFor(USER_ID, tWork);
		verify(eligibilityService, times(3)).getEligibilityFor(any(EligibilityUser.class), any(RequirementSetable.class));
	}

	@Test
	public void getEligibilityFor_userAndWork_userIsNotEligible() {
		when(work.getRequirementSetCollection()).thenReturn(Arrays.asList(requirementSet));
		when(requirementSet.getRequirements()).thenReturn(Arrays.asList(requirement));

		assertFalse(eligibilityService.getEligibilityFor(USER_ID, tWork).isEligible());
	}

	@Test
	public void getEligibilityFor_userAndUserGroup_userItEligible() {
		assertTrue(eligibilityService.getEligibilityFor(USER_ID, userGroup).isEligible());
	}

	@Test
	public void getEligibilityFor_userAndUserGroup_userIsNotEligible() {
		when(userGroup.getRequirementSetCollection()).thenReturn(Arrays.asList(requirementSet));
		when(requirementSet.getRequirements()).thenReturn(Arrays.asList(requirement));

		assertFalse(eligibilityService.getEligibilityFor(USER_ID, userGroup).isEligible());
	}
}
