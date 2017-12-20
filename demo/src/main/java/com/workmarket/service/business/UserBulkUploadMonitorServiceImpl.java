package com.workmarket.service.business;


import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.event.BulkUserUploadFinishedEvent;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.upload.users.model.BulkUserUploadCompletionStatus;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.event.EventRouter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserBulkUploadMonitorServiceImpl implements UserBulkUploadMonitorService {

	@Autowired private RedisAdapter redisAdapter;
	@Autowired private UserService userService;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;

	private static final String ALL_BULK_USER_UPLOAD_IN_PROGRESS_KEY = RedisFilters.userBulkUserUploadAllInProgressKey();


	@Override
	@Scheduled(fixedDelay = 30000, initialDelay = 120000)
	public void monitor() {
		checkProgress();
	}

	private void checkProgress() {
		Set<String> uploadKeys = redisAdapter.getSet(ALL_BULK_USER_UPLOAD_IN_PROGRESS_KEY);
		for (String key : uploadKeys) {
			//check progress
			Pair<Long, String> pairOfUserIdAndUUID = extractUserIdAndUUID(key);
			String uploadSizeKey = RedisFilters.userBulkUserUploadSizeKey(pairOfUserIdAndUUID.getLeft(), pairOfUserIdAndUUID.getRight());
			Integer totalUploadSize = Integer.valueOf((String) redisAdapter.get(uploadSizeKey).or("0"));

			String uploadSuccessKey = RedisFilters.userBulkUserUploadSuccessCounterKey(pairOfUserIdAndUUID.getLeft(), pairOfUserIdAndUUID.getRight());
			Integer successCounter = Integer.valueOf((String) redisAdapter.get(uploadSuccessKey).or("0"));

			String failedUpLoadKey = RedisFilters.userBulkUserFailedUploadKey(pairOfUserIdAndUUID.getLeft(), pairOfUserIdAndUUID.getRight());

			if (totalUploadSize == 0 || successCounter == 0) {
				continue;
			}

			if (successCounter < totalUploadSize) {
				Set<String> failedUpload = redisAdapter.getSet(failedUpLoadKey);
				if (failedUpload.size() + successCounter == totalUploadSize) {
					BulkUserUploadResponse response = buildBulkUserUploadResponse(pairOfUserIdAndUUID, BulkUserUploadCompletionStatus.COMPLETED_WITH_SYSTEM_ERROR);
					BulkUserUploadFinishedEvent event = eventFactory.buildBulkUserUploadFinishedEvent(response);
					eventRouter.sendEvent(event);
				}
			} else {
				BulkUserUploadResponse response = buildBulkUserUploadResponse(pairOfUserIdAndUUID, BulkUserUploadCompletionStatus.COMPLETED_WITH_NO_ERROR);
				BulkUserUploadFinishedEvent event = eventFactory.buildBulkUserUploadFinishedEvent(response);
				eventRouter.sendEvent(event);
			}
		}
	}

	private Pair<Long, String> extractUserIdAndUUID(String key) {
		Pair<Long, String> pairOfUserIdAndUUID = Pair.of(-1L, "");
		String[] tokens = key.split(":");
		if(tokens.length == 5) {
			pairOfUserIdAndUUID = Pair.of(Long.valueOf(tokens[2]), tokens[4]);
		}
		return pairOfUserIdAndUUID;
	}

	private BulkUserUploadResponse buildBulkUserUploadResponse(Pair<Long, String> pairOfUserIdAndUUID, BulkUserUploadCompletionStatus status) {
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		response.setUser(userService.getUser(pairOfUserIdAndUUID.getLeft()));
		response.setFileUUID(pairOfUserIdAndUUID.getRight());
		response.setStatus(status);
		return response;
	}
}