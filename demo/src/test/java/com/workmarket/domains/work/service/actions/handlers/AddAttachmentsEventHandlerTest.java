package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.domains.work.service.actions.WorkListFetcherService;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AddAttachmentsEventHandlerTest {

	@Mock AssetManagementService assetManagementService;
	@Mock MessageBundleHelper messageBundleHelper;
	@Mock WorkListFetcherService workListFetcherService;
	@InjectMocks AddAttachmentsEventHandler addAttachmentsEventHandler;

	AddAttachmentsWorkEvent event;
	AjaxResponseBuilder response;
	List<String> workNumbers;

	Work work;
	Set<WorkContext> workContexts;
	Asset savedAsset;

	@Before
	public void setup() throws Exception{

		savedAsset = mock(Asset.class);

		work = mock(Work.class);
		when(work.getId()).thenReturn(1L);


		workNumbers = mock(ArrayList.class);
		when(workNumbers.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);
		when(response.setSuccessful(anyBoolean())).thenReturn(response);

		workContexts = mock(HashSet.class);
		when(workContexts.contains(WorkContext.ACTIVE_RESOURCE)).thenReturn(true);
		event = mock(AddAttachmentsWorkEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("test");
		when(event.getWorkNumbers()).thenReturn(workNumbers);
		when(event.isValid()).thenReturn(true);
		when(event.getAbsoluteFilePath()).thenReturn("/tmp/temp");
		when(event.getAssociationType()).thenReturn(WorkAssetAssociationType.ATTACHMENT);
		when(event.getContentLength()).thenReturn(100L);
		when(event.getDescription()).thenReturn("test");
		when(event.getMimeType()).thenReturn(MimeType.IMAGE_PNG.getMimeType());
		when(event.getWorkContexts()).thenReturn(workContexts);

		when(assetManagementService.storeAsset(any(AssetDTO.class),any(Asset.class),any(Boolean.class))).thenReturn(savedAsset);
	}

	@Test(expected = Exception.class)
	public void test_addAttachmentsEventHandler_nullEvent(){
		addAttachmentsEventHandler.handleEvent(null);

	}

	@Test(expected = Exception.class)
	public void test_addAttachmentsEventHandler_validateFails(){
		when(event.isValid()).thenReturn(false);
		addAttachmentsEventHandler.handleEvent(event);
	}


	@Test
	public void test_addAttachmentsEventHandler_createAsset(){
		//times 2, once for validator, once for setting dto
		addAttachmentsEventHandler.handleEvent(event);
		verify(event,times(2)).getMimeType();
		verify(event,times(1)).getAssociationType();
		verify(event,times(1)).getContentLength();
		verify(event,times(1)).getDescription();
		verify(event,times(1)).getFilename();
	}

	@Test
	public void test_addAttachmentsEventHandler_storeAsset() throws Exception{
		addAttachmentsEventHandler.handleEvent(event);
		verify(assetManagementService).storeAsset(any(AssetDTO.class),any(Asset.class),any(Boolean.class));
	}

	@Test
	public void test_addAttachmentsEventHandler_storeAssetReturnsNull() throws Exception{
		when(assetManagementService.storeAsset(any(AssetDTO.class),any(Asset.class),any(Boolean.class))).thenReturn(null);
		addAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
		verify(assetManagementService,never()).addSavedAssetToWorks(savedAsset,event.getWorks(),"closing");
	}

	@Test
	public void test_addAttachmentsEventHandler_storeAssetThrowsException() throws Exception{
		doThrow(Exception.class).when(assetManagementService).storeAsset(any(AssetDTO.class),any(Asset.class),any(Boolean.class));
		addAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
		verify(assetManagementService,never()).addSavedAssetToWorks(savedAsset,event.getWorks(),"closing");
	}

	@Test
	public void test_addAttachmentsEventHandler_addMessage(){
		addAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse(),event.getMessageKey() + ".success");
	}

	@Test
	public void test_addAttachmentsEventHandler_returnsSuccessResponse(){
		addAttachmentsEventHandler.handleEvent(event);
		verify(event,times(4)).getResponse();
		verify(response).setSuccessful(true);
		verify(response).setData(any(Map.class));
	}

}
