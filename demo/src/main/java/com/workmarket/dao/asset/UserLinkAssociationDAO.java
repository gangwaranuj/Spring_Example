package com.workmarket.dao.asset;

import com.workmarket.dao.DeletableDAOInterface;
import com.workmarket.domains.model.asset.UserLinkAssociation;

import java.util.List;

public interface UserLinkAssociationDAO extends DeletableDAOInterface<UserLinkAssociation> {

	List<UserLinkAssociation> findUserLinkAssociationsByUserId(Long userId);
}
