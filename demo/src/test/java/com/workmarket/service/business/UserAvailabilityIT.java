package com.workmarket.service.business;

import com.workmarket.domains.model.user.NotificationAvailability;
import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.model.user.WorkAvailability;
import com.workmarket.service.business.dto.UserAvailabilityDTO;
import com.workmarket.test.BrokenTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class UserAvailabilityIT extends BaseServiceIT {

	@Autowired private UserService userService;

	@Test
	@Transactional
	public void testFindDefaultWorkingHours() throws Exception {
		List<UserAvailability> workingHours = userService.findWeeklyWorkingHours(EMPLOYEE_USER_ID);

		Assert.assertEquals(7, workingHours.size());
		for (UserAvailability wh : workingHours) {
			if (wh.getWeekDay() == Calendar.SATURDAY - 1 || wh.getWeekDay() == Calendar.SUNDAY - 1) {
				Assert.assertTrue(wh.getDeleted());
			} else {
				Assert.assertFalse(wh.getDeleted());
			}

			Assert.assertEquals(WorkAvailability.DEFAULT_FROM_HOUR, wh.getFromTime().get(Calendar.HOUR_OF_DAY));
			Assert.assertEquals(WorkAvailability.DEFAULT_FROM_MINUTE, wh.getFromTime().get(Calendar.MINUTE));
			Assert.assertEquals(WorkAvailability.DEFAULT_TO_HOUR, wh.getToTime().get(Calendar.HOUR_OF_DAY));
			Assert.assertEquals(WorkAvailability.DEFAULT_TO_MINUTE, wh.getToTime().get(Calendar.MINUTE));
		}
	}

	@Test
	@Transactional
	public void testFindDefaultNotificationHours() throws Exception {
		List<UserAvailability> notificationHours = userService.findWeeklyNotificationHours(EMPLOYEE_USER_ID);

		Assert.assertEquals(7, notificationHours.size());
		for (UserAvailability wh : notificationHours) {
			if (wh.getWeekDay() == Calendar.SATURDAY - 1 || wh.getWeekDay() == Calendar.SUNDAY - 1) {
				Assert.assertTrue(wh.getDeleted());
			} else {
				Assert.assertFalse(wh.getDeleted());
			}
			Assert.assertEquals(NotificationAvailability.DEFAULT_FROM_HOUR, wh.getFromTime().get(Calendar.HOUR_OF_DAY));
			Assert.assertEquals(NotificationAvailability.DEFAULT_TO_HOUR, wh.getToTime().get(Calendar.HOUR_OF_DAY));
		}
	}

	@Test
	@Transactional
	public void testSetNotificationHours() throws Exception {
		Calendar fromTime = Calendar.getInstance();
		Calendar toTime = Calendar.getInstance();

		UserAvailabilityDTO dto = new UserAvailabilityDTO();
		dto.setFromTime(fromTime);
		dto.setToTime(toTime);

		List<UserAvailabilityDTO> dtos = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			dto.setWeekDay(i);
			dtos.add(dto);
		}
		userService.updateUserNotificationHours(ANONYMOUS_USER_ID, dtos);

		List<UserAvailability> notificationHours = userService.findActiveWeeklyNotificationHours(ANONYMOUS_USER_ID);

		Assert.assertEquals(5, notificationHours.size());
		for (UserAvailability wh : notificationHours) {
			Assert.assertEquals(wh.getUser().getId(), ANONYMOUS_USER_ID);
		}

		UserAvailability wh = userService.findActiveNotificationHoursByUserId(ANONYMOUS_USER_ID, 6);
		Assert.assertNull(wh);

		dto.setWeekDay(0);
		dto.setDeleted(true);
		dtos.clear();
		dtos.add(dto);
		userService.updateUserNotificationHours(ANONYMOUS_USER_ID, dtos);

		wh = userService.findActiveNotificationHoursByUserId(ANONYMOUS_USER_ID, 0);
		Assert.assertNull(wh);
	}

	@Test
	public void testVerifyNotificationHours() throws Exception {
		Calendar now = DateUtilities.getCalendarNow();
		Calendar from = DateUtilities.getCalendarWithTime(8, 0);
		Calendar to = DateUtilities.getCalendarWithTime(17, 0);
		//Day of the week is 1 index		
		UserAvailabilityDTO dto = new UserAvailabilityDTO();
		dto.setWeekDay(now.get(Calendar.DAY_OF_WEEK) - 1);
		dto.setFromTime(from);
		dto.setToTime(to);
		userService.updateUserNotificationHours(ANONYMOUS_USER_ID, dto);

		Assert.assertTrue(userService.isAvailableForNotification(ANONYMOUS_USER_ID));

		// Premature

		from = DateUtilities.getCalendarWithTime(now.get(Calendar.HOUR_OF_DAY) - 2, 0);
		to = DateUtilities.getCalendarWithTime(now.get(Calendar.HOUR_OF_DAY) - 1, 0);

		dto = new UserAvailabilityDTO();
		dto.setWeekDay(now.get(Calendar.DAY_OF_WEEK) - 1);
		dto.setFromTime(from);
		dto.setToTime(to);
		userService.updateUserNotificationHours(ANONYMOUS_USER_ID, dto);

		Assert.assertFalse(userService.isAvailableForNotification(ANONYMOUS_USER_ID));

		// After

		from = DateUtilities.getCalendarWithTime(now.get(Calendar.HOUR_OF_DAY) + 1, 0);
		to = DateUtilities.getCalendarWithTime(now.get(Calendar.HOUR_OF_DAY) + 2, 0);

		dto = new UserAvailabilityDTO();
		dto.setWeekDay(now.get(Calendar.DAY_OF_WEEK) - 1);
		dto.setFromTime(from);
		dto.setToTime(to);
		userService.updateUserNotificationHours(ANONYMOUS_USER_ID, dto);

		Assert.assertFalse(userService.isAvailableForNotification(ANONYMOUS_USER_ID));
	}
}
