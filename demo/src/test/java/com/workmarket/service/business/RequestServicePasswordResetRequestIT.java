package com.workmarket.service.business;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.request.PasswordResetRequest;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class RequestServicePasswordResetRequestIT extends BaseServiceIT {

	@Autowired private RequestService requestService;
	private Long employeeId;
	private boolean setUpIsDone = false;

	@Before
	public void initData() throws Exception {
		if (setUpIsDone) {
			return;
		}
		User employee = newWMEmployee();
		employeeId = employee.getId();
		setUpIsDone = true;
	}

	@Test
	public void requestPasswordReset_RequestCreatedWithCorrectExpiry() throws Exception {
		PasswordResetRequest request = requestService.requestPasswordReset(employeeId, EMPLOYEE_USER_ID);

		Calendar now = DateUtilities.getCalendarNow();
		now.add(Calendar.MONTH, 6);

		assertFalse(request.isExpired());
		assertEquals(employeeId, request.getRequestor().getId());
		assertEquals(EMPLOYEE_USER_ID, request.getInvitedUser().getId());
		assertTrue(request.getExpiresOn().get(Calendar.MONTH) == (now.get(Calendar.MONTH)));
	}

	@Test
	public void requestPasswordReset_WithSpecificExpiry_RequestCreatedWithCorrectExpiry() throws Exception {
		Calendar now = DateUtilities.getCalendarNow();
		now.add(Calendar.HOUR_OF_DAY, 24);

		PasswordResetRequest request = requestService.requestPasswordReset(employeeId, EMPLOYEE_USER_ID, now);

		assertFalse(request.isExpired());
		assertEquals(employeeId, request.getRequestor().getId());
		assertEquals(EMPLOYEE_USER_ID, request.getInvitedUser().getId());
		assertTrue(request.getExpiresOn().get(Calendar.MONTH) == (now.get(Calendar.MONTH)));
	}

}