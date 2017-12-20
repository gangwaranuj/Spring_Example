package com.workmarket.service.admin;

import com.google.common.base.Optional;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import com.workmarket.service.business.dto.AssetDTO;
import com.workmarket.service.exception.asset.AssetSaveException;
import java.util.List;

/**
 * User: alexsilva Date: 10/2/14 Time: 12:24 PM
 */
public interface DefaultBackgroundImageService {

	List<DefaultBackgroundImage> getAll();

	Optional<DefaultBackgroundImage> getCurrentDefaultBackgroundImage();

	void setCurrentDefaultBackgroundImage(Long id);

	DefaultBackgroundImage getDefaultBackgroundImage(Long id);

	DefaultBackgroundImage saveBackgroundImage(AssetDTO dto) throws AssetSaveException;

	void removeBackgroundImage(DefaultBackgroundImage image);

	void removeBackgroundImage(Long id);
}
