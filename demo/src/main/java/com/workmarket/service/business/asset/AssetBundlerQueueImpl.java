package com.workmarket.service.business.asset;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.ListMultimap;
import com.workmarket.domains.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workmarket.utility.DateUtilities;

@Component
public class AssetBundlerQueueImpl implements AssetBundlerQueue, Runnable, InitializingBean {
	
	private static final Log logger = LogFactory.getLog(AssetBundlerQueueImpl.class);

	private static final ExecutorService runner = Executors.newSingleThreadExecutor();
	private static final BlockingQueue<AssetBundle> queue = new ArrayBlockingQueue<>(1000, false);

	@Autowired private AssetBundlerService assetBundlerService;
	
	public void bundleAssetsForUser(List<String> assetUuids, Long userId) {
		AssetBundle bundle = new AssetBundle();
		bundle.setUserId(userId);
		bundle.setAssetUuids(assetUuids);
		bundle.setRequestedOn(DateUtilities.getCalendarNow());
		queue.offer(bundle);
	}

	public void bundleAssetsForUser(List<String> assetUuids,List<String> assignmentIds, Long userId) {
		AssetAssignmentBundle bundle = new AssetAssignmentBundle();
		bundle.setUserId(userId);
		bundle.setAssetUuids(assetUuids);
		bundle.setAssetAssignments(assignmentIds);
		bundle.setRequestedOn(DateUtilities.getCalendarNow());
		queue.offer(bundle);
	}

	public void bundleAssetsForUser(ListMultimap<User,String>  assetMap, Long userId) {
		AssetGroupBundle bundle = new AssetGroupBundle();
		bundle.setUserId(userId);
		bundle.setUserAssetMap(assetMap);
		bundle.setRequestedOn(DateUtilities.getCalendarNow());
		queue.offer(bundle);
	}

	@Override
	public void run() {
		while (true) {
			try {
				assetBundlerService.sendAssetBundle(queue.take());
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		runner.execute(this);
	}
}
