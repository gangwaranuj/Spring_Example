package com.workmarket.dao.profile;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.ProfilePhoneAssociation;
import com.workmarket.domains.model.directory.Phone;

import java.util.List;

public interface ProfilePhoneAssociationDAO extends DAOInterface<ProfilePhoneAssociation> {

	List<Phone> findPhonesByProfileId(long profileId);
}
