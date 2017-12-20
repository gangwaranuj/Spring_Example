package com.workmarket.domains.work.service.actions.handlers;

import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.domains.work.service.actions.AbstractWorkEvent;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.MessageBundleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetAttachmentsEventHandler implements WorkEventHandler {

	@Autowired AssetManagementService assetManagementService;
	@Autowired MessageBundleHelper messageHelper;
	@Autowired AuthenticationService authenticationService;
	@Autowired FeatureEvaluator featureEvaluator;

	@Override
	public AjaxResponseBuilder handleEvent(AbstractWorkEvent event) {
		Assert.notNull(event);
		Assert.isTrue(event.isValid());
		Map<String, Object> assetIconMap = new HashMap<>();

		List<WorkAssetAssociation> workAssetAssociations =
				assetManagementService.findAllAssetAssociationsByWork(event.getWorks());
		if (workAssetAssociations == null) {
			event.getResponse().setData(new HashMap<String, Object>());
			messageHelper.addMessage(event.getResponse().setSuccessful(false),event.getMessageKey() + ".exception");
			return event.getResponse();
		}

		for (WorkAssetAssociation workAssetAssociation : workAssetAssociations) {
			if (workAssetAssociation.isDeliverable()) {
				continue;
			}
			Asset a = workAssetAssociation.getAsset();
			Map oneAsset = CollectionUtilities.newObjectMap(
					"successful", true,
					"id", a.getUUID(),
					"uuid", a.getUUID(),
					"file_name", a.getName(),
					"mimeType", a.getMimeType(),
					"description", a.getDescription(),
					"mime_type_icon", MimeTypeUtilities.getMimeIconName(a.getMimeType())
			);
			assetIconMap.put(Long.toString(a.getId()), oneAsset);
		}
		event.getResponse()
				.setData(assetIconMap)
				.setSuccessful(true);
		messageHelper.addMessage(event.getResponse(), event.getMessageKey() + ".success");
		return event.getResponse();
	}

}
