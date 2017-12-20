package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.workmarket.domains.work.dao.BaseWorkDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.dao.follow.WorkFollowDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.follow.WorkFollow;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.EncryptionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
public class WorkFollowServiceImpl implements WorkFollowService {

	private static final Log logger = LogFactory.getLog(WorkFollowServiceImpl.class);

	@Autowired private WorkFollowDAO workFollowDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private BaseWorkDAO baseWorkDAO;
	@Autowired private EventRouter eventRouter;
	@Autowired private WorkFollowCache workFollowCache;
	@Autowired private WorkService workService;

	@Override
	public boolean toggleFollowWork(String workNumber, Long userId) {
		Assert.hasText(workNumber);
		Assert.notNull(userId);

		AbstractWork work = baseWorkDAO.findByWorkNumber(workNumber);
		return toggleFollowWork(work, userId);
	}

	private boolean toggleFollowWork(AbstractWork work, Long userId) {
		User user = userDAO.get(userId);
		if (!canUserFollowWork(user, work)) {
			return false;
		}

		Optional<WorkFollow> result = workFollowDAO.getWorkFollow(work.getId(), userId);
		WorkFollow workFollow;

		if (!result.isPresent()) {
			workFollow = new WorkFollow(work, user);
		} else if (result.get().getDeleted()) {
			workFollow = result.get();
			workFollow.setDeleted(false);
		} else {
			workFollow = result.get();
			workFollow.setDeleted(true);
		}

		workFollowDAO.saveOrUpdate(workFollow);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));

		return true;
	}

	@Override
	public boolean isFollowingWork(Long workId, Long userId) {
		Assert.notNull(workId);
		Assert.notNull(userId);

		return workFollowDAO.isFollowingWork(workId, userId);
	}

	@Override
	public void saveFollowers(Long workId, List<Long> followers, boolean deleteOmitted) {
		if (workId == null || isEmpty(followers)) {
			return;
		}

		AbstractWork work = baseWorkDAO.get(workId);
		Assert.notNull(work);

		saveFollowers(work, followers, deleteOmitted);
	}

	@Override
	public void saveFollowers(String workNumber, List<Long> followers, boolean deleteOmitted) {
		Assert.notNull(followers);
		Assert.hasText(workNumber);

		AbstractWork work = baseWorkDAO.findByWorkNumber(workNumber);
		Assert.notNull(work);
		saveFollowers(work, followers, deleteOmitted);
	}

	private void saveFollowers(AbstractWork work, List<Long> followers, boolean deleteOmitted) {
		if (deleteOmitted) {
			// remove existing followers in case we have deleted existing followers
			List<WorkFollow> workFollows = workFollowDAO.getFollowers(work.getId());

			for (WorkFollow workFollow : workFollows) {
				workFollow.setDeleted(true);
			}
		}

		// save all followers
		for (Long followerId : followers) {
			User follower = userDAO.findUserById(followerId);

			if (!canUserFollowWork(follower, work)) {
				continue;
			}

			Optional<WorkFollow> result = workFollowDAO.getWorkFollow(work.getId(), follower.getId());
			WorkFollow workFollow;

			if (!result.isPresent()) {
				workFollow = makeWorkFollow(work, follower);
			} else {
				workFollow = result.get();
			}

			workFollow.setDeleted(false);
			workFollowDAO.saveOrUpdate(workFollow);
		}

		workFollowCache.evict(work.getId());
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(work.getId()));
	}

	@Override
	public Optional<WorkFollow> removeWorkFollower(String workNumber, Long workFollowId) {
		Assert.notNull(workFollowId);
		Assert.notNull(workNumber);

		Optional<WorkFollow> workFollowOptional = getWorkFollow(workFollowId);
		if (workFollowOptional.isPresent()) {
			workFollowOptional.get().setDeleted(true);
			workFollowCache.evict(workService.findWorkId(workNumber));
			return workFollowOptional;
		}

		return Optional.absent();
	}

	public WorkFollow makeWorkFollow(AbstractWork work, User follower) {
		return new WorkFollow(work, follower);
	}

	@Override
	public List<WorkFollow> getWorkFollowers(Long workId) {
		Assert.notNull(workId);

		return workFollowDAO.getFollowers(workId);
	}

	@Override
	public List<WorkFollowDTO> getWorkFollowDTOs(String workNumber) {
		Assert.hasText(workNumber);

		Long workId = workService.findWorkId(workNumber);
		if (workId == null) {
			return Collections.emptyList();
		}

		return getWorkFollowDTOs(workId);
	}

	@Override
	public List<WorkFollowDTO> getWorkFollowDTOs(Long workId) {
		Assert.notNull(workId);

		Optional<List<WorkFollowDTO>> followersOptional = workFollowCache.get(workId);
		if (followersOptional.isPresent()) {
			return followersOptional.get();
		}

		List<WorkFollowDTO> followers = workFollowDAO.getWorkFollowDTOs(workId);
		workFollowCache.set(workId, followers);
		return followers;
	}

	@Override
	public Optional<WorkFollow> getWorkFollow(Long workFollowId) {
		return Optional.fromNullable(workFollowDAO.get(workFollowId));
	}

	@Override
	public void saveOrUpdateWorkFollow(WorkFollow workFollow) {
		Assert.notNull(workFollow);

		workFollowDAO.saveOrUpdate(workFollow);

		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(workFollow.getWork().getId()));
		workFollowCache.evict(workFollow.getWork().getId());
	}

	private boolean canUserFollowWork(User user, AbstractWork work) {
		if (user == null || work == null)
			return false;

		// must be in same company
		if (!user.getCompany().getId().equals(work.getCompany().getId())) {
			return false;
		}

		// owner can't follow assignment
		return !user.getId().equals(work.getBuyer().getId());
	}

	@Override
	public WorkFollow findWorkFollowByEncryptedId(String encryptedWorkFollowId) {
		Assert.notNull(encryptedWorkFollowId);
		Long workFollowId;
		try {
			workFollowId = EncryptionUtilities.decryptLong(encryptedWorkFollowId);
		} catch (EncryptionOperationNotPossibleException e) {
			logger.error(e.getMessage());
			return null;
		}
		return workFollowDAO.get(workFollowId);
	}
}
