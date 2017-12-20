package com.workmarket.domains.work.service.actions;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.redis.repositories.WorkSearchRequestRepository;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.service.business.SelectService;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkListFetcherServiceUnitTest {

	@Mock WorkSearchRequestRepository workSearchRequestRepository;
	@Mock SelectService selectService;
	@Mock BaseWorkDAO baseWorkDAO;
	@Mock WorkDAO workDAO;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock UserDAO userDAO;
	@Mock WorkEventAuthService workEventAuthService;
	@InjectMocks WorkListFetcherServiceImpl workListFetcherService;

	@Mock List<String> workNumbers;
	@Mock AjaxResponseBuilder response;
	@Mock List<Work> works;
	@Mock Work work;
	@Mock User user;
	@Mock Optional<WorkSearchRequest> optRequest;
	@Mock WorkSearchRequest workSearchRequest;
	String messageKey = "";


	@Before
	public void setup(){

		when(workSearchRequest.isFullSelectAll()).thenReturn(true);

		when(optRequest.isPresent()).thenReturn(true);
		when(optRequest.get()).thenReturn(workSearchRequest);

		when(userDAO.findUserById(anyLong())).thenReturn(user);

		response = mock(AjaxResponseBuilder.class);
		when(response.isSuccessful()).thenReturn(true);

		work = mock(Work.class);
		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);

		//when(baseWorkDAO.findByWorkNumbers(workNumbers)).thenReturn(works);
		when(workDAO.findWorkByWorkNumber(workNumbers)).thenReturn(works);


		when(workSearchRequestRepository.get(user.getId())).thenReturn(optRequest);
		when(selectService.fetchAllWorkBySearchFilter(user.getId())).thenReturn(workNumbers);
	}


	@Test(expected=Exception.class)
	public void fetchValidatedWork_nullUser_exception(){
		workListFetcherService.fetchValidatedWork(null,workNumbers,response,messageKey);
	}

	@Test(expected=Exception.class)
	public void fetchValidatedWork_nullWorkNumbers_exception(){
		workListFetcherService.fetchValidatedWork(user,null,response,messageKey);
	}

	@Test(expected=Exception.class)
	public void fetchValidatedWork_nullResponse_exception(){
		workListFetcherService.fetchValidatedWork(user,workNumbers,null,messageKey);
	}

	@Test(expected=Exception.class)
	public void fetchValidatedWork_nullMessageKey_exception(){
		workListFetcherService.fetchValidatedWork(user,workNumbers,response,null);
	}

	@Test
	public void fetchValidatedWork_isSelectAll_workSearchCalled(){
		workListFetcherService.fetchValidatedWork(user,workNumbers,response,messageKey);
		verify(workSearchRequestRepository).get(user.getId());

	}

	@Test
	public void fetchValidateWork_isSelectAll_optIsNotPresent_solrnNotCalled(){
		when(workSearchRequest.isFullSelectAll()).thenReturn(false);
		workListFetcherService.fetchValidatedWork(user,workNumbers,response,messageKey);
		verify(selectService,never()).fetchAllWorkBySearchFilter(user.getId());
	}

	@Test
	public void fetchValidateWork_isNotSelectAll_solrnNotCalled(){
		when(optRequest.isPresent()).thenReturn(false);
		workListFetcherService.fetchValidatedWork(user,workNumbers,response,messageKey);
		verify(selectService,never()).fetchAllWorkBySearchFilter(user.getId());
	}

	@Test
	public void fetchValidateWork_validateCalled_success(){
		workListFetcherService.fetchValidatedWork(user,workNumbers,response,messageKey);
		verify(workEventAuthService).validateAndAuthorizeWork(user,works,response,messageKey,ImmutableSet.of(
				WorkContext.OWNER,
				WorkContext.COMPANY_OWNED,
				WorkContext.ACTIVE_RESOURCE
		));
	}

	@Test(expected=Exception.class)
	public void fetchWork_nullResponse_exception(){
		workListFetcherService.fetchWork(workNumbers, null, messageKey);
	}

	@Test(expected=Exception.class)
	public void fetchWork_nullMessageKey_exception(){
		workListFetcherService.fetchWork(workNumbers, response, null);
	}

	@Test
	public void fetchWork_findWorks_success(){
		workListFetcherService.fetchWork(workNumbers, response, messageKey);
		verify(workDAO).findWorkByWorkNumber(workNumbers);
	}

	@Test
	public void findWork_workDaoReturnsEmpty(){
		when(workDAO.findWorkByWorkNumber(anyList())).thenReturn(Collections.EMPTY_LIST);
		workListFetcherService.fetchWork(workNumbers, response, messageKey);
		verify(messageBundleHelper).addMessage(response.setSuccessful(false), messageKey + ".exception");
	}

}
