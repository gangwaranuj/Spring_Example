package com.workmarket.domains.work.service.route;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.workresource.WorkResourceService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.utility.NumberUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.CoreMatchers.is;

@Component
public class WorkBundleRoutingImpl implements WorkBundleRouting {

	private static final Log logger = LogFactory.getLog(WorkBundleRoutingImpl.class);

	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private WorkBundleService workBundleService;
	@Autowired private WorkResourceService workResourceService;
	@Autowired private WorkRoutingService workRoutingService;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private VendorService vendorService;

	@Override
	public boolean isWorkBundlePendingRouting(long workBundleId) {
		WorkBundle bundle = workBundleService.findById(workBundleId);
		return isWorkBundlePendingRouting(bundle);
	}

	@Override
	public boolean isWorkBundlePendingRouting(WorkBundle bundle) {
		return bundle != null && bundle.isRoutable() && !NumberUtilities.isZero(accountRegisterAuthorizationService.findRemainingAuthorizedAmountByWorkBundle(bundle.getId()));
	}

	@Override
	public WorkBundle routeWorkBundle(long workBundleId) {
		WorkBundle bundle = workBundleService.findById(workBundleId, true);
		Assert.notNull(bundle, "Unable to find work bundle " + workBundleId);
		boolean isFirstTimeRouting = isWorkBundlePendingRouting(bundle);
		Map<Long, Boolean> responseMap = Maps.newHashMapWithExpectedSize(bundle.getBundle().size());

		Set<Long> newWorkerIds = makeSet();
		for (Work work : bundle.getBundle()) {
			try {
				Assert.isTrue(work.isInBundle(), "Work is not in a bundle " + work.getId());
				responseMap.put(work.getId(), false);
				// Authorize and add all the workResources at once.
				if (isFirstTimeRouting) {
					WorkAuthorizationResponse authorizationResponse = accountRegisterAuthorizationService.registerWorkInBundleAuthorization(work.getId());
					if (authorizationResponse.fail()) {
						continue;
					}
				}

				// Copy all the resources from the parent to the child
				Set<WorkResource> workResources = workResourceService.saveAllResourcesFromWorkToWork(bundle.getId(), work.getId());
				if (isNotEmpty(workResources)) {
					workRoutingService.openWork(work);
					responseMap.put(work.getId(), true);
					for (WorkResource resource : workResources) {
						newWorkerIds.add(resource.getUser().getId());
					}
				}
			} catch (Exception e) {
				logger.error("[workBundleRouting] Unable to save resource parent/child " + bundle.getId() + "/" + work.getId(), e);
			}
		}

		// Open Bundle if any successful
		List<Boolean> failures = filter(is(Boolean.FALSE), responseMap.values());
		List<Boolean> successes = filter(is(Boolean.TRUE), responseMap.values());
		if (isEmpty(failures) || isNotEmpty(successes)) {
			workRoutingService.openBundle(workBundleId, newWorkerIds);
			Set<Long> workIds = workBundleService.getAllWorkIdsInBundle(bundle);
			workIds.add(workBundleId);
			workSearchService.reindexWorkAsynchronous(workIds);
		}
		return bundle;
	}

	@Override
	public WorkBundle routeWorkBundleToVendor(long workBundleId) {
		WorkBundle bundle = workBundleService.findById(workBundleId, true);
		Assert.notNull(bundle, "Unable to find work bundle " + workBundleId);
		boolean isPendingRouting = isWorkBundlePendingRouting(bundle);
		Set<Long> invitedVendorIds = Sets.newHashSet();

		for (Work work : bundle.getBundle()) {
			if (isPendingRouting) {
				WorkAuthorizationResponse authorizationResponse = accountRegisterAuthorizationService.registerWorkInBundleAuthorization(work.getId());
				if (authorizationResponse.fail()) {
					continue;
				}
			}
			List <Long> vendorIds = vendorService.copyVendorsFromWorkToWork(workBundleId, work.getId());
			invitedVendorIds.addAll(vendorIds);
			workRoutingService.openWork(work.getId());
		}

		workRoutingService.openWork(bundle.getId());
		Set<Long> workIds = workBundleService.getAllWorkIdsInBundle(bundle);
		workIds.add(workBundleId);
		workSearchService.reindexWorkAsynchronous(workIds);
		if (!invitedVendorIds.isEmpty()) {
			vendorService.sendVendorsInvitedNotifications(bundle.getId(), invitedVendorIds);
		}

		return bundle;
	}

	public Set<Long> makeSet() {
		return Sets.newHashSet();
	}
}
