package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.service.business.AssetManagementService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.RemoveAttachmentsEvent;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RemoveAttachmentsEventHandler implements WorkEventHandler{

	@Autowired AssetManagementService assetManagementService;
	@Autowired MessageBundleHelper messageHelper;

	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event){
		Assert.notNull(event);
		Assert.isTrue(event instanceof RemoveAttachmentsEvent);
		@SuppressWarnings("ConstantConditions") RemoveAttachmentsEvent removeAttachmentsEvent = (RemoveAttachmentsEvent) event;
		if(!removeAttachmentsEvent.isValid()){
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
			return event.getResponse();
		}

		try{
			assetManagementService.bulkRemoveAssetFromWork(removeAttachmentsEvent.getWorks(),removeAttachmentsEvent.getAssetId());
		}catch(Exception e){
			messageHelper.addMessage(event.getResponse().setSuccessful(false), event.getMessageKey() + ".exception");
			return event.getResponse();
		}
		assetManagementService.bulkNotifyRemoveAssetFromWork(removeAttachmentsEvent.getWorks(),removeAttachmentsEvent.getAssetId());
		messageHelper.addMessage(event.getResponse().setSuccessful(true), removeAttachmentsEvent.getMessageKey() + ".success");
		return event.getResponse().setSuccessful(true);
	}


}
