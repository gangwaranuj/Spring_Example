package com.workmarket.service.thrift.transactional.work;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.thrift.work.WorkResponse;

import java.util.Set;

public interface WorkResponseBuilder {

	WorkResponse buildWorkResponse(AbstractWork work, User currentUser, Set<WorkRequestInfo> includes) throws Exception;

	WorkResponse buildWorkDetailResponseLight(long workId, Long userId, Set<WorkRequestInfo> includes) throws Exception;

	WorkResponse buildWorkDetailResponse(long workId, long userId) throws Exception;

	WorkResponse buildWorkResponse(long workId, long userId, Set<WorkRequestInfo> includes) throws Exception;

}
