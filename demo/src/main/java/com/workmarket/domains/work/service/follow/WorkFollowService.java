package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.service.business.dto.WorkFollowDTO;

import java.util.List;

public interface WorkFollowService {

	boolean toggleFollowWork(String workNumber, Long userId);

	boolean isFollowingWork(Long workId, Long userId);

	void saveFollowers(Long workId, List<Long> followers, boolean deleteOmitted);

	void saveFollowers(String workNumber, List<Long> followers, boolean deleteOmitted);

	Optional<WorkFollow> removeWorkFollower(String workNumber, Long workFollowId);

	List<WorkFollow> getWorkFollowers(Long workId);

	List<WorkFollowDTO> getWorkFollowDTOs(String workNumber);

	List<WorkFollowDTO> getWorkFollowDTOs(Long workId);

	Optional<WorkFollow> getWorkFollow(Long workFollowId);

	void saveOrUpdateWorkFollow(WorkFollow workFollow);

	WorkFollow findWorkFollowByEncryptedId(String encryptedWorkFollowId);
}
