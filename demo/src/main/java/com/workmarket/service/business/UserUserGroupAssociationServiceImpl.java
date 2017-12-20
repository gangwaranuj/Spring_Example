package com.workmarket.service.business;

import com.workmarket.domains.groups.dao.UserUserGroupAssociationDAO;
import com.workmarket.domains.groups.model.UserUserGroupAssociation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: alexsilva Date: 3/10/14 Time: 6:19 PM
 */

@Service
public class UserUserGroupAssociationServiceImpl implements UserUserGroupAssociationService {

	@Autowired private UserUserGroupAssociationDAO userUserGroupAssociationDAO;

	@Override
	public void saveOrUpdateAssociation(UserUserGroupAssociation userUserGroupAssociation) {
		userUserGroupAssociationDAO.saveOrUpdate(userUserGroupAssociation);
	}
}
