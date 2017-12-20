package com.workmarket.dao.notification;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.notification.UserDeviceAssociation;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: andrew
 * Date: 11/21/13
 */
@Repository
public class UserDeviceAssociationDAOImpl extends AbstractDAO<UserDeviceAssociation> implements UserDeviceAssociationDAO {

	@Override
	protected Class<UserDeviceAssociation> getEntityClass() {
		return UserDeviceAssociation.class;
	}

	@Override
	public UserDeviceAssociation findByDeviceUID(String deviceUID) {
		Query query = getFactory().getCurrentSession().createQuery("from userDeviceAssociation u where u.deviceUid = :deviceUID");

		query.setParameter("deviceUID", deviceUID)
				.setMaxResults(1);

		return (UserDeviceAssociation) query.uniqueResult();
	}

	@Override
	public List<UserDeviceAssociation> findAllByUserId(long userId) {
		Query query = getFactory().getCurrentSession().createQuery("from userDeviceAssociation u where u.user.id = :userId")
			.setParameter("userId", userId);

		return query.list();
	}

	@Override
	public UserDeviceAssociation findByDeviceUIDAndUserId(String deviceUID, long userId) {
		Query query = getFactory().getCurrentSession().createQuery("from userDeviceAssociation u where u.deviceUid = :deviceUID AND u.user.id = :userId AND u.deleted = false");

		query.setParameter("deviceUID", deviceUID)
				.setParameter("userId", userId)
				.setMaxResults(1);

		return (UserDeviceAssociation) query.uniqueResult();
	}
}
