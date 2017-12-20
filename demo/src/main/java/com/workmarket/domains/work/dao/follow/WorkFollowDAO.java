package com.workmarket.domains.work.dao.follow;

import com.google.common.base.Optional;
import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.service.business.dto.WorkFollowDTO;

import java.util.List;

public interface WorkFollowDAO extends DAOInterface<WorkFollow> {

	public Optional<WorkFollow> getWorkFollow(Long workId, Long userId);

	List<WorkFollow> getFollowers(Long workId);

	List<WorkFollowDTO> getWorkFollowDTOs(Long workId);

	List<WorkFollow> getFollowers(String workNumber);

	boolean isFollowingWork(Long workId, Long userId);
}
