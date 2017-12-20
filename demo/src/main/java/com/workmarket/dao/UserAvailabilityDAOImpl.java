package com.workmarket.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.workmarket.domains.model.user.UserAvailability;

@Repository
@SuppressWarnings("unchecked")
public class UserAvailabilityDAOImpl extends AbstractDAO<UserAvailability> implements UserAvailabilityDAO {

	protected Class<UserAvailability> getEntityClass() {
		return UserAvailability.class;
	}

	@Override
	public UserAvailability findActiveWorkingHoursByUserId(Long userId, Integer weekDay) {
		Assert.notNull(userId);
		Assert.notNull(weekDay);

		return (UserAvailability)getFactory().getCurrentSession().getNamedQuery("availability.activeDailyWorkingHoursByUser")
			.setParameter("userId", userId)
			.setParameter("weekDay", weekDay)
			.uniqueResult();
	}

	@Override
	public UserAvailability findWorkingHoursByUserId(Long userId, Integer weekDay) {
		Assert.notNull(userId);
		Assert.notNull(weekDay);

		return (UserAvailability)getFactory().getCurrentSession().getNamedQuery("availability.dailyWorkingHoursByUser")
			.setParameter("userId", userId)
			.setParameter("weekDay", weekDay)
			.uniqueResult();
	}

	@Override
	public List<UserAvailability> findActiveWeeklyWorkingHours(Long userId) {
		Assert.notNull(userId);
		
		return getFactory().getCurrentSession().getNamedQuery("availability.activeWeeklyWorkingHoursByUser")
			.setParameter("userId", userId)
			.list();
	}

	@Override
	public List<UserAvailability> findWeeklyWorkingHours(Long userId) {
		Assert.notNull(userId);
		
		return getFactory().getCurrentSession().getNamedQuery("availability.weeklyWorkingHoursByUser")
			.setParameter("userId", userId)
			.list();
	}

	@Override
	public UserAvailability findActiveNotificationHoursByUserId(Long userId, Integer weekDay) {
		Assert.notNull(userId);
		Assert.notNull(weekDay);
		
		return (UserAvailability)getFactory().getCurrentSession().getNamedQuery("availability.activeDailyNotificationHoursByUser")
			.setParameter("userId", userId)
			.setParameter("weekDay", weekDay)
			.uniqueResult();
	}

	@Override
	public UserAvailability findNotificationHoursByUserId(Long userId, Integer weekDay) {
		Assert.notNull(userId);
		Assert.notNull(weekDay);
		
		return (UserAvailability)getFactory().getCurrentSession().getNamedQuery("availability.dailyNotificationHoursByUser")
			.setParameter("userId", userId)
			.setParameter("weekDay", weekDay)
			.uniqueResult();
	}

	@Override
	public List<UserAvailability> findActiveWeeklyNotificationHours(Long userId) {
		Assert.notNull(userId);
		
		return getFactory().getCurrentSession().getNamedQuery("availability.activeWeeklyNotificationHoursByUser")
			.setParameter("userId", userId)
			.list();
	}

	@Override
	public List<UserAvailability> findWeeklyNotificationHours(Long userId) {
		Assert.notNull(userId);
		
		return getFactory().getCurrentSession().getNamedQuery("availability.weeklyNotificationHoursByUser")
			.setParameter("userId", userId)
			.list();
	}

	@Override
	public List<UserAvailability> findWeeklyHoursByClientLocation(Long clientLocationId) {
		Assert.notNull(clientLocationId);
		
		return getFactory().getCurrentSession().getNamedQuery("availability.weeklyHoursByClientLocation")
			.setParameter("clientLocationId", clientLocationId)
			.list();
	}
}
