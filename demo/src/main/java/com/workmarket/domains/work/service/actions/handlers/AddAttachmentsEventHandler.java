package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.asset.AssetUploaderService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.domains.work.service.actions.AddAttachmentsWorkEvent;
import com.workmarket.domains.work.service.actions.WorkListFetcherService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;


@Service
public class AddAttachmentsEventHandler implements WorkEventHandler {

	private static final Logger logger = LoggerFactory.getLogger(AddAttachmentsEventHandler.class);

	@Autowired AssetManagementService assetManagementService;
	@Autowired AssetUploaderService assetUploaderService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired AuthenticationService authenticationService;
	@Autowired FeatureEvaluator featureEvaluator;

	@Autowired WorkListFetcherService workListFetcherService;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event){
		Assert.notNull(event);
		Assert.isTrue(event instanceof AddAttachmentsWorkEvent);
		AddAttachmentsWorkEvent addAttachmentsWorkEvent = (AddAttachmentsWorkEvent)event;
		Assert.isTrue(addAttachmentsWorkEvent.isValid());

		AssetDTO dto = new AssetDTO();
		String type = addAttachmentsWorkEvent.getAssociationType();

		dto.setMimeType(addAttachmentsWorkEvent.getMimeType());
		dto.setName(addAttachmentsWorkEvent.getFilename());
		dto.setDescription(addAttachmentsWorkEvent.getDescription());
		dto.setAssociationType(type);
		dto.setFileByteSize(Long.valueOf(addAttachmentsWorkEvent.getContentLength()).intValue());
		dto.setSourceFilePath(addAttachmentsWorkEvent.getAbsoluteFilePath());
		dto.setLargeTransformation(true);
		Asset asset = dto.toAsset();
		Asset savedAsset;

		try {
			savedAsset = assetManagementService.storeAsset(dto, asset, true);
		} catch (Exception e) {
			logger.error("exception storing asset" + e.toString());
			savedAsset=null;
		}
		if(savedAsset == null){
			messageHelper.addMessage(
				addAttachmentsWorkEvent.getResponse().setSuccessful(false), addAttachmentsWorkEvent.getMessageKey() + ".exception"
			);
			return addAttachmentsWorkEvent.getResponse();
		}

		List<Work> works = workListFetcherService.fetchValidatedWork(
			event.getUser(), event.getWorkNumbers(), addAttachmentsWorkEvent.getResponse(), event.getMessageKey()
		);

		assetManagementService.addSavedAssetToWorks(savedAsset, works, type);

		messageHelper.addMessage(addAttachmentsWorkEvent.getResponse(), addAttachmentsWorkEvent.getMessageKey() + ".success");
		return addAttachmentsWorkEvent.getResponse()
				.setSuccessful(true)
				.setData(CollectionUtilities.newObjectMap(
						"successful", true,
						"id", asset.getUUID(),
						"file_name", dto.getName(),
						"uuid", asset.getUUID(),
						"mimeType", dto.getMimeType(),
						"description", dto.getDescription(),
						"type", type,
						"mime_type_icon", MimeTypeUtilities.getMimeIconName(addAttachmentsWorkEvent.getMimeType()),
						"message", addAttachmentsWorkEvent.getResponse().getMessages()));
	}

}
