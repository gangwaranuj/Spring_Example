package com.workmarket.domains.forums.dao;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.forums.model.UserForumBan;
import com.workmarket.domains.forums.model.UserForumBanPagination;

import java.util.List;

public interface UserForumBanDAO extends DeletableDAOInterface<UserForumBan> {

	UserForumBanPagination getAllBannedUsers(UserForumBanPagination pagination);
	UserForumBan getBannedUser(Long userId);
	List<Long> getAllBannedUsersOnPost(Long postId);
}
