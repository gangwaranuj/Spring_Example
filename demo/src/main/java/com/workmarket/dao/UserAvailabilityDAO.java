package com.workmarket.dao;

import com.workmarket.domains.model.user.UserAvailability;

import java.util.List;

public interface UserAvailabilityDAO extends DAOInterface<UserAvailability> {

	/**
	 * Excludes records set to deleted = true, which in this case means that the user didn't set up specific hours.
	 *
	 * @param userId
	 * @param weekDay
	 * @return {@link com.workmarket.domains.model.user.UserAvailability UserAvailability}
	 */
	public UserAvailability findActiveWorkingHoursByUserId(Long userId, Integer weekDay);
		
	/**
	 * All working hours that has been set even if they are deleted
	 */
	public UserAvailability findWorkingHoursByUserId(Long userId, Integer weekDay);
	
	/**
	 * Excludes records set to deleted = true, which in this case means that the user didn't set up specific hours.
	 */
	public List<UserAvailability> findActiveWeeklyWorkingHours(Long userId);
	public List<UserAvailability> findWeeklyWorkingHours(Long userId);
	
	public UserAvailability findActiveNotificationHoursByUserId(Long userId, Integer weekDay);
	public UserAvailability findNotificationHoursByUserId(Long userId, Integer weekDay);
	public List<UserAvailability> findActiveWeeklyNotificationHours(Long userId);
	public List<UserAvailability> findWeeklyNotificationHours(Long userId);
	
	public List<UserAvailability> findWeeklyHoursByClientLocation(Long clientLocationId);
}
