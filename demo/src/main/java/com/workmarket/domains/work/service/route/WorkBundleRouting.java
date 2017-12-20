package com.workmarket.domains.work.service.route;

import com.workmarket.domains.work.model.WorkBundle;

/**
 *
 * THIS CLASS IS NON TRANSACTIONAL SERVICE
 *
 * Author: rocio
 */
public interface WorkBundleRouting {

	/**
	 * This method will return true if there are still assignments in the bundle pending to be routed.
	 *
	 * @param workBundleId
	 * @return boolean
	 */
	boolean isWorkBundlePendingRouting(long workBundleId);

	boolean isWorkBundlePendingRouting(WorkBundle bundle);

	WorkBundle routeWorkBundle(long workId);

	WorkBundle routeWorkBundleToVendor(long workBundleId);
}
