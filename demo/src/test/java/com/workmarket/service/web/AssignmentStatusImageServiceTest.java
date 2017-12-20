package com.workmarket.service.web;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.WorkService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: micah
 * Date: 9/25/13
 * Time: 11:47 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class AssignmentStatusImageServiceTest {
	@Mock WorkService workService;
	@Mock WorkBundleService workBundleService;

	@InjectMocks AssignmentStatusImageServiceImpl assignmentStatusImageService;

	static final String workNumber = "YYYY";
	User user;

	@Before
	public void setup() {
		user = mock(User.class);
		when(user.getId()).thenReturn(1L);
	}

	@Test
	public void getImageAsset_NotAllowed() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, user.getId())).thenReturn(false);

		ImageAsset expected = ImageAsset.NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_BundleNotAllowed() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, 1L)).thenReturn(false);
		when(workBundleService.isAssignmentBundle(workNumber)).thenReturn(true);

		ImageAsset expected = ImageAsset.BUNDLE_NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_Available() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, 1L)).thenReturn(true);
		when(workService.isWorkStatusForWorkByWorkNumber(workNumber, WorkStatusType.SENT)).thenReturn(true);

		ImageAsset expected = ImageAsset.AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_BundleAvailable() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, 1L)).thenReturn(true);
		when(workService.isWorkStatusForWorkByWorkNumber(workNumber, WorkStatusType.SENT)).thenReturn(true);
		when(workBundleService.isAssignmentBundle(workNumber)).thenReturn(true);

		ImageAsset expected = ImageAsset.BUNDLE_AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_NotAvailable() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, 1L)).thenReturn(true);
		when(workService.isWorkStatusForWorkByWorkNumber(workNumber, WorkStatusType.SENT)).thenReturn(false);

		ImageAsset expected = ImageAsset.NOT_AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_BundleNotAvailable() {
		when(workService.isWorkStatusAccessibleForUser(workNumber, 1L)).thenReturn(true);
		when(workService.isWorkStatusForWorkByWorkNumber(workNumber, WorkStatusType.SENT)).thenReturn(false);
		when(workBundleService.isAssignmentBundle(workNumber)).thenReturn(true);

		ImageAsset expected = ImageAsset.BUNDLE_NOT_AVAILABLE;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, workNumber);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_NullWorkNumber_Exception() {
		ImageAsset expected = ImageAsset.NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(user, null);

		assertEquals(expected, actual);
	}

	@Test
	public void getImageAsset_UserNotFound_NotAllowed() {
		ImageAsset expected = ImageAsset.NOT_ALLOWED;
		ImageAsset actual = assignmentStatusImageService.getImageAsset(null, workNumber);

		assertEquals(expected, actual);
	}
}
