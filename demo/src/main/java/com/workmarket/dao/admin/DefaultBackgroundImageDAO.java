package com.workmarket.dao.admin;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;

public interface DefaultBackgroundImageDAO extends DAOInterface<DefaultBackgroundImage> {

	DefaultBackgroundImage getDefaultBackgroundImage(Long id);

	void setCurrentDefaultBackgroundImage(Long id);

	Optional<DefaultBackgroundImage> getCurrentDefaultBackgroundImage();

	void deleteBackgroundImage(Long id);

	DefaultBackgroundImage addBackgroundImage(DefaultBackgroundImage image);

}
