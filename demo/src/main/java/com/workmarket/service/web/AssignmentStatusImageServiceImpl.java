package com.workmarket.service.web;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("assignmentStatusService")
public class AssignmentStatusImageServiceImpl implements AssignmentStatusImageService {
	@Autowired WorkService workService;
	@Autowired WorkBundleService workBundleService;

	@Override
	public ImageAsset getImageAsset(User user, String workNumber) {
		boolean allowed = user != null && workNumber != null && workService.isWorkStatusAccessibleForUser(workNumber, user.getId());
		boolean available = workNumber != null && workService.isWorkStatusForWorkByWorkNumber(workNumber, WorkStatusType.SENT);
		boolean isAssignmentBundle = workNumber != null && workBundleService.isAssignmentBundle(workNumber);

		if (allowed && available && isAssignmentBundle) {
			return ImageAsset.BUNDLE_AVAILABLE;
		} else if (allowed && available) {
			return ImageAsset.AVAILABLE;
		} else if (allowed && isAssignmentBundle) {
			return ImageAsset.BUNDLE_NOT_AVAILABLE;
		} else if (allowed) {
			return ImageAsset.NOT_AVAILABLE;
		} else if (isAssignmentBundle) {
			return ImageAsset.BUNDLE_NOT_ALLOWED;
		} else {
			return ImageAsset.NOT_ALLOWED;
		}
	}
}
