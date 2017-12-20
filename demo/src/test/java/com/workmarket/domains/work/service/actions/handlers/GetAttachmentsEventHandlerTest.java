package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.domains.work.service.actions.GetAttachmentsEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetAttachmentsEventHandlerTest {

	@Mock MessageBundleHelper messageBundleHelper;
	@Mock AssetManagementService assetManagementService;
	@InjectMocks GetAttachmentsEventHandler getAttachmentsEventHandler;


	AjaxResponseBuilder response;
	List<Work> works;
	Work work;
	GetAttachmentsEvent event;
	List<WorkAssetAssociation> workAssetAssociations;
	Iterator<WorkAssetAssociation> mockIter;
	WorkAssetAssociation workAssetAssociation;
	Asset asset;

	@Before
	public void setup(){
		work = mock(Work.class);
		when(work.getId()).thenReturn(1L);


		works = mock(ArrayList.class);
		when(works.isEmpty()).thenReturn(false);

		response = mock(AjaxResponseBuilder.class);
		when(response.setSuccessful(anyBoolean())).thenReturn(response);
		when(response.setData(any(Map.class))).thenReturn(response);

		event = mock(GetAttachmentsEvent.class);
		when(event.getResponse()).thenReturn(response);
		when(event.getMessageKey()).thenReturn("test");
		when(event.getWorks()).thenReturn(works);
		when(event.isValid()).thenReturn(true);

		asset = mock(Asset.class);

		workAssetAssociation = mock(WorkAssetAssociation.class);
		when(workAssetAssociation.getAsset()).thenReturn(asset);

		mockIter = mock(Iterator.class);
		when(mockIter.hasNext()).thenReturn(true,false);
		when(mockIter.next()).thenReturn(workAssetAssociation,(WorkAssetAssociation) null);

		workAssetAssociations=mock(ArrayList.class);
		when(workAssetAssociations.iterator()).thenReturn(mockIter);

		when(assetManagementService.findAllAssetAssociationsByWork(event.getWorks())).thenReturn(workAssetAssociations);
	}

	@Test(expected = Exception.class)
	public void test_getAttachmentsEventHandler_nullEvent(){
		getAttachmentsEventHandler.handleEvent(null);
	}

	@Test(expected = Exception.class)
	public void test_getAttachmentsEventHandler_validateThrowsException(){
		doThrow(Exception.class).when(event).isValid();
		getAttachmentsEventHandler.handleEvent(event);
	}

	@Test
	public void test_getAttachmentsEventHandler_single(){
		getAttachmentsEventHandler.handleEvent(event);
		verify(assetManagementService).findAllAssetAssociationsByWork(event.getWorks());
	}

	@Test
	public void test_getAttachmentsEventHandler_findAllAssetReturnsNull(){
		when(assetManagementService.findAllAssetAssociationsByWork(event.getWorks())).thenReturn(null);
		getAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
	}

	@Test
	public void test_getAttachmentsEventHandler_findAllAssetReturnsEmpty(){
		when(mockIter.hasNext()).thenReturn(false);
		getAttachmentsEventHandler.handleEvent(event);
		verify(workAssetAssociation,never()).getAsset();
	}

	@Test
	public void test_getAttachmentsEventHandler_returnSuccess(){
		getAttachmentsEventHandler.handleEvent(event);
		verify(messageBundleHelper).addMessage(event.getResponse().setSuccessful(true), event.getMessageKey() + ".success");
	}
}
