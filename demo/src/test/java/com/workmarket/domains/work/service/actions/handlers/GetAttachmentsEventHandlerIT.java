package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.work.service.actions.GetAttachmentsEvent;
import com.workmarket.domains.work.service.actions.WorkEventFactory;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class GetAttachmentsEventHandlerIT extends BaseServiceIT {

	@Autowired GetAttachmentsEventHandler getAttachmentsEventHandler;
	@Autowired WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired WorkEventFactory workEventFactory;


	Work work1;
	Work work2;
	List<Work> works;
	User user;
	GetAttachmentsEvent event;
	Asset asset;
	WorkAssetAssociation workAssetAssociation;
	AjaxResponseBuilder response;

	@Before
	@Transactional
	public void setup() throws Exception{
		user = newEmployeeWithCashBalance();
		work1 = newWork(user.getId());
		work2 = newWork(user.getId());
		works = Lists.newArrayList();
		works.add(work1);
		AssetDTO assetDTO = newAssetDTO();
		workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work1.getId());
		asset = workAssetAssociation.getAsset();
		response = new AjaxResponseBuilder().setSuccessful(true);
		List<String> workNumbers = Lists.newArrayList();
		event = (GetAttachmentsEvent) new GetAttachmentsEvent.Builder(workNumbers, user, "test", "get_attachments")
				.work(works)
				.workEventHandler(getAttachmentsEventHandler)
				.response(response)
				.build();
	}

	@Test
	@Transactional
	public void test_getAttachmentsEventHandler_single(){
		AjaxResponseBuilder response = getAttachmentsEventHandler.handleEvent(event);
		Assert.assertTrue(response.isSuccessful());
		Assert.assertNotNull(response.getData());
		Assert.assertEquals(response.getData().size(), works.size());
	}



}
