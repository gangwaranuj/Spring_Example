package com.workmarket.dao.notification;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.notification.UserDeviceAssociation;

import java.util.List;

/**
 * User: andrew
 * Date: 11/21/13
 */
public interface UserDeviceAssociationDAO extends DAOInterface<UserDeviceAssociation>{

	UserDeviceAssociation findByDeviceUID(String deviceUID);

	List<UserDeviceAssociation> findAllByUserId(long userId);

	UserDeviceAssociation findByDeviceUIDAndUserId(String deviceUID, long userId);
}
