package com.workmarket.dao.profile;

import java.util.List;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.ProfileActionType;

public interface ProfileActionTypeDAO extends DAOInterface<ProfileActionType> {

	List<ProfileActionType> findAll();
	List<ProfileActionType> findIn(String[] types);

}
