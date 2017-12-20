package com.workmarket.service.admin;

import com.google.common.base.Optional;
import com.workmarket.dao.admin.DefaultBackgroundImageDAO;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.domains.model.asset.type.UserAssetAssociationType;
import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.service.exception.asset.AssetSaveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * User: alexsilva Date: 10/2/14 Time: 12:26 PM
 */

@Service
public class DefaultBackgroundImageServiceImpl implements DefaultBackgroundImageService {

	@Autowired DefaultBackgroundImageDAO defaultBackgroundImageDAO;
	@Autowired AssetManagementService assetManagementService;

	@Override
	public List<DefaultBackgroundImage> getAll() {
		return defaultBackgroundImageDAO.getAll();
	}

	@Override
	public DefaultBackgroundImage getDefaultBackgroundImage(Long id) {
		Assert.notNull(id);
		return defaultBackgroundImageDAO.getDefaultBackgroundImage(id);
	}

	@Override
	public Optional<DefaultBackgroundImage> getCurrentDefaultBackgroundImage() {
		return defaultBackgroundImageDAO.getCurrentDefaultBackgroundImage();
	}

	@Override
	public void setCurrentDefaultBackgroundImage(Long id) {
		Assert.notNull(id);
		defaultBackgroundImageDAO.setCurrentDefaultBackgroundImage(id);
	}

	@Override
	public void removeBackgroundImage(DefaultBackgroundImage image) {
		if (image == null) { return; }
		assetManagementService.removeAssetFromUser(image.getAsset().getId(), Constants.WORKMARKET_SYSTEM_USER_ID) ;
		defaultBackgroundImageDAO.deleteBackgroundImage(image.getId());
	}

	@Override
	public void removeBackgroundImage(Long id) {
		if (id == null) { return; }
		DefaultBackgroundImage image = defaultBackgroundImageDAO.getDefaultBackgroundImage(id);
		removeBackgroundImage(image);
	}

	@Override
	public DefaultBackgroundImage saveBackgroundImage(AssetDTO dto) throws AssetSaveException {
		Assert.notNull(dto);
		dto.setAssociationType(UserAssetAssociationType.BACKGROUND_IMAGE);

		Asset asset;
		try {
			asset = assetManagementService.storeAssetForUser(dto, Constants.WORKMARKET_SYSTEM_USER_ID);
		} catch (Exception e) {
			throw new AssetSaveException(e);
		}

		DefaultBackgroundImage image = new DefaultBackgroundImage();
		image.setAsset(asset);
		return defaultBackgroundImageDAO.addBackgroundImage(image);
	}

}
