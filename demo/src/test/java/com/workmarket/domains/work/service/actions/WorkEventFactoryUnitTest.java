package com.workmarket.domains.work.service.actions;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.actions.handlers.*;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkEventFactoryUnitTest {

	@Mock AddNotesWorkEventHandler addNotesWorkEventHandler;
	@Mock RemoveAttachmentsEventHandler removeAttachmentsEventHandler;
	@Mock AddAttachmentsEventHandler addAttachmentsEventHandler;
	@Mock UserService userService;
	@Mock WorkListFetcherService workListFetcherService;
	@InjectMocks WorkEventFactoryImpl workEventFactory;


	User currUser = new User();
	List<String> workNumbers = Lists.newArrayList();
	User user;
	String actionName = "test";
	String messageKey = "msgkey";
	@Mock AjaxResponseBuilder response;
	@Mock List<Work> works;

	@Before
	public void setup(){
		user = mock(User.class);
		when(user.getId()).thenReturn(1L);
		when(workListFetcherService.fetchValidatedWork(user,workNumbers,response,messageKey)).thenReturn(works);
	}

	private void checkEvent(AbstractWorkEvent event){
		Assert.assertNotNull(event);
		Assert.assertNotNull(event.getResponse());
		Assert.assertEquals(event.getActionName(),actionName);
		Assert.assertEquals(event.getUser(),user);
		Assert.assertEquals(event.getWorkNumbers(),workNumbers);
		Assert.assertEquals(event.getMessageKey(), messageKey);
	}

	@Test
	public void createAddNotesWorkAction(){
		String content = "test";
		boolean isPrivate = false;
		AddNotesWorkEvent event = workEventFactory.createAddNotesWorkAction(workNumbers, user, actionName, messageKey, content, isPrivate);
		checkEvent(event);
		Assert.assertEquals(content, event.getContent());
		Assert.assertEquals(isPrivate,event.isPrivate());
		Assert.assertTrue(event.isQueue());
	}

	@Test
	public void createRemoveAttachmentsEvent(){
		String assetId = "789798";
		RemoveAttachmentsEvent event = workEventFactory.createRemoveAttachmentsEvent(workNumbers,user,actionName, messageKey,assetId);
		checkEvent(event);
		Assert.assertEquals(assetId, event.getAssetId());
		Assert.assertTrue(event.isQueue());
	}

	@Test
	public void createAddAttachmentsEvent(){

		String associationType = "closing";
		String mimeType = ".jpg";
		String filename = "tmp";
		String description  = "test";
		long contentLength = 100;
		String absoluteFilePath = "/tmp/tmp";


		AddAttachmentsWorkEvent event = workEventFactory.createAddAttachmentsEvent(workNumbers,
				user, actionName, messageKey, associationType,
				mimeType, filename, description, contentLength, absoluteFilePath);
		checkEvent(event);
		Assert.assertEquals(event.getAssociationType(),associationType);
		Assert.assertEquals(event.getMimeType(),mimeType);
		Assert.assertEquals(event.getFilename(),filename);
		Assert.assertEquals(event.getDescription(),description);
		Assert.assertEquals(event.getContentLength(), contentLength);
		Assert.assertEquals(event.getAbsoluteFilePath(), absoluteFilePath);
		Assert.assertFalse(event.isQueue());
	}

	@Test
	public void createApproveForPaymentWorkEvent(){
		ApproveForPaymentWorkEvent event = workEventFactory.createApproveForPaymentEvent(workNumbers,user,actionName,messageKey);
		Assert.assertTrue(event.isQueue());
		checkEvent(event);
	}

	@Test
	public void createDoNothingEvent(){
		DoNothingEvent event = workEventFactory.createDoNothingEvent(workNumbers, user, actionName, messageKey);
	}

}
