package com.workmarket.service.web;

import com.workmarket.domains.model.User;

/**
 * User: micah
 * Date: 9/25/13
 * Time: 7:58 AM
 */
public interface AssignmentStatusImageService {
	ImageAsset getImageAsset(User user, String workNumber);
}
