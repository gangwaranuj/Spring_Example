package com.workmarket.domains.work.service.actions.handlers;


import com.google.common.collect.Lists;
import com.workmarket.dao.asset.WorkAssetAssociationDAO;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.asset.type.WorkAssetAssociationType;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.test.IntegrationTest;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AddAttachmentsEventHandlerIT extends BaseServiceIT {

	@Autowired AddAttachmentsEventHandler addAttachmentsEventHandler;
	@Autowired WorkAssetAssociationDAO workAssetAssociationDAO;
	@Autowired AssetManagementService assetManagementService;

	Work work1;
	Work work2;
	List<Work> works;
	User user;
	AddAttachmentsWorkEvent event;
	Asset asset;
	AjaxResponseBuilder response = new AjaxResponseBuilder().setSuccessful(true);
	List<String> workNumbers = Lists.newArrayList();
	File file;

	private static final String TEST_IMAGE = "/tmp/testmonkey.jpg";

	@Before
	public void setup() throws Exception{

		user = newEmployeeWithCashBalance();
		work1 = newWork(user.getId());
		work2 = newWork(user.getId());
		works = Lists.newArrayList();
		works.add(work1);
		workNumbers.add(work1.getWorkNumber());

		file = new File(TEST_IMAGE);
		if (file.exists()){
			file.delete();
		}
		Resource image = resourceLoader.getResource("classpath:assets/testmonkey.jpeg");
		IOUtils.copy(image.getInputStream(), new FileOutputStream(TEST_IMAGE));


		event = (AddAttachmentsWorkEvent) new AddAttachmentsWorkEvent.Builder(workNumbers,user,"test","add_attachment",
				WorkAssetAssociationType.ATTACHMENT,MimeType.IMAGE_JPEG.getMimeType(),file.getName(),"test monkey!",file.getTotalSpace(),file.getAbsolutePath())
				.workEventHandler(addAttachmentsEventHandler)
				.response(response)
				.build();
	}


	@Test
	@SuppressWarnings("unchecked")
	public void test_addAttachmentsEventHandler_singleWork(){
		logger.info("addAttachmentsEventHandler");
		addAttachmentsEventHandler.handleEvent(event);

		List<Work> works = (List<Work>)(List<?>)workService.findWorkByWorkNumbers(event.getWorkNumbers());

		List<WorkAssetAssociation> workAssetAssociations = assetManagementService.findAllAssetAssociationsByWork(works);
		Assert.assertEquals(event.getWorkNumbers().size(),workAssetAssociations.size());
	}


	@Test
	public void test_addAttachmentsEventHandler_multipleWork(){
		works.add(work2);
		workNumbers.add(work2.getWorkNumber());
		event = (AddAttachmentsWorkEvent) new AddAttachmentsWorkEvent.Builder(workNumbers,user,"test","add_attachment",
				WorkAssetAssociationType.ATTACHMENT,MimeType.IMAGE_JPEG.getMimeType(),file.getName(),"test monkey!",file.getTotalSpace(),file.getAbsolutePath())
				.work(works)
				.workEventHandler(addAttachmentsEventHandler)
				.response(response)
				.build();
		addAttachmentsEventHandler.handleEvent(event);
		List<WorkAssetAssociation> workAssetAssociations = assetManagementService.findAllAssetAssociationsByWork(event.getWorks());
		Assert.assertEquals(event.getWorks().size(),workAssetAssociations.size());
	}

}
