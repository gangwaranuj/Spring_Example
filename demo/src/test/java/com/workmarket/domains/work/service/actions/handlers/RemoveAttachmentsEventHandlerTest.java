package com.workmarket.domains.work.service.actions.handlers;


import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
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
public class RemoveAttachmentsEventHandlerTest {

	@Mock AssetManagementService assetManagementService;
	@Mock MessageBundleHelper messageBundleHelper;
	@InjectMocks RemoveAttachmentsEventHandler removeAttachmentsEventHandler;

	RemoveAttachmentsEvent event;
	AjaxResponseBuilder response;
	List<String> workNumbers;
	List<Work> works;
	Work work;



	@Before
	public void setup(){

		work = mock(Work.class);
		when(work.getId()).thenReturn(1L);


		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);

		workNumbers = mock(ArrayList.class);
		when(workNumbers.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);
		when(response.setSuccessful(anyBoolean())).thenReturn(response);

		event = mock(RemoveAttachmentsEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("test");
		when(event.getWorkNumbers()).thenReturn(workNumbers);
		when(event.getAssetId()).thenReturn("test");
		when(event.getWorks()).thenReturn(works);
		when(event.isValid()).thenReturn(true);
	}

	@Test(expected=Exception.class)
	public void test_handleEvent_nullEvent(){
		removeAttachmentsEventHandler.handleEvent(null);
	}

	@Test
	public void test_handleEvent_eventValidateReturnsFalse(){
		when(event.isValid()).thenReturn(false);
		removeAttachmentsEventHandler.handleEvent(event);
		verify(event,times(2)).getResponse();
	}


	@Test
	public void test_handleEvent_removeAssetFromWork(){
		removeAttachmentsEventHandler.handleEvent(event);
		verify(assetManagementService,times(1)).bulkRemoveAssetFromWork(event.getWorks(),event.getAssetId());
	}

	@Test
	public void test_handleEvent_bulkNotifyRemoveAssetFromWork(){
		removeAttachmentsEventHandler.handleEvent(event);
		verify(assetManagementService,times(1)).bulkNotifyRemoveAssetFromWork(event.getWorks(),event.getAssetId());
	}

	@Test
	public void test_handleEvent_removeAssetFromWork_throwException(){
		doThrow(Exception.class).when(assetManagementService).bulkRemoveAssetFromWork(anyList(), anyString());
		removeAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
	}

	@Test
	public void test_handleEvent_responseSuccess(){
		removeAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
	}

}
