package com.workmarket.domains.work.service.actions;

import com.google.common.collect.ImmutableSet;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkEventAuthServiceTest {

	@Mock WorkService workService;
	@Mock WorkEventAuthServiceImpl.FactoryHelper factoryHelper;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock private UserRoleService userRoleService;

	@InjectMocks WorkEventAuthServiceImpl workEventAuthService;

	@Mock AjaxResponseBuilder response;
	@Mock User user;
	@Mock List<Work> works;
	@Mock Iterator mockWorkIter;
	@Mock Work work;
	@Mock User buyer;
	@Mock List<WorkContext> workAuthz;
	String messageKey = "";
	ImmutableSet<WorkContext> authz = ImmutableSet.of(
			WorkContext.OWNER,
			WorkContext.COMPANY_OWNED,
			WorkContext.ACTIVE_RESOURCE);
	@Mock List<Work> validWork;


	@Before
	public void setup(){

		when(factoryHelper.makeValidWorkList()).thenReturn(validWork);


		user = mock(User.class);
		when(user.isInternalUser()).thenReturn(false);
		when(user.getId()).thenReturn(1L);


		work = mock(Work.class);
		when(work.getId()).thenReturn(1L);
		when(work.getWorkNumber()).thenReturn("");


		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);

		mockWorkIter = mock(Iterator.class);
		when(works.iterator()).thenReturn(mockWorkIter);
		when(mockWorkIter.hasNext()).thenReturn(true,false);
		when(mockWorkIter.next()).thenReturn(work);


		when(workService.getWorkContext(work,buyer)).thenReturn(workAuthz);
		when(workService.findWorkByWorkNumber(anyString())).thenReturn(work);

	}

	@Test(expected = Exception.class)
	public void validateAndAuth_nullUser_exception(){
		workEventAuthService.validateAndAuthorizeWork(null,works,response,messageKey,authz);
	}

	@Test(expected = Exception.class)
	public void validateAndAuth_nullWork_exception(){
		workEventAuthService.validateAndAuthorizeWork(user,null,response,messageKey,authz);
	}


	@Test(expected = Exception.class)
	public void validateAndAuth_nullResponse_exception(){
		workEventAuthService.validateAndAuthorizeWork(user,works,null,messageKey,authz);
	}

	@Test(expected = Exception.class)
	public void validateAndAuth_nullMessageKey_exception(){
		workEventAuthService.validateAndAuthorizeWork(user,works,response,null,authz);
	}

	@Test(expected = Exception.class)
	public void validateAndAuth_nullAuthz_exception(){
		workEventAuthService.validateAndAuthorizeWork(user,works,response,messageKey,null);
	}


	@Test
	public void validateAndAuth_isInternal_returns(){
		when(buyer.isInternalUser()).thenReturn(true);
		when(userRoleService.isInternalUser(buyer)).thenReturn(true);
		workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		verify(mockWorkIter,never()).hasNext();
	}

	@Test
	public void validateAndAuth_NotInternal_validatesWork(){
		workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		verify(mockWorkIter,times(2)).hasNext();
	}


	@Test
	public void validAndAuth_unrelated_notAddedToValidWork(){
		when(workAuthz.contains(WorkContext.UNRELATED)).thenReturn(true);
		workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		verify(response).setSuccessful(false);
		verify(validWork,never()).add(work);
	}


	@Test
	public void validAndAuth_noMatchingContexts_notAddedToValidWork(){
		when(workAuthz.contains(WorkContext.OWNER)).thenReturn(false);
		when(workAuthz.contains(WorkContext.ACTIVE_RESOURCE)).thenReturn(false);
		when(workAuthz.contains(WorkContext.COMPANY_OWNED)).thenReturn(false);
		workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		verify(response).setSuccessful(false);
		verify(validWork,never()).add(work);
	}

	@Test
	public void validAndAuth_matchingContext_addedToValidWork(){
		when(workAuthz.contains(WorkContext.OWNER)).thenReturn(true);
		workEventAuthService.validateAndAuthorizeWork(buyer,works,response,messageKey,authz);
		verify(validWork).add(work);
	}



}
