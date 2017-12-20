package com.workmarket.dao.tag;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.tag.TagPagination;
import com.workmarket.domains.model.tag.UserTag;
import com.workmarket.domains.model.tag.UserTagAssociation;

import java.util.List;

public interface UserTagAssociationDAO extends DAOInterface<UserTagAssociation> {

	TagPagination findAllUserTags(Long userId, TagPagination pagination);

	public List<UserTag> findAllUserTags();
}