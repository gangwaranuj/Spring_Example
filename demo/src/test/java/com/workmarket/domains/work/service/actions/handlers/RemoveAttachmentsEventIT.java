package com.workmarket.domains.work.service.actions.handlers;

import com.google.common.collect.Lists;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
import com.workmarket.domains.work.service.actions.WorkEventFactory;
import com.workmarket.service.infra.business.AuthenticationService;
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
public class RemoveAttachmentsEventIT extends BaseServiceIT {

	@Autowired RemoveAttachmentsEventHandler removeAttachmentsEventHandler;
	@Autowired AssetManagementService assetManagementService;
	@Autowired WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired WorkEventFactory workEventFactory;
	@Autowired AuthenticationService authenticationService;

	Work work1;
	Work work2;
	List<Work> works;
	List<String> workNumbers;
	User user;
	RemoveAttachmentsEvent event;
	Asset asset;
	WorkAssetAssociation workAssetAssociation;
	AjaxResponseBuilder response;

	@Before
	public void setup() throws Exception{

		user = newEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);

		work1 = newWork(user.getId());
		work2 = newWork(user.getId());
		works = Lists.newArrayList();
		works.add(work1);
		workNumbers = Lists.newArrayList();
		workNumbers.add(work1.getWorkNumber());
		AssetDTO assetDTO = newAssetDTO();
		workAssetAssociation = assetManagementService.storeAssetForWork(assetDTO, work1.getId());
		asset = workAssetAssociation.getAsset();
		response = new AjaxResponseBuilder().setSuccessful(true);
		event = (RemoveAttachmentsEvent) new RemoveAttachmentsEvent.Builder(workNumbers, user, "test", "get_attachments", asset.getUUID())
				.work(works)
				.workEventHandler(removeAttachmentsEventHandler)
				.response(response)
				.build();
	}


	@Test
	@Transactional
	public void test_removeAttachments_single(){
		removeAttachmentsEventHandler.handleEvent(event);
		Assert.assertTrue(event.getResponse().isSuccessful());
		WorkAssetAssociation workAssetAssociation = workAssetAssociationDAO.findWorkAssetAssociation(work1.getId(), asset.getId());
		Assert.assertTrue(workAssetAssociation.getDeleted());
	}

	@Test
	@Transactional
	public void test_removeAttachments_multiple(){
		works.add(work2);
		event = (RemoveAttachmentsEvent) new RemoveAttachmentsEvent.Builder(workNumbers,user,"test","get_attachments",asset.getUUID())
				.work(works)
				.workEventHandler(removeAttachmentsEventHandler)
				.response(response)
				.build();
		removeAttachmentsEventHandler.handleEvent(event);
		Assert.assertTrue(event.getResponse().isSuccessful());

		List<Long> workIds = Lists.newArrayList();
		for(Work work:event.getWorks()){
			workIds.add(work.getId());
		}
		List<WorkAssetAssociation> workAssetAssociations = workAssetAssociationDAO.findWorkAssetAssociationsByWork(workIds,asset.getId());
		for(WorkAssetAssociation workAssetAssociation : workAssetAssociations){
			Assert.assertTrue(workAssetAssociation.getDeleted());
		}
	}


}
